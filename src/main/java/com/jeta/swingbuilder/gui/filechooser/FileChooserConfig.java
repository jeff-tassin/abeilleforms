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

package com.jeta.swingbuilder.gui.filechooser;

import java.awt.Component;

import javax.swing.JFileChooser;

/**
 * This class is used to configure the file chooser dialog. Parameters such as
 * initial directory, file filters, etc are set in this config class.
 * 
 * @author Jeff Tassin
 */
public class FileChooserConfig {

	/**
	 * This is used when loading files of a given type. It is not the file
	 * filter. Rather is is used to store the directory last accessed for this
	 * type. For example, if the type is an ".img", we want to store the
	 * directory last accessed for images. However, if the file is a ".txt", we
	 * don't want to go to the last images directory but rather the last .txt
	 * directory.
	 */
	private String m_file_type;

	/**
	 * The mode for the JFileChooser (e.g. FILES_ONLY )
	 */
	private int m_mode = JFileChooser.FILES_ONLY;

	/**
	 * The file filters.
	 */
	private TSFileFilter[] m_file_filter;

	/**
	 * The directory to open in the file chooser.
	 */
	private String m_initial_directory;

	private Component m_parent = null;

	/**
	 * ctor
	 */
	public FileChooserConfig(String initialDirectory, String type, int mode, TSFileFilter fileFilter) {
		m_initial_directory = initialDirectory;
		m_file_type = type;
		m_mode = mode;
		if (fileFilter != null)
			m_file_filter = new TSFileFilter[] { fileFilter };
	}

	/**
	 * ctor
	 */
	public FileChooserConfig(String type, int mode, TSFileFilter fileFilter) {
		m_file_type = type;
		m_mode = mode;
		if (fileFilter != null)
			m_file_filter = new TSFileFilter[] { fileFilter };
	}

	/**
	 * ctor
	 */
	public FileChooserConfig(String type, TSFileFilter fileFilter) {
		m_file_type = type;
		if (fileFilter != null)
			m_file_filter = new TSFileFilter[] { fileFilter };
	}

	/**
	 * ctor
	 */
	public FileChooserConfig(String initialDir, String type, TSFileFilter fileFilter) {
		m_initial_directory = initialDir;
		m_file_type = type;
		if (fileFilter != null)
			m_file_filter = new TSFileFilter[] { fileFilter };
	}

	/**
	 * ctor
	 */
	public FileChooserConfig(String type, int mode, TSFileFilter[] fileFilters) {
		m_file_type = type;
		m_mode = mode;
		m_file_filter = fileFilters;
	}

	/**
	 * ctor
	 */
	public FileChooserConfig(String initialDirectory, String type, int mode, TSFileFilter[] fileFilters) {
		m_initial_directory = initialDirectory;
		m_file_type = type;
		m_mode = mode;
		m_file_filter = fileFilters;
	}

	public String getFileType() {
		return m_file_type;
	}

	/**
	 * The mode for the JFileChooser (e.g. FILES_ONLY )
	 */
	public int getMode() {
		return m_mode;
	}

	/**
	 * The file filters.
	 */
	public TSFileFilter[] getFileFilters() {
		return m_file_filter;
	}

	/**
	 * The directory to open in the file chooser.
	 */
	public String getInitialDirectory() {
		return m_initial_directory;
	}

	public void setInitialDirectory(String dir) {
		m_initial_directory = dir;
	}

	public Component getParentComponent() {
		return m_parent;
	}

	public void setParentComponent(Component comp) {
		m_parent = comp;
	}
}
