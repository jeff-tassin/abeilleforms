/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.project;

import javax.swing.ImageIcon;

/**
 * This interface defines methods for project management. A project is composed
 * of a set of 'source' directories. The application uses these diectories to
 * form absolute/relative paths for forms and image resources when in design
 * mode. When in run mode, the forms are loaded from the classpath. When in
 * design mode, forms are loaded from the source paths. It is the responsibility
 * of the ProjectManager to handle loading of form and image resources.
 * 
 * @author Jeff Tassin
 */
public interface ProjectManager {
	/**
	 * component id for the JETARegistry
	 */
	public static final String COMPONENT_ID = "jeta.forms.projectManager";

	/**
	 * Clears any cached resources such as images.
	 */
	public void clearResourceCache();

	/**
	 * Returns a valid absolute path for a resource given only a relative path.
	 * The the registered source paths are searched to determine if the
	 * relativePath exists within one of the source paths. The source paths are
	 * set by the user for a given project.
	 * 
	 * @return a valid absolute path given a relative path. Null is returned if
	 *         no resource matches the given relative path.
	 */
	public String getAbsolutePath(String relativePath);

	/**
	 * Returns the relative path given an absolute path to a resource. The
	 * relative path is determined using the source directories. If the path
	 * does not lie within a source directory, null is returned.
	 * 
	 * @return a valid relative package/filename given an absolute path.
	 */
	public String getRelativePath(String absPath);

	/**
	 * Determines if the given absolute path lies within one of the registered
	 * source directories.
	 * 
	 * @return true if the given absolute path lies within one of the source
	 *         directories.
	 */
	public boolean isValidAbsolutePath(String path);

	/**
	 * Returns true if the given relative path exists and is a file.
	 * 
	 * @return true if the given relative resource exists and is a file
	 */
	public boolean isValidResource(String relpath);

	/**
	 * Utility method that loads an image from the CLASSPATH.
	 * 
	 * @param imageName
	 *            the subdirectory and name of image file (i.e.
	 *            images/edit16.gif )
	 */
	public ImageIcon loadImage(String imageName);

}
