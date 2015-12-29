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

package com.jeta.forms.components.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.LayoutManager;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormAccessor;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.FormIterator;
import com.jeta.forms.gui.formmgr.FormManagerUtils;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;
import com.jeta.open.i18n.I18NUtils;

/**
 * This is the main panel class used to load and view a form during runtime.
 * Usage:
 * 
 * <pre>
 * FormPanel panel = new FormPanel(&quot;com/mycorp/app/gui/login/loginView.jfrm&quot;);
 * </pre>
 * 
 * It assumed that all form files are located in your classpath.
 * <p>
 * 
 * You should only use the published APIs to programmatically add, remove, or
 * access Swing Components from a form. If you need to programmatically change a
 * form, you use a FormAccessor, see {@link #getFormAccessor(String) }. If you
 * pass a valid form name, this method will return a FormAccessor instance. Use
 * FormAccessors to access the FormLayout or to add, remove, change, or
 * enumerate components in the underlying container.
 * <p>
 * 
 * <PRE>
 * 
 * FormPanel myform = new FormPanel( "test.jfrm" ); <i>// where the main form in
 * test.jfrm is named "settings"</i> FormAccessor form_accessor =
 * (FormAccessor)myform.getFormAccessor( "settings" ); <i>// adds a component at
 * column 2 and row 5</i> form_accessor.addBean( new JButton("Test"), new
 * CellConstraints( 2, 5 ) );
 * 
 * <i>// or replace the component named 'wizard.view' with a different
 * component.</i> FormPanel wiz_view = new FormPanel( "pane2.jfrm" );
 * form_accessor.replaceBean( "wizard.view", wiz_view );
 * 
 * 
 * 
 * <i>// use FormAccessor to iterate over components in a form as well</i>
 * Iterator iter = formaccessor.beanIterator(); while( iter.hasNext() ) {
 * Component comp = (Component)iter.next(); if ( comp instanceof FormAccessor ) {
 * <i>// found a nested form.</i> <i>// if this iterator is nested, the next
 * call to next() will</i> <i>// return components in the nested form.</i> }
 * else { <i>// found a standard Java Bean</i> } }
 * 
 * </PRE>
 * 
 * @author Jeff Tassin
 */
public class FormPanel extends JETAPanel {

	/**
	 * A HashMap<String,Object> of names to Objects. This is a utility for
	 * clients of this class. One example of usage is ButtonGroups. We store
	 * ButtonGroups here according to their name. At runtime buttons can find
	 * their associated group and add themselves accordingly.
	 */
	private HashMap m_user_objects = new HashMap();

	/**
	 * The top level form component
	 */
	private FormComponent m_form_comp;

	/**
	 * The focus traversal policy.
	 */
	private FocusTraversalPolicy m_focus_policy;
	private boolean m_policy_set = false;

	/**
	 * FormPanel constructor. Loads the form from the given path. This
	 * constructor does not throw an exception if the form resource cannot be
	 * found. Instead, it will display an error message in the panel.
	 * 
	 * @param formPath
	 *            the path to the form file. This path can be absolute or
	 *            relative to the classpath.
	 */
	public FormPanel(String formPath) {
		com.jeta.forms.defaults.DefaultInitializer.initialize();

		boolean designmode = FormUtils.isDesignMode();
		try {
			/** always assume design mode is false for FormPanels */
			FormUtils.setDesignMode(false);
			FormComponent fc = FormManagerUtils.openPackagedForm(formPath);
			fc.postInitialize(this);
			m_form_comp = fc;
			addForm(fc, new BorderLayout(), BorderLayout.CENTER);
		} catch (Exception e) {
			/** show an error message in the panel if we can load the form */
			BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
			setLayout(layout);
			JLabel error_label = new JLabel(I18N.getLocalizedMessage("Error:"));
			error_label.setForeground(java.awt.Color.red);
			error_label.setAlignmentX(LEFT_ALIGNMENT);

			JLabel form_label = new JLabel(formPath);
			form_label.setForeground(java.awt.Color.red);
			form_label.setAlignmentX(LEFT_ALIGNMENT);
			form_label.setToolTipText(formPath);
			add(error_label);
			add(form_label);
			add(javax.swing.Box.createVerticalStrut(5));
		} finally {
			FormUtils.setDesignMode(designmode);
		}
	}

	/**
	 * FormPanel constructor. Creates a FormPanel using the given InputStream.
	 * The InputStream must reference a valid underlying .jfrm.
	 * 
	 * @throws FormException
	 *             if any type of I/O error occurs or the input stream is not a
	 *             valid form file.
	 */
	public FormPanel(InputStream istream) throws FormException {
		try {
			com.jeta.forms.defaults.DefaultInitializer.initialize();

			FormComponent fc = FormManagerUtils.openForm(istream);
			m_form_comp = fc;

			setLayout(new BorderLayout());
			add(fc, BorderLayout.CENTER);
		} catch (Exception e) {
			if (e instanceof FormException)
				throw (FormException) e;

			throw new FormException("FormPane failed to load form (.jfrm) from InputStream", e);
		}
	}

	/**
	 * FormPanel constructor. Creates a FormPanel using the given FormComponent
	 * as the content.
	 */
	public FormPanel(FormComponent fc) {
		com.jeta.forms.defaults.DefaultInitializer.initialize();

		setLayout(new BorderLayout());
		add(fc, BorderLayout.CENTER);
		fc.postInitialize(this);
		m_form_comp = fc;
	}

	/**
	 * Adds the form to this panel using the given layout manager and
	 * constraints
	 */
	protected void addForm(FormComponent form, LayoutManager layout, Object constraints) {
		setLayout(layout);
		add(form, constraints);
	}

	/**
	 * Returns an iterator for a collection of Java Beans (java.awt.Component
	 * objects) contained by this form and its nested forms. Only components
	 * that occupy a cell in the grid on the form are returned - not children of
	 * those components. So, if you have a Java Bean that has several child
	 * components, only the Java Bean will be returned and not its children.
	 * This iterator is fail-fast. If any components are added or removed by
	 * invoking the underlying FormAccessors at any time after the Iterator is
	 * created, the iterator will throw a ConcurrentModificationException. If
	 * nested is set to true, then the iterator will fail if components are
	 * added to <i>any</i> FormAccessor in the form hierarchy. If nested if
	 * false, the iterator will fail only if modifications are made to the Form
	 * associated with the current FormAccessor. You may safely call remove on
	 * the iterator if you want to remove the component from the form.
	 * 
	 * @return an iterator to a collection of components (java.awt.Component
	 *         objects) contained by this form.
	 */
	public Iterator beanIterator(boolean nested) {
		return new FormIterator(getFormAccessor(), nested);
	}

	/**
	 * Returns the user object associated with the given name. This method does
	 * not return a Swing component (use instead
	 * {@link com.jeta.open.gui.framework.JETAPanel#getComponentByName}).
	 * Rather, this method returns any user object that was associated with this
	 * panel by calling {@link #put(String,Object)}. Null is returned if the
	 * object does not exist.
	 */
	public Object get(String objName) {
		if (objName == null)
			return null;
		else
			return m_user_objects.get(objName);
	}

	/**
	 * Returns the parent container that contains the top-level form in this
	 * panel. You should rarely have to call this method. Note that a <B>better
	 * and safer solution</B> is to name the form in the builder and call
	 * {@link #getFormAccessor(String)}
	 */
	public Container getFormContainer() {
		return m_form_comp.getChildView().getFormContainer();
	}

	/**
	 * Return an instance of a FormAccessor that is associated with the top-most
	 * form in this panel (recall that a form can have nested forms). Use
	 * FormAccessors if you want to programmatically change the underlying
	 * FormLayout and/or container.
	 * 
	 * @return the FormAccessor associated with the topmost form in this panel.
	 */
	public FormAccessor getFormAccessor() {
		return m_form_comp.getChildView().getFormAccessor();
	}

	/**
	 * Return an instance of a FormAccessor that has the given name. This is the
	 * same name you gave to the form (either the main form or nested forms) in
	 * the designer. Use FormAccessors if you want to programmatically change
	 * the underlying FormLayout and/or container.
	 * 
	 * @param compName
	 *            the name of the form to retrieve.
	 * @return the FormAccessor associated with the named form. Null is returned
	 *         if component cannot be found with the given name or if the
	 *         component is not a FormAccessor object.
	 */
	public FormAccessor getFormAccessor(String compName) {
		Component comp = getComponentByName(compName);
		if (comp instanceof FormAccessor)
			return (FormAccessor) comp;
		else
			return null;
	}

	/**
	 * Puts the given object into the user objects map. If an object already
	 * exists for the given name, it is overwritten. Objects can be retrieved
	 * from the map by calling {@link #get( String )}
	 * 
	 * @param objName
	 *            the name of the object
	 * @param obj
	 *            the object
	 */
	public void put(String objName, Object obj) {
		if (objName != null) {
			m_user_objects.put(objName, obj);
		}
	}

	/**
	 * Revalidates this panel.
	 */
	public void revalidate() {
		if (m_form_comp != null)
			m_form_comp.revalidate();

		super.revalidate();
	}

	public void applyComponentOrientation(ComponentOrientation orientation) {
		I18NUtils.applyComponentOrientation(this, orientation);
		super.applyComponentOrientation(orientation);
	}

	/**
	 * Sets the focus traversal policy for this panel. Only call this if you
	 * wish to override the default handling provided by the form.
	 */
	public void setFocusTraversalPolicy(FocusTraversalPolicy policy) {
		super.setFocusTraversalPolicy(policy);
		// m_focus_policy = null;
		// m_policy_set = true;
	}

	/**
	 * If this form panel contains a form with a custom focus policy, you should
	 * call updateFocusPolicy whenever you programatically add or remove
	 * components from the form or any nested forms. If a custom focus policy
	 * was not assigned in the Form Designer, then this method is not needed.
	 */
	public void updateFocusPolicy() {
		// no longer used.
	}

	/**
	 * Override so we can update the underlying FormComponent
	 */
	public void updateUI() {
		super.updateUI();
		if (m_form_comp != null)
			m_form_comp.updateUI();
	}
}
