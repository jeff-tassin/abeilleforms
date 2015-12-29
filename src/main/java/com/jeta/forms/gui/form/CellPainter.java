/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.gui.form;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.store.properties.effects.PaintProperty;

/**
 * This class is a Swing component that paints fill effects for cells in the
 * GridView. It iterates over all invalid cells (i.e. needing repaint) and
 * paints those cells if they have an assigned painter object. Examples of fill
 * effects are: solid, texture, linear gradient, and radial gradient. See:
 * {@link com.jeta.forms.gui.effects.Painter}
 * 
 * @author Jeff Tassin
 */
public class CellPainter extends JPanel {
	/**
	 * The view associated with this grid painter.
	 */
	private GridView m_view;

	/**
	 * The parent form for the GridView
	 */
	private FormComponent m_form;

	/**
	 * We keep a rectangle around so we don't have to instantiate with every
	 * paint.
	 */
	private Rectangle m_gc_rect = new Rectangle();

	/**
	 * Creates a <code>CellPainter</code> associated with the given view.
	 * 
	 * @param view
	 *            the GridView associated with this cell painter.
	 */
	public CellPainter(GridView view) {
		m_view = view;
		setOpaque(false);
	}

	/**
	 * Override paintComponent so can render the fill effects for each cell that
	 * needs it.
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Color old_c = g2.getColor();
		Paint old_paint = g2.getPaint();

		/**
		 * we need to increase the height of the clip rectangle by 2 pixels
		 * because the bottom line of the grid overlay is not painted for
		 * composite child views in some cases
		 */
		Rectangle clip_rect = g.getClipBounds();
		clip_rect.setBounds(clip_rect.x, clip_rect.y, clip_rect.width + 2, clip_rect.height + 2);
		g.setClip(clip_rect.x, clip_rect.y, clip_rect.width, clip_rect.height);
		int clip_x1 = (int) clip_rect.x;
		int clip_x2 = (int) clip_rect.x + clip_rect.width;
		int clip_y1 = (int) clip_rect.y;
		int clip_y2 = (int) clip_rect.y + clip_rect.height;

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

		for (int row = min_row; row <= max_row; row++) {
			for (int col = min_col; col <= max_col; col++) {
				PaintProperty pp = m_view.getPaintProperty(col, row);
				if (pp != null) {
					Painter painter = pp.createPainter();
					if (painter != null) {
						GridComponent gc = m_view.getGridComponent(col, row);
						if (gc == null) {
							m_gc_rect.setBounds(m_view.getColumnOrgX(col), m_view.getRowOrgY(row), m_view.getColumnWidth(col), m_view.getRowHeight(row));
						}
						else {
							m_gc_rect.setBounds(gc.getCellX(), gc.getCellY(), gc.getCellWidth(), gc.getCellHeight());
						}

						if (m_gc_rect.intersects(clip_rect)) {
							painter.paint(this, g, m_gc_rect);
						}
					}
				}
			}
		}
		g2.setColor(old_c);
		g2.setPaint(old_paint);
	}

}
