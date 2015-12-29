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

package com.jeta.forms.store.properties.effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.effects.Paintable;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.gui.effects.TexturePainter;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.properties.IconProperty;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.open.i18n.I18N;

/**
 * Property for storing settings for a texture fill effect. A texture is an
 * image that is repeated to fill a background. See:
 * {@link com.jeta.forms.gui.effects.TexturePainter}
 * 
 * @author Jeff Tassin
 */
public class TextureProperty extends JETAProperty implements PaintSupport {
	static final long serialVersionUID = 6397209471447777696L;

	/**
	 * The version for this class
	 */
	public static final int VERSION = 1;

	/**
	 * The icon for our texture
	 */
	private IconProperty m_icon;

	/**
	 * A buffered image that contains the icon
	 */
	private transient BufferedImage m_buffered_image;

	/**
	 * A cached painter
	 */
	private transient TexturePainter m_painter;

	/**
	 * Creates an uninitialized <code>TextureProperty</code> instance.
	 */
	public TextureProperty() {

	}

	/**
	 * PaintSupport implementation. Creates a TexturePainter that renders a
	 * textured background.
	 */
	public Painter createPainter() {
		if (m_painter == null)
			m_painter = new TexturePainter(this);

		return m_painter;
	}

	/**
	 * Returns a buffered image of the icon associated with this texture.
	 * 
	 * @return a bufferedimage of this property's icon.
	 */
	public BufferedImage getBufferedImage() {
		if (m_buffered_image == null && m_icon != null) {
			int width = m_icon.getIconWidth();
			int height = m_icon.getIconHeight();
			m_buffered_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D bg = m_buffered_image.createGraphics();
			bg.drawImage(m_icon.imageIcon().getImage(), 0, 0, bg.getColor(), null);
			bg.dispose();
		}
		return m_buffered_image;
	}

	/**
	 * Returns the icon that is the basis for this texture.
	 * 
	 * @return the icon that is the basis for this texture
	 */
	public IconProperty getIconProperty() {
		return m_icon;
	}

	/**
	 * Sets the icon that is the basis for this texture.
	 * 
	 * @param icon
	 *            the icon
	 */
	public void setIconProperty(IconProperty icon) {
		m_icon = icon;
		m_buffered_image = null;
		m_painter = null;
	}

	/**
	 * Sets this property to that of another TextureProperty.
	 * 
	 * @param prop
	 *            a TextureProperty object
	 */
	public void setValue(Object prop) {
		if (prop instanceof TextureProperty) {
			TextureProperty tp = (TextureProperty) prop;
			m_icon = tp.m_icon;
			m_painter = null;
		}
		else {
			assert (false);
		}
	}

	/**
	 * Updates the bean. Gets the underlying Java bean component associated with
	 * this property and if that component implements the Paintable interface,
	 * sets the background painter.
	 */
	public void updateBean(JETABean jbean) {
		TexturePainter painter = (TexturePainter) createPainter();
		if (jbean != null) {
			java.awt.Component comp = jbean.getDelegate();
			if (comp instanceof Paintable) {
				((Paintable) comp).setBackgroundPainter(painter);
			}
		}
	}

	/**
	 * @return a string representation of this proprety
	 */
	public String toString() {
		return I18N.getLocalizedMessage("Texture");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_icon = (IconProperty) in.readObject("icon");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("icon", m_icon);
	}

}
