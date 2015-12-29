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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.components.line.HorizontalLineComponent;
import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.EmptyCollection;
import com.jeta.swingbuilder.gui.beanmgr.DefaultBeanManager;
import com.jeta.swingbuilder.gui.components.EmbeddedFormComponentFactory;
import com.jeta.swingbuilder.gui.components.GenericComponentFactory;
import com.jeta.swingbuilder.gui.components.LinkedFormComponentFactory;
import com.jeta.swingbuilder.gui.components.SwingComponentFactory;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;
import com.jeta.swingbuilder.store.RegisteredBean;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The frame used to contain the toolbar for editing forms.
 * 
 * @author Jeff Tassin
 */
public class ComponentsToolBar extends JETAPanel implements ComponentSource {
	public static final int MAX_TOOLBAR_ROWS = 20;

	public static final String ID_SELECTION_TOOL = "selection.tool";
	public static final String ID_EMBEDDED_FORM_COMPONENT = "embedded.form.tool";
	public static final String ID_LINKED_FORM_COMPONENT = "linked.form.tool";
	public static final String ID_GENERIC_COMPONENT = "generic.component";
	public static final String ID_TEST_COMPONENT = "com.jeta.swingbuilder.gui.components.TestBeanFactory";

	/** the selected tool */
	private String m_current_tool = ID_SELECTION_TOOL;

	/**
	 * Factories for creating components
	 */
	private HashMap m_factories = new HashMap();

	/** @param model */
	public ComponentsToolBar() {
		try {
			FormLayout layout = new FormLayout("pref", "fill:pref:grow");
			setLayout(layout);
			CellConstraints cc = new CellConstraints();
			add(createToolbar(), cc.xy(1, 1));
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}

	/** Creates a button for our toolbar */
	private Component createPaletteButton(Icon icon, String cmd, String tooltip) {
		AbstractButton btn = new javax.swing.JToggleButton();
		Dimension d = new Dimension(20, 20);
		btn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				AbstractButton src = (AbstractButton) e.getSource();
				JPanel btn_panel = (JPanel) src.getParent();
				if (src.isSelected()) {
					btn_panel.setBackground(java.awt.Color.lightGray);
					btn_panel.setBorder(BorderFactory.createLineBorder(java.awt.Color.gray));
				}
				else {
					btn_panel.setBackground(javax.swing.UIManager.getColor("control"));
					btn_panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
				}
			}
		});

		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setPreferredSize(d);
		btn.setMinimumSize(d);
		btn.setSize(d);
		btn.setBorderPainted(false);
		btn.setIcon(icon);
		btn.setActionCommand(cmd);
		btn.setName(cmd);
		if (tooltip != null)
			btn.setToolTipText(tooltip);
		return btn;
	}

	/**
	 * Creates the java beans palette toolbar.
	 * 
	 */
	private Container createToolbar() {

		LinkedList button_list = new LinkedList();
		button_list.addAll(registerDefaultBeans());
		button_list.addAll(registerImportedBeans());

		StringBuffer colspec = new StringBuffer();
		StringBuffer rowspec = new StringBuffer();

		int cols = button_list.size() / MAX_TOOLBAR_ROWS + (button_list.size() % MAX_TOOLBAR_ROWS == 0 ? 0 : 1);
		int rows = Math.min(button_list.size(), MAX_TOOLBAR_ROWS);
		for (int col = 1; col <= cols; col++) {
			if (col > 1)
				colspec.append(",");

			colspec.append("pref");
		}

		for (int row = 1; row <= rows; row++) {
			if (row > 1)
				rowspec.append(",");

			rowspec.append("pref");
		}

		ButtonGroup bgroup = new ButtonGroup();

		JETAPanel toolbar = new JETAPanel();
		toolbar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 4));
		FormLayout layout = new FormLayout(colspec.toString(), rowspec.toString());
		CellConstraints cc = new CellConstraints();
		toolbar.setLayout(layout);

		int row_count = button_list.size() / cols + (button_list.size() % cols == 0 ? 0 : 1);
		Iterator iter = button_list.iterator();
		for (int col = 1; col <= cols; col++) {
			for (int row = 1; row <= row_count; row++) {
				if (iter.hasNext()) {
					Component btn = (Component) iter.next();
					bgroup.add((AbstractButton) btn);
					JPanel btn_panel = new JPanel(new BorderLayout());
					btn_panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
					btn_panel.add(btn, BorderLayout.CENTER);
					toolbar.add(btn_panel, cc.xy(col, row));
				}
				else {
					break;
				}
			}
		}
		layout.insertRow(1, new RowSpec("5px"));
		toolbar.setController(new ToolbarController(toolbar, button_list));

		assert (!iter.hasNext());
		return toolbar;
	}

	/** ComponentSource imlementation */
	public ComponentFactory getComponentFactory() {
		if (!isSelectionTool()) {
			String toolname = getCurrentTool();
			return (ComponentFactory) m_factories.get(toolname);
		}
		return null;
	}

	/** @return the classname of the currently selected tool in the tool palette. */
	public String getCurrentTool() {
		return m_current_tool;
	}

	/** ComponentSource imlementation */
	public boolean isSelectionTool() {
		return (m_current_tool == ID_SELECTION_TOOL);
	}

	/**
	 * Registers all default bean factories
	 */
	private Collection registerDefaultBeans() {
		try {
			LinkedList btns = new LinkedList();

			btns.add(registerBean(ID_SELECTION_TOOL, I18N.getLocalizedMessage("Selection Tool"), null, Icons.MOUSE_16));

			DefaultBeanManager bm = (DefaultBeanManager) JETARegistry.lookup(DefaultBeanManager.COMPONENT_ID);
			if (bm == null)
				return btns;

			boolean add_forms = true;
			Collection default_beans = bm.getDefaultBeans();
			Iterator iter = default_beans.iterator();
			while (iter.hasNext()) {
				RegisteredBean rbean = (RegisteredBean) iter.next();
				Icon icon = rbean.getIcon();
				if (icon == null) {
					icon = FormDesignerUtils.loadImage(Icons.BEAN_16);
				}

				String bname = rbean.getClassName();
				try {

					/**
					 * insert the form beans just before the horizontal line
					 * component - legacy reasons
					 */
					if (HorizontalLineComponent.class.getName().equals(bname) && add_forms) {
						btns.add(registerBean(ID_EMBEDDED_FORM_COMPONENT, I18N.getLocalizedMessage("Embedded Form"), new EmbeddedFormComponentFactory(this),
								Icons.EMBEDDED_FORM_16));

						btns.add(registerBean(ID_LINKED_FORM_COMPONENT, I18N.getLocalizedMessage("LinkedForm"), new LinkedFormComponentFactory(this),
								Icons.LINKED_FORM_16));

						btns.add(registerBean(ID_GENERIC_COMPONENT, I18N.getLocalizedMessage("Generic Component"), new GenericComponentFactory(this),
								Icons.GENERIC_COMPONENT_16));

						add_forms = false;
					}

					Class bean_class = bm.getBeanClass(bname);
					btns.add(registerBean(bname, rbean.getDescription(), new SwingComponentFactory(this, bname), icon));
				} catch (Exception e) {
					FormsLogger.debug(e);
				}
			}
			return btns;
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
		return EmptyCollection.getInstance();

	}

	/**
	 * Registers all imported java beans
	 */
	private Collection registerImportedBeans() {
		try {
			LinkedList btns = new LinkedList();
			BeanManager bm = (BeanManager) JETARegistry.lookup(DefaultBeanManager.COMPONENT_ID);
			if (bm == null)
				return btns;

			Collection imported_beans = bm.getImportedBeans();
			Iterator iter = imported_beans.iterator();
			while (iter.hasNext()) {
				RegisteredBean rbean = (RegisteredBean) iter.next();
				Icon icon = rbean.getIcon();
				if (icon == null) {
					icon = FormDesignerUtils.loadImage(Icons.BEAN_16);
				}

				String bname = rbean.getClassName();
				try {
					Class bean_class = bm.getBeanClass(bname);
					btns.add(registerBean(bname, rbean.getDescription(), new SwingComponentFactory(this, bname), icon));
				} catch (Exception e) {
					FormsLogger.debug(e);
				}
			}
			return btns;
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
		return EmptyCollection.getInstance();
	}

	/**
	 * Registers a Java bean for this toolbar
	 */
	public Component registerBean(String toolName, String tooltip, ComponentFactory factory, String imageName) {
		return registerBean(toolName, tooltip, factory, FormDesignerUtils.loadImage(imageName));
	}

	/**
	 * Registers a Java bean for this toolbar
	 */
	public Component registerBean(String toolName, String tooltip, ComponentFactory factory, Icon icon) {
		if (factory != null) {
			if (m_factories.get(toolName) != null) {
				System.out.println("ComponentsToolbar.registerBean:  factory registered twice: " + toolName);
			}
			m_factories.put(toolName, factory);
		}
		return createPaletteButton(icon, toolName, tooltip);
	}

	/**
	 * Reloads the toolbar
	 */
	public void reload() {
		m_factories.clear();
		removeAll();
		CellConstraints cc = new CellConstraints();
		add(createToolbar(), cc.xy(1, 1));
		revalidate();
	}

	/**
	 * ComponentSource implementation Sets the active component factory to the
	 * selection tool
	 */
	public void setSelectionTool() {
		AbstractButton btn = (AbstractButton) getComponentByName(ID_SELECTION_TOOL);
		btn.setSelected(true);
		m_current_tool = ID_SELECTION_TOOL;
	}

	/** The controller for this frame */
	public class ToolbarController extends JETAController {
		public ToolbarController(JETAPanel view, Collection btns) {
			super(view);

			Iterator iter = btns.iterator();
			while (iter.hasNext()) {
				Component comp = (Component) iter.next();
				if (comp instanceof AbstractButton) {
					((AbstractButton) comp).addActionListener(new StandardComponentAction(comp.getName()));
				}
			}
		}
	}

	/** ActionHandler for standard Swing components */
	public class StandardComponentAction implements ActionListener {
		private String m_compname;

		public StandardComponentAction(String compName) {
			m_compname = compName;
		}

		public void actionPerformed(ActionEvent evt) {
			m_current_tool = m_compname;
		}
	}

}
