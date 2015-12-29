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

package com.jeta.forms.components.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A view that allows the user to select a color that is either constant or
 * based on the current look and feel.
 * 
 * This class uses an internal component (JETAColorWell) that is not defined in
 * the forms runtime. Reflection is used to work with this component to allow
 * the forms runtime to compile.
 * 
 * @author Jeff Tassin
 */
public class ColorSelector extends JComponent {
	/**
	 * The current color property
	 */
	private ColorProperty m_color_prop = new ColorProperty();

	/**
	 * A list of ActionListeners that get notified when the color property
	 * changes.
	 */
	private LinkedList m_listeners;

	private JTextField m_key_field;
	private Component m_color_well;
	private JButton m_definition_btn;

	private LinkedHashMap m_prepended_colors;

	private ColorSelectorController m_controller;

	/**
	 * ctor
	 */
	public ColorSelector() {
		try {
			FormLayout layout = new FormLayout("center:pref,2dlu,pref,70dlu,24px,10px", "fill:pref");
			setLayout(layout);

			CellConstraints cc = new CellConstraints();
			Class color_well_class = Class.forName("com.jeta.forms.components.colors.JETAColorWell");
			m_color_well = (Component) color_well_class.newInstance();
			m_color_well.setName(ColorSelectorNames.ID_COLOR_INKWELL);
			add(m_color_well, cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.CENTER));

			add(new JLabel("L&F"), cc.xy(3, 1));

			m_key_field = new JTextField();
			m_key_field.setEnabled(false);
			m_key_field.setName(ColorSelectorNames.ID_COLOR_FIELD);
			m_key_field.setText(ColorProperty.CONSTANT_COLOR);
			add(m_key_field, cc.xy(4, 1));

			JButton btn = new JButton();
			m_definition_btn = btn;
			try {
				btn.setIcon(new ImageIcon(ColorSelector.class.getClassLoader().getResource("forms/16x16/palette.png")));
				btn.setPreferredSize(new Dimension(24, 10));
			} catch (Exception e) {
				btn.setText("...");
			}
			add(btn, cc.xy(5, 1));
			m_controller = new ColorSelectorController();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds an listener that wants to be notified when the color is changed.
	 */
	public void addActionListener(ActionListener listener) {
		if (m_listeners == null)
			m_listeners = new LinkedList();

		m_listeners.add(listener);
	}

	/**
	 * @return the color property
	 */
	public ColorProperty getColorProperty() {
		try {
			ColorProperty cprop = new ColorProperty();
			cprop.setValue(m_color_prop);
			cprop.setConstantColor(ColorSelectorUtils.getColor(m_color_well));
			cprop.setColorKey(getSelectedColorKey());
			return cprop;
		} catch (Exception e) {
			e.printStackTrace();
			return m_color_prop;
		}
	}

	public String getSelectedColorKey() {
		return m_key_field.getText();
	}

	/**
	 * Notifies all listeners that the ColorProperty has changed
	 */
	public void notifyListeners() {
		if (m_listeners != null) {
			ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getName());
			Iterator iter = m_listeners.iterator();
			while (iter.hasNext()) {
				ActionListener listener = (ActionListener) iter.next();
				listener.actionPerformed(evt);
			}
		}
	}

	/**
	 * Adds a color to the view that is not in the color manager
	 */
	public void prependColor(String key, Color c) {
		if (key == null)
			return;

		if (m_prepended_colors == null)
			m_prepended_colors = new LinkedHashMap();

		m_prepended_colors.put(key, c);
	}

	/**
	 * Removes a registered action listener that wants to be notified when the
	 * color is changed.
	 */
	public void removeActionListener(ActionListener listener) {
		if (m_listeners != null)
			m_listeners.remove(listener);
	}

	/**
	 * Sets the color property
	 */
	public void setColorProperty(ColorProperty cprop) {
		try {
			/** silence the controller so we don't get combo box events */
			m_color_prop.setValue(cprop);
			String color_name = cprop.getColorKey();
			setSelectedColor(color_name);
			ColorSelectorUtils.setColor(m_color_well, cprop.getColor());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setSelectedColor(String cname) {
		if (cname == null) {
			m_key_field.setText(ColorProperty.CONSTANT_COLOR);
		}
		else {
			m_key_field.setText(cname);
		}

	}

	/**
	 * Controller for this view
	 */
	private class ColorSelectorController {
		private boolean m_silent = false;

		public ColorSelectorController() {
			ColorSelectorUtils.addActionListener(m_color_well, new ColorWellClicked());
			m_definition_btn.addActionListener(new DefinitionListener());
		}

		public void setSilent(boolean silent) {
			m_silent = silent;
		}

		public class ColorWellClicked implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				boolean silent = m_silent;
				if (!silent) {
					setSilent(true);
					m_key_field.setText(ColorProperty.CONSTANT_COLOR);
					m_color_prop.setBrightness(0);
					m_color_prop.setBrightnessFactor(ColorProperty.DEFAULT_FACTOR);
					setSilent(silent);
					notifyListeners();
				}
			}
		}

		public class DefinitionListener implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				ColorDefinitionView view = new ColorDefinitionView();
				if (m_prepended_colors != null) {
					Iterator iter = m_prepended_colors.keySet().iterator();
					while (iter.hasNext()) {
						String cname = (String) iter.next();
						Color c = (Color) m_prepended_colors.get(cname);
						view.prependColor(cname, c);
					}
				}

				view.setColorProperty(getColorProperty());
				JETADialog dlg = JETAToolbox.invokeDialog(view, ColorSelector.this, I18N.getLocalizedMessage("Color Definition"));
				if (dlg.isOk()) {
					setColorProperty(view.getColorProperty());
					notifyListeners();
				}
			}
		}

	}

}
