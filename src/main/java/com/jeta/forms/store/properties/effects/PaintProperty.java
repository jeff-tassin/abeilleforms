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

import java.io.IOException;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.effects.Paintable;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.open.i18n.I18N;

/**
 * Property for storing settings for a fill effect (i.e. gradient, solid, or
 * texture ). This is the <i>fill</i> property value that handles painting
 * effects for forms, components, and cells. This object maintains a reference
 * to an actual paint property and delegates all painting to the delegate.
 * 
 * @author Jeff Tassin
 */
public class PaintProperty extends JETAProperty implements PaintSupport {
	static final long serialVersionUID = 7144184663481849539L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 2;

	/**
	 * The property name
	 */
	public static final String PROPERTY_ID = "fill";

	/**
	 * The property we delegate to such as: GradientProperty, SolidFillProperty,
	 * TextureProperty, etc.
	 */
	private JETAProperty m_delegate;

	/**
	 * Creates an unitialized <code>PaintProperty</code> instance.
	 */
	public PaintProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Creates a <code>PaintProperty</code> instance with the specified paint
	 * property delegate.
	 */
	public PaintProperty(JETAProperty delegate) {
		super(PROPERTY_ID);
		m_delegate = delegate;
	}

	/**
	 * PaintSupport implementation. Simply forwards the call to the delegate.
	 */
	public Painter createPainter() {
		if (m_delegate instanceof PaintSupport)
			return ((PaintSupport) m_delegate).createPainter();
		else
			return null;
	}

	/**
	 * Returns the underlying paint delegate.
	 * 
	 * @return the underlying delegate
	 */
	public JETAProperty getPaintDelegate() {
		return m_delegate;
	}

	/**
	 * Sets this property to that of another PaintProperty
	 * 
	 * @param prop
	 *            a PaintProperty instance
	 */
	public void setValue(Object prop) {
		if (prop instanceof PaintProperty) {
			m_delegate = ((PaintProperty) prop).m_delegate;
		}
		else if (prop == null) {
			m_delegate = null;
		}
		else {
			assert (false);
		}
	}

	/**
	 * Updates the bean. Simply forwards the call to the delegate.
	 */
	public void updateBean(JETABean jbean) {
		if (m_delegate == null) {
			java.awt.Component comp = jbean.getDelegate();
			if (comp instanceof Paintable) {
				((Paintable) comp).setBackgroundPainter(null);
			}

		}
		else {
			m_delegate.updateBean(jbean);
		}
	}

	/**
	 * @return a string representation of this proprety
	 */
	public String toString() {
		if (m_delegate == null)
			return I18N.getLocalizedMessage("null");
		else
			return m_delegate.toString();
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		if (version > 1) {
			m_delegate = (JETAProperty) in.readObject("delegate");
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("delegate", m_delegate);
	}

}
