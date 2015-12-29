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

package com.jeta.forms.store.properties;

import java.io.IOException;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.effects.ImagePainter;
import com.jeta.forms.gui.effects.Paintable;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.properties.effects.PaintSupport;
import com.jeta.open.i18n.I18N;

/**
 * An <code>ImageProperty</code> is used to store the settings for rendering
 * an image as a background for a Paintable object.
 * 
 * See: {@link com.jeta.forms.gui.effects.ImagePainter}
 * 
 * @author Jeff Tassin
 */
public class ImageProperty extends IconProperty implements PaintSupport {
	static final long serialVersionUID = -9049185327280513748L;

	/** alignment definitions */
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int TOP = 3;
	public static final int BOTTOM = 5;

	/**
	 * The version of this class.
	 */
	public static final int VERSION = 1;

	/**
	 * The horizontal alignment of the image: LEFT, CENTER, RIGHT
	 */
	private int m_halign;

	/**
	 * The vertical alignment of the image: TOP, CENTER, BOTTOM
	 */
	private int m_valign;

	/**
	 * A cached painter used to render the image.
	 */
	private transient ImagePainter m_painter;

	/**
	 * Creates an unitialized <code>ImageProperty</code> instance.
	 */
	public ImageProperty() {

	}

	/**
	 * PaintSupport implementation.
	 */
	public Painter createPainter() {
		if (m_painter == null)
			m_painter = new ImagePainter(this, getHorizontalAlignment(), getVerticalAlignment());

		return m_painter;
	}

	/**
	 * Returns the horizontal alignment for the image.
	 * 
	 * @return the horizontal alignment for the image (LEFT,CENTER,RIGHT)
	 */
	public int getHorizontalAlignment() {
		return m_halign;
	}

	/**
	 * Returns the vertical alignment for the image.
	 * 
	 * @return the vertical alignment for the image (TOP,CENTER,BOTTOM)
	 */
	public int getVerticalAlignment() {
		return m_valign;
	}

	/**
	 * Sets the horizontal alignment for this image
	 * 
	 * @param align
	 *            the horizontal alignment: LEFT,CENTER,RIGHT
	 */
	public void setHorizontalAlignment(int align) {
		m_halign = align;
		m_painter = null;
	}

	/**
	 * Sets this property to that of another ImageProperty.
	 * 
	 * @param prop
	 *            an ImageProperty object
	 */
	public void setValue(Object prop) {
		super.setValue(prop);
		if (prop instanceof ImageProperty) {
			ImageProperty tp = (ImageProperty) prop;
			m_halign = tp.m_halign;
			m_valign = tp.m_valign;
			m_painter = null;
		}
		else if (!(prop instanceof IconProperty)) {
			assert (false);
		}
	}

	/**
	 * Sets the vertical alignment for this image.
	 * 
	 * @param align
	 *            the vertical alignment: TOP, CENTER, BOTTOM
	 */
	public void setVerticalAlignment(int align) {
		m_valign = align;
		m_painter = null;
	}

	/**
	 * Updates the bean. Creates an image painter and sets it as a background
	 * effect for the Java bean. Note that the Java bean associated with this
	 * property must implement the Paintable interface. See:
	 * {@link com.jeta.forms.gui.effects.Paintable}
	 */
	public void updateBean(JETABean jbean) {
		super.updateBean(jbean);
		ImagePainter painter = (ImagePainter) createPainter();
		if (jbean != null) {
			java.awt.Component comp = jbean.getDelegate();
			if (comp instanceof Paintable) {
				((Paintable) comp).setBackgroundPainter(painter);
			}
		}
	}

	/**
	 * Returns the name for this property.
	 * 
	 * @return a string representation of this proprety
	 */
	public String toString() {
		return I18N.getLocalizedMessage("Image");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_halign = in.readInt("halign");
		m_valign = in.readInt("valign");
	}

	/**
	 * JETAPersitable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(IconProperty.class));
		out.writeVersion(VERSION);
		out.writeInt("halign", m_halign);
		out.writeInt("valign", m_valign);
	}

}
