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

package com.jeta.swingbuilder.gui.commands;

import java.awt.Component;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.store.properties.TransformOptionsProperty;

public class SetPropertyCommand extends FormUndoableEdit {
	/**
	 * The JETABean whose delegate we are changing
	 */
	private JETABean m_bean;

	/**
	 * The parent grid component that contains the bean
	 */
	private GridComponent m_gc;

	private Object m_newvalue;
	private Object m_oldvalue;

	private JETAPropertyDescriptor m_property_descriptor;

	public SetPropertyCommand(JETAPropertyDescriptor pd, JETABean bean, Object newValue, Object oldValue, FormComponent form) {
		super(form);
		m_property_descriptor = pd;
		m_bean = bean;
		m_newvalue = newValue;

		Component comp = bean.getParent();
		while (comp != null) {
			if (comp instanceof GridComponent) {
				m_gc = (GridComponent) comp;
				break;
			}
		}

		/**
		 * Special case for TransformOptionsProperty
		 */
		if (oldValue instanceof TransformOptionsProperty) {
			m_oldvalue = ((TransformOptionsProperty) oldValue).getCurrentItem();
		}
		else {
			m_oldvalue = oldValue;
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof SetPropertyCommand) {
			SetPropertyCommand prop = (SetPropertyCommand) obj;
			if (m_bean == prop.m_bean) {
				if (m_newvalue == prop.m_newvalue)
					return true;
				return (m_newvalue != null && m_newvalue.equals(prop.m_newvalue));
			}
		}
		return false;
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		try {
			// System.out.println( "redo: " + toString() );
			m_property_descriptor.setPropertyValue(m_bean, m_newvalue);
			if (m_gc != null) {
				m_gc.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_CHANGED, m_gc));
			}
		} catch (Exception e) {
			throw new CannotRedoException();
		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotUndoException {
		super.undo();
		try {
			m_property_descriptor.setPropertyValue(m_bean, m_oldvalue);
			if (m_gc != null) {
				m_gc.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_CHANGED, m_gc));
			}
		} catch (Exception e) {
			throw new CannotUndoException();
		}
	}

	public String toString() {
		return "SetPropertyCommand " + m_property_descriptor.getName() + "  newvalue: " + m_newvalue + "  oldvalue: " + m_oldvalue;
	}

}
