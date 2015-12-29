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

import java.awt.Insets;

import com.jgoodies.forms.layout.CellConstraints;

/**
 * An interface that defines the attributes of a CellConstraints object in a
 * FormLayout. This interface is needed because we want to decouple the our
 * architecture from the knowledge needed to get the CellConstraints from the
 * FormLayout. This has the added benefit of allowing us to create Proxy objects
 * which can improve performance because the FormLayout always returns copies
 * rather than the actual CellConstraints. This causes significant delays during
 * repaints.
 * 
 * @author Jeff Tassin
 */
public interface ComponentConstraints extends Cloneable {
	/**
	 * Cloneable implementation
	 */
	public Object clone();

	/**
	 * Returns a CellConstraints copy based on this definition.
	 * 
	 * @returns a CellConstraints object based on this definition
	 */
	public CellConstraints createCellConstraints();

	/**
	 * Return the first column of the cell specified by this constraints object.
	 * 
	 * @return the first column
	 */
	public int getColumn();

	/**
	 * Return the first row of the cell specified by this constraints object.
	 * 
	 * @return the first row
	 */
	public int getRow();

	/**
	 * Return the number of columns spanned by the component associated with
	 * these constraints.
	 * 
	 * @return the number of columns spanned by the component
	 */
	public int getColumnSpan();

	/**
	 * Return the number of rows spanned by the component associated with these
	 * constraints.
	 * 
	 * @return the number of rows spanned by the component
	 */
	public int getRowSpan();

	/**
	 * Return the cell insets of the component associated with these
	 * constraints.
	 * 
	 * @return the insets for this component
	 */
	public Insets getInsets();

	/**
	 * Returns the components horizontal alignment relative to its cell.
	 * 
	 * @return the component's horizontal alignment.
	 */
	public CellConstraints.Alignment getHorizontalAlignment();

	/**
	 * Returns the components vertical alignment relative to its cell.
	 * 
	 * @return the component's vertical alignment.
	 */
	public CellConstraints.Alignment getVerticalAlignment();

}
