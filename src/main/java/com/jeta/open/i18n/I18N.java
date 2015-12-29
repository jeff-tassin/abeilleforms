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

package com.jeta.open.i18n;

import java.util.Collection;
import java.util.Iterator;

/**
 * This is a wrapper class around I18NHelper
 * 
 * @author Jeff Tassin
 */
public class I18N {
	public static int compareToIgnoreCase(String str1, String str2) {
		if (str1 != null)
			return str1.compareToIgnoreCase(str2);
		else
			return -1;
	}

	/**
	 * Compares two string arrays in a Locale independent way.
	 * 
	 * @param str1
	 *            the first string to compare
	 * @param str2
	 *            the second string to compare
	 * @return true if the two strings are equals
	 */
	public static boolean equals(char[] str1, char[] str2) {
		if (str1 == null)
			return false;

		if (str2 == null)
			return false;

		if (str1.length == 0)
			return false;

		if (str1.length != str2.length)
			return false;

		for (int index = 0; index < str1.length; index++) {
			if (str1[index] != str2[index])
				return false;
		}
		return true;
	}

	/**
	 * Compares two strings in a Locale independent way.
	 * 
	 * @param str1
	 *            the first string to compare
	 * @param str2
	 *            the second string to compare
	 * @return true if the two strings are equals
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null)
			return false;
		// @todo this currently does not work correctly for I18N
		return str1.equals(str2);
	}

	/**
	 * Compares two strings in a Locale independent way.
	 * 
	 * @param str1
	 *            the first string to compare
	 * @param str2
	 *            the second string to compare
	 * @return true if the two strings are equals
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		if (str1 == null)
			return false;
		// @todo this currently does not work correctly for I18N
		return str1.equalsIgnoreCase(str2);
	}

	/**
	 * A wrapper routine around I18NHelper for formatting locale specific
	 * messages.
	 * 
	 * @param template
	 *            the template name of the message
	 * @param arguments
	 *            an array or arguments required for the message
	 * @return the formatted message.
	 */
	public static String format(String template, Object[] arguments) {
		return I18NHelper.getInstance().format(template, arguments);
	}

	/**
	 * This is a helper routine that formats a message string that takes 1
	 * argument in the current locale
	 * 
	 * @param template
	 *            the name of the message template to use
	 * @param arg1
	 *            the argument value for the message tempalte
	 * @return the formatted message
	 */
	public static String format(String template, Object arg1) {
		Object[] args = new Object[1];
		if (arg1 == null)
			args[0] = "";
		else
			args[0] = arg1;

		return I18NHelper.getInstance().format(template, args);
	}

	/**
	 * This is a helper routine that formats a message string that takes 2
	 * arguments in the current locale
	 * 
	 * @param template
	 *            the name of the message template to use
	 * @param arg1
	 *            the first argument value for the message tempalte
	 * @param arg2
	 *            the second argument value for the message tempalte
	 * @return the formatted message
	 */
	public static String format(String template, Object arg1, Object arg2) {
		Object[] args = new Object[2];
		args[0] = arg1;
		args[1] = arg2;
		return I18NHelper.getInstance().format(template, args);
	}

	/**
	 * This is a helper routine that formats a message string that takes 3
	 * arguments in the current locale
	 * 
	 * @param template
	 *            the name of the message template to use
	 * @param arg1
	 *            the first argument value for the message tempalte
	 * @param arg2
	 *            the second argument value for the message tempalte
	 * @param arg3
	 *            the third argument value for the message tempalte
	 * @return the formatted message
	 */
	public static String format(String template, Object arg1, Object arg2, Object arg3) {
		Object[] args = new Object[3];
		args[0] = arg1;
		args[1] = arg2;
		args[2] = arg3;
		return I18NHelper.getInstance().format(template, args);
	}

	/**
	 * iterates over the collection of objects and creates a string that
	 * contains the objects separated by a comma (or the locale version of a CSV
	 * list). If an item in the array is null it is skipped.
	 * 
	 * @return the generated string
	 */
	public static String generateCSVList(String[] strs) {
		if (strs == null)
			return null;

		StringBuffer buffer = new StringBuffer();
		boolean bfirst = true;
		for (int index = 0; index < strs.length; index++) {
			String str = strs[index];
			if (str != null) {

				if (bfirst)
					bfirst = false;
				else
					buffer.append(", ");

				buffer.append(str);
			}
		}
		return buffer.toString();
	}

	/**
	 * iterates over the collection of objects and creates a string that
	 * contains the objects separated by a comma (or the locale version of a CSV
	 * list)
	 * 
	 * @return the generated string
	 */
	public static String generateCSVList(Collection objs) {
		if (objs == null)
			return null;

		StringBuffer result = new StringBuffer();
		Iterator iter = objs.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj != null)
				result.append(obj.toString());

			if (iter.hasNext())
				result.append(",");
		}
		return result.toString();
	}

	/**
	 * Returns the locale string for the given resource name with the addition
	 * of a colon on the end (for western languages). Dialog labels generally
	 * have a colon at the end of the label.
	 * 
	 * @param resourceName
	 *            the name of the resource to lookup in the properties files.
	 * @return the localized string. If the resourceName is not found, it is
	 *         returned.
	 */
	public static String getLocalizedDialogLabel(String resourceName) {
		return getLocalizedMessage(resourceName) + ":";
	}

	/**
	 * Gets the locale string for the given resource name.
	 * 
	 * @param resourceName
	 *            the name of the resource to lookup in the properties files.
	 * @return the localized string. If the resourceName is not found, it is
	 *         returned.
	 */
	public static String getLocalizedMessage(String resourceName) {
		return I18NHelper.getInstance().getLocalizedMessage(resourceName);
	}

	/**
	 * Gets the locale string for the given resource name.
	 * 
	 * @param resourceName
	 *            the name of the resource to lookup in the properties files.
	 * @return the localized string. If the resourceName is not found, it is
	 *         returned.
	 * @deprecated
	 */
	public static String getResource(String resourceName) {
		return I18NHelper.getInstance().getLocalizedMessage(resourceName);
	}

}
