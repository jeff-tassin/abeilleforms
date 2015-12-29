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

package com.jeta.forms.store.properties;

import java.awt.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JTabbedPane;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.ContainedFormFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.EmptyCollection;

/**
 * A <code>TabbedPaneProperties</code> object is responsible for adding tabs
 * to a JTabbedPane at runtime. Since a JTabbedPane does not have a <i>tabs</i>
 * property, this custom property was created to provide a convienient way to
 * work with JTabbedPanes in the designer.
 * 
 * @author Jeff Tassin
 */
public class TabbedPaneProperties extends JETAProperty {
	static final long serialVersionUID = 3096566296606257673L;

	/**
	 * The current version number of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The Collection of tabs (TabProperty objects) in this property.
	 */
	private LinkedList m_items;

	/**
	 * The name of this property
	 */
	public static final String PROPERTY_ID = "tabs";

	/**
	 * Creates an unitialized <code>TabbedPaneProperties</code> instance.
	 */
	public TabbedPaneProperties() {
		super(PROPERTY_ID);
	}

	/**
	 * Adds a tab property to this tabbed pane.
	 * 
	 * @param prop
	 *            the tab property to add.
	 */
	public void addTab(TabProperty prop) {
		if (m_items == null)
			m_items = new LinkedList();

		m_items.add(prop);
	}

	/**
	 * Returns a collection of TabProperty objects that make up the tabs in the
	 * tab pane.
	 * 
	 * @return the tabs (TabProperty objects) in this property
	 */
	public Collection getTabs() {
		if (m_items != null)
			return m_items;
		else
			return EmptyCollection.getInstance();
	}

	/**
	 * Sets this property to that of another TabbedPaneProperties object.
	 * 
	 * @param prop
	 *            a TabbedPaneProperties object.
	 */
	public void setValue(Object prop) {
		if (prop instanceof TabbedPaneProperties) {
			TabbedPaneProperties tpp = (TabbedPaneProperties) prop;
			m_items = new LinkedList();
			if (tpp.m_items != null) {
				Iterator iter = tpp.m_items.iterator();
				while (iter.hasNext()) {
					TabProperty tp = (TabProperty) iter.next();
					TabProperty new_tp = new TabProperty();
					new_tp.setValue(tp);
					m_items.add(new_tp);
				}
			}
		}
		else {
			assert (false);
		}
	}

	/**
	 * Updates the bean with the current value of this property. In this case
	 * the tabs specified in this property are added to the the associated
	 * JTabbedPane bean. Any pre-existing tabs in the bean are removed.
	 */
	public void updateBean(JETABean jbean) {
		Component comp = null;
		if (jbean != null)
			comp = jbean.getDelegate();

		if (comp instanceof JTabbedPane) {
			JTabbedPane tpane = (JTabbedPane) comp;
			int selected_tab = tpane.getSelectedIndex();
			tpane.removeAll();

			if (m_items != null) {

				Iterator iter = m_items.iterator();
				while (iter.hasNext()) {
					TabProperty tp = (TabProperty) iter.next();
					if (FormUtils.isDesignMode()) {
						try {
							/**
							 * if we are in design mode, forward the call to the
							 * correct factory so any mouse and keyboard
							 * handlers can be added to the form
							 */
							ContainedFormFactory factory = (ContainedFormFactory) JETARegistry.lookup(ContainedFormFactory.COMPONENT_ID);
							ComponentSource compsrc = (ComponentSource) JETARegistry.lookup(ComponentSource.COMPONENT_ID);
							FormUtils.safeAssert(compsrc != null);
							FormUtils.safeAssert(factory != null);
							FormComponent topparent = factory.createTopParent(tpane, compsrc, tp.getForm());
							tpane.addTab(tp.getTitle(), tp.icon(), topparent);
						} catch (FormException e) {
							FormsLogger.severe(e);
						}
					}
					else {
						try {
							FormComponent fc = FormComponent.create();
							fc.setState(tp.getFormMemento());
							fc.setTopLevelForm(true);
							tpane.addTab(tp.getTitle(), tp.icon(), fc);
						} catch (FormException e) {
							FormsLogger.severe(e);
						}
					}
				}

				if ((selected_tab >= 0) && (selected_tab < tpane.getTabCount())) {
					tpane.setSelectedIndex(selected_tab);
				}
			}

			/**
			 * if we are in design mode, add a default tab if no tabs are
			 * currently defined. The default tab will automatically be removed
			 * if a user adds a tab in the designer
			 */
			if (FormUtils.isDesignMode()) {
				if (tpane.getTabCount() == 0) {
					tpane.addTab(I18N.getLocalizedMessage("Tab Pane"), new javax.swing.JLabel(""));
				}
			}

		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_items = (LinkedList) in.readObject("items");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("items", m_items);
	}

}
