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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenProcessor;

/**
 * Mapping of colorings to particular token types
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JavaImport implements TokenProcessor {

	/**
	 * Initial length of the document to be scanned. It should be big enough so
	 * that only one pass is necessary. If the initial section is too long, then
	 * this value is doubled and the whole parsing restarted.
	 */
	private static final int INIT_SCAN_LEN = 4096;

	private static final int INIT = 0; // at the line begining before import
										// kwd
	private static final int AFTER_IMPORT = 1; // right after the import kwd
	private static final int INSIDE_EXP = 2; // inside import expression
	// inside import expression mixed from several different tokens
	// exp string buffer is used in this case
	private static final int INSIDE_MIXED_EXP = 3;

	/** Short names to classes map */
	private HashMap name2Class = new HashMap(501);

	private char[] buffer;

	private ArrayList infoList = new ArrayList();

	/** Current state of the imports parsing */
	private int state;

	/**
	 * Whether parsing package statement instead of import statment. They have
	 * similair syntax so only this flag distinguishes them.
	 */
	private boolean parsingPackage;

	/** Start of the whole import statement */
	private int startPos;

	/** Start position of the particular import expression */
	private int expPos;

	private boolean eotReached;

	private StringBuffer exp = new StringBuffer();

	/** Whether the star was found at the end of package expression */
	private boolean star;

	/** The end of the import section. Used for optimized reparsing */
	private int posEndOfImportSection;

	/** Disable reparing when change is not in import section */
	private boolean disableReparsing;

	JavaSyntax debugSyntax = new JavaSyntax(); // !!! debugging syntax

	public JavaImport() {
		posEndOfImportSection = -1;
		disableReparsing = false;
	}

	public synchronized void update(BaseDocument doc) {

		// optimalization of the parsing
		if (disableReparsing)
			return;

		doc.readLock();
		try {
			int scanLen = INIT_SCAN_LEN;
			int docLen = doc.getLength();
			boolean wholeDoc = false;
			do {
				if (scanLen >= docLen) {
					scanLen = docLen;
					wholeDoc = true;
				}
				eotReached = false;
				init();
				try {
					doc.getSyntaxSupport().tokenizeText(this, 0, scanLen, false);
				} catch (BadLocationException e) {
					if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
						e.printStackTrace();
					}
				}
				scanLen *= 4; // increase the scanning size
			} while (!wholeDoc && eotReached);
		} finally {
			doc.readUnlock();
		}
		buffer = null;
	}

	protected void init() {
		exp.setLength(0);
		star = false;
		parsingPackage = false;
		infoList.clear();

		name2Class.clear(); // clear current mappings
		// add java.lang package by default
		JCPackage pkg = JavaCompletion.getFinder().getExactPackage("java.lang"); // NOI18N
		if (pkg != null) {
			JCClass[] classes = pkg.getClasses();
			for (int i = 0; i < classes.length; i++) {
				name2Class.put(classes[i].getName(), classes[i]);
			}
		}

	}

	public JCClass getClazz(String className) {
		JCFinder finder = JavaCompletion.getFinder();
		JCClass ret = (JCClass) name2Class.get(className);// first try package
															// scope
		if (ret == null) {
			ret = finder.getExactClass(className);
		}
		return ret;
	}

	protected void packageStatementFound(int packageStartPos, int packageEndPos, String packageExp) {
		JCPackage pkg = JavaCompletion.getFinder().getExactPackage(packageExp);
		if (pkg != null) {
			JCClass[] classes = pkg.getClasses();
			for (int i = 0; i < classes.length; i++) {
				name2Class.put(classes[i].getName(), classes[i]);
			}
		}
	}

	protected void importStatementFound(int importStartPos, int importEndPos, String importExp, boolean starAtEnd) {
		JCFinder finder = JavaCompletion.getFinder();
		Info info = new Info(importStartPos, importEndPos, starAtEnd);
		JCClass cls = finder.getExactClass(importExp);
		if (cls != null) {
			info.cls = cls;
			if (star) { // !!! dodelat
			}
			else { // only this single class
				name2Class.put(cls.getName(), cls);
			}
		}
		else { // not a direct class, try package
			JCPackage pkg = finder.getExactPackage(importExp);
			if (pkg != null) {
				info.pkg = pkg;
				if (starAtEnd) { // only useful with star
					JCClass[] classes = pkg.getClasses();
					for (int i = 0; i < classes.length; i++) {
						name2Class.put(classes[i].getName(), classes[i]);
					}
				}
			}
			else { // not package, will be class
				String pkgName = importExp;
				String simplePkgName = null;
				int ind;
				while ((ind = pkgName.lastIndexOf('.')) >= 0) {
					pkgName = pkgName.substring(0, ind);
					if (simplePkgName == null) {
						simplePkgName = pkgName;
					}
					pkg = finder.getExactPackage(pkgName);
					if (pkg != null) { // found valid package, but unknown
										// class
						cls = JavaCompletion.getSimpleClass(importExp, pkgName.length());
						info.cls = cls;
						info.unknownImport = importExp;
						if (star) {
							// don't add in this case, can change in the future
						}
						else {
							name2Class.put(cls.getName(), cls);
						}
						break;
					}
				}

				if (cls == null) {
					// didn't found a direct package, assume last is class name
					if (simplePkgName != null) { // at least one dot in
													// importExp
						cls = JavaCompletion.getSimpleClass(importExp, simplePkgName.length());
						if (star) {
							// don't add in this case, can change in the future
						}
						else {
							name2Class.put(cls.getName(), cls);
						}
					}
				}
			}
		}
		if ((info.cls == null) && (info.pkg == null)) {
			info.unknownImport = importExp;
		}
		infoList.add(info);
	}

	/**
	 * Returns true if className is in import, but in a package, that hasn't
	 * updated DB
	 */
	public boolean isUnknownImport(String className) {
		for (int i = 0; i < infoList.size(); i++) {
			String unknown = ((Info) infoList.get(i)).unknownImport;
			if ((unknown != null) && (unknown.indexOf(className) > -1))
				return true;
		}
		return false;
	}

	/** Returns all imports that aren't in parser DB yet */
	protected List getUnknownImports() {
		ArrayList ret = new ArrayList();
		for (int i = 0; i < infoList.size(); i++) {
			String unknownImport = ((Info) infoList.get(i)).unknownImport;
			if (unknownImport != null) {
				if (((Info) infoList.get(i)).star)
					unknownImport = unknownImport + ".*"; // NOI18N
				ret.add(unknownImport);
			}
		}
		return ret;
	}

	/**
	 * Returns true if the given class is in the import statement directly or
	 * indirectly (package.name.*)
	 */
	public boolean isImported(JCClass cls) {
		if (cls == null)
			return false;

		String clsName = cls.getFullName();
		String pkgName = cls.getPackageName();

		for (int i = 0; i < infoList.size(); i++) {
			JCClass infoClass = ((Info) infoList.get(i)).cls;
			JCPackage infoPackage = ((Info) infoList.get(i)).pkg;

			if ((clsName != null) && (infoClass != null)) {
				if (clsName.equals(infoClass.getFullName())) {
					return true;
				}
			}
			if ((pkgName != null) && (infoPackage != null)) {
				if (pkgName.equals(infoPackage.getName())) {
					return true;
				}
			}

		}
		return false;
	}

	public boolean token(TokenID tokenID, TokenContextPath tokenContextPath, int tokenOffset, int tokenLen) {
		boolean cont = true;

		switch (tokenID.getNumericID()) {
		case JavaTokenContext.IDENTIFIER_ID:
			switch (state) {
			case AFTER_IMPORT:
				expPos = tokenOffset;
				state = INSIDE_EXP;
				break;

			case INSIDE_MIXED_EXP:
				exp.append(buffer, tokenOffset, tokenLen);
				// let it flow to INSIDE_EXP
			case INSIDE_EXP:
				if (star) { // not allowed after star was found
					cont = false;
				}
				break;
			}
			break;

		case JavaTokenContext.DOT_ID:
			switch (state) {
			case INIT: // ignore standalone dot
				break;

			case AFTER_IMPORT:
				cont = false; // dot after import keyword
				break;

			case INSIDE_MIXED_EXP:
				exp.append('.');
				// let it flow to INSIDE_EXP
			case INSIDE_EXP:
				if (star) { // not allowed after star was found
					cont = false;
				}
				break;
			}
			break;

		case JavaTokenContext.SEMICOLON_ID:
			String impExp = null;
			switch (state) {
			case INIT: // ignore semicolon
				break;

			case AFTER_IMPORT: // semicolon after import kwd
				cont = false;
				break;

			case INSIDE_EXP:
				impExp = new String(buffer, expPos, (star ? (tokenOffset - 2) : tokenOffset) - expPos);
				break;

			case INSIDE_MIXED_EXP:
				impExp = exp.toString();
				exp.setLength(0);
				break;
			}

			if (impExp != null) {
				if (parsingPackage) {
					packageStatementFound(startPos, tokenOffset + 1, impExp);
				}
				else { // parsing import statement
					importStatementFound(startPos, tokenOffset + 1, impExp, star);
				}
				star = false;
				parsingPackage = false;
				state = INIT;
			}
			break;

		case JavaTokenContext.MUL_ID:
			if (star || parsingPackage) {
				cont = false;
			}
			else {
				switch (state) {
				case INIT: // ignore star at the begining
					break;

				case AFTER_IMPORT:
					cont = false; // star after import kwd
					break;

				case INSIDE_EXP:
					star = true;
					if (tokenOffset == 0 || buffer[tokenOffset - 1] != '.') {
						cont = false;
					}
					break;

				case INSIDE_MIXED_EXP:
					int len = exp.length();
					if (len > 0 && exp.charAt(len - 1) == '.') {
						exp.setLength(len - 1); // remove ending dot
						star = true;
					}
					else { // error
						cont = false;
					}
					break;
				}
			}
			break;

		case JavaTokenContext.PACKAGE_ID:
			switch (state) {
			case INIT:
				parsingPackage = true;
				state = AFTER_IMPORT; // the same state is used
				break;

			default:
				cont = false; // error in other states
				break;
			}
			break;

		case JavaTokenContext.IMPORT_ID:
			switch (state) {
			case INIT:
				parsingPackage = false;
				state = AFTER_IMPORT;
				startPos = tokenOffset;
				break;

			default:
				cont = false; // error in other states
				break;
			}
			break;

		case JavaTokenContext.WHITESPACE_ID:
		case JavaTokenContext.LINE_COMMENT_ID:
		case JavaTokenContext.BLOCK_COMMENT_ID:
			switch (state) {
			case INSIDE_EXP:
				if (tokenOffset - expPos < 0) {
					cont = false;
					break;
				}
				// Need to continue as string
				exp.append(buffer, expPos, tokenOffset - expPos);
				state = INSIDE_MIXED_EXP;
				break;
			}
			break;

		default:
			// when we get here, it means that all packages and imports
			// were already parsed. the rest of the document will be skipped
			// and so this is right place to set end of import section
			if (posEndOfImportSection == -1 || tokenOffset + tokenLen > posEndOfImportSection)
				posEndOfImportSection = tokenOffset + tokenLen;
			cont = false;
			break;
		}

		return cont;
	}

	private String debugState(int state) {
		switch (state) {
		case INIT:
			return "INIT"; // NOI18N
		case AFTER_IMPORT:
			return "AFTER_IMPORT"; // NOI18N
		case INSIDE_EXP:
			return "INSIDE_EXP"; // NOI18N
		case INSIDE_MIXED_EXP:
			return "INSIDE_MIXED_EXP"; // NOI18N
		}
		return "UNKNOWN STATE"; // NOI18N
	}

	public int eot(int offset) {
		eotReached = true; // will be rescanned
		return 0;
	}

	public void nextBuffer(char[] buffer, int offset, int len, int startPos, int preScan, boolean lastBuffer) {
		this.buffer = buffer;
	}

	/**
	 * Optimalization for document parsing. The owner of JavaImport instance can
	 * call this function to inform the JavaImport where the change has occured
	 * in the document. If this function is not called, the whole document is
	 * parsed. If it is, the parsing is done only when the import section of the
	 * document is being modified.
	 * 
	 * @param offset
	 *            offset of the change in document
	 */
	public void documentModifiedAtPosition(int offset) {
		// if end of import section has already been found, then check
		// if change is in import section or not
		if (posEndOfImportSection != -1) {
			if (offset > posEndOfImportSection) {
				// reparing is not necessary, because change is after the import
				// section
				disableReparsing = true;
			}
			else {
				// the document must be completely reparsed
				disableReparsing = false;
				posEndOfImportSection = -1;
			}
		}
	}

	class Info {

		Info(int startPos, int endPos, boolean star) {
			this.startPos = startPos;
			this.endPos = endPos;
			this.star = star;
		}

		int startPos;

		int endPos;

		boolean star;

		JCPackage pkg;

		JCClass cls;

		String unknownImport;

	}

}
