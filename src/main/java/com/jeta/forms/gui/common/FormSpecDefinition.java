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

package com.jeta.forms.gui.common;


/**
 * An interface that fully defines a RowSpec or ColumnSpec
 * 
 * @author Jeff Tassin
 */
public interface FormSpecDefinition {
	/**
	 * @return the alignment string depending on the specification type column:
	 *         LEFT, CENTER, RIGHT, RILL row: TOP, CENTER, BOTTOM, RILL
	 */
	public String getAlignment();

	/**
	 * @return CONSTANT, COMPONENT, BOUNDED
	 */
	public String getSizeType();

	/**
	 * @return the units (integer) (double) PX, PT, DLU IN, MM, CM
	 */
	public String getConstantUnits();

	/**
	 * @return the size.
	 */
	public double getConstantSize();

	/**
	 * @return the component size: MIN, PREF, DEFAULT
	 */
	public String getComponentSize();

	/**
	 * @return the bounded size MIN, MAX
	 */
	public String getBoundedSize();

	/**
	 * @return the resize behavior NONE, GROW
	 */
	public String getResize();

	/**
	 * @return the resize weight (0.0-1.0)
	 */
	public double getResizeWeight();

}
