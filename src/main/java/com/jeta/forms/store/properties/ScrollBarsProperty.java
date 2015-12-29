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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JScrollPane;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A custom property for scrollable Swing components such as lists, tables, and
 * text areas. We provide a scroll bars property for these components instead of
 * requiring the user to create a separate JScrollPane in the designer. This
 * property will automatically create a JScrollPane and add the Java bean during
 * runtime.
 * 
 * @author Jeff Tassin
 */
public class ScrollBarsProperty extends JETAProperty {
	static final long serialVersionUID = 9130862301613978006L;

	/**
	 * version number for this class
	 */
	public static final int VERSION = 3;

	/**
	 * The vertical scroll bar policy.
	 * 
	 * @see #setVerticalScrollBarPolicy
	 */
	private int m_vert_policy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;

	/**
	 * The horizontal scroll bar policy.
	 * 
	 * @see #setHorizontalScrollBarPolicy
	 */
	private int m_horz_policy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;

	/**
	 * The name for the scroll pane.
	 */
	private String m_scroll_name;

	/**
	 * The border for the scroll pane. If null, use the default scroll pane
	 * border.
	 */
	private CompoundBorderProperty m_scroll_border = null;

	public static final String PROPERTY_ID = "scollBars";

	/**
	 * Creates a <code>ScrollBarsProperty</code> instances with the default
	 * horizontal and vertical scroll bar policies (AS_NEEDED).
	 */
	public ScrollBarsProperty() {
		super(PROPERTY_ID);
		m_vert_policy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
		m_horz_policy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
	}

	/**
	 * Creates a <code>ScrollBarsProperty</code> instances with the specified
	 * horizontal and vertical scroll bar policies.
	 * 
	 * @param vertpolicy
	 *            the vertical scroll bar policy.
	 * @param horzpolicy
	 *            the horizontal scroll bar policy.
	 */
	public ScrollBarsProperty(int vertpolicy, int horzpolicy) {
		super(PROPERTY_ID);
		m_vert_policy = vertpolicy;
		m_horz_policy = horzpolicy;
	}

	/**
	 * Returns the property that defines the border to apply to the scroll pane.
	 * 
	 * @return the scroll pane border attributes.
	 */
	public CompoundBorderProperty getBorderProperty() {
		return m_scroll_border;
	}

	/**
	 * Returns the vertical scroll bar policy value.
	 * 
	 * @return the <code>verticalScrollBarPolicy</code> property
	 * @see #setVerticalScrollBarPolicy
	 */
	public int getVerticalScrollBarPolicy() {
		return m_vert_policy;
	}

	/**
	 * Returns the horizontal scroll bar policy value.
	 * 
	 * @return the <code>horizontalScrollBarPolicy</code> property
	 * @see #setHorizontalScrollBarPolicy
	 */
	public int getHorizontalScrollBarPolicy() {
		return m_horz_policy;
	}

	/**
	 * Returns the name of the JScrollPane component.
	 * 
	 * @return the name of the JScrollPane.
	 */
	public String getScrollName() {
		return m_scroll_name;
	}

	/**
	 * Returns true if both the vertical and horizontal policies are NOT equal
	 * to NEVER. If a property is scrollable then a JScrollPane will be created.
	 * If this property is not scrollabe, a JScrollPane instance will not be
	 * created.
	 * 
	 * @return true if both the vertical and horizontal policies are NOT equal
	 *         to NEVER
	 */
	public boolean isScrollable() {
		boolean result = !(getVerticalScrollBarPolicy() == JScrollPane.VERTICAL_SCROLLBAR_NEVER && getHorizontalScrollBarPolicy() == JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return result;
	}

	/**
	 * Sets the horizontal scrollbar policy. The options are:<code>JScrollPane</code>
	 * <code>HORIZONTAL_SCROLLBAR_AS_NEEDED</code>
	 * <code>HORIZONTAL_SCROLLBAR_NEVER</code>
	 * <code>HORIZONTAL_SCROLLBAR_ALWAYS</code>
	 * 
	 * @param policy
	 *            the horizontal scroll bar policy
	 */
	public void setHorizontalScrollBarPolicy(int policy) {
		m_horz_policy = policy;
	}

	/**
	 * Sets the vertical scrollbar policy. The options are:<code>JScrollPane</code>
	 * <code>VERTICAL_SCROLLBAR_AS_NEEDED</code>
	 * <code>VERTICAL_SCROLLBAR_NEVER</code>
	 * <code>VERTICAL_SCROLLBAR_ALWAYS</code>
	 * 
	 * @param policy
	 *            the vertical scroll bar policy
	 */
	public void setVerticalScrollBarPolicy(int policy) {
		m_vert_policy = policy;
	}

	/**
	 * Sets the name for the JScrollPane component.
	 */
	public void setScrollName(String scrollName) {
		m_scroll_name = scrollName;
	}

	/**
	 * Sets the property that defines the border to apply to the scroll pane.
	 * 
	 * @return the scroll pane border attributes.
	 */
	public void setBorderProperty(CompoundBorderProperty prop) {
		if (prop == null) {
			m_scroll_border = null;
		}
		else {
			if (m_scroll_border == null)
				m_scroll_border = new CompoundBorderProperty();

			m_scroll_border.setValue(prop);
		}
	}

	/**
	 * Sets this property to that of another ScrollBarsProperty.
	 */
	public void setValue(Object prop) {
		if (prop instanceof ScrollBarsProperty) {
			ScrollBarsProperty sb = (ScrollBarsProperty) prop;
			m_vert_policy = sb.m_vert_policy;
			m_horz_policy = sb.m_horz_policy;
			m_scroll_name = sb.m_scroll_name;

			if (m_scroll_border == null)
				m_scroll_border = new CompoundBorderProperty();

			if (sb.m_scroll_border == null)
				m_scroll_border.addBorder(new DefaultBorderProperty());
			else
				m_scroll_border.setValue(sb.m_scroll_border);
		}
	}

	/**
	 * JETAProperty implementation. Removes the Java bean from the form and adds
	 * it to a JScrollPane. The JScrollPane is then added to the form in place
	 * of the Java bean.
	 */
	public void updateBean(JETABean bean) {
		Component comp = null;
		if (bean != null)
			comp = bean.getDelegate();

		bean.removeAll();
		if (isScrollable()) {
			JScrollPane scroll = null;

			/** check if component's parent is a previously created scrollbar */
			Container c = comp.getParent();
			if (c instanceof javax.swing.JViewport) {
				scroll = (JScrollPane) c.getParent();
			}
			else {
				final boolean bdesign = FormUtils.isDesignMode();
				final Dimension pref_sz = new Dimension(80, 80);
				scroll = new JScrollPane(comp);
				/**
				 * we need to set the preferred size otherwise the scroll pane
				 * could become too large in some cases. The reason is that the
				 * scrollpanes preferred size is related to the internal size of
				 * the scrollable area and not the child component
				 */
				scroll.setPreferredSize(pref_sz);
			}

			scroll.setHorizontalScrollBarPolicy(m_horz_policy);
			scroll.setVerticalScrollBarPolicy(m_vert_policy);

			if (comp.isOpaque())
				scroll.setOpaque(true);

			if (m_scroll_name != null)
				scroll.setName(m_scroll_name);

			if (m_scroll_border != null) {
				try {
					scroll.setBorder(m_scroll_border.createBorder(scroll));
				} catch (Exception e) {
					FormsLogger.severe(e);
				}
			}
			bean.add(scroll, BorderLayout.CENTER);
		}
		else {
			bean.add(comp, BorderLayout.CENTER);
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());

		int version = in.readVersion();
		m_vert_policy = in.readInt("verticalpolicy");
		m_horz_policy = in.readInt("horizontalpolicy");
		if (version >= 2) {
			m_scroll_name = (String) in.readObject("scrollname");
		}
		if (version >= 3) {
			m_scroll_border = (CompoundBorderProperty) in.readObject("border");
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeInt("verticalpolicy", m_vert_policy);
		out.writeInt("horizontalpolicy", m_horz_policy);
		out.writeObject("scrollname", m_scroll_name);
		out.writeObject("border", m_scroll_border);
	}

}
