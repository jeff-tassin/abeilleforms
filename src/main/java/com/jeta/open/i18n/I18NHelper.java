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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.jeta.open.registry.JETARegistry;
import com.jeta.open.resources.ResourceLoader;

/**
 * This is a utility class for dealing with internationalization issues
 * 
 * @author Jeff Tassin
 */
public class I18NHelper {
	private Locale m_locale;
	private static I18NHelper m_singleton;
	private LinkedList m_bundles = new LinkedList();

	private I18NHelper() {

	}

	public boolean equals(String arg1, String arg2) {
		if (arg1 == null || arg2 == null)
			return false;

		return arg1.equals(arg2);
	}

	public static I18NHelper getInstance() {
		if (m_singleton == null)
			m_singleton = new I18NHelper();

		return m_singleton;
	}

	public void loadBundle(String bundleName) {
		ResourceBundle currentbundle = null;
		ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
		currentbundle = ResourceBundle.getBundle(bundleName, m_locale, loader.getClassLoader());
		m_bundles.add(currentbundle);
	}

	public void setLocale(Locale locale) {
		Locale.setDefault(locale);
		m_locale = locale;
	}

	/**
	 * @return a locale specific message string for a message with a variable
	 *         number of arguments. Allows string messages to be composed
	 *         dynamically.
	 */
	public String format(String template, Object[] arguments) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(m_locale);
		formatter.applyPattern(getLocalizedMessage(template));
		return formatter.format(arguments);
	}

	public String _getLocalizedMessage(ResourceBundle bundle, String messageId) {
		String result = null;
		try {
			result = bundle.getString(messageId);
		} catch (MissingResourceException mre) {
		}
		return result;
	}

	public String getLocalizedMessage(String messageId) {
		if (messageId == null)
			return null;

		String result = null;
		Iterator iter = m_bundles.iterator();
		while (iter.hasNext()) {
			ResourceBundle bundle = (ResourceBundle) iter.next();
			result = _getLocalizedMessage(bundle, messageId);
			if (result != null)
				break;

		}
		if (result == null) {
			result = messageId;
		}
		return result;
	}

	public boolean toBoolean(String strVal) {
		if (strVal == null)
			return false;
		return strVal.compareToIgnoreCase(getLocalizedMessage("true")) == 0;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// Helpers

	public char closeParenthesis() {
		return ')';
	}

	public char comma() {
		return ',';
	}

	public char newline() {
		return '\n';
	}

	public char openParenthesis() {
		return '(';
	}

	public char semicolon() {
		return ';';
	}

	public char space() {
		return ' ';
	}
}
