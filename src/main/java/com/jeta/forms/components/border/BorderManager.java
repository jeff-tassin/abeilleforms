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

package com.jeta.forms.components.border;

import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * This class maintains a list of default borders for swing components. The
 * runtime uses the BorderManager to obtain default borders for components in
 * cases where the user has specified a default border in addition to other
 * borders in the border properties designer.
 * 
 * If the look and feel changes, this class will automatically update the list
 * based on the new look and feel.
 * 
 * @author Jeff Tassin
 */
public class BorderManager {
	/**
	 * A map of default borders for component classes. m_default_borders<Class,Border>
	 */
	private static HashMap m_default_borders = new HashMap();

	/**
	 * The current look and feel
	 */
	private static LookAndFeel m_look_and_feel;

	/**
	 * @return the default border for the given component.
	 */
	public static Border getDefaultBorder(JComponent comp) {
		synchronized (m_default_borders) {
			if (comp != null) {
				if (m_default_borders.size() == 0 || isLookAndFeelChanged()) {
					resetDefaultBorders();
				}

				Border default_border = (Border) m_default_borders.get(comp.getClass());
				if (default_border == null) {
					Class comp_class = comp.getClass();
					try {
						JComponent new_comp = (JComponent) comp_class.newInstance();
						default_border = new_comp.getBorder();
						m_default_borders.put(comp_class, default_border);
					} catch (Exception e) {
						e.printStackTrace();
						m_default_borders.put(comp_class, null);
					}
				}
				return default_border;
			}
		}
		return null;
	}

	private static Border getDefaultBorder(String borderResource) {
		Border b = UIManager.getBorder(borderResource);
		return b;
	}

	private static boolean isLookAndFeelChanged() {
		LookAndFeel lf = UIManager.getLookAndFeel();
		if (m_look_and_feel != lf) {
			m_look_and_feel = lf;
			return true;
		}
		return false;
	}

	/**
	 * Resets the default borders.
	 */
	private static void resetDefaultBorders() {
		m_default_borders.clear();
		m_default_borders.put(JButton.class, getDefaultBorder("Button.border"));
		m_default_borders.put(JComboBox.class, getDefaultBorder("ComboBox.border"));
		m_default_borders.put(JLabel.class, new JLabel().getBorder());
		m_default_borders.put(JList.class, getDefaultBorder("List.border"));
		m_default_borders.put(JSpinner.class, getDefaultBorder("Spinner.border"));
		m_default_borders.put(JTable.class, new JTable().getBorder());
		m_default_borders.put(JTextArea.class, getDefaultBorder("TextArea.border"));
		m_default_borders.put(JTextField.class, getDefaultBorder("TextField.border"));
		m_default_borders.put(JTree.class, new JTree().getBorder());
		m_default_borders.put(JScrollPane.class, new JScrollPane().getBorder());

		m_default_borders.put(JCheckBox.class, new javax.swing.border.EmptyBorder(1, 0, 1, 2));
		m_default_borders.put(JRadioButton.class, new javax.swing.border.EmptyBorder(1, 0, 1, 2));

		/**
		 * JCheckBox and JRadioButton have overriden borders because their
		 * default borders have a horizontal offset that produces a rather
		 * unpleasant looking GUI.
		 */
	}
}
