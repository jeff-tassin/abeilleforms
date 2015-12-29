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

package com.jeta.forms.store.support;

import java.io.IOException;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>Matrix</code> represents a storable grid of objects. The grid can
 * grow or shrink by adding/removing rows or column.
 * 
 * @author Jeff Tassin
 */
public class Matrix extends AbstractJETAPersistable {
	static final long serialVersionUID = -6465003684197543526L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 1;

	/**
	 * An array of object[].
	 */
	private Object[] m_rows;

	public Matrix() {
		m_rows = new Object[0];
	}

	/**
	 * ctor
	 */
	public Matrix(int rows, int cols) {
		m_rows = new Object[rows];
		for (int row = 0; row < rows; row++) {
			m_rows[row] = new Object[cols];
		}
	}

	/**
	 * @return the number of rows in the matrix
	 */
	public int getRowCount() {
		return (m_rows == null ? 0 : m_rows.length);
	}

	/**
	 * @return the number of columns in the matrix
	 */
	public int getColumnCount() {
		if (m_rows == null || m_rows.length == 0)
			return 0;

		Object[] row = (Object[]) m_rows[0];
		if (row == null)
			return 0;
		else
			return row.length;
	}

	/**
	 * @return the value at the given row/column
	 */
	public Object getValue(int row, int col) {
		if (row >= 0 && row < getRowCount()) {
			Object[] rowdata = (Object[]) m_rows[row];
			if (rowdata != null && col >= 0 && col < rowdata.length) {
				return rowdata[col];
			}
		}
		return null;
	}

	/**
	 * Sets the value at the given row/column
	 */
	public void setValue(int row, int col, Object value) {
		if (row >= 0 && row < getRowCount()) {
			Object[] rowdata = (Object[]) m_rows[row];
			if (rowdata != null && col >= 0 && col < rowdata.length) {
				rowdata[col] = value;
			}
		}
	}

	/**
	 * Inserts a row in the matrix
	 * 
	 * @param row
	 *            the 0-based row
	 */
	public void insertRow(int row) {
		if (row > m_rows.length)
			row = m_rows.length;
		if (row < 0)
			row = 0;

		Object[] newdata = insertElement(m_rows, row);
		newdata[row] = new Object[getColumnCount()];
		m_rows = newdata;
	}

	/**
	 * Inserts a column in the matrix
	 * 
	 * @param col
	 *            the 0-based column
	 */
	public void insertColumn(int col) {
		if (col > getColumnCount())
			col = getColumnCount();
		if (col < 0)
			col = 0;

		for (int row = 0; row < m_rows.length; row++) {
			Object[] newrow = insertElement((Object[]) m_rows[row], col);
			m_rows[row] = newrow;
		}
	}

	/**
	 * Removes a column from the matrix
	 * 
	 * @param col
	 *            the 0-based column
	 */
	public void removeColumn(int col) {
		if (col > getColumnCount())
			col = getColumnCount();
		if (col < 0)
			col = 0;

		for (int row = 0; row < m_rows.length; row++) {
			Object[] newrow = deleteElement((Object[]) m_rows[row], col);
			m_rows[row] = newrow;
		}
	}

	/**
	 * Removes a row from the matrix
	 * 
	 * @param row
	 *            the 0-based row
	 */
	public void removeRow(int row) {
		if (row > m_rows.length)
			row = m_rows.length;
		if (row < 0)
			row = 0;

		Object[] newdata = deleteElement(m_rows, row);
		m_rows = newdata;
	}

	private Object[] insertElement(Object[] src, int index) {
		Object[] newdata = new Object[src.length + 1];
		System.arraycopy(src, 0, newdata, 0, index);
		if (src.length - index > 0) {
			System.arraycopy(src, index, newdata, index + 1, src.length - index);
		}
		return newdata;
	}

	private Object[] deleteElement(Object[] src, int index) {
		if ((src.length - 1) < 0) {
			assert (false);
		}
		Object[] newdata = new Object[src.length - 1];
		System.arraycopy(src, 0, newdata, 0, index);
		if ((src.length - index - 1) > 0) {
			System.arraycopy(src, index + 1, newdata, index, src.length - index - 1);
		}
		return newdata;
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_rows = (Object[]) in.readObject("rows");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("rows", m_rows);
	}

}
