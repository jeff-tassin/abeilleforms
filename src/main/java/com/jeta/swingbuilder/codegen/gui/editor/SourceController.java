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

package com.jeta.swingbuilder.codegen.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.store.memento.FormMemento;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.codegen.builder.DefaultSourceBuilder;
import com.jeta.swingbuilder.codegen.gui.config.OptionsView;
import com.jeta.swingbuilder.store.CodeModel;

public class SourceController extends JETAController {
	private SourceEditor m_view;
	private FormMemento m_form_state;

	public SourceController(SourceEditor view) {
		super(view);
		m_view = view;
		assignAction(SourceNames.ID_OPTIONS_BTN, new OptionsAction());
	}

	public class OptionsAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			OptionsView view = new OptionsView(CodeModel.createInstance(m_view.getFormMemento()));
			JETADialog dlg = JETAToolbox.invokeDialog(view, m_view, I18N.getLocalizedMessage("Code Generation Options"));
			if (dlg.isOk()) {
				view.saveToModel();
				CodeModel model = view.getModel();
				CodeModel.save(model);

				String txt = DefaultSourceBuilder.buildSource(model, m_view.getFormMemento());
				m_view.setText(txt);
			}
		}
	}
}
