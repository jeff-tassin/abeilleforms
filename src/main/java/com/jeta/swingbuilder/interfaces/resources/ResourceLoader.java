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

package com.jeta.swingbuilder.interfaces.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.swing.ImageIcon;

/**
 * This interface is used to load resources for applications or applets
 * 
 * @author Jeff Tassin
 */
public interface ResourceLoader {
	public static final String COMPONENT_ID = "jeta.ResourceLoader";

	/**
	 * Creates a resource(file) relative to the application directory e.g.
	 * createResource( "keybindings/emacs.xml" ); will create the file emacs.xml
	 * in the keybindings directory off of the main subdirectory
	 * 
	 * @param resourceName
	 *            a path and file name of the file to create.
	 */
	public void createResource(String resourceName) throws IOException;

	/**
	 * @returns true if the given resource exists
	 */
	public boolean exists(String resourceName) throws IOException;

	/**
	 * Creates a set of subdirectories under the main resources directory. e.g.
	 * createDirectories( "data/application" ); This will create the directory
	 * structure: TS_HOME/resources/data/application
	 */
	public void createSubdirectories(String directories) throws IOException;

	/**
	 * Deletes a named resource from disk or store
	 */
	public void deleteResource(String resourceName) throws IOException;

	/**
	 * @return an output stream for the given resource name
	 */
	public OutputStream getOutputStream(String resourceName) throws IOException;

	/**
	 * @return an input stream for the given resource name
	 */
	public InputStream getInputStream(String resourceName) throws IOException;

	/**
	 * Opens and returns an input stream for the given resourceName. The
	 * resourceName is relative to the application CLASSPATH (i.e. JAR file).
	 * 
	 * @param resourceName
	 *            the relative name of the resource to open
	 * @return an input stream for the given resourceName.
	 */
	public InputStream getPackagedInputStream(String resourceName) throws IOException;

	/**
	 * @return a string reader for the given resource name
	 */
	public Reader getReader(String resourceName) throws IOException;

	/**
	 * @return a string writer for the given resource name
	 */
	public Writer getWriter(String resourceName) throws IOException;

	/**
	 * Used to provide a custom class loader for certain cases. This is
	 * especially useful during development when we want the resource bundles to
	 * be loaded from the source directories
	 */
	public ClassLoader getClassLoader();

	/**
	 * @return the home directory for the application
	 */
	public String getHomeDirectory();

	/**
	 * Lists all files that are found in the given subdirectory. The
	 * subdirectory is relative to the main application directory.
	 * 
	 * @param subdirectory
	 *            the subdirectory whose file names we are going to return. Only
	 *            the name of the file is returned (no path information)
	 * @param regexFilter
	 *            this is a regular expression filter that you can use to locate
	 *            the files
	 */
	public String[] listFiles(String subdirectory, String regexFilter) throws IOException;

	/**
	 * Utility method that loads an image.
	 * 
	 * @param imageName
	 *            the subdirectory and name of image file (i.e. edit16.png )
	 */
	public ImageIcon loadImage(String imageName);

	/**
	 * Used to provide a custom class loader for certain cases. This is
	 * especially useful during development when we want the resource bundles to
	 * be loaded from the source directories
	 */
	public void setClassLoader(ClassLoader loader);

}
