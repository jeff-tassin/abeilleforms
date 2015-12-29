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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.store.ImportedBeanInfo;
import com.jeta.swingbuilder.store.ImportedBeansModel;

/**
 * The view for managing imported beans in the builder
 * 
 * @author Jeff Tassin
 */
public class BeanManagerView extends JETAPanel {
	/**
	 * The beanManager.jfrm form
	 */
	private FormPanel m_view;

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
	public BeanManagerView(ImportedBeansModel ibmodel) {
		initialize(ibmodel);
		setController(new BeanManagerController(this));
	}

	/**
	 * Adds a user to the list model
	 */
	public void addUrl(URL url) {
		m_paths_model.addElement(url);
		m_bean_loader = null;
	}

	/**
	 * Deletes the bean from the view/model
	 */
	public void deleteSelectedBean() {
		JTable table = m_view.getTable(BeanManagerNames.ID_BEAN_TABLE);
		int row = table.getSelectedRow();
		m_beansmodel.removeRow(row);
		table.repaint();
		m_view.repaint();
	}

	/**
	 * Deletes the selected URL from the view/model
	 */
	public void deleteSelectedUrl() {
		JList list = m_view.getList(BeanManagerNames.ID_CLASSPATH_LIST);
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
				m_bean_loader.addUrl((URL) m_paths_model.elementAt(index));
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
	 * @return the model that describes the imported beans and their classpaths
	 */
	public ImportedBeansModel getImportedBeansModel() {
		ImportedBeansModel ibm = new ImportedBeansModel();
		for (int row = 0; row < m_beansmodel.getRowCount(); row++) {
			ibm.addImportedBean(m_beansmodel.getRow(row));
		}

		for (int index = 0; index < m_paths_model.size(); index++) {
			ibm.addUrl((URL) m_paths_model.elementAt(index));
		}
		return ibm;
	}

	/**
	 * @return the preferred size for this view
	 */
	public Dimension getPreferredSize() {
		return FormDesignerUtils.getWindowDimension(this, 280, 240);
	}

	/**
	 * @return the selected bean
	 */
	public ImportedBeanInfo getSelectedBean() {
		JTable table = m_view.getTable(BeanManagerNames.ID_BEAN_TABLE);
		int row = table.getSelectedRow();
		return m_beansmodel.getRow(row);
	}

	/**
	 * Initializes the view
	 */
	public void initialize(ImportedBeansModel ibmodel) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/beanmgr/beanManager.jfrm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JTable table = m_view.getTable(BeanManagerNames.ID_BEAN_TABLE);
		m_beansmodel = new BeansModel();
		table.setModel(m_beansmodel);
		Iterator iter = ibmodel.getImportedBeans().iterator();
		while (iter.hasNext()) {
			m_beansmodel.addRow((ImportedBeanInfo) iter.next());
		}

		int col_width = 60;
		TableColumnModel cmodel = table.getColumnModel();
		cmodel.getColumn(BeansModel.ICON_COLUMN).setPreferredWidth(col_width);
		cmodel.getColumn(BeansModel.NAME_COLUMN).setPreferredWidth(col_width * 5);
		cmodel.getColumn(BeansModel.SCROLLABLE_COLUMN).setPreferredWidth(col_width);

		m_paths_model = new DefaultListModel();
		JList list = m_view.getList(BeanManagerNames.ID_CLASSPATH_LIST);
		list.setModel(m_paths_model);

		iter = ibmodel.getUrls().iterator();
		while (iter.hasNext()) {
			m_paths_model.addElement((URL) iter.next());
		}

	}

}
