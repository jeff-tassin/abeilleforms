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

package com.jeta.swingbuilder.gui.properties.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jeta.open.gui.components.BasicPopupMenu;
import com.jeta.open.gui.components.JETAComponentNames;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.gui.framework.UIDirector;
import com.jeta.open.i18n.I18N;
import com.jeta.open.support.CompositeComponentFinder;
import com.jeta.open.support.DefaultComponentFinder;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;

/**
 * This class displays a panel that allows a user to add and arrange String
 * objects in a collection.
 * 
 * @author Jeff Tassin
 */
public class ItemsPanel extends JETAPanel {
	public static final String ID_ADD_ITEM = "add.item";
	public static final String ID_EDIT_ITEM = "edit.item";
	public static final String ID_DELETE_ITEM = "delete.item";
	public static final String ID_MOVE_UP = "move.up";
	public static final String ID_MOVE_DOWN = "move.down";

	private JList m_list;
	private DefaultListModel m_model;
	private BasicPopupMenu m_copy_popup;

	/**
	 * ctor
	 */
	public ItemsPanel() {
		this(null);
	}

	/**
	 * ctor
	 */
	public ItemsPanel(Collection items) {
		super(new CompositeComponentFinder());

		m_copy_popup = new BasicPopupMenu();

		CompositeComponentFinder finder = (CompositeComponentFinder) getComponentFinder();
		finder.add(new DefaultComponentFinder(this));
		finder.add(new DefaultComponentFinder(m_copy_popup));

		setLayout(new BorderLayout());
		add(createToolBar(), BorderLayout.NORTH);
		add(createList(), BorderLayout.CENTER);
		setController(new ItemsPanelController(this, m_copy_popup));

		if (items != null) {
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				m_model.addElement(iter.next());
			}
		}
	}

	/**
	 * Creates a button for the toolbar
	 */
	private Component _createButton(String imageName, String cmdId) {
		JButton btn = new JButton(FormDesignerUtils.loadImage(imageName));
		btn.setActionCommand(cmdId);
		btn.setName(cmdId);
		Dimension d = new Dimension(24, 24);
		btn.setPreferredSize(d);
		btn.setMaximumSize(d);
		return btn;
	}

	/**
	 * Creates the toolbar at the top of the panel
	 */
	private Component createToolBar() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		panel.add(_createButton(Icons.NEW_16, ID_ADD_ITEM));
		panel.add(_createButton(Icons.EDIT_16, ID_EDIT_ITEM));
		panel.add(_createButton(Icons.DELETE_16, ID_DELETE_ITEM));
		panel.add(_createButton(Icons.UP_16, ID_MOVE_UP));
		panel.add(_createButton(Icons.DOWN_16, ID_MOVE_DOWN));
		return panel;
	}

	/**
	 * Creates the list that displays the items in the collection we are
	 * editing.
	 */
	private Component createList() {
		m_list = new JList();
		m_model = new DefaultListModel();
		m_list.setModel(m_model);
		JScrollPane scroll = new JScrollPane(m_list);
		return scroll;
	}

	/**
	 * @return the items in the list
	 */
	public Collection getItems() {
		return java.util.Collections.list(m_model.elements());
	}

	/**
	 * @return the underlying list
	 */
	public JList getList() {
		return m_list;
	}

	/**
	 * @return the underlying list model
	 */
	public DefaultListModel getListModel() {
		return m_model;
	}

	/**
	 * @return the preferred size for this component
	 */
	public Dimension getPreferredSize() {
		return FormDesignerUtils.getWindowDimension(this, 175, 144);
	}

	/**
	 * Controller for this panel
	 */
	public static class ItemsPanelController extends JETAController {
		/**
		 * The panel we are controlling
		 */
		private ItemsPanel m_panel;

		private BasicPopupMenu m_copy_popup;

		/**
		 * ctor
		 */
		public ItemsPanelController(ItemsPanel panel, BasicPopupMenu popup) {
			super(panel);
			m_panel = panel;
			m_copy_popup = popup;

			assignAction(ItemsPanel.ID_ADD_ITEM, new AddItemAction());
			assignAction(ItemsPanel.ID_EDIT_ITEM, new EditItemAction());
			assignAction(ItemsPanel.ID_DELETE_ITEM, new DeleteItemAction());
			assignAction(ItemsPanel.ID_MOVE_UP, new MoveUpAction());
			assignAction(ItemsPanel.ID_MOVE_DOWN, new MoveDownAction());

			assignAction(JETAComponentNames.ID_CUT, new CutAction());
			assignAction(JETAComponentNames.ID_COPY, new CopyAction());
			assignAction(JETAComponentNames.ID_PASTE, new PasteAction());
			// setUIDirector( new ItemsPanelUIDirector( m_panel ) );

			m_panel.getList().addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						m_copy_popup.show(m_panel.getList(), evt.getX(), evt.getY());
					}
				}
			});
		}

		void ensureIndexIsVisible() {
			JList list = m_panel.getList();
			int index = list.getSelectedIndex();
			if (index >= 0)
				list.ensureIndexIsVisible(index);
		}

		public class AddItemAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				String item = JOptionPane.showInputDialog(I18N.getLocalizedMessage("Enter New Item"));
				if (item != null) {
					JList list = m_panel.getList();
					int index = list.getSelectedIndex();
					DefaultListModel model = m_panel.getListModel();
					if (index >= 0 && index < (model.size() - 1)) {
						model.insertElementAt(item, index + 1);
						list.setSelectedIndex(index + 1);
					}
					else {
						model.addElement(item);
						list.setSelectedIndex(model.size() - 1);
					}
				}
				ensureIndexIsVisible();
			}
		}

		public class CutAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				invokeAction(JETAComponentNames.ID_COPY);
				invokeAction(ItemsPanel.ID_DELETE_ITEM);
			}
		}

		public class CopyAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				StringBuffer sbuff = new StringBuffer();
				JList list = m_panel.getList();
				DefaultListModel model = (DefaultListModel) list.getModel();
				int[] indexes = list.getSelectedIndices();
				for (int i = 0; i < indexes.length; i++) {
					sbuff.append(model.elementAt(indexes[i]));
					if ((i + 1) < indexes.length)
						sbuff.append("\n");
				}

				Toolkit kit = Toolkit.getDefaultToolkit();
				Clipboard clipboard = kit.getSystemClipboard();
				StringSelection transferable = new StringSelection(sbuff.toString());
				clipboard.setContents(transferable, null);
			}
		}

		public class PasteAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				try {
					JList list = m_panel.getList();
					DefaultListModel model = (DefaultListModel) list.getModel();
					int index = list.getSelectedIndex();
					if (index < 0)
						index = 0;

					Toolkit kit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = kit.getSystemClipboard();
					Transferable transferable = clipboard.getContents(null);
					if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						String sval = (String) transferable.getTransferData(DataFlavor.stringFlavor);
						StringTokenizer st = new StringTokenizer(sval, "\t\n\r\f");
						while (st.hasMoreTokens()) {
							String token = st.nextToken();
							model.insertElementAt(token, index);
							index++;
						}
					}
				} catch (Exception e) {

				}
			}
		}

		public class EditItemAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				JList list = m_panel.getList();
				int index = list.getSelectedIndex();
				if (index >= 0) {
					DefaultListModel model = m_panel.getListModel();
					String item = JOptionPane.showInputDialog(I18N.getLocalizedMessage("Enter New Item"), model.elementAt(index));
					if (item != null) {
						model.setElementAt(item, index);
					}
				}
				ensureIndexIsVisible();

			}
		}

		public class DeleteItemAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				LinkedList del_items = new LinkedList();
				JList list = m_panel.getList();
				int[] indexes = list.getSelectedIndices();
				DefaultListModel model = m_panel.getListModel();
				for (int i = 0; i < indexes.length; i++) {
					del_items.add(model.elementAt(indexes[i]));
				}

				Iterator iter = del_items.iterator();
				while (iter.hasNext()) {
					model.removeElement(iter.next());
				}
				int index = indexes[0];
				index--;
				if (index < 0)
					index = 0;
				if (model.size() > 0)
					list.setSelectedIndex(index);
				ensureIndexIsVisible();
			}
		}

		public class MoveUpAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				JList list = m_panel.getList();
				DefaultListModel model = m_panel.getListModel();
				int index = list.getSelectedIndex();
				System.out.println("MoveUP action  index: " + index);

				if (index > 0 && model.size() > 1) {
					Object mv_obj = model.getElementAt(index);
					model.removeElementAt(index);
					model.add(index - 1, mv_obj);
					list.setSelectedIndex(index - 1);
				}
				ensureIndexIsVisible();

			}
		}

		public class MoveDownAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				JList list = m_panel.getList();
				DefaultListModel model = m_panel.getListModel();
				int index = list.getSelectedIndex();
				if ((index + 1) < model.size()) {
					Object mv_obj = model.getElementAt(index);
					model.removeElementAt(index);
					if ((index + 1) == model.size()) {
						model.addElement(mv_obj);
					}
					else {
						model.add(index + 1, mv_obj);
					}
					list.setSelectedIndex(index + 1);
				}
				ensureIndexIsVisible();

			}
		}
	}

	public static class ItemsPanelUIDirector implements UIDirector {
		/**
		 * The panel we are controlling
		 */
		private ItemsPanel m_panel;

		/**
		 * ctor
		 */
		public ItemsPanelUIDirector(ItemsPanel panel) {
			m_panel = panel;
		}

		public void updateComponents(java.util.EventObject evt) {

		}
	}

}
