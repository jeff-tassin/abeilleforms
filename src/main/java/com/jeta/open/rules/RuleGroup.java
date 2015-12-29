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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class defines a rule for running a composite of rules as one
 * 
 * @author Jeff Tassin
 */
public class RuleGroup extends AbstractRule {

	/** params to pass to each rule */
	private Object[] m_params;

	/** the rules that form the group */
	private LinkedList m_rules;

	/**
	 * ctor
	 */
	public RuleGroup(Object param) {
		m_params = new Object[1];
		m_params[0] = param;
	}

	/**
	 * ctor
	 */
	public RuleGroup(Object[] params) {
		m_params = params;
	}

	public void add(JETARule rule) {
		if (m_rules == null)
			m_rules = new LinkedList();
		m_rules.add(rule);
	}

	/**
	 * JETARule implementation
	 */
	public RuleResult check(Object[] params) {
		if (m_rules == null)
			return RuleResult.SUCCESS;
		else {
			Iterator iter = m_rules.iterator();
			while (iter.hasNext()) {
				JETARule rule = (JETARule) iter.next();
				RuleResult result = rule.check(m_params);
				if (result != RuleResult.SUCCESS)
					return result;
			}
		}

		return RuleResult.SUCCESS;
	}
}
