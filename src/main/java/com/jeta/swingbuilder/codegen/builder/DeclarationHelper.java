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

package com.jeta.swingbuilder.codegen.builder;

import java.util.TreeSet;

public class DeclarationHelper {
	/**
	 * Set of variable names.
	 */
	private TreeSet m_declarations = new TreeSet();

	private String m_var_prefix;

	public DeclarationHelper(String prefix) {
		m_var_prefix = prefix;
	}

	public void addVariable(String varName) {
		m_declarations.add(varName);
	}

	public String getPrefix() {
		if (m_var_prefix == null)
			return "";
		else
			return m_var_prefix;
	}

	public String createVariable(Class compClass, String suggestedName) {
		String compName = getPrefix() + toJavaVariable(compClass, suggestedName);
		if (suggestedName == null)
			suggestedName = "";
		else
			suggestedName = suggestedName.trim();

		if (suggestedName.length() == 0) {
			String newname = compName + "1";
			if (m_declarations.contains(newname)) {
				for (int count = 2; count < 1000; count++) {
					newname = compName + String.valueOf(count);
					if (!m_declarations.contains(newname)) {
						compName = newname;
						break;
					}
				}
			}
			else {
				compName = newname;
			}
		}
		else {
			if (m_declarations.contains(compName)) {
				for (int count = 1; count < 1000; count++) {
					String newname = compName + String.valueOf(count);
					if (!m_declarations.contains(newname)) {
						compName = newname;
						break;
					}
				}
			}
		}
		m_declarations.add(compName);
		return compName;
	}

	public static String trimPackage(Class c) {
		if (c == null)
			return "";
		String cname = c.getName();
		int pos = cname.lastIndexOf('.');
		if (pos >= 0)
			cname = cname.substring(pos + 1, cname.length());
		return cname;
	}

	/**
	 * Converts the given variable name to a valid Java variable. If any symbols
	 * are found that are invalid symbols, they are removed.
	 */
	public static String toJavaVariable(Class compClass, String name) {
		if (name != null)
			name = name.trim();

		if (name == null || name.length() == 0) {
			name = trimPackage(compClass);
			name = name.toLowerCase();
		}

		StringBuffer sbuff = new StringBuffer();
		for (int index = 0; index < name.length(); index++) {
			char c = name.charAt(index);
			if (index == 0 && !Character.isJavaIdentifierStart(c)) {
				c = '_';
			}
			else if (!Character.isJavaIdentifierPart(c)) {
				c = '_';
			}
			sbuff.append(c);
		}
		return sbuff.toString();
	}
}
