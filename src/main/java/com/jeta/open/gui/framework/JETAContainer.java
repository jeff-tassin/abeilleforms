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

package com.jeta.open.gui.framework;

import java.awt.Component;
import java.util.Collection;

/**
 * An interface for containers ( Panels, Dialogs, InternalFrames, and Frames).
 * JETAControllers work with JETAContainers.
 * 
 * @author Jeff Tassin
 */
public interface JETAContainer {
	/**
	 * Enables/Disables the menu/toolbar button associated with the commandid
	 * 
	 * @param commandId
	 *            the id of the command whose button to enable/disable
	 * @param bEnable
	 *            true/false to enable/disable
	 */
	public void enableComponent(String commandId, boolean bEnable);

	/**
	 * Locates the first component found in this container hierarchy that has
	 * the given name. This will recursively search into child containers as
	 * well. If no component is found with the given name, null is returned.
	 * 
	 * @param compName
	 *            the name of the component to search for
	 * @return the named component
	 */
	public Component getComponentByName(String compName);

	/**
	 * Locates all components found in this container hierarchy that has the
	 * given name. This will recursively search into child containers as well.
	 * This method is useful for frame windows that can have multiple components
	 * with the same name. For example, a menu item and toolbar button for the
	 * same command would have the same name.
	 * 
	 * @param compName
	 *            the name of the components to search for
	 * @return a collection of
	 * @see Component objects that have the given name.
	 */
	public Collection getComponentsByName(String compName);

	/**
	 * Returns the UIDirector for this container. UIDirectors are part of this
	 * framework and are responsible for enabling/disabling components based on
	 * the program state. For example, menu items and toolbar buttons must be
	 * enabled or disabled depending on the current state of the frame window.
	 * UIDirectors handle this logic.
	 * 
	 * @return the UIDirector
	 */
	public UIDirector getUIDirector();
}
