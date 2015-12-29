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

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.open.registry.JETARegistry;

/**
 * A specialization of ComponentFactory that maintains a reference to a
 * component source. This is a common requirement of most ComponentFactories.
 * 
 * @author Jeff Tassin
 */
abstract public class AbstractComponentFactory implements ComponentFactory {
	/**
	 * The object that is responsible for managing the component factories
	 */
	private ComponentSource m_compsrc;

	/**
	 * Default <code>AbstractComponentFactory</code> constructor.
	 */
	public AbstractComponentFactory() {
	}

	/**
	 * Creates an <code>AbstractComponentFactory</code> instance with the
	 * specified ComponentSource.
	 * 
	 * @param compsrc
	 *            the component source associated with this factory.
	 */
	public AbstractComponentFactory(ComponentSource compsrc) {
		FormUtils.safeAssert(compsrc != null);
		m_compsrc = compsrc;
	}

	/**
	 * Returns the component source associated with this factory.
	 * 
	 * @return the component source.
	 */
	public ComponentSource getComponentSource() {
		return m_compsrc;
	}

	/**
	 * Installs event listeners on the specified GridComponent. This is only
	 * used in design mode.
	 * 
	 * @param comp
	 *            the component to install event listeners on.
	 */
	public void installHandlers(GridComponent comp) {
		FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		if (fmgr != null) {
			fmgr.installHandlers(getComponentSource(), comp);
		}
	}

	/**
	 * Sets the component source associated with this factory.
	 * 
	 * @param compsrc
	 *            the component source to set.
	 */
	public void setComponentSource(ComponentSource compsrc) {
		m_compsrc = compsrc;
	}
}
