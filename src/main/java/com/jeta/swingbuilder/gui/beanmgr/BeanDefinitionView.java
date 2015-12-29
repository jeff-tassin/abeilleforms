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

import java.awt.BorderLayout;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.open.gui.framework.JETAPanel;

/**
 * View for configuring an imported Java Bean
 * 
 * @author Jeff Tassin
 */
public class BeanDefinitionView extends JETAPanel {
	/**
	 * The view panel
	 */
	private FormPanel m_view;

	/**
	 * ctor
	 */
	public BeanDefinitionView() {
		m_view = new FormPanel("com/jeta/swingbuilder/gui/beanmgr/beanDefinition.jfrm");
		setLayout(new BorderLayout());
		add(m_view, BorderLayout.CENTER);
	}

	public String getBeanName() {
		String bname = m_view.getText(BeanDefinitionNames.ID_BEAN_CLASS);
		bname = bname.replace('/', '.');
		bname = bname.replace('\\', '.');
		return bname;
	}

	public boolean isScrollable() {
		return m_view.isSelected(BeanDefinitionNames.ID_SCROLLABLE);
	}
}
