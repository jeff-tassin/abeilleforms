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

package com.jeta.swingbuilder.gui.formmgr;

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.EmptyCollection;
import com.jeta.swingbuilder.gui.dnd.FormObjectFlavor;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;

/**
 * Utility functions for dealing with the FormManager while in design mode.
 * 
 * @author Jeff Tassin
 */
public class FormManagerDesignUtils {

	/**
	 * Traverses the container hiearchy and checks if each child component is an
	 * instance of a FormComponent that has the given form id.
	 * 
	 * @return true if the given form contains a nested form with the given id.
	 */
	public static boolean containsForm(Container cc, String formId) {
		if (cc == null)
			return false;

		if (cc instanceof FormComponent) {
			FormComponent form = (FormComponent) cc;
			if (formId.equals(form.getId()))
				return true;
		}
		else if (cc instanceof FormSurrogate) {
			if (containsForm(((FormSurrogate) cc).getForm(), formId))
				return true;
		}

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof Container) {
				if (containsForm((Container) comp, formId))
					return true;
			}
		}
		return false;
	}

	/**
	 * Creates a clone of the given form component. This will also reset the
	 * id's of all the embedded child forms and register the new forms with the
	 * form manager
	 */
	public static FormComponent clone(FormManager fmgr, FormEditor editor, String newPath) throws FormException {
		FormComponent fc = editor.getForm();
		FormMemento memento = fc.getExternalState(StateRequest.SHALLOW_COPY);

		/**
		 * we must first deactivate all forms in the editor because when we set
		 * the state on the cloned object, any linked forms are added
		 */
		fmgr.deactivateForms(editor);

		FormComponent fcopy = FormComponent.create();
		fcopy.setState(memento);
		if (newPath != null)
			fcopy.setAbsolutePath(newPath);

		return fcopy;
	}

	/**
	 * Clears all forms in the given form manager that are not referenced or
	 * contained in any editor.
	 */
	public static void clearUnreferencedForms() {
		FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		if (fmgr instanceof AbstractFormManager) {
			TreeSet ref_forms = new TreeSet();
			ref_forms.addAll(getLinkedFormsFromClipboard());
			AbstractFormManager afm = (AbstractFormManager) fmgr;
			EditorManager em = afm.getEditorManager();
			Collection editors = em.getEditors();
			Iterator iter = editors.iterator();
			while (iter.hasNext()) {
				FormEditor editor = (FormEditor) iter.next();
				ref_forms.addAll(getNestedForms(fmgr, editor.getTopParent()));
				ref_forms.add(editor.getTopParent().getId());
			}

			Collection forms = fmgr.getForms();
			LinkedList to_remove = new LinkedList();
			to_remove.addAll(forms);
			iter = to_remove.iterator();
			while (iter.hasNext()) {
				String fid = (String) iter.next();
				if (!ref_forms.contains(fid)) {
					FormsLogger.debug("FormManagerDesignUtils.clearUnreferencedForms   removed form: " + fid);
					fmgr.removeForm(fid);
				}
			}
		}
	}

	/**
	 * Recursively deselects all cells
	 */
	public static void deselectAll(FormComponent form) {
		if (form == null)
			return;

		GridView view = form.getChildView();
		view.deselectAll();
		Iterator iter = view.gridIterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			gc.setSelected(false);
			if (gc instanceof FormComponent) {
				deselectAll((FormComponent) gc);
			}
		}
	}

	private static int getComponentCount(FormManager fmgr, Container form) {
		if (form == null)
			return 0;

		int totalCount = 0;
		for (int index = 0; index < form.getComponentCount(); index++) {
			Component comp = form.getComponent(index);
			if (comp instanceof Container) {
				if (comp instanceof FormSurrogate) {
					totalCount += getComponentCount(fmgr, ((FormSurrogate) comp).getForm());
				}
				else {
					totalCount += getComponentCount(fmgr, (Container) comp);
				}

				if (comp instanceof GridComponent) {
					java.awt.Component bean = ((GridComponent) comp).getBeanDelegate();
					if (bean instanceof javax.swing.JComponent) {
						totalCount++;
					}
				}
			}
		}
		return totalCount;
	}

	/**
	 * @return the total number of components in the form
	 */
	public static int getComponentCount(FormComponent form) {
		FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		return getComponentCount(fmgr, form);
	}

	/**
	 * Gets the ids of all nested forms contained in this component
	 */
	public static void getNestedForms(FormManager fmgr, LinkedList list, Container cc) {
		if (cc == null) {
			return;
		}

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof FormComponent || comp instanceof FormSurrogate) {
				list.add(((GridComponent) comp).getId());
			}

			if (comp instanceof FormSurrogate) {
				getNestedForms(fmgr, list, ((FormSurrogate) comp).getForm());
			}
			else if (comp instanceof Container) {
				getNestedForms(fmgr, list, (Container) comp);
			}
		}
	}

	/**
	 * @return the ids (as a Collection of Strings) of all nested forms
	 *         contained in this component
	 */
	public static Collection getNestedForms(FormManager fmgr, FormComponent fc) {
		LinkedList list = new LinkedList();
		getNestedForms(fmgr, list, fc);
		return list;
	}

	/**
	 * Get all linked forms that are nested within the given form component.
	 */
	private static void getLinkedForms(LinkedList list, Container cc) {
		if (cc == null) {
			assert (false);
			return;
		}

		if (cc instanceof FormComponent) {
			FormComponent fc = (FormComponent) cc;
			if (fc.isLinked())
				list.add(fc.getId());
		}
		else if (cc instanceof FormSurrogate) {
			FormComponent fc = ((FormSurrogate) cc).getForm();
			if (fc != null && fc.isLinked())
				list.add(fc.getId());

			cc = fc;
		}

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof Container)
				getLinkedForms(list, (Container) comp);
		}
	}

	/**
	 * @return a set of all nested linked form ids (String objects) including
	 *         the given form that are contained by the given form.
	 */
	public static Collection getLinkedForms(Container cc) {
		LinkedList forms = new LinkedList();
		getLinkedForms(forms, cc);
		return forms;
	}

	public static Component getApplicationFrame() {
		Object comp = JETARegistry.lookup(JETAToolbox.APPLICATION_FRAME);
		if (comp instanceof Component)
			return (Component) comp;
		else
			return null;
	}

	/**
	 * @return the collection of linked form ids on the clipboard if any.
	 */
	public static Collection getLinkedFormsFromClipboard() {
		try {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = kit.getSystemClipboard();
			Transferable transferable = clipboard.getContents(null);
			if (transferable.isDataFlavorSupported(FormObjectFlavor.LINKED_FORM_SET)) {
				Collection forms = (Collection) transferable.getTransferData(FormObjectFlavor.LINKED_FORM_SET);
				return forms;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EmptyCollection.getInstance();
	}

	/**
	 * Invokes a file chooser dialog which allows the user to select a form
	 * file. The form file is then checked to determine if it lies on the valid
	 * source path. If the form does not lie on a valid source path, an error
	 * dialog is displayed and null is returned.
	 */
	public static File openLinkedFormFile() {
		FileChooserConfig fcc = new FileChooserConfig(".form", new TSFileFilter("jfrm,xml", "Form Files(*.jfrm,*.xml)"));
		fcc.setParentComponent(getApplicationFrame());
		File f = TSFileChooserFactory.showOpenDialog(fcc);
		if (f != null) {
			ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
			/** check if the path is contained in a valid package for the project */
			if (pmgr.isValidAbsolutePath(f.getPath())) {
				return f;
			}
			else {
				String msg = I18N.getLocalizedMessage("Selected_form_not_in_source_path");
				String title = I18N.getLocalizedMessage("Error");

				JOptionPane.showMessageDialog(FormManagerDesignUtils.getApplicationFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}

	public static void registerForms(FormManager fmgr, Container cc) {
		if (cc == null)
			return;

		if (cc instanceof FormComponent)
			fmgr.registerForm((FormComponent) cc);
		else if (cc instanceof FormSurrogate)
			fmgr.registerForm(((FormSurrogate) cc).getForm());

		if (cc instanceof FormSurrogate)
			cc = ((FormSurrogate) cc).getForm();

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component obj = cc.getComponent(index);
			if (obj instanceof Container) {
				registerForms(fmgr, (Container) obj);
			}
		}
	}

}
