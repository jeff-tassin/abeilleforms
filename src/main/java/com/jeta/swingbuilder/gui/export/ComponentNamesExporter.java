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

package com.jeta.swingbuilder.gui.export;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.StandardComponent;

/**
 * Exports the component names to the clipboard for the given form.
 * 
 * 
 * @author Jeff Tassin
 */
public class ComponentNamesExporter {
	/**
	 * Flag that indicates if linked, nested forms should be included in the export
	 */
	private boolean m_include_linked = false;

	/**
	 * Flag that indicates if embedded, nested forms should be included in the export
	 */
	private boolean m_include_embedded = true;

	/**
	 * Flag that indicates if labels should be included in the export
	 */
	private boolean m_include_labels = false;

	/**
	 * Valid tokens for the line decorator
	 */
	public static final String METHOD = "$method";
	public static final String IDENTIFIER = "$identifier";
	public static final String COMPONENT_NAME = "$name";
	public static final String TYPE = "$type";
	public static final String FORM_PATH = "$formpath";
	public static final String FORM_NAME = "$formname";

	ExportNamesView view;

	/**
	 * ctor
	 */
	public ComponentNamesExporter(ExportNamesView view) {
		this.view = view;
		m_include_linked = view.isIncludeLinkedForms();
		m_include_embedded = view.isIncludeEmbeddedForms();
		m_include_labels = view.isIncludeLabels();

		String decorator = view.getDecorator();
		assert (decorator != null);
	}

	/**
	 * Decorates the component name using the supplied formatting rules
	 */
	private String decorateName(String compName, Component comp, FormComponent form) {
		if (compName == null || compName.length() == 0)
			return null;

		if (comp instanceof javax.swing.JLabel && !m_include_labels)
			return null;

		String decorator = view.getDecorator();
		File f = null;
		if (form.getAbsolutePath() != null)
			f = new File(form.getAbsolutePath());
		decorator = decorator.replaceAll("\\" + IDENTIFIER, toJavaLiteral(compName));
		decorator = decorator.replaceAll("\\" + COMPONENT_NAME, compName);
		decorator = decorator.replaceAll("\\" + METHOD, toJavaMethod(compName));
		if (f == null) {
			decorator = decorator.replaceAll("\\" + FORM_PATH, "???");
			decorator = decorator.replaceAll("\\" + FORM_NAME, "???");
		} else {
			decorator = decorator.replaceAll("\\" + FORM_PATH, f.getAbsolutePath());
			decorator = decorator.replaceAll("\\" + FORM_NAME, f.getName().substring(0, f.getName().lastIndexOf('.')));
		}
		if (comp instanceof GridView)
			decorator = decorator.replaceAll("\\" + TYPE, javax.swing.JPanel.class.getName());
		else
			decorator = decorator.replaceAll("\\" + TYPE, comp.getClass().getName());
		return decorator;
	}

	/**
	 * Decorates the prefix or postfix using the supplied formatting rules
	 */
	private String decorateFormPart(String part, FormComponent form) {
		if (part == null || part.length() == 0)
			return null;
		if (form.getAbsolutePath() == null)
			return null;
		File f = new File(form.getAbsolutePath());
		String decorator = new String(part);
		decorator = decorator.replaceAll("\\" + FORM_PATH, f.getAbsolutePath());
		decorator = decorator.replaceAll("\\" + FORM_NAME, f.getName().substring(0, f.getName().lastIndexOf('.')));
		return decorator;
	}

	/**
	 * Exports the component names to the clipboard for the given form.
	 */
	private String export(FormComponent form) {
		return export(form, null);
	}

	/**
	 * Exports the component names to the clipboard
	 */
	public void exportToClipboard(FormComponent fc) {
		String result = export(fc);
		try {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = kit.getSystemClipboard();
			StringSelection transferable = new StringSelection(result);
			clipboard.setContents(transferable, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the given name to a valid Java Literal. All non-characters are converted to a _ (underscore).
	 */
	private String toJavaLiteral(String compNameValue) {
		String compName = compNameValue.toUpperCase();
		StringBuffer sbuff = new StringBuffer();
		for (int index = 0; index < compName.length(); index++) {
			char c = compName.charAt(index);
			if (index == 0) {
				if (Character.isJavaIdentifierStart(c)) {
					sbuff.append(c);
				} else {
					sbuff.append('_');
					sbuff.append(c);
				}
			} else {
				if (Character.isJavaIdentifierPart(c)) {
					sbuff.append(c);
				} else {
					sbuff.append('_');
				}
			}
		}
		return sbuff.toString();
	}

	/**
	 * Converts the given name to a valid Java Literal. All non-characters are converted to a _ (underscore).
	 */
	private String toJavaMethod(String compNameValue) {
		String compName = compNameValue;
		StringBuffer sbuff = new StringBuffer();
		boolean nextUpper = true;
		for (int index = 0; index < compName.length(); index++) {
			char c = compName.charAt(index);
			if (nextUpper) {
				c = Character.toUpperCase(c);
				nextUpper = false;
			}
			if (Character.isJavaIdentifierPart(c))
				sbuff.append(c);
			else
				nextUpper = true;
		}
		return sbuff.toString();
	}

	/**
	 * Exports the component names to the clipboard for the given form.
	 */
	private String export(FormComponent form, String prefix) {
		StringBuffer sbuff = new StringBuffer();

		String formprefix = decorateFormPart(view.getPrefix(), form);
		if (formprefix != null) {
			sbuff.append(formprefix);
			sbuff.append("\n");
		}

		String formname = form.getChildView().getName();
		if (formname != null && formname.length() > 0) {
			String result = decorateName(formname, form.getChildView(), form);
			if (result != null) {
				sbuff.append(result);
				sbuff.append('\n');
			}
		}

		Iterator iter = form.gridIterator();

		LinkedList forms = new LinkedList();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			if (gc instanceof FormComponent) {
				FormComponent childform = (FormComponent) gc;
				if (childform.isLinked() && !m_include_linked)
					continue;

				if (childform.isEmbedded() && !m_include_embedded)
					continue;

				forms.add(childform);
			} else if (gc instanceof StandardComponent) {
				StandardComponent stdcomp = (StandardComponent) gc;
				JETABean bean = stdcomp.getBean();
				if (bean != null) {
					Component comp = bean.getDelegate();
					if (comp != null) {
						String compname = (prefix == null ? "" : prefix + ".") + comp.getName();
						if (compname != null && compname.length() > 0) {
							String result = decorateName(compname, comp, form);
							if (result != null) {
								sbuff.append(result);
								sbuff.append('\n');
							}
						}
					}
				}
			} else {
				assert (false);
			}
		}

		/**
		 * Now, export each child form
		 */
		iter = forms.iterator();
		while (iter.hasNext()) {
			FormComponent fc = (FormComponent) iter.next();
			String compname = (prefix == null ? "" : prefix + ".") + fc.getChildView().getName();
			sbuff.append(export(fc, compname));
		}

		String formpostfix = decorateFormPart(view.getPostfix(), form);
		if (formpostfix != null) {
			sbuff.append(formpostfix);
			sbuff.append("\n");
		}

		return sbuff.toString();
	}
}
