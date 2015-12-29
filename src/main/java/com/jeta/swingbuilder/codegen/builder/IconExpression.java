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

import javax.swing.Icon;

import com.jeta.forms.store.properties.IconProperty;

public class IconExpression implements Expression {
	private MethodExpression m_method_expr;

	public IconExpression(DeclarationManager decl_mgr, IconProperty iprop) {
		if (iprop != null) {
			String path = iprop.getRelativePath();
			path = (path == null) ? "" : path.trim();
			if (path.length() > 0) {
				String methodname = decl_mgr.getResourceMethod(Icon.class);
				if (methodname != null) {
					m_method_expr = new MethodExpression(null, methodname);
					m_method_expr.addParameter(StringExpression.quoteString(path));
				}
			}
		}
	}

	public void output(SourceBuilder builder) {
		if (m_method_expr == null) {
			builder.print("null");
		}
		else {
			m_method_expr.output(builder);
		}
	}
}
