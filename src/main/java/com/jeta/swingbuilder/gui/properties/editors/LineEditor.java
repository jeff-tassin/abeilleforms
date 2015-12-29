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

package com.jeta.swingbuilder.gui.properties.editors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.jeta.forms.store.properties.CompoundLineProperty;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.line.CompoundLineValidator;
import com.jeta.swingbuilder.gui.components.line.CompoundLineView;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

public class LineEditor extends JETAPropertyEditor {
	/**
	 * Used to render the value of our border
	 */
	private ValuePainter m_value_painter;

	/**
	 * ctor
	 */
	public LineEditor() {
		m_value_painter = new ValuePainter(I18N.getLocalizedMessage("line definition"));
		m_value_painter.setPreImages((javax.swing.ImageIcon) FormDesignerUtils.loadImage("forms/line_property.gif"));
	}

	/**
	 * Invokes a dialog used to update the property
	 */
	public void invokePropertyDialog(Component comp) {
		CompoundLineView view = new CompoundLineView((CompoundLineProperty) getValue());
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, comp, true);
		dlg.addValidator(view, new CompoundLineValidator());
		dlg.setPrimaryPanel(view);
		dlg.setSize(dlg.getPreferredSize());
		dlg.setTitle(I18N.getLocalizedMessage("Edit Line"));
		dlg.showCenter();
		if (dlg.isOk()) {
			setValue(view.createLineProperty());
		}
	}

	/**
	 * Determines whether this class renders itself using the
	 * paintValue(Graphics g, Rectangle rect) method. Generally, editors that
	 * are not JComponents are paintable.
	 */
	public boolean isPaintable() {
		return true;
	}

	/**
	 * Method that renders the text on the given graphics context
	 */
	public void paintValue(Graphics g, Rectangle rect) {
		// forward the call to the value painter
		m_value_painter.paintValue(g, rect);
	}

	/**
	 * Sets the value for this editor
	 */
	public void setValue(Object value) {
		super.setValue(value);
	}

	/**
	 * @return true since we have a custom editor dialog for this type
	 */
	public boolean supportsCustomEditor() {
		return true;
	}
}
