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

package com.jeta.swingbuilder.gui.colorchooser;

import java.awt.Color;
import java.awt.Component;

import com.jeta.forms.components.colors.ColorChooserFactory;

/**
 * This interface defines a method for invoking a color chooser other than the
 * standard JColorChooser. Simply provide an implementation and put an instance
 * in the JETARegistry. The JETAColorWell uses this factory.
 * 
 * @author Jeff Tassin
 */
public class DefaultColorChooserFactory implements ColorChooserFactory {
	/**
	 * Invokes a color chooser dialog.
	 * 
	 * @param invoker
	 *            the invoker of the dialog. Used to determine the parent window
	 * @param title
	 *            the title for the dialog
	 * @param selectedColor
	 *            the initial color to select in the dialog.
	 * @return the color selected by the user when the dialog is closed. If the
	 *         user cancels the dialog, null is returned.
	 */
	public Color showColorChooser(Component invoker, String title, Color selectedColor) {
		return JETAColorChooser.invokeColorChooser(invoker, selectedColor);
	}
}
