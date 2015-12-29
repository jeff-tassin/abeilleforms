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

import java.awt.Dialog;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.ext.ListCompletionView;

/**
 * 
 * @author Miloslav Metelka
 * @version 1.0
 */

public class JavaFastOpen implements ActionListener {

	private static final int TIMER_DELAY = 500;

	private JavaFastOpenPanel panel;

	private ListCellRenderer cellRenderer;

	private JList resultList;

	private Dialog dialog;

	private JButton[] buttons;

	private Evaluator evaluator;

	private static final int SET_EXP = 1;
	private static final int POPULATE_LIST = 2;
	private static final int OPEN = 3;

	/**
	 * Reference to instance of this class. Used for showing only one FastOpen
	 * dialog
	 */
	protected static JavaFastOpen fastOpen = null;

	public JavaFastOpen() {
	}

	public void setDialogVisible(boolean visible) {
		if (dialog == null) {
			dialog = createDialog();
		}

		dialog.setVisible(visible);

		if (visible) {
			requestFocus();

		}
		else {

			if (evaluator != null) {
				evaluator.breakLoop();
			}

			dialog.dispose();
			fastOpen = null;
		}
	}

	protected void openSource(Object item) {
	}

	/** Move focus to the opened window */
	public void requestFocus() {
		if (dialog == null)
			return;
		dialog.requestFocus();
		getPanel().popupNotify();
	}

	protected ListCellRenderer createCellRenderer() {
		JCCellRenderer rr = new JCCellRenderer();
		rr.setClassDisplayFullName(true);
		return rr;
	}

	protected JList createResultList() {
		JList list = new ListCompletionView(getCellRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					actionPerformed(new ActionEvent(getButtons()[0], 0, ""));
				}
			}
		});

		return list;
	}

	private JButton[] getButtons() {
		if (buttons == null) {
			buttons = new JButton[] { new JButton(LocaleSupport.getString("JFO_openSourceButton", "Open Source")), // NOI18N
					new JButton(LocaleSupport.getString("JFO_closeButton", "Close")) // NOI18N
			};
			buttons[0].setEnabled(false);

			String mnemonic = LocaleSupport.getString("JFO_openSourceButtonMnemonic", "O"); // NOI18N
			if (mnemonic != null && mnemonic.length() > 0) {
				buttons[0].setMnemonic(mnemonic.charAt(0));
			}

			mnemonic = LocaleSupport.getString("JFO_closeButtonMnemonic", "C"); // NOI18N
			if (mnemonic != null && mnemonic.length() > 0) {
				buttons[1].setMnemonic(mnemonic.charAt(0));
			}
			buttons[0].getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFO_openSourceButton")); // NOI18N
			buttons[1].getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_JFO_closeButton")); // NOI18N
		}

		return buttons;
	}

	private Dialog createDialog() {
		String title = LocaleSupport.getString("JFO_title", "Open Java Source");

		Dialog dialog = DialogSupport.createDialog(title, getPanel(), false, getButtons(), false, 0, 1, this);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JavaFastOpen.this.setDialogVisible(false);
			}
		});

		return dialog;
	}

	private JavaFastOpenPanel getPanel() {
		if (panel == null) {
			panel = new JavaFastOpenPanel(this);
		}
		return panel;
	}

	ListCellRenderer getCellRenderer() {
		if (cellRenderer == null) {
			cellRenderer = createCellRenderer();
		}
		return cellRenderer;
	}

	JList getResultList() {
		if (resultList == null) {
			resultList = createResultList();
		}
		return resultList;
	}

	private Evaluator getEvaluator() {
		if (evaluator == null) {
			evaluator = new Evaluator(0);
			evaluator.start();
		}
		return evaluator;
	}

	public void setSearchText(String text) {
		getPanel().setSearchText(text);
		postUpdate();
	}

	void postUpdate() {
		SwingUtilities.invokeLater(new Evaluator(SET_EXP));
	}

	List evaluate(String exp) {
		List ret = Collections.EMPTY_LIST;

		if (exp != null && exp.length() > 0) {
			JCFinder finder = JavaCompletion.getFinder();
			ret = finder.findClasses(null, exp, false);
			// remove innerclasses
			Iterator it = ret.iterator();
			while (it.hasNext()) {
				JCClass cls = (JCClass) it.next();
				if (cls.getName().indexOf('.') >= 0) {
					it.remove();
				}
			}
		}

		return ret;
	}

	void populate(List result) {
		if (result != null) {
			if (getResultList() instanceof ListCompletionView) {
				SwingUtilities.invokeLater(new Evaluator(POPULATE_LIST, result));
			}
		}
	}

	void scrollDown() {
		int currIndex = resultList.getSelectedIndex();
		int maxIndex = resultList.getModel().getSize() - 1;
		if (currIndex == -1 || currIndex == maxIndex)
			return;
		resultList.setSelectedIndex(currIndex + 1);
		Rectangle r = resultList.getCellBounds(currIndex + 1, currIndex + 1);
		if (r != null) {
			resultList.scrollRectToVisible(r);
		}
	}

	void scrollUp() {
		int currIndex = resultList.getSelectedIndex();
		if (currIndex == -1 || currIndex == 0)
			return;
		resultList.setSelectedIndex(currIndex - 1);
		Rectangle r = resultList.getCellBounds(currIndex - 1, currIndex - 1);
		if (r != null) {
			resultList.scrollRectToVisible(r);
		}
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();

		if (src == buttons[0] || src == panel) { // Open button
			getEvaluator().postOpen();
		}
		else {
			setDialogVisible(false);
		}
	}

	private void open() {
		SwingUtilities.invokeLater(new Evaluator(OPEN));
	}

	private class Evaluator extends Thread {

		private int opID;

		private List result;

		private String exp;

		private String lastExp;

		private boolean open;

		private boolean exit;

		Evaluator(int opID) {
			this(opID, null);
		}

		Evaluator(int opID, List result) {
			this.opID = opID;
			this.result = result;
		}

		synchronized void setExp(String exp) {
			this.exp = exp;
		}

		synchronized void postOpen() {
			this.open = true;
		}

		void breakLoop() {
			exit = true;
		}

		public void run() {
			switch (opID) {
			case SET_EXP:
				String text = getPanel().getSearchText();

				// We enable immediately after first typed character
				if (getEvaluator().lastExp == null && text.length() > 0) {
					getButtons()[0].setEnabled(true);
				}

				getEvaluator().setExp(text);
				return;

			case POPULATE_LIST:
				((ListCompletionView) getResultList()).setResult(result);
				getResultList().setSelectedIndex(0);
				getButtons()[0].setEnabled(result.size() > 0);

				if (dialog instanceof JDialog) {
					JDialog jd = (JDialog) dialog;
					jd.getRootPane().setDefaultButton(getButtons()[0].isEnabled() ? getButtons()[0] : null);
				}
				return;

			case OPEN:
				int selIndex = getResultList().getSelectedIndex();
				if (selIndex >= 0) {
					openSource(getResultList().getModel().getElementAt(selIndex));
					setDialogVisible(false);
				}
				return;
			}

			// regular evaluator behavior
			try {
				while (!exit) {
					if (exp != null && !exp.equals(lastExp)) {
						lastExp = exp;
						if (lastExp != null) {
							List result = evaluate(lastExp);

							if (lastExp == exp) {
								populate(result);
							}
						}

					}

					synchronized (Evaluator.this) {
						if (exp != null && exp.equals(lastExp) && open) {
							JavaFastOpen.this.open();
							this.open = false;
						}
					}

					sleep(200);
				}

			} catch (InterruptedException e) {
			}
		}

	}

}
