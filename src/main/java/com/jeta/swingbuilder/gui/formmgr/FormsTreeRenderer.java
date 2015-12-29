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
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * This is the renderer for objects in the FormsTree. This is only used during
 * debugging.
 * 
 * @author Jeff Tassin
 */
public class FormsTreeRenderer extends JLabel implements TreeCellRenderer {
	private static ImageIcon m_embedded_icon;
	private static ImageIcon m_linked_icon;

	static {
		m_embedded_icon = FormDesignerUtils.loadImage("forms/form_control_embedded.gif");
		m_linked_icon = FormDesignerUtils.loadImage("forms/form_control_linked.gif");
	}

	/**
	 * ctor
	 */
	public FormsTreeRenderer() {
		super("");
		initialize();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		if (obj instanceof FormComponent) {
			FormComponent fc = (FormComponent) obj;

			if (sel) {
				setForeground(UIManager.getColor("Tree.selectionForeground"));
				setBackground(UIManager.getColor("Tree.selectionBackground"));
				setOpaque(true);
			}
			else {
				setBackground(UIManager.getColor("Tree.background"));
				setForeground(UIManager.getColor("Tree.foreground"));
				setOpaque(false);
			}

			if (fc.isEmbedded())
				setIcon(m_embedded_icon);
			else
				setIcon(m_linked_icon);

			setText(fc.getId());
		}
		else {
			if (obj == null)
				setText("null");
			else
				setText(obj.toString());
		}
		return this;
	}

	public void initialize() {
		setLayout(new FlowLayout());
		setFont(UIManager.getFont("Tree.font"));
	}

	public java.awt.Font getFont() {
		return UIManager.getFont("Tree.font");
	}

}
