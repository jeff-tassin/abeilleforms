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

import java.awt.Component;
import java.io.File;

import javax.swing.JOptionPane;

import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.properties.IconProperty;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;

public class ImageUtils {

	public static boolean chooseImageFile(Component parentComp, IconProperty iprop) {
		if (iprop == null)
			return false;

		boolean bresult = false;
		String relativepath = null;
		if (iprop != null)
			relativepath = iprop.getRelativePath();

		ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
		String abspath = null;
		if (pmgr.isValidResource(relativepath))
			abspath = pmgr.getAbsolutePath(relativepath);
		FileChooserConfig fcc = new FileChooserConfig(abspath, ".img", new TSFileFilter("gif,png,jpg,jpeg", "Image Files(*.gif,*.png,*.jpg)"));
		fcc.setParentComponent(parentComp);

		File f = TSFileChooserFactory.showOpenDialog(fcc);
		if (f != null) {
			/** check if the path is contained in a valid package for the project */
			if (pmgr.isValidAbsolutePath(f.getPath())) {
				iprop.setRelativePath(pmgr.getRelativePath(f.getPath()));
				iprop.setDescription(f.getName());
				bresult = true;
			}
			else {
				String msg = I18N.getLocalizedMessage("Selected image is not in source path.");
				String title = I18N.getLocalizedMessage("Error");
				JOptionPane.showMessageDialog(FormManagerDesignUtils.getApplicationFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
			}

		}
		return bresult;
	}

}
