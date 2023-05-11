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

package com.jeta.swingbuilder.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.ContainedFormFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridOverlay;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.commands.FormUndoableEdit;
import com.jeta.swingbuilder.gui.components.LinkedFormComponentFactory;
import com.jeta.swingbuilder.gui.components.TSCell;
import com.jeta.swingbuilder.gui.focus.FocusView;
import com.jeta.swingbuilder.gui.formmgr.AbstractFormManager;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;
import com.jeta.swingbuilder.gui.handler.GridMouseListener;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.gui.undo.EditorUndoManager;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Displays a form and an editor grid.
 * 
 * @author Jeff Tassin
 */
public class FormEditor extends JETAPanel implements GridViewListener {
	/**
	 * The margin at the top of the view for selecting/editing/resizing column
	 * settings
	 */
	private Margin m_colmargin;

	/**
	 * The margin at the left of the view for selecting/editing/resizing row
	 * settings
	 */
	private Margin m_rowmargin;
	private static int COLUMN_MARGIN_HEIGHT = 24;
	private static int ROW_MARGIN_WIDTH = 24;

	/** @directed */
	private DesignFormComponent m_form;

	/**
	 * The 1x1 topmost parent the contains the form we are editing.
	 */
	private FormComponent m_topparent;

	/** @directed */
	private GridView m_gridview;

	/**
	 * Displays the focus order for the form in this editor
	 */
	private FocusView m_focus_view;

	/**
	 * The object that determines the currently selected component to create
	 * when adding a component to the form.
	 */
	private ComponentSource m_compsrc;

	/**
	 * The scroll pane that contains the form.
	 */
	private JScrollPane m_scroll;

	/**
	 * Flag that indicates if this editor is currently active.
	 */
	private boolean m_active = true;

	/**
	 * The row/column cells on the status bar. Displays the row/column specs for
	 * the selected component
	 */
	private TSCell m_formtype_cell;
	private TSCell m_colcell;
	private TSCell m_rowcell;

	/**
	 * The row/column specs cells. Display the row and column specs for the
	 * selected form.
	 */
	private TSCell m_colspec_cell;
	private TSCell m_rowspec_cell;

	/**
	 * For debugging only Displays the form id for the current parent and form
	 * components.
	 */
	private TSCell m_formcell;
	private TSCell m_parentcell;

	/**
	 * Undo/Redo support for this editor
	 */
	private EditorUndoManager m_undomanager;

	/**
	 * A list of GridViewListeners who are interested in GridViewEvents for the
	 * forms in this editor. We keep track of this list in case the form changes
	 * such as during a Save As operation. In this situation, we can re-register
	 * the listeners on the new form.
	 */
	private LinkedList m_grid_listeners = new LinkedList();

	/** icons for status bar */
	private static ImageIcon m_linked_icon = FormDesignerUtils.loadImage("forms/form_control_linked.gif");
	private static ImageIcon m_embedded_icon = FormDesignerUtils.loadImage("forms/form_control_embedded.gif");

	/**
	 * ctor
	 * 
	 */
	public FormEditor(ComponentSource compsrc, FormComponent comp) {
		try {
			assert (compsrc != null);
			assert (comp != null);

			m_compsrc = compsrc;
			initialize(createTopParent(comp));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ctor
	 * 
	 * @param gmodel
	 */
	public FormEditor(ComponentSource compsrc, int cols, int rows) {
		try {
			m_compsrc = compsrc;

			LinkedFormComponentFactory factory = new LinkedFormComponentFactory(m_compsrc);
			m_form = (DesignFormComponent) factory.create(m_compsrc, "", null, cols, rows, false);

			m_gridview = m_form.getChildView();
			GridView.fillCells(m_gridview, m_compsrc);
			initialize(createTopParent(m_form));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets this editor as the currently active editor
	 */
	public void activate() {
		m_active = true;
		getForm().setGridViewVisible(true);
		getForm().setControlButtonsVisible(false);
		getForm().getChildView().refreshView();
		if (m_focus_view != null) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					m_focus_view.showFocusBoxes();
					getForm().getChildView().repaint();
				}
			});
		}
		unitTest();
	}

	/**
	 * Adds GridListeners to the form in this editor
	 */
	public void addListener(GridViewListener listener) {
		m_topparent.getChildView().addListener(listener);
		m_grid_listeners.add(listener);
	}

	/**
	 * Clears all undo/redo edits for this editor. This is typically performed
	 * after the editor has been saved.
	 */
	public void clearUndoableEdits() {
		m_undomanager.discardAllEdits();
	}

	/**
	 * Creates the status bar for this editor
	 */
	private JETAPanel createStatusBar() {
		if (FormDesignerUtils.isDebug()) {
			JETAPanel panel = new JETAPanel(new FormLayout("pref:grow", "pref,pref"));
			CellConstraints cc = new CellConstraints();

			panel.add(createStandardStatusBar(), cc.xy(1, 1));
			panel.add(createDebugStatusBar(), cc.xy(1, 2));
			return panel;
		}
		else {
			return createStandardStatusBar();
		}
	}

	/**
	 * Creates a debug bar for this editor. Shows extra information such as form
	 * id.
	 */
	private JETAPanel createDebugStatusBar() {
		JETAPanel panel = new JETAPanel();
		FormLayout layout = new FormLayout("pref:grow", "pref,pref");
		panel.setLayout(layout);

		m_formcell = new TSCell("formcell", "left:pref:nogrow");
		m_formcell.setFont(javax.swing.UIManager.getFont("Table.font"));
		m_formcell.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		m_parentcell = new TSCell("parentcell", "left:pref:nogrow");
		m_parentcell.setFont(javax.swing.UIManager.getFont("Table.font"));
		m_parentcell.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		CellConstraints cc = new CellConstraints();
		panel.add(m_formcell, cc.xy(1, 1));
		panel.add(m_parentcell, cc.xy(1, 2));
		return panel;
	}

	/**
	 * Creates the status bar for this editor
	 */
	private JETAPanel createStandardStatusBar() {
		JETAPanel panel = new JETAPanel();
		FormLayout layout = new FormLayout("20px:nogrow,2px,pref:grow(0.5),2px,pref:grow(0.5)", "pref");
		panel.setLayout(layout);

		m_formtype_cell = new TSCell("formtypecell", "center:pref:nogrow");

		m_rowcell = new TSCell("rowcell", "center:pref:nogrow");
		m_rowcell.setFont(javax.swing.UIManager.getFont("Table.font"));
		m_rowcell.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		m_colcell = new TSCell("colcell", "center:pref:nogrow");
		m_colcell.setFont(javax.swing.UIManager.getFont("Table.font"));
		m_colcell.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		CellConstraints cc = new CellConstraints();
		panel.add(m_formtype_cell, cc.xy(1, 1));
		panel.add(m_colcell, cc.xy(3, 1));
		panel.add(m_rowcell, cc.xy(5, 1));
		return panel;
	}

	/**
	 * Creates a top level form cell
	 */
	private FormComponent createTopParent(FormComponent form) throws FormException {
		m_form = (DesignFormComponent) form;
		m_gridview = form.getChildView();

		// **** now make sure that any empty cells are properly filled
		m_gridview.enableEvents(true);

		ContainedFormFactory factory = (ContainedFormFactory) JETARegistry.lookup(ContainedFormFactory.COMPONENT_ID);
		m_topparent = factory.createTopParent(this, m_compsrc, form);
		GridView view = m_topparent.getChildView();
		view.enableEvents(true);

		/**
		 * you must call setOpaque here or there will be some color artifacts
		 * visible when look and feel changes
		 */
		m_topparent.setOpaque(true);

		/**
		 * Re-add any listeners to the top parent. This is needed when doing a
		 * Save As on a form. The editor does not change but the form does.
		 */
		Iterator iter = m_grid_listeners.iterator();
		while (iter.hasNext()) {
			view.addListener((GridViewListener) iter.next());
		}

		m_topparent.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		return m_topparent;
	}

	/**
	 * Deactivates this editor.
	 */
	public void deactivate() {
		m_active = false;
	}

	/**
	 * Invokes the given command and registers it with the UndoSupport
	 */
	public void editHappened(FormUndoableEdit edit) {
		m_undomanager.addEdit(edit);
	}

	/**
	 * @return the component source
	 */
	public ComponentSource getComponentSource() {
		return m_compsrc;
	}

	/**
	 * @return the editor that is an ancestor of the given component. If the
	 *         comp is not in a FormEditor hierarchy, null is returned.
	 */
	public static FormEditor getEditor(Component comp) {
		if (comp instanceof FormEditor)
			return (FormEditor) comp;

		java.awt.Container parent = comp.getParent();
		while (parent != null) {
			if (parent instanceof FormEditor)
				return (FormEditor) parent;

			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * @return the formcomponent that is this form
	 */
	public DesignFormComponent getForm() {
		return (DesignFormComponent) m_form;
	}

	/**
	 * @return the formcomponent that is this form
	 */
	public DesignFormComponent getFormComponent() {
		return (DesignFormComponent) m_form;
	}

	/**
	 * @return the overlay for the main form
	 */
	public GridOverlay getGridOverlay() {
		return m_gridview.getOverlay();
	}

	/**
	 * @return the id of the form we are editing.
	 */
	public String getId() {
		return m_form.getId();
	}

	/**
	 * @return the top most overlay on the form. This is the 1x1 form that is
	 *         the parent for the main form we are editing.
	 */
	public GridOverlay getTopOverlay() {
		assert (m_topparent != null);
		assert (m_topparent.getChildView() != null);
		return m_topparent.getChildView().getOverlay();
	}

	/**
	 * @return the top most parent FormComponent. This is the 1x1 form that is
	 *         the parent for the main form we are editing.
	 */
	public FormComponent getTopParent() {
		return m_topparent;
	}

	/**
	 * @return the UndoManager for this editor
	 */
	public EditorUndoManager getUndoManager() {
		return m_undomanager;
	}

	/** GridViewListener implementation */
	public void gridChanged(GridViewEvent evt) {
		updateStatusBar();
		if (evt.getId() != GridViewEvent.EDIT_COMPONENT && evt.getId() != GridViewEvent.CELL_SELECTED) {
			revalidate();
			m_gridview.revalidate();
			m_gridview.doLayout();
			m_gridview.repaint();
			m_form.revalidate();
			m_form.doLayout();
			m_form.repaint();
			doLayout();
			repaint();
		}

		if (m_colmargin != null) {
			if (evt.getId() == GridViewEvent.CELL_SELECTED) {
				GridComponent gc = getSelectedComponent();
				m_colmargin.update(gc);
				m_rowmargin.update(gc);
			}
			else {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						GridComponent gc = getSelectedComponent();
						m_colmargin.update(gc);
						m_rowmargin.update(gc);
					}
				});
			}
		}

		// if ( m_active && m_focus_view != null)
		// {
		// m_focus_view.gridChanged( evt );
		// }
	}

	/**
	 * @returns the first selected component it finds in the component hierarhcy
	 *          of this container.
	 */
	public GridComponent getSelectedComponent() {
		GridComponent comp = m_gridview.getSelectedComponent();
		if (comp == null) {
			if (m_form.isSelected())
				comp = m_form;
		}
		return comp;
	}

	/**
	 * Initializes the form
	 */
	private void initialize(FormComponent formcell) throws FormException {
		m_undomanager = new EditorUndoManager((AbstractFormManager) JETARegistry.lookup(AbstractFormManager.COMPONENT_ID), this);

		m_form.setControlButtonsVisible(false);
		GridView gridview = formcell.getChildView();

		setLayout(new BorderLayout());

		m_scroll = new JScrollPane(formcell);
		m_scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		m_colmargin = new ColumnMargin(m_form, gridview, m_compsrc, m_scroll.getViewport(), showMargins());
		m_rowmargin = new RowMargin(m_form, gridview, m_compsrc, m_scroll.getViewport(), showMargins());

		m_scroll.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				m_colmargin.update();
			}
		});

		m_scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				m_rowmargin.update();
			}
		});

		JPanel content = new JPanel(new FormEditorLayout(m_colmargin, m_rowmargin, m_scroll, formcell));

		content.add(m_colmargin);
		content.add(m_rowmargin);
		content.add(m_scroll);
		add(content, BorderLayout.CENTER);

		add(createStatusBar(), BorderLayout.SOUTH);
		gridview.addListener(this);

		final DesignGridOverlay overlay = (DesignGridOverlay) gridview.getOverlay();
		final GridMouseListener mlistener = new GridMouseListener(gridview.getOverlay(), formcell.getMouseHandler());
		overlay.setFocusable(true);

		/**
		 * we need to delay adding the listeners because the form component
		 * might not be fully initialized yet
		 */
		Runnable gui_update = new Runnable() {
			public void run() {
				overlay.addMouseListener(mlistener);
				overlay.addMouseMotionListener(mlistener);
			}
		};
		javax.swing.SwingUtilities.invokeLater(gui_update);

		/**
		 * This mouse listener is for clicks in the border around the main form.
		 * It gives the user a little room to select the main form
		 */
		formcell.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				FormManagerDesignUtils.deselectAll(m_form);
				m_form.setSelected(true);
				repaint();
			}
		});

		unitTest();
	}

	/**
	 * @return true if this form is an embedded form. An embedded form is stored
	 *         within the parent form.
	 */
	public boolean isEmbedded() {
		return m_form.isEmbedded();
	}

	/**
	 * @return true if the focus view is visible
	 */
	public boolean isFocusViewVisible() {
		return (m_focus_view != null);
	}

	/**
	 * @return true if this form is a linked form. A linked form is one that is
	 *         actually stored in a file.
	 */
	public boolean isLinked() {
		return m_form.isLinked();
	}

	/**
	 * @return true if this form has been modified.
	 */
	public boolean isModified() {
		return (m_undomanager.size() > 0 && m_undomanager.getIndexOfNextAdd() > 0);
	}

	/**
	 * Removes the given GridListener from this editor.
	 */
	public void removeListener(GridViewListener listener) {
		m_topparent.getChildView().removeListener(listener);
		m_grid_listeners.remove(listener);
	}

	/**
	 * Saves the current focus policy
	 */
	public void saveFocusPolicy() {
		if (m_focus_view != null) {
			m_form.setFocusPolicy(m_focus_view.getFocusPolicyMemento());
		}
	}

	/**
	 * Sets the focus view visible
	 */
	public void setFocusViewVisible(boolean bvisible) {
		GridView gridview = m_topparent.getChildView();
		/*
		 * JLayeredPane layeredpane = gridview.getLayeredPane(); if ( bvisible ) {
		 * if ( m_focus_view != null ) { layeredpane.remove( m_focus_view ); }
		 * m_focus_view = new FocusView( m_form ); layeredpane.add(
		 * m_focus_view, GridView.FOCUS_LAYER ); } else { if ( m_focus_view !=
		 * null ) { if ( layeredpane.isAncestorOf( m_focus_view ) ) {
		 * layeredpane.remove( m_focus_view ); } m_form.setFocusPolicy(
		 * m_focus_view.getFocusPolicyMemento() ); m_focus_view = null; } }
		 */
	}

	/**
	 * Sets the formcomponent that is this form. This call is made when the user
	 * performs a Save As.
	 */
	public void setFormComponent(FormComponent fc) throws FormException {
		removeAll();
		initialize(createTopParent(fc));
		revalidate();
	}

	/**
	 * Returns true if the resize margins are visible (user set property)
	 */
	boolean showMargins() {
		return TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_SHOW_RESIZE_HANDLES, true);
	}

	/**
	 * Override updateUI so we can update the scrollpane viewport background.
	 */
	public void updateUI() {
		super.updateUI();
	}

	/**
	 * Runs unit test routines on this editor.
	 */
	public void unitTest() {
		if (FormDesignerUtils.isTest()) {
			// com.jeta.swingbuilder.test.JETATestFactory.runTest(
			// "test.jeta.swingbuilder.gui.editor.EditorValidator", this );
		}
	}

	/**
	 * Updates the display based on user preferences.
	 */
	public void updatePreferences() {
		if (showMargins() != m_colmargin.isPaintMargin()) {
			m_colmargin.setPaintMargins(showMargins());
			m_rowmargin.setPaintMargins(showMargins());
			revalidate();
			repaint();
			GridComponent gc = getSelectedComponent();
			m_colmargin.update(gc);
			m_rowmargin.update(gc);
		}
	}

	/**
	 * Updates the status bar to display the column and row specs for the given
	 * component.
	 */
	private void updateStatusBar() {
		m_colcell.setText("");
		m_rowcell.setText("");
		if (m_formcell != null) {
			m_formcell.setText("");
			m_parentcell.setText("");
		}

		GridComponent gc = getSelectedComponent();

		if (gc != null) {
			GridView view = gc.getParentView();
			if (view != null) {
				ColumnSpec cspec = view.getColumnSpec(gc.getColumn());
				m_colcell.setText(I18N.format("column_spec_2", gc.getColumn(), cspec.toString()));
				RowSpec rspec = view.getRowSpec(gc.getRow());
				m_rowcell.setText(I18N.format("row_spec_2", gc.getRow(), rspec.toString()));

			}

			if (gc instanceof FormComponent) {
				FormComponent fc = (FormComponent) gc;
				if (fc.isLinked()) {
					m_formtype_cell.setIcon(m_linked_icon);
				}
				else {
					m_formtype_cell.setIcon(m_embedded_icon);
				}

				if (m_formcell != null) {
					m_formcell.setText("Form: " + gc.getId());
					FormComponent parent = fc.getParentForm();
					if (parent != null) {
						m_parentcell.setText("Parent Form: " + parent.getId());
					}
				}
			}
			else {
				m_formtype_cell.setIcon(null);
			}
			// gc.print();
		}
	}

	/**
	 * LayoutManager for this view. It lays out the Margins and the main scroll
	 * pane
	 */
	public static class FormEditorLayout implements LayoutManager {
		private Margin m_colmargin;
		private Margin m_rowmargin;
		private JScrollPane m_scroll;
		private FormComponent m_topparent;

		public FormEditorLayout(Margin colMargin, Margin rowMargin, JScrollPane scroll, FormComponent topparent) {
			m_colmargin = colMargin;
			m_rowmargin = rowMargin;
			m_scroll = scroll;
			m_topparent = topparent;
		}

		/**
		 * @param name
		 * @param comp
		 */
		public void addLayoutComponent(String name, Component comp) {
		}

		/** @param parent */
		public void layoutContainer(Container parent) {
			Dimension margin_sz = m_colmargin.getPreferredSize();

			Insets insets = parent.getInsets();
			int colm_x = insets.left + margin_sz.width;
			int colm_y = insets.top;
			int colm_width = parent.getWidth() - insets.right - colm_x;
			int colm_height = margin_sz.height;

			int rowm_x = insets.left;
			int rowm_y = insets.top + margin_sz.height;
			int rowm_width = margin_sz.width;
			int rowm_height = parent.getHeight() - insets.bottom - rowm_y;

			m_scroll.setLocation(colm_x, rowm_y);
			m_scroll.setSize(colm_width, rowm_height);

			m_colmargin.setLocation(colm_x, colm_y);
			m_colmargin.setSize(colm_width, colm_height);

			m_rowmargin.setLocation(rowm_x, rowm_y);
			m_rowmargin.setSize(rowm_width, rowm_height);
		}

		/**
		 * @param parent
		 * @return
		 */
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(100, 100);
		}

		/**
		 * @param parent
		 * @return
		 */
		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(600, 400);
		}

		/** @param comp */
		public void removeLayoutComponent(Component comp) {
		}
	}
}
