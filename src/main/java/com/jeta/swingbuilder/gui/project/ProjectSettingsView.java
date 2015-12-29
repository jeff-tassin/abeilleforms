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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.beanmgr.BeanLoader;
import com.jeta.swingbuilder.gui.beanmgr.BeansModel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.store.ImportedBeanInfo;
import com.jeta.swingbuilder.store.ProjectLevelImportedBeansModel;
import com.jeta.swingbuilder.store.ProjectModel;

/**
 * Displays the view for editing the current project settings
 * 
 * @author Jeff Tassin
 */
public class ProjectSettingsView extends JETAPanel {
	/**
	 * The projectSettings.jfrm form
	 */
	private FormPanel m_view;

	/**
	 * The list model for the source path
	 */
	private DefaultListModel m_list_model = new DefaultListModel();

	/**
	 * Set when we are editing an existing project
	 */
	private ProjectModel m_existing_model;

	/**
	 * The beans model.
	 */
	private BeansModel m_beansmodel;

	/**
	 * The list model for the classpaths
	 */
	private DefaultListModel m_paths_model;

	/**
	 * Resonsible for loading beans from a classpath of URLs
	 */
	private BeanLoader m_bean_loader;

	/**
	 * ctor
	 */
	public ProjectSettingsView() {
		initialize(null);
	}

	/**
	 * ctor
	 */
	public ProjectSettingsView(ProjectModel pmodel) {
		m_existing_model = pmodel;
		initialize(pmodel);
	}

	public void addPath(String path) {
		File rootDir = getProjectRootDir();
		if (rootDir != null) {
			path = PathParser.getRelativePath(rootDir, new File(path));
			if (!m_list_model.contains(path)) {
				m_list_model.addElement(path);
			}
		}
	}

	/**
	 * Deletes the selected path from the view
	 */
	public void deleteSelectedPath() {
		JList list = m_view.getList(ProjectSettingsNames.ID_PROJECT_DIRECTORY_LIST);
		int index = list.getSelectedIndex();
		if (index >= 0) {
			m_list_model.removeElementAt(index);
		}
	}

	/**
	 * Adds a user to the list model
	 */
	public void addBeanPath(File file, int mode) {
		File rootDir = getProjectRootDir();
		if (rootDir != null) {
			File newFile = new File(file.getAbsolutePath());
			String relFilePath = (mode == javax.swing.JFileChooser.DIRECTORIES_ONLY ? PathParser.getRelativePath(rootDir, newFile) : PathParser
					.getRelativeFile(rootDir, newFile));
			if (!m_paths_model.contains(relFilePath)) {
				try {
					m_paths_model.addElement(relFilePath);
					m_bean_loader = null;
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Deletes the bean from the view/model
	 */
	public void deleteSelectedBean() {
		JTable table = m_view.getTable(ProjectSettingsNames.ID_BEAN_TABLE);
		int row = table.getSelectedRow();
		m_beansmodel.removeRow(row);
		table.repaint();
		m_view.repaint();
	}

	/**
	 * Deletes the selected URL from the view/model
	 */
	public void deleteSelectedUrl() {
		JList list = m_view.getList(ProjectSettingsNames.ID_CLASSPATH_LIST);
		int index = list.getSelectedIndex();
		if (index >= 0) {
			m_paths_model.removeElementAt(index);
		}
		m_bean_loader = null;
	}

	/**
	 * @return the bean loader which is responsible for creating beans using the
	 *         given classpaths
	 */
	BeanLoader getBeanLoader() {
		if (m_bean_loader == null) {
			m_bean_loader = new BeanLoader();
			for (int index = 0; index < m_paths_model.size(); index++) {
				String relClasspath = (String) m_paths_model.elementAt(index);
				try {
					File file = new File(getProjectRootDir(), relClasspath);
					m_bean_loader.addUrl(file.toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return m_bean_loader;
	}

	/**
	 * @return the underlying beans model
	 */
	BeansModel getBeansModel() {
		return m_beansmodel;
	}

	/**
	 * @return the selected bean
	 */
	public ImportedBeanInfo getSelectedBean() {
		JTable table = m_view.getTable(ProjectSettingsNames.ID_BEAN_TABLE);
		int row = table.getSelectedRow();
		return m_beansmodel.getRow(row);
	}

	/**
	 * @return the model that describes the imported beans and their classpaths
	 */
	public ProjectLevelImportedBeansModel getProjectLevelImportedBeansModel() {
		ProjectLevelImportedBeansModel plibm = new ProjectLevelImportedBeansModel((String) getSelectedItem(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE));
		if (m_beansmodel != null) {
			for (int row = 0; row < m_beansmodel.getRowCount(); row++) {
				plibm.addImportedBean(m_beansmodel.getRow(row));
			}
		}
		if (m_paths_model != null) {
			for (int index = 0; index < m_paths_model.size(); index++) {
				plibm.addRelativeClasspath((String) m_paths_model.elementAt(index));
			}
		}
		return plibm;
	}

	/**
	 * @return a project model defined by the information in this view.
	 */
	public ProjectModel getModel() {
		ProjectModel pmodel = new ProjectModel();
		Collection paths = getPaths();
		Iterator iter = paths.iterator();
		while (iter.hasNext()) {
			pmodel.addSourcePath((String) iter.next());
		}

		pmodel.setShared(m_view.isSelected(ProjectSettingsNames.ID_PROJECT_FILE_SHARED));

		if (m_existing_model == null) {
			pmodel.setProjectPath(getProjectFile());
		}
		else {
			pmodel.setProjectPath(m_existing_model.getProjectPath());
		}

		pmodel.setProjectEnvVariable((String) m_view.getSelectedItem(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE));

		pmodel.setProjectLevelImportedBeans(getProjectLevelImportedBeansModel());

		pmodel.setClassPath(m_view.getText(ProjectSettingsNames.ID_PROJECT_CLASSPATH));
		return pmodel;

	}

	public File getProjectRootDir() {
		String tempPath = null;

		if (isSelected(ProjectSettingsNames.ID_PROJECT_FILE_SHARED)) {
			String envVar = (String) getSelectedItem(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE);
			if (envVar != null) {
				String directory = System.getenv(envVar);
				try {
					File file = new File(directory);
					tempPath = file.getAbsolutePath();
				} catch (Exception e) {
				}
			}
		}
		else {
			if (m_existing_model == null) {
				tempPath = getProjectFile();
			}
			else {
				tempPath = m_existing_model.getProjectPath();
			}
			File file = new File(tempPath);
			if (!file.isDirectory()) {
				tempPath = file.getParent();
			}
		}

		File rootDir = null;

		if ((tempPath != null) && (!"".equals(tempPath))) {
			String path = FormDesignerUtils.fastTrim(tempPath);
			char c = File.separatorChar;
			if (c == '\\')
				path = path.replace('/', File.separatorChar);
			else
				path = path.replace('\\', File.separatorChar);

			try {
				rootDir = new File(path);
			} catch (Exception e) {
				rootDir = null;
			}
		}

		return rootDir;
	}

	public String getProjectFile() {
		String path = FormDesignerUtils.fastTrim(getText(ProjectSettingsNames.ID_PROJECT_FILE_PATH));
		char c = File.separatorChar;
		if (c == '\\')
			path = path.replace('/', File.separatorChar);
		else
			path = path.replace('\\', File.separatorChar);

		return path;
	}

	/**
	 * @return a collection of source paths (String objects) in this view
	 */
	public Collection getPaths() {
		LinkedList list = new LinkedList();
		for (int index = 0; index < m_list_model.size(); index++) {
			list.add(m_list_model.elementAt(index));
		}
		return list;
	}

	public void initialize(ProjectModel pmodel) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/project/projectSettingsMain.jfrm");

		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		m_view.getTabbedPane(ProjectSettingsNames.ID_PROJECT_SETTINGS_TAB).setEnabledAt(1, (pmodel != null && (pmodel.isShared()) ? true : false));

		// Populate the JComboBox with Environment Variables...
		m_view.getComboBox(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE).setModel(new DefaultComboBoxModel(FormDesignerUtils.getEnvVars(false)));

		m_view.getButton(ProjectSettingsNames.ID_PROJECT_FILE_BTN).setPreferredSize(new Dimension(24, 10));
		m_view.getButton(ProjectSettingsNames.ID_PROJECT_CLASSPATH_BTN).setPreferredSize(new Dimension(24, 10));

		JList list = m_view.getList(ProjectSettingsNames.ID_PROJECT_DIRECTORY_LIST);
		list.setModel(m_list_model);

		JTable table = m_view.getTable(ProjectSettingsNames.ID_BEAN_TABLE);
		m_beansmodel = new BeansModel();
		table.setModel(m_beansmodel);

		m_paths_model = new DefaultListModel();
		JList classpathList = m_view.getList(ProjectSettingsNames.ID_CLASSPATH_LIST);
		classpathList.setModel(m_paths_model);

		if ((pmodel != null) && pmodel.isShared()) {
			Iterator iterator = pmodel.getProjectLevelImportedBeansModel().getImportedBeans().iterator();
			while (iterator.hasNext()) {
				ImportedBeanInfo beanInfo = (ImportedBeanInfo) ((ImportedBeanInfo) iterator.next()).clone();
				m_beansmodel.addRow(beanInfo);
			}

			int col_width = 60;
			TableColumnModel cmodel = table.getColumnModel();
			cmodel.getColumn(BeansModel.ICON_COLUMN).setPreferredWidth(col_width);
			cmodel.getColumn(BeansModel.NAME_COLUMN).setPreferredWidth(col_width * 5);
			cmodel.getColumn(BeansModel.SCROLLABLE_COLUMN).setPreferredWidth(col_width);

			iterator = pmodel.getProjectLevelImportedBeansModel().getRelativeClasspaths().iterator();
			while (iterator.hasNext()) {
				m_paths_model.addElement((String) iterator.next());
			}
		}

		setController(new ProjectSettingsController(this));

		m_view.enableComponent(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE, false);
		m_view.enableComponent(ProjectSettingsNames.ID_PROJECT_ENV_VAR_REFRESH_BTN, false);

		if (pmodel != null) {
			m_view.enableComponent(ProjectSettingsNames.ID_PROJECT_FILE_SHARED, false);
			m_view.enableComponent(ProjectSettingsNames.ID_PROJECT_FILE_PATH, false);
			m_view.enableComponent(ProjectSettingsNames.ID_PROJECT_FILE_BTN, false);

			m_view.setSelected(ProjectSettingsNames.ID_PROJECT_FILE_SHARED, pmodel.isShared());
			m_view.getTabbedPane(ProjectSettingsNames.ID_PROJECT_SETTINGS_TAB).setEnabledAt(1, pmodel.isShared());

			m_view.setSelectedItem(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE, pmodel.getProjectEnvVariable());

			m_view.setText(ProjectSettingsNames.ID_PROJECT_FILE_PATH, pmodel.getProjectPath());
			Collection paths = pmodel.getSourcePaths();
			Iterator iter = paths.iterator();
			while (iter.hasNext()) {
				m_list_model.addElement(iter.next());
			}
			m_view.setText(ProjectSettingsNames.ID_PROJECT_CLASSPATH, pmodel.getClassPath());
		}
		else {
			m_view.setSelectedItem(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE, null);
		}
	}
}
