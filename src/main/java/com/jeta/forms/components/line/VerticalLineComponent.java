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

package com.jeta.forms.components.line;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.util.Iterator;

import com.jeta.forms.store.properties.LineProperty;

public class VerticalLineComponent extends LineComponent {
	public static final int CENTER = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;

	/**
	 * @return true if this line is oriented horizontally
	 */
	public boolean isHorizontal() {
		return false;
	}

	/**
	 * Paints the line
	 */
	public void paintComponent(Graphics g) {
		int x1 = 0;
		int y1 = 0, y2 = 0;
		Insets insets = getInsets();

		y1 = insets.top;
		int height = getHeight() - (insets.top + insets.bottom);
		if (height < 0)
			return;

		y2 = height + y1;

		int total_thickness = getThickness();
		if (getPosition() == CENTER) {
			x1 = (getWidth() - total_thickness) / 2;
		}
		else if (getPosition() == LEFT) {
			// x1 = total_thickness/2;

		}
		else {
			x1 = getWidth() - total_thickness;
		}

		Graphics2D g2 = (Graphics2D) g;
		Stroke old_stroke = g2.getStroke();
		Color old_color = g2.getColor();
		Iterator iter = iterator();
		while (iter.hasNext()) {
			LineProperty prop = (LineProperty) iter.next();
			Stroke s = prop.getStroke();
			g2.setStroke(s);
			g2.setColor(prop.getColor());

			int x = x1 + prop.getThickness() / 2;
			g2.drawLine(x, y1, x, y2);

			x1 = x1 + prop.getThickness();
		}

		g2.setColor(old_color);
		g2.setStroke(old_stroke);
	}

}
