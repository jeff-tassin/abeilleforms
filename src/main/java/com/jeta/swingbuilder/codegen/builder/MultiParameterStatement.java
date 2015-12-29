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

import java.util.Collection;
import java.util.LinkedList;

import com.jeta.open.support.EmptyCollection;

public abstract class MultiParameterStatement implements Statement {
	private LinkedList m_parameters = new LinkedList();

	public void addParameter(String exp) {
		addParameter(new BasicExpression(exp));
	}

	public void addParameter(int ival) {
		addParameter(new BasicExpression(String.valueOf(ival)));
	}

	public void addParameter(boolean bval) {
		if (bval)
			addParameter(new BasicExpression("true"));
		else
			addParameter(new BasicExpression("false"));

	}

	public void addParameter(Expression exp) {
		if (m_parameters == null)
			m_parameters = new LinkedList();

		m_parameters.add(exp);
	}

	public Collection getParameters() {
		if (m_parameters == null)
			return EmptyCollection.getInstance();
		else
			return m_parameters;
	}
}
