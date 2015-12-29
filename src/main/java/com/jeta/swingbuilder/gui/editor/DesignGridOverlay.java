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

package com.jeta.swingbuilder.gui.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridCellListener;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridOverlay;
import com.jeta.forms.gui.form.GridView;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.resources.ResourceLoader;

/**
 * This component renders the grid lines on a form in the designer. This class
 * is also responsible for drawing the blue selection rectangle for a selected
 * component in the designer.
 * 
 * @author Jeff Tassin
 */
public class DesignGridOverlay extends JPanel implements GridCellListener, GridOverlay {
	/**
	 * The view associated with this overlay
	 */
	private GridView m_view;

	/**
	 * The color of the grid lines
	 */
	private Color m_grid_color;

	/**
	 * Line used to indicate resize of row or column
	 */
	private ResizeIndicator m_resize_indicator;

	/**
	 * The selection rectangle inner color
	 */
	private static Color m_sel_inner_color = new Color(241, 240, 227);

	/**
	 * The selection rectangle color
	 */
	private static Color m_sel_color = Color.blue;

	/**
	 * Flag that indicates if the grid is visible or not. We don't use the
	 * setVisible/isVisible method of Component because the selected cell won't
	 * be repainted.
	 */
	private boolean m_grid_visible = true;

	/**
	 * Icon for showing if a component is valid or not
	 */
	private static ImageIcon m_invalid_image;

	static {
		try {
			ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
			m_invalid_image = loader.loadImage("forms/invalid_comp.gif");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>GridOverlay</code> instance.
	 * 
	 * @param view
	 *            the GridView that contains this overlay.
	 */
	public DesignGridOverlay(GridView view) {
		m_view = view;
		m_grid_color = javax.swing.UIManager.getColor("control").darker();
		setOpaque(false);
	}

	/** GridCellListener implementation */
	public void cellChanged(GridCellEvent evt) {
	}

	public FormComponent getForm() {
		return m_view.getParentForm();
	}

	/**
	 * @return the grid cell that contains the given mouse point
	 */
	public GridComponent getCell(Point mousePt) {
		// @todo change to faster algorigthm if possible
		for (int index = 0; index < m_view.getGridComponentCount(); index++) {
			GridComponent gc = (GridComponent) m_view.getGridComponent(index);
			if (mousePt.x >= gc.getCellX() && (mousePt.x < gc.getCellX() + gc.getCellWidth()) && mousePt.y >= gc.getCellY()
					&& (mousePt.y < gc.getCellY() + gc.getCellHeight())) {
				return gc;
			}
		}
		return null;
	}

	/**
	 * @return the flag that indicates if the grid is visible or not.
	 */
	public boolean isGridVisible() {
		return m_grid_visible;
	}

	/**
	 * Paints a small icon on the grid cell if the GridComponent is too small to
	 * be seen
	 */
	private void paintInvalidIcon(Graphics g, GridComponent gc) {
		java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
		g2.drawImage(m_invalid_image.getImage(), gc.getCellX() + 2, gc.getCellY() + 2, gc);
	}

	/**
	 * Repaints the region occupied by the given component
	 */
	public void repaint(GridComponent gc) {
		repaint(gc.getCellX(), gc.getCellY(), gc.getCellWidth() + 1, gc.getCellHeight() + 1);
	}

	/**
	 * Shows/hides the grid
	 */
	public void setGridVisible(boolean bvis) {
		m_grid_visible = bvis;
		repaint();
	}

	void setResizeIndicator(ResizeIndicator indicator) {
		m_resize_indicator = indicator;
	}

	/**
	 * Override updateUI so we can update the grid lines color. It should be one
	 * shade darker than the current component background color. This method is
	 * invoked when the look and feel changes in the designer.
	 */
	public void updateUI() {
		super.updateUI();
		if (m_grid_color != null) {
			m_grid_color = javax.swing.UIManager.getColor("control").darker();
		}
		setBackground(javax.swing.UIManager.getColor("control"));
	}

	private Rectangle m_gc_rect = new Rectangle();
	private Rectangle m_clip_rect = new Rectangle();

	/**
	 * Paint routine that renders the grid lines. Only renders the grid lines
	 * that are contained in the clipping rectangle.
	 */
	public void paintComponent(Graphics g) {
		Color old_c = g.getColor();
		g.setColor(m_grid_color);

		/**
		 * we need to increase the height of the clip rectangle by 2 pixels
		 * because the bottom line of the grid overlay is not painted for
		 * composite child views in some cases
		 */
		Rectangle clip_rect = g.getClipBounds();

		m_clip_rect.setBounds(clip_rect.x - 1, clip_rect.y - 1, clip_rect.width + 2, clip_rect.height + 2);
		g.setClip(m_clip_rect.x, m_clip_rect.y, m_clip_rect.width, m_clip_rect.height);
		int clip_x1 = (int) m_clip_rect.x;
		int clip_x2 = (int) m_clip_rect.x + m_clip_rect.width;
		int clip_y1 = (int) m_clip_rect.y;
		int clip_y2 = (int) m_clip_rect.y + m_clip_rect.height;

		/** now find the cells that need to be repainted */
		int min_row = -1;
		int max_row = -1;

		int min_col = -1;
		int max_col = -1;

		for (int row = 1; row <= m_view.getRowCount(); row++) {
			int row_y1 = m_view.getRowOrgY(row);
			int row_y2 = row_y1 + m_view.getRowHeight(row);
			if (clip_y1 >= row_y1 && clip_y1 <= row_y2) {
				if (min_row < 0)
					min_row = row;
				else
					max_row = row;
			}
			else if (clip_y2 >= row_y1 && clip_y2 <= row_y2) {
				if (min_row < 0)
					min_row = row;
				else
					max_row = row;

			}
			else if (row_y1 >= clip_y1 && row_y2 <= clip_y2) {
				// here, the row is contained entirely in the clip
				if (min_row < 0)
					min_row = row;
				else
					max_row = row;
			}
		}

		for (int col = 1; col <= m_view.getColumnCount(); col++) {
			int col_x1 = m_view.getColumnOrgX(col);
			int col_x2 = col_x1 + m_view.getColumnWidth(col);
			if (clip_x1 >= col_x1 && clip_x1 <= col_x2) {
				if (min_col < 0)
					min_col = col;
				else
					max_col = col;
			}
			else if (clip_x2 >= col_x1 && clip_x2 <= col_x2) {
				if (min_col < 0)
					min_col = col;
				else
					max_col = col;

			}
			else if (col_x1 >= clip_x1 && col_x2 <= clip_x2) {
				// here, the col is contained entirely in the clip
				if (min_col < 0)
					min_col = col;
				else
					max_col = col;
			}
		}

		if (min_row < 0 || min_col < 0)
			return;

		if (max_row < 0)
			max_row = min_row;
		if (max_col < 0)
			max_col = min_col;

		if (isGridVisible()) {
			for (int row = min_row; row <= max_row; row++) {
				for (int col = min_col; col <= max_col; col++) {
					GridComponent gc = (GridComponent) m_view.getGridComponent(col, row);
					if (gc != null && !gc.isSelected()) {
						GridComponent overlap = m_view.getOverlappingComponent(gc.getColumn(), gc.getRow());
						if (overlap == null) {
							m_gc_rect.setBounds(gc.getCellX() - 1, gc.getCellY() - 1, gc.getCellWidth() + 2, gc.getCellHeight() + 2);
							if (m_gc_rect.intersects(m_clip_rect)) {
								g.drawRect(gc.getCellX(), gc.getCellY(), gc.getCellWidth(), gc.getCellHeight());
							}
						}
					}
				}
			}
		}

		/**
		 * Now draw the selection rectangle.
		 */
		for (int row = min_row; row <= max_row; row++) {
			for (int col = min_col; col <= max_col; col++) {
				GridComponent gc = (GridComponent) m_view.getGridComponent(col, row);
				if (gc == null || !gc.isSelected())
					gc = m_view.getOverlappingComponent(col, row);

				if (gc != null) {
					if (gc.isSelected()) {
						int gx = gc.getCellX();
						int gy = gc.getCellY();
						int gwidth = gc.getCellWidth();
						int gheight = gc.getCellHeight();

						m_gc_rect.setBounds(gx, gy, gwidth, gheight);
						if (m_gc_rect.intersects(m_clip_rect)) {
							g.setColor(m_sel_color);
							g.drawRect(gx, gy, gwidth, gheight);
							g.setColor(m_sel_inner_color);
							if ((gwidth > 2) && (gheight > 2)) {
								g.drawRect(gx + 1, gy + 1, gwidth - 2, gheight - 2);
							}
						}
					}
					if (!gc.isShowing()) {
						paintInvalidIcon(g, gc);
					}
				}
			}
		}
		g.setColor(old_c);
		g.setClip(clip_rect.x, clip_rect.y, clip_rect.width, clip_rect.height);

		if (m_resize_indicator != null) {
			m_resize_indicator.paint(g);
		}
	}

}
