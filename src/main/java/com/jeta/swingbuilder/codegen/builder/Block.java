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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Block implements CodeSegment {
	private LinkedList m_codeLines = new LinkedList();

	public void addCode(String codeLines) {
		m_codeLines.add(codeLines);
	}

	public void println() {
		m_codeLines.add("\n");
	}

	public void output(SourceBuilder builder) {
		Iterator iter = m_codeLines.iterator();
		while (iter.hasNext()) {
			String code = (String) iter.next();
			StringTokenizer st = new StringTokenizer(code, "{};\n", true);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.equals("{")) {
					builder.openBrace();
					builder.println();
					builder.indent();
				}
				else if (token.equals("}")) {
					builder.dedent();
					builder.closeBrace();
				}
				else if (token.equals(";")) {
					builder.println(";");
				}
				else {
					if (token.equals("\n")) {
						builder.println();
					}
					else {
						builder.print(token);
					}
				}
			}
		}
	}
}
