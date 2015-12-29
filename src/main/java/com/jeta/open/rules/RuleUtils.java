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

import javax.swing.JOptionPane;

import com.jeta.open.i18n.I18N;

/**
 * Utils class for rules handling
 * 
 * @author Jeff Tassin
 */
public class RuleUtils {
	/**
	 * Helper method that displays a dialog if the check fails
	 */
	public static RuleResult checkNotify(JETARule rule, Object param1) {
		Object[] params = new Object[1];
		params[0] = param1;
		return checkNotify(rule, params);
	}

	/**
	 * Helper method that displays a dialog if the check fails
	 */
	public static RuleResult checkNotify(JETARule rule, Object param1, Object param2) {
		Object[] params = new Object[2];
		params[0] = param1;
		params[1] = param2;
		return checkNotify(rule, params);
	}

	/**
	 * Helper method that displays a dialog if the check fails
	 */
	public static RuleResult checkNotify(JETARule rule, Object param1, Object param2, Object param3) {
		Object[] params = new Object[3];
		params[0] = param1;
		params[1] = param2;
		params[2] = param3;
		return checkNotify(rule, params);
	}

	/**
	 * Helper method that displays a dialog if the check fails
	 */
	public static RuleResult checkNotify(JETARule rule, Object[] params) {
		RuleResult result = rule.check(params);
		if (result != null && (result.getCode() == RuleResult.FAIL_MESSAGE_ID)) {
			JOptionPane.showMessageDialog(null, result.getMessage(), I18N.getLocalizedMessage("Error"), JOptionPane.ERROR_MESSAGE);

			return RuleResult.FAIL;
		}
		else {
			return result;
		}
	}
}
