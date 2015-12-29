/*
 * Copyright (c) 2009-2013 JGoodies Software GmbH. All Rights Reserved.
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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

import com.jgoodies.common.base.Objects;
import com.jgoodies.common.base.Strings;

/**
 * Wraps a given {@link DateFormat} and adds behavior to convert to/from
 * the empty string. Therefore it holds an <em>empty value</em> that is
 * mapped to/from the empty string. The {@code #format} result
 * of the empty value is the empty string, and the {@code #parse}
 * result of the empty string is the empty value. In all other cases
 * the formatting and parsing is forwarded to the wrapped DateFormat.<p>
 *
 * Often the empty value is {@code null} but you can construct an
 * EmptyDateFormat with a given {@code Date}.<p>
 *
 * <strong>Examples:</strong><pre>
 * new EmptyDateFormat(DateFormat.getDateInstance());
 * new EmptyDateFormat(DateFormat.getDateInstance(), new Date());
 *
 * new EmptyDateFormat(DateFormat.getDateInstance(DateFormat.SHORT));
 * new EmptyDateFormat(DateFormat.getDateInstance(DateFormat.SHORT), new Date());
 * </pre>
 *
 * @author Karsten Lentzsch
 */
public final class EmptyDateFormat extends AbstractWrappedDateFormat {

    /**
     * Holds the object that represents the <em>empty</em> value.
     * The result of formatting this value is the empty string;
     * the result of parsing an empty string is this object.
     */
    private final Date emptyValue;


    // Instance Creation ****************************************************

    /**
     * Constructs an {@code EmptyFormat} that wraps the given mandatory format
     * to convert {@code null} to the empty string and vice versa.
     *
     * @param delegate  the format that handles the standard cases
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public EmptyDateFormat(DateFormat delegate) {
        this(delegate, null);
    }


    /**
     * Constructs an {@code EmptyFormat} that wraps the given mandatory format
     * to convert the given {@code emptyValue} to the empty string
     * and vice versa.
     *
     * @param delegate       the format that handles non-{@code null} values
     * @param emptyValue   the representation of the empty string
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public EmptyDateFormat(DateFormat delegate, Date emptyValue) {
        super(delegate);
        this.emptyValue = emptyValue;
    }


    // Overriding Superclass Behavior *****************************************

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo,
            FieldPosition pos) {
        return Objects.equals(date, emptyValue)
                ? toAppendTo
                : delegate.format(date, toAppendTo, pos);
    }


    @Override
    public Date parse(String source, ParsePosition pos) {
        if (Strings.isBlank(source)) {
            // DateFormat#parse(String) throws a ParseException,
            // if the parse position is 0. We change it to 1.
            pos.setIndex(1);
            return emptyValue;
        }
        return delegate.parse(source, pos);
    }


}
