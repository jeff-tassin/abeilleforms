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
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Reduces the effort required to write custom DateFormat implementations
 * that retain the ability to iterate through Dates with the arrow keys
 * in formatted text fields (using an appropriate DateFormatter).
 *
 * This class wraps a given {@code DateFormat} and delegates most
 * of its behavior to the delegate, except for the abstract methods
 * {@link #format(Date, StringBuffer, FieldPosition)} and
 * {@link #parse(String, ParsePosition)} that can be overridden by
 * a subclass. For example the subclass EmptyDateFormat wraps a DateFormat
 * but it parses the empty String and returns {@code null} where the
 * predefined Java DateFormats throw an exception.<p>
 *
 * @author Karsten Lentzsch
 */
public abstract class AbstractWrappedDateFormat extends DateFormat {

    /**
     * Refers to the wrapped Format that is used to forward
     * {@code #format} and {@code #parseObject}.
     */
    protected final DateFormat delegate;


    // Instance Creation ******************************************************

    /**
     * Constructs an AbstractWrappedDateFormat that wraps the given mandatory
     * format.
     *
     * @param delegate  the format that handles the standard cases
     *
     * @throws NullPointerException if {@code delegate} is {@code null}
     */
    public AbstractWrappedDateFormat(DateFormat delegate) {
        this.delegate = checkNotNull(delegate, MUST_NOT_BE_NULL, "delegate format");
    }


    // Abstract Behavior ******************************************************

    @Override
    public abstract StringBuffer format(Date date, StringBuffer toAppendTo,
            FieldPosition pos);


    @Override
    public abstract Date parse(String source, ParsePosition pos);


    // Pure Delegation ********************************************************

    @Override
    public Calendar getCalendar() {
        return delegate.getCalendar();
    }


    @Override
    public void setCalendar(Calendar newCalendar) {
        delegate.setCalendar(newCalendar);
    }


    @Override
    public NumberFormat getNumberFormat() {
        return delegate.getNumberFormat();
    }


    @Override
    public void setNumberFormat(NumberFormat newNumberFormat) {
        delegate.setNumberFormat(newNumberFormat);
    }


    @Override
    public TimeZone getTimeZone() {
        return delegate.getTimeZone();
    }


    @Override
    public void setTimeZone(TimeZone zone) {
        delegate.setTimeZone(zone);
    }


    @Override
    public boolean isLenient() {
        return delegate.isLenient();
    }


    @Override
    public void setLenient(boolean lenient) {
        delegate.setLenient(lenient);
    }


    @Override
    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return delegate.formatToCharacterIterator(obj);
    }


}
