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

package com.jeta.open.support;

import java.awt.Component;
import java.util.Collection;

/**
 * Interface used to define a service for locating child components within a
 * container that have a given name. Specialized implementations of this
 * interface are responsible for managing the parent container assocation.
 * 
 * @author Jeff Tassin
 */
public interface ComponentFinder {

	/**
	 * Recursively searches an associated parent container for a component with
	 * the given name. The first component found that has the name is returned.
	 * Null is returned if no component is found.
	 */
	public Component getComponentByName(String compName);

	/**
	 * Recursively searches an associated parent container for all components
	 * with the given name. An empty collection is returned if no components are
	 * found with the given name.
	 * 
	 * @return a collection of Component objects that have the given name.
	 */
	public Collection getComponentsByName(String compName);

	/**
	 * Recursively searches an associated parent container for all components
	 * that are named. An empty collection is returned if no names components
	 * exist.
	 * 
	 * @return a collection of all named Component objects.
	 */
	public Collection getAllNamedComponents();

	/**
	 * Tells the implementation that any cached components should be flushed and
	 * reloaded because the parent container might have changed.
	 */
	public void reset();
}
