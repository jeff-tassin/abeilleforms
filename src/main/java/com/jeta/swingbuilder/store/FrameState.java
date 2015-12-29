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

package com.jeta.swingbuilder.store;

import java.awt.Rectangle;
import java.io.Externalizable;
import java.io.IOException;

public class FrameState implements Externalizable {
	static final long serialVersionUID = -2421986789797208149L;

	/**
	 * verion of this class
	 */
	public static final int VERSION = 1;

	private boolean m_docked;
	private Rectangle m_bounds;

	public FrameState() {
		m_docked = true;
		m_bounds = new Rectangle();
	}

	public FrameState(Rectangle bounds) {
		m_docked = false;
		m_bounds = new Rectangle();
		if (bounds != null)
			m_bounds.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public boolean isDocked() {
		return m_docked;
	}

	public Rectangle getBounds() {
		return m_bounds;
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_docked = in.readBoolean();
		m_bounds = (Rectangle) in.readObject();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeBoolean(m_docked);
		out.writeObject(m_bounds);
	}
}
