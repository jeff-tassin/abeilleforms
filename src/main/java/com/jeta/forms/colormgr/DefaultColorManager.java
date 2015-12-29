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

package com.jeta.forms.colormgr;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * The default implementation of ColorManager. All calls for getColor are
 * forwarded to UIManager.getColor.
 * 
 * @author Jeff Tassin
 */
public class DefaultColorManager implements ColorManager {
	/**
	 * A list of color keys (String objects)
	 */
	private LinkedList m_color_keys;

	/**
	 * ctor
	 */
	public DefaultColorManager() {
	}

	/**
	 * @return a Collection of color names (String objects) maintained by this
	 *         color manager.
	 */
	public Collection getColorKeys() {
		if (m_color_keys == null) {
			m_color_keys = new LinkedList();
			m_color_keys.add("constant");
			m_color_keys.add("control");

			TreeSet default_colors = new TreeSet(String.CASE_INSENSITIVE_ORDER);

			/** add default UIManager keys for components and borders */
			try {

				UIDefaults defs = UIManager.getLookAndFeelDefaults();
				LinkedList keys = new LinkedList(defs.keySet());
				Iterator iter = keys.iterator();
				while (iter.hasNext()) {
					Object key = iter.next();
					Object value = defs.get(key);
					if (value instanceof Color && !("control".equals(key))) {
						default_colors.add(key.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Iterator iter = default_colors.iterator();
			while (iter.hasNext()) {
				m_color_keys.add(iter.next());
			}
			// m_color_keys.add( "Table.background" );
			// m_color_keys.add( "Table.foreground" );

		}
		return m_color_keys;
	}

	/**
	 * @param colorKey
	 *            the name associated with the color to retrieve.
	 * @param defaultColor
	 *            the color to return if not color is found associated with
	 *            colorKey
	 * @return the color associated with the given color name. It is up to the
	 *         implementation to determine this association. Most
	 *         implementations will probabaly use the UIManager.getColor( Object
	 *         colorName ) call to get the color. However, it is possible for an
	 *         implementation to provide custom logic for a subset of
	 *         colorNames.
	 */
	public Color getColor(String colorKey, Color defaultColor) {
		Color c = UIManager.getColor(colorKey);
		if (c != null) {
			/**
			 * we need to do this because some look and feels (e.g. Alloy L&F)
			 * will create their own Color objects that are invalid if the look
			 * and feel is not available on the deployed system
			 */
			c = new Color(c.getRed(), c.getGreen(), c.getBlue());
		}

		return (c == null ? defaultColor : c);
	}

}
