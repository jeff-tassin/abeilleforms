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

package com.jeta.swingbuilder.gui.properties.editors;

import com.jeta.forms.store.properties.ButtonGroupProperty;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

public class ButtonGroupEditor extends StringEditor {

	public ButtonGroupEditor() {

	}

	public void setValue(Object value) {
		if (value instanceof ButtonGroupProperty) {
			ButtonGroupProperty p = (ButtonGroupProperty) value;
			super.setValue(p.getGroupName());
		}
		else if (value instanceof String) {
			super.setValue(value);
		}
		else if (value == null) {
			super.setValue(null);
		}
		else {
			assert (false);
		}
	}

	public Object getValue() {
		return new ButtonGroupProperty(FormDesignerUtils.fastTrim((String) super.getValue()));
	}

}
