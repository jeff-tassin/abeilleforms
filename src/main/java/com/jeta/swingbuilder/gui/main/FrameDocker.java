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

package com.jeta.swingbuilder.gui.main;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.jeta.forms.components.image.ImageComponent;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.common.ComponentNames;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.app.ObjectStore;
import com.jeta.swingbuilder.resources.Icons;
import com.jeta.swingbuilder.store.FrameState;

/**
 * This class is responsible for handling the docking behavior of the properties
 * window.
 * 
 * @author Jeff Tassin
 */
class FrameDocker {
	/**
	 * The main container in the application frame that contains the tool
	 * palette, form editor, and optionally the properties window
	 */
	private JPanel m_main_panel;

	/**
	 * The main split pane in the application frame.
	 */
	private JSplitPane m_split;

	/**
	 * The properties frame window. Null if docked.
	 */
	private JFrame m_properties_frame;

	/**
	 * The properties view
	 */
	private FormPanel m_properties_view;

	/**
	 * Panel that contains the form editor and component toolbar
	 */
	private JPanel m_editor_panel;

	/**
	 * The last know split pane divider location
	 */
	private int m_divider_location = 0;

	public static final String ID_PROPERTIES_FRAME_STATE = "property.frame.bounds";

	/**
	 * ctor
	 */
	FrameDocker(JPanel main_panel, JPanel editor_panel, JSplitPane split, FormPanel properties_view) {
		m_main_panel = main_panel;
		m_editor_panel = editor_panel;
		m_split = split;
		m_properties_view = properties_view;
	}

	/**
	 * Docks or undocks properties window window
	 */
	void dockPropertiesFrame(Rectangle bounds) {
		if (m_properties_frame == null) {
			Point frame_org = new Point(0, 0);
			SwingUtilities.convertPointToScreen(frame_org, m_properties_view);

			togglePropertiesFrame();

			m_properties_frame = new JFrame();
			m_properties_frame.setTitle(I18N.getLocalizedMessage("Form Properties"));
			javax.swing.ImageIcon icon = FormDesignerUtils.loadImage(Icons.LINKED_FORM_16);
			if (icon != null)
				m_properties_frame.setIconImage(icon.getImage());

			ImageComponent btn = (ImageComponent) m_properties_view.getComponentByName(FormPropertiesNames.ID_DOCK_FRAME);
			btn.setIcon(FormDesignerUtils.loadImage(Icons.SPLIT_WINDOWS_16));

			m_properties_frame.getContentPane().add(m_properties_view);

			if (bounds == null)
				setPropertyPaneBounds(frame_org.x - 10, frame_org.y - 10, m_properties_view.getWidth() + 20, m_properties_view.getHeight() + 20);
			else
				setPropertyPaneBounds(bounds.x, bounds.y, bounds.width, bounds.height);

			m_properties_frame.setVisible(true);
		}
		else {
			ImageComponent btn = (ImageComponent) m_properties_view.getComponentByName(FormPropertiesNames.ID_DOCK_FRAME);
			btn.setIcon(FormDesignerUtils.loadImage(Icons.WINDOWS_16));

			m_properties_frame.getContentPane().remove(m_properties_view);
			m_properties_frame.dispose();
			m_properties_frame = null;
			m_main_panel.remove(m_editor_panel);
			m_split.add(m_editor_panel);
			m_split.add(m_properties_view);

			if (m_divider_location > 0)
				m_split.setDividerLocation(m_divider_location);
			else
				m_split.setDividerLocation(0.7);

			m_main_panel.add(m_split);

			m_main_panel.revalidate();
			m_main_panel.repaint();
		}
	}

	/**
	 * Retrieves the last known frame bounds of the properties frame from the
	 * property store and sets the frame size.
	 */
	void initializeFrameBounds() {
		try {
			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
			FrameState fstate = (FrameState) os.load(ID_PROPERTIES_FRAME_STATE);
			if (fstate != null) {
				if (!fstate.isDocked()) {
					dockPropertiesFrame(fstate.getBounds());
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	private void setPropertyPaneBounds(int x, int y, int width, int height) {
		Dimension screensz = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		width = Math.min(width, screensz.width * 8 / 10);
		height = Math.min(height, screensz.height * 8 / 10);

		width = Math.max(64, width);
		height = Math.max(64, height);

		x = Math.min(screensz.width - width - 50, x);
		y = Math.min(screensz.height - height - 50, y);

		x = Math.max(20, x);
		y = Math.max(20, y);

		m_properties_frame.setBounds(x, y, width, height);
	}

	/**
	 * Saves the frame state to the object store and
	 */
	void shutDown() {
		try {
			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);

			FrameState fstate;
			if (m_properties_frame == null)
				fstate = new FrameState();
			else
				fstate = new FrameState(m_properties_frame.getBounds());

			os.store(ID_PROPERTIES_FRAME_STATE, fstate);
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Hides or displays properties window window
	 */
	void togglePropertiesFrame() {
		if (m_properties_frame == null) {
			if (m_properties_view.getParent() == m_split) {
				m_divider_location = m_split.getDividerLocation();
				m_split.remove(m_properties_view);
				m_split.remove(m_editor_panel);
				m_main_panel.removeAll();
				m_main_panel.add(m_editor_panel);
			}
			else {
				m_main_panel.removeAll();
				m_split.add(m_editor_panel);
				m_split.add(m_properties_view);
				m_main_panel.add(m_split);

				if (m_divider_location > 0)
					m_split.setDividerLocation(m_divider_location);
				else
					m_split.setDividerLocation(0.7);
			}

			m_main_panel.revalidate();
			m_main_panel.repaint();
		}
		else {
			if (m_properties_frame.isActive() && m_properties_frame.isVisible()) {
				m_properties_frame.setVisible(false);
			}
			else {
				m_properties_frame.setVisible(true);
				if (m_properties_frame.getExtendedState() == java.awt.Frame.ICONIFIED)
					m_properties_frame.setExtendedState(java.awt.Frame.NORMAL);
				m_properties_frame.toFront();
			}
		}
	}

	/**
	 * Updates all child components when the look and feel has changed
	 */
	public void updateUI() {
		if (m_properties_frame != null)
			FormUtils.updateLookAndFeel(m_properties_frame);
	}

}
