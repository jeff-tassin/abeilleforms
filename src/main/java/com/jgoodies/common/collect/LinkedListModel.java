/*
 * Copyright (c) 2002-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.common.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Adds {@link javax.swing.ListModel} capabilities to its superclass.
 * It allows to observe changes in the content and structure. Useful for
 * Lists that are bound to list views such as JList, JComboBox and JTable.<p>
 *
 * This class should be be final and it will be marked final in a future
 * version, if its subclass in the JGoodies Binding has been removed.<p>
 *
 * @author Karsten Lentzsch
 *
 * @see ObservableList
 * @see ArrayListModel
 *
 * @param <E>  the type of the list elements
 */
public class LinkedListModel<E> extends LinkedList<E> implements ObservableList2<E> {

    private static final long serialVersionUID = 5753378113505707237L;


    // Instance Creation ******************************************************

    /**
     * Constructs an empty LinkedListModel.
     */
    public LinkedListModel() {
        // Just invoke the super constructor implicitly.
    }


    /**
     * Constructs a LinkedListModel containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param  c the collection whose elements are to be placed into this list.
     * @throws NullPointerException if {@code c} is {@code null}
     */
    public LinkedListModel(Collection<? extends E> c) {
        super(c);
    }


    // Overriding Superclass Behavior *****************************************

    @Override
    public final void add(int index, E element) {
        super.add(index, element);
        fireIntervalAdded(index, index);
    }


    @Override
    public final boolean add(E e) {
        int newIndex = size();
        super.add(e);
        fireIntervalAdded(newIndex, newIndex);
        return true;
    }


    @Override
    public final boolean addAll(int index, Collection<? extends E> c) {
        boolean changed = super.addAll(index, c);
        if (changed) {
            int lastIndex = index + c.size() - 1;
            fireIntervalAdded(index, lastIndex);
        }
        return changed;
    }


    /**
     * Removes from this collection all of its elements that are contained in
     * the specified collection (optional operation). <p>
     *
     * This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's so contained, it's removed from
     * this collection with the iterator's <tt>remove</tt> method.<p>
     *
     * Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements in common with the
     * specified collection.
     *
     * @param c elements to be removed from this collection.
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call.
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
     *         is not supported by this collection.
     * @throws NullPointerException if the specified collection is null.
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<?> e = iterator();
        while (e.hasNext()) {
            if (c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }


    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this collection all of its elements that are not contained in the
     * specified collection. <p>
     *
     * This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's not so contained, it's removed
     * from this collection with the iterator's <tt>remove</tt> method.<p>
     *
     * Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements not present in the
     * specified collection.
     *
     * @param c elements to be retained in this collection.
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call.
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
     *         is not supported by this Collection.
     * @throws NullPointerException if the specified collection is null.
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> e = iterator();
        while (e.hasNext()) {
            if (!c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public final void addFirst(E e) {
        super.addFirst(e);
        fireIntervalAdded(0, 0);
    }


    @Override
    public final void addLast(E e) {
        int newIndex = size();
        super.addLast(e);
        fireIntervalAdded(newIndex, newIndex);
    }


    @Override
    public final void clear() {
        if (isEmpty()) {
            return;
        }

        int oldLastIndex = size() - 1;
        super.clear();
        fireIntervalRemoved(0, oldLastIndex);
    }


    @Override
    public final E remove(int index) {
        E removedElement = super.remove(index);
        fireIntervalRemoved(index, index);
        return removedElement;
    }


    @Override
    public final boolean remove(Object o) {
        int index = indexOf(o);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }


    @Override
    public final E removeFirst() {
        E first = super.removeFirst();
        fireIntervalRemoved(0, 0);
        return first;
    }


    @Override
    public final E removeLast() {
        int lastIndex = size() - 1;
        E last = super.removeLast();
        fireIntervalRemoved(lastIndex, lastIndex);
        return last;
    }


    @Override
    protected final void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        fireIntervalRemoved(fromIndex, toIndex - 1);
    }


    @Override
    public final E set(int index, E element) {
        E previousElement = super.set(index, element);
        fireContentsChanged(index, index);
        return previousElement;
    }


    @Override
    public final ListIterator<E> listIterator(int index) {
        return new ReportingListIterator(super.listIterator(index));
    }


    // ListModel Field ********************************************************

    /**
     * Holds the registered ListDataListeners. The list that holds these
     * listeners is initialized lazily in {@code #getEventListenerList}.
     *
     * @see #addListDataListener(ListDataListener)
     * @see #removeListDataListener(ListDataListener)
     */
    private EventListenerList listenerList;


    // ListModel Implementation ***********************************************

    @Override
	public final void addListDataListener(ListDataListener l) {
        getEventListenerList().add(ListDataListener.class, l);
    }


    @Override
	public final void removeListDataListener(ListDataListener l) {
        getEventListenerList().remove(ListDataListener.class, l);
    }


    @Override
	public final E getElementAt(int index) {
        return get(index);
    }


    @Override
	public final int getSize() {
        return size();
    }


    // Explicit Change Notification *******************************************

    @Override
    public final void fireContentsChanged(int index) {
        fireContentsChanged(index, index);
    }


    /**
     * {@inheritDoc}
     * 
     * @since 1.7
     */
    @Override
    public final void fireContentsChanged(int index0, int index1) {
        Object[] listeners = getEventListenerList().getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this,
                            ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }


    // ListModel Helper Code **************************************************

    /**
     * Returns an array of all the list data listeners
     * registered on this {@code LinkedListModel}.
     *
     * @return all of this model's {@code ListDataListener}s,
     *         or an empty array if no list data listeners
     *         are currently registered
     *
     * @see #addListDataListener(ListDataListener)
     * @see #removeListDataListener(ListDataListener)
     */
    public final ListDataListener[] getListDataListeners() {
        return getEventListenerList().getListeners(ListDataListener.class);
    }


    /**
     * This method must be called <em>after</em> one or more elements
     * are added to the model.  The new elements
     * are specified by a closed interval index0, index1 -- the end points
     * are included.  Note that index0 need not be less than or equal to index1.
     *
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     */
    private void fireIntervalAdded(int index0, int index1) {
        Object[] listeners = getEventListenerList().getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }


    /**
     * This method must be called <em>after</em>  one or more elements
     * are removed from the model.
     * {@code index0} and {@code index1} are the end points
     * of the interval that's been removed.  Note that {@code index0}
     * need not be less than or equal to {@code index1}.
     *
     * @param index0 one end of the removed interval,
     *               including {@code index0}
     * @param index1 the other end of the removed interval,
     *               including {@code index1}
     * @see EventListenerList
     */
    private void fireIntervalRemoved(int index0, int index1) {
        Object[] listeners = getEventListenerList().getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }


    /**
     * Lazily initializes and returns the event listener list used
     * to notify registered listeners.
     *
     * @return the event listener list used to notify listeners
     */
    private EventListenerList getEventListenerList() {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        return listenerList;
    }


    // Helper Class ***********************************************************

    /**
     * A ListIterator that fires ListDataEvents if elements are added or removed.
     */
    private final class ReportingListIterator implements ListIterator<E> {

        /**
         * Refers to the wrapped ListIterator that is used
         * to forward all ListIterator methods to.
         */
        private final ListIterator<E> delegate;

        /**
         * Holds the object that was returned last by the underlying
         * ListIteratur. Used to determine the index of the element removed.
         */
        private int lastReturnedIndex;

        ReportingListIterator(ListIterator<E> delegate) {
            this.delegate = delegate;
            lastReturnedIndex = -1;
        }

        @Override
		public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
		public E next() {
            lastReturnedIndex = nextIndex();
            return delegate.next();
        }

        @Override
		public boolean hasPrevious() {
            return delegate.hasPrevious();
        }

        @Override
		public E previous() {
            lastReturnedIndex = previousIndex();
            return delegate.previous();
        }

        @Override
		public int nextIndex() {
            return delegate.nextIndex();
        }

        @Override
		public int previousIndex() {
            return delegate.previousIndex();
        }

        @Override
		public void remove() {
            int oldSize = size();
            delegate.remove();
            int newSize = size();
            if (newSize < oldSize) {
                LinkedListModel.this.fireIntervalRemoved(lastReturnedIndex, lastReturnedIndex);
            }
        }

        @Override
		public void set(E e) {
            delegate.set(e);
            LinkedListModel.this.fireContentsChanged(lastReturnedIndex);
        }

        @Override
		public void add(E e) {
            delegate.add(e);
            int newIndex = previousIndex();
            LinkedListModel.this.fireIntervalAdded(newIndex, newIndex);
            lastReturnedIndex = -1;
        }

    }


}
