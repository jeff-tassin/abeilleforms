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

package com.jeta.swingbuilder.gui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.BeanDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.SetPropertyCommand;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.properties.editors.UnknownEditor;

/**
 * TableModel for managing properties for a given bean.
 * 
 * @author Jeff Tassin
 */
public class PropertyTableModel extends AbstractTableModel {
	private JETAPropertyDescriptor[] m_descriptors;

	private BeanDescriptor m_beandescriptor;

	private DynamicBeanInfo m_beaninfo;

	private JETABean m_bean;

	// Cached property editors.
	private static Hashtable m_prop_editors;

	// Shared instance of a comparator
	private static DescriptorComparator comparator = new DescriptorComparator();

	private UnknownEditor m_unknown_editor = new UnknownEditor();

	private static final int NUM_COLUMNS = 2;

	public static final int COL_NAME = 0;

	public static final int COL_VALUE = 1;

	// Filter options
	public static final int VIEW_ALL = 0;

	public static final int VIEW_PREFERRED = 7;

	private int currentFilter = VIEW_PREFERRED;

	/**
	 * A list of PropertyEditorListeners that want to receive
	 * PropertyEditorEvents
	 */
	private LinkedList m_listeners = new LinkedList();

	public PropertyTableModel() {

		if (m_prop_editors == null) {
			m_prop_editors = new Hashtable();
			registerPropertyEditors();
		}
		setFilter(VIEW_ALL);
	}

	public PropertyTableModel(JETABean jbean) {
		this();
		setBean(jbean);
	}

	public void addPropertyListener(PropertyEditorListener listener) {
		m_listeners.add(listener);
	}

	/**
	 * Filters the table to display only properties with specific attributes.
	 * Will sort the table after the data has been filtered.
	 * 
	 * @param view
	 *            The properties to display.
	 */
	public void filterTable(int view) {
		if (m_beaninfo == null)
			return;

		Collection descriptors = m_beaninfo.getPropertyDescriptors();

		// Use collections to filter out unwanted properties
		ArrayList list = new ArrayList();
		list.addAll(descriptors);

		ListIterator iterator = list.listIterator();
		JETAPropertyDescriptor desc;
		while (iterator.hasNext()) {
			desc = (JETAPropertyDescriptor) iterator.next();

			switch (view) {
			case VIEW_ALL:
				if (desc.isHidden()) {
					iterator.remove();
				}
				break;

			case VIEW_PREFERRED:
				if (!desc.isPreferred() || desc.isHidden()) {
					iterator.remove();
				}
				// System.out.println( "PropertyTableModel.filterProps: desc: "
				// +
				// desc.getName() + " pref: " + desc.isPreferred() );
				break;
			}
		}
		m_descriptors = (JETAPropertyDescriptor[]) list.toArray(new JETAPropertyDescriptor[list.size()]);
		fireTableDataChanged();
	}

	public void firePropertyEditorEvent(PropertyEditorEvent evt) {
		Iterator iter = m_listeners.iterator();
		while (iter.hasNext()) {
			PropertyEditorListener listener = (PropertyEditorListener) iter.next();
			listener.propertyChanged(evt);
		}
	}

	/**
	 * Sets the current filter of the Properties.
	 * 
	 * @param filter
	 *            one of VIEW_ constants
	 */
	public void setFilter(int filter) {
		this.currentFilter = filter;
		filterTable(currentFilter);
	}

	/**
	 * Returns the current filter type
	 */
	public int getFilter() {
		return currentFilter;
	}

	/**
	 * Return the current object that is represented by this model.
	 */
	public JETABean getBean() {
		return m_bean;
	}

	/**
	 * Get row count (total number of properties shown)
	 */
	public int getRowCount() {
		if (m_descriptors == null) {
			return 0;
		}
		return m_descriptors.length;
	}

	/**
	 * Get column count (2: name, value)
	 */
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	/**
	 * Check if given cell is editable
	 * 
	 * @param row
	 *            table row
	 * @param col
	 *            table column
	 */
	public boolean isCellEditable(int row, int col) {
		if (col == COL_VALUE) {
			Class type = getPropertyType(row);
			if (type != null) {
				PropertyEditor editor = (PropertyEditor) m_prop_editors.get(type);
				if (editor == null)
					return false;
			}

			JETAPropertyDescriptor pd = getPropertyDescriptor(row);
			JETAPropertyDescriptor dpd = (JETAPropertyDescriptor) pd;
			return dpd.isWritable();
			// return ( pd.getWriteMethod() == null) ? false : true;
		}
		else {
			return false;
		}
	}

	/**
	 * Get text value for cell of table
	 * 
	 * @param row
	 *            table row
	 * @param col
	 *            table column
	 */
	public Object getValueAt(int row, int col) {
		Object value = null;

		if (col == COL_NAME) {
			value = m_descriptors[row].getDisplayName();
		}
		else {
			try {
				// COL_VALUE is handled
				JETAPropertyDescriptor dpd = getPropertyDescriptor(row);
				value = dpd.getPropertyValue(m_bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * Returns the Java type info for the property at the given row.
	 */
	public Class getPropertyType(int row) {
		return m_descriptors[row].getPropertyType();
	}

	/**
	 * Returns the PropertyDescriptor for the row.
	 */
	public JETAPropertyDescriptor getPropertyDescriptor(int row) {
		return (JETAPropertyDescriptor) m_descriptors[row];
	}

	/**
	 * Returns a new instance of the property editor for a given class. If an
	 * editor is not specified in the property descriptor then it is looked up
	 * in the PropertyEditorManager.
	 */
	public PropertyEditor getPropertyEditor(int row) {
		Class cls = m_descriptors[row].getPropertyEditorClass();
		PropertyEditor editor = null;

		if (cls != null) {

			try {
				editor = (PropertyEditor) cls.newInstance();
			} catch (Exception ex) {
				// XXX - debug
				System.out.println("PropertyTableModel: Instantiation exception creating PropertyEditor");
			}
		}
		else {

			// Look for a registered editor for this type.
			Class type = getPropertyType(row);
			if (type != null) {
				editor = (PropertyEditor) m_prop_editors.get(type);
				if (editor == null) {
					// Load a shared instance of the property editor.
					editor = PropertyEditorManager.findEditor(type);
					/**
					 * The property editor manager will return default editors
					 * for the Java primitives. Everything else is up to the
					 * application.
					 */

					if (editor != null)
						m_prop_editors.put(type, editor);
				}

				if (editor == null) {
					// Use the editor for Object.class
					editor = (PropertyEditor) m_prop_editors.get(Object.class);
					if (editor == null) {
						editor = PropertyEditorManager.findEditor(Object.class);
						if (editor != null)
							m_prop_editors.put(Object.class, editor);
					}

				}

				if (editor == null) {
					editor = m_unknown_editor;
				}

				// System.out.println( "PropertyEditorManager returned: " +
				// editor +
				// " for type: " + type );

			}
		}
		return editor;
	}

	/**
	 * Returns a flag indicating if the encapsulated object has a customizer.
	 */
	public boolean hasCustomizer() {
		if (m_beandescriptor != null) {
			Class cls = m_beandescriptor.getCustomizerClass();
			return (cls != null);
		}

		return false;
	}

	/**
	 * Gets the customizer for the current object.
	 * 
	 * @return New instance of the customizer or null if there isn't a
	 *         customizer.
	 */
	public Component getCustomizer() {
		Component customizer = null;

		if (m_beandescriptor != null) {
			Class cls = m_beandescriptor.getCustomizerClass();

			if (cls != null) {
				try {
					customizer = (Component) cls.newInstance();
				} catch (Exception ex) {
					// XXX - debug
					System.out.println("PropertyTableModel: Instantiation exception creating Customizer");
				}
			}
		}

		return customizer;
	}

	/**
	 * Method which registers property editors for types.
	 */
	private static void registerPropertyEditors() {
		PropertyEditorManager.registerEditor(Font.class, com.jeta.swingbuilder.gui.properties.editors.FontEditor.class);
		PropertyEditorManager.registerEditor(Color.class, com.jeta.swingbuilder.gui.properties.editors.ColorEditor.class);
		PropertyEditorManager.registerEditor(Boolean.class, com.jeta.swingbuilder.gui.properties.editors.BooleanEditor.class);
		PropertyEditorManager.registerEditor(boolean.class, com.jeta.swingbuilder.gui.properties.editors.BooleanEditor.class);
		PropertyEditorManager.registerEditor(String.class, com.jeta.swingbuilder.gui.properties.editors.StringEditor.class);
		PropertyEditorManager.registerEditor(Dimension.class, com.jeta.swingbuilder.gui.properties.editors.DimensionEditor.class);
		PropertyEditorManager.registerEditor(Icon.class, com.jeta.swingbuilder.gui.properties.editors.IconEditor.class);

		PropertyEditorManager.registerEditor(byte.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.ByteEditor.class);

		PropertyEditorManager.registerEditor(short.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.ShortEditor.class);
		PropertyEditorManager.registerEditor(Short.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.ShortEditor.class);

		PropertyEditorManager.registerEditor(int.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.IntegerEditor.class);
		PropertyEditorManager.registerEditor(Integer.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.IntegerEditor.class);

		PropertyEditorManager.registerEditor(long.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.LongEditor.class);
		PropertyEditorManager.registerEditor(Long.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.LongEditor.class);

		PropertyEditorManager.registerEditor(float.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.FloatEditor.class);
		PropertyEditorManager.registerEditor(Float.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.FloatEditor.class);

		PropertyEditorManager.registerEditor(double.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.DoubleEditor.class);
		PropertyEditorManager.registerEditor(Double.class, com.jeta.swingbuilder.gui.properties.editors.NumericEditor.DoubleEditor.class);

		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.ButtonGroupProperty.class,
				com.jeta.swingbuilder.gui.properties.editors.ButtonGroupEditor.class);
		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.ItemsProperty.class,
				com.jeta.swingbuilder.gui.properties.editors.ItemsEditor.class);
		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.TransformOptionsProperty.class,
				com.jeta.swingbuilder.gui.properties.editors.ComboEditor.class);
		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.CompoundBorderProperty.class,
				com.jeta.swingbuilder.gui.properties.editors.BorderEditor.class);
		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.CompoundLineProperty.class,
				com.jeta.swingbuilder.gui.properties.editors.LineEditor.class);
		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.effects.PaintProperty.class,
				com.jeta.swingbuilder.gui.properties.editors.FillEditor.class);
		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.ScrollBarsProperty.class,
				com.jeta.swingbuilder.gui.properties.editors.ScrollBarsEditor.class);

		PropertyEditorManager.registerEditor(com.jeta.forms.store.properties.TabbedPaneProperties.class,
				com.jeta.swingbuilder.gui.properties.editors.TabbedPaneEditor.class);

	}

	/**
	 * Set the table model to represents the properties of the object.
	 */
	public void setBean(JETABean bean) {
		if (bean == m_bean) {
			fireTableDataChanged();
			return;
		}

		m_bean = bean;

		if (m_bean == null || m_bean.getDelegate() == null) {
			if (m_descriptors != null && m_descriptors.length > 0) {
				m_descriptors = new JETAPropertyDescriptor[0];
				m_beaninfo = null;
				fireTableDataChanged();
			}
			return;
		}

		try {
			m_beaninfo = bean.getBeanInfo();
		} catch (Exception ex) {
			FormsLogger.severe(ex);
		}

		if (m_beaninfo != null) {
			m_beandescriptor = m_beaninfo.getBeanDescriptor();
			filterTable(getFilter());
		}
	}

	/**
	 * Set the value of the Values column.
	 */
	public void setValueAt(Object value, int row, int column) {
		if (column != COL_VALUE || m_descriptors == null || row > m_descriptors.length) {
			return;
		}

		try {
			Object old_value = getValueAt(row, column);

			if (old_value == null && value == null)
				return;

			if (old_value == value)
				return;

			if (value != null && value.equals(old_value))
				return;

			if (old_value != null && old_value.equals(value))
				return;

			// System.out.println( "setValueAt new_value: " + value + " " +
			// value.hashCode() + " old_value: " + old_value + " " +
			// old_value.hashCode() );

			JETAPropertyDescriptor dpd = getPropertyDescriptor(row);
			SetPropertyCommand cmd = new SetPropertyCommand(dpd, m_bean, value, old_value, FormComponent.getParentForm(m_bean));
			CommandUtils.invoke(cmd, FormEditor.getEditor(m_bean));
			fireTableRowsUpdated(row, row);
			firePropertyEditorEvent(new PropertyEditorEvent(PropertyEditorEvent.BEAN_PROPERTY_CHANGED, m_bean));
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

}
