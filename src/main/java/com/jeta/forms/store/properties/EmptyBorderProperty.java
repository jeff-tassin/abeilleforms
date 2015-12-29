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

import java.awt.Component;
import java.io.IOException;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * An <code>EmptyBorderProperty</code> represents an empty border for a
 * component. An empty border is used to add padding around the top, left,
 * bottom, or right sides of a component. See:
 * {@link javax.swing.border.EmptyBorder}
 * 
 * @author Jeff Tassin
 */
public class EmptyBorderProperty extends BorderProperty {
	static final long serialVersionUID = 6035977997403297375L;

	public static final int VERSION = 1;

	/**
	 * The padding in pixels for the sides of this border.
	 */
	private int m_top;
	private int m_left;
	private int m_bottom;
	private int m_right;

	/**
	 * Creates an <code>EmptyBorderProperty</code> instance with no padding
	 * values.
	 */
	public EmptyBorderProperty() {

	}

	/**
	 * Creates an <code>EmptyBorderProperty</code> instance with the specified
	 * padding values.
	 * 
	 * @param top
	 *            the padding in pixels for the top of this border.
	 * @param left
	 *            the padding in pixels for the left side of this border.
	 * @param bottom
	 *            the padding in pixels for the bottom of this border.
	 * @param right
	 *            the padding in pixels for the right side of this border.
	 */
	public EmptyBorderProperty(int top, int left, int bottom, int right) {

		m_top = top;
		m_left = left;
		m_bottom = bottom;
		m_right = right;
	}

	/**
	 * Creates an <code>EmptyBorder</code> instance with the padding values
	 * specified by this property.
	 * 
	 * @return a newly created EmptyBorder instance that can be set on any Swing
	 *         component.
	 */
	public Border createBorder(Component comp) {
		Border b = new EmptyBorder(getTop(), getLeft(), getBottom(), getRight());
		return createTitle(b);
	}

	/**
	 * Returns the top padding in pixels for this border.
	 * 
	 * @return the top padding in pixels
	 */
	public int getTop() {
		return m_top;
	}

	/**
	 * Returns the left padding in pixels for this border.
	 * 
	 * @return the left padding in pixels.
	 */
	public int getLeft() {
		return m_left;
	}

	/**
	 * Returns the bottom padding in pixels for this border.
	 * 
	 * @return the bottom padding in pixels.
	 */
	public int getBottom() {
		return m_bottom;
	}

	/**
	 * Returns the right padding in pixels for this border.
	 * 
	 * @return the right padding in pixels
	 */
	public int getRight() {
		return m_right;
	}

	/**
	 * Sets this property to that of another <code>EmptyBorderProperty</code>.
	 * 
	 * @param prop
	 *            the property
	 */
	public void setValue(Object prop) {
		super.setValue(prop);
		if (prop instanceof EmptyBorderProperty) {
			EmptyBorderProperty bp = (EmptyBorderProperty) prop;
			m_top = bp.m_top;
			m_left = bp.m_left;
			m_bottom = bp.m_bottom;
			m_right = bp.m_right;
		}
		else {
			assert (false);
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_top = in.readInt("top");
		m_left = in.readInt("left");
		m_bottom = in.readInt("bottom");
		m_right = in.readInt("right");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(BorderProperty.class));
		out.writeVersion(VERSION);
		out.writeInt("top", m_top);
		out.writeInt("left", m_left);
		out.writeInt("bottom", m_bottom);
		out.writeInt("right", m_right);
	}

	public String toString() {
		return "EMPTY";
	}
}
