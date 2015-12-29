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

package com.jeta.swingbuilder.gui.editor;

/**
 * An enum type that defines the orientation for a margin
 */
public class Orientation {
	/**
	 * the name of the orientation
	 */
	private String m_name;

	public static final Orientation VERTICAL = new Orientation("vertical");
	public static final Orientation HORIZONTAL = new Orientation("horizontal");

	private Orientation(String name) {
		m_name = name;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Orientation) {
			Orientation o = (Orientation) obj;
			return m_name.equals(o.m_name);
		}
		else
			return false;
	}
}
