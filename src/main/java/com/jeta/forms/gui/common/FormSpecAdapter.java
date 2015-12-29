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

package com.jeta.forms.gui.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.StringReader;

import com.jeta.forms.gui.common.parsers.FormSpecParser;
import com.jeta.forms.logger.FormsLogger;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

/**
 * A <code>FormSpecAdapter</code> is used to correctly encode and decode a
 * ColumnSpec and RowSpec to/from a String. This software depends on version
 * 1.0.2 of the FormLayout, and that version does not provide support for
 * serializing ColumnSpec and RowSpec. Furthermore, the FormLayout does not
 * provide the ability to encode and decode a FormSpec to a String.
 * 
 * @author Jeff Tassin
 */
public class FormSpecAdapter implements FormSpecDefinition, Externalizable {
	/**
	 * The version of this class
	 */
	private int m_version = VERSION_ID;

	/**
	 * The current version number.
	 */
	public static final int VERSION_ID = 1;

	/**
	 * The alignment string depending on the specification type column: LEFT,
	 * CENTER, RIGHT, RILL row: TOP, CENTER, BOTTOM, RILL
	 */
	private String m_alignment;

	/**
	 * Type size spec for the component: CONSTANT, COMPONENT, BOUNDED
	 */
	private String m_size_type;

	/**
	 * If the size spec is constant, this is the value for the size.
	 */
	private double m_constant_size;

	/**
	 * If the size spec is constant, this is the units: PX, PT, DLU IN, MM, CM
	 */
	private String m_constant_units;

	/**
	 * If the size spec is component, this is the size: MIN, PREF, DEFAULT
	 */
	private String m_component_size;

	/**
	 * If the size spec is bounded, this is the bound: MIN, MAX
	 */
	private String m_bounded_size;

	/**
	 * The resize behavior: NONE, GROW
	 */
	private String m_resize;

	/**
	 * If the resize behavior is GROW, this is the resize weight (0.0-1.0)
	 */
	private double m_resize_weight = 1.0;

	/**
	 * ctor
	 */
	public FormSpecAdapter(ColumnSpec spec) {
		this(spec.toString());
	}

	/**
	 * ctor
	 */
	public FormSpecAdapter(RowSpec spec) {
		this(spec.toString());
	}

	/**
	 * ctor
	 */
	public FormSpecAdapter(String enc) {
		try {
			StringReader reader = new StringReader(enc);
			FormSpecParser parser = new FormSpecParser(reader);
			parser.parse();

			m_alignment = parser.m_alignment;
			m_size_type = parser.m_size_type;
			m_constant_units = parser.m_constant_units;
			m_component_size = parser.m_component_size;
			m_bounded_size = parser.m_bounded_size;
			m_resize = parser.m_resize;

			try {
				m_constant_size = Double.parseDouble(parser.m_constant_size);
			} catch (Exception e) {
			}

			try {
				m_resize_weight = Double.parseDouble(parser.m_resize_weight);
			} catch (Exception e) {
			}
		} catch (Throwable t) {
			FormsLogger.severe(t);
		}
	}

	/**
	 * Properly converts a stringified version of a FormSpec to its encoded
	 * version. FormLayout has a serious problem in that it stores the encoded
	 * string in a different form than it takes in the constructor. For example,
	 * the FormLayout stores dlu sizes as dluX or dluY. However, the FormSpec
	 * constructor cannot handle this form.
	 */
	public static String fixup(String enc) {
		try {
			FormSpecAdapter adapter = new FormSpecAdapter(enc);
			return FormUtils.toEncodedString(adapter);
		} catch (Exception e) {
			return enc;
		}
	}

	/**
	 * Converts a comma-separated-value set of specs for a rows or columns
	 * definition for a layout. (e.g spec1,spec2,spec3 )
	 */
	public static String fixupSpecs(String specs) {
		if (specs == null) {
			System.out.println("FormSpecAdapter.fixupSpecs  specs=null!");
			FormUtils.safeAssert(false);
		}
		// System.out.println( "FormSpecAdapter:fixupSpecs: " + specs );
		StringBuffer sbuff = new StringBuffer();
		java.util.StringTokenizer st = new java.util.StringTokenizer(specs, ",");
		while (st.hasMoreTokens()) {
			String spec = st.nextToken();
			sbuff.append(FormSpecAdapter.fixup(spec));
			if (st.hasMoreTokens())
				sbuff.append(",");
		}
		return sbuff.toString();
	}

	/**
	 * @return the alignment string depending on the specification type column:
	 *         LEFT, CENTER, RIGHT, RILL row: TOP, CENTER, BOTTOM, RILL
	 */
	public String getAlignment() {
		return m_alignment;
	}

	/**
	 * @return CONSTANT, COMPONENT, BOUNDED
	 */
	public String getSizeType() {
		return m_size_type;
	}

	/**
	 * @return the units (integer) (double) PX, PT, DLU IN, MM, CM
	 */
	public String getConstantUnits() {
		return m_constant_units;
	}

	/**
	 * @return the size.
	 */
	public double getConstantSize() {
		return m_constant_size;
	}

	/**
	 * @return the component size: MIN, PREF, DEFAULT
	 */
	public String getComponentSize() {
		return m_component_size;
	}

	/**
	 * @return the bounded size MIN, MAX
	 */
	public String getBoundedSize() {
		return m_bounded_size;
	}

	/**
	 * @return true if the bounded size is MIN
	 */
	public boolean isBoundedMinimum() {
		return "MIN".equalsIgnoreCase(m_bounded_size);
	}

	/**
	 * @return true if the bounded size is MAX
	 */
	public boolean isBoundedMaximum() {
		return "MAX".equalsIgnoreCase(m_bounded_size);
	}

	/**
	 * @return true if the units are PX, PT, or DLU
	 */
	public boolean isIntegralUnits() {
		return FormUtils.isIntegralUnits(m_constant_units);
	}

	/**
	 * @return the resize behavior NONE, GROW
	 */
	public String getResize() {
		return m_resize;
	}

	/**
	 * @return true if the resize behavior is NONE
	 */
	public boolean isResizeNone() {
		return "NONE".equalsIgnoreCase(m_resize);
	}

	/**
	 * @return true if the resize behavior is GROW
	 */
	public boolean isResizeGrow() {
		return "GROW".equalsIgnoreCase(m_resize);
	}

	/**
	 * @return the resize weight (0.0-1.0)
	 */
	public double getResizeWeight() {
		return m_resize_weight;
	}

	/**
	 * @return true if the size type is bounded
	 */
	public boolean isBoundedSize() {
		return "BOUNDED".equals(m_size_type);
	}

	/**
	 * @return true if the size type is component
	 */
	public boolean isComponentSize() {
		return "COMPONENT".equals(m_size_type);
	}

	/**
	 * @return true if the size type is constant
	 */
	public boolean isConstantSize() {
		return "CONSTANT".equals(m_size_type);
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		m_version = in.readInt();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(m_version);
	}

}
