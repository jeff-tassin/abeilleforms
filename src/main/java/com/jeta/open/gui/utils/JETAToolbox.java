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

package com.jeta.open.gui.utils;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Constructor;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.registry.JETARegistry;

/**
 * Some common utility functions for GUI stuff.
 * 
 * @author Jeff Tassin
 */
public class JETAToolbox {
	/** used for calculating average char widths */
	private static final String AVG_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static final String APPLICATION_FRAME = "application.frame.window";
	public static final String WINDOWS_LF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	/**
	 * Adds an array or collection of items to a combo box
	 */
	public static void addItems(JComboBox cbox, Object[] items) {
		assert (cbox != null);
		if (items == null || cbox == null)
			return;

		for (int index = 0; index < items.length; index++) {
			cbox.addItem(items[index]);
		}
	}

	/**
	 * Converts an Array of JComponents to an array of Components
	 */
	private static Component[] toComponentArray(JComponent[] components) {
		Component[] result = new Component[components.length];
		for (int index = 0; index < components.length; index++) {
			result[index] = components[index];
		}
		return result;
	}

	/**
	 * Calculates the width of an average string of text.
	 * 
	 * @param comp
	 *            the component whose font metrics will determine the width
	 * @param numCharacters
	 *            the number of characters to use to calculate the length
	 */
	public static int calculateAverageTextWidth(Component comp, int numCharacters) {
		if (comp == null)
			return 0;

		Font f = comp.getFont();
		FontMetrics metrics = comp.getFontMetrics(f);
		return metrics.stringWidth(AVG_STR) * numCharacters / AVG_STR.length();
	}

	/**
	 * Sets the size of the dimension to some reasonable value. If the given
	 * dimension is larger than the screen size, then we set the window to 80%
	 * of the screen size. Otherwise, we leave it alone
	 * 
	 * @param d
	 *            the dimension of the window to set
	 */
	public static void calculateReasonableComponentSize(Dimension d) {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		if (d.width > screensize.width)
			d.width = screensize.width * 8 / 10;

		if (d.height > screensize.height)
			d.height = screensize.height * 8 / 10;
	}

	/**
	 * This method centers and sizes a frame window on the screen. The caller
	 * must pass the x and y percentages of screen width/height.
	 */
	public static void centerFrame(Window frame, float xpctWidth, float ypctWidth) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int frame_width = (int) (screenSize.width * xpctWidth);
		int frame_height = (int) (screenSize.height * ypctWidth);
		int left = (screenSize.width - frame_width) / 2;
		int top = (screenSize.height - frame_height) / 2;
		frame.setBounds(left, top, frame_width, frame_height);
	}

	/**
	 * This method centers a window window on the screen. The caller must pass
	 * the x and y percentages of screen width/height.
	 */
	public static void centerWindow(Window frame) {
		float width = (float) frame.getWidth();
		float height = (float) frame.getHeight();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		float pctwidth = width / (float) screenSize.getWidth();
		float pctheight = height / (float) screenSize.getHeight();
		centerFrame(frame, pctwidth, pctheight);
	}

	/**
	 * This method centers a window window and changes the width on the screen.
	 * The caller must pass the x percentages of screen width. The height
	 * remains unchanged
	 */
	public static void centerWindowChangeWidth(Window frame, float xpctWidth) {
		float height = (float) frame.getHeight();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		float pctheight = height / (float) screenSize.getHeight();
		centerFrame(frame, xpctWidth, pctheight);
	}

	/**
	 * Copies to the clipboard the given string
	 */
	public static void copyToClipboard(String str) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = kit.getSystemClipboard();
		StringSelection ss = new StringSelection(str);
		clipboard.setContents(ss, ss);
	}

	/**
	 * This method creates a dialog of the type class. It assumes that the
	 * dialog will have constructors that have the following form: ctor( JDialog
	 * owner, boolean bmodal ) ctor( Frame owner, boolean bmodal )
	 * 
	 * This method interogates the owner object. If this object is a JDialog or
	 * Frame, then we call the appropriate constructor for the class. If this
	 * object is a JComponent, then we get the objects owner window and
	 * determine if that is a JDialog or Frame.
	 * 
	 * @param dlgclass
	 *            the class of the dialog we want to create
	 * @param owner
	 *            the owner of the new dialog. This object can be indirectly an
	 *            owner. For example, you can pass in a JPanel as the owner.
	 *            This method will detect this and get the appropriate owner of
	 *            the JPanel
	 * @param bModal
	 *            true if you want the dialog to be modal
	 * 
	 */
	public static Dialog createDialog(Class dlgclass, Component owner, boolean bModal) {
		Class[] cparams = new Class[2];
		Object[] params = new Object[2];

		if (owner == null) {
			// try the registry
			Object comp = JETARegistry.lookup(APPLICATION_FRAME);
			if (comp instanceof Component)
				owner = (Component) comp;
		}

		if (owner instanceof Dialog) {
			cparams[0] = Dialog.class;
			params[0] = owner;
		}
		else if (owner instanceof Frame) {
			cparams[0] = Frame.class;
			params[0] = owner;
		}
		else {
			if (owner == null) {
				cparams[0] = Frame.class;
				params[0] = null;
			}
			else {
				Window win = SwingUtilities.getWindowAncestor(owner);
				if (win instanceof Dialog) {
					cparams[0] = Dialog.class;
					params[0] = win;
				}
				else if (win instanceof Frame) {
					cparams[0] = Frame.class;
					params[0] = win;
				}
				else {
					cparams[0] = Frame.class;
					params[0] = null;
				}
			}
		}
		cparams[1] = boolean.class;
		params[1] = Boolean.valueOf(bModal);

		try {
			Constructor ctor = dlgclass.getConstructor(cparams);
			return (Dialog) ctor.newInstance(params);
		} catch (Exception e) {
			System.out.println("Unable to construct dialg   parent class: " + cparams[0] + "  owner = " + owner);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the thickness (in pixels) of the border on a frame or dialog
	 *         window Currently, this is hard coded until I figure out a way to
	 *         get this value.
	 */
	public static int getFrameBorderThickness() {
		if (isOSX())
			return 0;
		else
			return 4; // temporary
	}

	/**
	 * @return the height (in pixels) of the titlebar on a frame or dialog
	 *         window Currently, this is hard coded until I figure out a way to
	 *         get this value.
	 */
	public static int getTitleBarHeight() {
		return 20;
	}

	/**
	 * Creates and displays a model JETADialog using the given view as the
	 * content pane. The dialog is centered on the screen and it's modal parent
	 * is set to null.
	 * 
	 */
	public static JETADialog invokeDialog(JETAPanel view, Component owner, String title) {
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, owner, true);
		dlg.setPrimaryPanel(view);
		dlg.setTitle(title);
		dlg.setSize(dlg.getPreferredSize());
		dlg.showCenter();
		return dlg;
	}

	/**
	 * Creates and displays a model JETADialog using the given view as the
	 * content pane. The dialog is centered on the screen and it's modal parent
	 * is set to null.
	 * 
	 * @param view
	 *            the main content panel for the dialog
	 * @param title
	 *            the dialog's title
	 * @param initialFocus
	 *            the component that will have the initial focus
	 */
	public static JETADialog invokeDialog(JETAPanel view, Component owner, String title, JComponent initialFocus) {
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, owner, true);
		dlg.setPrimaryPanel(view);
		dlg.setTitle(title);
		dlg.setSize(dlg.getPreferredSize());
		dlg.setInitialFocusComponent(initialFocus);
		dlg.showCenter();
		return dlg;
	}

	/**
	 * Returns true if running OS X Aqua look and feel.
	 */
	public static boolean isAquaLookAndFeel() {
		javax.swing.LookAndFeel laf = javax.swing.UIManager.getLookAndFeel();
		return laf.getName().startsWith("Mac OS X");
	}

	/**
	 * @return true if running Mac OS X
	 */
	public static boolean isOSX() {
		try {
			return (System.getProperty("mrj.version") != null);
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * @return true if running Windows
	 */
	public static boolean isWindows() {
		try {
			String result = System.getProperty("os.name");
			if (result != null) {
				result = result.toLowerCase();
				if (result.indexOf("windows") >= 0)
					return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * @return true if we are currently in the windows look and feel. This L&F
	 *         has different focus issues with the popup
	 */
	public static boolean isWindowsLookAndFeel() {
		return (WINDOWS_LF.equals(javax.swing.UIManager.getLookAndFeel().getClass().getName()));
	}

	/**
	 * Sets the size of the given window to the passed in dimension. However, if
	 * the given dimension is larger than the screen size, then we set the
	 * window to 80% of the screen size
	 * 
	 * @param window
	 *            the window whose size we are setting
	 * @param d
	 *            the dimension of the window to set
	 */
	public static void setReasonableWindowSize(Component window, Dimension d) {
		calculateReasonableComponentSize(d);
		window.setSize(d);
	}
}
