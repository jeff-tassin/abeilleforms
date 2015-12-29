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

import com.jeta.forms.project.ProjectManager;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * Validator for the ImagePropertiesView
 * 
 * @author Jeff Tassin
 */
public class ImagePropertiesValidator implements JETARule {
	/**
	 * Validates the ImagePropertiesView.
	 * 
	 * @param params
	 *            a 1 element array that contains an ImagePropertiesView object.
	 */
	public RuleResult check(Object[] params) {
		ImagePropertiesView view = (ImagePropertiesView) params[0];
		String path = FormDesignerUtils.fastTrim(view.getRelativePath());
		if (path.length() == 0) {
			// no image selected
			return RuleResult.SUCCESS;
		}

		ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
		/** check if the path is contained in a valid package for the project */
		// @todo fix to allow embedded images from anywhere
		if (!pmgr.isValidResource(path)) {
			return new RuleResult(I18N.getLocalizedMessage("Selected image is not in source path"));
		}
		return RuleResult.SUCCESS;
	}
}
