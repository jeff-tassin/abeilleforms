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

package com.jeta.forms.gui.common;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JMenu;

import com.jeta.open.registry.JETARegistry;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Utility methods for the form builder.
 * 
 * @author Jeff Tassin
 */
public class FormUtils {
	/**
	 * temporary - for debugging only
	 */
	private static int m_count = 0;

	static char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	/**
	 * Creates a unique 8 digit UI.
	 */
	static String _createUID() {
		java.rmi.server.UID uid = new java.rmi.server.UID();
		// we need to strip off any non alphanumeric characters because we use
		// the UID as
		// file and directory names
		StringBuffer sbuff = new StringBuffer(uid.toString());
		for (int index = 0; index < sbuff.length(); index++) {
			char c = sbuff.charAt(index);
			if ((c < '0') || (c > '9' && c < 'A') || (c > 'Z' && c < 'a') || (c > 'z')) {
				int cindex = (int) (Math.random() * 26.0);
				sbuff.setCharAt(index, letters[cindex]);
			}
		}

		char c = sbuff.charAt(0);
		if (c < 65 || c > 90) {
			int cindex = (int) (Math.random() * 26.0);
			sbuff.setCharAt(0, letters[cindex]);
		}
		return sbuff.toString();
	}

	/**
	 * Creates a unique id in the application.
	 * 
	 * @return a unique id used to identify forms in the application.
	 */
	public static String createUID() {
		if (isDebug()) {
			/**
			 * If we are debugging, return a user-friendly string that is easy
			 * to read.
			 */
			m_count++;
			java.util.Calendar c = java.util.Calendar.getInstance();
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("EEE, MMM d, yyyy - HH:mm:ss");
			return String.valueOf(m_count) + "." + format.format(c.getTime());
		}
		else {
			return _createUID();
		}
	}

	/**
	 * Converts any / or \ characters to the correct path separator character
	 * for the current operating system.
	 */
	public static String fixPath(String path) {
		if (path == null)
			return null;

		char sep = '/';

		if (java.io.File.separatorChar == '/')
			sep = '\\';

		return path.replace(sep, java.io.File.separatorChar);
	}

	/**
	 * @return a reasonable default size for the given units
	 */
	public static String getReasonableSize(String units) {
		if ("DLU".equalsIgnoreCase(units))
			return "12";
		else if ("PX".equalsIgnoreCase(units))
			return "24";
		else if ("PT".equalsIgnoreCase(units))
			return "24";
		else if ("IN".equalsIgnoreCase(units))
			return "0.5";
		else if ("MM".equalsIgnoreCase(units))
			return "10";
		else if ("CM".equalsIgnoreCase(units))
			return "1";
		else {
			safeAssert(false);
			return "10";
		}
	}

	/**
	 * @return true if the environment is currently in design mode or runtime
	 *         mode.
	 */
	public static boolean isDesignMode() {
		Boolean result = (Boolean) JETARegistry.lookup("AbeilleForms.designMode");
		return (Boolean.TRUE.equals(result));
	}

	/**
	 * Debugging flag
	 */
	public static boolean isDebug() {
		try {
			String result = System.getProperty("jeta1.debug");
			return (result != null && result.equals("true"));
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * Returns true if the forms are running outside the designer. That is, the
	 * forms libarary is being used by some other application other than the
	 * designer. If this value is true, then isDesignMode must be false.
	 */
	public static boolean isRuntime() {
		Boolean result = (Boolean) JETARegistry.lookup("AbeilleForms.runTime");
		if (result == null)
			return true;
		return (Boolean.TRUE.equals(result));
	}

	/**
	 * @return true if the units are PX, PT, or DLU
	 */
	public static boolean isIntegralUnits(String units) {
		return ("PX".equalsIgnoreCase(units) || "PT".equalsIgnoreCase(units) || "DLU".equalsIgnoreCase(units));
	}

	/**
	 * @return true if the units are valid
	 */
	public static boolean isValidUnits(String units) {
		return ("DLU".equalsIgnoreCase(units) || "PX".equalsIgnoreCase(units) || "PT".equalsIgnoreCase(units) || "IN".equalsIgnoreCase(units)
				|| "MM".equalsIgnoreCase(units) || "CM".equalsIgnoreCase(units));
	}

	/**
	 * Sets the design mode flag for the environment. This should not be called
	 * outside of the designer. This flag changes momentarily to false when the
	 * designer shows a preview of a form.
	 * 
	 * @param bdesign
	 *            true if the environment should be set to to design mode. false
	 *            if runtime mode.
	 */
	public static void setDesignMode(boolean bdesign) {
		JETARegistry.rebind("AbeilleForms.designMode", Boolean.valueOf(bdesign));
	}

	public static String fromAlignment(CellConstraints.Alignment align) {
		if (align == CellConstraints.DEFAULT)
			return "DEFAULT";
		else if (align == CellConstraints.FILL)
			return "FILL";
		else if (align == CellConstraints.TOP)
			return "TOP";
		else if (align == CellConstraints.BOTTOM)
			return "BOTTOM";
		else if (align == CellConstraints.CENTER)
			return "CENTER";
		else if (align == CellConstraints.LEFT)
			return "LEFT";
		else if (align == CellConstraints.RIGHT)
			return "RIGHT";
		else
			return "DEFAULT";
	}

	/**
	 * Converts a string to an alignment value
	 */
	public static CellConstraints.Alignment toAlignment(String val) {
		if (val.equalsIgnoreCase("DEFAULT"))
			return CellConstraints.DEFAULT;
		else if (val.equalsIgnoreCase("FILL"))
			return CellConstraints.FILL;
		else if (val.equalsIgnoreCase("TOP"))
			return CellConstraints.TOP;
		else if (val.equalsIgnoreCase("BOTTOM"))
			return CellConstraints.BOTTOM;
		else if (val.equalsIgnoreCase("CENTER"))
			return CellConstraints.CENTER;
		else if (val.equalsIgnoreCase("LEFT"))
			return CellConstraints.LEFT;
		else if (val.equalsIgnoreCase("RIGHT"))
			return CellConstraints.RIGHT;
		else {
			safeAssert(false);
			return CellConstraints.DEFAULT;
		}
	}

	/**
	 * @return an encode string that represents the constant size params:
	 *         <integer>integralUnit | <double>doubleUnit
	 */
	public static String toConstantSize(FormSpecDefinition fspec) {
		StringBuffer sbuff = new StringBuffer();
		if (isIntegralUnits(fspec.getConstantUnits())) {
			sbuff.append(Math.round(fspec.getConstantSize()));
		}
		else {
			java.text.DecimalFormat format = new java.text.DecimalFormat("###0.0");
			String sz = format.format(fspec.getConstantSize());
			/** temporary fix to handle european locales */
			sz = sz.replace(',', '.');
			sbuff.append(sz);
		}
		sbuff.append(fspec.getConstantUnits());
		return sbuff.toString();
	}

	/**
	 * @return a property encoded string for this form spec
	 */
	public static String toEncodedString(FormSpecDefinition fspec) {
		StringBuffer sbuff = new StringBuffer();
		sbuff.append(fspec.getAlignment());
		sbuff.append(":");
		if ("CONSTANT".equalsIgnoreCase(fspec.getSizeType())) {
			sbuff.append(toConstantSize(fspec));
		}
		else if ("COMPONENT".equalsIgnoreCase(fspec.getSizeType())) {
			sbuff.append(fspec.getComponentSize());
		}
		else if ("BOUNDED".equalsIgnoreCase(fspec.getSizeType())) {
			sbuff.append(fspec.getBoundedSize());
			sbuff.append("(");
			sbuff.append(toConstantSize(fspec));
			sbuff.append(";");
			sbuff.append(fspec.getComponentSize());
			sbuff.append(")");
		}
		else {
			safeAssert(false);
		}
		sbuff.append(":");
		sbuff.append(fspec.getResize());
		if ("GROW".equalsIgnoreCase(fspec.getResize())) {
			sbuff.append("(");
			double weight = fspec.getResizeWeight();
			if (weight > 1.0)
				weight = 1.0;

			java.text.DecimalFormat format = new java.text.DecimalFormat("0.0");
			String fw = format.format(weight);
			/** temporary fix to handle european locales */
			fw = fw.replace(',', '.');

			sbuff.append(fw);
			sbuff.append(")");

		}
		return sbuff.toString();
	}

	/**
	 * Utility method that updates the look and feel for all components in a
	 * container.
	 */
	public static void updateLookAndFeel(Component c) {
		if (c == null)
			return;

		c.invalidate();
		c.validate();
		c.repaint();

		if (c instanceof JComponent) {
			((JComponent) c).updateUI();
		}

		Component[] children = null;
		if (c instanceof JMenu) {
			children = ((JMenu) c).getMenuComponents();
		}
		else if (c instanceof Container) {
			children = ((Container) c).getComponents();
		}
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				updateLookAndFeel(children[i]);
			}
		}
	}

	public static void safeAssert(boolean assertvalue) {
		if (FormUtils.isDebug()) {
			assert (assertvalue);
		}
	}
}
