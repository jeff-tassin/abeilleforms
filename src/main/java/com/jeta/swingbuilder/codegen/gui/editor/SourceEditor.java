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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import com.jeta.forms.store.memento.FormMemento;
import com.jeta.open.gui.framework.JETAPanel;

public class SourceEditor extends JETAPanel {
	private JTextComponent m_editor;

	private FormMemento m_form_memento;

	/**
	 * Creates a SourceEditor instance with the specified generated source code
	 * for the specified form state.
	 */
	public SourceEditor(String sourceText, FormMemento fm) {
		setLayout(new BorderLayout());
		m_form_memento = fm;

		add(new SourceView(buildView()), BorderLayout.CENTER);

		initialize(sourceText);
		setController(new SourceController(this));
	}

	private Component buildView() {
		JavaKit kit = new JavaKit();
		JEditorPane editor = new JEditorPane();
		editor.setEditorKit(kit);
		// JEditorPane editor = TSEditorUtils.createEditor( kit );
		// JComponent comp = TSEditorUtils.getExtComponent( editor );

		m_editor = editor;
		return new JScrollPane(editor);
	}

	FormMemento getFormMemento() {
		return m_form_memento;
	}

	private void initialize(String txt) {
		m_editor.setText(txt);
	}

	public String getText() {
		return m_editor.getText();
	}

	public void setText(String txt) {
		m_editor.setText(txt);

		try {
			m_editor.setCaretPosition(0);
		} catch (Exception e) {
		}

	}
}
