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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This model contains the list of beans imported by the user for the
 * application. It also includes all classpaths where these beans are located.
 * 
 * @author Jeff Tassin
 */
public class ProjectLevelImportedBeansModel implements Externalizable {
	static final long serialVersionUID = 4699864132660581997L;

	public static final int VERSION = 2;

	public static final String COMPONENT_ID = "project.level.imported.beans.model";

	private String m_env_var;

	private transient File m_root_dir;

	/**
	 * A list of ImportedBeanInfo objects that describe each imported java bean
	 */
	private LinkedList m_imported_beans = new LinkedList();

	/**
	 * A list of URLs for path and JAR files where the java beans are located.
	 */
	private LinkedList m_relative_class_paths = new LinkedList();

	/**
	 * ctor
	 */
	public ProjectLevelImportedBeansModel() {
	}

	public ProjectLevelImportedBeansModel(String envVar) {
		this.m_env_var = envVar;
		this.m_root_dir = getRootDir(m_env_var);
	}

	/**
	 * @param envVar
	 */
	private File getRootDir(String envVar) {
		if (envVar != null) {
			String directory = System.getenv(envVar);
			if ((directory != null) && (!"".equals(directory))) {
				try {
					File tempFile = new File(directory);
					if (tempFile.exists() && tempFile.isDirectory()) {
						return tempFile;
					}
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * Adds an imported bean definition to the model
	 */
	public void addImportedBean(ImportedBeanInfo bi) {
		if (m_imported_beans == null) {
			m_imported_beans = new LinkedList();
		}
		if (bi != null) {
			m_imported_beans.add(bi);
		}
	}

	/**
	 * Adds a relative classpath to the list of classpaths where the java beans
	 * can be found
	 */
	public void addRelativeClasspath(String relativeClasspath) {
		if (m_relative_class_paths == null) {
			m_relative_class_paths = new LinkedList();
		}
		if ((relativeClasspath != null) && (!"".equals(relativeClasspath))) {
			m_relative_class_paths.add(relativeClasspath);
		}
	}

	/**
	 * @return a collection of ImportedBeanInfo objects that define the list of
	 *         imported Java beans defined by the user.
	 */
	public Collection getImportedBeans() {
		return m_imported_beans;
	}

	/**
	 * @return the collection of relative classpaths (String objects) that
	 *         define the classpath where the imported Java beans are found.
	 */
	public Collection getRelativeClasspaths() {
		return m_relative_class_paths;
	}

	/**
	 * @return the collection of urls (URL objects) that define the classpath
	 *         where the imported Java beans are found.
	 */
	public Collection getUrls() {
		LinkedList urls = new LinkedList();
		for (ListIterator i = m_relative_class_paths.listIterator(); i.hasNext();) {
			String relClasspath = (String) i.next();
			File file = new File(this.m_root_dir, relClasspath);
			try {
				urls.addLast(file.toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return urls;
	}

	public String getEnvVar() {
		return this.m_env_var;
	}

	public void setEnvVar(String envVar) {
		this.m_env_var = envVar;
		this.m_root_dir = getRootDir(this.m_env_var);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (null == obj)
			return false;

		if (!(obj instanceof ProjectLevelImportedBeansModel))
			return false;

		ProjectLevelImportedBeansModel mObj = (ProjectLevelImportedBeansModel) obj;

		if ((null == m_env_var && null != mObj.getEnvVar()) || (null != m_env_var && null == mObj.getEnvVar())
				|| (null != m_env_var && null != mObj.getEnvVar() && !mObj.getEnvVar().equals(m_env_var))) {
			return false;
		}
		else {
			if ((null == m_relative_class_paths && null != mObj.getRelativeClasspaths())
					|| (null != m_relative_class_paths && null == mObj.getRelativeClasspaths())
					|| (null != m_relative_class_paths && null != mObj.getRelativeClasspaths() && mObj.getRelativeClasspaths().size() != m_relative_class_paths
							.size())) {
				return false;
			}
			else {
				Object[] a1 = m_relative_class_paths.toArray();
				Arrays.sort(a1);

				Object[] a2 = mObj.getRelativeClasspaths().toArray();
				Arrays.sort(a2);

				for (int i = 0; i < a1.length; i++) {
					if (!a1[i].equals(a2[i]))
						return false;
				}

				if ((null == m_imported_beans && null != mObj.getImportedBeans()) || (null != m_imported_beans && null == mObj.getImportedBeans())
						|| (null != m_imported_beans && null != mObj.getImportedBeans() && mObj.getImportedBeans().size() != m_imported_beans.size())) {
					return false;
				}
				else {
					Object[] beans1 = m_imported_beans.toArray();
					Arrays.sort(beans1);

					Object[] beans2 = mObj.getImportedBeans().toArray();
					Arrays.sort(beans2);

					for (int i = 0; i < beans1.length; i++) {
						if (!beans1[i].equals(beans2[i]))
							return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_imported_beans = (LinkedList) in.readObject();
		m_relative_class_paths = (LinkedList) in.readObject();
		if (version >= 2) {
			m_env_var = (String) in.readObject();
			m_root_dir = getRootDir(m_env_var);
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeObject(m_imported_beans);
		out.writeObject(m_relative_class_paths);
		out.writeObject(m_env_var);
	}
}
