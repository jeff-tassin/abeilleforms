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

package com.jeta.forms.store.memento;

/**
 * This class is used to pass information to a GridComponent when it is storing
 * its state. We use this class to provide a level of control over how the state
 * is stored. For example, we can do a shallow or deep copy of the state. A
 * shallow copy happens when only the link references to forms are stored. A
 * deep copy occurs with the full form information is stored for a linked form.
 * 
 * @author Jeff Tassin
 */
public class StateRequest {
	public static final StateRequest DEEP_COPY = new StateRequest(true);
	public static final StateRequest SHALLOW_COPY = new StateRequest(false);

	public static final String COMPONENT_ID = "state.request";

	/**
	 * Flag that indicates if we should perform a deep copy on linked forms.
	 */
	private boolean m_deep_copy = false;

	/**
	 * Creates a <code>StateRequest</code> instance with the shallow copy flag
	 */
	public StateRequest() {

	}

	/**
	 * Creates a <code>StateRequest</code> instance with the specified copy
	 * flag
	 * 
	 * @param deep_copy
	 *            set to true if the request is for a deep copy of the form
	 *            state.
	 */
	public StateRequest(boolean deep_copy) {
		m_deep_copy = deep_copy;
	}

	/**
	 * Returns the flag that indicates if we should perform a deep copy on
	 * linked forms.
	 * 
	 * @return the flag that indicates if we should perform a deep copy on
	 *         linked forms.
	 */
	public boolean isDeepCopy() {
		return m_deep_copy;
	}

	/**
	 * Returns the flag that indicates if we should perform a shallow copy on
	 * linked forms.
	 * 
	 * @return the flag that indicates if we should perform a shallow copy on
	 *         linked forms.
	 */
	public boolean isShallowCopy() {
		return !isDeepCopy();
	}
}
