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

package org.netbeans.editor;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.JComponent;

/**
 * Immutable class that stores font and foreground and background colors. The
 * coloring can be applied to either the drawing context, component or some
 * other coloring. Applying first checks whether each of the font and the colors
 * is non-null. If it's null the particular thing is left unchanged. For example
 * to change the background color only the Coloring must be created with the
 * font and foreground color being null. It's also possible to use a more
 * advanced way to apply the coloring by using the modes. There are two modes -
 * font-mode and color-mode. The deafult font-mode simply overwrites the whole
 * font. The font-mode constants allow to change the underlying font by only
 * changing some of the font-name, style or size. The
 * <tt>FONT_MODE_APPLY_STYLE</tt> for example will only apply the style of the
 * coloring's font. The "rest" of the coloring's font (name and size in this
 * case) is not used so there can be any valid values ("Monospaced" and 12 for
 * example). The colors can use the alpha value. It's also possible to set the
 * color of the underline line or the color of the strikethrough line.
 * 
 * Generally the editor uses two sets of the colorings to colorize the text.
 * They are component-set and printing-set. The component-set is used for the
 * editor component while the printing-set is used solely for colorizing the
 * printed text.
 * 
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public final class Coloring implements java.io.Serializable {

	/**
	 * Apply only the name from the font. This flag can be combined with other
	 * FONT_MODE flags. When using solely this constant the "rest" of the
	 * coloring's font (style and size in this case) is unused so there can be
	 * any valid values used to create the font (Font.PLAIN and 12 for example).
	 */
	public static final int FONT_MODE_APPLY_NAME = 1;

	/**
	 * Apply only the style from the font. This flag can be combined with other
	 * FONT_MODE flags. When using solely this constant the "rest" of the
	 * coloring's font (name and size in this case) is unused so there can be
	 * any valid values used ("Monospaced" and 12 for example).
	 */
	public static final int FONT_MODE_APPLY_STYLE = 2;

	/**
	 * Apply only the size from the font. This flag can be combined with other
	 * FONT_MODE flags. When using solely this constant the "rest" of the
	 * coloring's font (name and style in this case) is unused so there can be
	 * any valid values used to create the font ("Monospaced" and Font.PLAIN for
	 * example).
	 */
	public static final int FONT_MODE_APPLY_SIZE = 4;

	/**
	 * Replace the underlying font by the coloring's font. This value is a
	 * binary combination of FONT_MODE_APPLY_NAME, FONT_MODE_APPLY_STYLE,
	 * FONT_MODE_APPLY_SIZE.
	 */
	public static final int FONT_MODE_DEFAULT = FONT_MODE_APPLY_NAME | FONT_MODE_APPLY_STYLE | FONT_MODE_APPLY_SIZE;

	/** Font */
	private Font font;

	/** Font mode */
	private int fontMode;

	/** Foreground color */
	private Color foreColor;

	/** Background color */
	private Color backColor;

	/** Underline line color */
	private Color underlineColor;

	/** Strikethrough line color */
	private Color strikeThroughColor;

	/**
	 * Cache holding the [original-font, derived-font] pairs and also original
	 * [fore-color, derived-fore-color] pairs. This helps to avoid the
	 * repetitive computations of the derived font and foreground-color (the
	 * backround-color is handled through different cache) and it also avoids
	 * the repetitive creations of the derived font and color objects.
	 */
	private transient HashMap fontAndForeColorCache;

	/** Cache holding the [back-color, derived-back-color] pairs */
	private transient HashMap backColorCache;

	static final long serialVersionUID = -1382649127124476675L;

	/** Construct empty coloring */
	public Coloring() {
		this(null, null, null);
	}

	/** Construct new coloring */
	public Coloring(Font font, Color foreColor, Color backColor) {
		this(font, FONT_MODE_DEFAULT, foreColor, backColor, null, null);
	}

	/** Construct new coloring */
	public Coloring(Font font, int fontMode, Color foreColor, Color backColor) {
		this(font, fontMode, foreColor, backColor, null, null);
	}

	/** Construct new coloring */
	public Coloring(Font font, int fontMode, Color foreColor, Color backColor, Color underlineColor, Color strikeThroughColor) {
		font = (fontMode != 0) ? font : null;
		fontMode = (font != null) ? fontMode : FONT_MODE_DEFAULT;

		this.font = font;
		this.fontMode = fontMode;

		this.foreColor = foreColor;
		this.backColor = backColor;

		this.underlineColor = underlineColor;
		this.strikeThroughColor = strikeThroughColor;

		checkCaches();
	}

	private void checkCaches() {
		// Possibly create the caches
		boolean createFontCache = (font != null && fontMode != 0 && fontMode != FONT_MODE_DEFAULT);
		boolean createForeColorCache = (foreColor != null && hasAlpha(foreColor));
		if (createFontCache || createForeColorCache) {
			fontAndForeColorCache = new HashMap((createFontCache && createForeColorCache) ? 47 : 23);
		}

		if (backColor != null && hasAlpha(backColor)) {
			backColorCache = new HashMap(23);
		}
	}

	/** Whether the color has non-default alpha. */
	private boolean hasAlpha(Color c) {
		return ((c.getRGB() & 0xFF000000) != 0xFF000000);
	}

	/** Getter for font */
	public Font getFont() {
		return font;
	}

	/** Getter for font-mode */
	public int getFontMode() {
		return fontMode;
	}

	/** Getter for foreground color */
	public Color getForeColor() {
		return foreColor;
	}

	/** Getter for background color */
	public Color getBackColor() {
		return backColor;
	}

	/** Getter for underline line color */
	public Color getUnderlineColor() {
		return underlineColor;
	}

	/** Getter for strikethrough line color */
	public Color getStrikeThroughColor() {
		return strikeThroughColor;
	}

	/** Modify the given font according to the font-mode */
	private Font modifyFont(Font f) {
		return new Font(((fontMode & FONT_MODE_APPLY_NAME) != 0) ? font.getName() : f.getName(), ((fontMode & FONT_MODE_APPLY_STYLE) != 0) ? font.getStyle()
				: f.getStyle(), ((fontMode & FONT_MODE_APPLY_SIZE) != 0) ? font.getSize() : f.getSize());
	}

	private Color modifyForeColor(Color underForeColor) {
		int alpha = foreColor.getAlpha(); // alpha 0 - 255
		int fcRGB = foreColor.getRGB();
		int underRGB = underForeColor.getRGB();

		int rgb = (((foreColor.getRed() * alpha + underForeColor.getRed() * (255 - alpha)) / 255) & 0x000000FF) << 16;

		rgb += ((((fcRGB & 0x0000FF00) * alpha + (underRGB & 0x0000FF00) * (255 - alpha)) / 255) & 0x0000FF00) // green
				+ ((((fcRGB & 0x000000FF) * alpha + (underRGB & 0x000000FF) * (255 - alpha)) / 255) & 0x000000FF);// blue

		return new Color(rgb, false);
	}

	private Color modifyBackColor(Color underBackColor) {
		int alpha = backColor.getAlpha(); // alpha 0 - 255
		int bcRGB = backColor.getRGB();
		int underRGB = underBackColor.getRGB();
		int rgb = (((backColor.getRed() * alpha + underBackColor.getRed() * (255 - alpha)) / 255) & 0x000000FF) << 16;

		rgb += ((((bcRGB & 0x0000FF00) * alpha + (underRGB & 0x0000FF00) * (255 - alpha)) / 255) & 0x0000FF00) // green
				+ ((((bcRGB & 0x000000FF) * alpha + (underRGB & 0x000000FF) * (255 - alpha)) / 255) & 0x000000FF);// blue

		return new Color(rgb, false);
	}

	/** Apply this coloring to draw context. */
	public void apply(DrawContext ctx) {
		// Possibly change font
		if (font != null) {
			if (fontMode == FONT_MODE_DEFAULT) {
				ctx.setFont(font);

			}
			else { // non-default font-mode
				Font origFont = ctx.getFont();
				if (origFont != null) {
					synchronized (fontAndForeColorCache) {
						Font f = (Font) fontAndForeColorCache.get(origFont);
						if (f == null) {
							f = modifyFont(origFont);
							fontAndForeColorCache.put(origFont, f);
						}
						ctx.setFont(f);
					}
				}
			}
		}

		// Possibly change fore-color
		if (foreColor != null) {
			if (!hasAlpha(foreColor)) { // doesn't have an alpha
				ctx.setForeColor(foreColor);

			}
			else { // has alpha
				Color origForeColor = ctx.getForeColor();
				if (origForeColor != null) {
					synchronized (fontAndForeColorCache) {
						Color fc = (Color) fontAndForeColorCache.get(origForeColor);
						if (fc == null) {
							fc = modifyForeColor(origForeColor);
							fontAndForeColorCache.put(origForeColor, fc);
						}
						ctx.setForeColor(fc);
					}
				}
			}
		}

		// Possibly change back-color
		if (backColor != null) {
			if (!hasAlpha(backColor)) {
				ctx.setBackColor(backColor);

			}
			else { // non-default back color-mode
				Color origBackColor = ctx.getBackColor();
				if (origBackColor != null) {
					synchronized (backColorCache) {
						Color bc = (Color) backColorCache.get(origBackColor);
						if (bc == null) {
							bc = modifyBackColor(origBackColor);
							backColorCache.put(origBackColor, bc);
						}
						ctx.setBackColor(bc);
					}
				}
			}
		}

		if (underlineColor != null) {
			ctx.setUnderlineColor(underlineColor);
		}

		if (strikeThroughColor != null) {
			ctx.setStrikeThroughColor(strikeThroughColor);
		}
	}

	/**
	 * Apply this coloring to component colors/font. The underline and
	 * strikeThrough line colors have no effect here.
	 */
	public void apply(JComponent c) {
		// Possibly change font
		if (font != null) {
			if (fontMode == FONT_MODE_DEFAULT) {
				c.setFont(font);

			}
			else { // non-default font-mode
				Font origFont = c.getFont();
				if (origFont != null) {
					synchronized (fontAndForeColorCache) {
						Font f = (Font) fontAndForeColorCache.get(origFont);
						if (f == null) {
							f = modifyFont(origFont);
							fontAndForeColorCache.put(origFont, f);
						}
						c.setFont(f);
					}
				}
			}
		}

		// Possibly change fore-color
		if (foreColor != null) {
			if (!hasAlpha(foreColor)) {
				c.setForeground(foreColor);

			}
			else { // non-default fore color-mode
				Color origForeColor = c.getForeground();
				if (origForeColor != null) {
					synchronized (fontAndForeColorCache) {
						Color fc = (Color) fontAndForeColorCache.get(origForeColor);
						if (fc == null) {
							fc = modifyForeColor(origForeColor);
							fontAndForeColorCache.put(origForeColor, fc);
						}
						c.setForeground(fc);
					}
				}
			}
		}

		// Possibly change back-color
		if (backColor != null) {
			if (!hasAlpha(backColor)) {
				c.setBackground(backColor);

			}
			else { // non-default back color-mode
				Color origBackColor = c.getBackground();
				if (origBackColor != null) {
					synchronized (backColorCache) {
						Color bc = (Color) backColorCache.get(origBackColor);
						if (bc == null) {
							bc = modifyBackColor(origBackColor);
							backColorCache.put(origBackColor, bc);
						}
						c.setBackground(bc);
					}
				}
			}
		}
	}

	/**
	 * Apply this coloring to some other coloring c and return the resulting
	 * coloring.
	 * 
	 * @param c
	 *            coloring to which this coloring will be applied. If it's null
	 *            then this coloring will be returned as result.
	 */
	public Coloring apply(Coloring c) {
		if (c == null) { // if c is null, return this coloring as result
			return this;
		}

		Font newFont = c.font;
		Color newForeColor = c.foreColor;
		Color newBackColor = c.backColor;
		Color newUnderlineColor = c.underlineColor;
		Color newStrikeThroughColor = c.strikeThroughColor;

		// Possibly change font
		if (font != null) {
			if (fontMode == FONT_MODE_DEFAULT) {
				newFont = font;

			}
			else { // non-default font-mode
				if (newFont != null) {
					synchronized (fontAndForeColorCache) {
						Font f = (Font) fontAndForeColorCache.get(newFont);
						if (f == null) {
							f = modifyFont(newFont);
							fontAndForeColorCache.put(newFont, f);
						}
						newFont = f;
					}
				}
			}
		}

		// Possibly change fore-color
		if (foreColor != null) {
			if (!hasAlpha(foreColor)) {
				newForeColor = foreColor;

			}
			else { // non-default fore color-mode
				if (newForeColor != null) {
					synchronized (fontAndForeColorCache) {
						Color fc = (Color) fontAndForeColorCache.get(newForeColor);
						if (fc == null) {
							fc = modifyForeColor(newForeColor);
							fontAndForeColorCache.put(newForeColor, fc);
						}
						newForeColor = fc;
					}
				}
			}
		}

		// Possibly change back-color
		if (backColor != null) {
			if (!hasAlpha(backColor)) {
				newBackColor = backColor;

			}
			else { // non-default back color-mode
				newBackColor = backColor;
				if (newBackColor != null) {
					synchronized (backColorCache) {
						Color bc = (Color) backColorCache.get(newBackColor);
						if (bc == null) {
							bc = modifyBackColor(newBackColor);
							backColorCache.put(newBackColor, bc);
						}
						newBackColor = bc;
					}
				}
			}
		}

		if (underlineColor != null) {
			newUnderlineColor = underlineColor;
		}

		if (strikeThroughColor != null) {
			newStrikeThroughColor = strikeThroughColor;
		}

		if (c.fontMode != FONT_MODE_DEFAULT || newFont != c.font // currently
																	// only
																	// equality
				|| newForeColor != c.foreColor // currently only equality
				|| newBackColor != c.backColor // currently only equality
				|| newUnderlineColor != c.underlineColor // currently only
															// equality
				|| newStrikeThroughColor != c.strikeThroughColor // currently
																	// only
																	// equality
		) {
			return new Coloring(newFont, FONT_MODE_DEFAULT, newForeColor, newBackColor, newUnderlineColor, newStrikeThroughColor);
		}
		else {
			return c; // return original coloring
		}

	}

	/** All font, foreColor and backColor are the same. */
	public boolean equals(Object o) {
		if (o instanceof Coloring) {
			Coloring c = (Coloring) o;
			return ((font == null && c.font == null) || (font != null && font.equals(c.font)))
					&& (fontMode == c.fontMode)
					&& ((foreColor == null && c.foreColor == null) || (foreColor != null && foreColor.equals(c.foreColor)))
					&& ((backColor == null && c.backColor == null) || (backColor != null && backColor.equals(c.backColor)))
					&& ((underlineColor == null && c.underlineColor == null) || (underlineColor != null && underlineColor.equals(c.underlineColor)))
					&& ((strikeThroughColor == null && c.strikeThroughColor == null) || (strikeThroughColor != null && strikeThroughColor
							.equals(c.strikeThroughColor)));
		}
		return false;
	}

	public int hashCode() {
		return font.hashCode() ^ foreColor.hashCode() ^ backColor.hashCode();
	}

	/**
	 * Derive a new coloring by changing the font and leaving the rest of the
	 * coloring (including the font-mode) unchanged.
	 */
	public static Coloring changeFont(Coloring c, Font newFont) {
		return changeFont(c, newFont, c.getFontMode());
	}

	/**
	 * Derive a new coloring by changing the font and font-mode and leaving the
	 * rest of the coloring unchanged.
	 */
	public static Coloring changeFont(Coloring c, Font newFont, int newFontMode) {
		if ((newFont == null && c.font == null) || (newFont != null && newFont.equals(c.font) && c.fontMode == newFontMode)) {
			return c;
		}

		return new Coloring(newFont, c.foreColor, c.backColor);
	}

	/**
	 * Derive a new coloring by changing the foreground-color and its color-mode
	 * and leaving the rest of the coloring unchanged.
	 */
	public static Coloring changeForeColor(Coloring c, Color newForeColor) {
		if ((newForeColor == null && c.foreColor == null) || (newForeColor != null && newForeColor.equals(c.foreColor))) {
			return c;
		}

		return new Coloring(c.font, newForeColor, c.backColor);
	}

	/**
	 * Derive a new coloring by changing the background-color and its color-mode
	 * and leaving the rest of the coloring unchanged.
	 */
	public static Coloring changeBackColor(Coloring c, Color newBackColor) {
		if ((newBackColor == null && c.backColor == null) || (newBackColor != null && newBackColor.equals(c.backColor))) {
			return c;
		}

		return new Coloring(c.font, c.foreColor, newBackColor);
	}

	private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
		ois.defaultReadObject();

		if (fontMode == 0) {
			fontMode = FONT_MODE_DEFAULT;
		}

		checkCaches();
	}

	public String toString() {
		return "font=" + font + ", fontMode=" + fontMode // NOI18N
				+ ", foreColor=" + foreColor // NOI18N
				+ ", backColor=" + backColor // NOI18N
				+ ", underlineColor=" + underlineColor // NOI18N
				+ ", strikeThroughColor=" + strikeThroughColor; // NOI18N
	}

}
