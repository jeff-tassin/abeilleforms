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

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Defines an interface to describe a GridComponent's state in the form
 * designer. Different type of GridComponents have different mementos.
 * 
 * @author Jeff Tassin
 */
public class ComponentMemento extends AbstractJETAPersistable {
	static final long serialVersionUID = -8093663976165704348L;

	public static final int VERSION = 1;

	/**
	 * The GridComponent class name that this state represents. For example:
	 * <i>com.jeta.forms.gui.form.StandardComponent</i>
	 */
	private String m_comp_class;

	/**
	 * The cell constraints used in the FormLayout.
	 */
	private CellConstraintsMemento m_cc;

	/**
	 * Returns the cell constraints for the component specified by this memento.
	 * If this component represents a top level form, then this is null.
	 * 
	 * @return the cell constraints for this component.
	 */
	public CellConstraintsMemento getCellConstraintsMemento() {
		return m_cc;
	}

	/**
	 * Returns the class name of the GridComponent specified by this memento.
	 * For example: <i>com.jeta.forms.gui.form.StandardComponent</i>
	 * 
	 * @return the class name of the GridComponent that this memento represents.
	 */
	public String getComponentClass() {
		return m_comp_class;
	}

	/**
	 * Method used only for testing
	 */
	public void print() {
		//
	}

	/**
	 * Sets the GridComponent class name associated with this memento.
	 * 
	 * @param componentClass
	 *            the class name of the GridComponent.
	 */
	public void setComponentClass(String componentClass) {
		m_comp_class = componentClass;
	}

	/**
	 * Sets the CellConstraints assigned to the grid component associated with
	 * this memento.
	 * 
	 * @param cellConstraints
	 *            the CellConstraints to set.
	 */
	public void setCellConstraints(CellConstraints cellConstraints) {
		setCellConstraintsMemento(new CellConstraintsMemento(cellConstraints));
	}

	/**
	 * Sets the CellConstraints memento which defines the CellConstraints state
	 * assigned to the grid component associated with this memento.
	 * 
	 * @param cellConstraints
	 *            the CellConstraints to set.
	 */
	public void setCellConstraintsMemento(CellConstraintsMemento cellConstraints) {
		m_cc = cellConstraints;
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_cc = (CellConstraintsMemento) in.readObject("cellconstraints");
		m_comp_class = in.readString("componentclass");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("cellconstraints", m_cc);
		out.writeObject("componentclass", m_comp_class);
	}

}
