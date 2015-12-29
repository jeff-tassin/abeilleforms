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

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.editor.FormEditor;

/**
 * Displays the forms hierarhcy in a JTree for a given editor. This is only used
 * during debugging
 * 
 * @author Jeff Tassin
 */
public class FormsTree extends JTree {

	public FormsTree() {
		setShowsRootHandles(true);
		putClientProperty("JTree.lineStyle", "Angled");
		setCellRenderer(new FormsTreeRenderer());
		reload();
	}

	public void reload() {
		setRootVisible(false);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel model = new DefaultTreeModel(root);

		HashMap nodes = new HashMap();
		FormManager fm = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		Collection form_ids = fm.getForms();
		Iterator iter = form_ids.iterator();
		while (iter.hasNext()) {
			String form_id = (String) iter.next();
			FormComponent fc = fm.getForm(form_id);

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fc);
			if (fc.getParentForm() == null)
				root.add(node);

			nodes.put(form_id, node);
		}

		iter = nodes.keySet().iterator();
		while (iter.hasNext()) {
			String form_id = (String) iter.next();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.get(form_id);
			FormComponent fc = (FormComponent) node.getUserObject();
			FormComponent parent = fc.getParentForm();
			if (parent != null) {
				DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) nodes.get(parent.getId());
				assert (parent_node != null);
				parent_node.add(node);
			}
		}

		EditorManager emgr = (EditorManager) JETARegistry.lookup(EditorManager.COMPONENT_ID);
		if (emgr != null) {
			Collection editors = emgr.getEditors();
			iter = editors.iterator();
			while (iter.hasNext()) {
				FormEditor formeditor = (FormEditor) iter.next();
				FormComponent top = formeditor.getTopParent();
				// todo more
			}
		}
		setModel(model);
		expandNode(root, true);
	}

	/**
	 * Expands a given node.
	 * 
	 * @param parentNode
	 *            the node to expand
	 * @param bRecursive
	 *            set to true if you want to expand all descendent nodes as well
	 */
	public void expandNode(DefaultMutableTreeNode parentNode, boolean bRecursive) {
		if (parentNode != null) {
			expandPath(new TreePath(parentNode.getPath()));
			if (bRecursive) {
				for (Enumeration e = parentNode.children(); e.hasMoreElements();) {
					DefaultMutableTreeNode childnode = (DefaultMutableTreeNode) e.nextElement();
					expandNode(childnode, bRecursive);
				}
			}
		}
	}

}
