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

import java.awt.Color;
import java.io.Externalizable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.common.ComponentNames;
import com.jeta.swingbuilder.interfaces.app.ObjectStore;

/**
 * Model for storing recent colors selected with the JETAColorChooser
 * 
 * @author Jeff Tassin
 */
public class RecentColorsModel implements Externalizable {
	static final long serialVersionUID = 6974055371373328348L;

	public static final String COMPONENT_ID = "recent.colors.model";

	/**
	 * A list of ColorHolder objects.
	 */
	private ArrayList m_colors = new ArrayList();
	private int m_size = 30;

	/**
	 * verion of this class
	 */
	public static final int VERSION = 1;

	public RecentColorsModel() {
	}

	private RecentColorsModel(int size) {
		m_size = size;
	}

	public static RecentColorsModel createInstance(int size) {
		RecentColorsModel model = null;
		try {
			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
			model = (RecentColorsModel) os.load(COMPONENT_ID);
			if (model != null) {
				model.m_size = size;
				if (size < model.m_colors.size()) {
					ArrayList newlist = new ArrayList();
					for (int index = 0; index < size; index++) {
						newlist.add(model.m_colors.get(index));
					}
					model.m_colors = newlist;
				}
			}
		} catch (Exception e) {
			FormsLogger.severe(e);
		}

		if (model == null)
			model = new RecentColorsModel(size);
		return model;
	}

	/**
	 * Return the number of colors in this model
	 */
	public int size() {
		return m_colors.size();
	}

	/**
	 * Returns the ColorHolder at the given index. If the index is invalid null
	 * is returned.
	 */
	public ColorHolder getColor(int index) {
		return getColor(index, null);
	}

	/**
	 * Returns the ColorHolder at the given index. If the index is invalid the
	 * defaultColor is returned.
	 * 
	 * @param index
	 *            the index
	 * @param defaultColor
	 *            the color to return if the index is outside the valid range or
	 *            if a null is found at the given index.
	 */
	public ColorHolder getColor(int index, ColorHolder defaultColor) {
		if (index < 0 || index >= size())
			return defaultColor;

		ColorHolder result = (ColorHolder) m_colors.get(index);
		return result == null ? defaultColor : result;
	}

	public static void save(RecentColorsModel model) {
		try {
			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
			os.store(COMPONENT_ID, model);
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

	public void setRecentColor(Color c) {
		setRecentColor(new ColorHolder(c));
	}

	public void setRecentColor(ColorHolder colorHolder) {
		if (colorHolder == null)
			return;

		Iterator iter = m_colors.iterator();
		while (iter.hasNext()) {
			ColorHolder ch = (ColorHolder) iter.next();
			if (colorHolder.equals(ch))
				iter.remove();
		}
		m_colors.add(0, colorHolder);

		if (m_colors.size() > m_size)
			m_colors.remove(m_colors.size() - 1);
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_colors = (ArrayList) in.readObject();
		m_size = in.readInt();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeObject(m_colors);
		out.writeInt(m_size);
	}

}
