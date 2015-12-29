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

package com.jeta.forms.components.line;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JComponent;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.properties.CompoundLineProperty;
import com.jeta.forms.store.properties.LineProperty;

/**
 * A component that is one or more lines that can be oriented vertically or
 * horizontally
 * 
 * @author Jeff Tassin
 */
public abstract class LineComponent extends JComponent {
	private boolean m_design_mode = false;

	/**
	 * A list of LineProperties
	 */
	private CompoundLineProperty m_prop = new CompoundLineProperty();

	/**
	 * The preferred size
	 */
	private Dimension m_pref_size;

	/**
	 * ctor
	 */
	public LineComponent() {
		m_prop.addLine(new LineProperty());
		m_design_mode = FormUtils.isDesignMode();
	}

	/**
	 * @return the definition for this line
	 */
	public CompoundLineProperty getLineDefinition() {
		return m_prop;
	}

	/**
	 * @return the position for this line
	 */
	public int getPosition() {
		return m_prop.getPosition();
	}

	/**
	 * @return the preferred size for this component
	 */
	public Dimension getPreferredSize() {
		if (m_pref_size == null) {
			if (isHorizontal()) {
				int width = 0;
				int height = 0;

				if (m_prop != null) {
					height = getThickness();
				}

				if (width == 0)
					width = 10;
				if (height == 0)
					height = 1;
				m_pref_size = new Dimension(width, height);
			}
			else {
				int width = 0;
				int height = 0;

				if (m_prop != null) {
					width = getThickness();
				}
				if (width == 0)
					width = 1;
				if (height == 0)
					height = 10;
				m_pref_size = new Dimension(width, height);

			}
		}
		return m_pref_size;
	}

	/**
	 * @return the total thickness for this line. This is the total of all lines
	 *         in the compound line property
	 */
	public int getThickness() {
		int t = 0;
		Iterator iter = iterator();
		while (iter.hasNext()) {
			LineProperty prop = (LineProperty) iter.next();
			t += prop.getThickness();
		}
		return t;
	}

	boolean isDesignMode() {
		return m_design_mode;
	}

	/**
	 * @return true if this line is oriented horizontally
	 */
	public abstract boolean isHorizontal();

	/**
	 * @return an iterator to the lines (LineProperty objects)in this compound
	 *         border
	 */
	public Iterator iterator() {
		return m_prop.iterator();
	}

	/**
	 * Sets the definition for this line
	 */
	public void setLineDefinition(CompoundLineProperty prop) {
		m_prop = prop;
		m_pref_size = null;
		revalidate();
		repaint();
	}

	/**
	 * Sets the definition for this line
	 */
	public void setLineDefinitionEx(LineProperty prop) {
		setLineDefinition(new CompoundLineProperty(prop));
	}

	/**
	 * Prints this component state to the console
	 */
	public void print() {
		System.out.println("LineComponent.....................");
		m_prop.print();
	}

	/**
	 * @return the position for this line
	 */
	public void setPosition(int pos) {
		m_prop.setPosition(pos);
	}

}
