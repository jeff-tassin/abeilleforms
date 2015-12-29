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
import java.util.Iterator;

public class VariableDeclaration extends MultiParameterStatement {
	private Class m_obj_class;
	private String m_declaration;
	private DeclarationManager m_decl_mgr;
	private String m_varname;
	private String m_initializer;

	/** Creates a member variable declaration */
	public VariableDeclaration(DeclarationManager declMgr, Class objClass, String varName, boolean local) {
		this(declMgr, objClass, varName, local, null);
	}

	/** Creates a member variable declaration */
	public VariableDeclaration(DeclarationManager declMgr, Class objClass, String varName, boolean local, String initializer) {
		m_decl_mgr = declMgr;
		m_obj_class = objClass;
		if (objClass != com.jeta.forms.gui.form.GridView.class) {
			m_decl_mgr.addImport(m_obj_class.getName());
		}
		if (local)
			m_varname = m_decl_mgr.createLocalVariable(objClass, varName);
		else
			m_varname = m_decl_mgr.createMemberVariable(objClass, varName);

		m_initializer = initializer;
	}

	public String getVariable() {
		return m_varname;
	}

	protected String getInitializer() {
		if (m_initializer == null)
			return "new " + DeclarationHelper.trimPackage(m_obj_class);
		else
			return m_initializer;
	}

	public void output(SourceBuilder builder) {
		builder.print(DeclarationHelper.trimPackage(m_obj_class));
		builder.print(" ");
		builder.print(m_varname);
		builder.print(" = ");
		builder.print(getInitializer());
		builder.print("(");
		Collection params = getParameters();
		Iterator iter = params.iterator();
		while (iter.hasNext()) {
			Expression expr = (Expression) iter.next();
			expr.output(builder);
			if (iter.hasNext())
				builder.print(',');
		}
		builder.print(");");
		builder.println();
	}
}
