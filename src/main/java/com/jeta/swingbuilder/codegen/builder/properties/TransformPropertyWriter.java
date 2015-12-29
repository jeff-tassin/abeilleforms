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

import java.lang.reflect.Method;

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.store.properties.TransformOptionsProperty;
import com.jeta.swingbuilder.codegen.builder.BasicExpression;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.DeclarationHelper;
import com.jeta.swingbuilder.codegen.builder.DeclarationManager;
import com.jeta.swingbuilder.codegen.builder.Expression;
import com.jeta.swingbuilder.codegen.builder.MethodStatement;
import com.jeta.swingbuilder.codegen.builder.PropertyWriter;

public class TransformPropertyWriter implements PropertyWriter {

	/**
	 * PropertyWriter implementation
	 */
	public void writeProperty(DeclarationManager declMgr, BeanWriter writer, JETAPropertyDescriptor pd, Object value) {
		try {
			if (value instanceof TransformOptionsProperty) {
				TransformOptionsProperty tprop = (TransformOptionsProperty) value;
				Object pvalue = tprop.getCurrentItem();
				if (pvalue != null) {
					Method write = tprop.getWriteMethod();
					if (write != null) {
						MethodStatement ms = new MethodStatement(writer.getBeanVariable(), write.getName());
						ms.addParameter(createTransformExpression(writer.getBeanType(), pvalue.toString()));
						writer.addStatement(ms);
					}
					else {
						System.out.println("TransformOptionsProperty.write method is null for: " + pd.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Expression createTransformExpression(Class beanType, String propValue) {
		if (propValue != null && propValue.length() > 0) {
			String cname = DeclarationHelper.trimPackage(beanType);
			if (cname != null && cname.length() > 0) {
				BasicExpression expr = new BasicExpression(cname + "." + propValue);
				return expr;
			}
		}
		return null;
	}

}
