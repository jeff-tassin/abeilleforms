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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Java completion query specifications
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JCCellRenderer extends JPanel implements ListCellRenderer {

	public static final int CLASS_ICON = 0;
	public static final int INTERFACE_ICON = 4;
	public static final int FIELD_ICON = 8;
	public static final int FIELD_STATIC_ICON = 12;
	public static final int CONSTRUCTOR_ICON = 16;
	public static final int METHOD_ICON = 20;
	public static final int METHOD_STATIC_ICON = 24;
	public static final int PACKAGE_ICON = 28;
	private static final int END_ICON = 29;

	public static final int CLASS_COLOR = 0;
	public static final int INTERFACE_COLOR = 4;
	public static final int FIELD_COLOR = 8;
	public static final int FIELD_STATIC_COLOR = 12;
	public static final int CONSTRUCTOR_COLOR = 16;
	public static final int METHOD_COLOR = 20;
	public static final int METHOD_STATIC_COLOR = 24;
	public static final int PACKAGE_COLOR = 28;
	public static final int KEYWORD_COLOR = 29;
	public static final int TYPE_COLOR = 30;
	public static final int PARAMETER_NAME_COLOR = 31;
	private static final int END_COLOR = 32;

	private static final String THROWS = " throws "; // NOI18N

	private static final String[] frequentWords = new String[] { "", " ", "[]", "(", ")", ", ", "String", THROWS // NOI18N
	};

	static final long serialVersionUID = 4737618682220847017L;

	Map widths;

	Icon[] icons = new Icon[END_ICON];

	Color[] colors = new Color[END_COLOR];

	private FontMetrics fontMetrics;

	private int fontHeight;

	private int ascent;

	private int iconTextGap = 5;

	protected JList list;

	protected Object value;

	protected boolean isSelected;

	protected boolean packageLastNameOnly;

	protected boolean displayStaticWord;

	protected int classDisplayOffset;

	protected boolean classDisplayFullName;

	protected int drawX;

	protected int drawY;

	protected int drawHeight;

	public JCCellRenderer() {
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		for (int i = 0; i < colors.length; i++) {
			colors[i] = Color.black;
		}
		setForeColor(Color.green.darker().darker().darker(), PACKAGE_COLOR);
		setForeColor(Color.red.darker().darker().darker(), CLASS_COLOR);
		setForeColor(Color.darkGray, INTERFACE_COLOR);
		setForeColor(Color.blue.darker(), FIELD_COLOR);
		setForeColor(Color.orange.darker(), CONSTRUCTOR_COLOR);
		setForeColor(Color.red.darker().darker(), METHOD_COLOR);
		setForeColor(Color.darkGray, KEYWORD_COLOR);
		setForeColor(Color.magenta.darker(), PARAMETER_NAME_COLOR);

		setPackageLastNameOnly(true);
		// setDisplayStaticWord(true);
	}

	public void setFont(Font font) {
		super.setFont(font);

		fontMetrics = this.getFontMetrics(font);
		fontHeight = fontMetrics.getHeight();
		ascent = fontMetrics.getAscent();
		if (widths != null) {
			widths.clear();
		}
		else {
			widths = new HashMap();
		}
		for (int i = 0; i < frequentWords.length; i++) {
			storeWidth(frequentWords[i]);
		}
		Iterator i = JavaCompletion.getPrimitiveClassIterator();
		while (i.hasNext()) {
			storeWidth(((JCClass) i.next()).getName());
		}
	}

	public void setIconTextGap(int iconTextGap) {
		this.iconTextGap = iconTextGap;
	}

	public int getIconTextGap() {
		return iconTextGap;
	}

	private void storeWidth(String s) {
		fontMetrics.stringWidth(s);
	}

	public void setIcon(Icon icon, int type) {
		if (type < PACKAGE_ICON) {
			for (int i = 0; i < 4; i++) {
				icons[type + i] = icon;
			}
		}
		else { // package icon
			icons[type] = icon;
		}
	}

	public void setIcon(Icon icon, int type, int level) {
		icons[type + level] = icon;
	}

	public void setForeColor(Color color, int type) {
		if (type < PACKAGE_COLOR) {
			for (int i = 0; i < 4; i++) {
				colors[type + i] = color;
			}
		}
		else { // package icon
			colors[type] = color;
		}
	}

	public void setForeColor(Color color, int type, int level) {
		colors[type + level] = color;
	}

	public void setPackageLastNameOnly(boolean packageLastNameOnly) {
		this.packageLastNameOnly = packageLastNameOnly;
	}

	public void setDisplayStaticWord(boolean displayStaticWord) {
		this.displayStaticWord = displayStaticWord;
	}

	public void setClassDisplayOffset(int classDisplayOffset) {
		this.classDisplayOffset = classDisplayOffset;
	}

	public void setClassDisplayFullName(boolean classDisplayFullName) {
		this.classDisplayFullName = classDisplayFullName;
	}

	protected Color getColor(String s, Color defaultColor) {
		return isSelected ? getForeground() : defaultColor;
	}

	protected int getWidth(String s) {
		Integer i = (Integer) widths.get(s);
		if (i != null) {
			return i.intValue();
		}
		else {
			return fontMetrics.stringWidth(s);
		}
	}

	/** Draw string using the foreground color */
	protected void drawString(Graphics g, String s) {
		if (g != null) {
			g.setColor(getForeground());
		}
		drawStringToGraphics(g, s);
	}

	protected void drawString(Graphics g, String s, int type) {
		drawString(g, s, colors[type]);
	}

	/**
	 * Draw string with given color which is first possibly modified by calling
	 * getColor() method to care about selection etc.
	 */
	protected void drawString(Graphics g, String s, Color c) {
		if (g != null) {
			g.setColor(getColor(s, c));
		}
		drawStringToGraphics(g, s);
	}

	protected void drawStringToGraphics(Graphics g, String s) {
		if (g != null) {
			g.drawString(s, drawX, drawY);
		}
		drawX += getWidth(s);
	}

	/**
	 * Draw the icon if it is valid for the given type. Here the initial drawing
	 * assignments are also done.
	 */
	protected void drawIcon(Graphics g, Icon icon) {
		Insets i = getInsets();
		if (i != null) {
			drawX = i.left;
			drawY = i.top;
		}
		else {
			drawX = 0;
			drawY = 0;
		}

		if (icon != null) {
			if (g != null) {
				icon.paintIcon(this, g, drawX, drawY);
			}
			drawX += icon.getIconWidth() + iconTextGap;
			drawHeight = Math.max(fontHeight, icon.getIconHeight());
		}
		else {
			drawHeight = fontHeight;
		}
		if (i != null) {
			drawHeight += i.bottom;
		}
		drawHeight += drawY;
		drawY += ascent;
	}

	protected Color getTypeColor(String s) {
		return colors[(JavaCompletion.isPrimitiveClassName(s)) ? KEYWORD_COLOR : TYPE_COLOR];
	}

	protected void drawType(Graphics g, JCType typ) {
		Color c = getTypeColor(typ.getClazz().getName());
		drawString(g, typ.format(false), c);
	}

	protected void drawParameter(Graphics g, JCParameter prm) {
		drawType(g, prm.getType());
		String name = prm.getName();
		if (name.length() > 0) {
			drawString(g, " "); // NOI18N
			drawString(g, prm.getName(), PARAMETER_NAME_COLOR);
		}
	}

	protected void drawParameterList(Graphics g, JCConstructor ctr) {
		drawString(g, "("); // NOI18N
		JCParameter[] p = ctr.getParameters();
		for (int i = 0; i < p.length; i++) {
			drawParameter(g, p[i]);
			if (i != p.length - 1) {
				drawString(g, ", "); // NOI18N
			}
		}
		drawString(g, ")"); // NOI18N
	}

	protected void drawExceptions(Graphics g, JCConstructor ctr) {
		JCClass[] exc = ctr.getExceptions();
		if (exc.length > 0) {
			drawString(g, THROWS, KEYWORD_COLOR);
			for (int i = 0; i < exc.length; i++) {
				String name = exc[i].getName();
				Color c = getTypeColor(name);
				drawString(g, name, c);
				if (i != exc.length - 1) {
					drawString(g, ", "); // NOI18N
				}
			}
		}
	}

	protected void drawPackage(Graphics g, JCPackage pkg) {
		drawIcon(g, icons[PACKAGE_ICON]);
		String name = pkg.getName();
		if (packageLastNameOnly) {
			name = pkg.getLastName();
		}
		drawString(g, name, PACKAGE_COLOR);
	}

	protected void drawClass(Graphics g, JCClass cls) {
		boolean ic = cls.isInterface();
		int level = JavaCompletion.getLevel(cls.getModifiers());
		String text = cls.getName();
		if (classDisplayFullName) {
			text = cls.getFullName();
		}
		else if (classDisplayOffset > 0 && classDisplayOffset < text.length()) {
			text = text.substring(classDisplayOffset);
		}

		drawIcon(g, icons[(ic ? INTERFACE_ICON : CLASS_ICON) + level]);
		drawString(g, text, (ic ? INTERFACE_ICON : CLASS_ICON));
	}

	protected void drawField(Graphics g, JCField fld) {
		int level = JavaCompletion.getLevel(fld.getModifiers());
		drawIcon(g, ((fld.getModifiers() & Modifier.STATIC) == 0) ? icons[FIELD_ICON + level] : icons[FIELD_STATIC_ICON + level]);
		if (displayStaticWord) {
			if ((fld.getModifiers() & Modifier.STATIC) != 0) {
				drawString(g, "static ", KEYWORD_COLOR); // NOI18N
			}
		}
		drawType(g, fld.getType());
		drawString(g, " "); // NOI18N
		drawString(g, fld.getName(), FIELD_COLOR + level);
	}

	protected void drawConstructor(Graphics g, JCConstructor ctr) {
		int level = JavaCompletion.getLevel(ctr.getModifiers());
		drawIcon(g, icons[CONSTRUCTOR_ICON + level]);
		drawString(g, ctr.getClazz().getName(), CONSTRUCTOR_COLOR + level);
		drawParameterList(g, ctr);
		drawExceptions(g, ctr);
	}

	protected void drawMethod(Graphics g, JCMethod mtd) {
		int level = JavaCompletion.getLevel(mtd.getModifiers());
		drawIcon(g, ((mtd.getModifiers() & Modifier.STATIC) == 0) ? icons[METHOD_ICON + level] : icons[METHOD_STATIC_ICON + level]);
		if (displayStaticWord) {
			if ((mtd.getModifiers() & Modifier.STATIC) != 0) {
				drawString(g, "static ", KEYWORD_COLOR); // NOI18N
			}
		}
		drawType(g, mtd.getReturnType());
		drawString(g, " "); // NOI18N
		drawString(g, mtd.getName(), METHOD_COLOR + level);
		drawParameterList(g, mtd);
		drawExceptions(g, mtd);
	}

	protected void draw(Graphics g) {
		if (value instanceof JCPackage) {
			drawPackage(g, (JCPackage) value);
		}
		else if (value instanceof JCClass) {
			drawClass(g, (JCClass) value);
		}
		else if (value instanceof JCField) {
			drawField(g, (JCField) value);
		}
		else if (value instanceof JCMethod) {
			drawMethod(g, (JCMethod) value);
		}
		else if (value instanceof JCConstructor) {
			drawConstructor(g, (JCConstructor) value);
		}
		else if (value instanceof JCParameter) {
			drawParameter(g, (JCParameter) value);
		}
		else {
			drawString(g, value.toString());
		}
	}

	public Dimension getPreferredSize() {
		draw(null);
		Insets i = getInsets();
		if (i != null) {
			drawX += i.right;
		}
		return new Dimension(drawX, drawHeight);
	}

	public void paintComponent(Graphics g) {
		// clear background
		g.setColor(getBackground());
		java.awt.Rectangle r = g.getClipBounds();
		g.fillRect(r.x, r.y, r.width, r.height);

		draw(g);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		this.list = list;
		this.value = value;
		this.isSelected = isSelected;
		this.getAccessibleContext().setAccessibleName(this.value.toString());
		this.getAccessibleContext().setAccessibleDescription(this.value.toString());
		return this;
	}

}
