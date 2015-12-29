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

package com.jeta.swingbuilder.codegen.gui.editor;

import javax.swing.Action;
import javax.swing.text.Document;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Formatter;
import org.netbeans.editor.MultiKeyBinding;
import org.netbeans.editor.Settings;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.ext.Completion;
import org.netbeans.editor.ext.ExtEditorUI;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.editor.ext.java.JavaCompletion;
import org.netbeans.editor.ext.java.JavaFormatter;
import org.netbeans.editor.ext.java.JavaSettingsInitializer;
import org.netbeans.editor.ext.java.JavaSyntax;
import org.netbeans.editor.ext.java.JavaSyntaxSupport;

/**
 * Java editor kit with appropriate document
 * 
 * @author Jeff Tassin
 */

public class JavaKit extends ExtKit {

	static {
		try {
			javax.swing.JEditorPane.registerEditorKitForContentType("text/x-java", "JavaKit", JavaKit.class.getClassLoader());
			Settings.addInitializer(new JavaSettingsInitializer(JavaKit.class));
			Settings.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ctor
	 */
	public JavaKit() {

	}

	public String getContentType() {
		return "text/x-java";
	}

	/**
	 * Create new instance of syntax coloring scanner
	 * 
	 * @param doc
	 *            document to operate on. It can be null in the cases the syntax
	 *            creation is not related to the particular document
	 */
	public Syntax createSyntax(Document doc) {
		return new JavaSyntax();
	}

	/** Create syntax support */
	public SyntaxSupport createSyntaxSupport(BaseDocument doc) {
		return new JavaSyntaxSupport(doc);
	}

	public Completion createCompletion(ExtEditorUI extEditorUI) {
		return new JavaCompletion(extEditorUI);
	}

	/** Create the formatter appropriate for this kit */
	public Formatter createFormatter() {
		return new JavaFormatter(this.getClass());
	}

	protected EditorUI createEditorUI() {
		return new ExtEditorUI();
	}

	/**
	 * List all actions supported by this class.
	 */
	public static Action[] listDefaultActions() {
		return new Action[0];
	}

	/**
	 * Get the default bindings. We are using a different method for
	 * initializing default bindings than the netbeans callback method.
	 */
	public static MultiKeyBinding[] listDefaultKeyBindings() {
		MultiKeyBinding[] bindings = new MultiKeyBinding[0];
		return bindings;
	}
}
