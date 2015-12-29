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

package com.jeta.swingbuilder.codegen.builder.properties;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.store.properties.BevelBorderProperty;
import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.forms.store.properties.EmptyBorderProperty;
import com.jeta.forms.store.properties.EtchedBorderProperty;
import com.jeta.forms.store.properties.LineBorderProperty;
import com.jeta.forms.store.properties.TitledBorderProperty;
import com.jeta.swingbuilder.codegen.builder.BeanWriter;
import com.jeta.swingbuilder.codegen.builder.DeclarationManager;
import com.jeta.swingbuilder.codegen.builder.LocalVariableDeclaration;
import com.jeta.swingbuilder.codegen.builder.MethodStatement;
import com.jeta.swingbuilder.codegen.builder.PropertyWriter;
import com.jeta.swingbuilder.codegen.builder.StringExpression;
import com.jeta.swingbuilder.codegen.builder.VariableDeclaration;

public class BorderPropertyBuilder implements PropertyWriter {

	/**
	 * PropertyWriter implementation
	 */
	public void writeProperty(DeclarationManager declMgr, BeanWriter writer, JETAPropertyDescriptor pd, Object value) {
		try {
			Method write = pd.getWriteMethod();
			if (value instanceof BorderProperty) {
				BorderProperty bp = (BorderProperty) value;
				String methodname = "setBorder";
				if (write != null)
					methodname = write.getName();
				MethodStatement ms = new MethodStatement(writer.getBeanVariable(), methodname);
				VariableDeclaration border_var = null;
				if (bp instanceof CompoundBorderProperty) {
					border_var = createCompoundBorder(declMgr, writer, (CompoundBorderProperty) bp);
				}
				else {
					border_var = createBorder(declMgr, writer, bp);
				}

				if (border_var != null) {
					ms.addParameter(border_var.getVariable());
					writer.addStatement(ms);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private VariableDeclaration createCompoundBorder(DeclarationManager declMgr, BeanWriter writer, CompoundBorderProperty cp) {
		if (cp.size() == 0) {
			return null;
		}
		else if (cp.size() == 1) {
			Iterator iter = cp.iterator();
			VariableDeclaration last_border = createBorder(declMgr, writer, (BorderProperty) iter.next());
			return last_border;
		}
		else if (cp.size() > 1) {
			LocalVariableDeclaration last_border = null;
			Iterator iter = cp.iterator();
			while (iter.hasNext()) {
				BorderProperty nested_border = (BorderProperty) iter.next();
				LocalVariableDeclaration border = createBorder(declMgr, writer, nested_border);
				if (last_border == null) {
					last_border = border;
				}
				else if (border != null) {
					last_border = createBorderFactoryStatement(declMgr, last_border, border);
					writer.addStatement(last_border);
				}
			}
			return last_border;
		}
		else
			return null;
	}

	private LocalVariableDeclaration createBorderFactoryStatement(DeclarationManager declMgr, VariableDeclaration outer, VariableDeclaration inner) {
		declMgr.addImport("javax.swing.BorderFactory");
		LocalVariableDeclaration result = new LocalVariableDeclaration(declMgr, Border.class, null, "BorderFactory.createCompoundBorder");

		result.addParameter(outer.getVariable());
		result.addParameter(inner.getVariable());
		return result;
	}

	private LocalVariableDeclaration createBorder(DeclarationManager declMgr, BeanWriter writer, BorderProperty bp) {
		LocalVariableDeclaration border_var = null;

		if (bp instanceof BevelBorderProperty) {
			declMgr.addImport("javax.swing.border.BevelBorder");
			BevelBorderProperty bb = (BevelBorderProperty) bp;

			border_var = new LocalVariableDeclaration(declMgr, BevelBorder.class);
			if (bb.getBevelType() == BevelBorder.RAISED)
				border_var.addParameter("BevelBorder.RAISED");
			else
				border_var.addParameter("BevelBorder.LOWERED");

			declMgr.addImport("java.awt.Color");

			BevelBorder bevelborder = new BevelBorder(bb.getBevelType());
			Color c = bb.getHighlightOuterColor();
			if (ColorProperty.DEFAULT_COLOR.equals(bb.getHighlightOuterColorProperty().getColorKey()))
				c = bevelborder.getHighlightOuterColor();
			border_var.addParameter(ColorPropertyWriter.createColorExpression(c));

			c = bb.getHighlightInnerColor();
			if (ColorProperty.DEFAULT_COLOR.equals(bb.getHighlightInnerColorProperty().getColorKey()))
				c = bevelborder.getHighlightInnerColor();
			border_var.addParameter(ColorPropertyWriter.createColorExpression(c));

			c = bb.getShadowOuterColor();
			if (ColorProperty.DEFAULT_COLOR.equals(bb.getShadowOuterColorProperty().getColorKey()))
				c = bevelborder.getShadowOuterColor();
			border_var.addParameter(ColorPropertyWriter.createColorExpression(c));

			c = bb.getShadowInnerColor();
			if (ColorProperty.DEFAULT_COLOR.equals(bb.getShadowInnerColorProperty().getColorKey()))
				c = bevelborder.getShadowInnerColor();
			border_var.addParameter(ColorPropertyWriter.createColorExpression(c));

		}
		else if (bp instanceof EtchedBorderProperty) {
			declMgr.addImport("javax.swing.border.EtchedBorder");
			declMgr.addImport("java.awt.Color");

			EtchedBorderProperty eb = (EtchedBorderProperty) bp;
			border_var = new LocalVariableDeclaration(declMgr, EtchedBorder.class);
			if (eb.getEtchType() == EtchedBorder.RAISED)
				border_var.addParameter("EtchedBorder.RAISED");
			else
				border_var.addParameter("EtchedBorder.LOWERED");

			EtchedBorder eborder = new EtchedBorder(eb.getEtchType());
			Color c = eb.getHighlightColor();
			if (ColorProperty.DEFAULT_COLOR.equals(eb.getHighlightColorProperty().getColorKey()))
				c = eborder.getHighlightColor();
			border_var.addParameter(ColorPropertyWriter.createColorExpression(c));

			c = eb.getShadowColor();
			if (ColorProperty.DEFAULT_COLOR.equals(eb.getShadowColorProperty().getColorKey()))
				c = eborder.getShadowColor();
			border_var.addParameter(ColorPropertyWriter.createColorExpression(c));

		}
		else if (bp instanceof TitledBorderProperty) {
			TitledBorderProperty tb = (TitledBorderProperty) bp;
			declMgr.addImport("javax.swing.border.TitledBorder");
			declMgr.addImport("java.awt.Color");

			border_var = new LocalVariableDeclaration(declMgr, TitledBorder.class);

			border_var.addParameter("null");
			border_var.addParameter(new StringExpression(tb.getTitle()));
			border_var.addParameter("TitledBorder." + BorderProperty.toJustificationString(tb.getJustification()));
			border_var.addParameter("TitledBorder." + BorderProperty.toPositionString(tb.getPosition()));
			border_var.addParameter("null");
			border_var.addParameter(ColorPropertyWriter.createColorExpression(tb.getTextColor()));
		}
		else if (bp instanceof LineBorderProperty) {
			declMgr.addImport("java.awt.Color");
			declMgr.addImport("javax.swing.border.LineBorder");

			LineBorderProperty lp = (LineBorderProperty) bp;
			border_var = new LocalVariableDeclaration(declMgr, LineBorder.class);
			border_var.addParameter(ColorPropertyWriter.createColorExpression(lp.getLineColor()));
			border_var.addParameter(lp.getLineThickness());
			border_var.addParameter(lp.isCurved());
		}
		else if (bp instanceof EmptyBorderProperty) {
			declMgr.addImport("javax.swing.border.EmptyBorder");
			EmptyBorderProperty eb = (EmptyBorderProperty) bp;
			border_var = new LocalVariableDeclaration(declMgr, EmptyBorder.class);
			border_var.addParameter(eb.getTop());
			border_var.addParameter(eb.getLeft());
			border_var.addParameter(eb.getBottom());
			border_var.addParameter(eb.getRight());
		}

		writer.addStatement(border_var);
		if (!(bp instanceof TitledBorderProperty) && bp.isIncludeTitle()) {
			border_var = addTitle(declMgr, bp, border_var);
			writer.addStatement(border_var);
		}
		return border_var;
	}

	private LocalVariableDeclaration addTitle(DeclarationManager declMgr, BorderProperty bp, LocalVariableDeclaration lvar) {
		LocalVariableDeclaration border_var = null;
		if (lvar != null) {
			declMgr.addImport("javax.swing.BorderFactory");
			declMgr.addImport("java.awt.Color");
			declMgr.addImport("javax.swing.border.TitledBorder");

			border_var = new LocalVariableDeclaration(declMgr, Border.class, null, "BorderFactory.createTitledBorder");
			border_var.addParameter(lvar.getVariable());
			border_var.addParameter(new StringExpression(bp.getTitle()));
			border_var.addParameter("TitledBorder." + BorderProperty.toJustificationString(bp.getJustification()));
			border_var.addParameter("TitledBorder." + BorderProperty.toPositionString(bp.getPosition()));
			border_var.addParameter("null");
			border_var.addParameter(ColorPropertyWriter.createColorExpression(bp.getTextColor()));
		}
		return border_var;
	}
}
