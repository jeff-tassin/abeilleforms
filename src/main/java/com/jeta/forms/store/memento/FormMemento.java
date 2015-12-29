/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.store.memento;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.support.Matrix;

/**
 * This class represents the state of a FormComponent including all of its Java
 * bean components and nested forms. A FormMemento can be safely serialized to
 * an i/o stream. You can get the state of a FormComponent by calling:
 * {@link com.jeta.forms.gui.form.FormComponent#getState} Likewise, you can set
 * the state of a FormComponent by calling:
 * {@link com.jeta.forms.gui.form.FormComponent#setState} Once you have a
 * FormMemento object, you can create and initialize multiple FormComponents
 * from the memento.
 * 
 * <pre>
 * 
 *     // assume you have a given form component
 *     FormComponent fc1 = ...;
 * 
 *     FormMemento memento = fc1.getState();
 *     FormComponent fc2 = new FormComponent();
 *     fc2.setState( memento );
 * 
 *     FormComponent fc3 = new FormComponent();
 *     fc3.setState( memento );
 *    
 *     //  fc2 and fc3 are copies of fc1 
 *  
 * </pre>
 * 
 * @author Jeff Tassin
 */
public class FormMemento extends ComponentMemento {
	static final long serialVersionUID = -7808404997780438089L;

	/**
	 * The version of this class.
	 */
	public static final int VERSION = 5;

	/**
	 * The encoded row specifications used in the FormLayout for this form.
	 */
	private String m_row_specs;

	/**
	 * The encoded column specifications used in the FormLayout for this form.
	 */
	private String m_column_specs;

	/**
	 * A list of child component states (ComponentMemento)
	 */
	private LinkedList m_components = new LinkedList();

	/**
	 * The bean properties for the form such as background color, opaque, fill,
	 * etc. This variable will be null for class versions less than 5.
	 */
	private PropertiesMemento m_properties_memento;

	/**
	 * The relative path that contains this form if it is linked form.
	 */
	private String m_path;

	/**
	 * A unique id for this form.
	 */
	private String m_id;

	/**
	 * The cell painters for this form. These are responsible for fill effects
	 * for an individual cell.
	 */
	private Matrix m_cell_painters;

	/**
	 * The row group assignments for the form.
	 * 
	 * @see com.jgoodies.forms.layout.FormLayout#setRowGroups(int[][])
	 */
	private FormGroupSet m_row_groups;

	/**
	 * The column group assignments for the form.
	 * 
	 * @see com.jgoodies.forms.layout.FormLayout#setColumnGroups(int[][])
	 */
	private FormGroupSet m_column_groups;

	/**
	 * The focus policy for this form. Can be null. (Not currently used).
	 */
	private FocusPolicyMemento m_focus_policy;

	/**
	 * Properties for the form. These are properties such as background color
	 * and border that are applied to the form as a whole. m_properties<String,Object>
	 * where: String: property name Object: property value (must be
	 * serializable)
	 * 
	 * @deprecated As of version 5 of this class. PropertiesMemento is used
	 *             instead.
	 */
	private HashMap m_properties = new HashMap();

	/**
	 * Adds a child component's state to the list of states owned by this
	 * memento.
	 * 
	 * @param memento
	 *            the state of a Java Bean (BeanComponent) or nested form that
	 *            is contained by this form.
	 */
	public void addComponent(ComponentMemento memento) {
		m_components.add(memento);
	}

	/**
	 * Returns a matrix that defines the painters for individual cells in the
	 * form.
	 * 
	 * @return A matrix of cell painters
	 */
	public Matrix getCellPainters() {
		return m_cell_painters;
	}

	/**
	 * Return the column groups for the form.
	 * 
	 * @return the column groups for the form
	 */
	public FormGroupSet getColumnGroups() {
		return m_column_groups;
	}

	/**
	 * Return an encoded string of column specs for this form. Each column spec
	 * is separated by a comma.
	 * 
	 * @return the encoded ColumnSpecs used by the FormLayout for this form.
	 */
	public String getColumnSpecs() {
		return m_column_specs;
	}

	/**
	 * Returns the focus policy for the form.
	 * 
	 * @return the focus policy for this form
	 * @deprecated no longer supporting focus in the designer.
	 */
	public FocusPolicyMemento getFocusPolicy() {
		return m_focus_policy;
	}

	/**
	 * Returns a unique id for this form. The id is used primarily in the
	 * designer and has no effect during runtime.
	 * 
	 * @return the unique id for this form
	 */
	public String getId() {
		return m_id;
	}

	/**
	 * Returns properties memento for this form. The PropertiesMemento stores
	 * the Java Bean properties and custom properties for a component.
	 * 
	 * @return the properties memento to associated with this form.
	 */
	public PropertiesMemento getPropertiesMemento() {
		return m_properties_memento;
	}

	/**
	 * @return the form properties
	 * @deprecated replaced by getPropertiesMemento. see
	 *             #getPropertiesMemento().
	 */
	public HashMap getProperties() {
		return m_properties;
	}

	/**
	 * Returns the relative path where this form is located. This is the same as
	 * the package that contains the form.
	 * 
	 * @return the relative path where this form is located.
	 */
	public String getRelativePath() {
		return m_path;
	}

	/**
	 * Return the row groups for this form.
	 * 
	 * @return the row groups for the form
	 */
	public FormGroupSet getRowGroups() {
		return m_row_groups;
	}

	/**
	 * Return an encoded string of row specs for this form. Each row spec is
	 * separated by a comma.
	 * 
	 * @return the encoded RowSpecs used by the FormLayout for this form.
	 */
	public String getRowSpecs() {
		return m_row_specs;
	}

	/**
	 * Returns an iterator to a set of ComponentMemento objects which are
	 * contained by this form.
	 * 
	 * @returns an iterator that can be used to iterate over all child
	 *          ComponentMemento objects currently contained in this mememnto
	 */
	public Iterator iterator() {
		return m_components.iterator();
	}

	/**
	 * Used for testing
	 */
	public void print() {
		System.out.println(" >>>>>>>>>>>>>>>>>  FormMemento state >>>>>>>>>>>>>> ");
		System.out.println("rowSpecs: " + m_row_specs);
		System.out.println("colSpecs: " + m_column_specs);
		System.out.println(" ----------- comp mementos --------- ");
		Iterator iter = m_components.iterator();
		while (iter.hasNext()) {
			ComponentMemento cm = (ComponentMemento) iter.next();
			cm.print();
		}
	}

	/**
	 * Sets the cell painters defined in the form.
	 * 
	 * @param painters
	 *            A matrix of cell painters
	 */
	public void setCellPainters(Matrix painters) {
		m_cell_painters = painters;
	}

	/**
	 * Set the column groups for the form
	 * 
	 * @param colgrps
	 *            the column groups
	 * @see com.jgoodies.forms.layout.FormLayout#setColumnGroups(int[][])
	 */
	public void setColumnGroups(FormGroupSet colgrps) {
		m_column_groups = colgrps;
	}

	/**
	 * Sets the encoded ColumnSpecs used by the FormLayout for this form.
	 * 
	 * @param colSpecs
	 *            a comma separated list of column specs.
	 */
	public void setColumnSpecs(String colSpecs) {
		m_column_specs = colSpecs;
	}

	/**
	 * Sets the focus policy for this form
	 * 
	 * @param memento
	 *            the focus policy state
	 * @deprecated focus is no longer supported in the designer.
	 */
	public void setFocusPolicy(FocusPolicyMemento memento) {
		m_focus_policy = memento;
	}

	/**
	 * Sets the unique id for this form. Ids are only used by the designer and
	 * have no effect during runtime.
	 * 
	 * @param id
	 *            the id to assign to the form.
	 */
	public void setId(String id) {
		m_id = id;
	}

	/**
	 * Sets the relative path where this form is located. This is the same as
	 * the package that contains the form.
	 * 
	 * @param path
	 *            the relative path to set for the form.
	 */
	public void setRelativePath(String path) {
		m_path = path;
	}

	/**
	 * Set the row groups for the form
	 * 
	 * @param rowgrps
	 *            the row groups to set
	 * @see com.jgoodies.forms.layout.FormLayout#setRowGroups(int[][])
	 */
	public void setRowGroups(FormGroupSet rowgrps) {
		m_row_groups = rowgrps;
	}

	/**
	 * Sets the form properties
	 * 
	 * @deprecated As of version 5 of this class.
	 * @see #setPropertiesMemento(PropertiesMemento)
	 */
	public void setProperties(HashMap props) {
		m_properties = props;
	}

	/**
	 * Sets the properties memento for this form. The PropertiesMemento stores
	 * standard Java bean properties and custom properties.
	 * 
	 * @param pm
	 *            the properties memento to associated with this form.
	 */
	public void setPropertiesMemento(PropertiesMemento pm) {
		m_properties_memento = pm;
	}

	/**
	 * Sets the RowSpecs used by the FormLayout for this form.
	 * 
	 * @param rowSpecs
	 *            a comma separated list of row specs for the form.
	 */
	public void setRowSpecs(String rowSpecs) {
		m_row_specs = rowSpecs;
	}

	/**
	 * Returns the number of child components in this form.
	 */
	public int size() {
		return m_components.size();
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_id = (String) in.readObject("id");
		m_path = (String) in.readObject("path");
		if (FormUtils.isDesignMode()) {
			m_path = FormUtils.fixPath(m_path);
		}

		m_row_specs = (String) in.readObject("rowspecs");
		m_column_specs = (String) in.readObject("colspecs");
		m_components = (LinkedList) in.readObject("components");

		if (version >= 5) {
			m_properties_memento = (PropertiesMemento) in.readObject("properties");
		}
		else {
			m_properties = (HashMap) in.readObject("properties");
		}

		if (version >= 2)
			m_cell_painters = (Matrix) in.readObject("cellpainters");

		if (version >= 3) {
			m_focus_policy = (FocusPolicyMemento) in.readObject("focuspolicy");
			// not doing focus anymore
			m_focus_policy = null;
		}

		if (version >= 4) {
			m_row_groups = (FormGroupSet) in.readObject("rowgroups");
			m_column_groups = (FormGroupSet) in.readObject("colgroups");
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(ComponentMemento.class));
		out.writeVersion(VERSION);
		out.writeObject("id", m_id);
		out.writeObject("path", m_path);
		out.writeObject("rowspecs", m_row_specs);
		out.writeObject("colspecs", m_column_specs);
		out.writeObject("components", m_components);
		out.writeObject("properties", m_properties_memento);
		out.writeObject("cellpainters", m_cell_painters);
		out.writeObject("focuspolicy", m_focus_policy);
		out.writeObject("rowgroups", m_row_groups);
		out.writeObject("colgroups", m_column_groups);
	}
}
