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

package com.jeta.swingbuilder.gui.editor;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.DefaultFormComponentFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.FormComponentFactory;

public class DesignFormComponentFactory implements FormComponentFactory {
	private DefaultFormComponentFactory m_default_factory = new DefaultFormComponentFactory();

	public FormComponent createFormComponent() {
		if (FormUtils.isDesignMode())
			return new DesignFormComponent();
		else
			return m_default_factory.createFormComponent();
	}
}
