/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.codegen.builder;

import java.util.HashMap;
import java.util.Iterator;

import com.jeta.forms.gui.common.FormSpecAdapter;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.memento.BeanMemento;
import com.jeta.forms.store.memento.CellConstraintsMemento;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FormGroupSet;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

/**
 * Builds the Java Source for a form and all its child forms.
 * 
 * @author Jeff Tassin
 */
public class PanelWriter {
	private CellConstraints m_default_cc = new CellConstraints();

	public PanelWriter() {
	}

	public MethodWriter createPanel(DeclarationManager decl_mgr, FormMemento fm) {
		decl_mgr.addImport("javax.swing.JPanel");
		decl_mgr.addImport("com.jgoodies.forms.layout.CellConstraints");
		decl_mgr.addImport("com.jgoodies.forms.layout.FormLayout");

		PropertiesMemento pm = fm.getPropertiesMemento();

		MethodWriter method_writer = new MethodWriter(decl_mgr, null, getSuggestedMethodName(pm.getComponentName()));
		BeanWriter form_bean_writer = new BeanWriter(method_writer, pm);
		method_writer.setReturnResult(form_bean_writer);

		LocalVariableDeclaration layout = new LocalVariableDeclaration(method_writer, com.jgoodies.forms.layout.FormLayout.class);
		layout.addParameter(new StringExpression(fm.getColumnSpecs()));
		layout.addParameter(new StringExpression(fm.getRowSpecs()));

		/** we need this to get the row/column count */
		FormLayout formlayout = new FormLayout(FormSpecAdapter.fixupSpecs(fm.getColumnSpecs()), FormSpecAdapter.fixupSpecs(fm.getRowSpecs()));

		method_writer.addStatements(form_bean_writer.getStatements());

		method_writer.addStatement(layout);

		/** set the column and row groups */
		setGroups(method_writer, layout.getVariable(), fm, true);
		setGroups(method_writer, layout.getVariable(), fm, false);

		LocalVariableDeclaration ccvar = new LocalVariableDeclaration(method_writer, com.jgoodies.forms.layout.CellConstraints.class, "cc");
		method_writer.addStatement(ccvar);

		/** add the panel declaration/ctor to the method */
		MethodStatement ss = new MethodStatement(form_bean_writer.getBeanVariable(), "setLayout");
		ss.addParameter(new BasicExpression(layout.getVariable()));
		method_writer.addStatement(ss);

		decl_mgr.addMethod(method_writer);

		/** puts a newline between beans */
		method_writer.addStatement(new BasicStatement(""));

		HashMap row_cache = new HashMap();
		HashMap col_cache = new HashMap();

		Iterator iter = fm.iterator();
		while (iter.hasNext()) {
			ComponentMemento cm = (ComponentMemento) iter.next();
			CellConstraintsMemento ccm = cm.getCellConstraintsMemento();
			CellConstraints cc = ccm.createCellConstraints();

			try {
				if (cm instanceof FormMemento) {
					FormMemento cfm = (FormMemento) cm;
					MethodWriter subpanel = createPanel(method_writer, cfm);
					method_writer.addStatement(createAddComponentStatement(form_bean_writer.getBeanVariable(), subpanel.getMethodName() + "()", ccvar
							.getVariable(), cc));

				}
				else if (cm instanceof BeanMemento) {
					BeanMemento bm = (BeanMemento) cm;
					Integer icol = new Integer(cc.gridX);
					Integer irow = new Integer(cc.gridY);

					if (bm.getBeanClass() == null) {
						/** found an empty component */
						if (col_cache.get(icol) == null)
							col_cache.put(icol, new FillMarker(bm, formlayout.getColumnSpec(icol.intValue())));

						if (row_cache.get(irow) == null)
							row_cache.put(irow, new FillMarker(bm, formlayout.getRowSpec(irow.intValue())));

					}
					else if (bm.getProperties() != null) {
						String beanclass = bm.getBeanClass();
						if (beanclass.indexOf("GridView") > 0 || beanclass.indexOf("JETALabel") > 0 || decl_mgr.isIncludeNonStandard()
								|| beanclass.indexOf("com.jeta") < 0) {

							BeanWriter bw = new BeanWriter(method_writer, bm.getProperties());
							method_writer.addStatements(bw.getStatements());

							method_writer.addStatement(createAddComponentStatement(form_bean_writer.getBeanVariable(), bw.getResultVariable(), ccvar
									.getVariable(), cc));
							/** puts a newline between beans */
							method_writer.addStatement(new BasicStatement(""));

							if (icol.intValue() == 1)
								row_cache.put(irow, new FillMarker(bm, formlayout.getRowSpec(irow.intValue())));

							if (irow.intValue() == 1)
								col_cache.put(icol, new FillMarker(bm, formlayout.getColumnSpec(icol.intValue())));
						}

					}
				}
				else {
					assert (false);
				}
			} catch (Exception e) {

			}
		}

		MethodStatement addseps = new MethodStatement("addFillComponents");
		addseps.addParameter(form_bean_writer.getBeanVariable());
		addseps.addParameter(createFillArray(col_cache, formlayout.getColumnCount()));
		addseps.addParameter(createFillArray(row_cache, formlayout.getRowCount()));
		method_writer.addStatement(addseps);

		return method_writer;
	}

	private Statement createAddComponentStatement(String formVariable, String beanVariable, String ccVar, CellConstraints cc) {
		MethodStatement ms = new MethodStatement(formVariable, "add");
		ms.addParameter(beanVariable);

		if (cc.hAlign == m_default_cc.hAlign && cc.vAlign == m_default_cc.vAlign) {
			if (cc.gridWidth == 1 && cc.gridHeight == 1) {
				MethodExpression ccme = new MethodExpression(ccVar, "xy");
				ccme.addParameter(String.valueOf(cc.gridX));
				ccme.addParameter(String.valueOf(cc.gridY));
				ms.addParameter(ccme);
			}
			else {
				MethodExpression ccme = new MethodExpression(ccVar, "xywh");
				ccme.addParameter(String.valueOf(cc.gridX));
				ccme.addParameter(String.valueOf(cc.gridY));
				ccme.addParameter(String.valueOf(cc.gridWidth));
				ccme.addParameter(String.valueOf(cc.gridHeight));
				ms.addParameter(ccme);
			}
		}
		else {
			MethodExpression ccme = new MethodExpression("new CellConstraints");
			ccme.addParameter(String.valueOf(cc.gridX));
			ccme.addParameter(String.valueOf(cc.gridY));
			ccme.addParameter(String.valueOf(cc.gridWidth));
			ccme.addParameter(String.valueOf(cc.gridHeight));
			ccme.addParameter("CellConstraints." + FormUtils.fromAlignment(cc.hAlign));
			ccme.addParameter("CellConstraints." + FormUtils.fromAlignment(cc.vAlign));
			ms.addParameter(ccme);
		}
		return ms;
	}

	/**
	 * Creates an array of column or row indices that correspond to cells in the
	 * first row or column that require empty filler components. This is
	 * required to keep the spacing of the grid consistent with the view in the
	 * editor.
	 * 
	 * @param indexes
	 *            a map of Integer indexes to ComponentMememeto objects.
	 */
	private String createFillArray(HashMap indexes, int count) {
		StringBuffer sbuff = null;
		for (int index = 1; index <= count; index++) {
			Integer ival = new Integer(index);
			FillMarker fm = (FillMarker) indexes.get(ival);
			if (fm == null || fm.isFillable()) {
				/** we have an empty row/column */
				if (sbuff == null) {
					sbuff = new StringBuffer("new int[]{ ");
					sbuff.append(String.valueOf(index));
				}
				else {
					sbuff.append(',');
					sbuff.append(String.valueOf(index));
				}
			}
		}

		if (sbuff == null) {
			return "new int[0]";
		}
		else {
			sbuff.append(" }");
			return sbuff.toString();
		}
	}

	private String getSuggestedMethodName(String mname) {
		mname = FormDesignerUtils.fastTrim(mname);
		if (mname.length() == 0)
			mname = "createPanel";
		else
			mname = "create" + mname;
		return mname;
	}

	private void setGroups(MethodWriter method_writer, String layoutVariable, FormMemento fm, boolean colGroups) {
		StringBuffer group_params = null;
		FormGroupSet grpset = fm.getColumnGroups();
		if (colGroups)
			grpset = fm.getColumnGroups();
		else
			grpset = fm.getRowGroups();

		if (grpset != null) {
			int[][] grps = grpset.toArray();
			for (int index = 0; index < grps.length; index++) {
				int[] group = grps[index];
				if (group != null && group.length > 0) {
					if (group_params == null)
						group_params = new StringBuffer();
					else
						group_params.append(", ");

					group_params.append("{");
					for (int gindex = 0; gindex < group.length; gindex++) {
						if (gindex != 0)
							group_params.append(",");
						group_params.append(String.valueOf(group[gindex]));
					}
					group_params.append("}");
				}
			}

			if (group_params != null) {
				group_params.insert(0, "new int[][]{ ");
				group_params.append(" }");

				MethodStatement setgroup = null;
				if (colGroups)
					setgroup = new MethodStatement(layoutVariable, "setColumnGroups");
				else
					setgroup = new MethodStatement(layoutVariable, "setRowGroups");

				setgroup.addParameter(group_params.toString());
				method_writer.addStatement(setgroup);
			}
		}
	}

	private static class FillMarker {
		private BeanMemento m_memento;
		private Object m_spec;

		public FillMarker(BeanMemento memento, ColumnSpec spec) {
			m_memento = memento;
			m_spec = spec;
		}

		public FillMarker(BeanMemento memento, RowSpec spec) {
			m_memento = memento;
			m_spec = spec;
		}

		public boolean isFillable() {
			if (m_spec instanceof RowSpec)
				return isFillable((RowSpec) m_spec);
			else if (m_spec instanceof ColumnSpec)
				return isFillable((ColumnSpec) m_spec);
			else {
				assert (false);
				return false;
			}
		}

		public boolean isFillable(ColumnSpec spec) {
			return (m_memento == null || m_memento.getBeanClass() == null && (spec.getSize() == Sizes.DEFAULT || spec.getSize() == Sizes.PREFERRED));

		}

		public boolean isFillable(RowSpec spec) {
			return (m_memento == null || m_memento.getBeanClass() == null && (spec.getSize() == Sizes.DEFAULT || spec.getSize() == Sizes.PREFERRED));
		}
	}
}
