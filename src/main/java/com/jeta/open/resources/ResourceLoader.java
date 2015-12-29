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

package com.jeta.open.resources;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

/**
 * Defines a set of methods used to load resources such as images, property
 * files, and other types of files from the CLASSPATH. It provides a layer of
 * abstraction so the application is not bound to a particular loading stategy.
 * Note that a full ClassLoader could be used instead, but this is a little
 * easier to code with.
 * 
 * @author Jeff Tassin
 */
public interface ResourceLoader {
	public static final String COMPONENT_ID = "jeta.resourceloader";

	/**
	 * Opens and returns an input stream for the given resourceName. The
	 * resourceName is relative to the application CLASSPATH (i.e. JAR file).
	 * 
	 * @param resourceName
	 *            the relative name of the resource to open
	 * @return an input stream for the given resourceName.
	 */
	public InputStream getResourceAsStream(String resourceName) throws IOException;

	/**
	 * Used to provide a custom class loader for certain cases. This is
	 * especially useful during development when we want the resource bundles to
	 * be loaded from the source directories or if you want to provide a custom
	 * class loader.
	 */
	public ClassLoader getClassLoader();

	/**
	 * Utility method that loads an image from the CLASSPATH.
	 * 
	 * @param imageName
	 *            the subdirectory and name of image file (i.e.
	 *            images/edit16.gif )
	 */
	public ImageIcon loadImage(String imageName);

	/**
	 * Used to provide a custom class loader for certain cases. This is
	 * especially useful during development when we want the resource bundles to
	 * be loaded from the source directories
	 */
	public void setClassLoader(ClassLoader loader);

}
