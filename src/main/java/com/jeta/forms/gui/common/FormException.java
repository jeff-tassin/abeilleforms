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

package com.jeta.forms.gui.common;

/**
 * This is a catch-all exception for parts of the application. Because java
 * beans are used extensively, a lot of reflection and introspection exceptions
 * are thrown. Instead of catching all of these, we will just wrap them and
 * rethrow as a FormException.
 * 
 * @author Jeff Tassin
 */
public class FormException extends Exception {
	private Exception m_error;

	/**
	 * Creates a <code>FormException</code> with the specified exception
	 * information.
	 * 
	 * @param e
	 *            the original cause of the exception
	 */
	public FormException(Exception e) {
		super(createMessage(null, e));
		m_error = e;
	}

	/**
	 * Creates a <code>FormException</code> with the specified message and
	 * exception information.
	 * 
	 * @param msg
	 *            a message to add to the exception message
	 * @param e
	 *            the original cause of the xception
	 */
	public FormException(String msg, Exception e) {
		super(createMessage(msg, e));
		m_error = e;
	}

	/**
	 * Returns the source exception. This can be null.
	 */
	public Exception getSourceException() {
		return m_error;
	}

	/**
	 * Creates an exception message using the specified message and source
	 * exception.
	 */
	private static String createMessage(String msg, Exception e) {
		StringBuffer sbuff = new StringBuffer();
		if (msg != null) {
			sbuff.append(msg);
			sbuff.append("\n");
		}
		String emsg = e.getMessage();
		if (emsg != null) {
			sbuff.append(e.getMessage());
		}
		return sbuff.toString();
	}
}
