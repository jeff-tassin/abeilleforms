/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.editor.ext.html;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.netbeans.editor.ext.Completion;
import org.netbeans.editor.ext.CompletionQuery;
import org.netbeans.editor.ext.CompletionView;
import org.netbeans.editor.ext.ExtEditorUI;
import org.netbeans.editor.ext.ListCompletionView;

/**
 * HTML Completion query specifications
 * 
 * @author Petr Nejedly
 * @version 1.0
 */

public class HTMLCompletion extends Completion {

	public HTMLCompletion(ExtEditorUI extEditorUI) {
		super(extEditorUI);
	}

	protected CompletionView createView() {
		return new ListCompletionView(new DelegatingCellRenderer());
	}

	protected CompletionQuery createQuery() {
		return new HTMLCompletionQuery();
	}

	/**
	 * Substitute the document's text with the text that is appopriate for the
	 * selection in the view. This function is usually triggered upon pressing
	 * the Enter key.
	 * 
	 * @return true if the substitution was performed false if not.
	 */
	public synchronized boolean substituteText(boolean flag) {
		if (getLastResult() != null) {
			int index = getView().getSelectedIndex();
			if (index >= 0) {
				getLastResult().substituteText(index, flag);
			}
			return true;
		}
		else {
			return false;
		}
	}

	/* -------------------------------------------------------------------------- */
	// This would go out as the interfaces of all completions will meet
	public class DelegatingCellRenderer implements ListCellRenderer {
		ListCellRenderer defaultRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof CompletionQuery.ResultItem) {
				return ((CompletionQuery.ResultItem) value).getPaintComponent(list, isSelected, cellHasFocus);
			}
			else {
				return defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		}
	}

}
