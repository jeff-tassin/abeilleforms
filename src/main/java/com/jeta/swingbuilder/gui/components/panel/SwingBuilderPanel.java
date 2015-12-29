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

package com.jeta.swingbuilder.gui.components.panel;

import java.awt.Color;
import java.awt.Component;

import com.jeta.forms.components.colors.ColorSelector;
import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.ColorProperty;

/**
 * An extension of FormPanel to provde some common features needed in the swing
 * builder app.
 * 
 * @author Jeff Tassin
 */
public class SwingBuilderPanel extends FormPanel {
	/**
	 * ctor
	 * 
	 * @param formPath
	 *            the path to the form file. This path can be absolute or
	 *            relative to the classpath.
	 */
	public SwingBuilderPanel(String formPath) {
		super(formPath);
	}

	/**
	 * @return the ColorProperty for the JETAColorWell with the given name. Null
	 *         is returned if a JETAColorWell cannot be found.
	 */
	public Color getColor(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JETAColorWell) {
			return ((JETAColorWell) comp).getColor();
		}
		else
			return null;
	}

	/**
	 * @return the ColorProperty for the ColorSelector with the given name. Null
	 *         is returned if a ColorSelector cannot be found.
	 */
	public ColorProperty getColorProperty(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof ColorSelector) {
			return ((ColorSelector) comp).getColorProperty();
		}
		else
			return null;
	}

	/**
	 * Sets the Color for the JETAColorWell with the given name. The call is
	 * ignored if a JETAColorWell cannot be found.
	 */
	public void setColorProperty(String compName, ColorProperty cprop) {
		Component comp = getComponentByName(compName);
		if (comp instanceof ColorSelector) {
			((ColorSelector) comp).setColorProperty(cprop);
		}
	}

	/**
	 * Sets the ColorProperty for the ColorSelector with the given name. The
	 * call is ignored if a ColorSelector cannot be found.
	 */
	public void setColor(String compName, Color cprop) {
		Component comp = getComponentByName(compName);
		if (comp instanceof JETAColorWell) {
			((JETAColorWell) comp).setColor(cprop);
		}
	}

}
