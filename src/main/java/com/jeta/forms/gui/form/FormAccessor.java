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

import com.jeta.open.support.SwingComponentSupport;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * A FormAccessor is used to programmatically access and modify components on a
 * form.
 * <p>
 * 
 * You should always use FormAccessors instead of modifying the underlying
 * <tt>Container</tt> directly. The main reason is because the forms component
 * hierachy may change in the future.
 * <p>
 * 
 * FormAccessors only modify the form they are associated with. A form can have
 * nested forms, but FormAccessors do not access nested forms (except when using
 * nested iterators).
 * <p>
 * 
 * Be careful when using components that have implicit JScrollPanes. The
 * designer allows you to set a <i>scroll property</i> for those components
 * that are scrollable (for example JTree, JTable, JList), and the forms runtime
 * automatically creates JScrollPanes for those components you specifiy as
 * scrollable. When iterating over a container, implicit scroll pane instances
 * will be returned rather than the underlying Java bean for those scrollable
 * components.
 * <p>
 * 
 * FormAccessors provide direct access to the underlying FormLayout and its
 * assciated container. It is not recommended that you programmatically create
 * CellConstraints and change the layout using hard-coded column and row values.
 * The reason is because it is very easy to modify the form using the Forms
 * Designer. Any modificiations can break code that has hard-coded columns and
 * rows. The best approach is to name all of your components (including the
 * scrollpanes) and access them by name whenever possible. This applies to
 * wizard-like GUIs as well. It is recommended that in these cases, you provide
 * a dummy component on the form such as an empty JLabel. You can then replace
 * the dummy component with another component at runtime (via
 * <tt>replaceBean</tt> ).
 * <p>
 * 
 * @author Jeff Tassin
 */
public interface FormAccessor extends SwingComponentSupport {
	/**
	 * Returns the name assigned to the form component associated with this
	 * accessor.
	 * 
	 * @return the name assigned to the form component associated with this
	 *         accessor.
	 */
	public String getFormName();

	/**
	 * Returns a CellConstraints instance associated with the given component.
	 * If the component is not contained by the form associated with this
	 * accessor, null is returned.
	 * 
	 * @return the constraints associated with the given component.
	 */
	public CellConstraints getConstraints(Component comp);

	/**
	 * Adds a bean to this container using the given constraints.
	 * 
	 * @param comp
	 *            the bean to add to the form.
	 * @param cc
	 *            the constraints for the bean. This must be a valid
	 *            CellConstraints instance.
	 */
	public void addBean(Component comp, CellConstraints cc);

	/**
	 * Return the actual container that has the given layout. This method should
	 * rarely be called. It is only provided for very limited cased. If you need
	 * to access the underlying FormLayout, you can retrieve it from the
	 * Container returned by this call.
	 * 
	 * @return the container associated with the FormLayout
	 */
	public Container getContainer();

	/**
	 * Defaults to beanIterator(false). See {@link #beanIterator(boolean)}
	 * 
	 * @return an iterator to all the Java Beans in this container. Beans in
	 *         nested containers are not included.
	 */
	public Iterator beanIterator();

	/**
	 * An iterator for a collection of Java Beans (java.awt.Component objects)
	 * contained by a FormPanel. Only components that occupy a cell in the grid
	 * on the form are returned - not children of those components. For example,
	 * if you have a Java Bean such as a calendar that has several child
	 * components, only the calendar instance will be returned. This iterator
	 * will not return the child components of that bean. However, if a
	 * component is a nested form and the nested parameter is true, then this
	 * iterator will return the components in the nested form (as well as the
	 * form itself).
	 * 
	 * If an iterator encounters a nested form instance, that object will be
	 * returned (regardless of whether the nested flag is set). A component is a
	 * nested form if it is an instance of a FormAccessor:
	 * 
	 * <PRE>
	 * 
	 * Iterator iter = formaccessor.beanIterator(); while( iter.hasNext() ) {
	 * Component comp = (Component)iter.next(); if ( comp instanceof
	 * FormAccessor ) { // found a nested form. // if this iterator is nested,
	 * the next call to <i>next</i> will // return components in the nested
	 * form. } else { // found a standard Java Bean } }
	 * 
	 * </PRE>
	 * 
	 * The iterator is fail-fast. If any components are added or removed by
	 * invoking the underlying FormAccessors at any time after the Iterator is
	 * created, the iterator will throw a ConcurrentModificationException. If
	 * nested is true, then the iterator will fail if components are added to
	 * <i>any</i> FormAccessor in the form hierarchy. You may safely call
	 * remove on the iterator if you want to remove the component from the form.
	 * Note that you should not modify the underlying form container by calling
	 * the <tt>Container</tt> methods directly. This is not recommended and
	 * can also leave the form in an undefined state.
	 * 
	 * @param nested
	 *            if true, all components in nested forms will be returned.
	 * @return an iterator to all the Java Beans in this container.
	 */
	public Iterator beanIterator(boolean nested);

	/**
	 * Removes a bean from the container associated with this accessor. If the
	 * given component is contained by an implicit JScrollPane or is an implicit
	 * JSCrollPane, the JScrollPane is removed.
	 * 
	 * @param comp
	 *            the component to remove.
	 * @return the component that was removed. If this method fails for any
	 *         reason then null is returned.
	 */
	public Component removeBean(Component comp);

	/**
	 * Removes a bean with the given name from the container associated with
	 * this accessor. The bean must be contained within the current form. This
	 * method will not remove beans in nested forms. If compName refers to a
	 * component contained by an implicit JScrollPane or compName directly
	 * refers to an implicit JSCrollPane, the JScrollPane is removed.
	 * 
	 * @param compName
	 *            the name of the Java Bean to remove.
	 * @return the component that was removed. If this method fails for any
	 *         reason then null is returned.
	 */
	public Component removeBean(String compName);

	/**
	 * Replaces an existing bean with a new bean. If the old component is
	 * contained by an implicit JScrollPane or is an implicit JSCrollPane, the
	 * JScrollPane is replaced.
	 * 
	 * @param oldComp
	 *            the component to replace
	 * @param newComponent
	 *            the component to add.
	 * @return the component that was replaced. If this method fails for any
	 *         reason then null is returned.
	 */
	public Component replaceBean(Component oldComp, Component newComponent);

	/**
	 * Locates an existing bean with the given name and replaces it with a new
	 * bean. The bean must be contained within the current form. This method
	 * will not remove beans in nested forms. If the old component is contained
	 * by an implicit JScrollPane or is an implicit JSCrollPane, the JScrollPane
	 * is replaced.
	 * 
	 * @param compName
	 *            the name of the component to replace.
	 * @param newComponent
	 *            the component to add.
	 * @return the component that was replaced. If this method fails for any
	 *         reason then null is returned.
	 */
	public Component replaceBean(String compName, Component newComponent);

}
