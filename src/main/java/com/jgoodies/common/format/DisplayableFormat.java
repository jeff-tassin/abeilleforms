/*
 * Copyright (c) 2013 JGoodies Software GmbH. All Rights Reserved.
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

package com.jgoodies.common.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import com.jgoodies.common.display.Displayable;

/**
 * Formats implementations of the {@link Displayable} interface.
 * Cannot parse.
 * 
 * @author Karsten Lentzsch
 * 
 * @since 1.7
 */
public final class DisplayableFormat extends Format {


    public static final DisplayableFormat INSTANCE = new DisplayableFormat();


    // Instance Creation ****************************************************

    private DisplayableFormat() {
        // Overrides default constructor; prevents instantiation.
    }


    // Overriding Superclass Behavior *****************************************

    /**
     * {@inheritDoc}<p>
     *
     * Appends the object's default display string,
     * or nothing of {@code obj} is {@code null}.
     * 
     * @throws IllegalArgumentException if {@code obj} is not a {@link Displayable}
     */
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj == null) {
            return toAppendTo;
        }
        if (!(obj instanceof Displayable)) {
            throw new ClassCastException("The object to format must implement the Displayable interface.");
        }
        toAppendTo.append(((Displayable) obj).getDisplayString());
        return toAppendTo;
    }


    /**
     * Throws an {@link UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("The DisplayableFormat cannot parse.");
    }


}
