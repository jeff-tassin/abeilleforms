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

package com.jeta.forms.components.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;

/**
 * This class is used to display a small rectangular button on a panel. The
 * button is used to select a color for those dialogs that support color
 * selection/preferences.
 * 
 * @author Jeff Tassin
 */
public class JETAColorWell extends JComponent {
	/**
	 * The current color
	 */
	private Color m_color;

	/**
	 * A list of ActionListener objects that get notified when the color
	 * changes.
	 */
	private LinkedList m_listeners;

	/**
	 * ctor
	 */
	public JETAColorWell() {
		initialize(null);
	}

	/**
	 * ctor
	 */
	public JETAColorWell(Color color) {
		initialize(color);
	}

	/**
	 * Adds an listener that wants to be notified when the color is changed.
	 */
	public void addActionListener(ActionListener listener) {
		if (m_listeners == null)
			m_listeners = new LinkedList();
		if (!m_listeners.contains(listener))
			m_listeners.add(listener);
	}

	/**
	 * Returns the currently selected color
	 * 
	 * @return the color for this ink well
	 */
	public Color getColor() {
		return m_color;
	}

	/**
	 * Initializes the color well
	 */
	protected void initialize(Color color) {
		if (color == null)
			m_color = Color.black;
		else
			m_color = color;

		setBorder(javax.swing.BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		Dimension d = new Dimension(16, 16);
		setSize(d);
		setPreferredSize(d);
		addMouseListener(new ColorWellListener());
	}

	/**
	 * Notifies any listeners that the color has changed.
	 */
	protected void notifyListeners(ActionEvent evt) {
		if (m_listeners != null) {
			Iterator iter = m_listeners.iterator();
			while (iter.hasNext()) {
				ActionListener listener = (ActionListener) iter.next();
				listener.actionPerformed(evt);
			}
		}
	}

	/**
	 * Paints the component
	 */
	public void paintComponent(Graphics g) {
		java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
		g2.setPaint(getColor());
		Rectangle rect = g.getClipBounds();
		g2.fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Removes a listener that was previous added with addListener
	 */
	public void removeActionListener(ActionListener listener) {
		if (m_listeners != null)
			m_listeners.remove(listener);
	}

	/**
	 * Sets the color for this ink well
	 * 
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		m_color = color;
		repaint();
	}

	/**
	 * Handler for changing color
	 */
	public class ColorWellListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			JETAColorWell comp = (JETAColorWell) evt.getSource();

			Color result = null;
			ColorChooserFactory factory = (ColorChooserFactory) JETARegistry.lookup(ColorChooserFactory.COMPONENT_ID);
			if (factory != null) {
				result = factory.showColorChooser(comp, I18N.getLocalizedMessage("Color Chooser"), comp.getColor());
			}
			else {
				result = JColorChooser.showDialog(SwingUtilities.getWindowAncestor(comp), I18N.getLocalizedMessage("Color Chooser"), comp.getColor());
			}

			if (result != null) {
				comp.setColor(result);
				comp.notifyListeners(new ActionEvent(JETAColorWell.this, ActionEvent.ACTION_PERFORMED, comp.getName()));
			}
		}
	}

}
