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

package com.jeta.swingbuilder.gui.commands;

import java.awt.Component;

import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.text.TextPropertyView;
import com.jeta.swingbuilder.gui.editor.FormEditor;

/**
 * Command that edits the text property of a component if that component has a
 * text property.
 * 
 * @author Jeff Tassin
 */
public class EditTextPropertyCommand extends FormUndoableEdit {
	/**
	 * The component we are editing.
	 */
	private GridComponent m_comp;

	/**
	 * The old text value
	 */
	private String m_old_value;

	/**
	 * The new value the user typed.
	 */
	private String m_new_value;

	/**
	 * Adds a new component to a form.
	 */
	public EditTextPropertyCommand(FormComponent fc, GridComponent gc, String newValue, String oldValue) {
		super(fc);
		m_comp = gc;
		m_new_value = newValue;
		m_old_value = oldValue;
	}

	public static void tryEditTextProperty(FormEditor editor, FormComponent fc, GridComponent gc) {
		if (editor != null && fc != null && gc != null) {
			try {
				Component comp = gc.getBeanDelegate();
				if (comp != null) {
					Class c = comp.getClass();
					Class[] setparams = new Class[] { String.class };
					Class[] getparams = new Class[0];
					java.lang.reflect.Method setm = c.getMethod("setText", setparams);
					java.lang.reflect.Method getm = c.getMethod("getText", getparams);

					Object[] getvalues = new Object[0];
					String oldvalue = (String) getm.invoke(comp, getvalues);

					String newvalue = null;
					if (comp instanceof JTextComponent) {
						TextPropertyView view = new TextPropertyView(oldvalue);
						JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, editor, true);
						dlg.setPrimaryPanel(view);
						dlg.setTitle(I18N.getLocalizedMessage("Set Text Property"));
						dlg.setInitialFocusComponent((javax.swing.JComponent) view.getComponentByName(TextPropertyView.ID_TEXT_AREA));
						dlg.setSize(dlg.getPreferredSize());

						/**
						 * override the ENTER key handling for the dialog. We
						 * need to do this because the ENTER key is bound to
						 * setting the Text property. However, this key also
						 * closes a dialog. The dialog can sometimes get the
						 * ENTER event for editing the text and close
						 * prematurely
						 */
						javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0, true);
						dlg.getRootPane().getInputMap().put(ks, "EnterAction");
						dlg.getRootPane().getActionMap().put("EnterAction", new javax.swing.AbstractAction() {
							public void actionPerformed(java.awt.event.ActionEvent ae) {
								// ignore.
							}
						});

						dlg.showCenter();
						if (dlg.isOk())
							newvalue = view.getText();
					}
					else {
						newvalue = javax.swing.JOptionPane.showInputDialog(editor, I18N.getLocalizedMessage("Set Text Property"), oldvalue);
					}

					if (newvalue != null) {
						// if we are here, then the component has both set and
						// get methods, so
						// let's pass a valid command
						EditTextPropertyCommand cmd = new EditTextPropertyCommand(fc, gc, newvalue, oldvalue);
						CommandUtils.invoke(cmd, editor);
					}
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		try {
			if (m_comp instanceof StandardComponent) {
				Component comp = m_comp.getBeanDelegate();
				if (comp != null) {
					Class c = comp.getClass();
					Class[] setparams = new Class[] { String.class };
					java.lang.reflect.Method setm = c.getMethod("setText", setparams);
					Object[] setvalues = new Object[] { m_new_value };
					setm.invoke(comp, setvalues);
					m_comp.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_CHANGED, m_comp));
					if (comp instanceof JTextComponent)
						((JTextComponent) comp).setCaretPosition(0);
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotUndoException {
		super.undo();

		try {
			if (m_comp instanceof StandardComponent) {
				Component comp = m_comp.getBeanDelegate();
				if (comp != null) {
					Class c = comp.getClass();
					Class[] setparams = new Class[] { String.class };
					java.lang.reflect.Method setm = c.getMethod("setText", setparams);
					Object[] setvalues = new Object[] { m_old_value };
					setm.invoke(comp, setvalues);
					m_comp.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_CHANGED, m_comp));
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public String toString() {
		return "EditTextPropertyCommand     ";
	}

}
