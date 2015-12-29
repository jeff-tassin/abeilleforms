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

package com.jeta.forms.components.border;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ComponentUI;

/**
 * The UI for a component that simulates the top part of a TitledBorder. This
 * component is useful when flattening layouts. It allows a titled border to be
 * used to separate components, while allowing those components to stay in the
 * same row or column of the form. This allows components to have the same x,y,
 * width, or height, yet have differnt title borders. This is not possible when
 * using standard TitledBorder.
 * 
 * @author Jeff Tassin
 */
public class TitledBorderLabelUI extends ComponentUI {
	static protected final int VERTICAL_PADDING = 2;

	/**
	 * Determines the intersection of two rectangles. The first rectangle is
	 * specified by a Rectangle object. The second rectangle is specified by x,
	 * y, width, and height parameters. The first rectangle is set to the
	 * resulting intersection if any.
	 * 
	 * @return true if the rectangles intersect.
	 */
	private boolean calcIntersection(Rectangle rect, int x, int y, int width, int height) {
		int x1 = Math.max(x, rect.x);
		int y1 = Math.max(y, rect.y);

		int x2 = Math.min(x + width, rect.x + rect.width);
		int y2 = Math.min(y + height, rect.y + rect.height);

		rect.setBounds(x1, y1, x2 - x1, y2 - y1);

		if (rect.width <= 0 || rect.height <= 0)
			return false;
		else
			return true;
	}

	/**
	 * Calculates the preferred size for a TitledBorderLabel
	 */
	public Dimension getPreferredSize(JComponent c) {
		if (c instanceof TitledBorderLabel) {
			TitledBorderLabel label = (TitledBorderLabel) c;
			String title = label.getText();
			if (title != null) {
				Font font = label.getFont();
				FontMetrics metrics = label.getFontMetrics(font);
				/** add 4 pixels to line height for padding */
				int line_height = metrics.getHeight() + 4;
				/** add 10 pixels to label width for padding */
				int line_width = metrics.stringWidth(title) + 10;
				return new Dimension(line_width, line_height);
			}
		}
		return new Dimension(10, 10);
	}

	/**
	 * Renders the border and label using the current look and feel.
	 * 
	 * @param g
	 *            the <code>Graphics</code> context in which to paint
	 * @param c
	 *            the component being painted;
	 */
	public void paint(Graphics g, JComponent c) {
		Border border = UIManager.getBorder("TitledBorder.border");
		if (c instanceof TitledBorderLabel && border != null) {
			TitledBorderLabel bordercomp = (TitledBorderLabel) c;
			String title = bordercomp.getText();
			if (title == null)
				title = "";

			int width = c.getWidth();
			int height = c.getHeight();

			Font oldfont = g.getFont();
			Color oldcolor = g.getColor();
			Insets insets = border.getBorderInsets(c);

			g.setFont(bordercomp.getFont());
			FontMetrics fm = g.getFontMetrics();
			int stringWidth = fm.stringWidth(title);

			int border_y = fm.getAscent() / 2 + VERTICAL_PADDING;
			int text_y = (border_y - fm.getDescent()) + (insets.top + fm.getAscent() + fm.getDescent()) / 2;
			int text_x = 0;

			int justification = bordercomp.getTitleJustification();
			if (c.getComponentOrientation().isLeftToRight()) {
				if (justification == TitledBorder.TRAILING)
					justification = TitledBorder.RIGHT;
			}
			else {
				if (justification == TitledBorder.LEADING || justification == TitledBorder.DEFAULT_JUSTIFICATION)
					justification = TitledBorder.RIGHT;
			}

			if (justification == TitledBorder.RIGHT)
				text_x = width - (stringWidth + insets.right + insets.left);
			else if (justification == TitledBorder.CENTER)
				text_x = (width - stringWidth) / 2;
			else
				text_x = insets.left;

			Rectangle clip = new Rectangle();
			Rectangle oldclip = g.getClipBounds();

			if (title.length() == 0) {
				/**
				 * paint the border at offsets of 12 pixels so we don't include
				 * the corners.
				 */
				border.paintBorder(c, g, -12, border_y, width + 24, height + 12);
			}
			else {
				clip.setBounds(oldclip);
				/** horizontal line to the left of title */
				if (calcIntersection(clip, 0, 0, text_x - 1, height)) {
					g.setClip(clip);
					/**
					 * paint the border at offsets of 12 pixels so we don't
					 * include the corners
					 */
					border.paintBorder(c, g, -12, border_y, width, height + 12);
				}

				clip.setBounds(oldclip);
				/** horizontal line to the right of title */
				if (calcIntersection(clip, text_x + stringWidth + 1, 0, width - (text_x + stringWidth + 1), height)) {
					g.setClip(clip);
					/**
					 * paint the border at offsets of 12 pixels so we don't
					 * include the corners
					 */
					border.paintBorder(c, g, 0, border_y, width + 12, height + 12);
				}

				/** restore the original clipping rectangle */
				g.setClip(oldclip);

				g.setColor(bordercomp.getTitleColor());
				g.drawString(title, text_x, text_y);
			}

			/** restore original font and color */
			g.setFont(oldfont);
			g.setColor(oldcolor);

		}
	}

}
