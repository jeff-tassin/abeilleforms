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

package com.jeta.swingbuilder.gui.main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.FormPackage;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.codegen.ForwardEngineer;
import com.jeta.swingbuilder.gui.beanmgr.BeanManagerView;
import com.jeta.swingbuilder.gui.beanmgr.DefaultBeanManager;
import com.jeta.swingbuilder.gui.components.SystemPropertiesPanel;
import com.jeta.swingbuilder.gui.components.TSErrorDialog;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.editor.RunFrame;
import com.jeta.swingbuilder.gui.export.ComponentNamesExporter;
import com.jeta.swingbuilder.gui.export.ExportNamesView;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;
import com.jeta.swingbuilder.gui.lookandfeel.DefaultLookAndFeelManager;
import com.jeta.swingbuilder.gui.lookandfeel.LookAndFeelInfo;
import com.jeta.swingbuilder.gui.project.ProjectSettingsRule;
import com.jeta.swingbuilder.gui.project.ProjectSettingsView;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.gui.project.UserPreferencesView;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;
import com.jeta.swingbuilder.project.DefaultProjectManager;
import com.jeta.swingbuilder.store.ProjectLevelImportedBeansModel;
import com.jeta.swingbuilder.store.ProjectModel;

/**
 * Controller class for MainFrame
 * 
 * @author Jeff Tassin
 */
public class MainFrameController extends FormEditorController {
	private MainFrame m_frame;

	/**
	 * The last preview frame that was active
	 */
	private RunFrame m_runframe;

	/**
	 * Special action for handling look and feel changes.
	 */
	LookAndFeelAction m_lfaction = new LookAndFeelAction();

	static final int CLOSE_OK = 0;

	static final int CLOSE_CANCELED = 1;

	/**
	 * The controller for the MainFrame window. Handles all user events.
	 * 
	 * @param frame
	 */
	MainFrameController(MainFrame frame) {
		super(frame, frame.m_form_popup);
		m_frame = frame;
		assignAction(MainFrameNames.ID_CREATE_FORM, new NewFormAction());
		assignAction(MainFrameNames.ID_CREATE_PROJECT, new NewProjectAction());
		assignAction(MainFrameNames.ID_CLOSE_PROJECT, new CloseProjectAction());
		assignAction(MainFrameNames.ID_OPEN_FORM, new OpenFormAction());
		assignAction(MainFrameNames.ID_OPEN_PROJECT, new OpenProjectAction());
		assignAction(MainFrameNames.ID_SHOW_FORM, new ShowFormAction());
		assignAction(MainFrameNames.ID_SAVE_FORM, new SaveFormAction());
		assignAction(MainFrameNames.ID_SAVE_FORM_AS, new SaveAsAction());
		assignAction(MainFrameNames.ID_CLOSE_FORM, new CloseFormAction());

		assignAction(FormEditorNames.ID_EXPORT_COMPONENT_NAMES, new ExportNamesAction());
		assignAction(MainFrameNames.ID_FORWARD_ENGINEER, new ForwardEngineerAction());
		assignAction(MainFrameNames.ID_PROJECT_SETTINGS, new ProjectSettingsAction());
		assignAction(MainFrameNames.ID_BEAN_MANAGER, new BeanManagerAction());
		assignAction(MainFrameNames.ID_SYSTEM_PROPERTIES, new SystemPropertiesAction());
		assignAction(MainFrameNames.ID_ENV_SETTINGS, new UserPreferencesAction());

		assignAction(MainFrameNames.ID_ABOUT, new AboutAction());
		assignAction(MainFrameNames.ID_EXIT, new ExitAction());

		assignAction(MainFrameNames.ID_FORM_PROPERTIES, new TogglePropertiesFrame());
		m_frame.getComponentByName(FormPropertiesNames.ID_CLOSE_FRAME).addMouseListener(new ClosePropertiesFrame());
		m_frame.getComponentByName(FormPropertiesNames.ID_DOCK_FRAME).addMouseListener(new DockPropertiesFrame());

		if (FormDesignerUtils.isDebug()) {
			assignAction(MainFrameNames.ID_SHOW_FORM_MANAGER, new ShowFormManagerAction());
			assignAction(MainFrameNames.ID_SHOW_UNDO_MANAGER, new ShowUndoManagerAction());
		}

		/**
		 * clear the clipboard of any previous form components. This causes
		 * problems with falsely enabling the paste command
		 */
		try {

			java.awt.Toolkit kit = java.awt.Toolkit.getDefaultToolkit();
			java.awt.datatransfer.Clipboard clipboard = kit.getSystemClipboard();
			java.awt.datatransfer.Transferable transferable = clipboard.getContents(null);
			if (com.jeta.swingbuilder.gui.dnd.FormObjectFlavor.isDesignerFlavorSupported(transferable)) {
				clipboard.setContents(new java.awt.datatransfer.StringSelection(""), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Attempts to copy the given collection of form files (String objects) to
	 * the classpath. This will only happen if the classpath is valid in the
	 * project settings.
	 */
	private void copyToClasspath(Collection files) {
		try {
			ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
			ProjectModel pmodel = m_frame.getProject();
			if (pmodel != null) {
				String classpath = FormDesignerUtils.fastTrim(pmodel.getClassPath());

				if ((classpath != null) && (!"".equals(classpath))) {
					File rootDir = pmodel.getProjectRootDir();
					File f = new File(rootDir, classpath);
					if (f.isDirectory())
						classpath = f.getPath();
					else
						f = new File(classpath);

					if (f.isDirectory()) {
						char lastchar = classpath.charAt(classpath.length() - 1);
						if (lastchar != '\\' && lastchar != '/') {
							classpath = classpath + File.separatorChar;
						}
						Iterator iter = files.iterator();
						while (iter.hasNext()) {
							String abspath = (String) iter.next();
							String relativepath = pmgr.getRelativePath(abspath);
							if (relativepath != null) {
								File outputfile = new File(classpath + relativepath);
								File outputdir = new File(outputfile.getParent());
								if (!outputdir.exists()) {
									outputdir.mkdirs();
								}

								FormDesignerUtils.copyFile(outputfile.getPath(), abspath);
							}
						}
					}
					else {
						System.err.println("Error. Unable to determine classes directory: " + pmodel.getClassPath());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void editProject(ProjectModel model) {
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
		ProjectSettingsView view = new ProjectSettingsView(model);
		dlg.addValidator(view, new ProjectSettingsRule());
		dlg.setPrimaryPanel(view);
		if (model == null) {
			dlg.setTitle(I18N.getLocalizedMessage("New Project"));
		}
		else {
			dlg.setTitle(I18N.getLocalizedMessage("Edit Project"));
		}
		dlg.setSize(dlg.getPreferredSize());
		dlg.showCenter();
		if (dlg.isOk()) {
			ProjectModel pmodel = view.getModel();
			try {
				String path = pmodel.getProjectPath();
				int pos = path.lastIndexOf(".jfpr");
				if (pos != path.length() - 5) {
					path = path + ".jfpr";
					pmodel.setProjectPath(path);
				}

				DefaultBeanManager bmgr = (DefaultBeanManager) JETARegistry.lookup(DefaultBeanManager.COMPONENT_ID);
				bmgr.setModel(pmodel.getProjectLevelImportedBeansModel());
				m_frame.reloadComponentsToolbar();
				
				if (!pmodel.equals(model)) {
					FileOutputStream fos = new FileOutputStream(pmodel.getProjectPath());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(pmodel);
					oos.close();
				}
				m_frame.setProject(pmodel);
			} catch (Exception e) {
				FormsLogger.severe(e);
			}
		}
	}

	/**
	 * @return the current form editor
	 */
	public FormEditor getCurrentEditor() {
		return m_frame.getCurrentEditor();
	}

	/**
	 * Closes the current form.
	 * 
	 * @return CLOSE_OK(0) if the form was successfully closed CLOSE_CANCELED
	 *         (1) if the operation was canceled by the user
	 */
	private int closeEditor(FormEditor editor) {
		if (editor != null) {
			if (editor.isModified() && editor.isLinked()) {
				String filename = editor.getForm().getFileName();
				if (filename == null)
					filename = I18N.getLocalizedMessage("New Form");
				String msg = I18N.format("Form_is_modified_save_1", filename);
				String title = I18N.getLocalizedMessage("Confirm");
				int result = JOptionPane.showConfirmDialog(m_frame, msg, title, JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					if (saveForm(false) == null)
						return CLOSE_CANCELED;
				}
				else if (result == JOptionPane.CANCEL_OPTION) {
					return CLOSE_CANCELED;
				}
			}
			FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
			fmgr.closeForm(editor.getForm().getId());
			FormManagerDesignUtils.clearUnreferencedForms();
		}
		return CLOSE_OK;
	}

	/**
	 * Closes the current project
	 * 
	 * @return CLOSE_OK(0) if the project was successfully closed CLOSE_CANCELED
	 *         (1) if the operation was canceled by the user
	 */
	int closeProject() {
		ProjectModel pmodel = m_frame.getProject();
		if (pmodel != null) {
			TSUserPropertiesUtils.setString(UserPreferencesNames.ID_LAST_PROJECT, pmodel.getProjectPath());
		}
		Collection editors = m_frame.getEditors();
		Iterator iter = editors.iterator();
		while (iter.hasNext()) {
			FormEditor editor = (FormEditor) iter.next();
			if (editor.isModified()) {
				if (closeEditor(editor) == CLOSE_CANCELED)
					return CLOSE_CANCELED;
			}
		}

		/**
		 * now close all unmodified forms.
		 */
		editors = m_frame.getEditors();
		iter = editors.iterator();
		while (iter.hasNext()) {
			FormEditor editor = (FormEditor) iter.next();
			closeEditor(editor);
		}

		m_frame.setProject(null);

		DefaultBeanManager bmgr = (DefaultBeanManager) JETARegistry.lookup(DefaultBeanManager.COMPONENT_ID);
		DefaultProjectManager projectMgr = (DefaultProjectManager) JETARegistry.lookup(DefaultProjectManager.COMPONENT_ID);
		bmgr.setModel((projectMgr.getProject() != null ? projectMgr.getProject().getProjectLevelImportedBeansModel() : (ProjectLevelImportedBeansModel) null));
		m_frame.reloadComponentsToolbar();

		return CLOSE_OK;
	}

	/**
	 * Opens the last project if the OPEN_LAST_PROJECT flag is true.
	 */
	void openLastProject() {
		if (m_frame.getProject() != null) {
			return;
		}

		try {
			String projpath = (String) JETARegistry.lookup(UserPreferencesNames.ID_LAST_PROJECT);
			if (projpath != null || TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_OPEN_LAST_PROJECT, true)) {
				if (projpath == null)
					projpath = TSUserPropertiesUtils.getString(UserPreferencesNames.ID_LAST_PROJECT, null);

				if (projpath != null) {
					File f = new File(projpath);
					if (f.exists()) {
						FileInputStream fis = new FileInputStream(f);
						ObjectInputStream ois = new ObjectInputStream(fis);
						ProjectModel pmodel = (ProjectModel) ois.readObject();
						pmodel.setProjectPath(f.getPath());
						m_frame.setProject(pmodel);
						validateProject(pmodel);

						DefaultBeanManager bm = (DefaultBeanManager) JETARegistry.lookup(DefaultBeanManager.COMPONENT_ID);
						bm.setModel(pmodel.getProjectLevelImportedBeansModel());
						m_frame.reloadComponentsToolbar();
					}
				}
			}
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

	private void postSaveForm(boolean saveAs) {
		final boolean sa = saveAs;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				saveForm(sa);
			}
		});
	}

	/**
	 * Saves the form to a file.
	 * 
	 * @param saveAs
	 *            if true, then prompts the user for a new file name. If false,
	 *            uses the current filename for the selected form.
	 */
	private File saveForm(boolean saveAs) {
		m_frame.unitTest();
		m_frame.getPropertyContainer().stopEditing();

		FormEditor editor = m_frame.getCurrentEditor();
		FormComponent fc = null;
		if (editor != null) {
			editor.saveFocusPolicy();
			fc = editor.getFormComponent();
		}

		/**
		 * the list of form files that we are saving. This is the current form
		 * and any linked forms
		 */
		LinkedList files = new LinkedList();
		File file = null;
		try {
			if (fc != null) {
				FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);

				String path = fc.getAbsolutePath();
				if (saveAs)
					path = null;

				if (path == null) {
					FileChooserConfig fcc = new FileChooserConfig(".form", new TSFileFilter("jfrm,xml", "Form Files(*.jfrm,*.xml)"));
					fcc.setParentComponent(m_frame);
					file = TSFileChooserFactory.showSaveDialog(fcc);
					if (file == null)
						return null;

					path = file.getPath();
					int pos = path.lastIndexOf(".jfrm");
					if (pos != path.length() - 5) {
						pos = path.lastIndexOf(".xml");
						if (pos != path.length() - 4) {
							String ext = ".jfrm";
							if (TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_STORE_AS_XML, false))
								ext = ".xml";

							path = path + ext;
							file = new File(path);
						}
					}

					/** see if form is already opened */
					if (fmgr.isOpened(file.getPath())) {
						String msg = I18N.format("Form_is_already_opened_in_editor_1", file.getName());
						String title = I18N.getLocalizedMessage("Error");
						JOptionPane.showMessageDialog(m_frame, msg, title, JOptionPane.ERROR_MESSAGE);
						return null;
					}
				}
				else {
					file = new File(path);
				}

				String oldid = fc.getId();
				path = file.getPath();

				FormPackage fpackage = new FormPackage(fc.getExternalState(StateRequest.SHALLOW_COPY));
				FormDesignerUtils.saveForm(fpackage, file);

				files.add(path);
				if (!path.equals(oldid)) {
					// the clone operation will update the forms ids with the
					// FormManager
					fc = FormManagerDesignUtils.clone(fmgr, editor, path);
					FormManagerDesignUtils.registerForms(fmgr, fc);
					editor.setFormComponent(fc);
					fmgr.installHandlers(m_frame, fc);
					installHandlers(editor);
					fmgr.activateForm(editor.getTopParent().getId());
					editor.activate();

					/** unit test after the clone */
					m_frame.unitTest();
				}
				m_frame.formNameChanged(fc);

				/**
				 * now, save linked forms that are located in the form we are
				 * saving
				 */
				Collection links = FormManagerDesignUtils.getNestedForms(fmgr, fc);
				Iterator iter = links.iterator();
				while (iter.hasNext()) {
					String fid = (String) iter.next();
					FormComponent nest = fmgr.getForm(fid);
					if (nest != null) {
						if (nest != fc && nest.isLinked()) {
							fpackage = new FormPackage(nest.getExternalState(StateRequest.SHALLOW_COPY));
							FormDesignerUtils.saveForm(fpackage, new File(nest.getAbsolutePath()));
							files.add(nest.getAbsolutePath());
						}
						FormEditor parent_editor = m_frame.getEditor(nest);
						if (parent_editor != null)
							parent_editor.clearUndoableEdits();
					}
				}

				editor.clearUndoableEdits();
				FormManagerDesignUtils.clearUnreferencedForms();
				copyToClasspath(files);
				m_frame.unitTest();

				return file;

			}
		} catch (Exception e) {
			TSErrorDialog dlg = null;

			String caption = I18N.getLocalizedMessage("Error.  Unable to save file");
			if (file == null) {
				dlg = TSErrorDialog.createDialog(m_frame, caption, null, e);
			}
			else {
				String msg = I18N.format("Unable_to_save_file_1", file.getName());
				dlg = TSErrorDialog.createDialog(m_frame, caption, msg, e);
			}

			dlg.showCenter();
		}
		m_frame.updateModifiedStatus();
		return null;
	}

	/** Sets the look and feel for the application based on user settings */
	public static void setDefaultLookAndFeel() {
		DefaultLookAndFeelManager dfm = (DefaultLookAndFeelManager) JETARegistry.lookup(DefaultLookAndFeelManager.COMPONENT_ID);
		if (dfm != null)
			setLookAndFeel(null, null, dfm.getDefaultLookAndFeel());
	}

	/** sets the look and feel for the given frame and look an feel class name */
	public static void setLookAndFeel(MainFrame mainframe, JFrame runframe, LookAndFeelInfo lookandfeel) {
		try {
			if (lookandfeel == null)
				return;

			DefaultLookAndFeelManager dfm = (DefaultLookAndFeelManager) JETARegistry.lookup(DefaultLookAndFeelManager.COMPONENT_ID);
			dfm.setLookAndFeel(lookandfeel);

			if (mainframe != null) {
				FormUtils.updateLookAndFeel(mainframe);
				mainframe.updateUI();
			}

			if (runframe != null && runframe.isVisible()) {
				FormUtils.updateLookAndFeel(runframe);
			}
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}

	private void validateProject(ProjectModel pmodel) {
		try {
			if (pmodel == null)
				return;

			String emsg = ProjectSettingsRule.validateProject(pmodel);
			if (emsg != null) {
				TSErrorDialog dlg = TSErrorDialog.createDialog(m_frame, "Error", emsg, null);
				dlg.showErrorIcon(I18N.getLocalizedMessage("Project has invalid paths"));
				dlg.showCenter();
			}
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

	/**
	 * Show About dialog
	 */
	public class AboutAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
			AboutView view = new AboutView();
			dlg.setPrimaryPanel(view);
			dlg.setTitle(I18N.getLocalizedMessage("About"));
			dlg.pack();
			dlg.showOkButton(false);
			dlg.setCloseText(I18N.getLocalizedMessage("Close"));
			/*
                        Dimension d = dlg.getSize();
			if (JETAToolbox.isWindows() || JETAToolbox.isOSX()) {
				d.width -= 2;
				dlg.setSize(d);
			}
                        */
			dlg.setSize( new java.awt.Dimension(430,350) );
			dlg.showCenter();
		}
	}

	/**
	 * Invokes the Bean Manager dialog
	 */
	public class BeanManagerAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
			DefaultBeanManager bmgr = (DefaultBeanManager) JETARegistry.lookup(DefaultBeanManager.COMPONENT_ID);
			BeanManagerView view = new BeanManagerView(bmgr.getModel());
			dlg.setPrimaryPanel(view);
			dlg.setTitle(I18N.getLocalizedMessage("Java Bean Manager"));
			dlg.setSize(dlg.getPreferredSize());
			dlg.showCenter();
			if (dlg.isOk()) {
				bmgr.setModel(view.getImportedBeansModel());
				m_frame.reloadComponentsToolbar();
			}
		}
	}

	/**
	 * Closes the form
	 */
	public class CloseFormAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			closeEditor(getCurrentEditor());
		}
	}

	/**
	 * Closes the current project.
	 */
	public class CloseProjectAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			closeProject();
		}
	}

	public class TogglePropertiesFrame implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_frame.getDocker().togglePropertiesFrame();
		}
	}

	public class ClosePropertiesFrame extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			m_frame.getDocker().togglePropertiesFrame();
		}
	}

	public class DockPropertiesFrame extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			m_frame.getDocker().dockPropertiesFrame(null);
		}
	}

	/**
	 * Exit action handler
	 */
	public class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_frame.shutDown();
		}
	}

	/**
	 * Exports the component names on a given form.
	 */
	public class ExportNamesAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = m_frame.getCurrentEditor();
			if (editor != null) {
				GridComponent gc = editor.getSelectedComponent();
				if (gc instanceof FormComponent) {
					JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
					ExportNamesView view = new ExportNamesView();
					dlg.setTitle(I18N.getLocalizedMessage("Export Component Names"));
					dlg.setPrimaryPanel(view);
					dlg.setSize(dlg.getPreferredSize());
					dlg.showCenter();
					if (dlg.isOk()) {
						view.saveToModel();
						ComponentNamesExporter exporter = new ComponentNamesExporter(view);
						exporter.exportToClipboard((FormComponent) gc);
					}
				}
			}
		}
	}

	/**
	 * Exports a Form to Java Source
	 */
	public class ForwardEngineerAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = m_frame.getCurrentEditor();
			if (editor != null) {
				try {
					FormComponent fc = editor.getFormComponent();
					FormMemento fm = (FormMemento) fc.getExternalState(StateRequest.DEEP_COPY);
					ForwardEngineer fe = new ForwardEngineer();
					fe.generate(m_frame, fm);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Focus Management
	 */
	public class FocusManagerAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = m_frame.getCurrentEditor();
			if (editor != null) {
				boolean show_focus = false;
				Collection comps = m_frame.getComponentsByName(MainFrameNames.ID_FOCUS_MANAGER);
				Iterator iter = comps.iterator();
				while (iter.hasNext()) {
					Component comp = (Component) iter.next();
					if (comp instanceof javax.swing.AbstractButton) {
						show_focus = ((javax.swing.AbstractButton) comp).isSelected();
						break;
					}
				}
				editor.setFocusViewVisible(show_focus);
				editor.revalidate();
				editor.repaint();
			}
		}
	}

	/**
	 * Sets the look and feel for the application
	 */
	public class LookAndFeelAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			try {
				DefaultLookAndFeelManager lfm = (DefaultLookAndFeelManager) JETARegistry.lookup(DefaultLookAndFeelManager.COMPONENT_ID);
				if (lfm != null) {
					javax.swing.AbstractButton btn = (javax.swing.AbstractButton) evt.getSource();
					// the menu's action command will be the LookAndFeelInfo Id.
					setLookAndFeel(m_frame, m_runframe, lfm.findById(btn.getActionCommand()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ActionListener for creating a new form.
	 */
	public class NewFormAction implements ActionListener {
		/**
		 * @param evt
		 */
		public void actionPerformed(ActionEvent evt) {
			FormEditor editor = new FormEditor(m_frame, 20, 20);
			FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
			fmgr.registerForm(editor.getForm());
			m_frame.addForm(editor);
			editor.getForm().setControlButtonsVisible(false);
		}
	}

	/**
	 * Create a new project
	 */
	public class NewProjectAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			editProject(null);
		}
	}

	/**
	 * Opens a form from a previously saved file
	 */
	public class OpenFormAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			try {
				FileChooserConfig fcc = new FileChooserConfig(".form", new TSFileFilter("jfrm,xml", "Form Files(*.jfrm,*.xml)"));
				fcc.setParentComponent(m_frame);
				File f = TSFileChooserFactory.showOpenDialog(fcc);
				if (f != null) {
					FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
					fmgr.deactivateForms(m_frame.getCurrentEditor());
					FormComponent fc = fmgr.openLinkedForm(f);
					fmgr.activateForm(fc.getId());
					fmgr.showForm(fc.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens a project from a previously saved file
	 */
	public class OpenProjectAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			try {
				FileChooserConfig fcc = new FileChooserConfig("project", new TSFileFilter("jfpr", "Project Files (*.jfpr)"));
				fcc.setParentComponent(m_frame);
				File f = TSFileChooserFactory.showOpenDialog(fcc);
				if (f != null) {
					FileInputStream fis = new FileInputStream(f);
					ObjectInputStream ois = new ObjectInputStream(fis);
					ProjectModel pmodel = (ProjectModel) ois.readObject();
					pmodel.setProjectPath(f.getPath());
					m_frame.setProject(pmodel);
					validateProject(pmodel);

					DefaultBeanManager bmgr = (DefaultBeanManager) JETARegistry.lookup(DefaultBeanManager.COMPONENT_ID);
					bmgr.setModel(pmodel.getProjectLevelImportedBeansModel());
					m_frame.reloadComponentsToolbar();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes the project settings dialog
	 */
	public class ProjectSettingsAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			editProject(m_frame.getProject());
		}
	}

	/**
	 * Saves the form to a file
	 */
	public class SaveFormAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			saveForm(false);
		}
	}

	/**
	 * Saves the form to a new file
	 */
	public class SaveAsAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			saveForm(true);
		}
	}

	/**
	 * Shows the form as it would appear when running
	 */
	public class ShowFormAction implements ActionListener {

		private Rectangle getFrameBounds() {
			Rectangle result = null;
			try {
				if (m_runframe != null) {
					Dimension screensz = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

					Rectangle frect = m_runframe.getBounds();
					if (frect.width > 100 && frect.height > 100 && frect.x > 0 && frect.y > 0) {
						if ((frect.x + frect.width) < screensz.width && (frect.y + frect.height) < screensz.height) {
							result = frect;
						}
					}
				}

				if (result == null || result.width < 100 || result.height < 100)
					result = new Rectangle(100, 100, 600, 400);
			} catch (Exception e) {
				// ignore
				result = new Rectangle(100, 100, 600, 400);
			}
			return result;
		}

		public void actionPerformed(ActionEvent evt) {
			try {
				FormEditor editor = m_frame.getCurrentEditor();
				if (editor != null) {
					editor.unitTest();
					FormComponent fc = editor.getFormComponent();
					ComponentMemento cm = fc.getExternalState(StateRequest.DEEP_COPY);

					FormUtils.setDesignMode(false);

					fc = FormComponent.create();
					fc.setState(cm);

					FormPanel jetapanel = new FormPanel(fc);
					fc.postInitialize(jetapanel);

					if (FormDesignerUtils.isTest()) {
						// com.jeta.swingbuilder.test.JETATestFactory.runTest(
						// "test.jeta.forms.gui.form.FormValidator", fc );
					}

					Rectangle rect = getFrameBounds();

					if (m_runframe == null) {
						m_runframe = new RunFrame(jetapanel);
					}
					else {
						m_runframe.setForm(jetapanel);
					}
					m_runframe.setSize(rect.width, rect.height);
					m_runframe.setLocation(rect.x, rect.y);
					m_runframe.setVisible(true);
					jetapanel.transferFocusDownCycle();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				FormUtils.setDesignMode(true);
			}
		}
	}

	public class ShowFormManagerAction implements ActionListener {
		private com.jeta.swingbuilder.gui.formmgr.FormManagerFrame m_frame;

		public void actionPerformed(ActionEvent evt) {
			if (m_frame == null) {
				m_frame = new com.jeta.swingbuilder.gui.formmgr.FormManagerFrame();
				m_frame.setSize(600, 400);
				m_frame.setLocation(150, 150);
			}
			m_frame.reload();
			m_frame.setVisible(true);
		}
	}

	public class ShowUndoManagerAction implements ActionListener {
		private com.jeta.swingbuilder.gui.undo.UndoManagerFrame m_frame;

		public void actionPerformed(ActionEvent evt) {
			if (m_frame == null) {
				m_frame = new com.jeta.swingbuilder.gui.undo.UndoManagerFrame(MainFrameController.this.m_frame);
				m_frame.setSize(600, 400);
				m_frame.setLocation(150, 150);
			}
			m_frame.reload();
			m_frame.setVisible(true);
		}
	}

	public class SystemPropertiesAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
			dlg.setTitle(I18N.getLocalizedMessage("System Properties"));
			SystemPropertiesPanel view = new SystemPropertiesPanel();
			dlg.setPrimaryPanel(view);
			dlg.setSize(dlg.getPreferredSize());
			dlg.showCenter();
		}
	}

	public class UserPreferencesAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_frame, true);
			UserPreferencesView view = new UserPreferencesView();
			dlg.setPrimaryPanel(view);
			dlg.setTitle(I18N.getLocalizedMessage("Preferences"));
			dlg.setSize(dlg.getPreferredSize());
			dlg.showCenter();
			if (dlg.isOk()) {
				view.saveToModel();
				Collection editors = m_frame.getEditors();
				Iterator iter = editors.iterator();
				while (iter.hasNext()) {
					FormEditor editor = (FormEditor) iter.next();
					editor.updatePreferences();
				}
			}
		}
	}

}
