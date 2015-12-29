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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.Icon;

import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.store.CodeModel;

public class ClassDeclarationManager implements DeclarationManager {
	private DeclarationHelper m_member_decls;

	private DeclarationHelper m_method_decls = new DeclarationHelper(null);

	/**
	 * A set of import statements for this class (without the import keyword)
	 */
	private TreeSet m_imports = new TreeSet();

	/**
	 * A list of MethodWriter objects
	 */
	private LinkedList m_methods = new LinkedList();

	/**
	 * A list of member variables
	 */
	private LinkedList m_fields = new LinkedList();

	private String m_class_name;

	private String m_package;

	private HashMap m_user_objects = new HashMap();

	private CodeModel m_code_model;

	public ClassDeclarationManager(CodeModel cgenmodel) {
		m_code_model = cgenmodel;
		m_class_name = cgenmodel.getClassName();
		m_member_decls = new DeclarationHelper(cgenmodel.getMemberPrefix());
	}

	public void addImport(String importDef) {
		if (importDef != null && importDef.indexOf(".") >= 0) {
			m_imports.add(importDef);
		}
	}

	/**
	 * 
	 */
	public void addMethod(MethodWriter mw) {
		m_method_decls.addVariable(mw.getMethodName());
		m_methods.add(mw);
	}

	public void addMemberVariable(Statement stmt) {
		if (stmt != null)
			m_fields.add(stmt);
	}

	public void build(SourceBuilder builder) {
		if (m_package.length() > 0) {

			if (m_package.indexOf("package") != 0) {
				builder.print("package ");
			}

			builder.print(m_package);
			builder.println(";");
			builder.println();
		}

		Iterator iter = m_imports.iterator();
		while (iter.hasNext()) {
			builder.print("import ");
			builder.print(iter.next().toString());
			builder.println(";");
		}
		builder.println();
		builder.println();

		builder.print("public class ");
		builder.print(getClassName());
		builder.print(" extends JPanel");

		builder.openBrace();
		builder.println();
		builder.indent();

		iter = m_fields.iterator();
		while (iter.hasNext()) {
			Statement stmt = (Statement) iter.next();
			stmt.output(builder);
		}

		iter = m_methods.iterator();
		while (iter.hasNext()) {
			builder.println();
			MethodWriter mw = (MethodWriter) iter.next();
			mw.build(builder);
			builder.println();
		}

		builder.dedent();

		builder.println();
		builder.println();

		builder.closeBrace();
		builder.println();
	}

	public String createMemberVariable(Class compClass, String compName) {
		return m_member_decls.createVariable(compClass, compName);
	}

	public String createLocalVariable(Class compClass, String compName) {
		assert (false);
		return "";
	}

	public String createMethodName(String name) {
		String methodname = m_method_decls.createVariable(null, name);
		return methodname;
	}

	public String getClassName() {
		return m_class_name;
	}

	/**
	 * This returns the name of a method used for loading resources in a form
	 * such as Icons and strings.
	 */
	public String getResourceMethod(Class resourceType) {
		if (Icon.class == resourceType) {
			return "loadImage";
		}
		else
			return null;
	}

	public boolean isIncludeNonStandard() {
		return m_code_model.isIncludeNonStandard();
	}

	public void setPackage(String pkg) {
		m_package = FormDesignerUtils.fastTrim(pkg);
	}

	public Object get(String name) {
		return m_user_objects.get(name);
	}

	public void put(String name, Object obj) {
		m_user_objects.put(name, obj);
	}

}
