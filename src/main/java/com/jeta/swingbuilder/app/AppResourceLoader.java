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

package com.jeta.swingbuilder.app;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.ImageIcon;

import com.jeta.swingbuilder.interfaces.resources.ResourceLoader;

/**
 * This class is an implementation of a ResourceLoader. It insulates the
 * application code from having any need to know about the local file system
 * directory structure. It is also useful for debugging and development so we
 * can redirect resource request to debug files if needed.
 * 
 * @author Jeff Tassin
 */
public class AppResourceLoader implements ResourceLoader {
	/** path to home directory for application */
	private String m_tshome;

	/** path to the images directory */
	private String m_imagespath;

	private ClassLoader m_classloader;

	/** an empty icon if a resource cannot be loaded */
	private static ImageIcon m_empty_icon;

	public AppResourceLoader(String tshome) {
		m_tshome = tshome;
		m_imagespath = "";
	}

	public AppResourceLoader(String tshome, String imagesPath) {
		m_tshome = tshome;
		m_imagespath = imagesPath;
	}

	/**
	 * Creates a set of subdirectories under the main resources directory. e.g.
	 * createDirectories( "data/application" ); This will create the directory
	 * structure: TS_HOME/resources/data/application
	 */
	public void createSubdirectories(String directories) throws IOException {
		StringBuffer abspath = new StringBuffer(m_tshome);
		// allow either \ or / path delimiter
		StringTokenizer tz = new StringTokenizer(directories, "/\\");
		while (tz.hasMoreElements()) {
			String subpath = (String) tz.nextToken();
			abspath.append(File.separatorChar);
			abspath.append(subpath);
			File dir = new File(abspath.toString());
			dir.mkdir();
		}
	}

	/**
	 * Creates a resource(file) relative to the application directory e.g.
	 * createResource( "keybindings/emacs.xml" ); will create the file emacs.xml
	 * in the keybindings directory off of the main subdirectory
	 * 
	 * @param resourceName
	 *            a path and file name of the file to create.
	 */
	public void createResource(String resourceName) throws IOException {
		StringBuffer abspath = new StringBuffer(m_tshome);
		// allow either \ or / path delimiter
		StringTokenizer tz = new StringTokenizer(resourceName, "/\\");
		while (tz.hasMoreElements()) {
			String subpath = (String) tz.nextToken();
			abspath.append(File.separatorChar);
			abspath.append(subpath);
			if (tz.hasMoreElements()) {
				File dir = new File(abspath.toString());
				dir.mkdir();
			}
		}
	}

	/**
	 * Deletes a named resource from disk or store
	 */
	public void deleteResource(String resourceName) throws IOException {
		File f = new File(m_tshome + File.separatorChar + resourceName);
		f.delete();
	}

	/**
	 * @returns true if the given resource exists
	 */
	public boolean exists(String resourceName) throws IOException {
		File f = new File(m_tshome + File.separatorChar + resourceName);
		return f.exists();
	}

	/**
	 * @return a custom class loader for the application
	 */
	public ClassLoader getClassLoader() {
		if (m_classloader == null)
			return AppResourceLoader.class.getClassLoader();
		else
			return m_classloader;
	}

	/**
	 * @return the absolute file name (with the ts home path )
	 */
	String getAbsoluteFileName(String resourceName) {
		StringBuffer buff = new StringBuffer();
		buff.append(m_tshome);
		buff.append(File.separatorChar);
		buff.append(resourceName);
		return buff.toString();
	}

	/**
	 * @return the home directory for the application
	 */
	public String getHomeDirectory() {
		return m_tshome;
	}

	/**
	 * Opens and returns an input stream for the given resourceName. The
	 * resourceName is relative to the application home directory.
	 * 
	 * @param resourceName
	 *            the relative name of the resource to open
	 * @return an input stream for the given resourceName.
	 */
	public InputStream getInputStream(String resourceName) throws IOException {
		File f = new File(getAbsoluteFileName(resourceName));
		return new BufferedInputStream(new FileInputStream(f));
	}

	/**
	 * Opens and returns an output stream for the given resourceName. The
	 * resourceName is relative to the application home directory.
	 * 
	 * @param resourceName
	 *            the relative name of the resource to open
	 * @return an input stream for the given resourceName.
	 */
	public OutputStream getOutputStream(String resourceName) throws IOException {
		String xmlfilename = getAbsoluteFileName(resourceName);
		return new BufferedOutputStream(new FileOutputStream(xmlfilename));
	}

	/**
	 * Opens and returns an input stream for the given resourceName. The
	 * resourceName is relative to the application CLASSPATH (i.e. JAR file).
	 * 
	 * @param resourceName
	 *            the relative name of the resource to open
	 * @return an input stream for the given resourceName.
	 */
	public InputStream getPackagedInputStream(String resourceName) throws IOException {
		ClassLoader classloader = getClassLoader();
		return classloader.getResourceAsStream(resourceName);
	}

	public Reader getReader(String resourceName) throws IOException {
		String filename = getAbsoluteFileName(resourceName);
		return new BufferedReader(new FileReader(filename));
	}

	public Writer getWriter(String resourceName) throws IOException {
		String filename = getAbsoluteFileName(resourceName);
		return new BufferedWriter(new FileWriter(filename));
	}

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
	public String[] listFiles(String subdirectory, String regexFilter) throws IOException {
		String fname = getAbsoluteFileName(subdirectory);
		File dir = new File(fname);

		Pattern pat = null;
		try {
			pat = Pattern.compile(regexFilter);
		} catch (PatternSyntaxException e) {
			throw new IllegalArgumentException(regexFilter);
		}

		final Pattern pattern = pat;
		String[] filenames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				Matcher matcher = pattern.matcher(name);
				if (matcher.find())
					return true;
				else
					return false;
			}
		});

		if (filenames == null)
			return new String[0];
		else
			return filenames;
	}

	/**
	 * Helper utility to load an image file from the application images
	 * directory
	 * 
	 * @param the
	 *            name (and optional sub directory ) of the file to load
	 */
	public ImageIcon loadImage(String imageName) {
		try {
			ClassLoader classloader = getClassLoader();
			java.net.URL url = classloader.getResource(m_imagespath + imageName);
			ImageIcon icon = new ImageIcon(url);
			return icon;
		} catch (Exception e) {
			System.out.println("invalid url: " + m_imagespath + imageName);
			e.printStackTrace();
		}

		synchronized (AppResourceLoader.class) {

			if (m_empty_icon == null) {
				BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
				m_empty_icon = new ImageIcon(img);
			}
		}
		return m_empty_icon;
	}

	public void setClassLoader(ClassLoader loader) {
		m_classloader = loader;
	}

}
