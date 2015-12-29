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

public class StringExpression implements Expression {
	private String m_svalue;

	public StringExpression(String sVal) {
		m_svalue = quoteString(sVal);
	}

	public static String quoteString(String str) {
		if (str == null)
			return null;

		StringBuffer sbuff = new StringBuffer();

		sbuff.append('\"');
		for (int index = 0; index < str.length(); index++) {
			char c = str.charAt(index);
			if (c == '\"') {
				sbuff.append("\\\"");
			}
			else if (c == '\\') {
				sbuff.append("\\\\");
			}
			else if (c == '\n') {
				sbuff.append("\\n");
			}
			else {
				sbuff.append(c);
			}
		}
		sbuff.append('\"');

		return sbuff.toString();
	}

	public void output(SourceBuilder builder) {
		builder.print(m_svalue);
	}
}
