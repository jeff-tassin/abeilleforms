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

package com.jeta.swingbuilder.gui.properties.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jeta.swingbuilder.gui.components.FloatDocument;
import com.jeta.swingbuilder.gui.components.IntegerDocument;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

public class NumericEditor extends JETAPropertyEditor {
	/**
	 * Panel that is used to hold our editor
	 */
	private JPanel m_panel;

	/**
	 * Text field that accepts only characters that are valid for floating point
	 * values (i.e. digit, ., - )
	 */
	private JTextField m_field = new JTextField();

	/**
	 * The type of value to expect (Integer.class, Long.class, Float.class,
	 * Short.class, or Double.class )
	 */
	private Class m_number_class;

	/**
	 * ctor
	 * 
	 * @param c
	 *            the type of value to expect (Integer.class, Long.class,
	 *            Float.class, Short.class, or Double.class )
	 */
	public NumericEditor(Class c) {
		m_number_class = c;
		m_panel = new JPanel();
		m_panel.setLayout(new BorderLayout());
		m_panel.add(m_field, BorderLayout.CENTER);

		m_panel.setBackground(javax.swing.UIManager.getColor("Table.background"));

		m_field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setValue(m_field.getText());
			}
		});

		if (isIntegral())
			m_field.setDocument(new IntegerDocument());
		else
			m_field.setDocument(new FloatDocument());
	}

	public Object convertValue(Object value) {
		if (value instanceof Byte)
			return value;
		else if (value instanceof Short)
			return value;
		else if (value instanceof Integer)
			return value;
		else if (value instanceof Long)
			return value;
		else if (value instanceof Float)
			return value;
		else if (value instanceof Double)
			return value;
		else if (value instanceof String) {
			if (isIntegral()) {
				return new Long(toLong((String) value));
			}
			else {
				return new Double(toDouble((String) value));
			}
		}
		else
			return "0";
	}

	/**
	 * @return the custom editor
	 */
	public Component getCustomEditor() {
		return m_panel;
	}

	/**
	 * @return true if this editor supports custom editing inline in the
	 *         property table. Property types such as the Java primitives and
	 *         Strings support inline editing.
	 */
	public boolean supportsInlineEditing() {
		return true;
	}

	/**
	 * Sets the value
	 */
	public void setValue(Object value) {
		value = convertValue(value);
		super.setValue(value);
		m_field.setText(value.toString());
	}

	/**
	 * @return the value represented by this field
	 */
	public Object getValue() {
		String field_txt = FormDesignerUtils.fastTrim(m_field.getText());
		if (isByte())
			return new Byte((byte) toLong(field_txt));
		else if (isShort())
			return new Short((short) toLong(field_txt));
		else if (isInteger())
			return new Integer((int) toLong(field_txt));
		else if (isLong())
			return new Long(toLong(field_txt));
		else if (isFloat())
			return new Float((float) toDouble(field_txt));
		else if (isDouble())
			return new Double(toDouble(field_txt));
		else {
			assert (false);
			return null;
		}
	}

	private boolean isByte() {
		return (Byte.class == m_number_class);
	}

	private boolean isInteger() {
		return (Integer.class == m_number_class);
	}

	private boolean isShort() {
		return (Short.class == m_number_class);
	}

	private boolean isLong() {
		return (Long.class == m_number_class);
	}

	private boolean isIntegral() {
		return (isByte() || isInteger() || isShort() || isLong());
	}

	private boolean isFloat() {
		return (Float.class == m_number_class);
	}

	private boolean isDouble() {
		return (Double.class == m_number_class);
	}

	/**
	 * @return the given string converted to a double. If the string is not
	 *         valid, 0 is returned.
	 */
	private double toDouble(String sval) {
		try {
			return Double.parseDouble(sval);
		} catch (Exception e) {
			return 0.0;
		}
	}

	/**
	 * @return the given string converted to a long. If the string is not valid,
	 *         0 is returned.
	 */
	private long toLong(String sval) {
		try {
			return Long.parseLong(sval);
		} catch (Exception e) {
			return 0L;
		}
	}

	public static class IntegerEditor extends NumericEditor {
		public IntegerEditor() {
			super(Integer.class);
		}
	}

	public static class ByteEditor extends NumericEditor {
		public ByteEditor() {
			super(Byte.class);
		}
	}

	public static class ShortEditor extends NumericEditor {
		public ShortEditor() {
			super(Short.class);
		}
	}

	public static class LongEditor extends NumericEditor {
		public LongEditor() {
			super(Long.class);
		}
	}

	public static class FloatEditor extends NumericEditor {
		public FloatEditor() {
			super(Float.class);
		}
	}

	public static class DoubleEditor extends NumericEditor {
		public DoubleEditor() {
			super(Double.class);
		}
	}
}
