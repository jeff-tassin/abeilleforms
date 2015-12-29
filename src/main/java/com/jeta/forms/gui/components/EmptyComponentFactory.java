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

import java.awt.Component;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;

/**
 * Class that creates an 'Empty' component in the grid in design mode. This is
 * for grid cells that don't have a component. We provide a empty JETABean
 * instead of no component in that cell because if we did not the grid lines
 * would be on top of one another if a row or column is completely empty.
 * 
 * @author Jeff Tassin
 */
public class EmptyComponentFactory extends StandardComponentFactory {

	/**
	 * Creates an <code>EmptyComponentFactory</code> instance.
	 */
	public EmptyComponentFactory(ComponentSource compsrc) {
		super(compsrc);
	}

	/**
	 * ComponentFactory implementation. Creates a GridComponent with an null
	 * child.
	 * 
	 * @param compName
	 *            the name of the component.
	 * @param view
	 *            the parent view for the new grid component. This value is
	 *            needed mainly for installing event listeners to the
	 *            GridComponent in design mode.
	 */
	public GridComponent createComponent(String compName, GridView view) throws FormException {
		return super.createComponent((Component) null, view);
	}
}
