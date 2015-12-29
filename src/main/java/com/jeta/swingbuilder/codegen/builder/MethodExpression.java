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

public class MethodExpression implements Expression {
	private String m_varname;
	private String m_method_name;

	private LinkedList m_parameters = new LinkedList();

	public MethodExpression(String methodName) {
		this(null, methodName);
	}

	public MethodExpression(String varName, String methodName) {
		if (varName == null || varName.length() == 0)
			m_varname = null;
		else
			m_varname = varName;

		m_method_name = methodName;
	}

	public void addParameter(Expression exp) {
		m_parameters.add(exp);
	}

	public void addParameter(String exp) {
		addParameter(new BasicExpression(exp));
	}

	public void output(SourceBuilder builder) {
		if (m_varname != null) {
			builder.print(m_varname);
			builder.print('.');
		}

		builder.print(m_method_name);
		builder.print('(');
		Iterator iter = m_parameters.iterator();
		while (iter.hasNext()) {
			Expression expr = (Expression) iter.next();
			expr.output(builder);
			if (iter.hasNext())
				builder.print(',');
		}
		builder.print(')');
	}
}
