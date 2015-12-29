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

package com.jeta.swingbuilder.project;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.project.RuntimeProjectManager;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;
import com.jeta.swingbuilder.store.ProjectModel;

/**
 * Concrete implementation of ProjectManager
 * 
 * @author Jeff Tassin
 */
public class DefaultProjectManager implements ProjectManager {
	/**
	 * A list of source directories for this project. We use source directories
	 * to locate forms when given a relative path.
	 */
	private LinkedList m_source_dirs = new LinkedList();

	/** an empty icon if a resource cannot be loaded */
	private static ImageIcon m_empty_icon;

	/** cache of images */
	private Hashtable m_image_cache = new Hashtable();

	/**
	 * This is used to locate resources for forms used by the Abeille Form
	 * Builder itself.
	 */
	private RuntimeProjectManager m_runtime = new RuntimeProjectManager();

	/**
	 * The current project
	 */
	private ProjectModel m_project;

	/**
	 * ctor
	 */
	public DefaultProjectManager() {

	}

	/**
	 * ctor
	 */
	public DefaultProjectManager(ProjectModel pmodel) {
		setProject(pmodel);
	}

	/**
	 * Clears any cached resources
	 */
	public void clearResourceCache() {
		m_image_cache.clear();
	}

	/**
	 * @return a valid absolute path given a relative path. Searches the
	 *         registered source paths and determines if the relativePath
	 *         (includes filename) exists within one of the source paths.
	 */
	public String getAbsolutePath(String relativePath) {
		if (relativePath == null)
			relativePath = "";

		relativePath = FormUtils.fixPath(relativePath);

		if (isJETAResource(relativePath)) {
			return m_runtime.getAbsolutePath(relativePath);
		}
		Iterator iter = m_source_dirs.iterator();
		while (iter.hasNext()) {
			String path = (String) iter.next();
			File f = new File(path + File.separatorChar + relativePath);
			if (f.exists()) {
				if (f.isFile()) {
					return f.getPath();
				}
				else {
					// should never happen
					assert (false);
				}
			}
		}
		return null;
	}

	/**
	 * @return the current project
	 */
	public ProjectModel getProject() {
		return m_project;
	}

	/**
	 * @return a valid relative package/filename given an absolute path. The
	 *         relative path is determined using the source directories. If the
	 *         path does not lie within a source directory, null is returned.
	 */
	public String getRelativePath(String absPath) {
		if (isJETAResource(absPath))
			return m_runtime.getRelativePath(absPath);

		if (absPath == null)
			return null;

		Iterator iter = m_source_dirs.iterator();
		while (iter.hasNext()) {
			String src_path = (String) iter.next();
			if (absPath.indexOf(src_path) == 0) {
				return absPath.substring(src_path.length() + 1, absPath.length());
			}
		}
		return null;
	}

	/**
	 * @return true if the given resource is a JETA resource for the Abeille
	 *         Forms Builder. JETA resources begin with "com.jeta" 
	 */
	private boolean isJETAResource(String resource) {
		if (FormUtils.isDesignMode())
			return false;
		else if (resource == null)
			return false;
		else if (resource.indexOf("com/jeta") >= 0)
			return true;
		else
			return false;
	}

	/**
	 * @return true if the given absolute path lies within one of the source
	 *         directories.
	 */
	public boolean isValidAbsolutePath(String path) {
		if (isJETAResource(path))
			return m_runtime.isValidAbsolutePath(path);

		Iterator iter = m_source_dirs.iterator();
		while (iter.hasNext()) {
			String src_path = (String) iter.next();
			if (path.indexOf(src_path) == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if the given relative resource exists and is a file
	 */
	public boolean isValidResource(String relpath) {
		// System.out.println( "DefaultProjectmanager.isValidResource: " +
		// relpath
		// );
		if (relpath == null)
			return false;

		if (relpath.length() > 0) {
			if (relpath.charAt(0) == '\\' || relpath.charAt(0) == '/') {
				relpath = relpath.substring(1, relpath.length());
			}
		}

		Iterator iter = m_source_dirs.iterator();
		while (iter.hasNext()) {
			String src_path = (String) iter.next();
			String full_path = src_path + File.separatorChar + relpath;

			// System.out.println( "DefaultProjectmanager.isValidResource:
			// full_path: " + full_path );

			File f = new File(full_path);
			if (f.isFile())
				return true;
		}
		return false;
	}

	/**
	 * Utility method that loads an image from the CLASSPATH.
	 * 
	 * @param imageName
	 *            the subdirectory and name of image file (i.e.
	 *            images/edit16.gif )
	 */
	public ImageIcon loadImage(String imageName) {
		ImageIcon result = null;
		boolean cache_images = TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_CACHE_IMAGES, true);
		if (cache_images) {
			synchronized (this) {
				result = (ImageIcon) m_image_cache.get(imageName);
				if (result != null)
					return result;
			}
		}

		
		if (isJETAResource(imageName)) {
			result = m_runtime.loadImage(imageName);
		} else {
			try {
				String path = getAbsolutePath(imageName);
				if (path != null) {
					/**
					 * The toolkit should be used to create the image, otherwise
					 * it may be pulled from the toolkit image cache.
					 */
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					ImageIcon image = new ImageIcon(toolkit.createImage(path));
					result = image;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result == null && m_empty_icon == null) {
				int width = 16;
				int height = 16;
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				java.awt.Graphics2D bg = img.createGraphics();
				bg.setColor(javax.swing.UIManager.getColor("control"));
				bg.fillRect(0, 0, width, height);
				bg.setColor(java.awt.Color.red);
				bg.drawRect(0, 0, width - 1, height - 1);
				bg.drawLine(0, 0, width - 1, height - 1);
				bg.drawLine(0, height - 1, width - 1, 0);
				bg.dispose();
				m_empty_icon = new ImageIcon(img);
			}
		}

		if (result == null)
			result = m_runtime.loadImage(imageName);
			if ( result == null ) {
				result = m_empty_icon;
			}
		else {
			if (cache_images) {
				synchronized (this) {
					m_image_cache.put(imageName, result);
				}
			}
		}
		return result;
	}

	/**
	 * Sets the current project
	 */
	public void setProject(ProjectModel pmodel) {
		m_project = pmodel;
		m_source_dirs.clear();
		if (pmodel != null) {
			File rootDir = pmodel.getProjectRootDir();

			
			Collection src_paths = pmodel.getSourcePaths();
			Iterator iter = src_paths.iterator();
			while (iter.hasNext()) {
				String src_path = (String) iter.next();
				if (src_path.equals("."))
					src_path = rootDir.getPath();

				if (File.separatorChar == '/')
					src_path = src_path.replace('\\', File.separatorChar);
				else if (File.separatorChar == '\\')
					src_path = src_path.replace('/', File.separatorChar);

				File f = new File(rootDir, src_path);
				try {
					if (f.isDirectory()) {
						//FormsLogger.debug("DefaultProjectManager loading (rel)project path: " + f.getCanonicalPath() + "  hash: " + this.hashCode());
						m_source_dirs.add(f.getCanonicalPath());
					} else {
						f = new File(src_path);
						if (f.isDirectory()) {
						//	FormsLogger.debug("DefaultProjectManager loading (abs)project path: " + src_path);
							m_source_dirs.add(src_path);
						}
						else {
							System.err.println("unable to load project directory: " + src_path);
						}
					}
				} catch (Exception e) {
					FormsLogger.severe(e);
				}
			}
		}
	}

}
