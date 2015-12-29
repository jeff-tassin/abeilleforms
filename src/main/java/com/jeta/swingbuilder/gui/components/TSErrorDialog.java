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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;

/**
 * This is a utility dialog that can be used to display an Exception.
 * 
 * @author Jeff Tassin
 */
public class TSErrorDialog extends JETADialog {
	/** message to be prepended to error */
	private String m_msg;

	/**
	 * ctor
	 */
	public TSErrorDialog(Dialog owner, boolean bmodal) {
		super(owner, bmodal);
	}

	/**
	 * ctor
	 */
	public TSErrorDialog(Frame owner, boolean bmodal) {
		super(owner, bmodal);
	}

	public static TSErrorDialog createDialog(Component owner, String caption, String errormsg, Throwable e) {
		TSErrorDialog dlg = (TSErrorDialog) JETAToolbox.createDialog(TSErrorDialog.class, owner, true);
		dlg.setTitle(I18N.getLocalizedMessage("Error"));
		dlg.initialize(errormsg, e);
		if (caption != null)
			dlg.showErrorIcon(caption);

		dlg.setSize(dlg.getPreferredSize());
		return dlg;
	}

	/**
	 * Initializes the dialog with the exception
	 */
	public void initialize(String msg, Throwable e) {
		setTitle(I18N.getLocalizedMessage("Error"));
		TSErrorPanel panel = new TSErrorPanel();
		panel.initialize(msg, e);
		setPrimaryPanel(panel);
		showErrorIcon(I18N.getLocalizedMessage("Error"));
	}

	/**
	 * Shows an error message 'title' with an error icon at top of panel
	 */
	public void showErrorIcon(String msg) {
		TSErrorPanel panel = (TSErrorPanel) getPrimaryPanel();
		if (panel != null) {
			panel.showErrorIcon(msg);
		}
	}
}
