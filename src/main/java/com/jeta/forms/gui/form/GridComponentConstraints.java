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
import java.lang.reflect.Field;
import java.util.Map;

import com.jeta.forms.gui.common.FormUtils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A <code>GridComponentConstraints</code> is a type of bridge to a live
 * CellConstraints in a FormLayout. The FormLayout always makes a clone of the
 * CellConstraints when returning them to a caller. This is very inefficient for
 * our needs since we need to get the CellConstraints on every repaint. Instead,
 * we use this class as a bridge and decouple ourselves from CellConstraints.
 * Currently, we use a modified FormLayout class that allows access to the
 * constraint map. This required a single line change to the FormLayout. We
 * could have used reflection to get this information, but this has implications
 * for WebStart applications.
 * 
 * @author Jeff Tassin
 */
public class GridComponentConstraints implements ComponentConstraints {
	/**
	 * The FormLayout keeps a map of Components to CellConstraints. We directly
	 * access this map from the FormLayout instance. This is a major hack for
	 * now, but there is no other solution yet.
	 */
	private Map m_constraintmap;

	/**
	 * The GridComponent associated with these constraints.
	 */
	private GridComponent m_gc;

	/**
	 * The view associated with the constraint map. This can change if we change
	 * views in the application. A view change can cause a form to change
	 * parents and therefor the constraintmap becomes invalid.
	 */
	private GridView m_parentview;

	/**
	 * @link dependency
	 * @label Gets constraints via reflection
	 */
	/* # com.jgoodies.forms.layout.FormLayout lnkFormLayout; */

	/**
	 * Creates a <code>GridComponentConstraints</code> object that is bound to
	 * the specified grid component.
	 */
	public GridComponentConstraints(GridComponent gc) {
		m_gc = gc;
		FormUtils.safeAssert(m_gc != null);
		FormUtils.safeAssert(m_gc.getParentView() != null);
	}

	/**
	 * Always returns a read only copy of the cell constraints
	 */
	public Object clone() {
		return new ReadOnlyConstraints(getColumn(), getRow(), getColumnSpan(), getRowSpan());
	}

	/**
	 * Creates a CellConstraints object that can be used by the FormLayout.
	 * 
	 * @returns a CellConstraints object based on this definition
	 */
	public CellConstraints createCellConstraints() {
		return (CellConstraints) getConstraints().clone();
	}

	/**
	 * Returns the CellConstraints held by the FormLayout.
	 */
	private CellConstraints getConstraints() {
		if (m_parentview != m_gc.getParentView())
			initializeConstraintMap();

		CellConstraints constraints = (CellConstraints) m_constraintmap.get(m_gc);
		if (constraints == null) {
			m_gc.print();
			throw new NullPointerException("Component has not been added to the container.");
		}
		return constraints;
	}

	/**
	 * Return the first column occupied by the component associated with these
	 * constraints.
	 * 
	 * @return the first column
	 */
	public int getColumn() {
		return getConstraints().gridX;
	}

	/**
	 * Return the first row occupied by the component associated with these
	 * constraints.
	 * 
	 * @return the first row
	 */
	public int getRow() {
		return getConstraints().gridY;
	}

	/**
	 * Return the number of columns occupied by the component associated with
	 * these constraints.
	 * 
	 * @return the column span
	 */
	public int getColumnSpan() {
		return getConstraints().gridWidth;
	}

	/**
	 * Return the number of rows occupied by the component associated with these
	 * constraints.
	 * 
	 * @return the column span
	 */
	public int getRowSpan() {
		return getConstraints().gridHeight;
	}

	/**
	 * @return the insets for this component
	 */
	public Insets getInsets() {
		return getConstraints().insets;
	}

	/**
	 * @return the component's horizontal alignment.
	 */
	public CellConstraints.Alignment getHorizontalAlignment() {
		return getConstraints().hAlign;
	}

	/**
	 * @return the component's vertical alignment.
	 */
	public CellConstraints.Alignment getVerticalAlignment() {
		return getConstraints().vAlign;
	}

	/**
	 * Here we need to do some hacking to get the constraintMap reference from
	 * the FormLayout.
	 */
	private void initializeConstraintMap() {
		try {
			m_parentview = m_gc.getParentView();
			FormLayout fl = m_parentview.getFormLayout();
			try {
				m_constraintmap = fl.getConstraintMap();
			} catch (Throwable t) {
				Field cmfield = FormLayout.class.getDeclaredField("constraintMap");
				cmfield.setAccessible(true);
				m_constraintmap = (Map) cmfield.get(fl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Reflection error.");
		}
	}
}
