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

import java.awt.Color;
import java.lang.reflect.Method;

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.swingbuilder.codegen.builder.BasicExpression;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.DeclarationManager;
import com.jeta.swingbuilder.codegen.builder.Expression;
import com.jeta.swingbuilder.codegen.builder.MethodExpression;
import com.jeta.swingbuilder.codegen.builder.MethodStatement;
import com.jeta.swingbuilder.codegen.builder.PropertyWriter;

public class ColorPropertyWriter implements PropertyWriter {

	/**
	 * PropertyWriter implementation
	 */
	public void writeProperty(DeclarationManager declMgr, BeanWriter writer, JETAPropertyDescriptor pd, Object value) {
		try {
			Color c = null;
			if (value instanceof Color)
				c = (Color) value;
			else if (value instanceof ColorHolder)
				c = ((ColorHolder) value).getColor();

			Method write = pd.getWriteMethod();
			if (c != null && write != null) {
				declMgr.addImport("java.awt.Color");
				MethodStatement ms = new MethodStatement(writer.getBeanVariable(), write.getName());
				ms.addParameter(createColorExpression(c));
				writer.addStatement(ms);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Expression createColorExpression(Color c) {
		if (c == null) {
			return new BasicExpression("null");
		}
		else {
			MethodExpression expr = new MethodExpression("new Color");
			expr.addParameter(String.valueOf(c.getRed()));
			expr.addParameter(String.valueOf(c.getGreen()));
			expr.addParameter(String.valueOf(c.getBlue()));
			return expr;
		}
	}

}
