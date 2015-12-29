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

package com.jeta.swingbuilder.codegen.builder;

import com.jeta.forms.store.memento.FormMemento;
import com.jeta.swingbuilder.store.CodeModel;

public class DefaultSourceBuilder implements SourceBuilder {

	private StringBuffer m_source = new StringBuffer();

	private int m_indent_pos = 0;
	private String m_indent_padding = "";

	private int m_tab_size = 3;

	private int m_column = 0;

	public static String buildSource(CodeModel cgenmodel, FormMemento fm) {

		DefaultSourceBuilder builder = new DefaultSourceBuilder();

		ClassDeclarationManager decl_mgr = new ClassDeclarationManager(cgenmodel);
		decl_mgr.setPackage(cgenmodel.getPackage());

		BuilderUtils.buildConstructor(decl_mgr, cgenmodel.getClassName());

		if (cgenmodel.isIncludeMain()) {
			BuilderUtils.buildMain(decl_mgr, cgenmodel.getClassName());
		}

		BuilderUtils.buildFillMethod(decl_mgr);
		BuilderUtils.buildImageLoader(decl_mgr);
		BuilderUtils.buildApplyComponentOrientation(decl_mgr);

		MethodWriter create_panel = new PanelWriter().createPanel(decl_mgr, fm);
		BuilderUtils.buildInitializer(decl_mgr, create_panel.getMethodName() + "()");

		decl_mgr.build(builder);
		return builder.m_source.toString();
	}

	public void closeBrace() {
		print('}');
	}

	public void dedent() {
		m_indent_pos--;
		if (m_indent_pos < 0)
			m_indent_pos = 0;
		updateIndentPadding();
	}

	public void indent() {
		m_indent_pos++;
		updateIndentPadding();
	}

	public void openBrace() {
		println();
		print('{');
	}

	public void println() {
		m_source.append('\n');
		m_column = 0;
	}

	public void println(String txt) {
		print(txt);
		println();
	}

	public void print(String txt) {
		if (txt != null && txt.length() > 0) {
			if (m_column == 0) {
				m_source.append(m_indent_padding);
			}
			m_source.append(txt);
			m_column += txt.length();
		}
	}

	public void print(char c) {
		if (m_column == 0) {
			m_source.append(m_indent_padding);
		}
		m_source.append(c);
		m_column++;
	}

	/**
	 * Fills a buffer with the given character count times
	 * 
	 * @param c
	 *            the character to fill the string with
	 * @param count
	 *            the number of characters to place in the string
	 */
	public static void fillBuffer(StringBuffer buff, char c, int count) {
		for (int index = 0; index < count; index++) {
			buff.append(c);
		}
	}

	/**
	 * Creates a string that is filled with the given character count times
	 * 
	 * @param c
	 *            the character to fill the string with
	 * @param count
	 *            the number of characters to place in the string
	 * @return the created string
	 */
	public static String fillString(char c, int count) {
		StringBuffer buff = new StringBuffer(count);
		fillBuffer(buff, c, count);
		return buff.toString();
	}

	private void updateIndentPadding() {
		if (m_indent_pos == 0)
			m_indent_padding = "";
		else {
			m_indent_padding = fillString(' ', m_indent_pos * m_tab_size);
		}
	}

}
