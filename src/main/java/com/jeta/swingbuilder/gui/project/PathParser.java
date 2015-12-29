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

package com.jeta.swingbuilder.gui.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PathParser {

	public static String getRelativeFile(File homeDirectory, File file) {
		try {
			if (file.isDirectory()) {
				return getRelativePath(homeDirectory, file);
			}
			else {
				return getRelativePath(homeDirectory, file.getParentFile()) + file.getCanonicalFile().getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return file.getPath();
		}
	}

	public static String getRelativePath(File homeDirectory, File path) {
		try {
			if (!homeDirectory.exists()) {
				homeDirectory = homeDirectory.getParentFile();
				if (homeDirectory == null || !homeDirectory.exists())
					return path.getPath();
			}

			/**
			 * this handles the case where the Drive letters are different on
			 * windows
			 */
			String homepath = homeDirectory.getCanonicalPath();
			String filepath = path.getCanonicalPath();
			if (homepath.length() == 0 || filepath.length() == 0)
				return filepath;
			if (homepath.charAt(0) != filepath.charAt(0))
				return filepath;

			List home_elements = getPathList(homeDirectory);
			List path_elements = getPathList(path);
			return matchPathElements(home_elements, path_elements);
		} catch (Exception e) {
			e.printStackTrace();
			return path.getPath();
		}
	}

	private static List getPathList(File f) {
		ArrayList elements = new ArrayList();
		try {
			f = f.getCanonicalFile();
			if (!f.isDirectory())
				f = f.getParentFile();

			while (f != null) {
				elements.add(0, f.getName());
				f = f.getParentFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			elements = null;
		}
		return elements;
	}

	private static String matchPathElements(List home, List file) {
		int sz1 = home.size();
		int sz2 = file.size();

		int count = 0;
		while (count < sz1 && count < sz2) {
			String e1 = (String) home.get(count);
			String e2 = (String) file.get(count);

			if (e1 == null || e2 == null)
				break;

			if (!e1.equals(e2))
				break;

			count++;
		}

		StringBuffer result = new StringBuffer();
		if (count > 0) {
			for (int index = (home.size() - count); index > 0; index--) {
				result.append("..");
				result.append(File.separatorChar);
			}
		}

		for (int index = count; index < file.size(); index++) {
			result.append(file.get(index));
			result.append(File.separatorChar);
		}

		if (result.length() == 0)
			result.append(".");

		return result.toString();
	}
}
