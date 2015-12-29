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
import java.util.LinkedList;

public class MethodWriter implements DeclarationManager {
	private DeclarationManager m_class_decl_mgr;

	private DeclarationHelper m_local_vars = new DeclarationHelper(null);

	/**
	 * A list of statements that make up this method
	 */
	private LinkedList m_segments = new LinkedList();

	/**
	 * The BeanWriter associated with this return value of this method
	 * declaration
	 */
	private BeanWriter m_return_result;

	/**
	 * The parameters for this method
	 */
	private Class[] m_parameters;

	/**
	 * The name of the method
	 */
	private String m_method_name;

	/**
	 * A list of Strings that make up the method comments. Each String object is
	 * a single comment line.
	 */
	private LinkedList m_comments;

	private String m_access = "public";

	/**
	 * Defines a method with no parameters.
	 */
	public MethodWriter(DeclarationManager declMgr, BeanWriter beanWriter, String suggestedName) {
		m_class_decl_mgr = declMgr;
		m_return_result = beanWriter;
		m_method_name = createMethodName(suggestedName);
	}

	/**
	 * Adds a line to the comments for this method. Do NOT include any /*, *
	 * tokens because they are automatically added here.
	 */
	public void addCommentLine(String comment) {
		if (m_comments == null)
			m_comments = new LinkedList();

		m_comments.add(comment);
	}

	protected String getSignature() {
		StringBuffer sbuff = new StringBuffer();
		sbuff.append(getAccess());
		sbuff.append(' ');
		sbuff.append(DeclarationHelper.trimPackage(getReturnType()));
		sbuff.append(' ');
		sbuff.append(m_method_name);
		sbuff.append("()");
		return sbuff.toString();
	}

	public void build(SourceBuilder builder) {
		if (m_comments != null) {
			builder.println("/**");
			Iterator iter = m_comments.iterator();
			while (iter.hasNext()) {
				String comment = (String) iter.next();
				builder.print(" * ");
				builder.println(comment);
			}
			builder.println(" */");
		}

		builder.print(getSignature());
		builder.openBrace();
		builder.println();
		builder.indent();
		Iterator iter = m_segments.iterator();
		while (iter.hasNext()) {
			CodeSegment seg = (CodeSegment) iter.next();
			seg.output(builder);
		}

		String returnval = getReturnVariable();
		if (returnval != null && returnval.length() > 0) {
			builder.print("return ");
			builder.print(returnval);
			builder.print(';');
			builder.println();
		}

		builder.dedent();
		builder.closeBrace();
	}

	public void addImport(String importDef) {
		m_class_decl_mgr.addImport(importDef);
	}

	public void addMemberVariable(Statement stmt) {
		m_class_decl_mgr.addMemberVariable(stmt);
	}

	public void addMethod(MethodWriter mw) {
		m_class_decl_mgr.addMethod(mw);
	}

	public void addStatement(Statement stmt) {
		m_segments.add(stmt);
	}

	public void addSegment(CodeSegment seg) {
		m_segments.add(seg);
	}

	public void addStatements(Collection stmts) {
		Iterator iter = stmts.iterator();
		while (iter.hasNext()) {
			addStatement((Statement) iter.next());
		}
	}

	public String createMemberVariable(Class compClass, String compName) {
		return m_class_decl_mgr.createMemberVariable(compClass, compName);
	}

	public String createLocalVariable(Class compClass, String compName) {
		return m_local_vars.createVariable(compClass, compName);
	}

	public String createMethodName(String name) {
		return m_class_decl_mgr.createMethodName(name);
	}

	public String getAccess() {
		return m_access;
	}

	public String getMethodName() {
		return m_method_name;
	}

	/**
	 * This returns the name of a method used for loading resources in a form
	 * such as Icons and strings.
	 */
	public String getResourceMethod(Class resourceType) {
		return m_class_decl_mgr.getResourceMethod(resourceType);
	}

	public Class getReturnType() {
		if (m_return_result != null)
			return m_return_result.getResultType();
		else
			return void.class;
	}

	public String getReturnVariable() {
		if (m_return_result != null)
			return m_return_result.getResultVariable();
		else
			return "";
	}

	public void setAccess(String access) {
		m_access = access;
	}

	public void setReturnResult(BeanWriter result) {
		m_return_result = result;
	}

	public boolean isIncludeNonStandard() {
		return m_class_decl_mgr.isIncludeNonStandard();
	}

	public Object get(String name) {
		return m_class_decl_mgr.get(name);
	}

	public void put(String name, Object obj) {
		m_class_decl_mgr.put(name, obj);
	}

}
