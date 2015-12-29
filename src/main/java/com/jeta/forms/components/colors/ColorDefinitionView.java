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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jeta.forms.colormgr.ColorManager;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.registry.JETARegistry;

/**
 * This is an internal component used by the designer and is not meant to be
 * used independently of the designer.
 * 
 * A component that allows the user to select a color that is either constant or
 * based on the current look and feel.
 * 
 * @author Jeff Tassin
 */
public class ColorDefinitionView extends JETAPanel {
	private FormPanel m_view;

	/**
	 * The current color property
	 */
	private ColorProperty m_color_prop = new ColorProperty();

	private ColorDefinitionController m_controller;

	private JComboBox m_key_combo;

	/**
	 * A JETAColorWell object. This class is not defined in the forms runtime.
	 * It is only used internally by the designer.
	 */
	private Component m_color_well;
	private JSpinner m_factor_spinner;
	private JSpinner m_bright_spinner;

	/**
	 * A cache of ColorCellValue objects
	 */
	private static LinkedList m_color_cell_values;
	/**
	 * The current look and feel
	 */
	private static LookAndFeel m_look_and_feel;

	/**
	 * ctor
	 */
	public ColorDefinitionView() {
		try {
			m_view = new FormPanel("com/jeta/forms/components/colors/colorSelection.jfrm");
			setLayout(new BorderLayout());
			add(m_view, BorderLayout.CENTER);

			m_color_well = m_view.getComponentByName(ColorSelectorNames.ID_COLOR_INKWELL);
			m_key_combo = m_view.getComboBox(ColorSelectorNames.ID_COLOR_NAME_COMBO);

			m_key_combo.setRenderer(new ColorCellRenderer());
			m_key_combo.setName(ColorSelectorNames.ID_COLOR_NAME_COMBO);
			m_key_combo.removeAllItems();

			Collection cvalues = getColorCellValues();
			Iterator iter = cvalues.iterator();
			while (iter.hasNext()) {
				m_key_combo.addItem(iter.next());
			}

			m_factor_spinner = m_view.getSpinner(ColorSelectorNames.ID_BRIGHTNESS_FACTOR);
			SpinnerNumberModel model = new SpinnerNumberModel(0.7, 0.05, 1.0, 0.05);
			m_factor_spinner.setModel(model);

			m_bright_spinner = m_view.getSpinner(ColorSelectorNames.ID_BRIGHTNESS_VALUE);
			model = new SpinnerNumberModel(0, -10, 10, 1);
			m_bright_spinner.setModel(model);

			m_controller = new ColorDefinitionController();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the color property
	 */
	public ColorProperty getColorProperty() {
		try {
			ColorProperty cprop = new ColorProperty();
			cprop.setConstantColor(ColorSelectorUtils.getColor(m_color_well));
			cprop.setColorKey(getSelectedColorKey());

			Double bfactor = (Double) m_factor_spinner.getValue();
			cprop.setBrightnessFactor(bfactor.floatValue());

			Integer b = (Integer) m_bright_spinner.getValue();
			cprop.setBrightness(b.intValue());
			return cprop;
		} catch (Exception e) {
			e.printStackTrace();
			return m_color_prop;
		}
	}

	private static Collection getColorCellValues() {
		LookAndFeel lf = UIManager.getLookAndFeel();
		if (m_look_and_feel != lf || m_color_cell_values == null) {
			if (m_color_cell_values == null)
				m_color_cell_values = new LinkedList();
			m_color_cell_values.clear();

			m_look_and_feel = lf;

			ColorManager cmgr = (ColorManager) JETARegistry.lookup(ColorManager.COMPONENT_ID);
			Collection cnames = cmgr.getColorKeys();
			Iterator iter = cnames.iterator();
			while (iter.hasNext()) {
				String cname = (String) iter.next();
				m_color_cell_values.add(new ColorCellValue(cname, cmgr.getColor(cname, null)));
			}
		}
		return m_color_cell_values;
	}

	public String getSelectedColorKey() {
		ColorCellValue ccv = (ColorCellValue) m_key_combo.getSelectedItem();
		return ccv.getName();
	}

	/**
	 * Adds a color to the view that is not in the color manager
	 */
	public void prependColor(String key, Color c) {
		if (key == null)
			return;

		/** insert after the 'constant' entry */
		m_key_combo.insertItemAt(new ColorCellValue(key, c), 1);
	}

	/**
	 * Sets the color property
	 */
	public void setColorProperty(ColorProperty cprop) {
		try {
			/** silence the controller so we don't get combo box events */
			m_controller.setSilent(true);
			m_color_prop.setValue(cprop);
			String color_name = cprop.getColorKey();
			setSelectedColor(color_name);
			ColorSelectorUtils.setColor(m_color_well, cprop.getColor());

			m_factor_spinner.setValue(new Double(cprop.getBrightnessFactor()));
			m_bright_spinner.setValue(new Integer(cprop.getBrightness()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			m_controller.setSilent(false);
		}
	}

	public void setSelectedColor(String cname) {
		javax.swing.ComboBoxModel cmodel = m_key_combo.getModel();
		for (int index = 0; index < cmodel.getSize(); index++) {
			if (cmodel.getElementAt(index).equals(cname)) {
				m_key_combo.setSelectedIndex(index);
				return;
			}
		}
		m_key_combo.setSelectedIndex(0);
	}

	/**
	 * Controller for this view
	 */
	private class ColorDefinitionController {
		private boolean m_silent = false;

		public ColorDefinitionController() {
			m_key_combo.addActionListener(new ComboChangedAction());
			ColorSelectorUtils.addActionListener(m_color_well, new ColorWellClicked());
			m_factor_spinner.addChangeListener(new ColorChangedAction());
			m_bright_spinner.addChangeListener(new ColorChangedAction());
		}

		public void setSilent(boolean silent) {
			m_silent = silent;
		}

		public class ComboChangedAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				boolean silent = m_silent;
				if (!silent) {
					setSilent(true);

					m_color_prop.setColorKey(getSelectedColorKey());
					if (!m_color_prop.isConstant()) {
						ColorSelectorUtils.setColor(m_color_well, m_color_prop.getColor());
					}
					setSilent(silent);
				}
			}
		}

		public class ColorChangedAction implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				//

			}
		}

		public class ColorWellClicked implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				boolean silent = m_silent;
				if (!silent) {
					setSilent(true);
					m_key_combo.setSelectedIndex(0);
					setSilent(silent);
				}
			}
		}
	}

	/**
	 * Used so we don't have to instantiate in Integer key everytime we want to
	 * lookup a ColorCellValue
	 */
	public static class ColorLookup {
		int key;

		public int hashCode() {
			return key;
		}
	}

	public static class ColorCellValue {
		String m_color_name;
		Color m_color;
		ColorLookup m_color_key = new ColorLookup();
		private static HashMap m_color_icons = new HashMap();

		/**
		 * ctor
		 */
		public ColorCellValue(String cname, Color color) {
			m_color_name = cname;
			m_color = color;
			if (m_color_name == null)
				m_color_name = "constant";
		}

		public boolean equals(Object obj) {
			return m_color_name.equals(obj.toString());
		}

		public String getName() {
			return m_color_name;
		}

		public Icon getIcon() {
			if (m_color == null)
				return null;

			m_color_key.key = m_color.getRGB();
			Icon icon = (Icon) m_color_icons.get(m_color_key);
			if (icon == null) {
				int width = 12;
				int height = 12;
				BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D bg = bimage.createGraphics();

				bg.setColor(m_color);
				bg.fillRect(0, 0, width, height);
				bg.setColor(Color.black);
				bg.drawRect(0, 0, width - 1, height - 1);

				bg.dispose();
				icon = new ImageIcon(bimage);
				m_color_icons.put(new Integer(m_color.getRGB()), icon);
			}
			return icon;
		}

		public String toString() {
			return m_color_name;
		}
	}

	/**
	 * Line Renderer
	 */
	public static class ColorCellRenderer extends JLabel implements ListCellRenderer {

		public ColorCellRenderer() {
			// must set or the background color won't show
			setOpaque(true);
			setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 1, 0));
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			ColorCellValue ccv = (ColorCellValue) value;
			if (ccv == null) {
				setIcon(null);
				setText("");
			}
			else {
				setIcon(ccv.getIcon());
				setText(ccv.getName());
			}

			if (isSelected) {
				setBackground(UIManager.getColor("ComboBox.selectionBackground"));
				setForeground(UIManager.getColor("ComboBox.selectionForeground"));
			}
			else {
				setBackground(UIManager.getColor("ComboBox.background"));
				setForeground(UIManager.getColor("ComboBox.foreground"));
			}
			return this;
		}
	}

}
