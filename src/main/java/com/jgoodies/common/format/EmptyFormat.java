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

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import com.jgoodies.common.base.Objects;
import com.jgoodies.common.base.Strings;

/**
 * Wraps a given {@code Format} and adds behavior to convert to/from
 * the empty string. Therefore it holds an <em>empty value</em>
 * (often {@code null}) that is
 * mapped to/from the empty string. The {@code #format} result
 * of the empty value is the empty string, and the {@code #parse}
 * result of the empty string is the empty value. In all other cases
 * the formatting and parsing is forwarded to the wrapped Format.<p>
 *
 * If you want to wrap a DateFormat or NumberFormat, you may use
 * {@link EmptyDateFormat} or {@link EmptyNumberFormat} resp.
 *
 * <strong>Examples:</strong><pre>
 * new EmptyFormat(new WeightFormat());
 * </pre>
 *
 * @author Karsten Lentzsch
 */
public class EmptyFormat extends Format {

    /**
     * Refers to the wrapped Format that is used to forward
     * {@code #format} and {@code #parseObject}.
     */
    private final Format delegate;

    /**
     * Holds the object that represents the <em>empty</em> value.
     * The result of formatting this value is the empty string;
     * the result of parsing an empty string is this object.
     */
    private final Object emptyValue;


    // Instance Creation ****************************************************

    /**
     * Constructs an EmptyFormat that wraps the given mandatory format
     * to convert {@code null} to the empty string and vice versa.
     *
     * @param delegate  the format that handles the standard cases
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public EmptyFormat(Format delegate) {
        this(delegate, null);
    }


    /**
     * Constructs an EmptyFormat that wraps the given mandatory format
     * to convert the given {@code emptyValue} to the empty string
     * and vice versa.
     *
     * @param delegate     the format that handles non-{@code null} values
     * @param emptyValue   the representation of the empty string
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public EmptyFormat(Format delegate, Object emptyValue) {
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


    /**
     * {@inheritDoc}<p>
     *
     * If {@code source} is empty or whitespace, the <em>emptyValue</em>
     * is returned. Otherwise parsing is forwarded to the delegate.
     */
    @Override
    public Object parseObject(String source) throws ParseException {
        return Strings.isBlank(source)
            ? emptyValue
            : super.parseObject(source);
    }


    @Override
    public final Object parseObject(String source, ParsePosition pos) {
        return delegate.parseObject(source, pos);
    }


    @Override
    public final AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return delegate.formatToCharacterIterator(obj);
    }


}
