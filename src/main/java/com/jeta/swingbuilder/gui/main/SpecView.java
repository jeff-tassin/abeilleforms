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

package com.jeta.swingbuilder.gui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormSpecAdapter;
import com.jeta.forms.gui.common.FormSpecDefinition;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.forms.store.memento.FormGroupSet;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.components.FloatDocument;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.EditorManager;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Displays the column and row settings.
 * 
 * @author Jeff Tassin
 */
public class SpecView extends JETAPanel implements GridViewListener, FormSpecDefinition {
	/**
	 * The panel that contains the form.
	 */
	private FormPanel m_spec_panel;

	/**
	 * Set to true if this flag indicates this view shows a RowSpec. False if
	 * this view is for a ColumnSpec;
	 */
	private boolean m_rowview;

	/**
	 * The grid component we are currently displaying values for
	 */
	private GridComponent m_current_comp;

	private EditorManager m_editor_mgr;

	public SpecView(String formPath, boolean rowView) {
		m_rowview = rowView;

		m_editor_mgr = (EditorManager) JETARegistry.lookup(EditorManager.COMPONENT_ID);
		//FormLayout layout = new FormLayout("fill:pref:grow", "pref");
		//CellConstraints cc = new CellConstraints();

		//setLayout(layout);
		setLayout( new BorderLayout() );

		m_spec_panel = new FormPanel(formPath);
		//add(m_spec_panel, cc.xy(1, 1));
		add( m_spec_panel, BorderLayout.CENTER );

		String def = m_spec_panel.getText(SpecViewNames.ID_CONST_SIZE_AMT_FIELD);
		m_spec_panel.getTextField(SpecViewNames.ID_CONST_SIZE_AMT_FIELD).setDocument(new FloatDocument(false));
		if (def != null)
			m_spec_panel.setText(SpecViewNames.ID_CONST_SIZE_AMT_FIELD, def);

		def = m_spec_panel.getText(SpecViewNames.ID_RESIZE_GROW_WEIGHT);
		m_spec_panel.getTextField(SpecViewNames.ID_RESIZE_GROW_WEIGHT).setDocument(new FloatDocument(false));
		if (def != null)
			m_spec_panel.setText(SpecViewNames.ID_RESIZE_GROW_WEIGHT, def);

		m_spec_panel.getButton(SpecViewNames.ID_GROUP_APPLY_BTN).setPreferredSize(new Dimension(32, 10));
		JSpinner sp = m_spec_panel.getSpinner(SpecViewNames.ID_GROUP_NUMBER_SPINNER);
		sp.setModel(new SpinnerNumberModel(0, 0, 100, 1));

		setController(new SpecViewController(this));
	}

	/**
	 * @return the alignment string depending on the specification type column:
	 *         LEFT, CENTER, RIGHT, RILL row: TOP, CENTER, BOTTOM, RILL
	 */
	public String getAlignment() {
		return (String) m_spec_panel.getComboBox(SpecViewNames.ID_ALIGNMENT_COMBO).getSelectedItem();
	}

	/**
	 * @return the bounded size MIN, MAX
	 */
	public String getBoundedSize() {
		if (m_spec_panel.getButton(SpecViewNames.ID_BOUNDED_MIN_RADIO).isSelected())
			return "MIN";
		else
			return "MAX";
	}

	/**
	 * @return the component size: MIN, PREF, DEFAULT
	 */
	public String getComponentSize() {
		return (String) m_spec_panel.getComboBox(SpecViewNames.ID_COMP_SIZE_COMBO).getSelectedItem();
	}

	/**
	 * @return the units (integer) (double) PX, PT, DLU IN, MM, CM
	 */
	public String getConstantUnits() {
		return (String) m_spec_panel.getComboBox(SpecViewNames.ID_CONST_SIZE_UNITS_COMBO).getSelectedItem();
	}

	/**
	 * @return the size.
	 */
	public double getConstantSize() {
		try {
			String result = m_spec_panel.getTextField(SpecViewNames.ID_CONST_SIZE_AMT_FIELD).getText();
			return Double.parseDouble(result);
		} catch (Exception e) {
			/** what else can we do here? */
			return 10.0;
		}
	}

	/**
	 * @return the component that we are currently displaying values for
	 */
	GridComponent getCurrentComponent() {
		return m_current_comp;
	}

	/**
	 * @return the underlying form panel
	 */
	FormPanel getFormPanel() {
		return m_spec_panel;
	}

	/**
	 * @return the current group that this column/row is assigned to
	 */
	int getGroupId(GridComponent gc) {
		GridView view = gc.getParentView();
		if (isRowView()) {
			FormGroupSet gset = view.getRowGroups();
			Integer id = gset.getGroupId(gc.getRow());
			return (id == null ? 0 : id.intValue());
		}
		else {
			FormGroupSet gset = view.getColumnGroups();
			Integer id = gset.getGroupId(gc.getColumn());
			return (id == null ? 0 : id.intValue());
		}
	}

	/**
	 * @return the resize behavior NONE, GROW
	 */
	public String getResize() {
		if (m_spec_panel.getButton(SpecViewNames.ID_RESIZE_NONE_RADIO).isSelected())
			return "NONE";
		else
			return "GROW";
	}

	/**
	 * @return the resize weight (0.0-1.0)
	 */
	public double getResizeWeight() {
		try {
			String result = m_spec_panel.getTextField(SpecViewNames.ID_RESIZE_GROW_WEIGHT).getText();
			double weight = Double.parseDouble(result);
			if (weight < 0.0)
				weight = 0.0;

			if (weight > 1.0)
				weight = 1.0;
			return weight;
		} catch (Exception e) {
			/** what else can we do here? */
			return 1.0;
		}
	}

	/**
	 * @return the size type for the FormSpec
	 */
	public String getSizeType() {
		if (m_spec_panel.isSelected(SpecViewNames.ID_CONSTANT_SIZE_RADIO))
			return "CONSTANT";
		else if (m_spec_panel.isSelected(SpecViewNames.ID_BOUNDED_SIZE_RADIO))
			return "BOUNDED";
		else
			return "COMPONENT";
	}

	/** GridViewListener implementation */
	public void gridChanged(GridViewEvent evt) {
		FormEditor editor = m_editor_mgr.getCurrentEditor();
		if (editor != null) {
			GridComponent comp = editor.getSelectedComponent();
			update(comp);
		}
		else {
			update((GridComponent) null);
		}
	}

	/**
	 * @return true if the size type is bounded
	 */
	public boolean isBoundedSize() {
		return "BOUNDED".equals(getSizeType());
	}

	/**
	 * @return true if the size type is component
	 */
	public boolean isComponentSize() {
		return "COMPONENT".equals(getSizeType());
	}

	/**
	 * @return true if the size type is constant
	 */
	public boolean isConstantSize() {
		return "CONSTANT".equals(getSizeType());
	}

	/**
	 * @return true if the resize grow radio is selected
	 */
	public boolean isResizeGrow() {
		return m_spec_panel.getButton(SpecViewNames.ID_RESIZE_GROW_RADIO).isSelected();
	}

	/**
	 * @return true if this spec view is for a row. False if this view is for a
	 *         column.
	 */
	public boolean isRowView() {
		return m_rowview;
	}

	void setGroup(int groupNumber) {
		JSpinner sp = m_spec_panel.getSpinner(SpecViewNames.ID_GROUP_NUMBER_SPINNER);
		sp.setValue(groupNumber);
	}

	/**
	 * Selects the size type radio that corresponds to the given size type
	 */
	public void setSizeType(String sizeType) {
		if ("CONSTANT".equalsIgnoreCase(sizeType))
			m_spec_panel.setSelected(SpecViewNames.ID_CONSTANT_SIZE_RADIO, true);
		else if ("BOUNDED".equalsIgnoreCase(sizeType))
			m_spec_panel.setSelected(SpecViewNames.ID_BOUNDED_SIZE_RADIO, true);
		else
			m_spec_panel.setSelected(SpecViewNames.ID_COMPONENT_SIZE_RADIO, true);
	}

	/**
	 * Updates the panel using the specs from the currently selected cell in the
	 * given editor
	 */
	public void update(GridComponent gc) {
		m_current_comp = gc;

		if (gc == null) {
			setEnabled(false);
		}
		else {
			GridView view = gc.getParentView();
			if (view != null) {
				setEnabled(true);
				int row = gc.getRow();
				int col = gc.getColumn();
				if (isRowView()) {
					RowSpec spec = view.getRowSpec(row);
					updateView(spec, gc);
				}
				else {
					ColumnSpec spec = view.getColumnSpec(col);
					updateView(spec, gc);
				}
			}
			else {
				setEnabled(false);
			}
		}
	}

	/**
	 * Updates the view given the column spec
	 */
	void updateView(ColumnSpec spec, GridComponent gc) {
		assert (!isRowView());
		FormSpecAdapter fspec = new FormSpecAdapter(spec);
		updateView(fspec, gc);
	}

	/**
	 * Updates the view given the column spec
	 */
	void updateView(RowSpec spec, GridComponent gc) {
		assert (isRowView());
		FormSpecAdapter fspec = new FormSpecAdapter(spec);
		updateView(fspec, gc);
	}

	/**
	 * Updates the view given the column spec
	 */
	void updateView(FormSpecAdapter fspec, GridComponent gc) {
		SpecViewController controller = (SpecViewController) getController();

		try {
			controller.enableEvents(false);

			m_spec_panel.getComboBox(SpecViewNames.ID_ALIGNMENT_COMBO).setSelectedItem(fspec.getAlignment());
			setSizeType(fspec.getSizeType());

			if (isConstantSize() || isBoundedSize()) {
				m_spec_panel.getComboBox(SpecViewNames.ID_CONST_SIZE_UNITS_COMBO).setSelectedItem(fspec.getConstantUnits());
				m_spec_panel.getButton(SpecViewNames.ID_BOUNDED_MIN_RADIO).setSelected(fspec.isBoundedMinimum());
				m_spec_panel.getButton(SpecViewNames.ID_BOUNDED_MAX_RADIO).setSelected(fspec.isBoundedMaximum());

				if (fspec.isIntegralUnits()) {
					m_spec_panel.getTextField(SpecViewNames.ID_CONST_SIZE_AMT_FIELD).setText(String.valueOf(Math.round(fspec.getConstantSize())));
				}
				else {
					m_spec_panel.getTextField(SpecViewNames.ID_CONST_SIZE_AMT_FIELD).setText(String.valueOf(fspec.getConstantSize()));
				}
			}

			if (isComponentSize() || isBoundedSize()) {
				m_spec_panel.getComboBox(SpecViewNames.ID_COMP_SIZE_COMBO).setSelectedItem(fspec.getComponentSize());
			}

			m_spec_panel.getButton(SpecViewNames.ID_RESIZE_NONE_RADIO).setSelected(fspec.isResizeNone());
			m_spec_panel.getButton(SpecViewNames.ID_RESIZE_GROW_RADIO).setSelected(fspec.isResizeGrow());
			m_spec_panel.getTextField(SpecViewNames.ID_RESIZE_GROW_WEIGHT).setText(String.valueOf(fspec.getResizeWeight()));

			setGroup(getGroupId(gc));
		} finally {
			controller.enableEvents(true);
		}
	}
}
