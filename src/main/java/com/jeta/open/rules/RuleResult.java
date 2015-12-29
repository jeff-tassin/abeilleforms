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

package com.jeta.open.rules;

/**
 * Defines a result from a JETARule
 * 
 * @author Jeff Tassin
 */
public class RuleResult {
	/**
	 * the result code
	 */
	private int m_resultcode;

	/**
	 * The error message if this result is a FAIL_MESSAGE
	 */
	private String m_message;

	/** rule result codes */
	public final static int SUCCESS_ID = 0;
	public final static int FAIL_MESSAGE_ID = -1;
	/**
	 * fail code to indicate the the validator will handle showing the error
	 * message
	 */
	public final static int FAIL_NO_MESSAGE_ID = -2;

	/** pre-defined rule result object */
	public static final RuleResult SUCCESS = new RuleResult(SUCCESS_ID);
	public static final RuleResult FAIL = new RuleResult(FAIL_NO_MESSAGE_ID);

	/**
	 * ctor
	 */
	public RuleResult(int code) {
		m_resultcode = code;
		assert ((code == SUCCESS_ID) || (code == FAIL_MESSAGE_ID) || (code == FAIL_NO_MESSAGE_ID));
	}

	/**
	 * ctor Construct a ResultResult with a FAIL_MESSAGE code
	 */
	public RuleResult(String msg) {
		if (msg == null)
			m_resultcode = SUCCESS_ID;
		else
			m_resultcode = FAIL_MESSAGE_ID;

		m_message = msg;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof RuleResult) {
			RuleResult rr = (RuleResult) obj;
			return (m_resultcode == rr.m_resultcode);
		}
		else {
			return false;
		}
	}

	/**
	 * @return the result code
	 */
	public int getCode() {
		return m_resultcode;
	}

	/**
	 * If this result is a FAIL_MESSAGE, then get the message
	 */
	public String getMessage() {
		return m_message;
	}
}
