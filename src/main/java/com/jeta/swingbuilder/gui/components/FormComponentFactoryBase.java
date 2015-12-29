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

package com.jeta.swingbuilder.gui.components;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.AbstractComponentFactory;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.editor.DesignFormComponent;

/**
 * ComponentFactory for creating forms.
 * 
 * @author Jeff Tassin
 */
public abstract class FormComponentFactoryBase extends AbstractComponentFactory {

	/**
	 * ctor
	 */
	public FormComponentFactoryBase() {
	}

	/**
	 * ctor
	 */
	public FormComponentFactoryBase(ComponentSource compsrc) {
		super(compsrc);
	}

	/**
	 * Helper method for creating a composite component that has a GridView as a
	 * child.
	 */
	public FormComponent create(ComponentSource compsrc, String compName, GridView parentView, int cols, int rows, boolean embedded) throws FormException {
		JETABean jbean = JETABeanFactory.createBean(GridView.class.getName(), compName, true, true);
		GridView childview = (GridView) jbean.getDelegate();
		childview.initialize(cols, rows);

		String id = FormUtils.createUID();
		if (compName != null && compName.indexOf("top.parent") >= 0) {
			id = "top.parent" + id;
		}
		else if (embedded) {
			id = "embedded." + id;
		}

		FormComponent form = new DesignFormComponent(id, jbean, parentView, embedded);
		installHandlers(form);
		FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		if (fmgr != null) {
			fmgr.registerForm(form);
		}
		return form;
	}

	/**
	 * ComponentFactory implementation
	 */
	public abstract GridComponent createComponent(String compName, GridView parentView) throws FormException;

}
