/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.codegen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.jeta.forms.components.label.JETALabel;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SourceView extends JETAPanel {
	JETALabel m_codegen_label = new JETALabel();
	JButton m_options_btn = new JButton();

	/**
	 * Default constructor
	 */
	public SourceView(Component editor) {
		initializePanel(editor);
	}

	/**
	 * Adds fill components to empty cells in the first row and first column of
	 * the grid. This ensures that the grid spacing will be the same as shown in
	 * the designer.
	 * 
	 * @param cols
	 *            an array of column indices in the first row where fill
	 *            components should be added.
	 * @param rows
	 *            an array of row indices in the first column where fill
	 *            components should be added.
	 */
	void addFillComponents(Container panel, int[] cols, int[] rows) {
		Dimension filler = new Dimension(10, 10);

		boolean filled_cell_11 = false;
		CellConstraints cc = new CellConstraints();
		if (cols.length > 0 && rows.length > 0) {
			if (cols[0] == 1 && rows[0] == 1) {
				/** add a rigid area */
				panel.add(Box.createRigidArea(filler), cc.xy(1, 1));
				filled_cell_11 = true;
			}
		}

		for (int index = 0; index < cols.length; index++) {
			if (cols[index] == 1 && filled_cell_11) {
				continue;
			}
			panel.add(Box.createRigidArea(filler), cc.xy(cols[index], 1));
		}

		for (int index = 0; index < rows.length; index++) {
			if (rows[index] == 1 && filled_cell_11) {
				continue;
			}
			panel.add(Box.createRigidArea(filler), cc.xy(1, rows[index]));
		}

	}

	/**
	 * Helper method to load an image file from the CLASSPATH
	 * 
	 * @param imageName
	 *            the package and name of the file to load relative to the
	 *            CLASSPATH
	 * @return an ImageIcon instance with the specified image file
	 * @throws IllegalArgumentException
	 *             if the image resource cannot be loaded.
	 */
	public ImageIcon loadImage(String imageName) {
		try {
			ClassLoader classloader = getClass().getClassLoader();
			java.net.URL url = classloader.getResource(imageName);
			if (url != null) {
				ImageIcon icon = new ImageIcon(url);
				return icon;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Unable to load image: " + imageName);
	}

	public JPanel createPanel(Component editor) {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		jpanel1.add(createPanel1(), cc.xy(2, 2));

		jpanel1.add(editor, cc.xy(2, 3));

		addFillComponents(jpanel1, new int[] { 1, 2, 3 }, new int[] { 1, 2, 3, 4 });
		return jpanel1;
	}

	public JPanel createPanel1() {
		JPanel jpanel1 = new JPanel();
		jpanel1.setOpaque(false);
		FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE", "CENTER:DEFAULT:NONE,CENTER:2DLU:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		m_codegen_label.setFont(new Font("Dialog", Font.BOLD, 12));
		m_codegen_label.setAntiAliased(true);
		m_codegen_label.setName("codegen.label");
		m_codegen_label.setText("Code Generation");
		jpanel1.add(m_codegen_label, cc.xy(1, 1));

		m_options_btn.setText(I18N.getLocalizedMessage("Options..."));
		m_options_btn.setName("options.btn");
		jpanel1.add(m_options_btn, cc.xy(3, 1));

		addFillComponents(jpanel1, new int[] { 2 }, new int[0]);
		return jpanel1;
	}

	/**
	 * Initializer
	 */
	protected void initializePanel(Component editor) {
		setLayout(new BorderLayout());
		add(createPanel(editor), BorderLayout.CENTER);
	}

}
