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

import java.awt.Font;
import java.io.IOException;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Defines the attributes for a Font object. A <code>FontProperty</code> must
 * be used instead of serializing a <code>Font</code> object directly. The
 * reason is because some look and feels have problems with fonts that were
 * serialized with a different look and feel. For example, saving a font on
 * Linux and then rendering that de-serialized font on OSX causes problems.
 * 
 * @author Jeff Tassin
 */
public class FontProperty extends AbstractJETAPersistable {
	static final long serialVersionUID = -1878354825497611389L;

	public static final int VERSION = 1;

	/**
	 * The family name for the font.
	 */
	private String m_family;

	/**
	 * The font style. One of the following constants defined in
	 * <code>Font</code>: PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
	 */
	private int m_style;

	/**
	 * The point size of the font.
	 */
	private int m_size;

	/**
	 * A cached font object based on the attributes defined in this property.
	 */
	private transient Font m_font;

	/**
	 * Creates an unitialized <code>FontProperty</code> instance.
	 */
	public FontProperty() {

	}

	/**
	 * Creates a <code>FontProperty</code> instance with the attributes from
	 * the specified font.
	 * 
	 * @param f
	 *            the font whose attributes will define this property.
	 */
	public FontProperty(Font f) {
		if (f != null) {
			m_family = f.getFamily();
			m_style = f.getStyle();
			m_size = f.getSize();
			m_font = f;
		}
	}

	/**
	 * Returns a font instance that is defined by the attributes defined by this
	 * property.
	 * 
	 * @return a font instance with the attributes defined by this property.
	 */
	public Font getFont() {
		if (m_font == null) {
			if (m_family == null)
				m_family = "Dialog";

			if (m_size == 0)
				m_size = 12;

			m_font = new Font(m_family, m_style, m_size);
		}
		return m_font;
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_family = (String) in.readObject("family");
		m_style = in.readInt("style");
		m_size = in.readInt("size");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("family", m_family);
		out.writeInt("style", m_style);
		out.writeInt("size", m_size);
	}

}
