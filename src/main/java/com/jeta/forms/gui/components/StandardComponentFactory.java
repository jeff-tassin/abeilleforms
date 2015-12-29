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

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.StandardComponent;

/**
 * A <code>StandardCompnentFactory</code> is a base class for creating and
 * initializing StandardComponent instances when in design mode. A
 * StandardComponent is a type of GridComponent that contains a Swing component.
 * 
 * See: {@link com.jeta.forms.gui.form.StandardComponent }
 * 
 * @author Jeff Tassin
 */
abstract public class StandardComponentFactory extends AbstractComponentFactory {
	/**
	 * Creates a <code>StandardComponentFactory</code> instance.
	 */
	public StandardComponentFactory() {
	}

	/**
	 * Creates a <code>StandardComponentFactory</code> instance with the
	 * specified component source.
	 * 
	 * @param compsrc
	 *            the component source associated with this factory.
	 */
	public StandardComponentFactory(ComponentSource compsrc) {
		super(compsrc);
	}

	/**
	 * Helper method that creates and initializes a StandardComponent.
	 * 
	 * @param compsrc
	 *            the component source.
	 * @param comp
	 *            the Swing component that will be contained by the newly
	 *            created GridComponent
	 * @param view
	 *            the view that will contain the newly created GridComponent.
	 * @return the grid component.
	 */
	public GridComponent create(ComponentSource compsrc, Component comp, GridView view) throws FormException {
		FormUtils.safeAssert(compsrc != null);

		if (comp == null) {
			StandardComponent gc = new StandardComponent(null, view);
			installHandlers(gc);
			return gc;
		}
		else {
			StandardComponent gc = new StandardComponent(new JETABean(comp), view);
			installHandlers(gc);
			return gc;
		}
	}

	/**
	 * ComponentFactory implementation Creates a GridComponent instances with
	 * the given name and the given parent view. However, the GridComponent is
	 * not added to the view in this call.
	 * 
	 * @param compName
	 *            the name of the component.
	 * @param parentView
	 *            the parent view for the new grid component. This value is
	 *            needed mainly for installing event listeners to the
	 *            GridComponent in design mode.
	 */
	public GridComponent createComponent(Component comp, GridView view) throws FormException {
		return create(getComponentSource(), comp, view);
	}

}
