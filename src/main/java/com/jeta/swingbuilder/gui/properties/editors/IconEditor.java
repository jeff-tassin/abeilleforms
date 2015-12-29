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
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.properties.IconProperty;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;

/**
 * Editor/renderer for handling images and icons
 * 
 * @author Jeff Tassin
 */
public class IconEditor extends JETAPropertyEditor {
	/**
	 * Used to render the value of our border
	 */
	private ValuePainter m_value_painter;

	private static ImageIcon[] m_icon = { (ImageIcon) FormDesignerUtils.loadImage(Icons.PORTRAIT_16) };

	/**
	 * ctor
	 */
	public IconEditor() {
		m_value_painter = new ValuePainter();
		m_value_painter.setPreImages(m_icon);
	}

	/**
	 * Invokes a dialog used to update the property
	 */
	public void invokePropertyDialog(Component comp) {
		FileChooserConfig fcc = new FileChooserConfig(".img", new TSFileFilter("gif,png,jpg,jpeg", "Image Files(*.gif,*.png,*.jpg)"));
		fcc.setParentComponent(SwingUtilities.getWindowAncestor(comp));
		File f = TSFileChooserFactory.showOpenDialog(fcc);
		if (f != null) {
			try {

				ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
				/**
				 * check if the path is contained in a valid package for the
				 * project
				 */
				// @todo fix to allow embedded images from anywhere
				if (!pmgr.isValidAbsolutePath(f.getPath())) {
					String msg = I18N.getLocalizedMessage("Selected image is not in source path.");
					String title = I18N.getLocalizedMessage("Error");
					JOptionPane.showMessageDialog(comp, msg, title, JOptionPane.ERROR_MESSAGE);
					return;
				}

				String relativepath = pmgr.getRelativePath(f.getPath());
				IconProperty iprop = new IconProperty();
				iprop.setValue((IconProperty) getValue());
				iprop.setRelativePath(relativepath);
				setValue(iprop);
			} catch (Exception e) {
				String msg = I18N.getLocalizedMessage("Unable to load image.");
				String title = I18N.getLocalizedMessage("Error");
				JOptionPane.showMessageDialog(comp, msg, title, JOptionPane.ERROR_MESSAGE);
			}
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
	 * 
	 */
	public void setValue(Object value) {
		if (value instanceof IconProperty) {
			super.setValue(value);
			IconProperty iprop = (IconProperty) value;
			m_value_painter.setValue(iprop.getDescription());
		}
		else if (value == null) {
			super.setValue(value);
			m_value_painter.setValue(null);
		}
		else {
			assert (false);
		}
	}

	/**
	 * @return true since we have a custom editor dialog for this type
	 */
	public boolean supportsCustomEditor() {
		return true;
	}
}
