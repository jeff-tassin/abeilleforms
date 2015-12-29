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

package org.netbeans.editor.ext.java;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ListCompletionView;

/**
 * 
 * @author Miloslav Metelka
 * @version 1.0
 */

public class JavaFastImport implements ActionListener {

	protected JTextComponent target;

	private String exp;

	private JavaFastImportPanel panel;

	private ListCellRenderer cellRenderer;

	private JList resultList;

	private Dialog dialog;

	private JButton[] buttons;

	public JavaFastImport(JTextComponent target) {
		this.target = target;

		exp = Utilities.getSelectionOrIdentifier(target);
	}

	public void setDialogVisible(boolean visible) {
		List result = null;
		if (visible) {
			result = evaluate();
			if (result == null || result.size() == 0) { // no data
				return;
			}
			populate(result);
		}

		if (dialog == null) {
			dialog = createDialog();
		}

		getResultList().requestFocus();
		dialog.setVisible(visible);

		if (visible) {
			getPanel().popupNotify();

		}
		else {
			dialog.dispose();
		}
	}

	protected void updateImport(Object item) {
	}

	protected ListCellRenderer createCellRenderer() {
		JCCellRenderer rr = new JCCellRenderer();
		rr.setClassDisplayFullName(true);
		return rr;
	}

	protected JList createResultList() {
		JList list = new ListCompletionView(getCellRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					actionPerformed(new ActionEvent(getButtons()[0], 0, ""));
				}
			}
		});
		return list;
	}

	private JButton[] getButtons() {
		if (buttons == null) {
			buttons = new JButton[] { new JButton(LocaleSupport.getString("JFI_importButton", "Import Class")), // NOI18N
					new JButton(LocaleSupport.getString("JFI_cancelButton", "Cancel")) // NOI18N
			};
			String mnemonic = LocaleSupport.getString("JFI_importButtonMnemonic", "I"); // NOI18N
			if (mnemonic != null && mnemonic.length() > 0) {
				buttons[0].setMnemonic(mnemonic.charAt(0));
			}

			mnemonic = LocaleSupport.getString("JFI_cancelButtonMnemonic", "C"); // NOI18N
			if (mnemonic != null && mnemonic.length() > 0) {
				buttons[1].setMnemonic(mnemonic.charAt(0));
			}
			buttons[0].getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFI_importButton")); // NOI18N
			buttons[1].getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFI_cancelButton")); // NOI18N
		}

		return buttons;
	}

	private Dialog createDialog() {
		String title = LocaleSupport.getString("JFI_title", "Import Class");

		Dialog dialog = DialogSupport.createDialog(title, getPanel(), true, getButtons(), false, 0, 1, this);

		return dialog;
	}

	private JavaFastImportPanel getPanel() {
		if (panel == null) {
			panel = new JavaFastImportPanel(this);
		}
		return panel;
	}

	ListCellRenderer getCellRenderer() {
		if (cellRenderer == null) {
			cellRenderer = createCellRenderer();
		}
		return cellRenderer;
	}

	JList getResultList() {
		if (resultList == null) {
			resultList = createResultList();
		}
		return resultList;
	}

	List evaluate() {
		List ret = null;

		if (exp != null && exp.length() > 0) {
			JCFinder finder = JavaCompletion.getFinder();
			ret = finder.findClasses(null, exp, true);

			/*
			 * Iterator it = ret.iterator(); while (it.hasNext()) { JCClass cls =
			 * (JCClass)it.next(); if (cls.getName().indexOf('.') >= 0) {
			 * it.remove(); } }
			 */
		}

		return ret;
	}

	void populate(List result) {
		if (result != null) {
			if (getResultList() instanceof ListCompletionView) {
				((ListCompletionView) getResultList()).setResult(result);
			}
		}
	}

	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();

		if (src == buttons[0]) { // Open button
			int selIndex = getResultList().getSelectedIndex();
			if (selIndex >= 0) {
				updateImport(getResultList().getModel().getElementAt(selIndex));
			}
			setDialogVisible(false);

		}
		else if (src == buttons[1]) { // Close button
			setDialogVisible(false);
		}
	}

}
