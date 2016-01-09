/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.open.gui.framework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;

/**
 * This is a base class for all dialogs in the system. It provides a skeleton
 * dialog box with a cancel and ok button. Callers can add their own containers
 * to this dialog's content panel. This class also supports validators for the
 * dialog. This allows the dialog to validate the input and if validation fails,
 * maintain the dialog on the screen. See {@link com.jeta.open.rules.JETARule}.
 * 
 * @author Jeff Tassin
 */
public class JETADialog extends JDialog {
	private JPanel m_contentpane; // this is the container that callers add
									// their controls to

	private CommandListener m_cmdListener = new CommandListener(this);
	private boolean m_bOk = false;

	/**
	 * The panel at the bottom of the dialog that contains the Ok and Cancel
	 * buttons.
	 */
	private JPanel m_btnPanel;
	private JButton m_okbtn;
	private JButton m_closebtn;
	private JButton m_helpbtn;

	private JETAController m_controller;

	/**
	 * a list of JETARules that are responsible for handling validation for the
	 * dialog
	 */
	private LinkedList m_validators;

	/**
	 * The component that has initial focus
	 */
	private JComponent m_initialFocusComponent;

	/**
	 * The main panel for this dialog.
	 */
	private JComponent m_primaryPanel;

	/** this is a list of to JETADialogListener objects */
	private LinkedList m_listeners;

	/** for ui director updates */
	private javax.swing.Timer m_timer;

	/**
	 * The name of the button panel.
	 */
	public static final String ID_BUTTON_PANEL = "button.panel";
	/**
	 * The names of the ok and cancel buttons.
	 */
	public static final String ID_OK = "JETADialog.ok";
	public static final String ID_CANCEL = "JETADialog.cancel";

	/**
	 * ctor
	 */
	public JETADialog(Dialog owner, boolean bModal) {
		super(owner, bModal);
		_initialize();
	}

	/**
	 * ctor
	 */
	public JETADialog(Frame owner, boolean bModal) {
		super(owner, bModal);
		_initialize();
	}

	/**
	 * Forwarded from the view when an action occurs.
	 * 
	 * @param commandId
	 *            the name of the action associated with the event
	 * @param evt
	 *            the action event
	 * @return true if the controller handled the action
	 */
	public boolean actionPerformed(String commandId, ActionEvent evt) {
		boolean bresult = false;
		JETAController controller = getController();
		if (controller != null)
			bresult = controller.actionPerformed(commandId, evt);

		if (!bresult) {
			String actionCommand = evt.getActionCommand();
			if (actionCommand.equals(ID_OK)) {
				cmdOk();
			}
			else if (actionCommand.equals(ID_CANCEL)) {
				cmdCancel();
			}
		}
		return true;
	}

	/**
	 * Adds a controller to the list of controllers that can handle events for
	 * this dialog. When an menu or toolbar event occurs, the event is routed to
	 * each controller added to this frame. Once a controller is found that
	 * handles the event, the event routing is considered complete and no other
	 * controllers are evaluated.
	 * 
	 * @param controller
	 *            the controller to add
	 */
	public void addController(JETAController controller) {
		assert (false);
	}

	/**
	 * Adds a listener to the list of listeners for this dialog
	 */
	public void addDialogListener(JETADialogListener listener) {
		if (m_listeners == null)
			m_listeners = new LinkedList();

		assert (listener != null);
		m_listeners.add(listener);
	}

	/**
	 * This adds a rule that will be used to validate the user input for this
	 * dialog. The validate method is called on each of these rules when the
	 * user hits the ok button. If any rule fails validation, an error message
	 * appears and the dialog will not close.
	 */
	public void addValidator(JETARule validator) {
		addValidator(null, validator);
	}

	/**
	 * This adds a rule that will be used to validate the user input for this
	 * dialog. The validate method is called on each of these validators when
	 * the user hits the ok button. If any rule fails validation, an error
	 * message appears and the dialog will not close.
	 * 
	 * @param parameter
	 *            a parameter to pass to the validator. If this object is an
	 *            Object[] type, then it is passed directly to the validator.
	 * @param validator
	 *            the rule to add to this dialog.
	 */
	public void addValidator(Object parameter, JETARule validator) {
		if (validator == null)
			return;

		if (m_validators == null)
			m_validators = new LinkedList();

		if (parameter == null) {
			m_validators.add(new ValidatorRule(validator, new Object[0]));
		}
		else if (parameter instanceof Object[]) {
			m_validators.add(new ValidatorRule(validator, (Object[]) parameter));
		}
		else {
			Object[] params = new Object[1];
			params[0] = parameter;
			m_validators.add(new ValidatorRule(validator, params));
		}
	}

	/**
	 * Closes the dialog
	 */
	public void cmdCancel() {
		setOk(false);
		dispose();
	}

	/**
	 * Close the dialog and set the ok flag
	 */
	public void cmdOk() {
		if (validateValidators() && validateListeners()) {
			setOk(true);
			dispose();
		}
	}

	/**
	 * Creates the button panel at the bottom of the dialog that contains the
	 * 'ok' and 'close' buttons.
	 * 
	 * @return the panel that contains 'ok' and 'close' btns.
	 */
	private JPanel createButtonPanel() {
		JETAPanel btnpanel = new JETAPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
		m_okbtn = new JButton(I18N.getLocalizedMessage("Ok"));
		m_closebtn = new JButton(I18N.getLocalizedMessage("Cancel"));
		m_helpbtn = new JButton(I18N.getLocalizedMessage("Help"));
		m_helpbtn.setVisible(false);
		btnpanel.add(m_helpbtn);
		btnpanel.add(m_okbtn);
		btnpanel.add(m_closebtn);
		m_btnPanel = btnpanel;
		m_btnPanel.setName(ID_BUTTON_PANEL);

		m_okbtn.setActionCommand(ID_OK);
		m_okbtn.setName(ID_OK);
		m_okbtn.addActionListener(m_cmdListener);
		m_closebtn.setActionCommand(ID_CANCEL);
		m_closebtn.setName(ID_CANCEL);
		m_closebtn.addActionListener(m_cmdListener);
		return m_btnPanel;
	}

	/**
	 * Dispose the dialog
	 */
	public void dispose() {
		super.dispose();
		JETAComponentCleanser cleanser = new JETAComponentCleanser();
		cleanser.cleanse(this);

		m_timer.stop();
		ActionListener[] als = (ActionListener[]) (m_timer.getListeners(ActionListener.class));
		if (als != null) {
			for (int index = 0; index < als.length; index++) {
				m_timer.removeActionListener(als[index]);
			}
		}

		m_cmdListener = null;
		m_controller = null;

		if (m_validators != null)
			m_validators.clear();

		m_initialFocusComponent = null;

		if (m_listeners != null)
			m_listeners.clear();

		if (m_contentpane != null) {
			m_contentpane.removeAll();
			m_contentpane = null;
		}
		m_primaryPanel = null;
	}

	/**
	 * @return the panel at the bottom of this dialog that contains the ok and
	 *         cancel buttons
	 */
	public JPanel getButtonPanel() {
		return m_btnPanel;
	}

	/**
	 * @return the close button for this dialog
	 */
	public JButton getCloseButton() {
		return m_closebtn;
	}

	/**
	 * @return the controller for this dialog
	 */
	public JETAController getController() {
		return m_controller;
	}

	/**
	 * @return the content container for this dialog. Callers add their controls
	 *         to this container.
	 */
	public Container getDialogContentPanel() {
		return m_contentpane;
	}

	/**
	 * @return the Help button for this dialog Normally, this button is not
	 *         visible.
	 */
	public JButton getHelpButton() {
		return m_helpbtn;
	}

	/**
	 * @return the Ok button for this dialog
	 */
	public JButton getOkButton() {
		return m_okbtn;
	}

	/**
	 * Returns the preferred size for this dialog. This includes the title bar
	 * height, border height,width, and ok/cancel button heights. This dialog
	 * also gets the preferred size of the content panel, so you need to
	 * correctly set the preferred size in any content panel you set for this
	 * dialog.
	 * 
	 * @return the preferred size for this dialog so that the caller can size
	 *         the window properly
	 */
	public Dimension getPreferredSize() {
		return getPreferredSize(getDialogContentPanel().getPreferredSize());
	}

	/**
	 * Returns the preferred size for this dialog. The size is calculated
	 * assuming the that dimensions of the main content panel are passed in by
	 * the caller. The content panel dimensions are added to the title bar
	 * height, border height,width, and ok/cancel button heights.
	 * 
	 * @return the preferred size for this dialog so that the caller can size
	 *         the window properly
	 */
	protected Dimension getPreferredSize(Dimension contentDims) {
		Dimension dbtn = m_btnPanel.getPreferredSize(); // add height of
														// ok/cancel button
														// panel
		Dimension d = new Dimension(contentDims);
		d.height += dbtn.height; // add height of ok/cancel button panel
		d.height += JETAToolbox.getTitleBarHeight(); // add height of title
														// bar
		d.height += JETAToolbox.getFrameBorderThickness(); // add height of
															// frame border
		d.width += 2 * JETAToolbox.getFrameBorderThickness(); // add width of
																// border
		/**
		 * @JMT - temporary hack until I figure out how to read the frame border
		 *      thickness
		 */
		if (JETAToolbox.isOSX()) {
			d.width -= 6;
		}
		return d;
	}

	/**
	 * @return the one and only panel for this dialog. The panel must have been
	 *         set previously by calling setPrimaryPanel.
	 */
	public Component getPrimaryPanel() {
		return m_primaryPanel;
	}

	/**
	 * Initializes the components on this dialog
	 */
	protected void _initialize() {
		Container container = getContentPane();
		container.setLayout(new BorderLayout());

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		m_contentpane = new JPanel(new BorderLayout());
		container.add(m_contentpane, BorderLayout.CENTER);
		container.add(createButtonPanel(), BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cmdCancel();
			}

			public void windowOpened(WindowEvent we) {
				if (m_initialFocusComponent != null)
					m_initialFocusComponent.requestFocus();
			}
		});

		/**
		 * Start a timer that updates the controls of the active window every
		 * second
		 */
		ActionListener uiupdater = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateComponents(evt);
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
		getRootPane().getInputMap().put(ks, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				cmdCancel();
			}
		});

		ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true);
		getRootPane().getInputMap().put(ks, "EnterAction");
		getRootPane().getActionMap().put("EnterAction", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				cmdOk();
			}
		});

		int delay = 500; // milliseconds
		m_timer = new javax.swing.Timer(delay, uiupdater);
		m_timer.start();
	}

	/**
	 * @return true if the user clicked the Ok button to close the dialog.
	 */
	public boolean isOk() {
		return m_bOk;
	}

	/**
	 * Removes all registered controllers added to this dialog
	 */
	public void removeAllControllers() {

	}

	/**
	 * Shows/Hides the button panel on this dialog.
	 */
	public void setButtonPanelVisible(boolean bvis) {
		assert (bvis == false);
		Container container = getContentPane();
		container.remove(m_btnPanel);
	}

	/**
	 * Enables/Disables the cancel command
	 */
	public void setCancelEnabled(boolean bCancel) {
		if (!bCancel) {
			m_closebtn.setEnabled(false);
		}
	}

	/**
	 * Sets the text for the close button
	 */
	public void setCloseText(String txt) {
		m_closebtn.setText(txt);
	}

	/**
	 * Sets the font for the ok and close buttons on this dialog
	 * 
	 * @param f
	 *            the new font to set
	 */
	public void setFont(Font f) {
		m_okbtn.setFont(f);
		m_closebtn.setFont(f);
	}

	/**
	 * Sets the one and only panel for this dialog
	 */
	public void setPrimaryPanel(JComponent primaryPanel) {
		if (m_primaryPanel != null) {
			m_contentpane.remove(m_primaryPanel);
		}
		m_primaryPanel = primaryPanel;
		m_contentpane.add(primaryPanel, BorderLayout.CENTER);
		m_contentpane.revalidate();
	}

	/**
	 * Sets the controller that will handle events for this dialog
	 */
	public void setController(JETAController controller) {
		m_controller = controller;
		if (controller instanceof JETARule)
			addValidator((JETARule) controller);
	}

	/**
	 * Sets the component in this frame that will have initial focus.
	 * 
	 * @param comp
	 *            the component that gets initial focus when dialog is displayed
	 */
	public void setInitialFocusComponent(JComponent comp) {
		if (comp == null)
			return;

		m_initialFocusComponent = comp;
		// just in case this is set after the window is visible
		comp.requestFocus();
	}

	/**
	 * Sets the Ok flag
	 */
	protected void setOk(boolean bok) {
		m_bOk = bok;
	}

	/**
	 * Sets the text for the ok button
	 */
	public void setOkText(String txt) {
		m_okbtn.setText(txt);
	}

	/**
	 * Sets the title to the frame
	 */
	public void setTitle(String title) {
		super.setTitle(title);
	}

	/**
	 * Shows the dialog in the center of the screen
	 */
	public void showCenter() {
		com.jeta.open.gui.utils.JETAToolbox.centerWindow(this);
		show();
	}

	/**
	 * Shows/hides the ok button
	 */
	public void showOkButton(boolean bvis) {
		m_okbtn.setVisible(bvis);
	}

	/**
	 * Updates the components in the dialog based on the model state.
	 */
	public void updateComponents(java.util.EventObject evt) {
		JETAController controller = getController();
		if (controller == null) {
			// try the controller on the primary panel
			if (m_primaryPanel != null && m_primaryPanel instanceof JETAPanel) {
				controller = ((JETAPanel) m_primaryPanel).getController();
			}
		}

		if (controller != null) {
			controller.updateComponents(evt);
		}
	}

	/**
	 * Iterates through the list of listeners and allows them to process the
	 * inputs. For example, the user may be inputing parameters for a databse.
	 * We would like to perform the database operation. If the operation fails,
	 * we would like the dialog to remain on the screen so the user can make any
	 * appropriate adjustments to the dialog data. This is the purpose of the
	 * JETADialogListeners.
	 */
	protected boolean validateListeners() {
		if (m_listeners != null) {
			Iterator iter = m_listeners.iterator();
			while (iter.hasNext()) {
				JETADialogListener listener = (JETADialogListener) iter.next();
				if (!listener.cmdOk())
					return false;
			}
		}
		return true;
	}

	/**
	 * Iterates through the list of controller validators and allows them to
	 * validate the input for this dialog. If any controller fails validation,
	 * then we show an error message and return false.
	 */
	protected boolean validateValidators() {
		boolean bresult = true;
		if (m_validators != null) {
			Iterator iter = m_validators.iterator();
			while (iter.hasNext()) {
				ValidatorRule validator = (ValidatorRule) iter.next();
				if (validator != null) {
					JETARule rule = validator.getRule();
					RuleResult result = rule.check(validator.getParameters());
					if (result != null) {
						if (result.getCode() == RuleResult.FAIL_MESSAGE_ID) {
							String title = I18N.getLocalizedMessage("Error");
							JOptionPane.showMessageDialog(this, result.getMessage(), title, JOptionPane.ERROR_MESSAGE);
							bresult = false;
							break;
						}
						else if (result.getCode() == RuleResult.FAIL_NO_MESSAGE_ID) {
							bresult = false;
							break;
						}
					}
				}
			}
		}
		return bresult;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// command listener
	static class CommandListener implements ActionListener {
		private java.lang.ref.WeakReference m_dlgref;

		public CommandListener(JETADialog dlg) {
			m_dlgref = new java.lang.ref.WeakReference(dlg);
		}

		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			JETADialog dlg = (JETADialog) m_dlgref.get();
			if (dlg != null)
				dlg.actionPerformed(actionCommand, e);
		}
	}

	/**
	 * This class maintains an association between a rule and the parameters
	 * used to pass to that rule
	 */
	static class ValidatorRule {
		/** the rule that performns validation on the dialog */
		private JETARule m_rule;

		/** a list of parameters to pass to the rule */
		private Object[] m_parameters;

		/**
		 * ctor
		 */
		public ValidatorRule(JETARule rule, Object[] params) {
			m_rule = rule;
			m_parameters = params;
		}

		/**
		 * @return the parameters to pass to the rule
		 */
		public Object[] getParameters() {
			return m_parameters;
		}

		/**
		 * @return the rule responsible for validating the dialog
		 */
		public JETARule getRule() {
			return m_rule;
		}
	}
}
