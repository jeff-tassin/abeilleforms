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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.open.support.EmptyCollection;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * Defines the settings for a project.
 * 
 * @author Jeff Tassin
 */
public class ProjectModel implements Externalizable {
	static final long serialVersionUID = 3273178622430479212L;

	public static final int VERSION = 3;

	/**
	 * Whether the project file is to be used in a "shared" context. (Across all
	 * team members on a development project)
	 */
	private boolean m_shared;

	/**
	 * For shared project files this is the base directory of the project
	 * itself. All project level java beans will be relative to this base
	 * directory.
	 */
	private String m_project_env_variable;

	/**
	 * Whether the project file is to be used in a "shared" context. (Across all
	 * team members on a development project)
	 */
	private ProjectLevelImportedBeansModel m_imported_beans;

	/**
	 * The list of source paths for the application
	 */
	private LinkedList m_source_paths;

	/**
	 * This is the path to the project file. This is transient because the user
	 * can rename/move the file.
	 */
	private transient String m_project_path;

	/**
	 * The path where classes are stored (can be null).
	 */
	private String m_class_path;

	/**
	 * ctor
	 */
	public ProjectModel() {
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (null == obj)
			return false;

		if (!(obj instanceof ProjectModel))
			return false;

		ProjectModel mObj = (ProjectModel) obj;

		if ((m_shared == mObj.m_shared)
				&& ((null == m_project_env_variable && null == mObj.m_project_env_variable)
						|| (m_project_env_variable != null && m_project_env_variable.equals(mObj.m_project_env_variable)) || (mObj.m_project_env_variable != null && mObj.m_project_env_variable
						.equals(m_project_env_variable)))
				&& ((null == m_class_path && null == mObj.m_class_path) || (m_class_path != null && m_class_path.equals(mObj.m_class_path)) || (mObj.m_class_path != null && mObj.m_class_path
						.equals(m_class_path)))
				&& ((null == m_imported_beans && null == mObj.m_imported_beans) || (m_imported_beans != null && m_imported_beans.equals(mObj.m_imported_beans)) || (mObj.m_imported_beans != null && mObj.m_imported_beans
						.equals(m_imported_beans)))) {
			//
			// Thus far the two projects are equal...final step is to examine
			// the sets of source paths...
			//
			if ((null == m_source_paths && null != mObj.m_source_paths) || (null != m_source_paths && null == mObj.m_source_paths)
					|| (null != m_source_paths && null != mObj.m_source_paths && mObj.m_source_paths.size() != m_source_paths.size())) {
				return false;
			}
			else {
				Object[] paths1 = m_source_paths.toArray();
				Arrays.sort(paths1);

				Object[] paths2 = mObj.m_source_paths.toArray();
				Arrays.sort(paths2);

				for (int i = 0; i < paths1.length; i++) {
					if (!paths1[i].equals(paths2[i]))
						return false;
				}
			}
		}
		else {
			return false;
		}

		return true;
	}

	/**
	 * Adds a source path to this model
	 */
	public void addSourcePath(String path) {
		if (m_source_paths == null) {
			m_source_paths = new LinkedList();
		}
		if ((path != null) && (!"".equals(path))) {
			m_source_paths.add(path);
		}
	}

	public boolean isShared() {
		return m_shared;
	}

	public String getClassPath() {
		return m_class_path;
	}

	public String getProjectEnvVariable() {
		return m_project_env_variable;
	}

	/**
	 * @return a collection of String objects that are the paths where source
	 *         and image files can be found.
	 */
	public Collection getSourcePaths() {
		if (m_source_paths == null)
			return EmptyCollection.getInstance();
		else
			return m_source_paths;
	}

	/**
	 * @return the path to the project file
	 */
	public String getProjectPath() {
		return m_project_path;
	}

	public File getProjectRootDir() {
		String tempPath = null;

		if (m_shared) {
			if (m_project_env_variable != null) {
				String directory = System.getenv(m_project_env_variable);
				try {
					File file = new File(directory);
					tempPath = file.getAbsolutePath();
				} catch (Exception e) {
				}
			}
		}
		else {
			tempPath = m_project_path;
			File file = new File(tempPath);
			if (!file.isDirectory()) {
				tempPath = file.getParent();
			}
		}

		File rootDir = null;

		if ((tempPath != null) && (!"".equals(tempPath))) {
			String path = FormDesignerUtils.fastTrim(tempPath);
			char c = File.separatorChar;
			if (c == '\\')
				path = path.replace('/', File.separatorChar);
			else
				path = path.replace('\\', File.separatorChar);

			try {
				rootDir = new File(path);
			} catch (Exception e) {
				rootDir = null;
			}
		}

		return rootDir;
	}

	/**
	 * @return the project level imported beans model
	 */
	public ProjectLevelImportedBeansModel getProjectLevelImportedBeansModel() {
		if (this.m_imported_beans == null) {
			this.m_imported_beans = new ProjectLevelImportedBeansModel(this.m_project_env_variable);
		}
		return this.m_imported_beans;
	}

	/**
	 * Set the path to the project file
	 */
	public void setProjectPath(String path) {
		this.m_project_path = path;
	}

	public void setClassPath(String classPath) {
		this.m_class_path = classPath;
	}

	public void setProjectEnvVariable(String envVariable) {
		getProjectLevelImportedBeansModel().setEnvVar(envVariable);
		this.m_project_env_variable = envVariable;
	}

	public void setShared(boolean m_shared) {
		this.m_shared = m_shared;

	}

	public void setProjectLevelImportedBeans(ProjectLevelImportedBeansModel imported_beans) {
		if (imported_beans != null) {
			this.m_imported_beans = imported_beans;
			this.m_imported_beans.setEnvVar(this.m_project_env_variable);
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_source_paths = (LinkedList) in.readObject();
		if (version >= 2) {
			m_class_path = (String) in.readObject();
		}

		LinkedList fixed_src_paths = new LinkedList();
		if (m_source_paths != null) {
			Iterator iter = m_source_paths.iterator();
			while (iter.hasNext()) {
				String path = (String) iter.next();
				path = FormUtils.fixPath(path);
				fixed_src_paths.add(path);
			}
		}
		m_source_paths = fixed_src_paths;
		m_class_path = FormUtils.fixPath(m_class_path);

		if (version >= 3) { 
			m_shared = in.readBoolean();
			m_project_env_variable = (String) in.readObject();
			m_imported_beans = (ProjectLevelImportedBeansModel) in.readObject();
			if (m_imported_beans == null) {
				m_imported_beans = new ProjectLevelImportedBeansModel(m_project_env_variable);
			}
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeObject(m_source_paths);
		out.writeObject(m_class_path);
		out.writeBoolean(m_shared);
		out.writeObject(m_project_env_variable);
		out.writeObject(m_imported_beans);
	}
}
