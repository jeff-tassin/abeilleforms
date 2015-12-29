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

package com.jeta.swingbuilder.defaults;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.gui.components.ContainedFormFactory;
import com.jeta.forms.gui.form.FormComponentFactory;
import com.jeta.forms.gui.form.GridOverlayFactory;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.support.UserProperties;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.ComponentFinderFactory;
import com.jeta.swingbuilder.codegen.builder.PropertyWriterFactory;
import com.jeta.swingbuilder.gui.beanmgr.DefaultBeanManager;
import com.jeta.swingbuilder.gui.colorchooser.DefaultColorChooserFactory;
import com.jeta.swingbuilder.gui.components.DefaultContainedFormFactory;
import com.jeta.swingbuilder.gui.editor.DesignFormComponentFactory;
import com.jeta.swingbuilder.gui.editor.DesignGridOverlayFactory;
import com.jeta.swingbuilder.gui.lookandfeel.DefaultLookAndFeelManager;
import com.jeta.swingbuilder.project.DefaultProjectManager;
import com.jeta.swingbuilder.support.DesignTimeComponentFinderFactory;
import com.jeta.swingbuilder.support.DesignerUserProperties;

/**
 * This class loads the JETARegistry with default objects required by the
 * SwingBuilder system.
 * 
 * @author Jeff Tassin
 */
public class DefaultInitializer {
	/**
	 * Initializes the components needed by the Forms system.
	 */
	public static void initialize() {
		com.jeta.forms.logger.FormsLogger.debug("SwingBuilder. default initializer");
		JETARegistry.rebind(ProjectManager.COMPONENT_ID, new DefaultProjectManager());
		JETARegistry.rebind(BeanManager.COMPONENT_ID, new DefaultBeanManager());
		JETARegistry.rebind(DefaultLookAndFeelManager.COMPONENT_ID, new DefaultLookAndFeelManager());
		JETARegistry.rebind(GridOverlayFactory.COMPONENT_ID, new DesignGridOverlayFactory());
		JETARegistry.rebind(FormComponentFactory.COMPONENT_ID, new DesignFormComponentFactory());
		JETARegistry.rebind(ComponentFinderFactory.COMPONENT_ID, new DesignTimeComponentFinderFactory());
		JETARegistry.rebind(UserProperties.COMPONENT_ID, new DesignerUserProperties());
		JETARegistry.rebind(ContainedFormFactory.COMPONENT_ID, new DefaultContainedFormFactory());
		JETARegistry.rebind(PropertyWriterFactory.COMPONENT_ID, new PropertyWriterFactory());
		JETARegistry.rebind(DefaultColorChooserFactory.COMPONENT_ID, new DefaultColorChooserFactory());

		JETARegistry.rebind("AbeilleForms.runTime", Boolean.valueOf(false));
	}
}
