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

package com.jeta.swingbuilder.gui.components.line;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.CompoundLineProperty;
import com.jeta.forms.store.properties.LineProperty;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * View that is used to create and edit lines
 * 
 * @author Jeff Tassin
 */
public class CompoundLineView extends JETAPanel {
	/**
	 * The actual view
	 */
	private FormPanel m_view;

	/**
	 * The list model that handles the borders
	 */
	private DefaultListModel m_lines_model;

	private int m_old_position = 0;

	/**
	 * ctor
	 */
	public CompoundLineView(CompoundLineProperty lines) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/components/line/compoundLineView.frm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		m_lines_model = new DefaultListModel();
		JList list = m_view.getList(CompoundLineNames.ID_LINES_LIST);
		list.setModel(m_lines_model);
		list.setCellRenderer(new LineCellRenderer());

		m_old_position = lines.getPosition();
		Iterator iter = lines.iterator();
		while (iter.hasNext()) {
			LineProperty bp = (LineProperty) iter.next();
			addLine(bp);
		}

		setController(new CompoundLineController(this));
	}

	/**
	 * Adds a border to the list
	 */
	public void addLine(LineProperty lp) {
		m_lines_model.addElement(new LineInfo(lp));
		updatePreview();
	}

	/**
	 * Ensure the selected border is visible in the list.
	 */
	void ensureIndexIsVisible() {
		JList list = m_view.getList(CompoundLineNames.ID_LINES_LIST);
		int index = list.getSelectedIndex();
		if (index >= 0)
			list.ensureIndexIsVisible(index);
	}

	/**
	 * Creates a LineProperty based on this view
	 */
	public CompoundLineProperty createLineProperty() {
		CompoundLineProperty prop = new CompoundLineProperty();
		prop.setPosition(m_old_position);
		for (int index = 0; index < m_lines_model.size(); index++) {
			LineInfo li = (LineInfo) m_lines_model.elementAt(index);
			LineProperty bp = li.getLineProperty();
			prop.addLine(bp);
		}
		return prop;
	}

	/**
	 * Deletes the selected lin
	 */
	public void deleteSelectedLine() {
		LineInfo li = (LineInfo) m_view.getList(CompoundLineNames.ID_LINES_LIST).getSelectedValue();
		if (li != null) {
			m_lines_model.removeElement(li);
			updatePreview();
		}
	}

	/**
	 * @return the selected border in the border list.
	 */
	public LineProperty getSelectedLine() {
		LineInfo li = (LineInfo) m_view.getList(CompoundLineNames.ID_LINES_LIST).getSelectedValue();
		if (li == null)
			return null;
		else
			return li.getLineProperty();
	}

	/**
	 * @return the current number of lines that make up this compound line
	 */
	public int getLineCount() {
		return m_lines_model.size();
	}

	/**
	 * Modifies a given border and sets it to a new border
	 */
	public void setLine(LineProperty new_prop, LineProperty old_prop) {
		for (int index = 0; index < m_lines_model.size(); index++) {
			LineInfo li = (LineInfo) m_lines_model.elementAt(index);
			if (li.getLineProperty() == old_prop) {
				m_lines_model.setElementAt(new LineInfo(new_prop), index);
				updatePreview();
				return;
			}
		}

		assert (false);
	}

	/**
	 * Creates a swing border based on the properties in this view and displays
	 * it.
	 */
	public void updatePreview() {
		/*
		 * LineComponent lcomp = (LineComponent)m_view.getComponentByName(
		 * CompoundLineNames.ID_LINE_COMPONENT ); lcomp.setLineDefinition(
		 * createLineProperty() ); revalidate(); doLayout(); lcomp.repaint();
		 */
	}

	public static class LineInfo {
		private LineProperty m_prop;
		private ImageIcon m_icon;
		private boolean m_selected = false;

		public LineInfo(LineProperty prop) {
			m_prop = prop;
		}

		public Icon getIcon(boolean selected) {
			if (m_icon == null || (m_selected != selected)) {
				m_selected = selected;
				int width = 100;
				int height = 20;
				BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D bg = bimage.createGraphics();

				if (selected)
					bg.setColor(UIManager.getColor("List.selectionBackground"));
				else
					bg.setColor(UIManager.getColor("List.background"));

				bg.fillRect(0, 0, width, height);
				ImageIcon line_icon = FormDesignerUtils.loadImage("forms/line_property.gif");
				bg.drawImage(line_icon.getImage(), 0, 4, bg.getColor(), null);

				java.awt.BasicStroke s = (java.awt.BasicStroke) m_prop.getStroke(16);
				// System.out.println( "CompoundLineView.getIcon stroke
				// thickness: " + s.getLineWidth() );

				bg.setColor(m_prop.getColor());
				bg.setStroke(s);
				bg.drawLine(20, height / 2, width, height / 2);
				bg.dispose();
				m_icon = new ImageIcon(bimage);
			}
			return m_icon;
		}

		public LineProperty getLineProperty() {
			return m_prop;
		}

		public void setLineProperty(LineProperty prop) {
			m_prop = prop;
			m_icon = null;
		}
	}

	/**
	 * Line Renderer
	 */
	public static class LineCellRenderer extends JLabel implements ListCellRenderer {
		public LineCellRenderer() {
			// must set or the background color won't show
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// LineProperty info = (LineProperty)value;
			LineInfo info = (LineInfo) value;
			if (info == null) {
				assert (false);
				setIcon(null);
			}
			else {
				setIcon(info.getIcon(isSelected));
			}

			if (isSelected) {
				setBackground(UIManager.getColor("List.selectionBackground"));
				setForeground(UIManager.getColor("List.selectionForeground"));
			}
			else {
				setBackground(UIManager.getColor("List.background"));
				setForeground(UIManager.getColor("List.foreground"));
			}
			return this;
		}
	}

}
