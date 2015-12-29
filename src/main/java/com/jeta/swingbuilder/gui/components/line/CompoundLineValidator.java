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

package com.jeta.swingbuilder.gui.components.line;

import com.jeta.open.i18n.I18N;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;

/**
 * Validator for the CompoundLineView
 * 
 * @author Jeff Tassin
 */
public class CompoundLineValidator implements JETARule {
	/**
	 * Validates the CompoundLineValidator.
	 * 
	 * @param params
	 *            a 1 element array that contains an CompoundLineView object.
	 */
	public RuleResult check(Object[] params) {
		CompoundLineView view = (CompoundLineView) params[0];
		if (view.getLineCount() > 0) {
			return RuleResult.SUCCESS;
		}
		else {
			return new RuleResult(I18N.getLocalizedMessage("At least one line definition is required"));
		}
	}
}
