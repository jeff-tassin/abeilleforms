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

package com.jeta.swingbuilder.codegen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.framework.JETADialogListener;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.codegen.builder.DefaultSourceBuilder;
import com.jeta.swingbuilder.codegen.gui.editor.SourceEditor;
import com.jeta.swingbuilder.gui.components.TSErrorDialog;
import com.jeta.swingbuilder.gui.filechooser.FileChooserConfig;
import com.jeta.swingbuilder.gui.filechooser.TSFileChooserFactory;
import com.jeta.swingbuilder.gui.filechooser.TSFileFilter;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.store.CodeModel;

/**
 * Front end class for invoking code generation.
 * 
 * @author Jeff Tassin
 */
public class ForwardEngineer {
	/**
	 * This is a map of form ids to the last filename used to store generated
	 * code for that form. It is used to popuplate the file save dialog so the
	 * user does not have to re-type the file name every time.
	 */
	private static HashMap m_file_names = new HashMap();

	public ForwardEngineer() {

	}

	public void generate(Component invoker, FormMemento fm) {
		try {
			FormUtils.setDesignMode(false);

			CodeModel cgenmodel = CodeModel.createInstance(fm);

			String txt = DefaultSourceBuilder.buildSource(cgenmodel, fm);
			final SourceEditor editor = new SourceEditor(txt, fm);
			JETAPanel panel = new JETAPanel(new BorderLayout());
			panel.add(editor, BorderLayout.CENTER);
			panel.setPreferredSize(FormDesignerUtils.getWindowDimension(panel, 420, 240));

			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, invoker, true);
			dlg.setTitle(I18N.getLocalizedMessage("Code Generation"));
			dlg.setPrimaryPanel(panel);
			dlg.setSize(dlg.getPreferredSize());
			dlg.setOkText(I18N.getLocalizedMessage("Save"));

			final Component parent = invoker;
			final String formid = fm.getId();
			dlg.addDialogListener(new JETADialogListener() {
				public boolean cmdOk() {
					FileChooserConfig fcc = new FileChooserConfig(".java", new TSFileFilter("java", "Java Files(*.java)"));
					fcc.setParentComponent(parent);
					fcc.setInitialDirectory((String) m_file_names.get(formid));
					File file = TSFileChooserFactory.showSaveDialog(fcc);
					if (file == null)
						return false;
					else {
						try {

							String path = file.getPath();
							int pos = path.lastIndexOf(".java");
							if (pos != path.length() - 5) {
								path = path + ".java";
								file = new File(path);
							}

							FileOutputStream fos = new FileOutputStream(file);
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
							bw.write(editor.getText());
							bw.close();
							m_file_names.put(formid, file.getCanonicalPath());
							return true;
						} catch (Exception e) {
							TSErrorDialog dlg = TSErrorDialog.createDialog(parent, "Error", "Unable to save file", e);
							dlg.showCenter();
							return false;
						}
					}
				}
			});
			dlg.showCenter();
		} finally {
			FormUtils.setDesignMode(true);
		}

	}
}
