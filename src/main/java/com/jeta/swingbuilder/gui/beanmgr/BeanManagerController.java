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

package com.jeta.swingbuilder.gui.beanmgr;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JOptionPane;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.store.memento.IconMemento;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.store.ImportedBeanInfo;

/**
 * The controller for the BeanManagerView
 * 
 * @author Jeff Tassin
 */
public class BeanManagerController extends JETAController {
	/**
	 * The view
	 */
	private BeanManagerView m_view;

	/**
	 * The beans table model
	 */
	private BeansModel m_beansmodel;

	/**
	 * ctor
	 */
	public BeanManagerController(BeanManagerView view) {
		super(view);
		m_view = view;
		m_beansmodel = view.getBeansModel();
		assignAction(BeanManagerNames.ID_ADD_BEAN, new AddBeanAction());
		assignAction(BeanManagerNames.ID_DELETE_BEAN, new DeleteBeanAction());
		assignAction(BeanManagerNames.ID_SET_BEAN_ICON, new SetBeanIconAction());
		assignAction(BeanManagerNames.ID_ADD_JAR, new AddPathAction(false));
		assignAction(BeanManagerNames.ID_ADD_PATH, new AddPathAction(true));
		assignAction(BeanManagerNames.ID_DELETE_JAR, new DeletePathAction());
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
					m_beansmodel.addRow(info);

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
	public class AddPathAction implements ActionListener {
		private boolean m_dir_only = false;

		public AddPathAction(boolean dir_only) {
			m_dir_only = dir_only;
		}

		public void actionPerformed(ActionEvent evt) {
			int mode = (m_dir_only ? javax.swing.JFileChooser.DIRECTORIES_ONLY : javax.swing.JFileChooser.FILES_ONLY);

			TSFileFilter filter = null;
			if (!m_dir_only)
				filter = new TSFileFilter("jar,zip", "JAR Files(*.jar,*.zip)");

			FileChooserConfig fcc = new FileChooserConfig(null, mode, filter);
			fcc.setParentComponent(m_view);
			File f = TSFileChooserFactory.showOpenDialog(fcc);
			if (f != null && f.exists()) {
				try {
					m_view.addUrl(f.toURL());
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
	public class DeletePathAction implements ActionListener {
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
				File f = TSFileChooserFactory.showOpenDialog(".img", new TSFileFilter("gif,png,jpg,jpeg", "Image Files(*.gif,*.png,*.jpg)"));
				if (f != null) {
					bi.setIconMemento(new IconMemento(f));
					m_beansmodel.fireTableDataChanged();
				}
			}
		}
	}

}
