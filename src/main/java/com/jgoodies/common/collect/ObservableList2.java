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

import javax.swing.event.EventListenerList;

/**
 * Adds behavior for explicit change notification to the {@link ObservableList}
 * interface. The ObservableList implementations that ship with the
 * JGoodies Common, {@link ArrayListModel} and {@link LinkedListModel},
 * implement ObservableList2.<p>
 *
 * @author Karsten Lentzsch
 * @since 1.7
 *
 * @param <E>  the type of the list elements
 */
public interface ObservableList2<E> extends ObservableList<E> {


    /**
     * Notifies all registered {@code ListDataListeners} that the element
     * at the specified index has changed. Useful if there's a content change
     * without any structural change.<p>
     *
     * This method must be called <em>after</em> the element of the list changes.
     *
     * @param index    the index of the element that has changed
     *
     * @see EventListenerList
     */
    void fireContentsChanged(int index);
    
    
    /**
     * Notifies all registered {@code ListDataListeners} that the element
     * at the specified index has changed. Useful if there's a content change
     * without any structural change.<p>
     *
     * This method must be called <em>after</em> one or more elements
     * of the list change.  The changed elements
     * are specified by the closed interval index0, index1 -- the end points
     * are included.  Note that index0 need not be less than or equal to index1.
     *
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     */
    void fireContentsChanged(int index0, int index1);

}
