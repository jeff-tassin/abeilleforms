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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.border.Border;

import com.jeta.forms.gui.effects.LinearGradientPainter;
import com.jeta.forms.gui.effects.RadialGradientPainter;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.ShadowBorderProperty;
import com.jeta.forms.store.properties.effects.GradientProperty;
import com.jeta.forms.store.properties.effects.RadialGradientProperty;

/**
 * This border paints a shadow on the right/bottom sides of a component. A
 * shadow border is painted only on the right and bottom sides of a component.
 * There are two types of shadows: solid and gradient. A gradient shadow changes
 * from a start color to an end color. The end color is normally the same color
 * as the background of the current form. This provides a fade effect for the
 * shadow. On OSX, the shadow border is less useful because the Aqua look and
 * feel uses a striped pattern for panel backgrounds. At some point in the
 * future, the shadow border will need to be updated to support a fade to
 * transparency.
 * 
 * @author Jeff Tassin
 */
public class ShadowBorder implements Border {
	/**
	 * The property that contains the attributes for this border
	 */
	private ShadowBorderProperty m_prop;

	/**
	 * The insets for the border
	 */
	private Insets m_insets;

	/**
	 * For gradient shadow borders only
	 */
	private LinearGradientPainter m_right_painter;
	private LinearGradientPainter m_bottom_painter;
	private RadialGradientPainter m_bottom_right_painter;
	private RadialGradientPainter m_top_right_painter;
	private RadialGradientPainter m_bottom_left_painter;
	private Rectangle m_rect;

	/**
	 * ctor needed for JavaBeans persistence.
	 */
	public ShadowBorder() {

	}

	/**
	 * ctor
	 */
	public ShadowBorder(int type, int thickness, ColorProperty startColor, ColorProperty endColor, boolean symmetric) {
		m_prop = new ShadowBorderProperty(type, thickness, startColor, endColor, symmetric);
		getBorderInsets(null);
	}

	/**
	 * ctor
	 */
	public ShadowBorder(ShadowBorderProperty prop) {
		m_prop = new ShadowBorderProperty();
		m_prop.setValue(prop);
		getBorderInsets(null);
	}

	/**
	 * @return the border insets
	 */
	public Insets getBorderInsets(Component c) {
		assert (m_prop != null);
		if (m_insets == null) {
			if (isSymmetric()) {
				m_insets = new Insets(m_prop.getThickness(), m_prop.getThickness(), m_prop.getThickness(), m_prop.getThickness());
			}
			else {
				m_insets = new Insets(0, 0, m_prop.getThickness(), m_prop.getThickness());
			}
		}
		return m_insets;
	}

	public boolean isSymmetric() {
		return m_prop.isSymmetric();
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public ColorProperty getStartColor() {
		return m_prop.getStartColor();
	}

	public ColorProperty getEndColor() {
		return m_prop.getEndColor();
	}

	public ShadowBorderProperty getBorderProperty() {
		return m_prop;
	}

	public void setBorderProperty(ShadowBorderProperty prop) {
		m_prop = prop;
		getBorderInsets(null);
		m_right_painter = null;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		if (m_prop.getType() == ShadowBorderProperty.SOLID) {
			int thickness = m_prop.getThickness();
			int rheight_offset = 0;
			int bwidth_offset = 0;

			if (thickness < 8) {
				rheight_offset = 1;
				bwidth_offset = 1;
			}

			if (isSymmetric()) {
				g.setColor(m_prop.getEndColor().getColor());
				rheight_offset += thickness;
				bwidth_offset += thickness;

				/** clear the top and left sides */
				g.fillRect(x, y, width + thickness, thickness);
				g.fillRect(x, y, thickness, height + thickness);
			}

			/** render the shadow offsets */
			g.setColor(m_prop.getEndColor().getColor());
			/**
			 * right shadown offset
			 */
			g.fillRect(x + width - thickness, y, thickness, thickness + rheight_offset);
			/**
			 * bottom shadown offset
			 */
			g.fillRect(x, y + height - thickness, thickness + bwidth_offset, thickness);

			/** render the shadow */
			g.setColor(m_prop.getStartColor().getColor());
			/**
			 * right shadow
			 */
			g.fillRect(x + width - thickness, y + thickness + rheight_offset, thickness, height);
			/**
			 * bottom shadow
			 */
			g.fillRect(x + thickness + bwidth_offset, y + height - thickness, width, thickness);
		}
		else {
			paintGradient(g, x, y, width, height);
		}
	}

	/**
	 * Renders this border as a gradient
	 */
	public void paintGradient(Graphics g, int x, int y, int width, int height) {
		if (m_right_painter == null) {
			m_right_painter = new LinearGradientPainter(new GradientProperty(getStartColor(), getEndColor(), GradientProperty.LEFT_RIGHT));

			m_bottom_painter = new LinearGradientPainter(new GradientProperty(getStartColor(), getEndColor(), GradientProperty.TOP_BOTTOM));

			m_bottom_right_painter = new RadialGradientPainter(new RadialGradientProperty(getStartColor(), getEndColor(), RadialGradientProperty.TOP_LEFT, 200));
			m_bottom_right_painter.setRadiusType(RadialGradientPainter.WIDTH_BASED);

			m_top_right_painter = new RadialGradientPainter(new RadialGradientProperty(getStartColor(), getEndColor(), RadialGradientProperty.BOTTOM_LEFT, 200));
			m_top_right_painter.setRadiusType(RadialGradientPainter.WIDTH_BASED);

			m_bottom_left_painter = new RadialGradientPainter(new RadialGradientProperty(getStartColor(), getEndColor(), RadialGradientProperty.TOP_RIGHT, 200));
			m_bottom_left_painter.setRadiusType(RadialGradientPainter.HEIGHT_BASED);
		}

		if (m_rect == null)
			m_rect = new Rectangle();

		int thickness = m_prop.getThickness();
		int padding = 1;
		int rheight_offset = -1 * padding;
		int bwidth_offset = -1 * padding;

		if (isSymmetric()) {
			/** clear the top and left sides */
			g.setColor(m_prop.getEndColor().getColor());
			rheight_offset += thickness;
			bwidth_offset += thickness;
			g.fillRect(x, y, width + thickness, thickness);
			g.fillRect(x, y, thickness, height + thickness);
			padding = 0;
		}

		/** render the shadow offsets */
		g.setColor(m_prop.getEndColor().getColor());
		/**
		 * right shadow offset
		 */
		g.fillRect(x + width - thickness, y, thickness, thickness + rheight_offset);
		/**
		 * bottom shadow offset
		 */
		g.fillRect(x, y + height - thickness, thickness + bwidth_offset, thickness);

		/** render the right shadow */
		m_rect.setBounds(x + width - thickness, y + thickness + rheight_offset, thickness + 1, height - thickness * 2 + padding);
		m_right_painter.paint(null, g, m_rect);

		/** render the top right shadow */
		m_rect.setBounds(x + width - thickness, y + thickness + rheight_offset, thickness, thickness);
		m_top_right_painter.paint(null, g, m_rect);

		/** render the bottom shadow */
		m_rect.setBounds(x + thickness + bwidth_offset, y + height - thickness, width - thickness * 2 + padding, thickness);
		m_bottom_painter.paint(null, g, m_rect);

		/** render the bottom left shadow */
		m_rect.setBounds(x + thickness + bwidth_offset, y + height - thickness, thickness, thickness);
		m_bottom_left_painter.paint(null, g, m_rect);

		/** render the bottom right shadow */
		m_rect.setBounds(x + width - thickness, y + height - thickness, thickness, thickness);
		m_bottom_right_painter.paint(null, g, m_rect);

	}
}
