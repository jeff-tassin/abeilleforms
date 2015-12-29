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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This is the base class for all controllers in an application. We follow the
 * model-view-controller pattern extensively in the UI. All controllers should
 * derive from this class. A controller is resposible for all event handlers for
 * a JETAPanel (JETAPanels handle only layout and view logic). This controller
 * class also supports disabling listeners. This is useful when updating a view
 * with components that fire events when changed programmatically such as
 * JComboBoxes. See {@link #enableEvents}. Usage:
 * 
 * <pre>
 * class MyController extends JETAController {
 * 	public MyController(JETAContainer view) {
 * 		super(view);
 * 		// assign a button handler. If the view has a menu and toolbar button
 * 		// for the same command, this assignAction method will install handlers
 * 		// on both.
 * 		assignAction(MyViewNames.ID_LOGIN_BUTTON, new LoginAction());
 * 		// assign other listeners
 * 	}
 * 
 * 	public class LoginAction implements ActionListener {
 * 		public void actionPerformed(ActionEvent evt) {
 * 			MyFrame frame = (MyFrame) getView();
 * 			//... 
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * 
 * @author Jeff Tassin
 */
public abstract class JETAController {
	/**
	 * The view we are handling events for
	 */
	private JETAContainer m_view;

	/**
	 * The single ActionListener for those components that take action
	 * listeners.
	 */

	private CommandListener m_cmdListener = new CommandListener();

	/**
	 * A map of CommandNames to ActionListeners m_commands<String,ActionListener>
	 * 
	 * @deprecated
	 */
	private ListenerManager m_action_listeners = new ListenerManager();

	/**
	 * The single ListListener for JList components
	 */
	private ListListener m_llistener = new ListListener();
	private ListenerManager m_list_listeners;

	/**
	 * ChangeListener Support
	 */
	private ComponentChangeListener m_clistener = new ComponentChangeListener();
	private ListenerManager m_change_listeners;

	private TextFocusListener m_text_focus_listener = new TextFocusListener();
	private HashMap m_txt_values = new HashMap();

	/**
	 * Flag that indicates if listener/action events should be propagated to
	 * registered listeners
	 */
	private boolean m_events_enabled = true;

	/**
	 * ctor
	 * 
	 * @param view
	 *            the view whose events we are handling.
	 */
	public JETAController(JETAContainer view) {
		m_view = view;
	}

	/**
	 * Forwarded from the view when an action occurs. This method Lookups up the
	 * name of any ActionListener in the m_commands hash map that corresponds to
	 * the actionName. It then invokes the
	 * 
	 * @see java.awt.ActionListener#actionPerformed method if a component is
	 *      found.
	 * @param actionName
	 *            the name of the action associated with the event
	 * @param evt
	 *            the action event
	 * @return true if the controller handled the action
	 */
	public boolean actionPerformed(String actionName, ActionEvent evt) {
		if (isEventsEnabled()) {
			boolean bresult = false;
			ActionListener listener = (ActionListener) m_action_listeners.getListener(actionName);

			if (listener != null) {
				listener.actionPerformed(evt);
				bresult = true;
			}

			JETAContainer view = getView();
			if (view != null) {
				UIDirector uidirector = view.getUIDirector();
				if (uidirector != null)
					uidirector.updateComponents(evt);
			}

			return bresult;
		}
		else {
			return false;
		}
	}

	/**
	 * Registers and ActionListener with this controller. This controller adds
	 * itself as a listener to all components with the given name in the
	 * associated view.
	 */
	public void assignAction(String compName, ActionListener action) {
		assignAction(getView(), compName, action);
	}

	/**
	 * The preferred way to register actions. This elimiates the need to
	 * register the button with the view's actionListener.
	 */
	private void assignAction(JETAContainer view, String commandId, ActionListener action) {
		Collection comps = view.getComponentsByName(commandId);
		if (comps.size() == 0) {
			System.out.println("JETAController.assignAction failed for: " + commandId);
		}
		Iterator iter = comps.iterator();
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if (comp != null)
				assignComponentAction(comp, commandId, action);
		}
		m_action_listeners.assignListener(commandId, action);
	}

	/**
	 * Registers this controller as an action listener with all components that
	 * have the given name found in the associated view.
	 */
	protected void assignComponentAction(Component comp, String commandId, ActionListener action) {
		try {
			if (comp instanceof AbstractButton) {
				AbstractButton btn = (AbstractButton) comp;
				/**
				 * we need to remove the listener in case this is being called
				 * for a derived view/controller. and we don't wan to
				 * re-register listeners
				 */
				btn.removeActionListener(m_cmdListener);
				btn.addActionListener(m_cmdListener);
				btn.setActionCommand(commandId);
			}
			else if (comp instanceof JTextField) {
				JTextField txtfield = (JTextField) comp;
				txtfield.removeActionListener(m_cmdListener);
				txtfield.addActionListener(m_cmdListener);
				txtfield.setActionCommand(commandId);
				/**
				 * here we add a focus listener that gets focusGained/lost
				 * events. When the focus is lost, we check the field for
				 * changes
				 */
				txtfield.addFocusListener(m_text_focus_listener);
			}
			else if (comp instanceof JComboBox) {
				JComboBox box = (JComboBox) comp;
				box.removeActionListener(m_cmdListener);
				box.addActionListener(m_cmdListener);
				box.setActionCommand(commandId);
			}
			else {

				Class[] params = new Class[] { ActionListener.class };
				Object[] values = new Object[] { m_cmdListener };

				try {
					Method m = comp.getClass().getMethod("removeActionListener", params);
					m.invoke(comp, values);
				} catch (Exception e) {

				}
				Method m = comp.getClass().getMethod("addActionListener", params);
				m.invoke(comp, values);
				/*
				 * params = new Class[] { String.class }; values = new Object[] {
				 * commandId }; m = comp.getClass().getMethod(
				 * "setActionCommand", params ); m.invoke( comp, values );
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assigns a list listener to the JList for the given name
	 */
	public void assignListener(String compName, ListSelectionListener listener) {
		assignListener(getView(), compName, listener);
	}

	/**
	 * Assigns a list listener to the JList for the given name
	 */
	private void assignListener(JETAContainer view, String compName, ListSelectionListener listener) {
		Component comp = view.getComponentByName(compName);
		if (comp instanceof JList) {
			JList list = (JList) comp;
			if (list != null) {
				if (m_list_listeners == null)
					m_list_listeners = new ListenerManager();

				list.addListSelectionListener(m_llistener);
				m_list_listeners.assignListener(compName, listener);
			}
			else {
				assert (false);
			}
		}
	}

	/**
	 * Assigns a change listener to the Component for the given name - assuming
	 * the component has method addChangeListener( ChangeListener )
	 */
	public void assignListener(String compName, ChangeListener listener) {
		assignListener(getView(), compName, listener);
	}

	/**
	 * Assigns a change listener to the Component for the given name - assuming
	 * the component has method addChangeListener( ChangeListener )
	 */
	private void assignListener(JETAContainer view, String compName, ChangeListener listener) {
		try {
			Component comp = view.getComponentByName(compName);
			if (comp != null) {

				if (m_change_listeners == null)
					m_change_listeners = new ListenerManager();

				Class[] params = new Class[] { ChangeListener.class };
				Object[] values = new Object[] { m_clistener };

				Method m = comp.getClass().getMethod("addChangeListener", params);
				m.invoke(comp, values);
				m_change_listeners.assignListener(compName, listener);
			}
			else {
				assert (false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the flag that indicates if listener/action events should be
	 * propagated to registered listeners
	 */
	public void enableEvents(boolean enable) {
		m_events_enabled = enable;
	}

	/**
	 * Returns the main action listener that is registered with components in
	 * the associated view.
	 * 
	 * @return the main action listener
	 */
	public ActionListener getPrimaryActionListener() {
		return m_cmdListener;
	}

	/**
	 * Returns the first action listener associated with the given name. If the
	 * listener is not found, null is returned
	 * 
	 * @return the action listener associated with the given component.
	 */
	public ActionListener getAction(String actionName) {
		return (ActionListener) m_action_listeners.getListener(actionName);
	}

	/**
	 * Returns the view that is associated with this controller.
	 */
	public JETAContainer getView() {
		return m_view;
	}

	/**
	 * Invokes the action assigned to the given name
	 */
	public boolean invokeAction(String actionName) {
		return actionPerformed(actionName, new ActionEvent(this, 0, actionName));
	}

	/**
	 * @return the flag that indicates if listener/action events should be
	 *         propagated to registered listeners
	 */
	public boolean isEventsEnabled() {
		return m_events_enabled;
	}

	/**
	 * Forwarded from the view when a change event occurs.
	 */
	public void stateChanged(ChangeEvent e) {
		if (isEventsEnabled()) {
			if (m_change_listeners == null)
				return;

			Component comp = (Component) e.getSource();

			ChangeListener listener = (ChangeListener) m_change_listeners.getListener(comp.getName());
			if (listener != null) {
				listener.stateChanged(e);
			}

			JETAContainer view = getView();
			if (view != null) {
				UIDirector uidirector = view.getUIDirector();
				if (uidirector != null)
					uidirector.updateComponents(e);
			}
		}
	}

	/**
	 * Forwards the call to the UIDirector if it is not null
	 */
	public void updateComponents(Object src) {
		/**
		 * This is a global update call that is not specific to any event, so we
		 * create an event.
		 */
		if (src == null)
			src = this;

		EventObject evt = null;
		if (!(src instanceof EventObject))
			evt = new EventObject(src);

		JETAContainer view = getView();
		if (view != null) {
			UIDirector uidirector = view.getUIDirector();
			if (uidirector != null)
				uidirector.updateComponents(evt);
		}
	}

	/**
	 * Override if you want to provide a validation in your controller.
	 * 
	 * @return an error message if the validation failed. Return null if the
	 *         validation succeeded
	 * @deprecated overrideValidateInputs instead
	 */
	private final String validate() {
		return null;
	}

	/**
	 * Override if you want to provide a validation in your controller.
	 * 
	 * @return an error message if the validation failed. Return null if the
	 *         validation succeeded
	 */
	private final String validateInputs() {
		return validate();
	}

	/**
	 * Forwarded from the view when a list selection event occurs. This method
	 * 
	 * @param e
	 *            the list selection event
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (isEventsEnabled()) {
			if (m_list_listeners == null)
				return;

			JList list = (JList) e.getSource();

			ListSelectionListener listener = (ListSelectionListener) m_list_listeners.getListener(list.getName());
			if (listener != null) {
				listener.valueChanged(e);
			}

			JETAContainer view = getView();
			if (view != null) {
				UIDirector uidirector = view.getUIDirector();
				if (uidirector != null)
					uidirector.updateComponents(e);
			}
		}
	}

	/**
	 * Override if you want to provide a custom validation in your controller.
	 * This is different from the standard validate because the controller must
	 * provide any error messages to the user.
	 * 
	 * @return an false message if the validation failed. This will cause the
	 *         dialog to remain on the screen
	 */
	private final boolean validateCustom() {
		return true;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// command listener
	class CommandListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			String actionCommand = e.getActionCommand();
			String actionName = ((Component) src).getName();

			if (src instanceof JTextField) {
				JTextField comp = (JTextField) src;
				m_txt_values.put(comp, comp.getText());
			}

			if (!actionName.equals(actionCommand)) {
				System.out.println("JETAController actionCommand != componentName.  actionCommand: " + actionCommand + "  componentName: " + actionName);
			}
			JETAController.this.actionPerformed(actionName, e);
		}
	}

	/**
	 * Listener for JList components
	 */
	class ListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			JETAController.this.valueChanged(e);
		}
	}

	/**
	 * Listener for those components that support change events
	 */
	class ComponentChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JETAController.this.stateChanged(e);
		}
	}

	/**
	 * Focus listener for text fields
	 */
	public class TextFocusListener implements FocusListener {

		public void focusGained(FocusEvent evt) {
			JTextField comp = (JTextField) evt.getSource();
			m_txt_values.put(comp, comp.getText());
		}

		public void focusLost(FocusEvent evt) {
			JTextField comp = (JTextField) evt.getSource();
			String old_value = (String) m_txt_values.get(comp);
			if (!comp.getText().equals(old_value)) {
				JETAController.this.actionPerformed(comp.getName(), new ActionEvent(comp, ActionEvent.ACTION_PERFORMED, comp.getName()));
			}
		}
	}

	public static class ListenerManager {
		private HashMap m_listeners = new HashMap();

		public Object getListener(String commandId) {
			return m_listeners.get(commandId);
		}

		/**
		 * Adds a listener to the list of listeners for a given commandid
		 */
		public void assignListener(String commandId, Object listener) {
			m_listeners.put(commandId, listener);
		}
	}

}
