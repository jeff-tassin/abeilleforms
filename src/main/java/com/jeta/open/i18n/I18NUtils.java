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
package com.jeta.open.i18n;

import java.awt.ComponentOrientation;
import java.awt.Insets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpec.DefaultAlignment;

/**
 * Provides some convenience behavior for flipping sides in column
 * specifications, arrays of column specifications and encoded column specs.
 * 
 * Credit to Karsten Lentzsch of JGoodies for the foundation code for this
 * utility class. For further information see his example entitled <a
 * href="http://www.java2s.com/Code/Java/Swing-Components/Buildpanelscomponentorientationlefttorightvsrighttoleft.htm">
 * "Build panels component orientation: left-to-right vs. right-to-left"</a>
 * 
 * @author Todd Viegut
 * @since Abeille 2.1
 * @version 1.0, 06.24.2007
 */
public class I18NUtils {
	public static FormPanel applyComponentOrientation(FormPanel form, ComponentOrientation orientation) {
		if (!orientation.isLeftToRight()) {
			flip((GridView) form.getFormAccessor());
		}
		return form;
	}

	/**
	 * Convenience method which
	 * 
	 * @param view
	 *            The Abeille <code>GridView</code> instance upon which to
	 *            interrogate and re-orient row and columns specs.
	 */
	private static void flip(GridView view) {
		FormLayout layout = view.getFormLayout();
		//
		int columnCount = layout.getColumnCount();
		int rowCount = layout.getRowCount();
		ColumnSpec[] colSpecs = new ColumnSpec[columnCount];
		RowSpec[] rowSpecs = new RowSpec[rowCount];
		//
		List views = new LinkedList();
		GridComponent[][] components = new GridComponent[columnCount][rowCount];
		for (int column = 1; column <= columnCount; column++) {
			colSpecs[column - 1] = layout.getColumnSpec(column);

			for (int row = 1; row <= rowCount; row++) {
				rowSpecs[row - 1] = layout.getRowSpec(row);

				GridComponent gc = view.getGridComponent(column, row);
				components[column - 1][row - 1] = gc;
				if ((gc != null) && (gc instanceof FormComponent)) {
					views.add((GridView) gc.getBeanDelegate());
				}
			}
		}

		ColumnSpec[] flippedColSpecs = flipped(colSpecs);
		for (int column = 1; column <= columnCount; column++) {
			layout.setColumnSpec(column, flippedColSpecs[column - 1]);
		}

		for (Iterator i = views.iterator(); i.hasNext();) {
			GridView gridView = (GridView) i.next();
			flip(gridView);
		}

		for (int row = 1; row <= rowCount; row++) {
			for (int column = 1; column <= columnCount; column++) {
				GridComponent component = components[column - 1][row - 1];
				if (component != null) {
					try {
						CellConstraints cc = view.getConstraints(component);
						if (cc != null) {
							CellConstraints flippedCC = flipHorizontally(cc, columnCount);
							view.setConstraints(component, flippedCC);
						}
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * Flips the default alignment of the given column specification and returns
	 * a new column specification object with the flipped alignment and the same
	 * size and growing behavior as the original.
	 * 
	 * @param spec
	 *            the original column specification
	 * @return the column specification with flipped default alignment
	 */
	private static ColumnSpec flipped(ColumnSpec spec) {
		DefaultAlignment alignment = spec.getDefaultAlignment();
		if (alignment == ColumnSpec.LEFT)
			alignment = ColumnSpec.RIGHT;
		else if (alignment == ColumnSpec.RIGHT)
			alignment = ColumnSpec.LEFT;
		return new ColumnSpec(alignment, spec.getSize(), spec.getResizeWeight());
	}

	/**
	 * Returns an array of column specifications that is built from the given
	 * array by flipping each column spec and reversing their order.
	 * 
	 * @param original
	 *            the original array of column specifications
	 * @return an array of flipped column specs in reversed order
	 */
	private static ColumnSpec[] flipped(ColumnSpec[] original) {
		int length = original.length;
		ColumnSpec[] flipped = new ColumnSpec[length];
		for (int i = 0; i < length; i++) {
			flipped[i] = flipped(original[length - 1 - i]);
		}
		return flipped;
	}

	/**
	 * Returns an array of column specifications that is built from the given
	 * encoded column specifications by flipping each column spec and reversing
	 * their order.
	 * 
	 * @param encodedColumnSpecs
	 *            the original comma-separated encoded column specifications
	 * @return an array of flipped column specs in reversed order
	 */
	private static ColumnSpec[] flipped(String encodedColumnSpecs) {
		return flipped(ColumnSpec.decodeSpecs(encodedColumnSpecs));
	}

	/**
	 * Creates and returns a horizontally flipped clone of the given cell
	 * constraints object. Flips the horizontal alignment and the left and right
	 * insets.
	 * 
	 * @param cc
	 *            the original cell constraints object
	 * @return the flipped cell constraints with flipped horizontal alignment,
	 *         and flipped left and right insets - if any
	 */
	private static CellConstraints flipHorizontally(CellConstraints cc) {
		CellConstraints.Alignment flippedHAlign = cc.hAlign;
		if (flippedHAlign == CellConstraints.LEFT)
			flippedHAlign = CellConstraints.RIGHT;
		else if (flippedHAlign == CellConstraints.RIGHT)
			flippedHAlign = CellConstraints.LEFT;

		CellConstraints flipped = new CellConstraints(cc.gridX, cc.gridY, cc.gridWidth, cc.gridHeight, flippedHAlign, cc.vAlign);
		if (cc.insets != null) {
			flipped.insets = new Insets(cc.insets.top, cc.insets.right, cc.insets.bottom, cc.insets.left);
		}
		return flipped;
	}

	/**
	 * Creates and returns a horizontally flipped clone of the given cell
	 * constraints object with the grid position adjusted to the given column
	 * count. Flips the horizontal alignment and the left and right insets. And
	 * swaps the left and right cell positions according to the specified column
	 * count.
	 * 
	 * @param cc
	 *            the original cell constraints object
	 * @param columnCount
	 *            the number of columns; used to swap the left and right cell
	 *            bounds
	 * @return the flipped cell constraints with flipped horizontal alignment,
	 *         and flipped left and right insets - if any
	 */
	private static CellConstraints flipHorizontally(CellConstraints cc, int columnCount) {
		CellConstraints flipped = flipHorizontally(cc);
		flipped.gridX = columnCount + 1 - cc.gridX - (flipped.gridWidth - 1);
		return flipped;
	}
}
