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

package com.jeta.open.registry;

import java.util.Hashtable;

/**
 * JETARegistry is a class that is used to manage objects and services for an
 * application. Client classes can obtain an instance to well-known objects
 * and/or services by using the JETARegistry. This class is not thread safe.
 * 
 * @author Jeff Tassin
 */
public class JETARegistry {
	/**
	 * A hash of component names (String) to a named component (Object)
	 */
	private static Hashtable m_components = new Hashtable();

	private static String NULL_HOLDER = "";

	/**
	 * @return a component that is registered with the given name. Null is
	 *         returned if the component has not been registered.
	 */
	synchronized public static Object lookup(String componentName) {
		Object obj = m_components.get(componentName);
		if (obj == NULL_HOLDER)
			return null;
		else
			return obj;
	}

	/**
	 * Register a component with the given name. If a component a component has
	 * already been registered, it is overwritten.
	 */
	synchronized public static void rebind(String componentName, Object componentImpl) {
		if (componentImpl == null)
			m_components.put(componentName, NULL_HOLDER);
		else
			m_components.put(componentName, componentImpl);
	}

	/**
	 * Removes a registered component with the given name.
	 */
	synchronized public static void remove(String componentName) {
		m_components.remove(componentName);
	}

}
