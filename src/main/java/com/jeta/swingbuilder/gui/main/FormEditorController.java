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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.EmptyComponentFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.FormComponentFactory;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.ComponentMementoProxy;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.CompositeCommand;
import com.jeta.swingbuilder.gui.commands.DeleteColumnCommand;
import com.jeta.swingbuilder.gui.commands.DeleteComponentCommand;
import com.jeta.swingbuilder.gui.commands.DeleteRowCommand;
import com.jeta.swingbuilder.gui.commands.EditColumnSpecCommand;
import com.jeta.swingbuilder.gui.commands.EditRowSpecCommand;
import com.jeta.swingbuilder.gui.commands.EditTextPropertyCommand;
import com.jeta.swingbuilder.gui.commands.FormUndoableEdit;
import com.jeta.swingbuilder.gui.commands.InsertColumnCommand;
import com.jeta.swingbuilder.gui.commands.InsertRowCommand;
import com.jeta.swingbuilder.gui.commands.ReplaceComponentCommand;
import com.jeta.swingbuilder.gui.commands.SetCellBackgroundCommand;
import com.jeta.swingbuilder.gui.commands.SetPropertyCommand;
import com.jeta.swingbuilder.gui.commands.TrimColumnsCommand;
import com.jeta.swingbuilder.gui.commands.TrimRowsCommand;
import com.jeta.swingbuilder.gui.components.TSComponentNames;
import com.jeta.swingbuilder.gui.dnd.ComponentTransferable;
import com.jeta.swingbuilder.gui.dnd.DesignerDragSource;
import com.jeta.swingbuilder.gui.dnd.FormObjectFlavor;
import com.jeta.swingbuilder.gui.editor.DesignGridOverlay;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.gui.properties.PropertyPaneContainer;
import com.jeta.swingbuilder.gui.undo.UndoableEditProxy;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

/**
 * Handles user events for the FormEditorFrame
 * 
 * @author Jeff Tassin
 */
public abstract class FormEditorController extends JETAController {
	private MainFrame m_frame;

	/**
	 * Popup menu
	 */
	private JPopupMenu m_popup;

	/**
	 * ctor
	 */
	public FormEditorController(MainFrame frame, JPopupMenu popup) {
		super(frame);
		m_frame = frame;
		m_popup = popup;

		assignAction(TSComponentNames.ID_CUT, new CutAction());
		assignAction(TSComponentNames.ID_COPY, new CopyAction());
		assignAction(TSComponentNames.ID_PASTE, new PasteAction());
		assignAction(FormEditorNames.ID_PASTE_SPECIAL, new PasteSpecialAction());
		assignAction(TSComponentNames.ID_UNDO, new UndoAction());
		assignAction(TSComponentNames.ID_REDO, new RedoAction());

		assignAction(FormEditorNames.ID_INSERT_ROW_ABOVE, new InsertRowAction(true));
		assignAction(FormEditorNames.ID_INSERT_ROW_BELOW, new InsertRowAction(false));
		assignAction(FormEditorNames.ID_INSERT_COLUMN_LEFT, new InsertColumnAction(true));
		assignAction(FormEditorNames.ID_INSERT_COLUMN_RIGHT, new InsertColumnAction(false));
		assignAction(FormEditorNames.ID_DELETE_COLUMN, new DeleteColumnAction());
		assignAction(FormEditorNames.ID_DELETE_ROW, new DeleteRowAction());
		assignAction(FormEditorNames.ID_SET_AS_COLUMN_SEPARATOR, new SetColumnSeparatorAction(true));
		assignAction(FormEditorNames.ID_SET_AS_BIG_COLUMN_SEPARATOR, new SetColumnSeparatorAction(false));
		assignAction(FormEditorNames.ID_SET_AS_ROW_SEPARATOR, new SetRowSeparatorAction(true));
		assignAction(FormEditorNames.ID_SET_AS_BIG_ROW_SEPARATOR, new SetRowSeparatorAction(false));

		assignAction(FormEditorNames.ID_COLUMN_DECREASE_SPAN, new IncrementColumnSpanAction(false));
		assignAction(FormEditorNames.ID_COLUMN_INCREASE_SPAN, new IncrementColumnSpanAction(true));
		assignAction(FormEditorNames.ID_ROW_DECREASE_SPAN, new IncrementRowSpanAction(false));
		assignAction(FormEditorNames.ID_ROW_INCREASE_SPAN, new IncrementRowSpanAction(true));

		assignAction(FormEditorNames.ID_COLUMN_PREFERRED_SIZE, new ColumnSetPreferredSizeAction());
		assignAction(FormEditorNames.ID_ROW_PREFERRED_SIZE, new RowSetPreferredSizeAction());

		assignAction(FormEditorNames.ID_SHOW_GRID, new ShowGridAction());

		assignAction(FormEditorNames.ID_TRIM_ROWS, new TrimRowsAction());
		assignAction(FormEditorNames.ID_TRIM_COLUMNS, new TrimColumnsAction());
		assignAction(FormEditorNames.ID_DELETE_COMPONENT, new DeleteComponentAction());

		assignAction(FormEditorNames.ID_COLUMN_RESIZE_GROW, new ColumnResizeAction(true));
		assignAction(FormEditorNames.ID_COLUMN_RESIZE_NONE, new ColumnResizeAction(false));
		assignAction(FormEditorNames.ID_ROW_RESIZE_GROW, new RowResizeAction(true));
		assignAction(FormEditorNames.ID_ROW_RESIZE_NONE, new RowResizeAction(false));
	}

	/**
	 * @return the current form editor
	 */
	public abstract FormEditor getCurrentEditor();

	/**
	 * Installs handlers for a specific editor
	 */
	void installHandlers(FormEditor editor) {
		DesignGridOverlay overlay = (DesignGridOverlay) editor.getTopOverlay();
		overlay.addMouseListener(new ContextMouseListener());

		InputMap inputmap = overlay.getInputMap();
		ActionMap actionmap = overlay.getActionMap();

		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), FormEditorNames.ID_DELETE_COMPONENT);
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), FormEditorNames.ID_NAVIGATE_LEFT);
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), FormEditorNames.ID_NAVIGATE_RIGHT);
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), FormEditorNames.ID_NAVIGATE_UP);
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), FormEditorNames.ID_NAVIGATE_DOWN);
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), FormEditorNames.ID_EDIT_COMPONENT_PROPERTIES);
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), FormEditorNames.ID_EDIT_COMPONENT_NAME);
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), FormEditorNames.ID_CANCEL_DRAG);

		actionmap.put(FormEditorNames.ID_DELETE_COMPONENT, new DeleteComponentAction());
		actionmap.put(FormEditorNames.ID_NAVIGATE_LEFT, new LeftAction());
		actionmap.put(FormEditorNames.ID_NAVIGATE_RIGHT, new RightAction());
		actionmap.put(FormEditorNames.ID_NAVIGATE_UP, new UpAction());
		actionmap.put(FormEditorNames.ID_NAVIGATE_DOWN, new DownAction());
		actionmap.put(FormEditorNames.ID_EDIT_COMPONENT_PROPERTIES, new EditComponentAction());
		actionmap.put(FormEditorNames.ID_EDIT_COMPONENT_NAME, new EditComponentNameAction());
		actionmap.put(FormEditorNames.ID_CANCEL_DRAG, new EscapeAction());
	}

	/**
	 * Creates a paste command. Gets a grid component memento from the
	 * clipboard, instantiates a new component, sets the state and places it on
	 * the parent view
	 */
	private FormUndoableEdit getPasteCommand(GridView parentView, Transferable transferable) {
		try {
			GridComponent oldcomp = getCurrentEditor().getSelectedComponent();
			if (oldcomp != null) {
				ComponentMementoProxy cmproxy = (ComponentMementoProxy) transferable.getTransferData(FormObjectFlavor.COMPONENT_MEMENTO);
				ComponentMemento cm = cmproxy.getComponentMemento();

				Class compclass = Class.forName(cm.getComponentClass());
				GridComponent newcomp = null;
				if (FormComponent.class.isAssignableFrom(compclass)) {
					FormComponentFactory factory = (FormComponentFactory) JETARegistry.lookup(FormComponentFactory.COMPONENT_ID);
					newcomp = factory.createFormComponent();
				}
				else {
					newcomp = (GridComponent) compclass.newInstance();
				}
				newcomp.setParentView(parentView);
				newcomp.setState(cm);

				FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
				if (newcomp instanceof FormComponent) {
					FormManagerDesignUtils.registerForms(fmgr, (FormComponent) newcomp);
				}
				fmgr.installHandlers(getCurrentEditor().getComponentSource(), newcomp);
				ReplaceComponentCommand cmd = new ReplaceComponentCommand(newcomp, oldcomp, parentView.getParentForm());
				return cmd;
			}
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
		return null;
	}

	/**
	 * Gets a Grid component memento from the clipboard, instantiates a new
	 * component, sets the state and places it on the parent view
	 */
	private void pasteComponent(GridView parentView, Transferable transferable) {
		try {
			CommandUtils.invoke(getPasteCommand(parentView, transferable), getCurrentEditor());
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

	public class IncrementColumnSpanAction extends AbstractAction {
		/*
		 * Set to false to decrement
		 */
		private boolean m_increment = true;

		public IncrementColumnSpanAction(boolean increase) {
			m_increment = increase;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				int colspan = gc.getColumnSpan();
				if (m_increment)
					colspan++;
				else
					colspan--;

				FormDesignerUtils.setSpan(gc, colspan, gc.getRowSpan());
			}
		}
	}

	public class IncrementRowSpanAction extends AbstractAction {
		/*
		 * Set to false to decrement
		 */
		private boolean m_increment = true;

		public IncrementRowSpanAction(boolean increase) {
			m_increment = increase;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				int rowspan = gc.getRowSpan();
				if (m_increment)
					rowspan++;
				else
					rowspan--;

				FormDesignerUtils.setSpan(gc, gc.getColumnSpan(), rowspan);
			}
		}
	}

	/**
	 * Sets the size behavior to preferred for the selected column
	 */
	public class ColumnSetPreferredSizeAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				int col = gc.getColumn();
				ColumnSpec oldspec = view.getFormLayout().getColumnSpec(col);
				ColumnSpec newspec = new ColumnSpec(oldspec.getDefaultAlignment(), Sizes.PREFERRED, oldspec.getResizeWeight());
				EditColumnSpecCommand cmd = new EditColumnSpecCommand(view.getParentForm(), col, newspec, oldspec);
				CommandUtils.invoke(cmd, getCurrentEditor());
			}
		}
	}

	/**
	 * Copies the selected bean and places it on the clipboard
	 */
	public class CopyAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			try {
				System.out.println("CopyAction........................ ");
				GridComponent gc = getCurrentEditor().getSelectedComponent();
				if (gc != null) {
					ComponentTransferable transferable = new ComponentTransferable(gc);
					Toolkit kit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = kit.getSystemClipboard();
					clipboard.setContents(transferable, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Deletes the selected bean and places it on the clipboard
	 */
	public class CutAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				FormEditorController.this.invokeAction(TSComponentNames.ID_COPY);
				FormEditorController.this.invokeAction(FormEditorNames.ID_DELETE_COMPONENT);
			}
		}
	}

	/**
	 * Deletes the selected column. Any components in the column are deleted as
	 * well.
	 */
	public class DeleteColumnAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null && view.getColumnCount() > 1) {
					int row = gc.getRow();
					int col = gc.getColumn();

					DeleteColumnCommand cmd = new DeleteColumnCommand(view.getParentForm(), col, getCurrentEditor().getComponentSource());
					CommandUtils.invoke(cmd, getCurrentEditor());
					if (col > view.getColumnCount())
						col--;

					gc = view.getGridComponent(col, row);
					if (gc != null)
						gc.setSelected(true);

					FormManagerDesignUtils.clearUnreferencedForms();

				}
			}
		}
	}

	/**
	 * Deletes the selected bean from the view
	 */
	public class DeleteComponentAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				DeleteComponentCommand cmd = new DeleteComponentCommand(gc, getCurrentEditor().getComponentSource());
				CommandUtils.invoke(cmd, getCurrentEditor());
				FormManagerDesignUtils.clearUnreferencedForms();
			}
		}
	}

	/**
	 * Deletes the selected row. Any components in the row are deleted as well.
	 */
	public class DeleteRowAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null && view.getRowCount() > 1) {
					int row = gc.getRow();
					int col = gc.getColumn();
					DeleteRowCommand cmd = new DeleteRowCommand(view.getParentForm(), row, getCurrentEditor().getComponentSource());
					CommandUtils.invoke(cmd, getCurrentEditor());
					if (row > view.getRowCount())
						row--;

					gc = view.getGridComponent(col, row);
					if (gc != null)
						gc.setSelected(true);

					FormManagerDesignUtils.clearUnreferencedForms();

				}
			}
		}
	}

	/**
	 * Selects the cell immediately below the current cell
	 */
	public class DownAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			GridView view = gc.getParentView();
			if (gc != null) {
				if (view != null) {
					int row = gc.getRow();
					int col = gc.getColumn();
					row++;
					if (row > view.getRowCount()) {
						row = 1;
						col++;
						if (col > view.getColumnCount())
							col = 1;
					}
					GridComponent next = view.getGridComponent(col, row);
					gc.setSelected(false);
					next.setSelected(true);
				}
			}
		}
	}

	/**
	 * Edits the row spec for the selected row
	 */
	public class EditColumnSpecAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int col = gc.getColumn();
					// temporary dialog

					ColumnSpec oldspec = view.getFormLayout().getColumnSpec(col);
					String newspec = javax.swing.JOptionPane.showInputDialog(m_frame, I18N.getLocalizedMessage("Column Spec:"), oldspec.toString());
					if (newspec != null) {
						EditColumnSpecCommand cmd = new EditColumnSpecCommand(view.getParentForm(), col, new ColumnSpec(newspec), oldspec);
						CommandUtils.invoke(cmd, getCurrentEditor());
					}
				}
			}
			else {
				System.out.println("FormEditorFrameComponent.editColumnSpec  selected comp is null");
			}
		}
	}

	/**
	 * Sends a message to the property editor to show the properties for the
	 * selected component
	 */
	public class EditComponentAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				EditTextPropertyCommand.tryEditTextProperty(FormEditor.getEditor(gc), FormComponent.getParentForm(gc), gc);
				gc.fireGridCellEvent(new GridCellEvent(GridCellEvent.EDIT_COMPONENT, gc));
			}
		}
	}

	/**
	 * Sends a message to the property editor to show the properties for the
	 * selected component
	 */
	public class EditComponentNameAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				java.awt.Component comp = gc.getBeanDelegate();
				if (comp != null) {
					String oldname = comp.getName();
					String newvalue = javax.swing.JOptionPane.showInputDialog(m_frame, I18N.getLocalizedMessage("Set Name"), oldname);
					if (newvalue != null) {
						comp.setName(newvalue);
						gc.fireGridCellEvent(new GridCellEvent(GridCellEvent.EDIT_COMPONENT, gc, GridCellEvent.COMPONENT_NAME_CHANGED));
					}
				}
			}
		}
	}

	/**
	 * Edits the row spec for the selected row
	 */
	public class EditRowSpecAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int row = gc.getRow();
					// temporary dialog

					RowSpec oldspec = view.getFormLayout().getRowSpec(row);
					String newspec = javax.swing.JOptionPane.showInputDialog(m_frame, I18N.getLocalizedMessage("Row Spec:"), oldspec.toString());
					if (newspec != null) {
						EditRowSpecCommand cmd = new EditRowSpecCommand(view.getParentForm(), row, new RowSpec(newspec), oldspec);
						CommandUtils.invoke(cmd, getCurrentEditor());
					}
				}
			}
		}
	}

	public class EscapeAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			DesignerDragSource dds = (DesignerDragSource) JETARegistry.lookup(DesignerDragSource.COMPONENT_ID);
			if (dds != null)
				dds.cancelDrag();
		}
	}

	/**
	 * Inserts a new column into the selected view
	 */
	public class InsertColumnAction implements ActionListener {
		/**
		 * Flag that indicates if column should be inserted immediately to
		 * left/right of selected column.
		 */
		private boolean m_left = true;

		/**
		 * ctor
		 */
		public InsertColumnAction(boolean bleft) {
			m_left = bleft;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int col = gc.getColumn();
					String colspec = "d";
					if (!m_left)
						col++;

					InsertColumnCommand cmd = new InsertColumnCommand(view.getParentForm(), col, new ColumnSpec(colspec), new EmptyComponentFactory(
							getCurrentEditor().getComponentSource()));
					CommandUtils.invoke(cmd, getCurrentEditor());
				}
			}
		}
	}

	/**
	 * Inserts a new row into the selected view
	 */
	public class InsertRowAction implements ActionListener {
		private boolean m_above = true;

		/**
		 * ctor
		 */
		public InsertRowAction(boolean babove) {
			m_above = babove;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int row = gc.getRow();
					if (!m_above)
						row++;

					String rowspec = "d";
					InsertRowCommand cmd = new InsertRowCommand(view.getParentForm(), row, new RowSpec(rowspec), new EmptyComponentFactory(getCurrentEditor()
							.getComponentSource()));
					CommandUtils.invoke(cmd, getCurrentEditor());
				}
			}
		}
	}

	/**
	 * Selects the cell to the immediate left
	 */
	public class LeftAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int row = gc.getRow();
					int col = gc.getColumn();
					col--;
					if (col < 1) {
						col = view.getColumnCount();
						row--;
						if (row < 1)
							row = view.getRowCount();
					}
					GridComponent next = view.getGridComponent(col, row);
					gc.setSelected(false);
					next.setSelected(true);
				}
			}
		}
	}

	/**
	 * Selects the cell to the immediate right
	 */
	public class RightAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int row = gc.getRow();
					int col = gc.getColumn();
					col++;
					if (col > view.getColumnCount()) {
						col = 1;
						row++;
						if (row > view.getRowCount())
							row = 1;
					}
					GridComponent next = view.getGridComponent(col, row);
					gc.setSelected(false);
					next.setSelected(true);
				}
			}
		}
	}

	/**
	 * Pastes any components from the system clipboard
	 */
	public class PasteAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			try {
				GridComponent oldcomp = getCurrentEditor().getSelectedComponent();
				if (oldcomp != null) {
					GridView view = oldcomp.getParentView();
					Toolkit kit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = kit.getSystemClipboard();
					Transferable transferable = clipboard.getContents(null);
					if (transferable.isDataFlavorSupported(FormObjectFlavor.LINKED_FORM_SET)) {
						FormComponent parentform = view.getParentForm();

						Collection forms = (Collection) transferable.getTransferData(FormObjectFlavor.LINKED_FORM_SET);
						Iterator iter = forms.iterator();
						while (iter.hasNext()) {
							String form_id = (String) iter.next();
							if (FormManagerDesignUtils.containsForm(getCurrentEditor().getForm(), form_id)) {
								String msg = I18N.format("linked_form_once_per_view_1", form_id);
								String title = I18N.getLocalizedMessage("Error");
								JOptionPane.showMessageDialog(m_frame, msg, title, JOptionPane.ERROR_MESSAGE);
								return;
							}
						}

						if (transferable.isDataFlavorSupported(FormObjectFlavor.LINKED_FORM)) {
							// means we have a top level linked form
							FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
							String form_path = (String) transferable.getTransferData(FormObjectFlavor.LINKED_FORM);
							FormComponent form = fmgr.openLinkedForm(form_path);
							fmgr.activateForm(form.getId());
							ReplaceComponentCommand cmd = new ReplaceComponentCommand(form, oldcomp, view.getParentForm());
							CommandUtils.invoke(cmd, getCurrentEditor());
						}
						else if (transferable.isDataFlavorSupported(FormObjectFlavor.COMPONENT_MEMENTO)) {
							// means we have a top level embedded form with
							// nested
							// links
							pasteComponent(view, transferable);
						}
					}
					else if (transferable.isDataFlavorSupported(FormObjectFlavor.COMPONENT_MEMENTO)) {
						pasteComponent(view, transferable);
					}
					else if (transferable.isDataFlavorSupported(FormObjectFlavor.CELL_BACKGROUND)) {
						int col = oldcomp.getColumn();
						int row = oldcomp.getRow();
						PaintProperty paint = (PaintProperty) transferable.getTransferData(FormObjectFlavor.CELL_BACKGROUND);
						PaintProperty oldpaint = view.getPaintProperty(col, row);
						SetCellBackgroundCommand cmd = new SetCellBackgroundCommand(view.getParentForm(), col, row, paint, oldpaint);
						CommandUtils.invoke(cmd, getCurrentEditor());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Invokes the paste special dialog
	 */
	public class PasteSpecialAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			try {
				GridComponent oldcomp = getCurrentEditor().getSelectedComponent();
				if (oldcomp != null) {
					GridView gridview = oldcomp.getParentView();
					Toolkit kit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = kit.getSystemClipboard();
					Transferable transferable = clipboard.getContents(null);
					PasteSpecialView view = new PasteSpecialView(transferable);
					JETADialog dlg = JETAToolbox.invokeDialog(view, gridview, I18N.getLocalizedMessage("Paste Special"));
					if (dlg.isOk()) {
						int col = oldcomp.getColumn();
						int row = oldcomp.getRow();

						FormUndoableEdit cmd = null;

						if (transferable.isDataFlavorSupported(FormObjectFlavor.LINKED_FORM_SET) && view.isSelected(PasteSpecialNames.ID_LINKED_AS_EMBEDDED)) {

							/**
							 * if LINKED_FORM is supported, then GRID_COMPONENT
							 * must also be supported
							 */
							if (transferable.isDataFlavorSupported(FormObjectFlavor.COMPONENT_MEMENTO)) {
								cmd = getPasteCommand(gridview, transferable);
							}
							else {
								assert (false);
							}
						}
						else if (transferable.isDataFlavorSupported(FormObjectFlavor.COMPONENT_MEMENTO) && view.isSelected(PasteSpecialNames.ID_COMPONENT)) {
							cmd = getPasteCommand(gridview, transferable);
						}

						FormUndoableEdit pp_cmd = null;
						if (transferable.isDataFlavorSupported(FormObjectFlavor.CELL_BACKGROUND) && view.isSelected(PasteSpecialNames.ID_CELL_BACKGROUND)) {
							PaintProperty paint = (PaintProperty) transferable.getTransferData(FormObjectFlavor.CELL_BACKGROUND);
							PaintProperty oldpaint = gridview.getPaintProperty(col, row);
							pp_cmd = new SetCellBackgroundCommand(gridview.getParentForm(), col, row, paint, oldpaint);
						}

						CompositeCommand cc_cmd = new CompositeCommand(gridview.getParentForm(), cmd, pp_cmd);
						CommandUtils.invoke(cc_cmd, getCurrentEditor());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Pastes a linked component from the clipboard as an embedded component.
	 */
	public class PasteAsEmbeddedAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			try {
				GridComponent oldcomp = getCurrentEditor().getSelectedComponent();
				if (oldcomp != null) {
					GridView view = oldcomp.getParentView();
					Toolkit kit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = kit.getSystemClipboard();
					Transferable transferable = clipboard.getContents(null);
					if (transferable.isDataFlavorSupported(FormObjectFlavor.LINKED_FORM)) {
						/**
						 * if LINKED_FORM is supported, then GRID_COMPONENT must
						 * also be supported
						 */
						if (transferable.isDataFlavorSupported(FormObjectFlavor.COMPONENT_MEMENTO)) {
							pasteComponent(view, transferable);
						}
						else {
							assert (false);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Redoes a previous edit
	 */
	public class RedoAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			CommandUtils.redoEdit(getCurrentEditor());
			FormManagerDesignUtils.deselectAll(getCurrentEditor().getTopParent());
		}
	}

	/**
	 * Sets the resize behavior on the selected column
	 */
	public class ColumnResizeAction extends AbstractAction {
		private boolean m_growable = false;

		public ColumnResizeAction(boolean growable) {
			m_growable = growable;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int col = gc.getColumn();
					ColumnSpec oldspec = view.getFormLayout().getColumnSpec(col);

					float resize_weight = m_growable ? 1.0f : 0.0f;

					ColumnSpec newspec = new ColumnSpec(oldspec.getDefaultAlignment(), oldspec.getSize(), resize_weight);
					EditColumnSpecCommand cmd = new EditColumnSpecCommand(view.getParentForm(), col, newspec, oldspec);
					CommandUtils.invoke(cmd, getCurrentEditor());
				}
			}
		}
	}

	/**
	 * Sets the resize behavior on the selected row
	 */
	public class RowResizeAction extends AbstractAction {
		private boolean m_growable = false;

		public RowResizeAction(boolean growable) {
			m_growable = growable;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int row = gc.getRow();
					RowSpec oldspec = view.getFormLayout().getRowSpec(row);

					float resize_weight = m_growable ? 1.0f : 0.0f;

					RowSpec newspec = new RowSpec(RowSpec.FILL, oldspec.getSize(), resize_weight);
					EditRowSpecCommand cmd = new EditRowSpecCommand(view.getParentForm(), row, newspec, oldspec);
					CommandUtils.invoke(cmd, getCurrentEditor());
				}
			}
		}
	}

	/**
	 * Sets the size behavior to preferred for the selected row
	 */
	public class RowSetPreferredSizeAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int row = gc.getRow();
					RowSpec oldspec = view.getFormLayout().getRowSpec(row);

					RowSpec newspec = new RowSpec(oldspec.getDefaultAlignment(), Sizes.PREFERRED, oldspec.getResizeWeight());
					EditRowSpecCommand cmd = new EditRowSpecCommand(view.getParentForm(), row, newspec, oldspec);
					CommandUtils.invoke(cmd, getCurrentEditor());
				}
			}
		}
	}

	/**
	 * Formats the selected column as a separator (e.g set its size to something
	 * like 4dlu )
	 */
	public class SetColumnSeparatorAction extends AbstractAction {
		/**
		 * Set to false for large separator
		 */
		private boolean m_std_size = true;

		public SetColumnSeparatorAction(boolean stdsize) {
			m_std_size = stdsize;
		}

		private String getSeparatorSpec() {
			String units_prop_name = m_std_size ? UserPreferencesNames.ID_COL_STD_SEP_UNITS : UserPreferencesNames.ID_COL_LARGE_SEP_UNITS;
			String sz_prop_name = m_std_size ? UserPreferencesNames.ID_COL_STD_SEP_SIZE : UserPreferencesNames.ID_COL_LARGE_SEP_SIZE;

			String units = TSUserPropertiesUtils.getString(units_prop_name, "dlu");
			if (!FormUtils.isValidUnits(units))
				units = "dlu";

			String sz = m_std_size ? "4" : "8";
			sz = FormDesignerUtils.fastTrim(TSUserPropertiesUtils.getString(sz_prop_name, sz));
			if (sz.length() == 0)
				sz = FormUtils.getReasonableSize(units);

			String spec = "";
			try {
				StringBuffer sbuff = new StringBuffer();
				sbuff.append(sz);
				sbuff.append(units);
				spec = sbuff.toString();
			} catch (Exception e) {
				spec = (m_std_size ? "4dlu" : "8dlu");
			}
			return spec;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			GridView view = gc.getParentView();
			if (view != null) {
				int col = gc.getColumn();
				ColumnSpec oldspec = view.getColumnSpec(col);
				ColumnSpec newspec = new ColumnSpec(getSeparatorSpec());
				EditColumnSpecCommand cmd = new EditColumnSpecCommand(view.getParentForm(), col, newspec, oldspec);
				CommandUtils.invoke(cmd, getCurrentEditor());
			}
		}
	}

	/**
	 * Formats the selected row as a separator (e.g set its size to something
	 * like 2dlu )
	 */
	public class SetRowSeparatorAction extends AbstractAction {
		/**
		 * Set to false for large separator
		 */
		private boolean m_std_size = true;

		public SetRowSeparatorAction(boolean stdsize) {
			m_std_size = stdsize;
		}

		private String getSeparatorSpec() {
			String units_prop_name = m_std_size ? UserPreferencesNames.ID_ROW_STD_SEP_UNITS : UserPreferencesNames.ID_ROW_LARGE_SEP_UNITS;
			String sz_prop_name = m_std_size ? UserPreferencesNames.ID_ROW_STD_SEP_SIZE : UserPreferencesNames.ID_ROW_LARGE_SEP_SIZE;

			String units = TSUserPropertiesUtils.getString(units_prop_name, "dlu");
			if (!FormUtils.isValidUnits(units))
				units = "dlu";

			String sz = m_std_size ? "2" : "4";
			sz = FormDesignerUtils.fastTrim(TSUserPropertiesUtils.getString(sz_prop_name, sz));
			if (sz.length() == 0)
				sz = FormUtils.getReasonableSize(units);

			String spec = "";
			try {
				StringBuffer sbuff = new StringBuffer();
				sbuff.append(sz);
				sbuff.append(units);
				spec = sbuff.toString();
			} catch (Exception e) {
				spec = (m_std_size ? "2dlu" : "4dlu");
			}
			return spec;
		}

		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			GridView view = gc.getParentView();
			if (view != null) {
				int row = gc.getRow();
				RowSpec oldspec = view.getRowSpec(row);
				RowSpec newspec = new RowSpec(getSeparatorSpec());
				EditRowSpecCommand cmd = new EditRowSpecCommand(view.getParentForm(), row, newspec, oldspec);
				CommandUtils.invoke(cmd, getCurrentEditor());
			}
		}
	}

	/**
	 * Toggles the selected grid on/off
	 */
	public class ShowGridAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			GridView view = gc.getParentView();
			if (view != null) {
				view.setGridVisible(!view.isGridVisible());
			}
		}
	}

	/**
	 * Trims all columns that don't have components.
	 */
	public class TrimColumnsAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null && view.getColumnCount() > 1) {
					FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
					TrimColumnsCommand cmd = new TrimColumnsCommand(view.getParentForm(), getCurrentEditor().getComponentSource());
					CommandUtils.invoke(cmd, getCurrentEditor());
					FormManagerDesignUtils.clearUnreferencedForms();
				}
			}
		}
	}

	/**
	 * Trims all rows that don't have components.
	 */
	public class TrimRowsAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null && view.getRowCount() > 1) {
					FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
					TrimRowsCommand cmd = new TrimRowsCommand(view.getParentForm(), getCurrentEditor().getComponentSource());
					CommandUtils.invoke(cmd, getCurrentEditor());
					FormManagerDesignUtils.clearUnreferencedForms();
				}
			}
		}
	}

	/**
	 * Undoes a previous edit
	 */
	public class UndoAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			FormUndoableEdit cmd = CommandUtils.undoEdit(getCurrentEditor());
			if (cmd instanceof UndoableEditProxy)
				cmd = ((UndoableEditProxy) cmd).getDelegate();

			if (cmd instanceof SetPropertyCommand) {
				java.awt.Component controls_view = m_frame.getCurrentControlsView();
				if (controls_view instanceof PropertyPaneContainer) {
					((PropertyPaneContainer) controls_view).refreshView();
				}
			}
			FormManagerDesignUtils.deselectAll(getCurrentEditor().getTopParent());
		}
	}

	/**
	 * Selects the cell immediately above the current cell
	 */
	public class UpAction extends AbstractAction {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = getCurrentEditor().getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int row = gc.getRow();
					int col = gc.getColumn();
					row--;
					if (row < 1) {
						row = view.getRowCount();
						col--;
						if (col < 1)
							col = view.getColumnCount();
					}
					GridComponent next = view.getGridComponent(col, row);
					gc.setSelected(false);
					next.setSelected(true);
				}
			}
		}
	}

	/**
	 * Handles popup mouse trigger events for form.
	 */
	public class ContextMouseListener extends MouseAdapter {
		private void handlePopup(MouseEvent evt) {
			if (evt.isPopupTrigger()) {
				m_popup.show((java.awt.Component) evt.getSource(), evt.getX(), evt.getY());
			}
		}

		public void mousePressed(MouseEvent e) {
			handlePopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			handlePopup(e);
		}
	}

}
