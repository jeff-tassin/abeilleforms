/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.interfaces.userprops;

/**
 * This interface describes a service used to store/retrive user properties
 * 
 * @author Jeff Tassin
 */
public interface TSUserProperties {
	public static final String COMPONENT_ID = "jeta.TSUserProperties";

	/**
	 * Reads the named property for the given component from the user properties
	 * store. This method does not throw any exceptions, it is assumed the
	 * implementor will log the exception somewhere
	 * 
	 * @param keyName
	 *            the unique key name to get the property for
	 * @return the property string. Null is returned if the string is not found
	 */
	public String getProperty(String keyName);

	/**
	 * Reads the named property for the given key name from the user properties
	 * store. This method does not throw any exceptions, it is assumed the
	 * implementor will log the exception somewhere
	 * 
	 * @param keyName
	 *            the unique key name within that component to get the property
	 *            for
	 * @param defaultValue
	 *            if the key is not found, return this value
	 * @return the property string. Null is returned if the string is not found
	 */
	public String getProperty(String keyName, String defaultValue);

	/**
	 * Stores the named property for the given key. This method does not throw
	 * any exceptions, it is assumed the implementor will log the exception
	 * somewhere
	 */
	public void setProperty(String keyName, String value);

}
