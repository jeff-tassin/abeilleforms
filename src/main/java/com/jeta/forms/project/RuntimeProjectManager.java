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

import com.jeta.open.registry.JETARegistry;
import com.jeta.open.resources.ResourceLoader;

/**
 * Concrete implementation of ProjectManager for loading form resources during
 * runtime (as opposed to design time which uses a different ProjectManager).
 * 
 * @author Jeff Tassin
 */
public class RuntimeProjectManager implements ProjectManager {

	/**
	 * Creates a <code>RuntimeProjectManager</code> instance.
	 */
	public RuntimeProjectManager() {

	}

	/**
	 * Clears any cached resources such as images. No op for this
	 * implementation.
	 */
	public void clearResourceCache() {

	}

	/**
	 * This method is not required when running in run-mode.
	 * 
	 * @return a valid absolute path given a relative path.
	 */
	public String getAbsolutePath(String relativePath) {
		return null;
	}

	/**
	 * This method is not required when running in run-mode.
	 * 
	 * @return a valid relative package/filename given an absolute path.
	 */
	public String getRelativePath(String absPath) {
		assert (false);
		return null;
	}

	/**
	 * This method is not required when running in run-mode.
	 * 
	 * @return true if the given absolute path lies within one of the source
	 *         directories.
	 */
	public boolean isValidAbsolutePath(String path) {
		assert (false);
		return false;
	}

	/**
	 * This method is not required when running in run-mode.
	 * 
	 * @return true if the given relative resource exists and is a file
	 */
	public boolean isValidResource(String relpath) {
		assert (false);
		return false;
	}

	/**
	 * Utility method that loads an image from the CLASSPATH.
	 * 
	 * @param imageName
	 *            the subdirectory and name of image file (i.e.
	 *            images/edit16.gif )
	 * @return a valid Icon for the given imageName. If the imageName is not
	 *         found in the CLASSPATH, null is returned.
	 */
	public ImageIcon loadImage(String imageName) {
		try {
			if (imageName != null)
				imageName = imageName.replace('\\', '/');
			ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
			return loader.loadImage(imageName);
		} catch (Exception e) {
			System.out.println("Error loading image: " + imageName);
			e.printStackTrace();
			return null;
		}
	}
}
