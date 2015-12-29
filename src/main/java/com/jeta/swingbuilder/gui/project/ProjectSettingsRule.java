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

package com.jeta.swingbuilder.gui.project;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JTabbedPane;

import com.jeta.open.i18n.I18N;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;
import com.jeta.swingbuilder.store.ProjectModel;

/**
 * Validator for the ProjectSettingsView
 * 
 * @author Jeff Tassin
 */
public class ProjectSettingsRule implements JETARule {
	/**
	 * Validates the ProjectSettingsView.
	 * 
	 * @param params
	 *            a 1 element array that contains a ProjectSettingsView object.
	 */
	public RuleResult check(Object[] params) {
		ProjectSettingsView view = (ProjectSettingsView) params[0];
		Collection paths = view.getPaths();
		if (paths.size() == 0) {
			return new RuleResult(I18N.getLocalizedMessage("One or more source paths is required"));
		}

		JTabbedPane tab = view.getTabbedPane(ProjectSettingsNames.ID_PROJECT_FILES_TAB);
		ProjectModel pmodel = view.getModel();
		String msg = validateProject(pmodel);
		if (msg != null) {
			msg = I18N.getLocalizedMessage("Invalid Paths") + "\n" + msg;
			tab.setSelectedIndex(0);
			return new RuleResult(msg);
		}

		msg = validateClassPath(pmodel);
		if (msg != null) {
			msg = I18N.getLocalizedMessage("Invalid Classes Path") + "\n" + msg;
			tab.setSelectedIndex(1);
			return new RuleResult(msg);
		}

		File rootDir = view.getProjectRootDir();
		if (rootDir == null || !rootDir.isDirectory() || !rootDir.exists()) {
			return new RuleResult(I18N.getLocalizedMessage("Invalid project path. Please verify\nthat the project directory exists."));
		}

		return RuleResult.SUCCESS;
	}

	public static String validateProject(ProjectModel pmodel) {
		StringBuffer error_buff = null;
		/** validate that all paths exist */
		File rootDir = pmodel.getProjectRootDir();

		Collection paths = pmodel.getSourcePaths();
		Iterator iter = paths.iterator();
		while (iter.hasNext()) {
			String path = (String) iter.next();
			File projpath = new File(rootDir, path);
			if (!projpath.isDirectory()) {
				projpath = new File(path);
				if (!projpath.isDirectory()) {
					if (error_buff == null)
						error_buff = new StringBuffer();
					error_buff.append(projpath);
					error_buff.append("\n");
				}
			}
		}

		// FIXME: MUST VALIDATE IMPORTED PROJECT LEVEL JAVA BEANS...

		if (error_buff != null) {
			return error_buff.toString();
		}
		else {
			return null;
		}
	}

	public static String validateClassPath(ProjectModel pmodel) {
		if (pmodel.getClassPath() == null || pmodel.getClassPath().length() == 0)
			return null;

		StringBuffer error_buff = null;
		/** validate that all paths exist */
		File rootDir = pmodel.getProjectRootDir();

		File classpath = new File(pmodel.getClassPath());
		if (!classpath.isDirectory()) {
			classpath = new File(rootDir, pmodel.getClassPath());
			if (!classpath.isDirectory()) {
				if (error_buff == null)
					error_buff = new StringBuffer();
				error_buff.append(classpath);
				error_buff.append("\n");
			}
		}

		if (error_buff != null) {
			return error_buff.toString();
		}
		else {
			return null;
		}
	}

}
