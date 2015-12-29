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

import com.jeta.swingbuilder.gui.beanmgr.BeanManagerNames;

/**
 * Names for the ProjectSettingsView
 * 
 * @author Jeff Tassin
 */
public class ProjectSettingsNames extends BeanManagerNames {
	public static final String ID_PROJECT_SETTINGS_TAB = "project.settings.tab";
	public static final String ID_PROJECT_DIRECTORY_LIST = "directory.list"; // javax.swing.JList
	public static final String ID_PROJECT_ADD_PATH = "add.project.path"; // javax.swing.JButton
	public static final String ID_PROJECT_DELETE_PATH = "delete.project.path"; // javax.swing.JButton
	public static final String ID_PROJECT_FILE_SHARED = "project.file.shared"; // javax.swing.JCheckBox
	public static final String ID_PROJECT_ROOT_ENV_VARIABLE = "project.root.env.variable"; // javax.swing.JComboBox
	public static final String ID_PROJECT_ENV_VAR_REFRESH_BTN = "project.env.var.refresh.btn";
	public static final String ID_PROJECT_FILE_PATH = "project.file.path"; // javax.swing.JTextField
	public static final String ID_PROJECT_FILE_BTN = "project.file.btn"; // javax.swing.JButton
	public static final String ID_PROJECT_CLASSPATH = "project.classpath"; // javax.swing.JTextField
	public static final String ID_PROJECT_CLASSPATH_BTN = "project.classpath.btn"; // javax.swing.JButton
	public static final String ID_PROJECT_FILES_TAB = "files.tab"; // javax.swing.JTabbedPane
}
