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
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.gui.formmgr.FormManagerUtils;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.editor.DesignFormComponent;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.handler.FormCellHandler;
import com.jeta.swingbuilder.gui.handler.FormKeyboardHandler;
import com.jeta.swingbuilder.gui.handler.StandardCellHandler;
import com.jeta.swingbuilder.gui.handler.StandardKeyboardHandler;
import com.jeta.swingbuilder.gui.handler.TabPaneCellHandler;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * Provides some common form management operations.
 * 
 * @author Jeff Tassin
 */
public abstract class AbstractFormManager implements FormManager {
	/**
	 * A map of formIds to FormComponents m_formcache<String,FormComponent>
	 */
	private HashMap m_formcache = new HashMap();

	/**
	 * Required for initializing newly created forms.
	 */
	private ComponentSource m_compsrc;

	/**
	 * The object responsible for managing the opened FormEditors
	 */
	private EditorManager m_editor_mgr;

	/**
	 * ctor
	 */
	public AbstractFormManager(ComponentSource compSrc, EditorManager emgr) {
		m_compsrc = compSrc;
		m_editor_mgr = emgr;
	}

	/**
	 * Activates an AWT container. We search for all child components and
	 * replace any nested forms with a surrogate. This is needed for components
	 * like JTabbedPanes where we can embed a form at design time.
	 */
	public void activateContainer(Container cc) {
		if (cc == null)
			return;

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof FormComponent) {
				activateForm((FormComponent) comp);
			}
			else if (comp instanceof Container) {
				activateContainer((Container) comp);
			}
		}
	}

	/**
	 * Activates this form. Basically, we need to search for all nested
	 * FormSurrogates contained here and replace with the actual form from the
	 * FormManager.
	 */
	public void activateForm(FormComponent form) {
		if (form == null)
			return;

		GridView view = form.getChildView();
		Collection children = view.listComponents();
		Iterator iter = children.iterator();
		// System.out.println( "FormManager.activateForm: id: " + form.getId() +
		// "
		// children: " + children.size() );

		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			if (gc instanceof FormSurrogate) {
				FormSurrogate fs = (FormSurrogate) gc;
				FormComponent fc = (FormComponent) getForm(fs.getId());
				if (fc == null && FormUtils.isDebug()) {
					System.out.println("FormManager.activateForm:   failed for: " + fs.getId());
					FormUtils.safeAssert(false);
				}
				view.replaceComponent(fc, fs);
				fc.setControlButtonsVisible(true);
				activateForm(fc);
			}
			else if (gc instanceof FormComponent) {
				FormComponent fc = (FormComponent) gc;
				fc.setControlButtonsVisible(true);
				activateForm(fc);
			}
			else if (gc instanceof StandardComponent) {
				StandardComponent sc = (StandardComponent) gc;
				Component comp = sc.getBeanDelegate();
				if (comp instanceof Container)
					activateContainer((Container) comp);
			}
		}
		form.revalidate();
	}

	/**
	 * Activates and shows the form in the application workspace. Additionally,
	 * any nested forms are synchronized with the latest changes in the new
	 * view. This is important because we can have multiple views of the same
	 * form.
	 */
	public void activateForm(String formId) {
		FormComponent fc = getForm(formId);
		assert (fc != null);
		if (fc != null) {
			activateForm(fc);
		}
	}

	/**
	 * Deactivates an AWT container. We search for all child components and
	 * replace any nested forms with a surrogate. This is needed for components
	 * like JTabbedPanes where we can embed a form at design time.
	 */
	public void deactivateContainer(Container cc) {
		if (cc == null)
			return;

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof FormComponent) {
				deactivateForm((FormComponent) comp);
			}
			else if (comp instanceof Container) {
				deactivateContainer((Container) comp);
			}
		}
	}

	/**
	 * Deactivates this form. Basically, we need to search for all nested
	 * FormComponents and replace with surrogates.
	 */
	public void deactivateForm(FormComponent form) {
		if (form == null)
			return;

		GridView view = form.getChildView();
		Collection children = view.listComponents();
		// System.out.println( "FormManager.deactivate: id: " + form.getId() + "
		// children: " + children.size() );

		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			if (gc instanceof FormComponent) {
				FormComponent childform = (FormComponent) gc;
				FormSurrogate fs = new FormSurrogate(childform);

				// System.out.println( "FormComponent.deactivate child form: " +
				// childform.getName() + " id: " + childform.getId() + " replace
				// child form at: col: " + childform.getColumn() + " row: " +
				// childform.getRow() );

				view.replaceComponent(fs, childform);

				/**
				 * We register the form each time because the child form could
				 * be embedded an not registered with the form manager yet.
				 */
				registerForm(childform);
				deactivateForm(childform);
			}
			else if (gc instanceof StandardComponent) {
				StandardComponent sc = (StandardComponent) gc;
				Component comp = sc.getBeanDelegate();
				if (comp instanceof Container) {
					deactivateContainer((Container) comp);
				}
			}
		}
	}

	/**
	 * DesActivates the forms in the given editor. Additionally, any nested
	 * forms are synchronized with the latest changes in the new view. This is
	 * important because we can have multiple views of the same form.
	 */
	public void deactivateForms(Container parent) {
		if (parent == null)
			return;

		assert (parent instanceof FormEditor);
		FormEditor editor = (FormEditor) parent;
		editor.deactivate();
		FormComponent fc = editor.getTopParent();
		assert (fc != null);
		if (fc != null) {
			deactivateForm(fc);
		}
	}

	/**
	 * @return the object responsible for managing the form editors.
	 */
	public EditorManager getEditorManager() {
		return m_editor_mgr;
	}

	/**
	 * Searches the list of cached forms for a form that has the given file
	 * path.
	 */
	protected FormComponent findForm(String path) {
		Iterator iter = m_formcache.values().iterator();
		while (iter.hasNext()) {
			FormComponent comp = (FormComponent) iter.next();
			if (path.equals(comp.getAbsolutePath())) {
				return comp;
			}
		}
		return null;
	}

	/**
	 * @return the form that has current formId. Null is returned if the form is
	 *         not in cache.
	 */
	public FormComponent getForm(String formId) {
		return (FormComponent) m_formcache.get(formId);
	}

	/**
	 * @return a collection of Form Ids that are current opened in the manager.
	 */
	public Collection getForms() {
		Collection form_ids = m_formcache.keySet();
		return form_ids;
	}

	/**
	 * Installs the event handlers for this component (keyboard and mouse). We
	 * need the componentsouce because some handlers are responsible for
	 * creating child components in the form.
	 */
	public void installFormHandlers(ComponentSource compsrc, FormComponent fc) {
		/**
		 * the only time fc is not an instanceof DesignFormComponent is when a
		 * custom bean is being used that itself uses a FormPanel. This is a
		 * very special case
		 */
		if (fc instanceof DesignFormComponent) {
			FormCellHandler cchandler = new FormCellHandler(fc, compsrc);
			FormKeyboardHandler keyhandler = new FormKeyboardHandler(fc, compsrc);
			fc.setMouseHandler(cchandler);
			fc.setKeyboardHandler(keyhandler);

			GridView view = fc.getChildView();
			Iterator iter = view.gridIterator();
			while (iter.hasNext()) {
				GridComponent gc = (GridComponent) iter.next();
				installHandlers(compsrc, gc);
			}
		}
	}

	/**
	 * Install event handlers (mouse and keyboard) for this component
	 */
	public void installStandardHandlers(ComponentSource compsrc, StandardComponent sc) {
		if (sc.getBeanDelegate() instanceof javax.swing.JTabbedPane) {
			TabPaneCellHandler handler = new TabPaneCellHandler(sc, compsrc);
			sc.setMouseHandler(handler);
		}
		else {
			StandardCellHandler handler = new StandardCellHandler(sc, compsrc);
			StandardKeyboardHandler keyhandler = new StandardKeyboardHandler(sc, compsrc);
			sc.setMouseHandler(handler);
			sc.setKeyboardHandler(keyhandler);
		}
	}

	/**
	 * Installs the mouse and keyboard handlers for the given component. Note
	 * that we don't do this in the GridComponent itself because handlers are
	 * only installed in design mode.
	 */
	public void installHandlers(ComponentSource compsrc, Container gridComponent) {
		if (gridComponent == null)
			return;

		if (gridComponent instanceof StandardComponent) {
			installStandardHandlers(compsrc, (StandardComponent) gridComponent);
		}
		else if (gridComponent instanceof FormComponent) {
			installFormHandlers(compsrc, (FormComponent) gridComponent);
		}

		for (int index = 0; index < gridComponent.getComponentCount(); index++) {
			Component comp = gridComponent.getComponent(index);
			if (comp instanceof Container) {
				installHandlers(compsrc, (Container) comp);
			}
		}
	}

	/**
	 * Opens a form from a absolute file path.
	 */
	public FormComponent openLinkedForm(String path) throws FormException {
		return openForm(new File(path));
	}

	/**
	 * Opens a form from a absolute file path.
	 */
	public FormComponent openLinkedForm(File f) throws FormException {
		return openForm(f);
	}

	/**
	 * Opens the form from a file and puts it in the cache.
	 */
	protected FormComponent openForm(File file) throws FormException {
		try {
			FormComponent fc = findForm(file.getPath());
			if (fc == null) {

				FormMemento memento = FormManagerUtils.loadForm(new FileInputStream(file));

				/**
				 * handle the case where the user may have copied the form to a
				 * different location/filename
				 */
				ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
				String relpath = pmgr.getRelativePath(file.getPath());
				if (relpath == null) {
					relpath = file.getPath();
				}

				/**
				 * The linked form may already be opened. So, let's use the
				 * currently opened form if this is the case.
				 */
				fc = getForm(relpath);
				if (fc == null) {
					fc = new DesignFormComponent();
					fc.setState(memento);

					fc.setAbsolutePath(file.getPath());
					// @JMT no need to reset id's because the embedded form id
					// is
					// derived from the hashCode of the form.
					FormManagerDesignUtils.registerForms(this, fc);

					installHandlers(m_compsrc, fc);
				}
			}
			else {
				// System.out.println( "AbstractFormManager.openForm found in
				// cache:
				// " + file.getPath() );
			}
			return fc;
		} catch (Exception e) {
			System.err.println("Unable to load form: " + file.getPath());
			FormsLogger.debug(e);
			throw new FormException(e);
		}
	}

	/**
	 * Opens an embedded form.
	 */
	public void openEmbeddedForm(FormComponent comp) {
		FormComponent fc = getForm(comp.getId());
		if (fc == null) {
			m_formcache.put(comp.getId(), comp);
		}
		else {
			assert (fc == comp);
		}
	}

	/**
	 * Registers a form with this FormManager. This is mainly used for embedded
	 * forms.
	 */
	public void registerForm(FormComponent fc) {
		if (fc == null)
			return;

		if (FormDesignerUtils.isDebug()) {
			FormComponent exists = getForm(fc.getId());
			if (exists != null) {
				if (exists != fc) {
					System.out.println("AbstractFormManager.registerForm failed  already exists.  " + fc.getId());
				}
			}
		}
		m_formcache.put(fc.getId(), fc);
	}

	/**
	 * Clears a form from the cache. This happens when we save as a new form.
	 */
	public void removeForm(String id) {
		m_formcache.remove(id);
	}

	/**
	 * Runs unit test routines on this FormManager.
	 */
	public void unitTest() {
		if (FormDesignerUtils.isTest()) {
			// com.jeta.swingbuilder.test.JETATestFactory.runTest(
			// "test.jeta.swingbuilder.gui.formmgr.FormManagerValidator", this
			// );
		}
	}

}
