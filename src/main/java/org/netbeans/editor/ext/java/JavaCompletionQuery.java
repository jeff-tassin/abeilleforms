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
import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Formatter;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.ext.CompletionQuery;
import org.netbeans.editor.ext.ExtFormatter;

/**
 * Java completion support finder
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JavaCompletionQuery implements CompletionQuery {

	public CompletionQuery.Result query(JTextComponent component, int offset, SyntaxSupport support) {
		return query(component, offset, support, false);
	}

	/**
	 * Perform the query on the given component. The query usually gets the
	 * component's document, the caret position and searches back to find the
	 * last command start. Then it inspects the text up to the caret position
	 * and returns the result.
	 * 
	 * @param component
	 *            the component to use in this query.
	 * @param offset
	 *            position in the component's document to which the query will
	 *            be performed. Usually it's a caret position.
	 * @param support
	 *            syntax-support that will be used during resolving of the
	 *            query.
	 * @param sourceHelp
	 *            whether the help is retrieved to open the source file. The
	 *            query behavior is slightly modified if this flag is true
	 * @return result of the query or null if there's no result.
	 */
	public CompletionQuery.Result query(JTextComponent component, int offset, SyntaxSupport support, boolean sourceHelp) {
		BaseDocument doc = (BaseDocument) component.getDocument();
		JavaSyntaxSupport sup = (JavaSyntaxSupport) support.get(JavaSyntaxSupport.class);
		JavaResult ret = null;

		try {
			// find last separator position
			int lastSepOffset = sup.getLastCommandSeparator(offset);
			JCTokenProcessor tp = new JCTokenProcessor();
			sup.tokenizeText(tp, lastSepOffset + 1, offset, true);

			// Check whether there's a comment under the cursor
			boolean inComment = false;
			TokenID lastValidTokenID = tp.getLastValidTokenID();
			if (lastValidTokenID != null) {
				switch (lastValidTokenID.getNumericID()) {
				case JavaTokenContext.BLOCK_COMMENT_ID:
					if (tp.getLastValidTokenText() == null || !tp.getLastValidTokenText().endsWith("*/")) {
						inComment = true;
					}
					break;

				case JavaTokenContext.LINE_COMMENT_ID:
					inComment = true;
					break;
				}
			}

			if (!inComment) {
				// refresh classes info before querying
				sup.refreshClassInfo();

				Context ctx = new Context(tp, component, sup, sourceHelp, offset);
				JCExpression exp = tp.getResultExp();
				ctx.resolveExp(exp);
				ret = ctx.result;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Finds the fields, methods and the inner classes.
	 */
	static List findFieldsAndMethods(JCFinder finder, JCClass cls, String name, boolean exactMatch, boolean staticOnly, boolean inspectOuterClasses) {
		// Find inner classes
		List ret = new ArrayList();
		if (staticOnly) {
			JCPackage pkg = finder.getExactPackage(cls.getPackageName());
			if (pkg != null) {
				ret = finder.findClasses(pkg, cls.getName() + '.' + name, false);
			}
		}

		// Add fields
		ret.addAll(finder.findFields(cls, name, exactMatch, staticOnly, inspectOuterClasses));
		// Add methods
		ret.addAll(finder.findMethods(cls, name, exactMatch, staticOnly, inspectOuterClasses));
		return ret;
	}

	static class Context {

		/** Token processor used for parsing the input */
		private JCTokenProcessor tp;

		/** Text component */
		private JTextComponent component;

		/** Syntax support for the given document */
		private JavaSyntaxSupport sup;

		/**
		 * Whether get the source help or not. The source help has slightly
		 * different handling in some situations.
		 */
		private boolean sourceHelp;

		/** End position of the scanning - usually the caret position */
		private int endOffset;

		/**
		 * If set to true true - find the type of the result expression. It's
		 * stored in the lastType variable or lastPkg if it's a package. The
		 * result variable is not populated. False means that the code
		 * completion output should be collected.
		 */
		private boolean findType;

		/**
		 * Whether currently scanning either the package or the class name so
		 * the results should limit the search to the static fields and methods.
		 */
		private boolean staticOnly = true;

		/** Last package found when scanning dot expression */
		private JCPackage lastPkg;

		/** Last type found when scanning dot expression */
		private JCType lastType;

		/** Result list when code completion output is generated */
		private JavaResult result;

		/** Helper flag for recognizing constructors */
		private boolean isConstructor;

		public Context(JCTokenProcessor tp, JTextComponent component, JavaSyntaxSupport sup, boolean sourceHelp, int endOffset) {
			this.tp = tp;
			this.component = component;
			this.sup = sup;
			this.sourceHelp = sourceHelp;
			this.endOffset = endOffset;
		}

		public void setFindType(boolean findType) {
			this.findType = findType;
		}

		protected Object clone() {
			return new Context(tp, component, sup, sourceHelp, endOffset);
		}

		/*
		 * private List getBaseHelp(String baseName) { if (sourceHelp) {
		 * JCFinder finder = JavaCompletion.getFinder(); List res =
		 * finder.findPackages(baseName, false, false); // find all subpackages
		 * if (res == null) { res = new ArrayList(); }
		 * 
		 * if (baseName != null && baseName.length() > 0) {
		 * res.addAll(finder.findClasses(null, baseName, false)); // add
		 * matching classes } return res; } return null; }
		 * 
		 * private JavaResult getBaseHelpResult(String baseName, JCExpression
		 * exp) { List res = getBaseHelp(baseName); if (res != null && exp !=
		 * null) { return new JavaResult(res, formatName(baseName, true), exp,
		 * exp.getTokenOffset(0), 0, 0); } return null; }
		 */

		private String formatName(String name, boolean appendStar) {
			return (name != null) ? (appendStar ? (name + '*') : name) : (appendStar ? "*" : ""); // NOI18N

		}

		private String formatType(JCType type, boolean useFullName, boolean appendDot, boolean appendStar) {
			StringBuffer sb = new StringBuffer();
			if (type != null) {
				sb.append(type.format(useFullName));
			}
			if (appendDot) {
				sb.append('.');
			}
			if (appendStar) {
				sb.append('*');
			}
			return sb.toString();
		}

		private JCType resolveType(JCExpression exp) {
			Context ctx = (Context) clone();
			ctx.setFindType(true);
			JCType typ = null;
			if (ctx.resolveExp(exp)) {
				typ = ctx.lastType;
			}
			return typ;
		}

		boolean resolveExp(JCExpression exp) {
			boolean lastDot = false; // dot at the end of the whole
										// expression?
			JCFinder finder = JavaCompletion.getFinder();
			boolean ok = true;

			switch (exp.getExpID()) {
			case JCExpression.DOT_OPEN: // Dot expression with the dot at the
										// end
				lastDot = true;
				// let it flow to DOT
			case JCExpression.DOT: // Dot expression
				int parmCnt = exp.getParameterCount(); // Number of items in
														// the dot exp

				for (int i = 0; i < parmCnt && ok; i++) { // resolve all items
															// in a dot exp
					ok = resolveItem(exp.getParameter(i), (i == 0), (!lastDot && i == parmCnt - 1));
				}

				if (ok && lastDot) { // Found either type or package help
					// Need to process dot at the end of the expression
					int tokenCntM1 = exp.getTokenCount() - 1;
					int substPos = exp.getTokenOffset(tokenCntM1) + exp.getTokenLength(tokenCntM1);
					if (lastType != null) { // Found type
						JCClass cls;
						if (lastType.getArrayDepth() == 0) { // Not array
							cls = lastType.getClazz();
						}
						else { // Array of some depth
							cls = JavaCompletion.OBJECT_CLASS_ARRAY; // Use
																		// Object
																		// in
																		// this
																		// case
						}
						List res;
						if (sourceHelp) {
							res = new ArrayList();
							res.add(lastType.getClazz());
						}
						else { // not source-help
							res = findFieldsAndMethods(finder, cls, "", false, staticOnly, false); // NOI18N
						}
						// Get all fields and methods of the cls
						result = new JavaResult(component, res, formatType(lastType, true, true, true), exp, substPos, 0, cls.getName().length() + 1);
					}
					else { // Found package (otherwise ok would be false)
						String searchPkg = lastPkg.getName() + '.';
						List res;
						if (sourceHelp) {
							res = new ArrayList();
							res.add(lastPkg); // return only the package
						}
						else {
							res = finder.findPackages(searchPkg, false, false); // find
																				// all
																				// subpackages
							res.addAll(Arrays.asList(lastPkg.getClasses())); // package
																				// classes
						}
						result = new JavaResult(component, res, searchPkg + '*', exp, substPos, 0, 0);
					}
				}
				break;

			case JCExpression.NEW: // 'new' keyword
				List res = finder.findClasses(null, "", false); // Find all
																// classes by
																// name //
																// NOI18N
				result = new JavaResult(component, res, "*", exp, endOffset, 0, 0); // NOI18N
				break;

			default: // The rest of the situations is resolved as a singleton
						// item
				ok = resolveItem(exp, true, true);
				break;
			}

			return ok;
		}

		/**
		 * Resolve one item from the expression connected by dots.
		 * 
		 * @param item
		 *            expression item to resolve
		 * @param first
		 *            whether this expression is the first one in a dot
		 *            expression
		 * @param last
		 *            whether this expression is the last one in a dot
		 *            expression
		 */
		boolean resolveItem(JCExpression item, boolean first, boolean last) {
			boolean cont = true; // whether parsing should continue or not
			boolean methodOpen = false; // helper flag for unclosed methods
			JCFinder finder = JavaCompletion.getFinder();

			switch (item.getExpID()) {
			case JCExpression.CONSTANT: // Constant item
				if (first) {
					lastType = item.getType(); // Get the constant type
					staticOnly = false;
				}
				else { // Not the first item in a dot exp
					cont = false; // impossible to have constant inside the
									// expression
				}
				break;

			case JCExpression.VARIABLE: // Variable or special keywords
				switch (item.getTokenID(0).getNumericID()) {
				case JavaTokenContext.THIS_ID: // 'this' keyword
					if (first) { // first item in expression
						JCClass cls = sup.getClass(item.getTokenOffset(0));
						if (cls != null) {
							lastType = JavaCompletion.getType(cls, 0);
							staticOnly = false;
						}
					}
					else { // 'something.this'
						staticOnly = false;
					}
					break;

				case JavaTokenContext.SUPER_ID: // 'super' keyword
					if (first) { // only allowed as the first item
						JCClass cls = sup.getClass(item.getTokenOffset(0));
						if (cls != null) {
							cls = finder.getExactClass(cls.getFullName());
							if (cls != null) {
								cls = cls.getSuperclass();
								if (cls != null) {
									lastType = JavaCompletion.getType(cls, 0);
									staticOnly = false;
								}
							}
						}
					}
					else {
						cont = false;
					}
					break;

				case JavaTokenContext.CLASS_ID: // 'class' keyword
					if (!first) {
						lastType = JavaCompletion.CLASS_TYPE;
						staticOnly = false;
					}
					else {
						cont = false;
					}
					break;

				default: // Regular constant
					String var = item.getTokenText(0);
					int varPos = item.getTokenOffset(0);
					if (first) { // try to find variable for the first item
						if (last && !findType) { // both first and last item
							List res = new ArrayList();
							JCClass cls = sup.getClass(varPos); // get document
																// class
							if (cls != null) {
								res.addAll(findFieldsAndMethods(finder, cls, var, false, sup.isStaticBlock(varPos), true));
							}
							if (var.length() > 0 || !sourceHelp) {
								res.addAll(finder.findPackages(var, false, false)); // add
																					// matching
																					// packages
								if (var.length() > 0) { // if at least one char
									res.addAll(finder.findClasses(null, var, false)); // add
																						// matching
																						// classes
									if (cls != null) {
										// add matching inner classes too
										JCPackage pkg = finder.getExactPackage(cls.getPackageName());
										res.addAll(finder.findClasses(pkg, cls.getName() + "." + var, false));
									}
								}
							}
							result = new JavaResult(component, res, var + '*', item, 0);
						}
						else { // not last item or finding type
							lastType = (JCType) sup.findType(var, varPos);
							if (lastType != null) { // variable found
								staticOnly = false;
							}
							else { // no variable found
								lastPkg = finder.getExactPackage(var); // try
																		// package
								if (lastPkg == null) { // not package, let's
														// try class name
									JCClass cls = sup.getClassFromName(var, true);
									if (cls != null) {
										lastType = JavaCompletion.getType(cls, 0);
									}
									else { // class not found
										cont = false;
									}
								}
							}
						}
					}
					else { // not the first item
						if (lastType != null) { // last was type
							if (findType || !last) {
								boolean inner = false;
								int ad = lastType.getArrayDepth();
								if (staticOnly && ad == 0) { // can be inner
																// class
									JCClass cls = finder.getExactClass(lastType.getClazz().getFullName() + "." + var); // NOI18N
									if (cls != null) {
										lastType = JavaCompletion.getType(cls, 0);
										inner = true;
									}
								}

								if (!inner) { // not inner class name
									if (ad == 0) { // zero array depth
										List fldList = finder.findFields(lastType.getClazz(), var, true, staticOnly, false);
										if (fldList.size() > 0) { // match
																	// found
											JCField fld = (JCField) fldList.get(0);
											lastType = fld.getType();
											staticOnly = false;
										}
										else { // no match found
											lastType = null;
											cont = false;
										}
									}
									else { // array depth > 0 but no array
											// dereference
										cont = false;
									}
								}
							}
							else { // last and searching for completion output
								JCClass cls;
								if (lastType.getArrayDepth() == 0) { // Not
																		// array
									cls = lastType.getClazz();
								}
								else { // Array of some depth
									cls = JavaCompletion.OBJECT_CLASS_ARRAY; // Use
																				// Object
																				// in
																				// this
																				// case
								}
								result = new JavaResult(component, findFieldsAndMethods(finder, cls, var, false, staticOnly, false), lastType.format(false)
										+ '.' + var + '*', item, cls.getName().length() + 1);
							}
						}
						else { // currently package
							String searchName = lastPkg.getName() + '.' + var;
							if (findType || !last) {
								lastPkg = finder.getExactPackage(searchName);
								if (lastPkg == null) { // package doesn't exist
									JCClass cls = finder.getExactClass(searchName);
									if (cls != null) {
										lastType = JavaCompletion.getType(cls, 0);
									}
									else {
										lastType = null;
										cont = false;
									}
								}
							}
							else { // last and searching for completion output
								if (last) { // get all matching
											// fields/methods/packages
									String searchPkg = lastPkg.getName() + '.' + var;
									List res = finder.findPackages(searchPkg, false, false); // find
																								// matching
																								// subpackages
									res.addAll(finder.findClasses(lastPkg, var, false)); // matching
																							// classes
									result = new JavaResult(component, res, searchPkg + '*', item, 0);
								}
							}
						}
					}
					break;

				}
				break;

			case JCExpression.ARRAY:
				cont = resolveItem(item.getParameter(0), first, false);
				if (cont) {
					cont = false;
					if (lastType != null) { // must be type
						if (item.getParameterCount() == 2) { // index in
																// array follows
							JCType arrayType = resolveType(item.getParameter(1));
							if (arrayType != null && arrayType.equals(JavaCompletion.INT_TYPE)) {
								lastType = JavaCompletion.getType(lastType.getClazz(), Math.max(lastType.getArrayDepth() - 1, 0));
								cont = true;
							}
						}
						else { // no index, increase array depth
							lastType = JavaCompletion.getType(lastType.getClazz(), lastType.getArrayDepth() + 1);
							cont = true;
						}
					}
				}
				break;

			case JCExpression.INSTANCEOF:
				lastType = JavaCompletion.BOOLEAN_TYPE;
				break;

			case JCExpression.OPERATOR:
				List res = new ArrayList();
				JCClass curCls = sup.getClass(item.getTokenOffset(0)); // 
				if (curCls != null) { // find all methods and fields for
										// "this" class
					res.addAll(findFieldsAndMethods(finder, curCls, "", false, sup.isStaticBlock(item.getTokenOffset(0)), true));
				}
				res.addAll(finder.findPackages("", false, false)); // find all
																	// packages
				res.addAll(finder.findClasses(null, "", false)); // find all
																	// classes

				result = new JavaResult(component, res, "*", item, endOffset, 0, 0);

				switch (item.getTokenID(0).getNumericID()) {
				case JavaTokenContext.EQ_ID: // Assignment operators
				case JavaTokenContext.PLUS_EQ_ID:
				case JavaTokenContext.MINUS_EQ_ID:
				case JavaTokenContext.MUL_EQ_ID:
				case JavaTokenContext.DIV_EQ_ID:
				case JavaTokenContext.AND_EQ_ID:
				case JavaTokenContext.OR_EQ_ID:
				case JavaTokenContext.XOR_EQ_ID:
				case JavaTokenContext.MOD_EQ_ID:
				case JavaTokenContext.LSHIFT_EQ_ID:
				case JavaTokenContext.RSSHIFT_EQ_ID:
				case JavaTokenContext.RUSHIFT_EQ_ID:
					if (item.getParameterCount() > 0) {
						lastType = resolveType(item.getParameter(0));
						staticOnly = false;
					}
					break;

				case JavaTokenContext.LT_ID: // Binary, result is boolean
				case JavaTokenContext.GT_ID:
				case JavaTokenContext.LT_EQ_ID:
				case JavaTokenContext.GT_EQ_ID:
				case JavaTokenContext.EQ_EQ_ID:
				case JavaTokenContext.NOT_EQ_ID:
				case JavaTokenContext.AND_AND_ID: // Binary, result is boolean
				case JavaTokenContext.OR_OR_ID:
					lastType = JavaCompletion.BOOLEAN_TYPE;
					break;

				case JavaTokenContext.LSHIFT_ID: // Always binary
				case JavaTokenContext.RSSHIFT_ID:
				case JavaTokenContext.RUSHIFT_ID:
				case JavaTokenContext.MUL_ID:
				case JavaTokenContext.DIV_ID:
				case JavaTokenContext.AND_ID:
				case JavaTokenContext.OR_ID:
				case JavaTokenContext.XOR_ID:
				case JavaTokenContext.MOD_ID:

				case JavaTokenContext.PLUS_ID:
				case JavaTokenContext.MINUS_ID:
					switch (item.getParameterCount()) {
					case 2:
						JCType typ1 = resolveType(item.getParameter(0));
						JCType typ2 = resolveType(item.getParameter(1));
						if (typ1 != null && typ2 != null && typ1.getArrayDepth() == 0 && typ2.getArrayDepth() == 0
								&& JavaCompletion.isPrimitiveClass(typ1.getClazz()) && JavaCompletion.isPrimitiveClass(typ2.getClazz())) {
							lastType = JCUtilities.getCommonType(typ1, typ2);
						}
						break;
					case 1: // get the only one parameter
						JCType typ = resolveType(item.getParameter(0));
						if (typ != null && JavaCompletion.isPrimitiveClass(typ.getClazz())) {
							lastType = typ;
						}
						break;
					}
					break;

				case JavaTokenContext.COLON_ID:
					switch (item.getParameterCount()) {
					case 2:
						JCType typ1 = resolveType(item.getParameter(0));
						JCType typ2 = resolveType(item.getParameter(1));
						if (typ1 != null && typ2 != null) {
							lastType = JCUtilities.getCommonType(typ1, typ2);
						}
						break;

					case 1:
						lastType = resolveType(item.getParameter(0));
						break;
					}
					break;

				case JavaTokenContext.QUESTION_ID:
					if (item.getParameterCount() >= 2) {
						lastType = resolveType(item.getParameter(1)); // should
																		// be
																		// colon
					}
					break;
				}
				break;

			case JCExpression.UNARY_OPERATOR:
				if (item.getParameterCount() > 0) {
					lastType = resolveType(item.getParameter(0));
				}
				break;

			case JCExpression.CONVERSION:
				lastType = resolveType(item.getParameter(0));
				staticOnly = false;
				break;

			case JCExpression.TYPE:
				lastType = item.getType();
				break;

			case JCExpression.PARENTHESIS:
				cont = resolveItem(item.getParameter(0), first, last);
				break;

			case JCExpression.CONSTRUCTOR: // constructor can be part of a DOT
											// expression
				isConstructor = true;
				cont = resolveExp(item.getParameter(0));
				staticOnly = false;
				break;

			case JCExpression.METHOD_OPEN: // Unclosed method
				methodOpen = true;
				// let it flow to method
			case JCExpression.METHOD: // Closed method
				String mtdName = item.getTokenText(0);

				// this() invoked, offer constructors
				if (("this".equals(mtdName)) && (item.getTokenCount() > 0)) { // NOI18N
					JCClass cls = sup.getClass(item.getTokenOffset(0));
					if (cls != null) {
						cls = finder.getExactClass(cls.getFullName());
						if (cls != null) {
							isConstructor = true;
							mtdName = cls.getName();
						}
					}
				}

				// super() invoked, offer constructors for super class
				if (("super".equals(mtdName)) && (item.getTokenCount() > 0)) { // NOI18N
					JCClass cls = sup.getClass(item.getTokenOffset(0));
					if (cls != null) {
						cls = finder.getExactClass(cls.getFullName());
						if (cls != null) {
							cls = cls.getSuperclass();
							if (cls != null) {
								isConstructor = true;
								mtdName = cls.getName();
							}
						}
					}
				}

				if (isConstructor) { // Help for the constructor
					JCClass cls = null;
					if (first) {
						cls = sup.getClassFromName(mtdName, true);
					}
					else { // not first
						if ((last) && (lastPkg != null)) { // valid package
							cls = JCUtilities.getExactClass(finder, mtdName, lastPkg.getName());
						}
						else if (lastType != null) {
							if (last) { // inner class
								cls = JCUtilities.getExactClass(finder, mtdName, lastType.getClazz().getFullName());
							}
							else {
								if (lastType.getArrayDepth() == 0) { // Not
																		// array
									cls = lastType.getClazz();
								}
								else { // Array of some depth
									cls = JavaCompletion.OBJECT_CLASS_ARRAY; // Use
																				// Object
																				// in
																				// this
																				// case
								}
							}
						}
					}
					if (cls != null) {
						lastType = JavaCompletion.getType(cls, 0);
						List ctrList = JCUtilities.getConstructors(cls);
						String parmStr = "*"; // NOI18N
						List typeList = getTypeList(item);
						List filtered = JCUtilities.filterMethods(ctrList, typeList, methodOpen);
						if (filtered.size() > 0) {
							ctrList = filtered;
							parmStr = formatTypeList(typeList, methodOpen);
						}
						List mtdList = finder.findMethods(cls, mtdName, true, false, first);
						if (mtdList.size() > 0) {
							if (last && !findType) {
								result = new JavaResult(component, mtdList, lastType.getClazz().getFullName() + '.' + mtdName + '(' + parmStr + ')', item,
										endOffset, 0, 0);
							}
							else {
								lastType = ((JCMethod) mtdList.get(0)).getReturnType();
								staticOnly = false;
							}
						}
						else {
							result = new JavaResult(component, ctrList, mtdName + '(' + parmStr + ')', item, endOffset, 0, 0);
						}
					}
					else {
						isConstructor = false;
					}
				}
				if (isConstructor == false) {
					// Help for the method
					if (first) {
						JCClass cls = sup.getClass(item.getTokenOffset(0));
						if (cls != null) {
							lastType = JavaCompletion.getType(cls, 0);
						}
					}

					if (lastType != null) {
						JCClass cls;
						if (lastType.getArrayDepth() == 0) { // Not array
							cls = lastType.getClazz();
						}
						else { // Array of some depth
							cls = JavaCompletion.OBJECT_CLASS_ARRAY; // Use
																		// Object
																		// in
																		// this
																		// case
						}

						List mtdList = finder.findMethods(cls, mtdName, true, false, first);
						String parmStr = "*"; // NOI18N
						List typeList = getTypeList(item);
						List filtered = JCUtilities.filterMethods(mtdList, typeList, methodOpen);
						if (filtered.size() > 0) {
							mtdList = filtered;
							parmStr = formatTypeList(typeList, methodOpen);
						}
						if (mtdList.size() > 0) {
							if (last && !findType) {
								result = new JavaResult(component, mtdList, lastType.getClazz().getFullName() + '.' + mtdName + '(' + parmStr + ')', item,
										endOffset, 0, 0);
							}
							else {
								if (mtdList.size() > 0) {
									lastType = ((JCMethod) mtdList.get(0)).getReturnType();
									staticOnly = false;
								}
							}
						}
						else {
							lastType = null; // no method found
							cont = false;
						}
					}
					else { // package.method() is invalid
						lastPkg = null;
						cont = false;
					}
				}
				break;
			}

			if (lastType == null && lastPkg == null) { // !!! shouldn't be
														// necessary
				cont = false;
			}

			return cont;
		}

		private List getTypeList(JCExpression item) {
			int parmCnt = item.getParameterCount();
			ArrayList typeList = new ArrayList();
			if (parmCnt > 0) { // will try to filter by parameters
				boolean methodOpen = (item.getExpID() == JCExpression.METHOD_OPEN);
				for (int i = 0; i < parmCnt; i++) {
					JCExpression parm = item.getParameter(i);
					JCType typ = resolveType(parm);
					typeList.add(typ);
				}
			}
			return typeList;
		}

	}

	private static String formatTypeList(List typeList, boolean methodOpen) {
		StringBuffer sb = new StringBuffer();
		if (typeList.size() > 0) {
			int cntM1 = typeList.size() - 1;
			for (int i = 0; i <= cntM1; i++) {
				JCType t = (JCType) typeList.get(i);
				if (t != null) {
					sb.append(t.format(false));
				}
				else {
					sb.append('?');
				}
				if (i < cntM1) {
					sb.append(", "); // NOI18N
				}
			}
			if (methodOpen) {
				sb.append(", *"); // NOI18N
			}
		}
		else { // no parameters
			if (methodOpen) {
				sb.append("*"); // NOI18N
			}
		}
		return sb.toString();
	}

	public static class JavaResult extends CompletionQuery.AbstractResult {

		/**
		 * First offset in the name of the (inner) class to be displayed. It's
		 * used to display the inner classes of the main class to exclude the
		 * initial part of the name.
		 */
		private int classDisplayOffset;

		/** Expression to substitute */
		private JCExpression substituteExp;

		/** Starting position of the text to substitute */
		private int substituteOffset;

		/** Length of the text to substitute */
		private int substituteLength;

		/** Component to update */
		private JTextComponent component;

		public JavaResult(JTextComponent component, List data, String title, JCExpression substituteExp, int classDisplayOffset) {
			this(component, data, title, substituteExp, substituteExp.getTokenOffset(0), substituteExp.getTokenLength(0), classDisplayOffset);
		}

		public JavaResult(JTextComponent component, List data, String title, JCExpression substituteExp, int substituteOffset, int substituteLength,
				int classDisplayOffset) {
			super(data, title);

			this.component = component;
			this.substituteExp = substituteExp;
			this.substituteOffset = substituteOffset;
			this.substituteLength = substituteLength;
			this.classDisplayOffset = classDisplayOffset;
		}

		/**
		 * Get the text that is normally filled into the text if enter is
		 * pressed.
		 */
		protected String getMainText(Object dataItem) {
			String text = null;
			if (dataItem instanceof JCPackage) {
				text = ((JCPackage) dataItem).getLastName();
			}
			else if (dataItem instanceof JCClass) {
				text = ((JCClass) dataItem).getName();
				if (classDisplayOffset > 0 && classDisplayOffset < text.length()) { // Only
																					// the
																					// last
																					// name
																					// for
																					// inner
																					// classes
					text = text.substring(classDisplayOffset);
				}
			}
			else if (dataItem instanceof JCField) {
				text = ((JCField) dataItem).getName();
			}
			else if (dataItem instanceof JCMethod) {
				JCMethod mtd = (JCMethod) dataItem;
				text = mtd.getName();
			}
			else if (dataItem instanceof JCConstructor) {
				text = ((JCConstructor) dataItem).getClazz().getName();
			}
			return text;
		}

		/** Get the text that is common to all the entries in the query-result */
		protected String getCommonText(String prefix) {
			List data = getData();
			int cnt = data.size();
			int prefixLen = prefix.length();
			String commonText = null;
			for (int i = 0; i < cnt; i++) {
				String mainText = getMainText(data.get(i));
				if (mainText != null && mainText.startsWith(prefix)) {
					mainText = mainText.substring(prefixLen);
					if (commonText == null) {
						commonText = mainText;
					}
					// Get largest common part
					int minLen = Math.min(mainText.length(), commonText.length());
					int commonInd;
					for (commonInd = 0; commonInd < minLen; commonInd++) {
						if (mainText.charAt(commonInd) != commonText.charAt(commonInd)) {
							break;
						}
					}
					if (commonInd != 0) {
						commonText = commonText.substring(0, commonInd);
					}
					else {
						return null; // no common text
					}
				}
			}
			return prefix + ((commonText != null) ? commonText : ""); // NOI18N
		}

		/**
		 * Update the text in response to pressing TAB key.
		 * 
		 * @return whether the text was successfully updated
		 */
		public boolean substituteCommonText(int dataIndex) {
			BaseDocument doc = (BaseDocument) component.getDocument();
			try {
				String prefix = doc.getText(substituteOffset, substituteLength);
				String commonText = getCommonText(prefix);
				if (commonText != null) {
					if (substituteExp != null) {
						if ((substituteExp.getExpID() == JCExpression.METHOD_OPEN) || (substituteExp.getExpID() == JCExpression.METHOD))
							return true;
					}
					doc.atomicLock();
					try {
						doc.remove(substituteOffset, substituteLength);
						doc.insertString(substituteOffset, commonText, null);
					} finally {
						doc.atomicUnlock();
					}
				}
			} catch (BadLocationException e) {
				// no updating
			}
			return true;
		}

		/**
		 * Update the text in response to pressing ENTER.
		 * 
		 * @return whether the text was successfully updated
		 */
		public boolean substituteText(int dataIndex, boolean shift) {
			BaseDocument doc = (BaseDocument) component.getDocument();
			String text = null;
			int selectionStartOffset = -1;
			int selectionEndOffset = -1;
			Object replacement = getData().get(dataIndex);

			if (replacement instanceof JCPackage) {
				text = ((JCPackage) replacement).getLastName();

			}
			else if (replacement instanceof JCClass) {
				text = ((JCClass) replacement).getName();
				if (classDisplayOffset > 0 && classDisplayOffset < text.length()) { // Only
																					// the
																					// last
																					// name
																					// for
																					// inner
																					// classes
					text = text.substring(classDisplayOffset);
				}

			}
			else if (replacement instanceof JCField) {
				text = ((JCField) replacement).getName();

			}
			else if (replacement instanceof JCConstructor) {
				JCConstructor mtd = (JCConstructor) replacement;
				switch ((substituteExp != null) ? substituteExp.getExpID() : -1) {
				case JCExpression.METHOD:
					// no substitution
					break;

				case JCExpression.METHOD_OPEN:
					JCParameter[] parms = mtd.getParameters();
					if (parms.length == 0) {
						text = ")"; // NOI18N
					}
					else { // one or more parameters
						int ind = substituteExp.getParameterCount();
						boolean addSpace = false;
						Formatter f = doc.getFormatter();
						if (f instanceof ExtFormatter) {
							Object o = ((ExtFormatter) f).getSettingValue(JavaSettingsNames.JAVA_FORMAT_SPACE_AFTER_COMMA);
							if ((o instanceof Boolean) && ((Boolean) o).booleanValue()) {
								addSpace = true;
							}
						}

						try {
							if (addSpace && (ind == 0 || (substituteOffset > 0 && Character.isWhitespace(doc.getText(substituteOffset - 1, 1).charAt(0))))) {
								addSpace = false;
							}
						} catch (BadLocationException e) {
						}

						if (ind < parms.length) {
							text = addSpace ? " " : ""; // NOI18N
							selectionStartOffset = text.length();
							text += parms[ind].getName();
							selectionEndOffset = text.length();
						}
					}
					break;

				default:
					text = getMainText(replacement);
					boolean addSpace = false;
					Formatter f = doc.getFormatter();
					if (f instanceof ExtFormatter) {
						Object o = ((ExtFormatter) f).getSettingValue(JavaSettingsNames.JAVA_FORMAT_SPACE_BEFORE_PARENTHESIS);
						if ((o instanceof Boolean) && ((Boolean) o).booleanValue()) {
							addSpace = true;
						}
					}

					if (addSpace) {
						text += ' ';
					}
					text += '(';

					parms = mtd.getParameters();
					if (parms.length > 0) {
						selectionStartOffset = text.length();
						text += parms[0].getName();
						selectionEndOffset = text.length();
					}
					else {
						text += ")"; // NOI18N
					}
					break;
				}

			}
			else if (replacement instanceof JCConstructor) {
				text = ((JCConstructor) replacement).getClazz().getName();
			}

			if (text != null) {
				// Update the text
				doc.atomicLock();
				try {
					doc.remove(substituteOffset, substituteLength);
					doc.insertString(substituteOffset, text, null);
					if (selectionStartOffset >= 0) {
						component.select(substituteOffset + selectionStartOffset, substituteOffset + selectionEndOffset);
					}
				} catch (BadLocationException e) {
					// Can't update
				} finally {
					doc.atomicUnlock();
				}
			}

			return true;
		}

	}

}
