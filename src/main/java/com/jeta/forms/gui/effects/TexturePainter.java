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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import com.jeta.forms.store.properties.effects.TextureProperty;

/**
 * This class is an implementation of a Painter that renders a texture on a
 * canvas or component. The caller specifies an image for the texture with a
 * TextureProperty. The standard Java Graphics APIs are used to render the
 * texture. See: {@link java.awt.TexturePaint}
 * 
 * @author Jeff Tassin
 */
public class TexturePainter implements Painter {
	/**
	 * The property that defines the image and texture properties.
	 */
	private TextureProperty m_texture_prop;

	/**
	 * A paint object derived from the texture property. It is cached so we
	 * don't have to re-instantiate with every paint.
	 */
	private Paint m_cached_paint;

	/**
	 * A rectangle used as a basis for the texture paint paint. This value is
	 * checked everytime the paint method is called. If the paint area has
	 * changed, we regenerate the Paint object.
	 */
	private Rectangle m_last_rect;

	/**
	 * Creates a <code>TexturePainter</code> instance with no paint
	 * attributes.
	 */
	public TexturePainter() {

	}

	/**
	 * Creates a <code>TexturePainter</code> instance with the specified
	 * texture properties.
	 */
	public TexturePainter(TextureProperty prop) {
		m_texture_prop = prop;
	}

	/**
	 * Creates a paint object based on the given texture property.
	 * 
	 * @param tp
	 *            the texture property used to create the paint. This object
	 *            specifies the image for the texture.
	 * @return the Paint object used to render the texture.
	 */
	private Paint createPaint(Rectangle rect, TextureProperty tp) {
		BufferedImage bi = tp.getBufferedImage();
		rect = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
		return new TexturePaint(bi, rect);
	}

	/**
	 * Painter implementation. Paints a texture pattern on a given graphics
	 * context.
	 * 
	 * @param g
	 *            the graphics context
	 * @param rect
	 *            the rectangle that defines the region to paint. Note, that
	 *            this is different than the clipping rectangle.
	 */
	public void paint(Component c, Graphics g, Rectangle rect) {
		if (rect == null || m_texture_prop == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		Paint old_paint = g2.getPaint();

		if (m_cached_paint == null || !rect.equals(m_last_rect)) {
			m_last_rect = rect;
			m_cached_paint = createPaint(rect, m_texture_prop);
		}
		g2.setPaint(m_cached_paint);
		g2.translate(rect.x, rect.y);
		g.fillRect(0, 0, rect.width, rect.height);
		g2.translate(-rect.x, -rect.y);
		g2.setPaint(old_paint);
	}

	/**
	 * Sets the texture attributes for this painter
	 * 
	 * @param prop
	 *            the texture property to set.
	 */
	public void setTextureProperty(TextureProperty prop) {
		m_cached_paint = null;
		m_texture_prop = prop;
	}
}
