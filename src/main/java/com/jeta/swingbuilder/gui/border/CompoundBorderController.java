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

package com.jeta.swingbuilder.gui.border;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.jeta.forms.store.properties.BevelBorderProperty;
import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.DefaultBorderProperty;
import com.jeta.forms.store.properties.EmptyBorderProperty;
import com.jeta.forms.store.properties.EtchedBorderProperty;
import com.jeta.forms.store.properties.LineBorderProperty;
import com.jeta.forms.store.properties.ShadowBorderProperty;
import com.jeta.forms.store.properties.TitledBorderProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;

/**
 * Controller for the CompoundBorderView
 * 
 * @author Jeff Tassin
 */
public class CompoundBorderController extends JETAController {
	private CompoundBorderView m_view;

	/**
	 * ctor
	 */
	public CompoundBorderController(CompoundBorderView view) {
		super(view);
		m_view = view;
		assignAction(CompoundBorderNames.ID_DEFAULT_BORDER_BUTTON, new AddDefaultBorderAction());
		assignAction(CompoundBorderNames.ID_BEVEL_BORDER_BUTTON, new AddBorderAction(BevelBorderView.class));
		assignAction(CompoundBorderNames.ID_TITLED_BORDER_BUTTON, new AddBorderAction(TitledBorderView.class));
		assignAction(CompoundBorderNames.ID_ETCHED_BORDER_BUTTON, new AddBorderAction(EtchedBorderView.class));
		assignAction(CompoundBorderNames.ID_LINE_BORDER_BUTTON, new AddBorderAction(LineBorderView.class));
		assignAction(CompoundBorderNames.ID_EMPTY_BORDER_BUTTON, new AddBorderAction(EmptyBorderView.class));
		assignAction(CompoundBorderNames.ID_SHADOW_BORDER_BUTTON, new AddBorderAction(ShadowBorderView.class));
		assignAction(CompoundBorderNames.ID_EDIT_BORDER_BUTTON, new EditBorderAction());

		assignAction(CompoundBorderNames.ID_DELETE_BORDER_BUTTON, new DeleteBorderAction());
		assignAction(CompoundBorderNames.ID_MOVE_UP_BUTTON, new MoveUpAction());
		assignAction(CompoundBorderNames.ID_MOVE_DOWN_BUTTON, new MoveDownAction());
	}

	/**
	 * Creates a view for editing the given border property
	 */
	private AbstractBorderView createBorderView(BorderProperty bp) {
		if (bp instanceof BevelBorderProperty) {
			return new BevelBorderView();
		}
		else if (bp instanceof EtchedBorderProperty) {
			return new EtchedBorderView();
		}
		else if (bp instanceof LineBorderProperty) {
			return new LineBorderView();
		}
		else if (bp instanceof EmptyBorderProperty) {
			return new EmptyBorderView();
		}
		else if (bp instanceof ShadowBorderProperty) {
			return new ShadowBorderView();
		}
		else if (bp instanceof TitledBorderProperty) {
			return new TitledBorderView();
		}
		else {
			assert (false);
			return null;
		}
	}

	/**
	 * Handler for creating a new BevelBorder
	 */
	public class AddBorderAction implements ActionListener {
		/**
		 * The border view class to create
		 */
		private Class m_border_view_class;

		public AddBorderAction(Class borderViewClass) {
			m_border_view_class = borderViewClass;
		}

		public void actionPerformed(ActionEvent evt) {
			try {
				AbstractBorderView view = (AbstractBorderView) m_border_view_class.newInstance();
				JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_view, true);
				dlg.setPrimaryPanel(view);
				dlg.setSize(dlg.getPreferredSize());
				dlg.setTitle(I18N.getLocalizedMessage("Create Border"));
				dlg.showCenter();
				if (dlg.isOk()) {
					BorderProperty bp = view.createBorderProperty();
					m_view.addBorder(bp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handler for creating a new DefaultBorderProperty
	 */
	public class AddDefaultBorderAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.addBorder(new DefaultBorderProperty());
		}
	}

	/**
	 * Deletes the selected border
	 */
	public class DeleteBorderAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JList list = m_view.getList(CompoundBorderNames.ID_BORDER_LIST);
			int index = list.getSelectedIndex();
			if (index >= 0) {
				DefaultListModel model = (DefaultListModel) list.getModel();
				model.removeElementAt(index);
				index--;
				if (index < 0)
					index = 0;
				if (model.size() > 0)
					list.setSelectedIndex(index);
			}
			m_view.ensureIndexIsVisible();
		}
	}

	/**
	 * Edits the selected border
	 */
	public class EditBorderAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			BorderProperty old_border = m_view.getSelectedBorder();
			if (old_border != null && !(old_border instanceof DefaultBorderProperty)) {
				AbstractBorderView view = createBorderView(old_border);
				view.setBorderProperty(old_border);
				JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_view, true);
				dlg.setPrimaryPanel(view);
				dlg.setSize(dlg.getPreferredSize());
				dlg.setTitle(view.getDescription());
				dlg.showCenter();
				if (dlg.isOk()) {
					BorderProperty new_border = view.createBorderProperty();
					m_view.setBorder(new_border, old_border);
				}
			}
		}
	}

	public class MoveUpAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JList list = m_view.getList(CompoundBorderNames.ID_BORDER_LIST);
			DefaultListModel model = (DefaultListModel) list.getModel();
			int index = list.getSelectedIndex();
			if (index > 0 && model.size() > 1) {
				Object mv_obj = model.getElementAt(index);
				Object next_obj = model.getElementAt(index - 1);
				model.setElementAt(mv_obj, index - 1);
				model.setElementAt(next_obj, index);
				list.setSelectedIndex(index - 1);
			}
			m_view.ensureIndexIsVisible();
		}
	}

	public class MoveDownAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JList list = m_view.getList(CompoundBorderNames.ID_BORDER_LIST);
			DefaultListModel model = (DefaultListModel) list.getModel();
			int index = list.getSelectedIndex();
			if ((index + 1) < model.size()) {
				Object mv_obj = model.getElementAt(index);
				Object next_obj = model.getElementAt(index + 1);
				model.setElementAt(mv_obj, index + 1);
				model.setElementAt(next_obj, index);
				list.setSelectedIndex(index + 1);
			}
			m_view.ensureIndexIsVisible();
		}
	}
}
