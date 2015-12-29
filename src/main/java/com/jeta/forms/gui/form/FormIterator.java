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

package com.jeta.forms.gui.form;

import java.awt.Component;
import java.awt.Container;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.jeta.forms.gui.beans.JETABean;

/**
 * An iterator for a collection of Java Beans (java.awt.Component objects)
 * contained by a FormPanel. Only components that occupy a cell in the grid on
 * the form are returned - not children of those components. For example, if you
 * have a Java Bean such as a calendar that has several child components, only
 * the calendar instance will be returned. This iterator will not return the
 * child components of that bean. However, if a component is a nested form and
 * this iterator is nested, then this iterator will return the components in the
 * nested form (as well as the form itself).
 * 
 * If an iterator encounters a nested form instance, that object will be
 * returned (regardless of whether the nested flag is set). A component is a
 * nested form if it is an instance of a FormAccessor:
 * 
 * <PRE>
 * 
 * Iterator iter = formaccessor.beanIterator(); while( iter.hasNext() ) {
 * Component comp = (Component)iter.next(); if ( comp instanceof FormAccessor ) { //
 * found a nested form. // if this iterator is nested, the next call to <i>next</i>
 * will // return components in the nested form. } else { // found a standard
 * Java Bean } }
 * 
 * </PRE>
 * 
 * This iterator is fail-fast. If any components are added or removed by
 * invoking the underlying FormAccessors at any time after the Iterator is
 * created, the iterator will throw a ConcurrentModificationException. If nested
 * is true, then the iterator will fail if components are added to <i>any</i>
 * FormAccessor in the form hierarchy. You may safely call remove on the
 * iterator if you want to remove the component from the form. Note that you
 * should not modify the underlying form container by calling the
 * {@link java.awt.Container} methods directly. This is not recommended and can
 * also leave the form in an undefined state.
 * 
 * @author Jeff Tassin
 */
public class FormIterator implements Iterator {
	/**
	 * The form panel that we are iterating over
	 */
	private FormAccessor m_accessor;

	/**
	 * The last time the grid view was modified by an add/remove
	 */
	private long m_time_stamp;

	/**
	 * Flag that indicates if components contained by nested forms should be
	 * included If set to false, only components found in the top most form are
	 * included.
	 */
	private boolean m_nested;

	/**
	 * The index in the current container
	 */
	private int m_index = -1;

	/**
	 * Used if this iterator is nested and we are currently iterating over a
	 * nested form.
	 */
	private Iterator m_nested_iter = null;

	/**
	 * The component that last returned by next. This value is cleared every
	 * time we call next or if we call remove.
	 */
	private Component m_last_comp;

	/**
	 * This is a modification stamp that the GridView maintains. It keeps track
	 * of when the last modifiation was made to the form. The FormIterator uses
	 * this to implement fail-fast.
	 */
	private long m_mod_stamp;

	/**
	 * Constructor
	 * 
	 * @param panel
	 *            the FormPanel which this iterator is associated with.
	 * @param nested
	 *            a flag that indicates if components contained by nested forms
	 *            should be included. If set to false, only components found in
	 *            the top most form are included.
	 */
	public FormIterator(FormAccessor accessor, boolean nested) {
		m_accessor = accessor;
		m_nested = nested;
		m_index = -1;
		m_mod_stamp = ((GridView) accessor).getModificationStamp();
	}

	/**
	 * Checks if the container has been modified since this form iterator was
	 * created. This is used to test for fail-fast.
	 */
	private void checkConcurrentModification() {
		GridView view = (GridView) m_accessor;
		if (m_mod_stamp < view.getModificationStamp()) {
			throw new java.util.ConcurrentModificationException();
		}

		long nested_stamp = view.getNestedModificationStamp();
		if (nested_stamp <= 0)
			nested_stamp = m_mod_stamp;

		if (isNested() && (m_mod_stamp < nested_stamp)) {
			throw new java.util.ConcurrentModificationException();
		}
	}

	/**
	 * Returns the next component in the iteration.
	 * 
	 * @return the next component in the iteration.
	 * 
	 * @exception NoSuchElementException
	 *                iteration has no more elements.
	 */
	public boolean hasNext() {
		if (m_nested_iter != null) {
			if (m_nested_iter.hasNext()) {
				return true;
			}
			else {
				m_nested_iter = null;
			}
		}

		Container cc = m_accessor.getContainer();

		int pos = m_index + 1;
		if (pos >= cc.getComponentCount())
			return false;

		while (pos < cc.getComponentCount()) {
			Component comp = cc.getComponent(pos);
			if (comp instanceof GridComponent) {
				GridComponent gc = (GridComponent) comp;
				if (gc instanceof FormComponent) {
					m_index = pos - 1;
					return true;
				}
				else if (gc instanceof StandardComponent) {
					Component jbean = gc.getBeanDelegate();
					if (jbean != null) {
						m_index = pos - 1;
						return true;
					}
				}
			}
			else if (comp instanceof Component) {
				m_index = pos - 1;
				return true;
			}
			pos++;
		}
		m_index = pos;
		return false;
	}

	private boolean isNested() {
		return m_nested;
	}

	/**
	 * Returns the next component in the iteration.
	 * 
	 * @return the next component in the iteration.
	 * 
	 * @exception NoSuchElementException
	 *                iteration has no more elements.
	 */
	public Object next() {
		checkConcurrentModification();

		if (m_nested_iter != null)
			return m_nested_iter.next();

		m_last_comp = null;

		m_index++;
		Container cc = m_accessor.getContainer();

		if (m_index >= cc.getComponentCount())
			throw new NoSuchElementException();

		while (m_index < cc.getComponentCount()) {
			Component comp = cc.getComponent(m_index);
			if (comp instanceof GridComponent) {
				GridComponent gc = (GridComponent) comp;
				if (gc instanceof FormComponent) {
					FormComponent fc = (FormComponent) gc;
					if (isNested()) {
						Iterator niter = new FormIterator(fc.getChildView().getFormAccessor(), isNested());
						if (niter.hasNext()) {
							m_nested_iter = niter;
						}
					}
					m_last_comp = fc;
					return fc.getChildView();
				}
				else if (gc instanceof StandardComponent) {
					/**
					 * This is for handling the case where a delegate is
					 * contained by a JScrollPane (e.g. JTree, JTable etc.) We
					 * should return the scroll pane in this case.
					 */
					JETABean jetabean = gc.getBean();
					Component jbean = null;
					if (jetabean != null)
						jbean = jetabean.getBeanChildComponent();
					if (jbean != null) {
						m_last_comp = jbean;
						return jbean;
					}
				}
			}
			else if (comp instanceof Component) {
				m_last_comp = comp;
				return comp;
			}
			m_index++;
		}

		throw new NoSuchElementException();
	}

	/**
	 * 
	 * Removes from the underlying collection the last component returned by the
	 * iterator. This method can be called only once per call to <tt>next</tt>.
	 * 
	 * @exception UnsupportedOperationException
	 *                if the <tt>remove</tt> operation is not supported by
	 *                this Iterator.
	 * 
	 * @exception IllegalStateException
	 *                if the <tt>next</tt> method has not yet been called, or
	 *                the <tt>remove</tt> method has already been called after
	 *                the last call to the <tt>next</tt> method.
	 */
	public void remove() {
		checkConcurrentModification();

		if (m_nested_iter != null) {
			m_nested_iter.remove();
			return;
		}

		if (m_last_comp == null)
			throw new IllegalStateException();

		if (m_accessor.removeBean(m_last_comp) != null) {
			m_index--;
			m_last_comp = null;
			m_mod_stamp = ((GridView) m_accessor).getModificationStamp();
		}
	}

}
