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

import java.awt.Color;
import java.io.IOException;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Used for storing colors. We don't serialize java.awt.Color objects directly
 * because on OS X there are Apple specific color objects that can be saved
 * indirectly which would cause problems on other platforms. So, all Color
 * values must be stored in ColorHolder objects.
 * 
 * @author Jeff Tassin
 */
public class ColorHolder extends AbstractJETAPersistable {
	static final long serialVersionUID = -4953997689182177487L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 1;

	private int m_red;
	private int m_green;
	private int m_blue;

	private transient Color m_color;

	/**
	 * Creates a <code>ColorHolder</code> instance that defaults to black.
	 */
	public ColorHolder() {

	}

	/**
	 * Creates a <code>ColorHolder</code> instance with the specified color.
	 * 
	 * @param c
	 *            the color
	 */
	public ColorHolder(Color c) {
		if (c != null) {
			m_red = c.getRed();
			m_green = c.getGreen();
			m_blue = c.getBlue();
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof ColorHolder) {
			ColorHolder ch = (ColorHolder) obj;
			return (m_red == ch.m_red && m_green == ch.m_green && m_blue == ch.m_blue);
		}
		else
			return false;
	}

	/**
	 * Returns the color contained by this holder.
	 * 
	 * @return the color
	 */
	public Color getColor() {
		if (m_color == null) {
			m_color = new Color(m_red, m_green, m_blue);
		}
		return m_color;
	}

	/**
	 * Returns the red component in the range 0-255 in the default RGB space.
	 * 
	 * @return the red component.
	 */
	public int getRed() {
		return m_red;
	}

	/**
	 * Returns the green component in the range 0-255 in the default RGB space.
	 * 
	 * @return the green component.
	 */
	public int getGreen() {
		return m_green;
	}

	/**
	 * Returns the blue component in the range 0-255 in the default RGB space.
	 * 
	 * @return the blue component.
	 */
	public int getBlue() {
		return m_blue;
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_red = in.readInt("red");
		m_green = in.readInt("green");
		m_blue = in.readInt("blue");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeInt("red", m_red);
		out.writeInt("green", m_green);
		out.writeInt("blue", m_blue);
	}

}
