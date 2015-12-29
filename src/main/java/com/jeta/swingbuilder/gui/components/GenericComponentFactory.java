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

import javax.swing.JOptionPane;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.open.i18n.I18N;

/**
 * A factory for creating swing components
 */
public class GenericComponentFactory extends SwingComponentFactory {

	private String m_supplied_class;

	/**
	 * ctor
	 */
	public GenericComponentFactory(ComponentSource compSrc) {
		super(compSrc, null);
	}

	public GridComponent createComponent(String compName, GridView view) throws FormException {
		m_supplied_class = javax.swing.JOptionPane.showInputDialog(view, I18N.getLocalizedMessage("Enter Class Name (e.g. javax.swing.JTable):"));

		if (m_supplied_class != null) {
			try {
				m_supplied_class = com.jeta.swingbuilder.gui.utils.FormDesignerUtils.fastTrim(m_supplied_class);
				Class c = Class.forName(m_supplied_class);
				return super.createComponent(compName, view);
			} catch (Exception e) {
				String msg = I18N.getLocalizedMessage("Unable to instantiate component");
				String title = I18N.getLocalizedMessage("Error");
				JOptionPane.showMessageDialog(view, msg, title, JOptionPane.ERROR_MESSAGE);
				ComponentSource compsrc = getComponentSource();
				compsrc.setSelectionTool();
				return null;
			}
		}
		else {
			ComponentSource compsrc = getComponentSource();
			compsrc.setSelectionTool();
			return null;
		}
	}

	public String getComponentClass() {
		return m_supplied_class;
	}
}
