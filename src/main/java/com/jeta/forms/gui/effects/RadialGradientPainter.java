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

package com.jeta.forms.gui.effects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.batik.ext.awt.RadialGradientPaint;

import com.jeta.forms.store.properties.effects.RadialGradientProperty;

/**
 * This class is an implementation of a Painter that renders a radial gradient
 * on part of a canvas or component. This class uses the Batik SVG library for
 * rendering the gradient. See <a
 * href="http://xml.apache.org/batik/">http://xml.apache.org/batik/</a>
 * 
 * @author Jeff Tassin
 */
public class RadialGradientPainter implements Painter {
	/**
	 * The property that defines the gradient settings.
	 */
	private RadialGradientProperty m_gradient_prop;

	/**
	 * A paint object derived from the gradient property. It is cached so we
	 * don't have to re-instantiate with every paint.
	 */
	private Paint m_cached_paint;

	/**
	 * A rectangle used as a basis for the gradient paint. This value is checked
	 * everytime the paint method is called. If the paint area has changed, we
	 * regenerate the Paint object.
	 */
	private Rectangle m_last_rect = new Rectangle();

	/**
	 * We keep track of the look and feel. If it changes, we need to regenerate
	 * the paint
	 */
	private LookAndFeel m_look_and_feel;

	/**
	 * Flag that determines how the radius of this gradient is calculated
	 */
	private int m_radius_type = SQUARE_BASED;

	/**
	 * The radius is calculated based on the distance from the center of a
	 * rectangle to its corners or sides.
	 */
	public static final int SQUARE_BASED = 0;

	/**
	 * The radius is calculated as the width of the paint area.
	 */
	public static final int WIDTH_BASED = 1;

	/**
	 * The radius is calculated as the height of the paint area.
	 */
	public static final int HEIGHT_BASED = 2;

	/**
	 * Creates a <code>RadialGradientProperty</code> instance with no paint
	 * attributes.
	 */
	public RadialGradientPainter() {

	}

	/**
	 * Creates a <code>RadialGradientProperty</code> instance with the
	 * specified paint properties.
	 */
	public RadialGradientPainter(RadialGradientProperty prop) {
		m_gradient_prop = prop;
	}

	/**
	 * Calculate the radius used for this radial gradient.
	 * 
	 * @return the radius for the radial paint
	 */
	private double calculateRadius(double width, double height, int magnitude) {
		if (m_radius_type == WIDTH_BASED)
			return width * magnitude / 100.0;
		else if (m_radius_type == HEIGHT_BASED)
			return height * magnitude / 100.0;
		else
			return Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) * magnitude / 100.0;
	}

	/**
	 * Creates a paint object based on the given rectangle and gradient
	 * property.
	 * 
	 * @param rect
	 *            the rectangle that defines the area for the gradient.
	 * @param gp
	 *            the gradient properties used to define the paint.
	 * @return a paint object used for rendering the gradient on a graphics
	 *         context.
	 */
	private Paint createPaint(Rectangle rect, RadialGradientProperty gp) {
		double w_half = rect.getWidth() / 2.0;
		double h_half = rect.getHeight() / 2.0;

		double x = rect.getX() + w_half;
		double y = rect.getY() + h_half;
		double radius = gp.getMagnitude() * rect.getWidth() / 400.0;

		if (gp.getPosition() == RadialGradientProperty.TOP_LEFT) {
			x = rect.getX();
			y = rect.getY();
			radius = calculateRadius(w_half, h_half, gp.getMagnitude());
		}
		else if (gp.getPosition() == RadialGradientProperty.TOP_CENTER) {
			x = w_half;
			y = rect.getY();
			radius = w_half * gp.getMagnitude() / 100.0;
		}
		else if (gp.getPosition() == RadialGradientProperty.TOP_RIGHT) {
			x = rect.getX() + rect.getWidth();
			y = rect.getY();
			radius = calculateRadius(w_half, h_half, gp.getMagnitude());
		}
		else if (gp.getPosition() == RadialGradientProperty.BOTTOM_LEFT) {
			x = rect.getX();
			y = rect.getY() + rect.getHeight();
			radius = calculateRadius(w_half, h_half, gp.getMagnitude());
		}
		else if (gp.getPosition() == RadialGradientProperty.BOTTOM_CENTER) {
			x = w_half;
			y = rect.getY() + rect.getHeight();
			radius = w_half * gp.getMagnitude() / 100.0;
		}
		else if (gp.getPosition() == RadialGradientProperty.BOTTOM_RIGHT) {
			x = rect.getX() + rect.getWidth();
			y = rect.getY() + rect.getHeight();
			radius = calculateRadius(w_half, h_half, gp.getMagnitude());
		}
		else if (gp.getPosition() == RadialGradientProperty.LEFT_CENTER) {
			x = rect.getX();
			y = rect.getY() + h_half;
			radius = h_half * gp.getMagnitude() / 100.0;
		}
		else if (gp.getPosition() == RadialGradientProperty.RIGHT_CENTER) {
			x = rect.getX() + rect.getWidth();
			y = rect.getY() + h_half;
			radius = h_half * gp.getMagnitude() / 100.0;
		}

		float[] fractions = new float[] { 0.0f, 1.0f };
		Color[] colors = new Color[] { gp.getStartColor().getColor(), gp.getEndColor().getColor() };
		RadialGradientPaint paint = new RadialGradientPaint((float) x, (float) y, (float) radius, fractions, colors);
		return paint;
	}

	/**
	 * Painter implementation. Paints a radial gradient on a given graphics
	 * context.
	 * 
	 * @param g
	 *            the graphics context
	 * @param rect
	 *            the rectangle that defines the region to paint. Note, that
	 *            this is different than the clipping rectangle.
	 */
	public void paint(Component c, Graphics g, Rectangle rect) {
		if (rect == null || m_gradient_prop == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		Paint old_paint = g2.getPaint();

		LookAndFeel lf = UIManager.getLookAndFeel();
		if (m_look_and_feel != lf) {
			m_cached_paint = null;
			m_look_and_feel = lf;
		}

		if (m_cached_paint == null || !rect.equals(m_last_rect)) {
			m_last_rect.setBounds(rect.x, rect.y, rect.width, rect.height);
			m_cached_paint = createPaint(rect, m_gradient_prop);
		}

		g2.setPaint(m_cached_paint);

		Rectangle clip_rect = g.getClipBounds();
		if (rect.intersects(clip_rect)) {
			Rectangle irect = rect.intersection(clip_rect);
			g.fillRect(irect.x, irect.y, irect.width, irect.height);
			g2.setPaint(old_paint);
		}

		g2.setPaint(old_paint);
	}

	/**
	 * Sets the gradient attributes for this painter
	 * 
	 * @param prop
	 *            the gradient property to associate with this painter.
	 */
	public void setGradientProperty(RadialGradientProperty prop) {
		m_cached_paint = null;
		m_gradient_prop = prop;
	}

	/**
	 * Sets the radius type for this painter. Valid values are:
	 * RadialGradientPainter.SQUARE_BASED RadialGradientPainter.WIDTH_BASED
	 * RadialGradientPainter.HEIGHT_BASED
	 * 
	 * @param rtype
	 *            the radius type to set.
	 */
	public void setRadiusType(int rtype) {
		m_radius_type = rtype;
	}
}
