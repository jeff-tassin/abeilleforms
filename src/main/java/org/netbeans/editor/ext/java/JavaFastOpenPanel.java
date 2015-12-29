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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.netbeans.editor.LocaleSupport;

/**
 * The panel for opening java classes.
 * 
 * @author Miloslav Metelka
 * @version 1.0
 */
class JavaFastOpenPanel extends javax.swing.JPanel {

	private JavaFastOpen jfo;

	/** Creates new form SaveMacroPanel */
	public JavaFastOpenPanel(JavaFastOpen jfo) {
		this.jfo = jfo;

		initComponents();

		getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFO")); // NOI18N
		expField.getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFOP_expLabel")); // NOI18N
		javax.swing.JList jl = jfo.getResultList();
		listScrollPane.setViewportView(jl);
		foundLabel.setLabelFor(jl);
		jl.getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFOP_foundLabel")); // NOI18N

		setPreferredSize(new Dimension(340, 178));
		setMaximumSize(new Dimension(400, 200));

		expLabel.setDisplayedMnemonic(LocaleSupport.getString("JFOP_expLabelMnemonic", "C").charAt(0)); // NOI18N
		expLabel.setLabelFor(expField);
		expField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent evt) {
				if (!checkArrows(evt))
					change();
			}

			public void keyReleased(KeyEvent evt) {
				if (!isArrows(evt))
					change();
			}

			public void keyTyped(KeyEvent evt) {
				if (!isArrows(evt))
					change();
			}

			private boolean isArrows(KeyEvent evt) {
				int key = evt.getKeyCode();
				return key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP;
			}

			private boolean checkArrows(KeyEvent evt) {
				int key = evt.getKeyCode();
				switch (key) {
				case KeyEvent.VK_DOWN:
					JavaFastOpenPanel.this.jfo.scrollDown();
					return true;
				case KeyEvent.VK_UP:
					JavaFastOpenPanel.this.jfo.scrollUp();
					return true;
				default:
				}
				return false;
			}

			private void change() {
				JavaFastOpenPanel.this.jfo.postUpdate();
			}
		});
		foundLabel.setDisplayedMnemonic(LocaleSupport.getString("JFOP_foundLabelMnemonic", "F").charAt(0)); // NOI18N
		foundLabel.setLabelFor(jfo.getResultList());
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
		expField.requestFocus();
	}

	String getSearchText() {
		return expField.getText();
	}

	void setSearchText(String text) {
		expField.setText(text);
		expField.setCaretPosition(expField.getText().length());
	}

	private void initComponents() {// GEN-BEGIN:initComponents
		java.awt.GridBagConstraints gridBagConstraints;

		queryPanel = new javax.swing.JPanel();
		expLabel = new javax.swing.JLabel();
		expField = new javax.swing.JTextField();
		listPanel = new javax.swing.JPanel();
		foundLabel = new javax.swing.JLabel();
		listScrollPane = new javax.swing.JScrollPane();

		setLayout(new java.awt.BorderLayout());

		setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 0, 11)));
		queryPanel.setLayout(new java.awt.GridBagLayout());

		expLabel.setText(LocaleSupport.getString("JFOP_expLabel", "Class Name:"));
		expLabel.setLabelFor(expField);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
		queryPanel.add(expLabel, gridBagConstraints);

		expField.setPreferredSize(new java.awt.Dimension(100, 21));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
		queryPanel.add(expField, gridBagConstraints);

		add(queryPanel, java.awt.BorderLayout.NORTH);

		listPanel.setLayout(new java.awt.GridBagLayout());

		listPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 0, 0, 0)));
		foundLabel.setText(LocaleSupport.getString("JFOP_foundLabel", "Matching Classes:"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
		listPanel.add(foundLabel, gridBagConstraints);

		listScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		listPanel.add(listScrollPane, gridBagConstraints);

		add(listPanel, java.awt.BorderLayout.CENTER);

	}// GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel listPanel;
	private javax.swing.JLabel expLabel;
	private javax.swing.JLabel foundLabel;
	private javax.swing.JTextField expField;
	private javax.swing.JScrollPane listScrollPane;
	private javax.swing.JPanel queryPanel;
	// End of variables declaration//GEN-END:variables

}
