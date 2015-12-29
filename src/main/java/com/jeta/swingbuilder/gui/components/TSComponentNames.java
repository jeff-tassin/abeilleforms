/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.gui.components;

/**
 * This class defines common literals for TSComponents
 * 
 * @author Jeff Tassin
 */
public class TSComponentNames {
	public static final String NULL_TEXT; // this is the text that appears in
											// a component if it's value is set
											// to null
	// as opposed to an empty string

	static {
		NULL_TEXT = com.jeta.open.i18n.I18N.getLocalizedMessage("null");
	}

	// common ids
	public static final String ID_CUT = "cut";
	public static final String ID_COPY = "copy";
	public static final String ID_COPY_SPECIAL = "copy.special";
	public static final String ID_PASTE = "paste";
	public static final String ID_PRINT = "print";
	public static final String ID_PRINT_PREVIEW = "print.preview";
	public static final String ID_UNDO = "undo";
	public static final String ID_REDO = "redo";

	/**
	 * This is a hack for the Windows Look and Feel. When changing manually to
	 * the Windows L&F, the toolbar separators somehow get changed to a ---
	 * horizontal orientation. This only happens when the user changes the L&F
	 * and not at startup. So, we check if the user is changing the L&F and
	 * manually reset the toolbar separators.
	 */
	public static final String ID_LOOK_AND_FEEL_CHANGED = "jeta.look.and.feel.changed";
}
