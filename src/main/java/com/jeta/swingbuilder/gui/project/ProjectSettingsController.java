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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.store.memento.IconMemento;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.beanmgr.BeanDefinitionValidator;
import com.jeta.swingbuilder.gui.beanmgr.BeanDefinitionView;
import com.jeta.swingbuilder.gui.beanmgr.BeanLoader;
import com.jeta.swingbuilder.gui.beanmgr.BeanManagerNames;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.store.ImportedBeanInfo;

/**
 * The controller for the ProjectSettingsView class
 * 
 * @author Jeff Tassin
 */
public class ProjectSettingsController extends JETAController {
	/**
	 * The view we are handling events for
	 */
	private ProjectSettingsView m_view;

	/**
	 * The beans table model
	 */
	// private BeansModel m_beansmodel;
	/**
	 * ctor
	 */
	public ProjectSettingsController(ProjectSettingsView view) {
		super(view);
		m_view = view;
		//
		// 1st tab...
		assignAction(ProjectSettingsNames.ID_PROJECT_FILE_SHARED, new ProjectSharedAction());
		assignAction(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE, new ProjectEnvVarAction());
		assignAction(ProjectSettingsNames.ID_PROJECT_ENV_VAR_REFRESH_BTN, new ProjectEnvVarsRefreshAction());
		assignAction(ProjectSettingsNames.ID_PROJECT_ADD_PATH, new AddPathAction());
		assignAction(ProjectSettingsNames.ID_PROJECT_DELETE_PATH, new DeletePathAction());
		assignAction(ProjectSettingsNames.ID_PROJECT_FILE_BTN, new ProjectFileAction());
		assignAction(ProjectSettingsNames.ID_PROJECT_CLASSPATH_BTN, new ProjectClassPathAction());
		//
		// 2nd tab...;
		assignAction(BeanManagerNames.ID_ADD_BEAN, new AddBeanAction());
		assignAction(BeanManagerNames.ID_DELETE_BEAN, new DeleteBeanAction());
		assignAction(BeanManagerNames.ID_SET_BEAN_ICON, new SetBeanIconAction());
		assignAction(BeanManagerNames.ID_ADD_JAR, new AddBeanPathAction(false));
		assignAction(BeanManagerNames.ID_ADD_PATH, new AddBeanPathAction(true));
		assignAction(BeanManagerNames.ID_DELETE_JAR, new DeleteBeanPathAction());
	}

	/**
	 * Adds a path to the project
	 */
	public class AddPathAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FileChooserConfig fcc = new FileChooserConfig(m_view.getProjectRootDir().getPath(), null, JFileChooser.DIRECTORIES_ONLY, (TSFileFilter) null);
			fcc.setParentComponent(m_view);
			File f = TSFileChooserFactory.showOpenDialog(fcc);
			if (f != null) {
				m_view.addPath(f.getPath());
			}
		}
	}

	/**
	 * Deletes a path from the project
	 */
	public class DeletePathAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.deleteSelectedPath();
		}
	}

	/**
	 * Adds a class path to the project
	 */
	public class ProjectClassPathAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			File rootDir = m_view.getProjectRootDir();
			FileChooserConfig fcc = new FileChooserConfig(rootDir.getPath(), null, JFileChooser.DIRECTORIES_ONLY, (TSFileFilter) null);
			fcc.setParentComponent(m_view);
			File f = TSFileChooserFactory.showOpenDialog(fcc);
			//
			if ((f != null) && (rootDir != null)) {
				String classpath = PathParser.getRelativePath(rootDir, f);
				m_view.setText(ProjectSettingsNames.ID_PROJECT_CLASSPATH, classpath);
			}
		}
	}

	/**
	 * Set the project as "shared"
	 */
	public class ProjectEnvVarAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String envVar = (String) ((JComboBox) evt.getSource()).getSelectedItem();
		}
	}

	/**
	 * Set the project as "shared"
	 */
	public class ProjectEnvVarsRefreshAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JComboBox comboBox = m_view.getComboBox(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE);
			comboBox.setModel(new DefaultComboBoxModel(FormDesignerUtils.getEnvVars(true)));
			comboBox.getModel().setSelectedItem(null);
		}
	}

	/**
	 * Set the project as "shared"
	 */
	public class ProjectSharedAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			boolean isShared = ((JCheckBox) evt.getSource()).isSelected();
			m_view.setSelected(ProjectSettingsNames.ID_PROJECT_FILE_SHARED, isShared);
			m_view.enableComponent(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE, isShared);
			m_view.enableComponent(ProjectSettingsNames.ID_PROJECT_ENV_VAR_REFRESH_BTN, isShared);
			m_view.getTabbedPane(ProjectSettingsNames.ID_PROJECT_SETTINGS_TAB).setEnabledAt(1, isShared);
			if (!isShared) {
				m_view.setSelectedItem(ProjectSettingsNames.ID_PROJECT_ROOT_ENV_VARIABLE, null);
			}
		}
	}

	public class ProjectFileAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			File f = TSFileChooserFactory.showSaveDialog("project", new TSFileFilter("jfpr", "Project Files (*.jfpr)"));
			if (f != null) {
				String fname = f.getName();
				if (fname != null && fname.length() > 0) {
					int pos = fname.indexOf(".");
					if (pos < 0) {
						fname = fname + ".jfpr";
					}
				}
				String path = f.getParent() + File.separatorChar + fname;
				m_view.setText(ProjectSettingsNames.ID_PROJECT_FILE_PATH, path);
			}
		}
	}

	/**
	 * Adds a bean to the view
	 */
	public class AddBeanAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			BeanDefinitionView view = new BeanDefinitionView();
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_view, true);
			dlg.setPrimaryPanel(view);
			dlg.setTitle(I18N.getLocalizedMessage("Bean Definition"));
			dlg.setSize(dlg.getPreferredSize());
			dlg.addValidator(new Object[] { view, m_view.getBeanLoader() }, new BeanDefinitionValidator());
			dlg.showCenter();
			if (dlg.isOk()) {
				BeanLoader loader = m_view.getBeanLoader();
				try {
					Component comp = loader.createBean(view.getBeanName());
					ImportedBeanInfo info = new ImportedBeanInfo(view.getBeanName(), view.isScrollable());
					m_view.getBeansModel().addRow(info);

					if (!(comp.isLightweight() || comp instanceof javax.swing.JComponent)) {
						String msg = I18N.getLocalizedMessage("Warning. Only lightweight Java beans are supported.");
						String title = I18N.getLocalizedMessage("Error");
						JOptionPane.showMessageDialog(m_view, msg, title, JOptionPane.ERROR_MESSAGE);
					}
				} catch (FormException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Adds a path to the project
	 */
	public class AddBeanPathAction implements ActionListener {
		private boolean m_dir_only = false;

		public AddBeanPathAction(boolean dir_only) {
			m_dir_only = dir_only;
		}

		public void actionPerformed(ActionEvent evt) {
			int mode = (m_dir_only ? javax.swing.JFileChooser.DIRECTORIES_ONLY : javax.swing.JFileChooser.FILES_ONLY);

			TSFileFilter filter = null;
			if (!m_dir_only)
				filter = new TSFileFilter("jar,zip", "JAR Files(*.jar,*.zip)");

			FileChooserConfig fcc = new FileChooserConfig(null, mode, filter);
			//FileChooserConfig fcc = new FileChooserConfig(m_view.getProjectRootDir().getPath(), "jar,zip", mode, filter);
			fcc.setParentComponent(m_view);
			File f = TSFileChooserFactory.showOpenDialog(fcc);
		
			if (f != null && f.exists()) {
				try {
					m_view.addBeanPath(f, mode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Deletes a bean from the view
	 */
	public class DeleteBeanAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.deleteSelectedBean();
		}
	}

	/**
	 * Deletes a path from the project
	 */
	public class DeleteBeanPathAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.deleteSelectedUrl();
		}
	}

	/**
	 * Sets an icon for the selected bean
	 */
	public class SetBeanIconAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			ImportedBeanInfo bi = (ImportedBeanInfo) m_view.getSelectedBean();
			if (bi != null) {
				File f = TSFileChooserFactory.showOpenDialog(".img", new TSFileFilter("gif,png,jpg,jpeg", "Image Files(*.gif,*.png,*.jpg,*.jpeg)"));
				if (f != null) {
					bi.setIconMemento(new IconMemento(f));
					m_view.getBeansModel().fireTableDataChanged();
				}
			}
		}
	}
}
