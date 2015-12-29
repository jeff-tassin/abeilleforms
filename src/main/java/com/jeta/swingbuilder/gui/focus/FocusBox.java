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

package com.jeta.swingbuilder.gui.focus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This component renders the focus order icons over the Form.
 * 
 * @author Jeff Tassin
 */
public class FocusBox extends JPanel {
	/**
	 * Displays the current focus index when this focusbox does not have 'focus'
	 */
	private FocusGlyph m_focus_glyph;

	/**
	 * A spinner box used to edit the current focus value. Only visible when
	 * this focusbox has 'focus' (i.e. the user clicked on this focusbox with
	 * the mouse
	 */
	private JSpinner m_spinner;

	/**
	 * Flag that indicates if the spinner is active
	 */
	private boolean m_spinner_active = false;

	/**
	 * The current focus index
	 */
	private int m_index;

	/**
	 * The max allowed focus index
	 */
	private int m_max_index;

	/**
	 * The form component associated with this box
	 */
	private Component m_component;

	/**
	 * A list of ActionListeners that want events for this FocusBox
	 */
	private LinkedList m_listeners;

	private Color m_background = Color.blue.darker().darker();
	private Color m_foreground = Color.white;

	/**
	 * Action commands
	 */
	public static final String FOCUS_INDEX_CHANGED = "focus.index.changed";
	public static final String FOCUS_BOX_ACTIVATED = "focus.box.activated";

	/** ctor */
	public FocusBox(int index, int maxIndex, Component comp) {
		m_index = index;
		m_max_index = maxIndex;
		m_component = comp;

		m_focus_glyph = new FocusGlyph(index, m_foreground, m_background);
		setLayout(new BorderLayout());
		add(m_focus_glyph, BorderLayout.CENTER);

		m_focus_glyph.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				remove(m_focus_glyph);
				JSpinner spinner = getSpinner();
				remove(spinner);
				Dimension d = spinner.getPreferredSize();
				// d.width = d.width*3/2;
				setSize(d);
				add(spinner);
				revalidate();
				m_spinner_active = true;
				notifyListeners(new ActionEvent(FocusBox.this, ActionEvent.ACTION_PERFORMED, FOCUS_BOX_ACTIVATED));
			}
		});
	}

	/**
	 * Adds an action listener to this focusbox. A call can get an action event
	 * when the focus index changes or the user has clicked on this focus box.
	 */
	public void addActionListener(ActionListener listener) {
		if (m_listeners == null)
			m_listeners = new LinkedList();

		m_listeners.add(listener);
	}

	/**
	 * Hides the spinner. There is no activate spinner because it is handled
	 * internally in this class.
	 */
	void deactivateSpinner() {
		m_spinner_active = false;
		if (m_spinner != null) {
			remove(m_spinner);
			remove(m_focus_glyph);
			add(m_focus_glyph);
			revalidate();
			setSize(getPreferredSize());
		}
	}

	/**
	 * @return the form component associated with this focus box.
	 */
	public Component getComponent() {
		return m_component;
	}

	public int getFocusIndex() {
		return m_index;
	}

	/**
	 * @return the preferred size for this component
	 */
	public Dimension getPreferredSize() {
		if (m_spinner_active)
			return m_spinner.getPreferredSize();
		else
			return m_focus_glyph.getPreferredSize();
	}

	int getSpinnerValue() {
		SpinnerNumberModel smodel = (SpinnerNumberModel) m_spinner.getModel();
		Integer ival = (Integer) smodel.getValue();
		return ival.intValue();
	}

	public void setFocusIndex(int index) {
		m_index = index;
		if (m_spinner != null) {
			SpinnerNumberModel smodel = (SpinnerNumberModel) m_spinner.getModel();
			Integer ival = (Integer) smodel.getValue();
			if (ival.intValue() != index) {
				smodel.setValue(new Integer(index));
			}
		}
		m_focus_glyph.setIndex(index);
		setSize(getPreferredSize());
		repaint();
	}

	/**
	 * @return the spinner component that is visible when the focus box has
	 *         'focus'.
	 */
	JSpinner getSpinner() {
		if (m_spinner == null) {
			m_spinner = new JSpinner(new SpinnerNumberModel(m_index, 1, m_max_index, 1));
			m_spinner.setBorder(javax.swing.BorderFactory.createLineBorder(m_background));
			JSpinner.NumberEditor seditor = (JSpinner.NumberEditor) m_spinner.getEditor();
			seditor.getTextField().setBackground(m_background);
			seditor.getTextField().setForeground(m_foreground);

			m_spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					notifyListeners(new ActionEvent(FocusBox.this, ActionEvent.ACTION_PERFORMED, FOCUS_INDEX_CHANGED));
				}
			});
		}
		return m_spinner;
	}

	/**
	 * Notifies all registered listeners that an action has occurred.
	 */
	private void notifyListeners(ActionEvent evt) {
		if (m_listeners == null)
			return;

		Iterator iter = m_listeners.iterator();
		while (iter.hasNext()) {
			ActionListener listener = (ActionListener) iter.next();
			listener.actionPerformed(evt);
		}
	}

	/**
	 * Removes a previously registered action listener from this focusbox.
	 */
	public void removeActionListener(ActionListener listener) {
		if (m_listeners == null)
			return;

		m_listeners.remove(listener);
	}

	/**
	 * Renders the current focus index.
	 */
	public static class FocusGlyph extends JComponent {
		private Font m_font;
		private int m_index;
		private String m_index_str;

		private Dimension m_pref_size = new Dimension();
		private Color m_foreground;
		private Color m_background;

		private static int MARGIN = 4;

		/**
		 * ctor
		 */
		public FocusGlyph(int index, Color foreground, Color background) {
			m_index = index;
			m_index_str = String.valueOf(m_index);
			m_font = UIManager.getFont("Table.font");
			m_foreground = foreground;
			m_background = background;
		}

		/**
		 * @return the focus index
		 */
		public int getIndex() {
			return m_index;
		}

		public void setIndex(int index) {
			m_index = index;
			m_index_str = String.valueOf(index);
		}

		/**
		 * @return the preferred size
		 */
		public Dimension getPreferredSize() {
			FontMetrics fm = getFontMetrics(m_font);
			int line_height = fm.getHeight();
			m_pref_size.setSize(fm.stringWidth(m_index_str) + MARGIN * 2, line_height);
			return m_pref_size;
		}

		/**
		 * Paint routine that renders the focus glyphs
		 */
		public void paintComponent(Graphics g) {
			Color old_c = g.getColor();

			FontMetrics fm = g.getFontMetrics(m_font);
			int line_height = fm.getHeight();
			int y = line_height - fm.getDescent();

			g.setColor(m_background);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(m_foreground);
			g.drawString(m_index_str, MARGIN, y);
			g.setColor(old_c);
		}
	}
}
