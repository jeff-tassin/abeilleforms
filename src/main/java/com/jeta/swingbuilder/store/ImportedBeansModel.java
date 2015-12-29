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

package com.jeta.swingbuilder.store;

import java.io.Externalizable;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This model contains the list of beans imported by the user for the
 * application. It also includes all classpaths where these beans are located.
 * 
 * @author Jeff Tassin
 */
public class ImportedBeansModel implements Externalizable {
	static final long serialVersionUID = 8638852111334242137L;

	public static final int VERSION = 1;

	public static final String COMPONENT_ID = "imported.beans.model";

	/**
	 * A list of ImportedBeanInfo objects that describe each imported java bean
	 */
	private LinkedList m_imported_beans = new LinkedList();

	/**
	 * A list of URLs for path and JAR files where the java beans are located.
	 */
	private LinkedList m_class_paths = new LinkedList();

	/**
	 * ctor
	 */
	public ImportedBeansModel() {

	}

	/**
	 * Adds an imported bean definition to the model
	 */
	public void addImportedBean(ImportedBeanInfo bi) {
		m_imported_beans.add(bi);
	}

	/**
	 * Adds a URL to the list of URLs where the java beans can be found
	 */
	public void addUrl(URL url) {
		m_class_paths.add(url);
	}

	/**
	 * @return a collection of ImportedBeanInfo objects that define the list of
	 *         imported Java beans defined by the user.
	 */
	public Collection getImportedBeans() {
		return m_imported_beans;
	}

	/**
	 * @return the collection of urls (URL objects) that define the classpath
	 *         where the imported Java beans are found.
	 */
	public Collection getUrls() {
		return m_class_paths;
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_imported_beans = (LinkedList) in.readObject();
		m_class_paths = (LinkedList) in.readObject();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeObject(m_imported_beans);
		out.writeObject(m_class_paths);
	}

}
