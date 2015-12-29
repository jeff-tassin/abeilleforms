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

package com.jeta.forms.gui.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Responsible for instantiating imported beans.
 * 
 * @author Jeff Tassin
 */
public class URLClassLoaderHelper {
	/**
	 * A list of URLs
	 */
	private ArrayList m_url_list = new ArrayList();

	/**
	 * An array of URLs that matches the url_list
	 */
	private URL[] m_urls;

	private URLClassLoader m_url_loader;

	/**
	 * ctor
	 */
	public URLClassLoaderHelper() {

	}

	/**
	 * ctor
	 */
	public URLClassLoaderHelper(Collection urls) {
		Iterator iter = urls.iterator();
		while (iter.hasNext()) {
			addUrl((URL) iter.next());
		}
	}

	/**
	 * Adds a url to this list
	 */
	public void addUrl(URL url) {
		m_url_list.add(url);
		m_urls = null;
		m_url_loader = null;
	}

	/**
	 * @return the underlying class loader
	 */
	public ClassLoader getClassLoader() throws FormException {
		if (m_urls == null)
			m_urls = (URL[]) m_url_list.toArray(new URL[0]);

		if (m_urls.length > 0 && m_url_loader == null)
			m_url_loader = new URLClassLoader(m_urls);

		return m_url_loader;
	}

	/**
	 * @return the class for the given bean
	 */
	public Class getClass(String className) throws FormException {
		try {
			getClassLoader();

			if (m_urls.length == 0) {
				Class c = Class.forName(className);
				return c;
			}
			else {
				ClassLoader loader = getClassLoader();
				Class c = loader.loadClass(className);
				return c;
			}
		} catch (Exception e) {
			if (e instanceof FormException)
				throw (FormException) e;
			else
				throw new FormException(e);
		}
	}

	public Object createObject(String beanName) throws FormException {
		try {
			Class c = getClass(beanName);
			return c.newInstance();
		} catch (Exception e) {
			if (e instanceof FormException)
				throw (FormException) e;
			else
				throw new FormException(e);
		}
	}

	/**
	 * Prints this object state to the console
	 */
	public void print() {
		try {
			getClassLoader();
			System.out.println("URLClassLoaderHelper  m_urls.length = " + m_urls.length);
			for (int index = 0; index < m_urls.length; index++) {
				System.out.println(" url: " + m_urls[index]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
