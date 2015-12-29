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

package com.jeta.forms.store.memento;

import java.awt.Insets;
import java.io.IOException;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Memento for serializing the state of a CellConstraints object.
 * 
 * @author Jeff Tassin
 */
public class CellConstraintsMemento extends AbstractJETAPersistable {
	static final long serialVersionUID = 7894081609390290274L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 1;

	/**
	 * The column to assign to the component
	 */
	private int m_column;

	/**
	 * The row to assign to the component.
	 */
	private int m_row;

	/**
	 * The number of columns spanned by the component
	 */
	private int m_colspan;

	/**
	 * The number of rows spanned by the component
	 */
	private int m_rowspan;

	/**
	 * The insets to apply to the component relative to its cell.
	 */
	private Insets m_insets;

	/**
	 * The vertical and horizontal cell alignments encoded as Strings
	 */
	private String m_valign;
	private String m_halign;

	/**
	 * Creates an unitialized <code>CellConstraintsMemento</code> object.
	 */
	public CellConstraintsMemento() {

	}

	/**
	 * Creates a <code>CellConstraintsMemento</code> object with the specified
	 * cell constraints.
	 * 
	 * @param cc
	 *            the cell constraints.
	 */
	public CellConstraintsMemento(CellConstraints cc) {
		m_column = cc.gridX;
		m_row = cc.gridY;
		m_colspan = cc.gridWidth;
		m_rowspan = cc.gridHeight;
		m_halign = cc.hAlign.toString();
		m_valign = cc.vAlign.toString();
		m_insets = cc.insets;
	}

	/**
	 * Creates a cell constraints object based on the data in this memento
	 */
	public CellConstraints createCellConstraints() {
		try {
			return new CellConstraints(m_column, m_row, m_colspan, m_rowspan, FormUtils.toAlignment(m_halign), FormUtils.toAlignment(m_valign), m_insets);
		} catch (Exception e) {
			/**
			 * an exception can potentially be thrown if the vertical or
			 * horizontal alignments get mangled somehow. This should never
			 * happen though
			 */
			e.printStackTrace();
			return new CellConstraints();
		}
	}

	/**
	 * Returns the row to assign to a component.
	 */
	public int getRow() {
		return m_row;
	}

	/**
	 * Returns the column to assign to a component.
	 */
	public int getColumn() {
		return m_column;
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_column = in.readInt("column");
		m_row = in.readInt("row");
		m_colspan = in.readInt("colspan");
		m_rowspan = in.readInt("rowspan");
		m_halign = (String) in.readObject("halign");
		m_valign = (String) in.readObject("valign");
		m_insets = (Insets) in.readObject("insets");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeInt("column", m_column);
		out.writeInt("row", m_row);
		out.writeInt("colspan", m_colspan);
		out.writeInt("rowspan", m_rowspan);
		out.writeObject("halign", m_halign);
		out.writeObject("valign", m_valign);
		out.writeObject("insets", m_insets);
	}

}
