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

package com.jeta.forms.gui.components;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;

/**
 * A <code>ComponentFactory</code> is responsible for creating GridComponent
 * objects for a GridView. A GridComponent is a container for a JETABean and
 * maintains information such as grid cell location and col/row span. There are
 * several types of GridComponents. For example, there is a GridComponent for
 * handling standard Swing components. There is also a GridComponent for
 * handling nested forms. Each type has different initialization requirements.
 * This is especially true during design mode when event listerners are
 * installed on the GridComponents. See:
 * {@link com.jeta.forms.gui.form.GridComponent }
 * 
 * @author Jeff Tassin
 */
public interface ComponentFactory {
	/**
	 * Creates a GridComponent instances with the given name and the given
	 * parent view. However, the GridComponent is not added to the view in this
	 * call.
	 * 
	 * @param compName
	 *            the name of the component.
	 * @param parentView
	 *            the parent view for the new grid component. This value is
	 *            needed mainly for installing event listeners to the
	 *            GridComponent in design mode.
	 */
	public GridComponent createComponent(String compName, GridView parentView) throws FormException;

	/**
	 * Returns the current component source object.
	 * 
	 * @return the current component source.
	 */
	ComponentSource getComponentSource();

	/**
	 * Sets the component source object associated with this factory.
	 * 
	 * @param compsrc
	 *            the current component source to associate with this factory.
	 */
	void setComponentSource(ComponentSource compsrc);
}
