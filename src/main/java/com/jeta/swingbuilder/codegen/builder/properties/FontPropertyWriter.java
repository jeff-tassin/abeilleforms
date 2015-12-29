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

import java.awt.Font;
import java.lang.reflect.Method;

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.store.properties.FontProperty;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.DeclarationManager;
import com.jeta.swingbuilder.codegen.builder.MethodExpression;
import com.jeta.swingbuilder.codegen.builder.MethodStatement;
import com.jeta.swingbuilder.codegen.builder.PropertyWriter;
import com.jeta.swingbuilder.codegen.builder.StringExpression;

public class FontPropertyWriter implements PropertyWriter {

	/**
	 * PropertyWriter implementation
	 */
	public void writeProperty(DeclarationManager declMgr, BeanWriter writer, JETAPropertyDescriptor pd, Object value) {
		try {
			Method write = pd.getWriteMethod();
			if (write != null) {
				Font font = null;
				if (value instanceof Font)
					font = (Font) value;
				else if (value instanceof FontProperty)
					font = ((FontProperty) value).getFont();

				if (font != null && write != null) {
					declMgr.addImport("java.awt.Font");
					MethodStatement ms = new MethodStatement(writer.getBeanVariable(), write.getName());
					ms.addParameter(createFontExpression(font));
					writer.addStatement(ms);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MethodExpression createFontExpression(Font font) {
		MethodExpression expr = new MethodExpression("new Font");
		expr.addParameter(new StringExpression(font.getName()));
		int style = font.getStyle();
		if (((style & Font.BOLD) != 0) && ((style & Font.ITALIC) != 0))
			expr.addParameter("Font.BOLD | Font.ITALIC");
		else if ((style & Font.BOLD) != 0)
			expr.addParameter("Font.BOLD");
		else if ((style & Font.ITALIC) != 0)
			expr.addParameter("Font.ITALIC");
		else
			expr.addParameter("Font.PLAIN");

		expr.addParameter(String.valueOf(font.getSize()));
		return expr;
	}

}
