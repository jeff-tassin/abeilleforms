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

import static com.jgoodies.common.base.Preconditions.checkNotNull;
import static com.jgoodies.common.internal.Messages.MUST_NOT_BE_NULL;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import com.jgoodies.common.base.Objects;
import com.jgoodies.common.base.Strings;

/**
 * Wraps a given {@code NumberFormat} and adds behavior to convert to/from
 * the empty string. Therefore it holds an <em>empty value</em> that is
 * mapped to/from the empty string. The {@code #format} result
 * of the empty value is the empty string, and the {@code #parse}
 * result of the empty string is the empty value. In all other cases
 * the formatting and parsing is forwarded to the wrapped NumberFormat.<p>
 *
 * <strong>Examples:</strong><pre>
 * new EmptyNumberFormat(NumberFormat().getInstance());
 * new EmptyNumberFormat(NumberFormat().getIntegerInstance(), -1);
 * </pre>
 *
 * @author Karsten Lentzsch
 */
public final class EmptyNumberFormat extends NumberFormat {


    private final NumberFormat delegate;
    private final Number emptyValue;


    // Instance Creation ****************************************************

    /**
     * Constructs an EmptyNumberFormat that wraps the given mandatory format
     * to convert {@code null} to the empty string and vice versa.
     *
     * @param delegate  the format that handles the standard cases
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public EmptyNumberFormat(NumberFormat delegate) {
        this(delegate, null);
    }


    /**
     * Constructs an EmptyNumberFormat that wraps the given mandatory format
     * to convert the given {@code emptyValue} to the empty string
     * and vice versa.
     *
     * @param delegate       the format that handles non-{@code null} values
     * @param emptyValue   the representation of the empty string
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public EmptyNumberFormat(NumberFormat delegate, int emptyValue) {
        this(delegate, Integer.valueOf(emptyValue));
    }


    /**
     * Constructs an EmptyNumberFormat that wraps the given mandatory format
     * to convert the given {@code emptyValue} to the empty string
     * and vice versa.
     *
     * @param delegate       the format that handles non-{@code null} values
     * @param emptyValue   the representation of the empty string
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public EmptyNumberFormat(NumberFormat delegate, Number emptyValue) {
        this.delegate = checkNotNull(delegate, MUST_NOT_BE_NULL, "delegate format");
        this.emptyValue = emptyValue;
    }


    // Overriding Superclass Behavior *****************************************

    /**
     * {@inheritDoc}<p>
     *
     * If {@code obj} is equal to the <em>emptyValue</em>,
     * {@code toAppendTo} is returned. Otherwise the format
     * is forwarded to the delegate.
     */
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
            FieldPosition pos) {
        return Objects.equals(obj, emptyValue)
            ? toAppendTo
            : delegate.format(obj, toAppendTo, pos);
    }


    @Override
    public StringBuffer format(double number,
                                        StringBuffer toAppendTo,
                                        FieldPosition pos) {
        return delegate.format(number, toAppendTo, pos);
    }


    @Override
    public StringBuffer format(long number,
                                        StringBuffer toAppendTo,
                                        FieldPosition pos) {
        return delegate.format(number, toAppendTo, pos);
    }


    /**
     * {@inheritDoc}<p>
     *
     * If {@code source} is empty or whitespace, the <em>emptyValue</em>
     * is returned. Otherwise parsing is forwarded to the delegate
     * - indirectly via {@link #parse(String, ParsePosition)}.
     */
    @Override
    public Object parseObject(String source) throws ParseException {
        return Strings.isBlank(source)
            ? emptyValue
            : super.parseObject(source);
    }


    /**
     * {@inheritDoc}<p>
     *
     * If {@code source} is empty or whitespace, the <em>emptyValue</em>
     * is returned. Otherwise parsing is forwarded to the delegate
     * - indirectly via {@link #parse(String, ParsePosition)}.
     */
    @Override
    public Number parse(String source) throws ParseException {
        return Strings.isBlank(source)
            ? emptyValue
            : super.parse(source);
    }


    @Override
    public Number parse(String source, ParsePosition pos) {
        return delegate.parse(source, pos);
    }


}
