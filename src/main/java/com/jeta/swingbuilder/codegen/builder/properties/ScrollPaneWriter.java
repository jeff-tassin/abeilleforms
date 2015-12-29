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

package com.jeta.swingbuilder.codegen.builder.properties;

import javax.swing.JScrollPane;

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.store.properties.ScrollBarsProperty;
import com.jeta.swingbuilder.codegen.builder.BasicExpression;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.DeclarationManager;
import com.jeta.swingbuilder.codegen.builder.LocalVariableDeclaration;
import com.jeta.swingbuilder.codegen.builder.MethodStatement;
import com.jeta.swingbuilder.codegen.builder.PropertyWriter;

public class ScrollPaneWriter implements PropertyWriter {
	/**
	 * PropertyWriter implementation
	 */
	public void writeProperty(DeclarationManager declMgr, BeanWriter writer, JETAPropertyDescriptor pd, Object value) {
		try {
			if (value instanceof ScrollBarsProperty) {
				ScrollBarsProperty sbp = (ScrollBarsProperty) value;
				if (sbp.isScrollable()) {
					LocalVariableDeclaration ds = new LocalVariableDeclaration(declMgr, javax.swing.JScrollPane.class);
					writer.addStatement(ds);

					MethodStatement ss = new MethodStatement(ds.getVariable(), "setViewportView");
					ss.addParameter(new BasicExpression(writer.getBeanVariable()));
					writer.addStatement(ss);

					ss = new MethodStatement(ds.getVariable(), "setVerticalScrollBarPolicy");
					ss.addParameter(new BasicExpression(getVerticalScrollBarPolicyString(sbp.getVerticalScrollBarPolicy())));
					writer.addStatement(ss);

					ss = new MethodStatement(ds.getVariable(), "setHorizontalScrollBarPolicy");
					ss.addParameter(new BasicExpression(getHorizontalScrollBarPolicyString(sbp.getHorizontalScrollBarPolicy())));
					writer.addStatement(ss);

					writer.setResultVariable(ds.getVariable(), javax.swing.JScrollPane.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getHorizontalScrollBarPolicyString(int policy) {
		switch (policy) {
		case JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED:
			return "JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED";

		case JScrollPane.HORIZONTAL_SCROLLBAR_NEVER:
			return "JScrollPane.HORIZONTAL_SCROLLBAR_NEVER";

		case JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS:
			return "JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS";

		default:
			return "JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED";
		}
	}

	private String getVerticalScrollBarPolicyString(int policy) {
		switch (policy) {
		case JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED:
			return "JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED";

		case JScrollPane.VERTICAL_SCROLLBAR_NEVER:
			return "JScrollPane.VERTICAL_SCROLLBAR_NEVER";

		case JScrollPane.VERTICAL_SCROLLBAR_ALWAYS:
			return "JScrollPane.VERTICAL_SCROLLBAR_ALWAYS";

		default:
			return "JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED";
		}
	}

}
