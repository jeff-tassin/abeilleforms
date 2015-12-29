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

public interface DeclarationManager {
	public void addImport(String importPath);

	public void addMethod(MethodWriter mw);

	public void addMemberVariable(Statement stmt);

	public String createMemberVariable(Class compClass, String compName);

	public String createLocalVariable(Class compClass, String compName);

	public String createMethodName(String name);

	/**
	 * This returns the name of a method used for loading resources in a form
	 * such as Icons and strings.
	 */
	public String getResourceMethod(Class resourceType);

	public Object get(String name);

	public void put(String name, Object obj);

	public boolean isIncludeNonStandard();

}
