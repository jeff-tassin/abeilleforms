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

package com.jeta.swingbuilder.gui.images;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.jeta.forms.project.ProjectManager;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;

/**
 * Handles events for the ImagePropertiesView
 * 
 * @author Jeff Tassin
 */
public class ImagePropertiesController extends JETAController {
	/**
	 * The view
	 */
	private ImagePropertiesView m_view;

	/**
	 * ctor
	 */
	public ImagePropertiesController(ImagePropertiesView view) {
		super(view);
		m_view = view;
		assignAction(ImagePropertiesNames.ID_FILE_BUTTON, new FileAction());
	}

	/**
	 * Action for loading an image from a file.
	 */
	public class FileAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FileChooserConfig fcc = new FileChooserConfig(".img", new TSFileFilter("gif,png,jpg,jpeg", "Image Files(*.gif,*.png,*.jpg)"));
			fcc.setParentComponent(m_view);
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
						JOptionPane.showMessageDialog(m_view, msg, title, JOptionPane.ERROR_MESSAGE);
						return;
					}

					/**
					 * The toolkit should be used to create the image, otherwise
					 * it may be pulled from the toolkit image cache.
					 */
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					ImageIcon image = new ImageIcon(toolkit.createImage(f.getPath()));
					m_view.setImage(image);
					m_view.setText(ImagePropertiesNames.ID_FILE_FIELD, pmgr.getRelativePath(f.getPath()));
					m_view.setText(ImagePropertiesNames.ID_DESCRIPTION_FIELD, f.getName());
				} catch (Exception e) {
					String msg = I18N.getLocalizedMessage("Unable to load image.");
					String title = I18N.getLocalizedMessage("Error");
					JOptionPane.showMessageDialog(m_view, msg, title, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
