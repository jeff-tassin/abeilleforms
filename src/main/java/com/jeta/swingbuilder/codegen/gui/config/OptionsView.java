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

package com.jeta.swingbuilder.codegen.gui.config;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.store.CodeModel;

/**
 * View for entering options for code generation
 * 
 * @author Jeff Tassin
 */
public class OptionsView extends FormPanel {
	private CodeModel m_model;

	public OptionsView(CodeModel om) {
		super("com/jeta/swingbuilder/store/codeOptions.jfrm");
		m_model = om;
		loadModel(om);
	}

	public CodeModel getModel() {
		return m_model;
	}

	public void loadModel(CodeModel om) {
		setText(OptionsNames.ID_PACKAGE, om.getPackage());
		setText(OptionsNames.ID_CLASS_NAME, om.getClassName());
		setText(OptionsNames.ID_MEMBER_PREFIX, om.getMemberPrefix());
		setSelected(OptionsNames.ID_INCLUDE_MAIN, om.isIncludeMain());
		setSelected(OptionsNames.ID_INCLUDE_NONSTANDARD, om.isIncludeNonStandard());
	}

	public void saveToModel() {
		m_model.setPackage(FormDesignerUtils.fastTrim(getText(OptionsNames.ID_PACKAGE)));
		m_model.setClassName(FormDesignerUtils.fastTrim(getText(OptionsNames.ID_CLASS_NAME)));
		m_model.setMemberPrefix(FormDesignerUtils.fastTrim(getText(OptionsNames.ID_MEMBER_PREFIX)));
		m_model.setIncludeMain(isSelected(OptionsNames.ID_INCLUDE_MAIN));
		m_model.setIncludeNonStandard(isSelected(OptionsNames.ID_INCLUDE_NONSTANDARD));
	}
}
