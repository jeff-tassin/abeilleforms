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

package com.jeta.swingbuilder.gui.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanDescriptor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * The container for the property pane
 * 
 * @author Jeff Tassin
 */
public class PropertyPaneContainer extends JETAPanel implements GridViewListener, PropertyEditorListener {
	/**
	 * The main view for our frame
	 */
	private PropertyPane m_advview;

	private PropertyPane m_basicview;

	private PropertyPane m_activeview;

	/**
	 * The current grid component that is visible in the property pane.
	 */
	private GridComponent m_current_comp;

	private JTabbedPane m_tabpane;

	/**
	 * Displays the component name for the current bean
	 */
	private JTextField m_name_field;

	/**
	 * Displays the classname for the current bean
	 */
	private JTextField m_class_field;

	/**
	 * Button for launching the customizer dialog for a Java Bean - if that Java
	 * Bean has a customizer
	 */
	private JButton m_customizer_btn;

	/**
	 * @param model
	 */
	public PropertyPaneContainer() {
		setLayout(new BorderLayout());
		add(createView(), BorderLayout.CENTER);
	}

	/**
	 * @param model
	 * @return
	 */
	private void createTabPane() {
		m_basicview = new PropertyPane(true);
		m_activeview = m_basicview;
		PropertyTableModel tmodel = m_basicview.getTableModel();
		tmodel.addPropertyListener(this);

		m_advview = new PropertyPane(false);
		tmodel = m_advview.getTableModel();
		tmodel.addPropertyListener(this);

		m_tabpane = new JTabbedPane();
		m_tabpane.addTab(I18N.getLocalizedMessage("Basic"), m_basicview);
		m_tabpane.addTab(I18N.getLocalizedMessage("All"), m_advview);
		m_tabpane.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				PropertyPane ppane = getActiveView();
				ppane.setBean(getBean());
			}
		});
	}

	/**
	 * Top panel that displays the name and class of the current bean
	 */
	private JPanel createNamePanel() {
		JPanel panel = new JPanel();
		FormLayout layout = new FormLayout("pref,2dlu,pref:grow", "pref,2dlu,pref,4dlu");
		panel.setLayout(layout);
		CellConstraints cc = new CellConstraints();

		panel.add(new JLabel(I18N.getLocalizedDialogLabel("Name")), cc.xy(1, 1));
		m_name_field = new JTextField();
		m_name_field.setEnabled(false);
		panel.add(m_name_field, cc.xy(3, 1));

		panel.add(new JLabel(I18N.getLocalizedDialogLabel("Component")), cc.xy(1, 3));

		layout = new FormLayout("d:grow,24px", "fill:pref");
		m_class_field = new JTextField();
		m_class_field.setEditable(false);
		JPanel cpanel = new JPanel(layout);

		m_customizer_btn = new JButton(FormDesignerUtils.loadImage(Icons.EDIT_16));
		m_customizer_btn.setPreferredSize(new java.awt.Dimension(24, 16));
		m_customizer_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showCustomizerDialog();
			}
		});
		m_customizer_btn.setToolTipText(I18N.getLocalizedMessage("Bean Customizer"));
		m_customizer_btn.setEnabled(false);

		cpanel.add(m_class_field, cc.xy(1, 1));
		cpanel.add(m_customizer_btn, cc.xy(2, 1));

		panel.add(cpanel, cc.xy(3, 3));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		m_name_field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateBeanName();
			}
		});

		return panel;
	}

	/**
	 * @param model
	 * @return
	 */
	private Component createView() {
		JPanel panel = new JPanel(new BorderLayout());
		createTabPane();
		panel.add(createNamePanel(), BorderLayout.NORTH);
		panel.add(m_tabpane, BorderLayout.CENTER);
		return panel;
	}

	PropertyPane getActiveView() {
		return (PropertyPane) m_tabpane.getSelectedComponent();
	}

	JETABean getBean() {
		if (m_current_comp == null)
			return null;
		return m_current_comp.getBean();
	}

	/**
	 * @return the customizer for the current bean. Null is returned if the bean
	 *         does not have a customizer.
	 */
	private Class getCustomizer() {
		JETABean bean = getBean();
		if (bean != null) {
			DynamicBeanInfo beaninfo = bean.getBeanInfo();
			if (beaninfo != null) {
				BeanDescriptor bd = beaninfo.getBeanDescriptor();
				if (bd != null) {
					return bd.getCustomizerClass();
				}
			}
		}
		return null;
	}

	public Dimension getMinimumSize() {
		return new Dimension(24, 24);
	}

	/** GridViewListener implementation */
	public void gridChanged(GridViewEvent evt) {
		/**
		 * Handle case where the user has changed the bean name in the editor.
		 * We need to update the name field in this case.
		 */
		if (GridCellEvent.COMPONENT_NAME_CHANGED.equals(evt.getCommand())) {
			GridComponent comp = evt.getComponent();
			if (comp == m_current_comp) {
				JETABean bean = comp.getBean();
				updateNameField(bean == null ? "" : bean.getBeanName());
				return;
			}
		}

		if (evt.getId() == GridViewEvent.EDIT_COMPONENT || evt.getId() == GridViewEvent.CELL_SELECTED) {
			GridComponent comp = evt.getComponent();
			update(comp);
		}

	}

	public void refreshView() {
		getActiveView().cancelEditing();
		getActiveView().getTableModel().fireTableDataChanged();
	}

	/**
	 * Stops any editing in the property table
	 */
	public void stopEditing() {
		getActiveView().stopEditing();
		updateBeanName();
	}

	public void update(GridComponent comp) {
		/**
		 * Handle the case where the user has typed a new name in the
		 * name_field. If the user then clicks on a different component in the
		 * editor, we want to update the name of the current component before
		 * doing anything else.
		 */
		if (comp != m_current_comp) {
			updateBeanName();
		}

		m_current_comp = comp;
		if (comp != null) {
			JETABean bean = comp.getBean();
			getActiveView().setBean(bean);

			if (bean == null) {
				updateNameField(null);
				m_class_field.setText("");
				m_class_field.setToolTipText("");
			}
			else {

				updateNameField(bean.getBeanName());
				if (bean.getDelegate() != null) {
					String classname = bean.getDelegate().getClass().getName();
					m_class_field.setToolTipText(classname);

					int pos = classname.lastIndexOf(".");
					if (pos >= 0) {
						classname = classname.substring(pos + 1, classname.length());
					}
					m_class_field.setText(classname);
				}
			}
		}
		else {
			updateNameField(null);
			getActiveView().setBean(null);
		}
		m_customizer_btn.setEnabled(getCustomizer() != null);
	}

	/**
	 * PropertyEditorListener Implementation
	 */
	public void propertyChanged(PropertyEditorEvent evt) {
		JETABean bean = getBean();
		if (m_current_comp != null && evt.getBean() == bean) {
			m_current_comp.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_CHANGED, m_current_comp));
		}

		if (bean == null) {
			updateNameField(null);
		}
		else {
			updateNameField(bean.getBeanName());
		}
	}

	/**
	 * Shows the customizerDialog for a java bean
	 */
	private void showCustomizerDialog() {
		try {
			stopEditing();
			Class ccustomizer = getCustomizer();
			if (ccustomizer != null) {
				Component comp = (Component) ccustomizer.newInstance();
				JETAPanel panel = new JETAPanel(new BorderLayout());
				panel.add(comp, BorderLayout.CENTER);
				JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, this, true);
				dlg.setPrimaryPanel(panel);
				dlg.setTitle(I18N.getLocalizedMessage("Bean Customizer"));
				Dimension sz = panel.getPreferredSize();
				sz.width = Math.max(sz.width, 200);
				sz.height = Math.max(sz.height, 75);
				panel.setPreferredSize(sz);

				dlg.setSize(dlg.getPreferredSize());

				java.beans.Customizer custom = (java.beans.Customizer) comp;
				custom.setObject(getBean().getDelegate());
				custom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
					public void propertyChange(java.beans.PropertyChangeEvent evt) {
						m_current_comp.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_CHANGED, m_current_comp));
					}
				});
				dlg.showCenter();
				if (dlg.isOk()) {
					m_current_comp.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_CHANGED, m_current_comp));
					refreshView();
				}
			}
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

	/**
	 * Updates the name of the bean using the value entered in the name field
	 */
	public void updateBeanName() {
		updateBeanName(getBean());
	}

	/**
	 * Updates the name of the bean using the value entered in the name field
	 */
	private void updateBeanName(JETABean bean) {
		if (bean != null) {
			Component comp = bean.getDelegate();
			if (comp != null) {
				String bname = FormDesignerUtils.fastTrim(m_name_field.getText());
				if (!bname.equals(comp.getName()))
					comp.setName(bname);
			}
		}
	}

	private void updateNameField(String beanName) {
		if (beanName == null) {
			m_name_field.setText("");
			m_name_field.setEnabled(false);
		}
		else {
			m_name_field.setText(beanName);
			m_name_field.setEnabled(true);
		}
	}

}
