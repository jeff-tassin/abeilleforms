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

import org.apache.batik.ext.awt.LinearGradientPaint;

import com.jeta.forms.store.properties.effects.GradientProperty;

/**
 * This class is an implementation of a Painter that renders a a linear gradient
 * on a part of a canvas or component. This class uses the Batik library for
 * rendering the gradient. See <a
 * href="http://xml.apache.org/batik/">http://xml.apache.org/batik/</a>
 * 
 * @author Jeff Tassin
 */
public class LinearGradientPainter implements Painter {
	/**
	 * The property that defines the gradient settings.
	 */
	private GradientProperty m_gradient_prop;

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
	 * Creates a <code>LinearGradientProperty</code> instance with no paint
	 * attributes.
	 */
	public LinearGradientPainter() {

	}

	/**
	 * Creates a <code>LinearGradientProperty</code> instance with the
	 * specified paint properties.
	 */
	public LinearGradientPainter(GradientProperty prop) {
		m_gradient_prop = prop;
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
	private Paint createPaint(Rectangle rect, GradientProperty gp) {
		Color startcolor = gp.getStartColor().getColor();
		Color endcolor = gp.getEndColor().getColor();
		int x1 = rect.x;
		int y1 = rect.y;
		int x2 = rect.x;
		int y2 = rect.y + (int) (rect.height * gp.getMagnitude());

		if (startcolor == null)
			startcolor = Color.white;
		if (endcolor == null)
			endcolor = Color.white;

		if (gp.getDirection() == GradientProperty.TOP_BOTTOM) {
			x1 = rect.x;
			y1 = rect.y;
			x2 = rect.x;
			y2 = rect.y + (int) (rect.height * gp.getMagnitude());
		}
		else if (gp.getDirection() == GradientProperty.BOTTOM_TOP) {
			x1 = rect.x;
			y2 = rect.y;
			x2 = rect.x;
			y1 = rect.y + (int) (rect.height * gp.getMagnitude());
		}
		else if (gp.getDirection() == GradientProperty.LEFT_RIGHT) {
			x1 = rect.x;
			y1 = rect.y;
			x2 = rect.x + (int) (rect.width * gp.getMagnitude());
			y2 = rect.y;
		}
		else if (gp.getDirection() == GradientProperty.RIGHT_LEFT) {
			x2 = rect.x;
			y1 = rect.y;
			x1 = rect.x + (int) (rect.width * gp.getMagnitude());
			y2 = rect.y;
		}
		else if (gp.getDirection() == GradientProperty.UP_LEFT) {
			x1 = rect.x;
			y1 = rect.y + (int) (rect.height * gp.getMagnitude());
			x2 = rect.x + (int) (rect.width * gp.getMagnitude());
			y2 = rect.y;
		}
		else // assume down diagonal
		{
			x1 = rect.x;
			y1 = rect.y;
			x2 = rect.x + (int) (rect.width * gp.getMagnitude());
			y2 = rect.y + (int) (rect.height * gp.getMagnitude());
		}

		float[] fractions = new float[] { 0.0f, 1.0f };
		Color[] colors = new Color[] { startcolor, endcolor };
		LinearGradientPaint paint = new LinearGradientPaint((float) x1, (float) y1, (float) x2, (float) y2, fractions, colors);
		return paint;
	}

	/**
	 * Painter Implementation. Paints a linear gradient on a given graphics
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
	}

	/**
	 * Sets the gradient attributes for this painter
	 * 
	 * @param prop
	 *            the gradient property to associate with this painter.
	 */
	public void setGradientProperty(GradientProperty prop) {
		m_cached_paint = null;
		m_gradient_prop = prop;
	}
}
