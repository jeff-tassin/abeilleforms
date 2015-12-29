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

package com.jeta.forms.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import com.jeta.forms.gui.common.FormException;

/**
 * This class is a helper class for logging messages in the application. This
 * class also initializes the logging API for the application
 * 
 * @author Jeff Tassin
 */
public class FormsLogger {
	/**
	 * The name of our logger.
	 */
	public static final String LOGGER_NAME = "forms.logger";

	/**
	 * Converts the stack trace for the given Throwable into a String
	 * 
	 * @param t
	 *            the throwable whose stack trace we wish to retrive.
	 * @return a String representation of the stack trace for the given
	 *         Throwable
	 */
	private static String getStackTrace(Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	/**
	 * Helper method that sends a message to the logger only when debugging
	 * 
	 * @param msg
	 *            the message to send to the logger
	 */
	public static void debug(String msg) {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.finest(msg);
	}

	/**
	 * Helper method that logs an exception.
	 * 
	 * @param t
	 *            the Throwable whose message and stack trace we send to the
	 *            logger.
	 */
	public static void debug(Throwable t) {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		if (t instanceof FormException) {
			Exception e = ((FormException) t).getSourceException();
			if (e != null)
				t = e;
		}

		String msg = getStackTrace(t);
		logger.finest(msg);
	}

	/**
	 * Helper method that sends a message to the logger.
	 * 
	 * @param msg
	 *            the message that is sent to the logger.
	 */
	public static void fine(String msg) {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.fine(msg);
	}

	/**
	 * Forwards a severe log message to the application logger
	 * 
	 * @param msg
	 *            the message to send to the logger.
	 */
	public static void severe(String msg) {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.fine(msg);
	}

	/**
	 * Forwards a severe log message to the application logger.
	 * 
	 * @param t
	 *            the Throwable whose message and stack trace is sent to the
	 *            logger.
	 */
	public static void severe(Throwable t) {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		if (t instanceof FormException) {
			Exception e = ((FormException) t).getSourceException();
			if (e != null)
				t = e;
		}
		String msg = getStackTrace(t);
		logger.severe(msg);
	}
}
