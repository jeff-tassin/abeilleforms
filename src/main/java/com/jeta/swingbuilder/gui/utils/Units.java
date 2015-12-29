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

package com.jeta.swingbuilder.gui.utils;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

import com.jgoodies.forms.util.AbstractUnitConverter;

/**
 */
public class Units extends AbstractUnitConverter {
	private static final int DTP_RESOLUTION = 72;

	private static Units instance;

	/**
	 * Holds the string that is used to compute the average character width. By
	 * default this is just &quot;X&quot;.
	 */
	private String averageCharWidthTestString = "X";

	/**
	 * Holds the font that is used to compute the global dialog base units. By
	 * default it is lazily created in method #getDefaultDialogFont, which in
	 * turn looks up a font in method #lookupDefaultDialogFont.
	 */
	private Font defaultDialogFont;

	/**
	 * Holds the cached global dialog base units that are used if a component is
	 * not (yet) available - for example in a Border.
	 */
	private DialogBaseUnits cachedGlobalDialogBaseUnits = computeGlobalDialogBaseUnits();

	/**
	 * Maps <code>FontMetrics</code> to horizontal dialog base units. This is
	 * a second-level cache, that stores dialog base units for a
	 * <code>FontMetrics</code> object.
	 */
	private Map cachedDialogBaseUnits = new HashMap();

	public double pixelAsInch(int pixels, Component component) {
		return pixelAsInch(pixels, getScreenResolution(component));
	}

	public double pixelAsMillimeter(int pixels, Component component) {
		return pixelAsMillimeter(pixels, getScreenResolution(component));
	}

	public double pixelAsCentimeter(int pixels, Component component) {
		return pixelAsCentimeter(pixels, getScreenResolution(component));
	}

	public int pixelAsPoint(int pixels, Component component) {
		return pixelAsPoint(pixels, getScreenResolution(component));
	}

	public int pixelAsDialogUnitX(int pixels, Component c) {
		return pixelAsDialogUnitX(pixels, getDialogBaseUnitsX(c));
	}

	public int pixelAsDialogUnitY(int pixels, Component c) {
		return pixelAsDialogUnitY(pixels, getDialogBaseUnitsY(c));
	}

	protected final double pixelAsInch(int pixels, int dpi) {
		return (double) pixels / (double) dpi;
	}

	protected final double pixelAsMillimeter(int pixels, int dpi) {
		return (pixels * 254.0) / (10.0 * dpi);
	}

	protected final double pixelAsCentimeter(int pixels, int dpi) {
		return (pixels * 254.0) / (100.0 * dpi);
	}

	protected final int pixelAsPoint(int pixels, int dpi) {
		return Math.round((pixels * DTP_RESOLUTION) / dpi);
	}

	protected int pixelAsDialogUnitX(int pixels, double dialogBaseUnitsX) {
		return (int) Math.round(pixels * 4 / dialogBaseUnitsX);
	}

	protected int pixelAsDialogUnitY(int pixels, double dialogBaseUnitsY) {
		return (int) Math.round(pixels * 8 / dialogBaseUnitsY);
	}

	private Units() {
		UIManager.addPropertyChangeListener(new LAFChangeHandler());
	}

	/**
	 * Lazily instantiates and returns the sole instance.
	 */
	public static Units getInstance() {
		if (instance == null) {
			instance = new Units();
		}
		return instance;
	}

	/**
	 * Returns the string used to compute the average character width. By
	 * default it is initialized to &quot;X&quot;.
	 * 
	 * @return the test string used to compute the average character width
	 */
	public String getAverageCharacterWidthTestString() {
		return averageCharWidthTestString;
	}

	/**
	 * Lazily creates and returns the dialog font used to compute the dialog
	 * base units.
	 * 
	 * @return the font used to compute the dialog base units
	 */
	public Font getDefaultDialogFont() {
		if (defaultDialogFont == null) {
			defaultDialogFont = lookupDefaultDialogFont();
		}
		return defaultDialogFont;
	}

	/**
	 * Sets a dialog font that will be used to compute the dialog base units.
	 * 
	 * @param newFont
	 *            the default dialog font to be set
	 */
	public void setDefaultDialogFont(Font newFont) {
		Font oldFont = defaultDialogFont; // Don't use the getter
		defaultDialogFont = newFont;
	}

	/**
	 * Answers the cached or computed horizontal dialog base units.
	 * 
	 * @param component
	 *            a Component that provides the font and graphics
	 * @return the horizontal dialog base units
	 */
	protected double getDialogBaseUnitsX(Component component) {
		return getDialogBaseUnits(component).x;
	}

	/**
	 * Answers the cached or computed vertical dialog base units for the given
	 * component.
	 * 
	 * @param component
	 *            a Component that provides the font and graphics
	 * @return the vertical dialog base units
	 */
	protected double getDialogBaseUnitsY(Component component) {
		return getDialogBaseUnits(component).y;
	}

	// Compute and Cache Global and Components Dialog Base Units **************

	/**
	 * Lazily computes and answer the global dialog base units. Should be
	 * re-computed if the l&amp;f, platform, or screen changes.
	 */
	private DialogBaseUnits getGlobalDialogBaseUnits() {
		if (cachedGlobalDialogBaseUnits == null) {
			cachedGlobalDialogBaseUnits = computeGlobalDialogBaseUnits();
		}
		return cachedGlobalDialogBaseUnits;
	}

	/**
	 * Looks up and answers the dialog base units for the given component. In
	 * case the component is <code>null</code> the global dialog base units
	 * are answered.
	 * <p>
	 * Before we compute the dialog base units, we check wether they have been
	 * computed and cached before - for the same component
	 * <code>FontMetrics</code>.
	 * 
	 * @param c
	 *            the component that provides the graphics object
	 * @return
	 */
	private DialogBaseUnits getDialogBaseUnits(Component c) {
		if (c == null) { // || (font = c.getFont()) == null) {
			return getGlobalDialogBaseUnits();
		}
		FontMetrics fm = c.getFontMetrics(getDefaultDialogFont());
		DialogBaseUnits dialogBaseUnits = (DialogBaseUnits) cachedDialogBaseUnits.get(fm);
		if (dialogBaseUnits == null) {
			dialogBaseUnits = computeDialogBaseUnits(fm);
			cachedDialogBaseUnits.put(fm, dialogBaseUnits);
		}
		return dialogBaseUnits;
	}

	/**
	 * Computes and answers the horizontal dialog base units. Honors the font,
	 * font size and resolution.
	 * <p>
	 * Implementation Note: 14dluY map to 22 pixel for 8pt Tahoma on 96 dpi. I
	 * could not yet manage to compute the Microsoft compliant font height.
	 * Therefore this method adds a correction value that seems to work well
	 * with the vast majority of desktops. Anyway, I plan to revise this, as
	 * soon as I have more information about the original computation for
	 * vertical dialog base units.
	 * 
	 * @return the horizontal and vertical dialog base units
	 */
	private DialogBaseUnits computeDialogBaseUnits(FontMetrics metrics) {
		double averageCharWidth = computeAverageCharWidth(metrics, averageCharWidthTestString);
		int ascent = metrics.getAscent();
		double height = ascent > 14 ? ascent : ascent + (15 - ascent) / 3;
		DialogBaseUnits dialogBaseUnits = new DialogBaseUnits(averageCharWidth, height);
		return dialogBaseUnits;
	}

	/**
	 * Computes the global dialog base units. The current implementation assumes
	 * a fixed 8pt font and on 96 or 120 dpi. A better implementation should ask
	 * for the main dialog font and should honor the current screen resolution.
	 * <p>
	 * Should be re-computed if the l&amp;f, platform, or screen changes.
	 */
	private DialogBaseUnits computeGlobalDialogBaseUnits() {
		Font dialogFont = getDefaultDialogFont();
		FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(dialogFont);
		DialogBaseUnits globalDialogBaseUnits = computeDialogBaseUnits(metrics);
		return globalDialogBaseUnits;
	}

	/**
	 * Looks up and returns the font used by buttons. First, tries to request
	 * the button font from the UIManager; if this fails a JButton is created
	 * and asked for its font.
	 * 
	 * @return the font used for a standard button
	 */
	private Font lookupDefaultDialogFont() {
		Font buttonFont = UIManager.getFont("Button.font");
		return buttonFont != null ? buttonFont : new javax.swing.JButton().getFont();
	}

	/**
	 * Invalidates the caches. Resets the global dialog base units and clears
	 * the Map from <code>FontMetrics</code> to dialog base units. This is
	 * invoked after a change of the look&amp;feel.
	 */
	private void invalidateCaches() {
		cachedGlobalDialogBaseUnits = null;
		cachedDialogBaseUnits.clear();
	}

	private static class DialogBaseUnits {
		final double x;
		final double y;

		DialogBaseUnits(double dialogBaseUnitsX, double dialogBaseUnitsY) {
			this.x = dialogBaseUnitsX;
			this.y = dialogBaseUnitsY;
		}

		public String toString() {
			return "DBU(x=" + x + "; y=" + y + ")";
		}
	}

	// Listens to changes of the Look and Feel and invalidates a cache
	private class LAFChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			invalidateCaches();
		}
	}
}
