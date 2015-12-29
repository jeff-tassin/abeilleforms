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

package com.jeta.open.gui.framework;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.ComponentFinder;
import com.jeta.open.support.ComponentFinderFactory;
import com.jeta.open.support.DefaultComponentFinder;
import com.jeta.open.support.SwingComponentSupport;

/**
 * A panel that supports locating components by name as well as supporting the
 * JETA controller/validation framework. Before adding components to this panel,
 * you should call {@link java.awt.Component#setName}. Components with unique
 * names can be accessed using the component getter methods in this class.
 * JETAPanel works with JETAController. Both classes follow the
 * Model-View-Controller architecture where JETAPanel is the view and
 * JETAController is the controller. Event handlers and listeners should be
 * declared in a JETAController derived class while view and layout code should
 * be confined to JETAPanel instances.
 * 
 * @author Jeff Tassin
 */
public class JETAPanel extends JPanel implements JETAContainer, SwingComponentSupport {
	/**
	 * Used for locating components by name in the container hierachy of this
	 * panel.
	 */
	private ComponentFinder m_finder;

	/**
	 * The controller that handles events for this panel
	 */
	private JETAController m_controller;

	/**
	 * we associate a director with the view the director is responsible for
	 * updating the ui components (menus/buttons/toolbars)
	 */
	private UIDirector m_uidirector;

	/**
	 * ctor
	 */
	public JETAPanel() {

	}

	/**
	 * ctor
	 */
	public JETAPanel(LayoutManager layout) {
		super(layout);
	}

	/**
	 * ctor
	 */
	public JETAPanel(ComponentFinder finder) {
		m_finder = finder;
	}

	/**
	 * Creates a component finder that is used to locate child components in
	 * this panel by name.
	 */
	protected ComponentFinder createComponentFinder() {
		if (m_finder == null) {
			ComponentFinderFactory ff = (ComponentFinderFactory) JETARegistry.lookup(ComponentFinderFactory.COMPONENT_ID);
			if (ff != null)
				m_finder = ff.createFinder(this);

			if (m_finder == null)
				m_finder = new DefaultComponentFinder(this);
		}
		return m_finder;
	}

	/**
	 * Enables/Disables the component associated with the given name.
	 * 
	 * @param commandId
	 *            the name of the component to enable/disable
	 * @param bEnable
	 *            true/false to enable/disable
	 */
	public void enableComponent(String commandId, boolean bEnable) {
		Collection comps = getComponentsByName(commandId);
		Iterator iter = comps.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if (comp.isEnabled() != bEnable)
				comp.setEnabled(bEnable);
		}
	}

	/**
	 * Returns the selected state of the AbstractButton that has the given name.
	 * If a component is found with the given name and that component is not an
	 * AbstractButton, then false is returned.
	 * 
	 * @return the selected state of the named AbstractButton.
	 */
	public boolean getBoolean(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof AbstractButton) {
			return ((AbstractButton) comp).isSelected();
		}
		else {
			return false;
		}
	}

	/**
	 * Returns the button that is contained in this panel and has the given
	 * name. If the component is not found nor is an AbstractButton, null is
	 * returned.
	 * 
	 * @return the named AbstractButton
	 */
	public AbstractButton getButton(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof AbstractButton)
			return (AbstractButton) comp;
		else
			return null;
	}

	/**
	 * Returns the JCheckBox that is contained in this panel and has the given
	 * name. If the component is not found nor is a JCheckBox, null is returned.
	 * 
	 * @return the named JCheckBox
	 */
	public JCheckBox getCheckBox(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JCheckBox)
			return (JCheckBox) comp;
		else
			return null;
	}

	/**
	 * Returns the JComboBox that is contained in this panel and has the given
	 * name. If the component is not found nor is a JComboBox, null is returned.
	 * 
	 * @return the named JComboBox
	 */
	public JComboBox getComboBox(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JComboBox)
			return (JComboBox) comp;
		else
			return null;
	}

	/**
	 * Locates the first component found in this container hierarchy that has
	 * the given name. This will recursively search into child containers as
	 * well. If no component is found with the given name, null is returned.
	 * 
	 * @param componentName
	 *            the name of the component to search for
	 * @return the named component
	 */
	public Component getComponentByName(String componentName) {
		Component comp = (Component) getComponentFinder().getComponentByName(componentName);
		if (comp == null) {
			System.err.println("JETAPanel.getComponentByName failed to find component: " + componentName);
		}
		return comp;
	}

	/**
	 * Returns the component finder associated with this panel
	 * 
	 * @return the component finder associated with this panel
	 */
	protected ComponentFinder getComponentFinder() {
		if (m_finder == null) {
			m_finder = createComponentFinder();
		}
		return m_finder;
	}

	/**
	 * Locates all components found in this container hierarchy that has the
	 * given name. This will recursively search into child containers as well.
	 * This method is useful for frame windows that can have multiple components
	 * with the same name. For example, a menu item and toolbar button for the
	 * same command would have the same name.
	 * 
	 * @return a collection of Component objects that have the given name.
	 */
	public Collection getComponentsByName(String compName) {
		return getComponentFinder().getComponentsByName(compName);
	}
	
	/**
	 * Recursively searches an associated parent container for all components
	 * that are named. An empty collection is returned if no names components
	 * exist.
	 * 
	 * @return a collection of all named Component objects.
	 */
	public Collection getAllNamedComponents() {
		return getComponentFinder().getAllNamedComponents();
	}

	/**
	 * Return the controller object that handles events for this panel. This
	 * framework assumes that all event listeners will be declared in a
	 * JETAController; however, this is not strictly required.
	 * 
	 * @return the controller that will handle events for this panel
	 */
	public JETAController getController() {
		return m_controller;
	}

	/**
	 * Locates the JTextField that has the given component name. The text in the
	 * field is converted to an integer and returned. If the text cannot be
	 * converted to an integer or the component is not a JTextField, the
	 * defaultValue is returned.
	 * 
	 * @param compName
	 *            the JTextField to find.
	 * @param defaultValue
	 *            the value to return if the component is not a JTextField or
	 *            the text in the field is not an integer.
	 * @return the text converted to an integer.
	 */
	public int getInteger(String compName, int defaultValue) {
		try {
			Component comp = getComponentByName(compName);
			if (comp instanceof JTextField) {
				return Integer.parseInt(((JTextField) comp).getText());
			}
		} catch (Exception e) {
		}
		return defaultValue;
	}

	/**
	 * Returns the JLabel that is contained in this panel and has the given
	 * name. If the component is not found nor is a JLabel, null is returned.
	 * 
	 * @return the named JLabel.
	 */
	public JLabel getLabel(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JLabel)
			return (JLabel) comp;
		else
			return null;
	}

	/**
	 * Returns the JList that is contained in this panel and has the given name.
	 * If the component is not found nor is a JList, null is returned.
	 * 
	 * @return the named JList
	 */
	public JList getList(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JList)
			return (JList) comp;
		else
			return null;
	}

	/**
	 * Returns the JPanel that is contained in this panel and has the given
	 * name. If the component is not found nor is a JPanel, null is returned.
	 * 
	 * @return the named JPanel
	 */
	public JPanel getPanel(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JPanel)
			return (JPanel) comp;
		else
			return null;
	}

	/**
	 * Returns JProgressBar that is contained in this panel and has the given
	 * name. If the component is not found nor is a JProgressBar, null is
	 * returned.
	 * 
	 * @return the named JProgressBar.
	 */
	public JProgressBar getProgressBar(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JProgressBar)
			return (JProgressBar) comp;
		else
			return null;
	}

	/**
	 * Returns the JRadioButton that is contained in this panel and has the
	 * given name. If the component is not found nor is a JRadioButton, null is
	 * returned.
	 * 
	 * @return the named JRadioButton
	 */
	public JRadioButton getRadioButton(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JRadioButton)
			return (JRadioButton) comp;
		else
			return null;
	}

	/**
	 * Returns the selected item from the JList or JComboBox that has the given
	 * name. If a list or combo is not found with the name, null is returned.
	 * 
	 * @return the selected item from the named JList or JComboBox.
	 */
	public Object getSelectedItem(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JList) {
			return ((JList) comp).getSelectedValue();
		}
		else if (comp instanceof JComboBox) {
			return ((JComboBox) comp).getSelectedItem();
		}
		else
			return null;
	}

	/**
	 * Returns JSpinner that is contained in this panel and has the given name.
	 * If the component is not found nor is a JSpinner, null is returned.
	 * 
	 * @return the named JSpinner
	 */
	public JSpinner getSpinner(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JSpinner)
			return (JSpinner) comp;
		else
			return null;
	}

	/**
	 * Returns the JTable that is contained in this panel and has the given
	 * name. If the component is not found nor is a JTable, null is returned.
	 * 
	 * @return the named JTable
	 */
	public JTable getTable(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JTable)
			return (JTable) comp;
		else
			return null;
	}

	/**
	 * Returns the JTabbedPane that is contained in this panel and has the given
	 * name. If the component is not found nor is a JTabbedPane, null is
	 * returned.
	 * 
	 * @return the named JTabbedPane
	 */
	public JTabbedPane getTabbedPane(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JTabbedPane)
			return (JTabbedPane) comp;
		else
			return null;
	}

	/**
	 * Returns the JTextComponent that is contained in this panel and has the
	 * given name. If the component is not found nor is a JTextComponent, null
	 * is returned.
	 * 
	 * @return the named JTextComponent
	 */
	public JTextComponent getTextComponent(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JTextComponent)
			return (JTextComponent) comp;
		else
			return null;

	}

	/**
	 * Returns the JTextField that is contained in this panel and has the given
	 * name. If the component is not found nor is a JTextField, null is
	 * returned.
	 * 
	 * @return the named JTextField
	 */
	public JTextField getTextField(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JTextField)
			return (JTextField) comp;
		else
			return null;

	}

	/**
	 * Returns the text property from a Component. If a component is not found
	 * with the given name or a component does not have a text property, then
	 * null is returned.
	 * 
	 * @return the text property.
	 */
	public String getText(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JTextComponent) {
			return ((JTextComponent) comp).getText();
		}
		else if (comp instanceof AbstractButton) {
			return ((AbstractButton) comp).getText();
		}
		else if (comp instanceof JLabel) {
			return ((JLabel) comp).getText();
		}
		else {
			try {
				if (comp != null) {
					Class c = comp.getClass();
					Class[] params = new Class[0];
					Object[] values = new Object[0];
					java.lang.reflect.Method m = c.getDeclaredMethod("getText", params);
					Object obj = m.invoke(comp, values);
					return obj == null ? null : obj.toString();
				}
			} catch (Exception e) {
				// ignore
			}
			return null;
		}
	}

	/**
	 * Returns the JTree that is contained in this panel and has the given name.
	 * If the component is not found nor is a JTree, null is returned.
	 * 
	 * @return the named JTree
	 */
	public JTree getTree(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JTree)
			return (JTree) comp;
		else
			return null;

	}

	/**
	 * Returns the UIDirector for this container. UIDirectors are part of this
	 * framework and are responsible for enabling/disabling components based on
	 * the program state. For example, menu items and toolbar buttons must be
	 * enabled or disabled depending on the current state of the frame window.
	 * UIDirectors handle this logic.
	 * 
	 * @return the UIDirector
	 */
	public UIDirector getUIDirector() {
		return m_uidirector;
	}

	/**
	 * Return the selected state of the AbstractButton that has the given name.
	 * If a component is found with the given name and that component is not an
	 * AbstractButton, then false is returned.
	 * 
	 * @see JETAPanel#getBoolean
	 */
	public boolean isSelected(String compName) {
		return getBoolean(compName);
	}

	/**
	 * Locates the component with the given name and removes it from its parent.
	 * 
	 * @param compName
	 *            the name of the component to locate.
	 */
	public void removeDescendent(String compName) {
		Component comp = getComponentByName(compName);
		if (comp != null) {
			removeFromParent(comp);
		}
	}

	/**
	 * Helper method that removes a component from its parent container.
	 * 
	 * @param comp
	 *            the component to remove
	 */
	public static void removeFromParent(Component comp) {
		if (comp != null) {
			Container parent = comp.getParent();
			if (parent != null) {
				parent.remove(comp);
				if (parent instanceof JComponent) {
					((JComponent) parent).revalidate();
				}
			}
		}
	}

	public void reset() {
		if (m_finder != null) {
			m_finder.reset();
		}
	}

	/**
	 * Sets the component finder associated with this panel
	 */
	protected void setComponentFinder(ComponentFinder finder) {
		m_finder = finder;
	}

	/**
	 * Sets the UIDirector for this panel.
	 */
	public void setUIDirector(UIDirector director) {
		m_uidirector = director;
	}

	/**
	 * Sets the main controller that will handle events for this panel.
	 */
	public void setController(JETAController controller) {
		m_controller = controller;
	}

	/**
	 * Shows/Hides the component with the given name.
	 * 
	 * @param compName
	 *            the name of the component to enable/disable
	 * @param bVisible
	 *            show/hide the component/disable
	 */
	public void setVisible(String compName, boolean bVisible) {
		Component comp = getComponentByName(compName);
		if (comp != null)
			comp.setVisible(bVisible);
	}

	/**
	 * Sets the selected attribute for the AbstractButton with the given name.
	 * If a component is found with the given name and that component is not an
	 * AbstractButton, this call is ignored.
	 * 
	 * @param compName
	 *            the name of the AbstractButton whose selected attribute to
	 *            set.
	 * @param sel
	 *            the selected attribute to set
	 */
	public void setSelected(String compName, boolean sel) {
		Component comp = getComponentByName(compName);
		if (comp instanceof AbstractButton) {
			((AbstractButton) comp).setSelected(sel);
		}
	}

	/**
	 * Sets the selected item in a JComboBox that has the given name. If a combo
	 * is not found with the name, no action is performed.
	 */
	public void setSelectedItem(String compName, Object value) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JComboBox) {
			((JComboBox) comp).setSelectedItem(value);
		}
		else if (comp instanceof JList) {
			((JList) comp).setSelectedValue(value, true);
		}
	}

	/**
	 * Sets text property for the Component with the given name. If no component
	 * is found or the Component does not have a text property, then this method
	 * is a no op.
	 * 
	 * @param compName
	 *            the name of the JTextComponent whose text to set
	 * @param txt
	 *            the text to set
	 */
	public void setText(String compName, String txt) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JTextComponent) {
			((JTextComponent) comp).setText(txt);
		}
		else if (comp instanceof JLabel) {
			((JLabel) comp).setText(txt);
		}
		else if (comp instanceof AbstractButton) {
			((AbstractButton) comp).setText(txt);
		}
		else {
			try {
				if (comp != null) {
					Class c = comp.getClass();
					Class[] params = new Class[] { String.class };
					Object[] values = new Object[] { txt };
					java.lang.reflect.Method m = c.getDeclaredMethod("setText", params);
					m.invoke(comp, values);
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * This is a helper method that simply forwards the call to the controller
	 * for this view (which forwards the call to the UIDirector if one exists)
	 */
	public void updateComponents() {
		updateComponents(null);
	}

	/**
	 * This is a helper method that simply forwards the call to the controller
	 * for this view (which forwards the call to the UIDirector if one exists)
	 */
	public void updateComponents(java.util.EventObject evt) {
		if (m_controller != null)
			m_controller.updateComponents(evt);
	}
}
