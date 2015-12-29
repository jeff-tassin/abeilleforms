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

/**
 * This interface defines methods for an object that handles color management
 * for an application. The main purpose of this interface is to allow forms to
 * update their colors when the look and feel changes. This is especially
 * important for gradients that fade to the background color of the current
 * form/control.
 * 
 * @author Jeff Tassin
 */
public interface ColorManager {
	/**
	 * The id for the component. Generally used for lookups in the JETARegistry
	 */
	public static final String COMPONENT_ID = "jeta.color.manager";

	/**
	 * @return a Collection of color names (String objects) maintained by this
	 *         color manager.
	 */
	public Collection getColorKeys();

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
	public Color getColor(String colorKey, Color defaultColor);

}
