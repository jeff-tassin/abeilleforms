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

package com.jeta.swingbuilder.app;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.interfaces.resources.ResourceLoader;
import com.jeta.swingbuilder.interfaces.userprops.TSUserProperties;

/**
 * @author Jeff Tassin
 */
public class UserPropertiesStore implements TSUserProperties {
	private static final String PROPS_RESOURCE_NAME = "userdata.properties"; // the
																				// name
																				// of
																				// the
																				// 'file'
																				// we
																				// store
																				// to
	private Properties m_props = new Properties(); // the set of objects
	/**
	 * Flag that indicates if store should be read only. (Needed for webstart)
	 */
	private boolean m_readonly = false;

	public UserPropertiesStore() {

	}

	/**
	 * Reads the named property for the given keyname from the user properties
	 * store. This method does not throw any exceptions, it is assumed the
	 * implementor will log the exception somewhere
	 * 
	 * @param keyName
	 *            the unique key name to get the property for
	 * @return the property string. Null is returned if the string is not found
	 */
	public String getProperty(String keyName) {
		return m_props.getProperty(keyName);
	}

	/**
	 * Reads the named property for the given key from the user properties
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
	public String getProperty(String keyName, String defaultValue) {
		return m_props.getProperty(keyName, defaultValue);
	}

	/**
	 * TSComponent implementation. Get's called by JETARegistry at startup
	 */
	public void startup() {
		try {
			JETARegistry.rebind(TSUserProperties.COMPONENT_ID, this);

			if (m_readonly) {
				m_props = new Properties();
			}
			else {
				ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
				InputStream reader = loader.getInputStream(PROPS_RESOURCE_NAME);
				m_props.load(reader);
				reader.close();
			}
		} catch (Exception e) {
			m_props = new Properties();
		}
	}

	/**
	 */
	public void shutdown() {
		try {
			if (!m_readonly) {
				ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
				OutputStream writer = loader.getOutputStream(PROPS_RESOURCE_NAME);
				m_props.store(writer, null);
				writer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stores the named property for the given key to the user properties store.
	 * This method does not throw any exceptions, it is assumed the implementor
	 * will log the exception somewhere
	 */
	public void setProperty(String keyName, String value) {
		m_props.setProperty(keyName, value);
	}

	public void setReadOnly(boolean readonly) {
		m_readonly = readonly;
	}
}
