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

package com.jeta.forms.gui.focus;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.LayoutFocusTraversalPolicy;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.FormContainerComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.forms.store.memento.CompositeFocusKey;
import com.jeta.forms.store.memento.ContainerFocusKey;
import com.jeta.forms.store.memento.FocusKey;
import com.jeta.forms.store.memento.FocusPolicyMemento;
import com.jeta.forms.store.memento.FormCellFocusKey;
import com.jeta.open.support.EmptyCollection;

/**
 * This class is responsible for handling the focus policy for a form. This
 * class is currently not being used since focus is not supported in the
 * designer.
 * 
 * @author Jeff Tassin
 */
public class FormFocusManager {
	/**
	 * The form whose focus policy we are managing.
	 */
	private FormComponent m_form;

	/**
	 * The list of components (Component objects )in their proper focus
	 * ordering.
	 */
	private ArrayList m_focus_list;

	/**
	 * ctor
	 */
	public FormFocusManager(FormComponent fc) {
		/**
		 * now set the focusCycleRoot to false for any child forms.
		 */
		disableChildFocusCycleRoots(fc);
		fc.setFocusCycleRoot(true);

		m_form = fc;

		/**
		 * First get all valid components from the stored focus policy. This
		 * list was created when the developer set the focus policy in the
		 * designer and stored the result.
		 */
		ArrayList stored_focus_set = buildStoredFocusList(fc);

		/**
		 * Now get the default focus policy which is assigned by Swing.
		 */
		LinkedHashSet default_focus_set = buildDefaultFocusPolicy(fc);

		/**
		 * Now we want to reconcile what has been stored versus the default
		 * policy because the form may have changed since the last time the
		 * focus policy was saved.
		 */

		/**
		 * now remove any elements from the stored policy that are not found in
		 * the default focus policy
		 */
		Iterator iter = stored_focus_set.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if (!default_focus_set.contains(comp)) {
				iter.remove();
			}
		}

		/** used for quick lookups */
		HashSet stored_lookup = new HashSet();
		stored_lookup.addAll(stored_focus_set);

		/**
		 * now iterate over the default focus policy. Any components not found
		 * in the stored focus policy are added at the default position
		 */
		Component prev_comp = null;
		iter = default_focus_set.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if (!stored_lookup.contains(comp)) {
				stored_lookup.add(comp);
				if (prev_comp == null) {
					stored_focus_set.add(0, comp);
				}
				else {
					int pos = stored_focus_set.indexOf(prev_comp);
					assert (pos >= 0);
					if (pos >= 0) {
						stored_focus_set.add(pos + 1, comp);
					}
				}
			}
			prev_comp = comp;
		}

		/** now the stored_focus_set has the correct focus order */
		m_focus_list = stored_focus_set;

	}

	/**
	 * ctor
	 */
	public FormFocusManager(FormComponent rootForm, Collection focusList) {
		m_form = rootForm;
		m_focus_list = new ArrayList(focusList);
	}

	/**
	 * Builds a list of components (Component) for a form that are ordered in
	 * the default focus order.
	 */
	public static LinkedHashSet buildDefaultFocusPolicy(FormComponent fc) {
		System.out.println("buildDefaultFocusPolicy  form: " + fc.getId());

		final FormComponent theform = fc;
		LinkedHashSet default_policy = new LinkedHashSet();
		LayoutFocusTraversalPolicy policy = new LayoutFocusTraversalPolicy() {
			protected boolean accept(Component aComponent) {
				if (aComponent instanceof StandardComponent) {
					if (((StandardComponent) aComponent).getBeanDelegate() == null)
						return false;
				}

				if (aComponent == theform)
					return super.accept(aComponent);

				if (aComponent instanceof FormComponent) {
					if (((FormComponent) aComponent).isTopLevelForm())
						return false;
				}

				if (aComponent instanceof JTabbedPane)
					return true;

				if (aComponent != null) {
					/**
					 * handle the case for embedded focus cycle roots such as
					 * JTabbedPane forms
					 */
					Container cc = aComponent.getParent();
					while (cc != null && cc != theform) {
						if (cc instanceof FormContainerComponent) {
							return false;
						}

						cc = cc.getParent();
					}
				}
				return super.accept(aComponent);
			}
		};
		Component comp = policy.getFirstComponent(fc);
		Component last_comp = policy.getLastComponent(fc);
		while (true) {

			/**
			 * Don't add scroll pane in design mode since the scroll bars might
			 * not be visible
			 */
			if (FormUtils.isDesignMode()) {
				if (!(comp instanceof JScrollPane) && !(comp instanceof JScrollBar)) {
					default_policy.add(comp);
				}
			}
			else {
				default_policy.add(comp);
			}

			if (comp == last_comp)
				break;

			System.out.println("FormFocusManager.getComponentAfter: " + comp.getClass());
			comp = policy.getComponentAfter(fc, comp);
		}

		return default_policy;
	}

	/**
	 * Builds an list of components that are ordered in the focus order
	 * previously set by a user for a given form. The form may have changed
	 * (components might have been deleted or moved), so we need to delete those
	 * components from the focus order that don't match the current state of the
	 * form.
	 */
	ArrayList buildStoredFocusList(FormComponent form) {
		ArrayList focus_list = new ArrayList();
		FocusPolicyMemento memento = form.getFocusPolicy();
		if (memento != null) {
			Collection fkeys = memento.getFocusPolicyKeys();
			Iterator iter = fkeys.iterator();
			while (iter.hasNext()) {
				FocusKey fkey = (FocusKey) iter.next();
				Component comp = fkey.getComponent(form);
				if (comp != null) {
					focus_list.add(comp);
				}
			}
		}
		return focus_list;
	}

	/**
	 * Builds the set of FocusKey objects for all focusable components on the
	 * form. A FocusKey allows us to store a reference to a focusable component
	 * and later find that component when a form has been de-serialized. We
	 * don't use the component name to reference the component because we don't
	 * want to keep track of when the user changes the name.
	 */
	public void buildFocusKeys(HashSet currentFocusSet, HashMap focus_key_map, FormComponent form, CompositeFocusKey compositeKey) {
		for (int row = 1; row <= form.getRowCount(); row++) {
			for (int col = 1; col <= form.getColumnCount(); col++) {
				GridComponent gc = form.getGridComponent(col, row);
				if (gc instanceof StandardComponent) {
					Component comp = gc.getBeanDelegate();
					if (comp != null) {
						if (currentFocusSet.contains(comp)) {
							CompositeFocusKey ckey = (CompositeFocusKey) compositeKey.clone();
							ckey.add(new FormCellFocusKey(row, col, comp));
							focus_key_map.put(comp, ckey);
						}
						else {
							/**
							 * This comp must be a container that contains
							 * components that are in the focus policy This can
							 * happen for Java Beans that are also panels which
							 * contain other components.
							 */
							if (comp instanceof Container) {
								CompositeFocusKey ckey = (CompositeFocusKey) compositeKey.clone();
								ckey.add(new FormCellFocusKey(row, col, comp));
								buildContainerFocusKeys(currentFocusSet, focus_key_map, (Container) comp, ckey);
							}
							else {
								// ignore because this could be a component like
								// a JLabel which does not need focus
							}
						}
					}
				}
				else if (gc instanceof FormComponent) {
					FormComponent childform = (FormComponent) gc;
					CompositeFocusKey ckey = (CompositeFocusKey) compositeKey.clone();
					ckey.add(new FormCellFocusKey(row, col, gc));
					buildFocusKeys(currentFocusSet, focus_key_map, childform, ckey);
				}
				else {
					if (gc != null) {
						System.out.println("FormFocusManager.buildDefaultPolicyFailed  found unknown comp: " + gc.getClass());
					}
				}
			}
		}
	}

	/**
	 * Builds a container focus key
	 */
	public void buildContainerFocusKeys(HashSet currentFocusSet, HashMap focus_key_map, Container container, CompositeFocusKey compositeKey) {
		for (int index = 0; index < container.getComponentCount(); index++) {
			Component comp = container.getComponent(index);
			if (currentFocusSet.contains(comp)) {
				CompositeFocusKey ckey = (CompositeFocusKey) compositeKey.clone();
				ckey.add(new ContainerFocusKey(index, comp));
				focus_key_map.put(comp, ckey);
			}
			else {
				// this comp must be a container that contains components that
				// are in the focus policy
				if (comp instanceof Container) {
					CompositeFocusKey ckey = (CompositeFocusKey) compositeKey.clone();
					ckey.add(new ContainerFocusKey(index, comp));
					buildContainerFocusKeys(currentFocusSet, focus_key_map, (Container) comp, ckey);
				}
			}
		}
	}

	/**
	 * @return true if the current focus list contains the given component
	 */
	public boolean contains(Component comp) {
		return m_focus_list.contains(comp);
	}

	/**
	 * Sets the focusCycleRoot to false for any child forms.
	 */
	private void disableChildFocusCycleRoots(Container cc) {
		if (cc == null)
			return;

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof StandardComponent || comp instanceof FormPanel) {
				((Container) comp).setFocusCycleRoot(false);
				disableChildFocusCycleRoots((Container) comp);
			}
			else if (comp instanceof Container) {
				disableChildFocusCycleRoots((Container) comp);
			}
		}
	}

	/**
	 * @return the focus policy memento that represents the current focus
	 *         ordering for this manager
	 */
	public FocusPolicyMemento getFocusPolicyMemento() {
		HashSet current_focus_set = new HashSet();
		current_focus_set.addAll(m_focus_list);

		HashMap focus_key_map = new HashMap();
		CompositeFocusKey cfk = new CompositeFocusKey();
		/** first build location keys for all components in the the form */
		buildFocusKeys(current_focus_set, focus_key_map, m_form, cfk);

		FocusPolicyMemento memento = new FocusPolicyMemento();

		/** now iterate over the focus list and get the corresponding focus key */
		Iterator iter = m_focus_list.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			FocusKey fkey = (FocusKey) focus_key_map.get(comp);
			if (fkey != null)
				memento.addFocusKey(fkey);
		}

		return memento;
	}

	/**
	 * @return the component at the index in the current focus list
	 */
	public Component getComponent(int index) {
		if (index < 0 || index >= m_focus_list.size())
			return null;

		return (Component) m_focus_list.get(index);
	}

	/**
	 * @return the number of components in the focus list
	 */
	public int getComponentCount() {
		return m_focus_list.size();
	}

	public Collection getFocusList() {
		if (m_focus_list == null)
			return EmptyCollection.getInstance();
		else
			return m_focus_list;
	}

	/**
	 * Checks that all focuskeys reference the correct components
	 */
	public void validateFocusKeys(FormComponent root, FocusPolicyMemento memento) {
		Iterator iter = memento.getFocusPolicyKeys().iterator();
		while (iter.hasNext()) {
			FocusKey fkey = (FocusKey) iter.next();
			Component comp = fkey.getComponent(root);
			assert (comp != null);
			System.out.print("Focuskey validated: ");
			fkey.print();
			System.out.println("   comp: " + comp.getClass());
		}
	}

}
