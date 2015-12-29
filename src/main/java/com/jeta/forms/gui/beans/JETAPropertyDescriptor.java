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

package com.jeta.forms.gui.beans;

import java.lang.reflect.Method;

import com.jeta.forms.gui.common.FormException;

/**
 * An enhanced PropertyDescriptor to support dynamically adding and removing
 * properties to a Java Bean. This type of descriptor also defines how a
 * property value is set and retrieved.
 * 
 * @see java.beans.PropertyDescriptor
 * 
 * @author Jeff Tassin
 */
public interface JETAPropertyDescriptor {
	/**
	 * Return the display name for the property.
	 */
	public String getDisplayName();

	/**
	 * Return the name of the property.
	 */
	public String getName();

	/**
	 * Returns the value of the associated property in the given bean.
	 * 
	 * @return the value for property
	 */
	public Object getPropertyValue(JETABean bean) throws FormException;

	/**
	 * Gets a PropertyEditor class that has been registered for the property.
	 * Null is returned if no property editor class has been registered.
	 */
	public Class getPropertyEditorClass();

	/**
	 * Returns the class object for the property.
	 */
	public Class getPropertyType();

	/**
	 * Gets the short description for the property.
	 */
	public String getShortDescription();

	/**
	 * Returns a method object used to set the property value. Null is returned
	 * if the property is not writable.
	 */
	public Method getWriteMethod();

	/**
	 * The "hidden" flag is used to identify features that are intended only for
	 * tool use, and which should not be exposed to humans.
	 */
	public boolean isHidden();

	/**
	 * The "preferred" flag is used to identify features that are particularly
	 * important for presenting to humans
	 */
	public boolean isPreferred();

	/**
	 * Returns true if the property should not be stored.
	 */
	public boolean isTransient();

	/**
	 * Returns true if the property is writable.
	 */
	public boolean isWritable();

	/**
	 * Sets this property to preferred.
	 */
	public void setPreferred(boolean bpref);

	/**
	 * Sets the value for this property on the given bean.
	 * 
	 * @param bean
	 *            the JETABean that contains the property to set
	 * @param value
	 *            the value of the property
	 */
	public void setPropertyValue(JETABean bean, Object value) throws FormException;

}
