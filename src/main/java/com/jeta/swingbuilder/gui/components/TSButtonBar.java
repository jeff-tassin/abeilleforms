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

package com.jeta.swingbuilder.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.jeta.open.gui.framework.JETAPanel;

/**
 * This class implements a simple button bar
 * 
 * @author Jeff Tassin
 */
public class TSButtonBar extends JETAPanel {

	private ButtonGroup m_button_group = new ButtonGroup();

	/** the current view */
	private ButtonBarView m_currentview;

	/** the list of views available on the button bar */
	private LinkedList m_views = new LinkedList();

	/**
	 * The selected background color for the button tab.
	 */
	private Color m_selected_color;

	private Border m_selected_border;
	private Border m_unselected_border;

	/**
	 * A list of action listeners that get invoked when the user clicks on a
	 * button on the button bar.
	 */
	private LinkedList m_listeners = new LinkedList();

	private float FACTOR = 0.8f;

	/**
	 * ctor
	 */
	public TSButtonBar() {
		setLayout(new BorderLayout());
		updateSettings();
	}

	/**
	 * Adds a listener that get invoked when the user clicks on a button on the
	 * button bar.
	 */
	public void addListener(ActionListener listener) {
		m_listeners.add(listener);
	}

	/**
	 * Adds a view to the bar.
	 * 
	 * @param caption
	 *            the text that appears in the button that will activate this
	 *            view
	 * @param view
	 *            the view that will be displayed for this button.
	 */
	public void addView(String caption, JComponent view) {
		addView(caption, view, null);
	}

	/**
	 * Adds a view to the bar.
	 * 
	 * @param caption
	 *            the text that appears in the button that will activate this
	 *            view
	 * @param view
	 *            the view that will be displayed for this button.
	 */
	public void addView(String caption, JComponent view, ImageIcon icon) {
		JLabel btn = new JLabel(caption);
		btn.setBorder(m_unselected_border);

		btn.setOpaque(true);
		btn.setBackground(UIManager.getColor("control"));
		if (icon != null)
			btn.setIcon(icon);

		btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		btn.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				JComponent btn = (JComponent) evt.getSource();
				if (btn != m_currentview.getButton()) {
					Iterator iter = m_views.iterator();
					while (iter.hasNext()) {
						ButtonBarView view = (ButtonBarView) iter.next();
						if (btn == view.getButton()) {
							m_currentview = view;

							/** notify any listeners that a button was pressed. */
							updateListeners();

							updateView();
							btn.setBackground(m_selected_color);
							btn.setBorder(m_selected_border);
						}
						else {
							view.getButton().setBackground(UIManager.getColor("control"));
							view.getButton().setBorder(m_unselected_border);
						}
					}
				}
			}
		});

		btn.setFont(javax.swing.UIManager.getFont("Table.font"));
		ButtonBarView bbview = new ButtonBarView(btn, view);

		m_views.add(bbview);

		if (m_currentview == null)
			m_currentview = bbview;

	}

	public JComponent getCurrentView() {
		if (m_currentview != null)
			return m_currentview.getView();

		return null;
	}

	/**
	 * @return the views in the button bar
	 */
	public Collection getViews() {
		LinkedList list = new LinkedList();
		Iterator iter = m_views.iterator();
		while (iter.hasNext()) {
			ButtonBarView bbview = (ButtonBarView) iter.next();
			list.add(bbview.getView());
		}
		return list;
	}

	/**
	 * Removes a listener from the list of listeners that gets invoked when the
	 * user clicks on a button on the button bar.
	 */
	public void removeListener(ActionListener listener) {
		m_listeners.remove(listener);
	}

	public void setCurrentView(Component view) {
		if (view == m_currentview.getView())
			return;

		Iterator iter = m_views.iterator();
		while (iter.hasNext()) {
			ButtonBarView bbview = (ButtonBarView) iter.next();
			if (view == bbview.getView()) {
				m_currentview = bbview;
				updateView();
				return;
			}
		}
	}

	/**
	 * Updates all listeners that the view has changed.
	 */
	private void updateListeners() {
		ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "view.changed");
		try {
			Iterator iter = m_listeners.iterator();
			while (iter.hasNext()) {
				ActionListener listener = (ActionListener) iter.next();
				listener.actionPerformed(evt);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Updates the button bar
	 */
	public void updateView() {
		removeAll();

		if (m_currentview != null) {
			add(m_currentview.getView(), BorderLayout.CENTER);

			m_currentview.getButton().setBackground(m_selected_color);
			m_currentview.getButton().setBorder(m_selected_border);

			if (m_views.size() > 1) {
				JPanel panel = new JPanel(new GridLayout(m_views.size(), 1));
				Iterator iter = m_views.iterator();
				while (iter.hasNext()) {
					ButtonBarView view = (ButtonBarView) iter.next();
					panel.add(view.getButton());
					if (view != m_currentview) {
						view.getButton().setBackground(UIManager.getColor("control"));
						view.getButton().setBorder(m_unselected_border);
					}
				}
				add(panel, BorderLayout.NORTH);
			}
		}

		revalidate();
		repaint();
	}

	private void updateSettings() {
		Color c = UIManager.getColor("control");
		m_selected_color = new Color(Math.max((int) (c.getRed() * FACTOR), 0), Math.max((int) (c.getGreen() * FACTOR), 0), Math.max(
				(int) (c.getBlue() * FACTOR), 0));

		m_unselected_border = BorderFactory.createCompoundBorder(UIManager.getBorder("TableHeader.cellBorder"), BorderFactory.createEmptyBorder(2, 2, 2, 2));

		m_selected_border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), BorderFactory
				.createEmptyBorder(2, 2, 2, 2));
	}

	/**
	 * Override so we can update the components thar are not 'current'
	 */
	public void updateUI() {
		super.updateUI();

		if (m_views != null) {
			updateSettings();

			Iterator iter = m_views.iterator();
			while (iter.hasNext()) {
				ButtonBarView view = (ButtonBarView) iter.next();
				if (view != m_currentview) {
					javax.swing.SwingUtilities.updateComponentTreeUI(view.getView());
					javax.swing.SwingUtilities.updateComponentTreeUI(view.getButton());
					view.getButton().setBackground(UIManager.getColor("control"));
					view.getButton().setBorder(m_unselected_border);
				}
				else {
					view.getButton().setBackground(m_selected_color);
					view.getButton().setBorder(m_selected_border);
				}
			}
		}
	}

	/**
	 * Updates the button bar
	 */
	public void updateView2() {
		removeAll();

		if (m_currentview != null) {
			add(m_currentview.getButton(), BorderLayout.NORTH);
			add(m_currentview.getView(), BorderLayout.CENTER);
			if (m_views.size() > 1) {
				JPanel panel = new JPanel(new GridLayout(m_views.size() - 1, 1));
				Iterator iter = m_views.iterator();
				while (iter.hasNext()) {
					ButtonBarView view = (ButtonBarView) iter.next();
					if (view != m_currentview) {
						panel.add(view.getButton());
					}
				}
				add(panel, BorderLayout.SOUTH);
			}
		}

		revalidate();
		repaint();
	}

	static class ButtonBarView {
		/** the button that will display the view */
		private JComponent m_btn;
		/** the view associated with this button */
		private JComponent m_view;

		/**
		 * ctor
		 */
		public ButtonBarView(JComponent btn, JComponent view) {
			m_btn = btn;
			m_view = view;
		}

		/**
		 * @return the button associated with this view
		 */
		public JComponent getButton() {
			return m_btn;
		}

		/**
		 * @return the view that is displayed in the ButtonBar when the view is
		 *         selected
		 */
		public JComponent getView() {
			return m_view;
		}
	}

}
