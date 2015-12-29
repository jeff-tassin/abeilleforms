/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.editor.ext.java;

import java.awt.Dimension;

import org.netbeans.editor.LocaleSupport;

/**
 * The panel for opening java classes.
 * 
 * @author Miloslav Metelka
 * @version 1.0
 */
class JavaFastImportPanel extends javax.swing.JPanel {

	private JavaFastImport jfi;

	/** Creates new form SaveMacroPanel */
	public JavaFastImportPanel(JavaFastImport jfi) {
		this.jfi = jfi;

		initComponents();

		getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFI")); // NOI18N
		listLabel.setDisplayedMnemonic(LocaleSupport.getString("JFI_listLabelMnemonic", "M").charAt(0)); // NOI18N
		javax.swing.JList jl = jfi.getResultList();
		listScrollPane.setViewportView(jl);
		listLabel.setLabelFor(jl);
		jl.getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFI_listLabel")); // NOI18N

		setMaximumSize(new Dimension(400, 200));

	}

	public Dimension getPreferredSize() {
		Dimension pref = super.getPreferredSize();
		Dimension max = getMaximumSize();
		if (pref.width > max.width)
			pref.width = max.width;
		if (pref.height > max.height)
			pref.height = max.height;
		return pref;
	}

	void popupNotify() {
		listScrollPane.requestFocus();
	}

	private void initComponents() {// GEN-BEGIN:initComponents
		java.awt.GridBagConstraints gridBagConstraints;

		listLabel = new javax.swing.JLabel();
		listPanel = new javax.swing.JPanel();
		listScrollPane = new javax.swing.JScrollPane();

		setLayout(new java.awt.BorderLayout(0, 2));

		setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
		listLabel.setText(LocaleSupport.getString("JFI_listLabel", "Matching Classes:"));
		add(listLabel, java.awt.BorderLayout.NORTH);

		listPanel.setLayout(new java.awt.GridBagLayout());

		listScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		listPanel.add(listScrollPane, gridBagConstraints);

		add(listPanel, java.awt.BorderLayout.CENTER);

	}// GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel listPanel;
	private javax.swing.JLabel listLabel;
	private javax.swing.JScrollPane listScrollPane;
	// End of variables declaration//GEN-END:variables

}
