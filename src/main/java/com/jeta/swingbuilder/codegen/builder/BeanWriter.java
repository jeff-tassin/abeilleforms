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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.components.label.JETALabel;
import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.DynamicPropertyDescriptor;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.forms.store.properties.TransformOptionsProperty;
import com.jeta.open.registry.JETARegistry;

public class BeanWriter {
	private String m_bean_variable_name;
	private Class m_bean_type;

	private String m_result_variable_name;
	private Class m_result_type;

	private LinkedList m_statements = new LinkedList();

	/** @directed */
	public BeanWriter(DeclarationManager decl_mgr, PropertiesMemento pm) {
		assert (pm != null);
		String classname = pm.getBeanClassName();

		if (GridView.class.getName().equals(classname))
			classname = "javax.swing.JPanel";
		else if (JETALabel.class.getName().equals(classname))
			classname = "javax.swing.JLabel";

		try {
			Class beanclass = null;
			try {
				BeanManager bmgr = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
				if (bmgr != null) {
					beanclass = bmgr.getBeanClass(classname);
				}
			} catch (Exception e) {
				FormsLogger.severe(e);
			}

			if (beanclass == null) {
				beanclass = Class.forName(classname);
			}

			VariableDeclaration ds = null;
			/**
			 * don't declare panels or labels as member variables if they don't
			 * have a name
			 */
			if (JLabel.class.isAssignableFrom(beanclass) || JPanel.class.isAssignableFrom(beanclass)) {
				String compname = pm.getComponentName();
				if (compname == null)
					compname = "";
				else
					compname = compname.trim();

				if (compname.length() == 0) {
					ds = new LocalVariableDeclaration(decl_mgr, beanclass, null);
					/** temporary hack */
					((MethodWriter) decl_mgr).addStatement(ds);
				}
			}

			if (ds == null) {
				ds = new MemberVariableDeclaration(decl_mgr, beanclass, pm.getComponentName());
				decl_mgr.addMemberVariable(ds);
			}

			setBeanVariable(ds.getVariable(), beanclass);
			setResultVariable(ds.getVariable(), beanclass);

			PropertyWriterFactory fac = (PropertyWriterFactory) JETARegistry.lookup(PropertyWriterFactory.COMPONENT_ID);
			LinkedList dynamic_props = new LinkedList();

			Class lookup_class = beanclass;
			if (GridView.class.getName().equals(pm.getBeanClassName()))
				lookup_class = GridView.class;

			DynamicBeanInfo beaninfo = JETABeanFactory.getBeanInfo(lookup_class);
			Collection jeta_pds = beaninfo.getPropertyDescriptors();
			Iterator iter = jeta_pds.iterator();
			while (iter.hasNext()) {
				try {
					JETAPropertyDescriptor jpd = (JETAPropertyDescriptor) iter.next();
					if (pm.containsProperty(jpd.getName())) {

						if (jpd instanceof DynamicPropertyDescriptor) {
							dynamic_props.add(jpd);
						}
						else {
							Object prop_value = pm.getPropertyValue(jpd.getName());
							if ("name".equals(jpd.getName()) && "".equals(prop_value)) {
								continue;
							}

							PropertyWriter pw = fac.createWriter(jpd.getPropertyType());
							if (pw != null) {
								pw.writeProperty(decl_mgr, this, jpd, prop_value);
							}
						}
					}
				} catch (Exception e) {
					FormsLogger.debug(e);
				}
			}

			/** now do the dynamic props */
			iter = dynamic_props.iterator();
			while (iter.hasNext()) {
				DynamicPropertyDescriptor dpd = (DynamicPropertyDescriptor) iter.next();

				PropertyWriter pw = fac.createWriter(dpd.getPropertyType());
				if (pw != null) {
					Object prop_value = null;
					if (dpd.getPropertyType() == TransformOptionsProperty.class) {
						JETABean jetabean = JETABeanFactory.createBean(beanclass.getName(), null, true, true);
						jetabean.setState(pm);
						TransformOptionsProperty tprop = (TransformOptionsProperty) jetabean.getCustomProperty(dpd.getName());
						prop_value = tprop;
					}
					else {
						prop_value = pm.getPropertyValue(dpd.getName());
					}
					pw.writeProperty(decl_mgr, this, dpd, prop_value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addStatement(Statement stmt) {
		if (stmt != null)
			m_statements.add(stmt);
	}

	public Collection getStatements() {
		return m_statements;
	}

	public void setBeanVariable(String varName, Class beanType) {
		m_bean_variable_name = varName;
		m_bean_type = beanType;
	}

	public String getBeanVariable() {
		return m_bean_variable_name;
	}

	public Class getBeanType() {
		return m_bean_type;
	}

	public String getResultVariable() {
		return m_result_variable_name;
	}

	public Class getResultType() {
		return m_result_type;
	}

	public void setResultVariable(String varName, Class resultType) {
		m_result_variable_name = varName;
		m_result_type = resultType;
	}
}
