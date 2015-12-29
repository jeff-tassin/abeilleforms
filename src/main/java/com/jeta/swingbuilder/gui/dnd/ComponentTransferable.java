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

package com.jeta.swingbuilder.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.LinkedList;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.ComponentMementoProxy;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;

/**
 * This class represents a transferable for a ComponentMemento user is dragging
 * or copying.
 * 
 * @author Jeff Tassin.
 */
public class ComponentTransferable implements Transferable {

	/**
	 * The data flavors supported by this component
	 */
	private DataFlavor[] m_flavors;

	private ComponentMementoProxy m_memento_proxy;

	/**
	 * If this transferable contains linked forms, this is the list of form ids
	 * (Strings)
	 */
	private LinkedList m_paths;

	/**
	 * The absolute path of a top level linked form.
	 */
	private String m_form_path;

	private PaintProperty m_paint;

	/**
	 * ctor
	 */
	public ComponentTransferable(GridComponent gc) throws FormException {
		initialize(gc);
	}

	/**
	 * ctor
	 */
	private void initialize(ComponentMemento cm) {
		setComponentMemento(cm);
		m_flavors = new DataFlavor[] { FormObjectFlavor.COMPONENT_MEMENTO };
	}

	/**
	 * for a linked form
	 */
	private void initialize(GridComponent fc) throws FormException {
		if (fc.getBeanDelegate() != null) {
			Collection linked_forms = FormManagerDesignUtils.getLinkedForms(fc);
			if (linked_forms.size() > 0) {
				m_paths = new LinkedList();
				m_paths.addAll(linked_forms);

				if ((fc instanceof FormComponent) && ((FormComponent) fc).isLinked()) {
					if (((FormComponent) fc).getAbsolutePath() == null) {
						initialize(fc.getState(StateRequest.DEEP_COPY));
					}
					else {
						m_flavors = new DataFlavor[] { FormObjectFlavor.LINKED_FORM_SET, FormObjectFlavor.LINKED_FORM, FormObjectFlavor.COMPONENT_MEMENTO };

						FormMemento fm = (FormMemento) ((FormComponent) fc).getExternalState(StateRequest.DEEP_COPY);
						fm.setRelativePath(null);
						setComponentMemento(fm);
						m_form_path = ((FormComponent) fc).getAbsolutePath();
					}
				}
				else {
					m_flavors = new DataFlavor[] { FormObjectFlavor.LINKED_FORM_SET, FormObjectFlavor.COMPONENT_MEMENTO };
					/**
					 * this must be a shallow copy. we don't support 'Paste
					 * Embedded' an embbeded form with linked nests
					 */
					if (fc instanceof FormComponent) {
						FormComponent form = (FormComponent) fc;
						FormMemento fm = form.getExternalState(StateRequest.SHALLOW_COPY);
						fm.setRelativePath(null);
						setComponentMemento(fm);
					}
					else {
						/** this can happen if we have a FormContainerComponent */
						setComponentMemento(fc.getState(StateRequest.SHALLOW_COPY));
					}
				}
			}
			else {
				initialize(fc.getState(StateRequest.DEEP_COPY));
			}
		}
		else {
			m_flavors = new DataFlavor[0];
		}

		GridView parentview = fc.getParentView();
		PaintProperty paint = parentview.getPaintProperty(fc.getColumn(), fc.getRow());
		if (paint != null) {
			m_paint = paint;

			DataFlavor[] flavors = new DataFlavor[m_flavors.length + 1];
			System.arraycopy(m_flavors, 0, flavors, 0, m_flavors.length);
			flavors[flavors.length - 1] = FormObjectFlavor.CELL_BACKGROUND;
			m_flavors = flavors;
		}
	}

	private ComponentMementoProxy getComponentMemento() {
		return m_memento_proxy;
	}

	private void setComponentMemento(ComponentMemento cm) {
		m_memento_proxy = new ComponentMementoProxy(cm);

	}

	/**
	 * @return the transfer data for the given flavor Note that we can return an
	 *         array as well as a single object
	 */
	public Object getTransferData(DataFlavor flavor) {
		if (flavor.equals(FormObjectFlavor.COMPONENT_MEMENTO)) {
			return getComponentMemento();
		}
		else if (flavor.equals(FormObjectFlavor.LINKED_FORM_SET)) {
			return m_paths;
		}
		else if (flavor.equals(FormObjectFlavor.LINKED_FORM)) {
			return m_form_path;
		}
		else if (flavor.equals(FormObjectFlavor.CELL_BACKGROUND)) {
			return m_paint;
		}
		else {
			return null;
		}
	}

	/**
	 * Transferable implementation.
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return m_flavors;
	}

	/**
	 * Transferable implemenetation
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (int index = 0; index < m_flavors.length; index++) {
			if (flavor.equals(m_flavors[index]))
				return true;
		}
		return false;
	}
}
