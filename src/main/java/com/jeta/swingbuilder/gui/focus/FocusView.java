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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.jeta.forms.gui.focus.FormFocusManager;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.forms.store.memento.FocusPolicyMemento;
import com.jeta.open.gui.framework.JETAPanel;

/**
 * This component renders the focus order icons over the Form.
 * 
 * @author Jeff Tassin
 */
public class FocusView extends JETAPanel implements GridViewListener {
	/**
	 * The view associated with this overlay
	 */
	private FormComponent m_form;

	/**
	 * A common listener for all focus boxes
	 */
	private FocusBoxListener m_focus_box_listener = new FocusBoxListener();

	private Runnable m_swing_updater = new Runnable() {
		public void run() {
			showFocusBoxes();
		}
	};

	/** ctor */
	public FocusView(FormComponent form) {
		m_form = form;
		setOpaque(false);
		setLayout(null);
		showFocusBoxes();

	}

	/**
	 * Shows the focus boxes on the view
	 */
	public void showFocusBoxes() {
		removeAll();
		FormFocusManager focus_mgr = new FormFocusManager(m_form);
		int comp_count = focus_mgr.getComponentCount();
		for (int index = 0; index < comp_count; index++) {
			Component comp = focus_mgr.getComponent(index);
			if (comp != null) {
				Point pt = SwingUtilities.convertPoint(comp, 0, 0, m_form);
				FocusBox fbox = new FocusBox(index + 1, comp_count, comp);
				fbox.addActionListener(m_focus_box_listener);
				fbox.setLocation(pt.x, pt.y);
				fbox.setSize(fbox.getPreferredSize());
				add(fbox);
			}
		}
		revalidate();
		repaint();
	}

	/**
	 * This method makes sure an ArrayList has at least sz elements. This
	 * guarantees that a call to list.size() will be valid for sz elements.
	 * 
	 * @param list
	 *            the list to add elements to
	 * @param sz
	 *            the size to ensure
	 */
	public static void ensureSize(ArrayList list, int sz) {
		if (list == null) {
			assert (false);
			return;
		}

		if (sz <= list.size())
			return;

		int old_size = list.size();
		for (int index = 0; index < (sz - old_size); index++) {
			list.add(null);
		}
	}

	/**
	 * @return the focus policy memento that represents the current focus
	 *         ordering for this manager
	 */
	public FocusPolicyMemento getFocusPolicyMemento() {
		ArrayList focus_order = new ArrayList();

		for (int index = 0; index < getComponentCount(); index++) {
			FocusBox fbox = (FocusBox) getComponent(index);
			int focus_index = fbox.getFocusIndex() - 1;
			ensureSize(focus_order, focus_index + 1);
			Component comp = (Component) focus_order.get(focus_index);
			if (comp == null) {
				focus_order.set(focus_index, fbox.getComponent());
			}
			else {
				assert (false);
				focus_order.add(focus_index, fbox.getComponent());
			}
		}

		FormFocusManager fmgr = new FormFocusManager(m_form, focus_order);
		return fmgr.getFocusPolicyMemento();
	}

	/** GridViewListener implementation */
	public void gridChanged(GridViewEvent evt) {
		if (evt.getId() != GridViewEvent.EDIT_COMPONENT && evt.getId() != GridViewEvent.CELL_SELECTED) {
			SwingUtilities.invokeLater(m_swing_updater);
		}
	}

	public class FocusBoxListener implements ActionListener {
		private boolean m_silent = false;

		public void actionPerformed(ActionEvent evt) {
			if (isSilent())
				return;

			FocusBox fbox = (FocusBox) evt.getSource();
			if (FocusBox.FOCUS_BOX_ACTIVATED.equals(evt.getActionCommand())) {
				for (int index = 0; index < getComponentCount(); index++) {
					Component child = getComponent(index);
					if (child != fbox) {
						((FocusBox) child).deactivateSpinner();
					}
				}
			}
			else if (FocusBox.FOCUS_INDEX_CHANGED.equals(evt.getActionCommand())) {
				try {
					setSilent(true);
					int new_idx = fbox.getSpinnerValue();
					int old_idx = fbox.getFocusIndex();
					for (int index = 0; index < getComponentCount(); index++) {
						FocusBox child = (FocusBox) getComponent(index);
						if (child.getFocusIndex() == new_idx) {
							assert (child != fbox);
							child.setFocusIndex(old_idx);
							break;
						}
					}
					fbox.setFocusIndex(new_idx);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					setSilent(false);
				}
			}
		}

		boolean isSilent() {
			return m_silent;
		}

		void setSilent(boolean silent) {
			m_silent = silent;
		}
	}

}
