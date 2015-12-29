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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Describes a series of line properties that make up a compound line. In the
 * designer, the user can use either a horizontal or vertical line component.
 * These components are really compound lines. This provides the ability to
 * support '3D' line separators on forms. The <code>CompoundLineProperty</code>
 * is used to store the attributes for each line in a compound line component.
 * 
 * @author Jeff Tassin
 */
public class CompoundLineProperty extends JETAProperty {
	static final long serialVersionUID = 8321803759018530163L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 2;

	/**
	 * A list of lines (LineProperty objects) that make up this compound line.
	 * The order is from left/top ---> right/bottom.
	 */
	private LinkedList m_lines = new LinkedList();

	/**
	 * The postion of the line (for horizontal lines: TOP, CENTER,BOTTOM. for
	 * vertical: LEFT,CENTER,RIGHT )
	 */
	private int m_position;

	public static final String PROPERTY_ID = "lineDefinition";

	/**
	 * Creates an empty <code>CompoundLineProperty</code>
	 */
	public CompoundLineProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Creates a <code>CompoundLineProperty</code> with the specified line.
	 * 
	 * @param lp
	 *            a property that describes a single line.
	 */
	public CompoundLineProperty(LineProperty lp) {
		super(PROPERTY_ID);
		addLine(lp);
	}

	/**
	 * Returns a collection of <code>LineProperty</code> objects that make up
	 * this compound line.
	 * 
	 * @return a collection of <code>LineProperty</code> objects.
	 */
	public Collection getLines() {
		return m_lines;
	}

	/**
	 * Adds a line to this compound line.
	 * 
	 * @param lp
	 *            the line property to add
	 */
	public void addLine(LineProperty lp) {
		if (lp != null) {
			m_lines.addLast(lp);
		}
	}

	/**
	 * The position of the lines relative to their containment area.
	 * 
	 * @return the position value, one of the following constants defined in
	 *         <code>HorizontalLineComponent</code> or defined in
	 *         <code>VerticalLineComponent</code>
	 *            <code>LEFT</code>,
	 *         <code>CENTER</code>, <code>RIGHT</code>, <code>TOP</code>
	 *         or <code>BOTTOM</code>.
	 */
	public int getPosition() {
		return m_position;
	}

	/**
	 * Returns an iterator to the lines <code>LineProperty</code> objects in
	 * this compound line.
	 * 
	 * @return an iterator to the lines (LineProperty objects)in this compound
	 *         line.
	 */
	public Iterator iterator() {
		return m_lines.iterator();
	}

	/**
	 * Prints this component state to the console
	 */
	public void print() {
		Iterator iter = iterator();
		while (iter.hasNext()) {
			LineProperty prop = (LineProperty) iter.next();
			prop.print();
		}
	}

	/**
	 * Clears all existing line properties from this object and adds the
	 * specified set of lines
	 * 
	 * @param lines
	 *            a collection of <code>LineProperty</code> objects to add to
	 *            this compound line.
	 */
	public void setLines(Collection lines) {
		m_lines.clear();
		m_lines.addAll(lines);
	}

	/**
	 * Sets the position of the lines relative to their bounds.
	 * 
	 * @param pos
	 *            the position value, one of the following constants defined in
	 *            <code>HorizontalLineComponent</code> or defined in
	 *            <code>VerticalLineComponent</code>
	 *            <code>LEFT</code>,
	 *            <code>CENTER</code>, <code>RIGHT</code>,
	 *            <code>TOP</code> or <code>BOTTOM</code>.
	 */
	public void setPosition(int pos) {
		m_position = pos;
	}

	/**
	 * Sets this property to that of another <code>CompoundLineProperty</code>
	 * 
	 * @param prop
	 *            a <code>CompoundLineProperty</code>
	 */
	public void setValue(Object prop) {
		if (prop instanceof CompoundLineProperty) {
			CompoundLineProperty cb = (CompoundLineProperty) prop;
			m_lines.clear();
			m_lines.addAll(cb.m_lines);
			m_position = cb.m_position;
		}
	}

	/**
	 * JETAProperty implementation. no op fo this property.
	 */
	public void updateBean(JETABean jbean) {
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_lines = (LinkedList) in.readObject("lines");
		if (version > 1)
			m_position = in.readInt("position");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("lines", m_lines);
		out.writeInt("position", m_position);
	}

}
