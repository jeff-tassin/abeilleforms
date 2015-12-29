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

import java.awt.Color;

import com.jeta.forms.logger.FormsLogger;
import com.jeta.open.registry.JETARegistry;

/**
 * Utils for store/retrive user properties
 * 
 * @author Jeff Tassin
 */
public class TSUserPropertiesUtils {

	public static boolean getBoolean(String propName, boolean defValue) {
		boolean result = defValue;
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		String value = userprops.getProperty(propName);
		if (value != null) {
			try {
				result = Boolean.valueOf(value).booleanValue();
			} catch (Exception e) {
				FormsLogger.debug(e);
			}
		}

		return result;
	}

	public static Color getColor(String propName, Color defaultColor) {
		assert (defaultColor != null);
		if (defaultColor != null) {
			int rgb = getInteger(propName, defaultColor.getRGB());
			return new Color(rgb);
		}
		else {
			return Color.black;
		}
	}

	public static int getInteger(String propName, int defValue) {
		int result = defValue;
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		String value = userprops.getProperty(propName);
		if (value != null) {
			try {
				result = Integer.parseInt(value);
			} catch (Exception e) {
				FormsLogger.debug(e);
			}
		}
		return result;
	}

	public static String getString(String propName, String defValue) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		return userprops.getProperty(propName, defValue);
	}

	public static void setBoolean(String propName, boolean value) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		userprops.setProperty(propName, String.valueOf(value));
	}

	public static void setColor(String propName, Color color) {
		assert (color != null);
		if (color != null) {
			setInteger(propName, color.getRGB());
		}
	}

	public static void setInteger(String propName, int value) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		userprops.setProperty(propName, String.valueOf(value));
	}

	public static void setString(String propName, String value) {
		TSUserProperties userprops = (TSUserProperties) JETARegistry.lookup(TSUserProperties.COMPONENT_ID);
		userprops.setProperty(propName, value);
	}

}
