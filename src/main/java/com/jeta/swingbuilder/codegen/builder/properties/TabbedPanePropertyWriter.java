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

import java.util.Collection;
import java.util.Iterator;

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.store.properties.TabProperty;
import com.jeta.forms.store.properties.TabbedPaneProperties;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.DeclarationManager;
import com.jeta.swingbuilder.codegen.builder.IconExpression;
import com.jeta.swingbuilder.codegen.builder.MethodExpression;
import com.jeta.swingbuilder.codegen.builder.MethodStatement;
import com.jeta.swingbuilder.codegen.builder.MethodWriter;
import com.jeta.swingbuilder.codegen.builder.PanelWriter;
import com.jeta.swingbuilder.codegen.builder.PropertyWriter;
import com.jeta.swingbuilder.codegen.builder.StringExpression;

public class TabbedPanePropertyWriter implements PropertyWriter {
	/**
	 * PropertyWriter implementation
	 */
	public void writeProperty(DeclarationManager declMgr, BeanWriter writer, JETAPropertyDescriptor pd, Object value) {
		try {
			if (value instanceof TabbedPaneProperties) {
				TabbedPaneProperties tpp = (TabbedPaneProperties) value;
				Collection tabs = tpp.getTabs();
				Iterator iter = tabs.iterator();
				while (iter.hasNext()) {
					TabProperty tp = (TabProperty) iter.next();
					MethodStatement ss = new MethodStatement(writer.getBeanVariable(), "addTab");
					ss.addParameter(new StringExpression(tp.getTitle()));
					ss.addParameter(new IconExpression(declMgr, tp.getIconProperty()));
					PanelWriter pw = new PanelWriter();
					MethodWriter mw = pw.createPanel(declMgr, tp.getFormMemento());
					ss.addParameter(new MethodExpression(mw.getMethodName()));
					writer.addStatement(ss);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
