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
 * A read-only implementation of ComponentConstraints.
 * 
 * @author Jeff Tassin
 */
public class ReadOnlyConstraints implements ComponentConstraints {
	private int m_col;
	private int m_row;
	private int m_colspan = 1;
	private int m_rowspan = 1;

	private Insets m_insets = EMPTY_INSETS;

	private CellConstraints.Alignment m_halign = CellConstraints.DEFAULT;
	private CellConstraints.Alignment m_valign = CellConstraints.DEFAULT;

	private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

	/**
	 * Creates a <code>ReadOnlyConstraints</code> object with the specified
	 * row and column
	 */
	public ReadOnlyConstraints(int col, int row) {
		m_col = col;
		m_row = row;
	}

	/**
	 * Creates a <code>ReadOnlyConstraints</code> object with the specified
	 * row and column and span.
	 */
	public ReadOnlyConstraints(int col, int row, int colspan, int rowspan) {
		m_col = col;
		m_row = row;
		m_colspan = colspan;
		m_rowspan = rowspan;
	}

	/**
	 * Creates a <code>ReadOnlyConstraints</code> object with the specified
	 * constraint information.
	 */
	public ReadOnlyConstraints(int col, int row, int colspan, int rowspan, CellConstraints.Alignment halign, CellConstraints.Alignment valign, Insets insets) {
		m_col = col;
		m_row = row;
		m_colspan = colspan;
		m_rowspan = rowspan;
		m_halign = halign;
		m_valign = valign;
		m_insets = insets;
	}

	/**
	 * Creates a <code>ReadOnlyConstraints</code> object with the specified
	 * constraint information.
	 */
	public ReadOnlyConstraints(ComponentConstraints cc) {
		this(cc.getColumn(), cc.getRow(), cc.getColumnSpan(), cc.getRowSpan(), cc.getHorizontalAlignment(), cc.getVerticalAlignment(), cc.getInsets());
	}

	/**
	 * Creates a CellConstraints object that can be used by the FormLayout.
	 * 
	 * @returns a CellConstraints object based on this definition
	 */
	public CellConstraints createCellConstraints() {
		return new CellConstraints(m_col, m_row, m_colspan, m_rowspan, m_halign, m_valign, m_insets);
	}

	/**
	 * Always returns a read only copy of the cell constraints
	 */
	public Object clone() {
		return new ReadOnlyConstraints(getColumn(), getRow(), getColumnSpan(), getRowSpan(), m_halign, m_valign, m_insets);
	}

	/**
	 * Return the first column occupied by the component associated with these
	 * constraints.
	 * 
	 * @return the first column
	 */
	public int getColumn() {
		return m_col;
	}

	/**
	 * Return the first row occupied by the component associated with these
	 * constraints.
	 * 
	 * @return the first row
	 */
	public int getRow() {
		return m_row;
	}

	/**
	 * Return the number of columns occupied by the component associated with
	 * these constraints.
	 * 
	 * @return the column span
	 */
	public int getColumnSpan() {
		return m_colspan;
	}

	/**
	 * Return the number of rows occupied by the component associated with these
	 * constraints.
	 * 
	 * @return the column span
	 */
	public int getRowSpan() {
		return m_rowspan;
	}

	/**
	 * @return the insets for this component
	 */
	public Insets getInsets() {
		return m_insets;
	}

	/**
	 * @return the component's horizontal alignment.
	 */
	public CellConstraints.Alignment getHorizontalAlignment() {
		return m_halign;
	}

	/**
	 * @return the component's vertical alignment.
	 */
	public CellConstraints.Alignment getVerticalAlignment() {
		return m_valign;
	}

}
