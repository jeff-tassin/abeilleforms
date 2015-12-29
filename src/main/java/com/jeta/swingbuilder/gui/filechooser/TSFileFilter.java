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

package com.jeta.swingbuilder.gui.filechooser;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.filechooser.FileFilter;

public class TSFileFilter extends FileFilter {
	/**
	 * A list of file extensions for this filter
	 */
	private LinkedList m_exts = new LinkedList();

	/**
	 * The description for this filter
	 */
	private String m_description;

	/**
	 * A comma separated list of file extensions without the . e.g.
	 * "txt,gif,jpeg"
	 */
	public TSFileFilter(String ext_tokens, String desc) {
		try {
			StringTokenizer st = new StringTokenizer(ext_tokens, ",");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				m_exts.add(token);
			}

			m_description = desc;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		if (m_exts.size() == 0)
			return true;

		String extension = getExtension(f);
		if (extension != null) {
			Iterator iter = m_exts.iterator();
			while (iter.hasNext()) {
				if (extension.equals(iter.next()))
					return true;
			}
		}
		return false;
	}

	/**
	 * The description for this filter
	 */
	public String getDescription() {
		return m_description;
	}
}
