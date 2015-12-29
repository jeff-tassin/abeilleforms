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

package com.jeta.swingbuilder.gui.beanmgr;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.URLClassLoaderHelper;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * Responsible for instantiating imported beans.
 * 
 * @author Jeff Tassin
 */
public class BeanLoader extends URLClassLoaderHelper {

	/**
	 * ctor
	 */
	public BeanLoader() {

	}

	/**
	 * ctor
	 */
	public BeanLoader(Collection urls) {
		super(urls);
	}

	public Component createBean(String beanName) throws FormException {
		try {
			return (Component) createObject(beanName);
		} catch (Exception e) {
			if (e instanceof FormException)
				throw (FormException) e;
			else
				throw new FormException(e);
		} catch (Error err) {
			if (TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_SHOW_ERROR_STACK, false)) {
				throw new FormException(new Exception(err));
			} else
				throw new FormException(err.getMessage(), null);
		}
	}
}
