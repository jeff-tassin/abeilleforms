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

package com.jeta.open.support;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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

/**
 * Interface that defines common methods for working with SwingComponents in a
 * Container.
 * 
 * @author Jeff Tassin
 */
public interface SwingComponentSupport extends ComponentFinder {
	/**
	 * Enables/Disables the component associated with the commandid
	 * 
	 * @param commandId
	 *            the id of the command whose button to enable/disable
	 * @param bEnable
	 *            true/false to enable/disable
	 */
	public void enableComponent(String commandId, boolean bEnable);

	/**
	 * Returns the selected state of the AbstractButton that has the given name.
	 * If a component is found with the given name and that component is not an
	 * AbstractButton, then false is returned.
	 * 
	 * @return the selected state of the named AbstractButton.
	 */
	public boolean getBoolean(String compName);

	/**
	 * Returns the button that is contained in this panel and has the given
	 * name. If the component is not found nor is an AbstractButton, null is
	 * returned.
	 * 
	 * @return the named AbstractButton
	 */
	public AbstractButton getButton(String compName);

	/**
	 * Returns the JCheckBox that is contained in this panel and has the given
	 * name. If the component is not found nor is a JCheckBox, null is returned.
	 * 
	 * @return the named JCheckBox
	 */
	public JCheckBox getCheckBox(String compName);

	/**
	 * Returns the JComboBox that is contained in this panel and has the given
	 * name. If the component is not found nor is a JComboBox, null is returned.
	 * 
	 * @return the named JComboBox
	 */
	public JComboBox getComboBox(String compName);

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
	public int getInteger(String compName, int defaultValue);

	/**
	 * Returns the JLabel that is contained in this panel and has the given
	 * name. If the component is not found nor is a JLabel, null is returned.
	 * 
	 * @return the named JLabel.
	 */
	public JLabel getLabel(String compName);

	/**
	 * Returns the JList that is contained in this panel and has the given name.
	 * If the component is not found nor is a JList, null is returned.
	 * 
	 * @return the named JList
	 */
	public JList getList(String compName);

	/**
	 * Returns the JPanel that is contained in this panel and has the given
	 * name. If the component is not found nor is a JPanel, null is returned.
	 * 
	 * @return the named JPanel
	 */
	public JPanel getPanel(String compName);

	/**
	 * Returns JProgressBar that is contained in this panel and has the given
	 * name. If the component is not found nor is a JProgressBar, null is
	 * returned.
	 * 
	 * @return the named JProgressBar.
	 */
	public JProgressBar getProgressBar(String compName);

	/**
	 * Returns the JRadioButton that is contained in this panel and has the
	 * given name. If the component is not found nor is a JRadioButton, null is
	 * returned.
	 * 
	 * @return the named JRadioButton
	 */
	public JRadioButton getRadioButton(String compName);

	/**
	 * Returns the selected item from the JList or JComboBox that has the given
	 * name. If a list or combo is not found with the name, null is returned.
	 * 
	 * @return the selected item from the named JList or JComboBox.
	 */
	public Object getSelectedItem(String compName);

	/**
	 * Returns JSpinner that is contained in this panel and has the given name.
	 * If the component is not found nor is a JSpinner, null is returned.
	 * 
	 * @return the named JSpinner
	 */
	public JSpinner getSpinner(String compName);

	/**
	 * Returns the JTable that is contained in this panel and has the given
	 * name. If the component is not found nor is a JTable, null is returned.
	 * 
	 * @return the named JTable
	 */
	public JTable getTable(String compName);

	/**
	 * Returns the JTabbedPane that is contained in this panel and has the given
	 * name. If the component is not found nor is a JTabbedPane, null is
	 * returned.
	 * 
	 * @return the named JTabbedPane
	 */
	public JTabbedPane getTabbedPane(String compName);

	/**
	 * Returns the JTextComponent that is contained in this panel and has the
	 * given name. If the component is not found nor is a JTextComponent, null
	 * is returned.
	 * 
	 * @return the named JTextComponent
	 */
	public JTextComponent getTextComponent(String compName);

	/**
	 * Returns the JTextField that is contained in this panel and has the given
	 * name. If the component is not found nor is a JTextField, null is
	 * returned.
	 * 
	 * @return the named JTextField
	 */
	public JTextField getTextField(String compName);

	/**
	 * Returns the text property from a Component. If a component is not found
	 * with the given name or a component does not have a text property, then
	 * null is returned.
	 * 
	 * @return the text property.
	 */
	public String getText(String compName);

	/**
	 * Returns the JTree that is contained in this panel and has the given name.
	 * If the component is not found nor is a JTree, null is returned.
	 * 
	 * @return the named JTree
	 */
	public JTree getTree(String compName);

	/**
	 * Return the selected state of the AbstractButton that has the given name.
	 * If a component is found with the given name and that component is not an
	 * AbstractButton, then false is returned.
	 * 
	 * @see SwingCompnentSupport#getBoolean
	 */
	public boolean isSelected(String compName);

	/**
	 * Shows/Hides the component with the given name.
	 * 
	 * @param compName
	 *            the name of the component to enable/disable
	 * @param bVisible
	 *            show/hide the component/disable
	 */
	public void setVisible(String compName, boolean bVisible);

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
	public void setSelected(String compName, boolean sel);

	/**
	 * Sets the selected item in a JComboBox that has the given name. If a combo
	 * is not found with the name, no action is performed.
	 */
	public void setSelectedItem(String compName, Object value);

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
	public void setText(String compName, String txt);

}
