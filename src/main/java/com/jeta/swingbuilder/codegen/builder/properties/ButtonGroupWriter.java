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

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.store.properties.ButtonGroupProperty;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.DeclarationManager;
import com.jeta.swingbuilder.codegen.builder.MemberVariableDeclaration;
import com.jeta.swingbuilder.codegen.builder.MethodStatement;
import com.jeta.swingbuilder.codegen.builder.PropertyWriter;
import com.jeta.swingbuilder.codegen.builder.VariableDeclaration;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

public class ButtonGroupWriter implements PropertyWriter {
	/**
	 * PropertyWriter implementation
	 */
	public void writeProperty(DeclarationManager declMgr, BeanWriter writer, JETAPropertyDescriptor pd, Object value) {
		try {
			if (value instanceof ButtonGroupProperty) {
				declMgr.addImport("javax.swing.ButtonGroup");

				ButtonGroupProperty bg = (ButtonGroupProperty) value;
				String groupname = FormDesignerUtils.fastTrim(bg.getGroupName());
				if (groupname.length() > 0) {
					VariableDeclaration vd = (VariableDeclaration) declMgr.get("buttongroup." + groupname);
					if (vd == null) {
						String varname = groupname;
						if (varname.length() == 1)
							varname = "buttongroup" + varname;

						vd = new MemberVariableDeclaration(declMgr, javax.swing.ButtonGroup.class, varname);
						declMgr.addMemberVariable(vd);
						declMgr.put("buttongroup." + groupname, vd);
					}
					MethodStatement ms = new MethodStatement(vd.getVariable(), "add");
					ms.addParameter(writer.getBeanVariable());
					writer.addStatement(ms);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
