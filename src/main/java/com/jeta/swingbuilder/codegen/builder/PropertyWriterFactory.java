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

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.ButtonGroupProperty;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.forms.store.properties.IconProperty;
import com.jeta.forms.store.properties.ItemsProperty;
import com.jeta.forms.store.properties.ScrollBarsProperty;
import com.jeta.forms.store.properties.TabbedPaneProperties;
import com.jeta.forms.store.properties.TransformOptionsProperty;
import com.jeta.swingbuilder.codegen.builder.properties.BooleanPropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.BorderPropertyBuilder;
import com.jeta.swingbuilder.codegen.builder.properties.ButtonGroupWriter;
import com.jeta.swingbuilder.codegen.builder.properties.ColorPropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.FloatPropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.FontPropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.IconPropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.IntegerPropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.ItemsPropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.ScrollPaneWriter;
import com.jeta.swingbuilder.codegen.builder.properties.StringPropertyBuilder;
import com.jeta.swingbuilder.codegen.builder.properties.TabbedPanePropertyWriter;
import com.jeta.swingbuilder.codegen.builder.properties.TransformPropertyWriter;

public class PropertyWriterFactory {
	public static final String COMPONENT_ID = "property.writer.factory";

	private HashMap m_writers = new HashMap();

	public PropertyWriterFactory() {
		m_writers.put(String.class, new StringPropertyBuilder());
		m_writers.put(java.awt.Color.class, new ColorPropertyWriter());
		m_writers.put(ScrollBarsProperty.class, new ScrollPaneWriter());
		m_writers.put(TabbedPaneProperties.class, new TabbedPanePropertyWriter());
		m_writers.put(BorderProperty.class, new BorderPropertyBuilder());
		m_writers.put(CompoundBorderProperty.class, new BorderPropertyBuilder());
		m_writers.put(ItemsProperty.class, new ItemsPropertyWriter());
		m_writers.put(ButtonGroupProperty.class, new ButtonGroupWriter());

		m_writers.put(int.class, new IntegerPropertyWriter());
		m_writers.put(Integer.class, new IntegerPropertyWriter());
		m_writers.put(short.class, new IntegerPropertyWriter());
		m_writers.put(Short.class, new IntegerPropertyWriter());
		m_writers.put(byte.class, new IntegerPropertyWriter());
		m_writers.put(Byte.class, new IntegerPropertyWriter());
		m_writers.put(long.class, new IntegerPropertyWriter());
		m_writers.put(Long.class, new IntegerPropertyWriter());

		m_writers.put(float.class, new FloatPropertyWriter());
		m_writers.put(Float.class, new FloatPropertyWriter());
		m_writers.put(double.class, new FloatPropertyWriter());
		m_writers.put(Double.class, new FloatPropertyWriter());

		m_writers.put(Font.class, new FontPropertyWriter());
		m_writers.put(Color.class, new ColorPropertyWriter());
		m_writers.put(boolean.class, new BooleanPropertyWriter());
		m_writers.put(Boolean.class, new BooleanPropertyWriter());
		m_writers.put(javax.swing.Icon.class, new IconPropertyWriter());
		m_writers.put(javax.swing.ImageIcon.class, new IconPropertyWriter());
		m_writers.put(IconProperty.class, new IconPropertyWriter());
		m_writers.put(TransformOptionsProperty.class, new TransformPropertyWriter());
	}

	public PropertyWriter createWriter(Class type) {
		PropertyWriter writer = (PropertyWriter) m_writers.get(type);
		return writer;
	}
}
