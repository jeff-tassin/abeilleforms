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
import java.awt.Rectangle;

import javax.swing.Icon;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.properties.ImageProperty;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.resources.AppResourceLoader;
import com.jeta.open.resources.ResourceLoader;

/**
 * This class is an implementation of a painter that renders an image on a part
 * of a canvas or component. The caller specifies an Icon and alignment
 * properties for the painter. Since this is not an AWT component, the caller
 * must explicitly call paint. The caller must also pass a rectangle that
 * specifies the coordinates of the painting area relative to the given graphics
 * context.
 * 
 * @author Jeff Tassin
 */
public class ImagePainter implements Painter {
	/**
	 * The icon that is rendered when paint is called.
	 */
	private Icon m_icon;

	/**
	 * The horizontal alignment ImageProperty.LEFT, CENTER, RIGHT
	 */
	private int m_halign;

	/**
	 * The vertical alignment ImageProperty.TOP, CENTER, BOTTOM
	 */
	private int m_valign;

	/**
	 * This icon is used in design mode when the assigned icon is null.
	 */
	private static Icon m_design_icon;

	/**
	 * A rectangle used for clipping. We keep it around so we don't have to
	 * reinstantiate with every paint message.
	 */
	private Rectangle m_rect = new Rectangle();

	/**
	 * Creates an <code>ImagePainter</code> instance with no icon.
	 */
	public ImagePainter() {
		this(null, 0, 0);
	}

	/**
	 * Creates an <code>ImagePainter</code> instance with the specified icon
	 * and alignment properties.
	 * 
	 * @param icon
	 *            the icon that will be rendered on the graphics context when
	 *            paint is invoked.
	 * @param hAlign
	 *            the horizontal alignment of the icon relative to the painting
	 *            area.
	 * @param vAlign
	 *            the horizontal alignment of the icon relative to the painting
	 *            area.
	 */
	public ImagePainter(Icon icon, int hAlign, int vAlign) {
		m_icon = icon;
		m_halign = hAlign;
		m_valign = vAlign;
		synchronized (ImagePainter.class) {
			if (m_design_icon == null) {
				try {
					/** use a place holder icon if the given icon is null */
					if (FormUtils.isDesignMode()) {
						ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
						FormUtils.safeAssert(loader != null);
						if (loader != null) {
							m_design_icon = loader.loadImage("general/16x16/portrait.png");
						}
					}
					else {
						m_design_icon = AppResourceLoader.getEmptyIcon();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Return the height of the icon associated with this painter.
	 * 
	 * @return the height of the icon associated with this painter
	 */
	public int getIconHeight() {
		if (m_icon == null) {
			if (m_design_icon == null)
				return 0;
			else
				return m_design_icon.getIconHeight();
		}
		else {
			return m_icon.getIconHeight();
		}
	}

	/**
	 * Return the width of the icon associated with this painter.
	 * 
	 * @return the width of the icon associated with this painter
	 */
	public int getIconWidth() {
		if (m_icon == null) {
			if (m_design_icon == null)
				return 0;
			else
				return m_design_icon.getIconWidth();
		}
		else {
			return m_icon.getIconWidth();
		}
	}

	/**
	 * Returns the horizontal alignment of the icon relative to the painting
	 * area. The alignment has an effect only if the painting area is larger
	 * than the icon size. Valid values are: ImageProperty.LEFT,
	 * ImageProperty.CENTER, ImageProperty.RIGHT
	 * 
	 * @return the horizontal alignment of the icon. See
	 *         {@link com.jeta.forms.store.properties.ImageProperty}
	 */
	public int getHorizontalAlignment() {
		return m_halign;
	}

	/**
	 * Returns the vertical alignment of the icon relative to the painting area.
	 * The alignment has an effect only if the painting area is larger than the
	 * icon size. Valid values are: ImageProperty.TOP, ImageProperty.CENTER,
	 * ImageProperty.BOTTOM
	 * 
	 * @return the vertical alignment of the icon. See
	 *         {@link com.jeta.forms.store.properties.ImageProperty}
	 */
	public int getVerticalAlignment() {
		return m_valign;
	}

	/**
	 * Returns the icon used by this painter.
	 * 
	 * @return the icon associated with this painter.
	 */
	public Icon getIcon() {
		return m_icon;
	}

	/**
	 * Sets the horizontal alignment of the icon relative to the painting area.
	 * 
	 * @param halign
	 *            the horizontal alignment of the icon.
	 */
	public void setHorizontalAlignment(int halign) {
		m_halign = halign;
	}

	/**
	 * Sets the vertical alignment of the icon relative to the painting area.
	 * 
	 * @param valign
	 *            the vertical alignment of the icon.
	 */
	public void setVerticalAlignment(int valign) {
		m_valign = valign;
	}

	/**
	 * Sets the icon used by this painter.
	 * 
	 * @param icon
	 *            the icon associated with this painter.
	 */
	public void setIcon(Icon icon) {
		m_icon = icon;
	}

	/**
	 * Renders this image in the given rectangle. Note, that the coordinates
	 * passed here are different than the clipping rectangle.
	 * 
	 * @param g
	 *            the graphics context
	 * @param x
	 *            the x position that defines the region to paint.
	 * @param y
	 *            the y position that defines the region to paint.
	 * @param width
	 *            the width that defines the region to paint.
	 * @param height
	 *            the height that defines the region to paint.
	 */
	public void paint(Component comp, Graphics g, int x1, int y1, int width, int height) {
		int x = x1;
		int y = y1;

		int halign = getHorizontalAlignment();
		if (halign == ImageProperty.CENTER) {
			x = (width - getIconWidth()) / 2;
		}
		else if (halign == ImageProperty.RIGHT) {
			x = width - getIconWidth();
		}
		else {
			x = x1;
		}

		int valign = getVerticalAlignment();
		if (valign == ImageProperty.CENTER) {
			y = (height - getIconHeight()) / 2;
		}
		else if (valign == ImageProperty.BOTTOM) {
			y = height - getIconHeight();
		}
		else {
			y = y1;
		}

		if (x < x1)
			x = x1;
		if (y < y1)
			y = y1;

		Icon icon = getIcon();

		Rectangle clip_rect = g.getClipBounds();
		m_rect.setBounds(x, y, width, height);
		if (m_rect.intersects(clip_rect)) {
			m_rect = m_rect.intersection(clip_rect);
			g.setClip(m_rect.x, m_rect.y, m_rect.width, m_rect.height);

			if (icon == null) {
				if (m_design_icon != null)
					m_design_icon.paintIcon(comp, g, x, y);
			}
			else {
				icon.paintIcon(comp, g, x, y);
			}

			g.setClip(clip_rect.x, clip_rect.y, clip_rect.width, clip_rect.height);
		}
	}

	/**
	 * Renders this image in the given rectangle
	 * 
	 * @param g
	 *            the graphics context
	 * @param rect
	 *            the rectangle that defines the region to paint. Note, that
	 *            this is different than the clipping rectangle.
	 */
	public void paint(Component comp, Graphics g, Rectangle rect) {
		if (rect == null)
			return;

		paint(comp, g, rect.x, rect.y, rect.width, rect.height);
	}

}
