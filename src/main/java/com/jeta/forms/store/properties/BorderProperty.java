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

package com.jeta.forms.store.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Base class for all border properties. Stores common border attributes.
 * 
 * @author Jeff Tassin
 */
public class BorderProperty extends JETAProperty {
	static final long serialVersionUID = -9007148666135189228L;

	/**
	 * The version of this class.
	 */
	public static final int VERSION = 4;

	/**
	 * Flags that indicate which part of the border should be painted.
	 * Currently, only LineBorders support this capability.
	 */
	private boolean m_top = true;

	private boolean m_left = true;

	private boolean m_right = true;

	private boolean m_bottom = true;

	/**
	 * Set to true if this border has a title
	 */
	private boolean m_include_title;

	/**
	 * The title text.
	 */
	private String m_title;

	/**
	 * The title justification: TitledBorder.LEFT, CENTER, RIGHT, LEADING,
	 * TRAILING, DEFAULT_JUSTICATION
	 */
	private int m_justification = TitledBorder.DEFAULT_JUSTIFICATION;

	/**
	 * The title position
	 * TitledBorder.ABOVE_TOP,TOP,BELOW_TOP,ABOVE_BOTTOM,BOTTOM,BELOW_BOTTOM,DEFAULT_POSITION
	 * (top)
	 */
	private int m_position = TitledBorder.DEFAULT_POSITION;

	/**
	 * Default title text color
	 */
	private static ColorProperty DEFAULT_TEXT_COLOR = new ColorProperty("TitledBorder.titleColor");

	/**
	 * The title text color.
	 */
	private ColorProperty m_text_color = new ColorProperty("TitledBorder.titleColor");

	/**
	 * The name for this property.
	 */
	public static final String PROPERTY_ID = "border";

	/**
	 * Creates an unitialized <code>BorderProperty</code> instance.
	 */
	public BorderProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Creates a Swing border instance based on the values in this
	 * BorderProperty instance.
	 * 
	 * @param comp
	 *            the component that is the basis for the border. This is
	 *            normally ignored except when assigning a default border on a
	 *            component. The comp instance is used to obtains the default
	 *            border.
	 * @return a Swing border instance based on the values in this
	 *         BorderProperty instance.
	 */
	public Border createBorder(Component comp) {
		return null;
	}

	/**
	 * Adds a title decoration to the given border. This method will add the
	 * title only if <code>isIncludeTitle()</code> is true.
	 * 
	 * @param b
	 *            the border to decorate with a title.
	 * @return a border instance with the given title decoration.
	 */
	public Border createTitle(Border b) {
		if (isIncludeTitle()) {
			return BorderFactory.createTitledBorder(b, getTitle(), getJustification(), getPosition(), getTextFont(), getTextColor());
		}
		else {
			return b;
		}
	}

	/**
	 * Object equals implementation.
	 */
	public boolean equals(Object object) {
		if (object instanceof BorderProperty) {
			BorderProperty bp = (BorderProperty) object;
			return (super.equals(object) && m_top == bp.m_top && m_left == bp.m_left && m_right == bp.m_right && m_bottom == bp.m_bottom
					&& m_include_title == bp.m_include_title && isEqual(m_title, bp.m_title) && m_justification == bp.m_justification
					&& m_position == bp.m_position && isEqual(m_text_color, bp.m_text_color));

		}
		else {
			return false;
		}
	}

	/**
	 * Returns the TitledBorder position constant for the given string
	 * representation
	 * 
	 * @param pos
	 *            the String representation of the TitledBorder position value.
	 * @return the TitleBorder position value. The constants are defined in
	 *         <code>TitledBorder</code>: <code>ABOVE_TOP</code>
	 *  <code>BELOW_TOP</code>
	 *  <code>TOP</code>
	 *  <code>ABOVE_BOTTOM</code>
	 *  <code>BOTTOM</code>
	 *  <code>BELOW_BOTTOMN</code>
	 *  <code>DEFAULT_POSITION</code>
	 */
	public static int fromPositionString(String pos) {
		if ("ABOVE_TOP".equals(pos))
			return TitledBorder.ABOVE_TOP;
		else if ("TOP".equals(pos))
			return TitledBorder.TOP;
		else if ("BELOW_TOP".equals(pos))
			return TitledBorder.BELOW_TOP;
		else if ("ABOVE_BOTTOM".equals(pos))
			return TitledBorder.ABOVE_BOTTOM;
		else if ("BOTTOM".equals(pos))
			return TitledBorder.BOTTOM;
		else if ("BELOW_BOTTOM".equals(pos))
			return TitledBorder.BELOW_BOTTOM;
		else
			return TitledBorder.DEFAULT_POSITION;
	}

	/**
	 * Returns the TitledBorder justification constant for the given string
	 * representation
	 * 
	 * @param just
	 *            the String representation of the TitledBorder justification
	 *            value.
	 * @return the TitleBorder justification value. The constants are defined in
	 *         <code>TitledBorder</code>: <code>LEFT</code>
	 *  <code>CENTER</code>
	 *  <code>RIGHT</code>
	 *  <code>LEADING</code>
	 *  <code>TRAILING</code>
	 *  <code>DEFAULT_JUSTIFICATION</code>
	 */
	public static int fromJustificationString(String just) {
		if ("LEFT".equals(just))
			return TitledBorder.LEFT;
		else if ("CENTER".equals(just))
			return TitledBorder.CENTER;
		else if ("RIGHT".equals(just))
			return TitledBorder.RIGHT;
		else if ("LEADING".equals(just))
			return TitledBorder.LEADING;
		else if ("TRAILING".equals(just))
			return TitledBorder.TRAILING;
		else
			return TitledBorder.DEFAULT_JUSTIFICATION;
	}

	/**
	 * Returns true if this border has a title.
	 * 
	 * @return true if this border has a title
	 */
	public boolean isIncludeTitle() {
		return m_include_title;
	}

	/**
	 * Return the text for this border's title. <code>isIncludeTitle()</code>
	 * must return true for this to have an effect.
	 * 
	 * @return the text for the title
	 */
	public String getTitle() {
		return m_title;
	}

	/**
	 * Returns the title justification. <code>isIncludeTitle()</code> must
	 * return true for this to have an effect.
	 * 
	 * @return the title justification. TitledBorder.LEFT, CENTER, RIGHT,
	 *         LEADING, TRAILING, DEFAULT_JUSTICATION
	 */
	public int getJustification() {
		return m_justification;
	}

	/**
	 * Returns the title position. <code>isIncludeTitle()</code> must return
	 * true for this to have an effect.
	 * 
	 * @return the title position
	 *         TitledBorder.ABOVE_TOP,TOP,BELOW_TOP,ABOVE_BOTTOM,BOTTOM,BELOW_BOTTOM,DEFAULT_POSITION
	 *         (top)
	 */
	public int getPosition() {
		return m_position;
	}

	/**
	 * Returns the title text color. <code>isIncludeTitle()</code> must return
	 * true for this to have an effect.
	 * 
	 * @return the color of the text in the title
	 */
	public Color getTextColor() {
		return m_text_color.getColor();
	}

	/**
	 * Returns the color property for the title text.
	 * 
	 * @return the color property for the title text.
	 */
	public ColorProperty getTextColorProperty() {
		return m_text_color;
	}

	/**
	 * The title text font
	 * 
	 * @return the font of the title text.
	 */
	public Font getTextFont() {
		return javax.swing.UIManager.getFont("Table.font");
	}

	/**
	 * Returns true if the top part of the border is painted. Currently, only
	 * LineBorders support this capability.
	 * 
	 * @return true if the top part of the border is painted
	 */
	public boolean isTopPainted() {
		return m_top;
	}

	/**
	 * Returns true if the left part of the border is painted. Currently, only
	 * LineBorders support this capability.
	 * 
	 * @return true if the left part of the border is painted
	 */
	public boolean isLeftPainted() {
		return m_left;
	}

	/**
	 * Returns true if the bottom part of the border is painted. Currently, only
	 * LineBorders support this capability.
	 * 
	 * @return true if the bottom part of the border is painted
	 */
	public boolean isBottomPainted() {
		return m_bottom;
	}

	/**
	 * Returns true if the right part of the border is painted. Currently, only
	 * LineBorders support this capability.
	 * 
	 * @return true if the right part of the border is painted
	 */
	public boolean isRightPainted() {
		return m_right;
	}

	/**
	 * Set the flag that indicates if this border has a title
	 */
	public void setIncludeTitle(boolean includeTitle) {
		m_include_title = includeTitle;
	}

	/**
	 * Sets the text for the title.
	 * 
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		m_title = title;
	}

	/**
	 * Sets the title justification.
	 * 
	 * @param justification
	 *            TitledBorder.LEFT, CENTER, RIGHT, LEADING, TRAILING,
	 *            DEFAULT_JUSTICATION
	 */
	public void setJustification(int justification) {
		m_justification = justification;
	}

	/**
	 * Sets the title position
	 * 
	 * @param position
	 *            TitledBorder.ABOVE_TOP,TOP,BELOW_TOP,ABOVE_BOTTOM,BOTTOM,BELOW_BOTTOM,DEFAULT_POSITION
	 *            (top)
	 */
	public void setPosition(int position) {
		m_position = position;
	}

	/**
	 * Sets the title text color.
	 */
	public void setTextColorProperty(ColorProperty c) {
		m_text_color.setValue(c);
	}

	/**
	 * Set to true if the top part of the border is painted. False if the top
	 * part of the border is not painted.
	 */
	public void setTopPainted(boolean bpaint) {
		m_top = bpaint;
	}

	/**
	 * Set to true if the left part of the border is painted. False if the left
	 * part of the border is not painted.
	 */
	public void setLeftPainted(boolean bpaint) {
		m_left = bpaint;
	}

	/**
	 * Set to true if the bottom part of the border is painted. False if the
	 * bottom part of the border is not painted.
	 */
	public void setBottomPainted(boolean bpaint) {
		m_bottom = bpaint;
	}

	/**
	 * Set to true if the right part of the border is painted. False if the
	 * right part of the border is not painted.
	 */
	public void setRightPainted(boolean bpaint) {
		m_right = bpaint;
	}

	public void setValue(Object obj) {
		if (obj instanceof BorderProperty) {
			BorderProperty bp = (BorderProperty) obj;
			m_include_title = bp.m_include_title;
			m_title = bp.m_title;
			m_justification = bp.m_justification;
			m_position = bp.m_position;
			m_text_color = bp.m_text_color;
			m_top = bp.m_top;
			m_left = bp.m_left;
			m_bottom = bp.m_bottom;
			m_right = bp.m_right;
		}
		else {
			assert (false);
		}
	}

	public static String toPositionString(int pos) {
		switch (pos) {
		case TitledBorder.ABOVE_TOP:
			return "ABOVE_TOP";
		case TitledBorder.TOP:
			return "TOP";
		case TitledBorder.BELOW_TOP:
			return "BELOW_TOP";
		case TitledBorder.ABOVE_BOTTOM:
			return "ABOVE_BOTTOM";
		case TitledBorder.BOTTOM:
			return "BOTTOM";
		case TitledBorder.BELOW_BOTTOM:
			return "BELOW_BOTTOM";
		default:
			return "DEFAULT_POSITION";
		}
	}

	public static String toJustificationString(int just) {
		switch (just) {
		case TitledBorder.LEFT:
			return "LEFT";

		case TitledBorder.CENTER:
			return "CENTER";

		case TitledBorder.RIGHT:
			return "RIGHT";

		case TitledBorder.LEADING:
			return "LEADING";

		case TitledBorder.TRAILING:
			return "TRAILING";

		default:
			return "DEFAULT_JUSTIFICATION";
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());

		int version = in.readVersion();
		if (version == 1) {
			m_title = (String) in.readObject("title");
		}
		else {
			m_include_title = in.readBoolean("includetitle", false);
			m_title = (String) in.readObject("title");
			m_justification = in.readInt("justification", TitledBorder.DEFAULT_JUSTIFICATION);
			m_position = in.readInt("position", TitledBorder.DEFAULT_POSITION);

			Object col = in.readObject("textcolor");
			if (col instanceof Color) {
				m_text_color.setConstantColor((Color) col);
			}
			else if (col instanceof ColorProperty) {
				m_text_color = (ColorProperty) col;
			}
			else if (col != null) {
				System.out.println("BorderProperty: Unknown object:  " + col);
				assert (false);
			}

			if (m_text_color == null)
				m_text_color = new ColorProperty("TitledBorder.titleColor");

			if (version > 2) {
				m_top = in.readBoolean("top", true);
				m_left = in.readBoolean("left", true);
				m_bottom = in.readBoolean("bottom", true);
				m_right = in.readBoolean("right", true);
			}
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeBoolean("includetitle", m_include_title, false);
		out.writeObject("title", m_title);
		out.writeInt("justification", m_justification, TitledBorder.DEFAULT_JUSTIFICATION);
		out.writeInt("position", m_position, TitledBorder.DEFAULT_POSITION);

		/**
		 * optimization. give persistent store the option of not storing text
		 * color if it is the default
		 */
		if (DEFAULT_TEXT_COLOR.equals(m_text_color)) {
			out.writeObject("textcolor", m_text_color);
		}
		else {
			out.writeObject("textcolor", null);
		}

		out.writeBoolean("top", m_top, true);
		out.writeBoolean("left", m_left, true);
		out.writeBoolean("bottom", m_bottom, true);
		out.writeBoolean("right", m_right, true);
	}

	public void updateBean(JETABean jbean) {
		Component comp = null;
		if (jbean != null)
			comp = jbean.getDelegate();

		if (comp instanceof JComponent) {
			((JComponent) comp).setBorder(createBorder(comp));
		}
		else {
			assert (false);
		}
	}

}
