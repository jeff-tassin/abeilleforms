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

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.netbeans.editor.TokenID;

/**
 * Java completion utilities
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JCUtilities {

	private static final int javaTokenIDsLength = JavaTokenContext.context.getTokenIDs().length;

	private static final boolean[][] primitivesAssignable = new boolean[javaTokenIDsLength][];

	private static final JCClass[][] primitivesCommonClass = new JCClass[javaTokenIDsLength][];

	static {
		int[] typeIDs = new int[] { JavaTokenContext.BOOLEAN_ID, JavaTokenContext.BYTE_ID, JavaTokenContext.CHAR_ID, JavaTokenContext.DOUBLE_ID,
				JavaTokenContext.FLOAT_ID, JavaTokenContext.INT_ID, JavaTokenContext.LONG_ID, JavaTokenContext.SHORT_ID, JavaTokenContext.VOID_ID };

		boolean[][] assignVals = new boolean[][] { new boolean[] { true, false, false, false, false, false, false, false, false }, // boolean
				new boolean[] { false, true, false, true, true, true, true, true, false }, // byte
				new boolean[] { false, false, true, true, true, true, true, false, false }, // char
				new boolean[] { false, false, false, true, false, false, false, false, false }, // double
				new boolean[] { false, false, false, true, true, false, false, false, false }, // float
				new boolean[] { false, false, false, true, true, true, true, false, false }, // int
				new boolean[] { false, false, false, true, true, false, true, false, false }, // long
				new boolean[] { false, false, false, true, true, true, true, true, false }, // short
				new boolean[] { false, false, false, false, false, false, false, false, true } // void
		};

		JCClass[][] classesVals = new JCClass[][] {
				new JCClass[] { JavaCompletion.BOOLEAN_CLASS, null, null, null, null, null, null, null, null }, // boolean

				new JCClass[] { null, JavaCompletion.BYTE_CLASS, JavaCompletion.INT_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.FLOAT_CLASS,
						JavaCompletion.INT_CLASS, JavaCompletion.LONG_CLASS, JavaCompletion.INT_CLASS, null }, // byte

				new JCClass[] { null, JavaCompletion.INT_CLASS, JavaCompletion.CHAR_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.FLOAT_CLASS,
						JavaCompletion.INT_CLASS, JavaCompletion.LONG_CLASS, JavaCompletion.INT_CLASS, null }, // char

				new JCClass[] { null, JavaCompletion.DOUBLE_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.DOUBLE_CLASS,
						JavaCompletion.DOUBLE_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.DOUBLE_CLASS, null }, // double

				new JCClass[] { null, JavaCompletion.FLOAT_CLASS, JavaCompletion.FLOAT_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.FLOAT_CLASS,
						JavaCompletion.FLOAT_CLASS, JavaCompletion.FLOAT_CLASS, JavaCompletion.FLOAT_CLASS, null }, // float

				new JCClass[] { null, JavaCompletion.INT_CLASS, JavaCompletion.INT_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.FLOAT_CLASS,
						JavaCompletion.INT_CLASS, JavaCompletion.LONG_CLASS, JavaCompletion.INT_CLASS, null }, // int

				new JCClass[] { null, JavaCompletion.LONG_CLASS, JavaCompletion.LONG_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.FLOAT_CLASS,
						JavaCompletion.LONG_CLASS, JavaCompletion.LONG_CLASS, JavaCompletion.LONG_CLASS, null }, // long

				new JCClass[] { null, JavaCompletion.INT_CLASS, JavaCompletion.INT_CLASS, JavaCompletion.DOUBLE_CLASS, JavaCompletion.FLOAT_CLASS,
						JavaCompletion.INT_CLASS, JavaCompletion.LONG_CLASS, JavaCompletion.SHORT_CLASS, null }, // short

				new JCClass[] { null, null, null, null, null, null, null, null, JavaCompletion.VOID_CLASS } // void
		};

		for (int i = 0; i < typeIDs.length; i++) {
			primitivesAssignable[typeIDs[i]] = new boolean[javaTokenIDsLength];
			primitivesCommonClass[typeIDs[i]] = new JCClass[javaTokenIDsLength];

			for (int j = 0; j < typeIDs.length; j++) {
				primitivesAssignable[typeIDs[i]][typeIDs[j]] = assignVals[i][j];
				primitivesCommonClass[typeIDs[i]][typeIDs[j]] = classesVals[i][j];
			}
		}

	}

	private static boolean stringEqual(String s1, String s2) {
		return (s1 == null) ? (s2 == null) : s1.equals(s2);
	}

	private static boolean classEqual(JCClass c1, JCClass c2) {
		return (c1 == null) ? (c2 == null) : c1.equals(c2);
	}

	private static boolean typeEqual(JCType t1, JCType t2) {
		return (t1 == null) ? (t2 == null) : classEqual(t1.getClazz(), t2.getClazz()) && (t1.getArrayDepth() == t2.getArrayDepth());
	}

	private static boolean parameterEqual(JCParameter p1, JCParameter p2) {
		return (p1 == null) ? (p2 == null) : typeEqual(p1.getType(), p2.getType()) && stringEqual(p1.getName(), p2.getName());
	}

	private static boolean constructorEqual(JCConstructor c1, JCConstructor c2) {
		return (c1 == null) ? (c2 == null) : (c1.getClazz().equals(c2.getClazz()) // mustn't
																					// be
																					// null
				&& c1.getModifiers() == c2.getModifiers() && parameterArrayEqual(c1.getParameters(), c2.getParameters()) && classArrayEqual(c1.getExceptions(),
				c2.getExceptions()));
	}

	private static boolean parameterArrayEqual(JCParameter[] pa1, JCParameter[] pa2) {
		if (pa1.length != pa2.length) {
			return false;
		}
		for (int i = pa1.length - 1; i >= 0; i--) {
			if (!parameterEqual(pa1[i], pa2[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean classArrayEqual(JCClass[] ca1, JCClass[] ca2) {
		if (ca1.length != ca2.length) {
			return false;
		}
		for (int i = ca1.length - 1; i >= 0; i--) {
			if (!classEqual(ca1[i], ca2[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean fieldArraysEqual(JCField[] fa1, JCField[] fa2) {
		if (fa1.length != fa2.length) {
			return false;
		}
		for (int i = fa1.length - 1; i >= 0; i--) {
			JCField f1 = fa1[i];
			JCField f2 = fa2[i];
			if (!parameterEqual(f1, f2) || !f1.getClazz().equals(f2.getClazz()) // mustn't
																				// be
																				// null
					|| (f1.getModifiers() != f2.getModifiers())) {
				return false;
			}
		}
		return true;
	}

	private static boolean constructorArrayEqual(JCConstructor[] ca1, JCConstructor[] ca2) {
		if (ca1.length != ca2.length) {
			return false;
		}
		for (int i = ca1.length - 1; i >= 0; i--) {
			if (!constructorEqual(ca1[i], ca2[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean methodArraysEqual(JCMethod[] ma1, JCMethod[] ma2) {
		if (ma1.length != ma2.length) {
			return false;
		}
		for (int i = ma1.length - 1; i >= 0; i--) {
			JCMethod m1 = ma1[i];
			JCMethod m2 = ma2[i];
			if (!constructorEqual(m1, m2) || !stringEqual(m1.getName(), m2.getName()) || !typeEqual(m1.getReturnType(), m2.getReturnType())) {
				return false;
			}
		}
		return true;
	}

	public static boolean equal(JCClass c1, JCClass c2) {
		if (c1 == null && c2 == null) { // both null
			return true;
		}
		if (c1 == null || c2 == null) { // one of them is null, but not both
			return false;
		}

		if (!c1.equals(c2) || c1.isInterface() != c2.isInterface() || c1.getModifiers() != c2.getModifiers()
				|| !classEqual(c1.getSuperclass(), c2.getSuperclass())) {
			return false;
		}

		if (!fieldArraysEqual(c1.getFields(), c2.getFields()) || !constructorArrayEqual(c1.getConstructors(), c2.getConstructors())
				|| !methodArraysEqual(c1.getMethods(), c2.getMethods()) || !classArrayEqual(c1.getInterfaces(), c2.getInterfaces())) {
			return false;
		}
		return true;
	}

	public static String dumpClass(JCClass c) {
		StringBuffer sb = new StringBuffer();
		sb.append(Modifier.toString(c.getModifiers()));
		sb.append(c.isInterface() ? " interface " : " class "); // NOI18N
		sb.append(c);
		sb.append(" extends "); // NOI18N
		sb.append(c.getSuperclass());
		// Add implemented interfaces
		JCClass[] ifcs = c.getInterfaces();
		int cntM1 = ifcs.length - 1;
		if (cntM1 >= 0) {
			sb.append(" implements "); // NOI18N
			for (int i = 0; i <= cntM1; i++) {
				sb.append(ifcs[i].toString());
				if (i < cntM1) {
					sb.append(", "); // NOI18N
				}
			}
		}
		sb.append('\n');

		String indentStr = "    "; // NOI18N
		// Add fields
		JCField[] flds = c.getFields();
		if (flds.length > 0) {
			sb.append("FIELDS:\n"); // NOI18N
			for (int i = 0; i < flds.length; i++) {
				sb.append(indentStr);
				sb.append(flds[i]);
				sb.append('\n');
			}
		}
		// Add constructors
		JCConstructor[] cons = c.getConstructors();
		if (cons.length > 0) {
			sb.append("CONSTRUCTORS:\n"); // NOI18N
			for (int i = 0; i < cons.length; i++) {
				sb.append(indentStr);
				sb.append(cons[i]);
				sb.append('\n');
			}
		}
		// Add methods
		JCMethod[] mtds = c.getMethods();
		if (mtds.length > 0) {
			sb.append("METHODS:\n"); // NOI18N
			for (int i = 0; i < mtds.length; i++) {
				sb.append(indentStr);
				sb.append(mtds[i]);
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	public static JCClass getExactClass(JCFinder finder, String name, String pkgName) {
		return finder.getExactClass((pkgName.length() != 0) ? (pkgName + "." + name) : name); // NOI18N
	}

	/**
	 * Filter the list of the methods (usually returned from
	 * Finder.findMethods()) or the list of the constructors by the given
	 * parameter specification.
	 * 
	 * @param methodList
	 *            list of the methods. They should have the same name but in
	 *            fact they don't have to.
	 * @param parmTypes
	 *            parameter types specification. If set to null, no filtering is
	 *            performed and the same list is returned. If a particular
	 * @param acceptMoreParameters
	 *            useful for code completion to get even the methods with more
	 *            parameters.
	 */
	public static List filterMethods(List methodList, List parmTypeList, boolean acceptMoreParameters) {
		if (parmTypeList == null) {
			return methodList;
		}

		List ret = new ArrayList();
		int parmTypeCnt = parmTypeList.size();
		int cnt = methodList.size();
		for (int i = 0; i < cnt; i++) {
			// Use constructor conversion to allow to use it too for the
			// constructors
			JCConstructor m = (JCConstructor) methodList.get(i);
			JCParameter[] methodParms = m.getParameters();
			if (methodParms.length == parmTypeCnt || (acceptMoreParameters && methodParms.length >= parmTypeCnt)) {
				boolean accept = true;
				boolean bestMatch = !acceptMoreParameters;
				for (int j = 0; accept && j < parmTypeCnt; j++) {
					JCType mpt = methodParms[j].getType();
					JCType t = (JCType) parmTypeList.get(j);
					if (t != null) {
						if (!t.equals(mpt)) {
							bestMatch = false;
							if (!isAssignable(t, mpt)) {
								accept = false;
								break;
							}
						}
					}
					else { // type in list is null
						bestMatch = false;
					}
				}

				if (accept) {
					if (bestMatch) {
						ret.clear();
					}
					ret.add(m);
					if (bestMatch) {
						break;
					}
				}

			}
		}
		return ret;
	}

	/** Get the sorted constructor list for the given class. */
	public static List getConstructors(JCClass cls) {
		TreeSet ts = new TreeSet();
		JCConstructor[] constructors = cls.getConstructors();
		for (int i = constructors.length - 1; i >= 0; i--) {
			ts.add(constructors[i]);
		}
		return new ArrayList(ts);
	}

	/**
	 * Get all the interfaces the class/interface implements/extends.
	 */
	public static List getAllInterfaces(JCClass cls) {
		ArrayList ret = new ArrayList();
		collectInterfaces(cls, ret);
		return ret;
	}

	/** Accumulate the subinterfaces recursively */
	private static void collectInterfaces(JCClass cls, ArrayList clsList) {
		JCClass[] ifcs = cls.getInterfaces();
		if (ifcs != null) {
			JCFinder finder = JavaCompletion.getFinder();
			for (int i = 0; i < ifcs.length; i++) {
				if (clsList.contains(ifcs[i]))
					continue;
				clsList.add(ifcs[i]);
				cls = finder.getExactClass(ifcs[i].getFullName());
				if (cls != null) {
					collectInterfaces(cls, clsList); // recurse implemented
														// interfaces
				}
			}
		}
	}

	/** Get the list containing the given class and all its superclasses. */
	public static List getSuperclasses(JCClass cls) {
		ArrayList clsList = new ArrayList();
		JCFinder finder = JavaCompletion.getFinder();
		cls = finder.getExactClass(cls.getFullName());
		if (cls != null) {
			cls = cls.getSuperclass();
		}

		while (cls != null && clsList.indexOf(cls) < 0) {
			clsList.add(cls);
			cls = finder.getExactClass(cls.getFullName());
			if (cls != null) {
				cls = cls.getSuperclass();
			}
		}

		return clsList;
	}

	public static boolean isAssignable(JCType from, JCType to) {
		JCClass fromCls = from.getClazz();
		JCClass toCls = to.getClazz();

		if (fromCls.equals(JavaCompletion.NULL_CLASS)) {
			return to.getArrayDepth() > 0 || !JavaCompletion.isPrimitiveClass(toCls);
		}

		if (toCls.equals(JavaCompletion.OBJECT_CLASS)) { // everything is
															// object
			return (from.getArrayDepth() > to.getArrayDepth()) || (from.getArrayDepth() == to.getArrayDepth() && !JavaCompletion.isPrimitiveClass(fromCls));
		}

		if (from.getArrayDepth() != to.getArrayDepth()) {
			return false;
		}

		if (fromCls.equals(toCls)) {
			return true; // equal classes
		}

		if (fromCls.isInterface()) {
			return toCls.isInterface() && (getAllInterfaces(fromCls).indexOf(toCls) >= 0);
		}
		else { // fromCls is a class
			TokenID fromClsKwd = JavaTokenContext.getKeyword(fromCls.getName());
			if (fromClsKwd != null) { // primitive class
				TokenID toClsKwd = JavaTokenContext.getKeyword(toCls.getName());
				return toClsKwd != null && primitivesAssignable[fromClsKwd.getNumericID()][toClsKwd.getNumericID()];
			}
			else {
				if (toCls.isInterface()) {
					return (getAllInterfaces(fromCls).indexOf(toCls) >= 0);
				}
				else { // toCls is a class
					return (getSuperclasses(fromCls).indexOf(toCls) >= 0);
				}
			}
		}
	}

	public static JCType getCommonType(JCType typ1, JCType typ2) {
		if (typ1.equals(typ2)) {
			return typ1;
		}

		// The following part
		TokenID cls1Kwd = JavaTokenContext.getKeyword(typ1.getClazz().getName());
		TokenID cls2Kwd = JavaTokenContext.getKeyword(typ2.getClazz().getName());
		if (cls1Kwd == null && cls2Kwd == null) { // non-primitive classes
			if (isAssignable(typ1, typ2)) {
				return typ1;
			}
			else if (isAssignable(typ2, typ1)) {
				return typ2;
			}
			else {
				return null;
			}
		}
		else { // at least one primitive class
			if (typ1.getArrayDepth() != typ2.getArrayDepth()) {
				return null;
			}
			if (cls1Kwd != null && cls2Kwd != null) {
				return JavaCompletion.getType(primitivesCommonClass[cls1Kwd.getNumericID()][cls2Kwd.getNumericID()], typ1.getArrayDepth());
			}
			else { // one primitive but other not
				return null;
			}
		}
	}

	public static JCClass createSimpleClass(Class c) {
		if (c == null || c.getName() == null) {
			return JavaCompletion.INVALID_CLASS;
		}
		return createSimpleClassImpl(c.getName());
	}

	private static JCClass createSimpleClassImpl(String className) {
		int dotInd = className.lastIndexOf('.');
		return JavaCompletion.getSimpleClass(className.replace('$', '.'), (dotInd >= 0) ? dotInd : 0);
	}

	public static JavaCompletion.BaseType createType(Class c) {
		if (c == null) {
			return JavaCompletion.INVALID_TYPE;
		}

		String className = c.getName();
		int arrayDepth = 0;
		while (className.length() > 0 && className.charAt(0) == '[') {
			arrayDepth++;
			className = className.substring(1);
		}

		if (arrayDepth > 0) {
			switch (className.charAt(0)) {
			case 'L':
				className = className.substring(1, className.length() - 1);
				break;
			case 'B':
				className = "byte"; // NOI18N
				break;
			case 'C':
				className = "char"; // NOI18N
				break;
			case 'D':
				className = "double"; // NOI18N
				break;
			case 'F':
				className = "float"; // NOI18N
				break;
			case 'I':
				className = "int"; // NOI18N
				break;
			case 'J':
				className = "long"; // NOI18N
				break;
			case 'S':
				className = "short"; // NOI18N
				break;
			case 'Z':
				className = "boolean"; // NOI18N
				break;
			}
		}

		return new JavaCompletion.BaseType(createSimpleClassImpl(className), arrayDepth);
	}

	public static List getClassList(List classNames, boolean storeDeclaredClasses, int classLevel, int fieldLevel, int methodLevel) {
		ArrayList l = new ArrayList();
		Iterator i = classNames.iterator();
		while (i.hasNext()) {
			String name = (String) i.next();
			Class c = null;
			try {
				c = Class.forName(name);
			} catch (ClassNotFoundException e) {
				System.err.println("Class '" + name + "' not found."); // NOI18N
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable t) {
				System.err.println("Exception thrown during class rebuild:"); // NOI18N
				t.printStackTrace();
				if (t instanceof OutOfMemoryError)
					throw (OutOfMemoryError) t;
			}
			if (c != null) {
				l.addAll(createClassList(c, storeDeclaredClasses, classLevel, fieldLevel, methodLevel));
			}
		}
		return l;
	}

	private static String strip(String name, String baseName, String suffix) {
		int startInd = 0;
		int endStrip = 0;
		if (name.startsWith(baseName)) {
			startInd = baseName.length();
		}
		if (name.endsWith(suffix)) {
			endStrip = suffix.length();
		}
		return name.substring(startInd, name.length() - endStrip);
	}

	private static String separatorToDot(String s) {
		return s.replace(File.separatorChar, '.');
	}

	private static List createClassList(Class c, boolean storeDeclaredClasses, int classLevel, int fieldLevel, int methodLevel) {
		ArrayList cL = new ArrayList();
		if (c == null) {
			return cL;
		}

		if (JavaCompletion.getLevel(c.getModifiers()) >= classLevel) {
			cL.add(new BaseJCClass(c, classLevel, fieldLevel, methodLevel));
		}

		// possibly store declared classes subclasses
		if (storeDeclaredClasses) {
			try {
				Class[] dC = c.getDeclaredClasses();
				for (int i = 0; i < dC.length; i++) {
					if (JavaCompletion.getLevel(dC[i].getModifiers()) >= classLevel) {
						cL.addAll(createClassList(dC[i], storeDeclaredClasses, classLevel, fieldLevel, methodLevel));
					}
				}
			} catch (SecurityException e) {
				// can't access declared classes
				e.printStackTrace();
			}
		}

		return cL;
	}

	public static List getClassNameList(String packageDirName) {
		File packageDir = new File(packageDirName);
		List classNames = new ArrayList();
		packageDirName += File.separator; // to strip begining
		if (packageDir.exists()) {
			getClassListFromSourcesRec(classNames, packageDirName, packageDir);
		}
		return classNames;
	}

	private static void getClassListFromSourcesRec(final List l, final String packageDirName, File curDir) {
		curDir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					getClassListFromSourcesRec(l, packageDirName, f);
				}
				if (f.getName().endsWith(".java")) { // NOI18N
					l.add(separatorToDot(strip(f.getAbsolutePath(), packageDirName, ".java"))); // NOI18N
				}
				return false;
			}
		});
	}

	public static class BaseJCClass extends JavaCompletion.AbstractClass {

		Class c;

		int classLevel;

		int fieldLevel;

		int methodLevel;

		/** Do reflection of given class */
		public BaseJCClass(Class c, int classLevel, int fieldLevel, int methodLevel) {
			this.c = c;
			this.classLevel = classLevel;
			this.fieldLevel = fieldLevel;
			this.methodLevel = methodLevel;
			JCClass sc = createSimpleClass(c);
			name = sc.getName();
			packageName = sc.getPackageName();
			modifiers = c.getModifiers();
			if (c.isInterface()) {
				modifiers |= JavaCompletion.INTERFACE_BIT;
			}
		}

		protected void init() {
			body = new Body();
			ArrayList lst = new ArrayList();
			body.superClass = createSimpleClass(c.getSuperclass());

			// create interface classes
			Class[] dI = c.getInterfaces();
			for (int i = 0; i < dI.length; i++) {
				if (JavaCompletion.getLevel(dI[i].getModifiers()) >= classLevel) {
					lst.add(createSimpleClass(dI[i]));
				}
			}
			body.interfaces = new JCClass[lst.size()];
			lst.toArray(body.interfaces);
			lst.clear();

			// create fields
			try {
				Field[] dF = c.getDeclaredFields();
				for (int i = 0; i < dF.length; i++) {
					if (JavaCompletion.getLevel(dF[i].getModifiers()) >= fieldLevel) {
						lst.add(new JavaCompletion.BaseField(this, dF[i].getName(), createType(dF[i].getType()), dF[i].getModifiers()));
					}
				}
				body.fields = new JCField[lst.size()];
				lst.toArray(body.fields);
				lst.clear();
			} catch (SecurityException e) {
				// can't access declared fields
				e.printStackTrace();
			}

			// create constructors
			try {
				Constructor[] dC = c.getDeclaredConstructors();
				for (int i = 0; i < dC.length; i++) {
					if (JavaCompletion.getLevel(dC[i].getModifiers()) >= methodLevel) {
						// get constructor parameters
						JCParameter[] parameters = JavaCompletion.EMPTY_PARAMETERS;
						try {
							Class[] dP = dC[i].getParameterTypes();
							parameters = new JCParameter[dP.length];
							for (int j = 0; j < dP.length; j++) {
								parameters[j] = new JavaCompletion.BaseParameter("", // name
																						// not
																						// known
																						// from
																						// reflection
																						// //
																						// NOI18N
										createType(dP[j]));
							}
						} catch (SecurityException e) {
							// can't get parameter types
							e.printStackTrace();
						}

						// get thrown exceptions - don't restrict to classes
						// level
						JCClass[] exceptions = JavaCompletion.EMPTY_CLASSES;
						try {
							Class[] dE = dC[i].getExceptionTypes();
							exceptions = new JCClass[dE.length];
							for (int j = 0; j < dE.length; j++) {
								exceptions[j] = createSimpleClass(dE[j]);
							}
						} catch (SecurityException e) {
							// can't get exception types
							e.printStackTrace();
						}

						lst.add(new JavaCompletion.BaseConstructor(this, dC[i].getModifiers(), parameters, exceptions));
					}
				}
				body.constructors = new JCConstructor[lst.size()];
				lst.toArray(body.constructors);
				lst.clear();
			} catch (SecurityException e) {
				// can't access declared constructors
				e.printStackTrace();
			}

			// create methods
			try {
				Method[] dM = c.getDeclaredMethods();
				for (int i = 0; i < dM.length; i++) {
					if (JavaCompletion.getLevel(dM[i].getModifiers()) >= methodLevel) {
						// get method parameters
						JCParameter[] parameters = JavaCompletion.EMPTY_PARAMETERS;
						try {
							Class[] dP = dM[i].getParameterTypes();
							parameters = new JCParameter[dP.length];
							for (int j = 0; j < dP.length; j++) {
								parameters[j] = new JavaCompletion.BaseParameter("", // name
																						// not
																						// known
																						// from
																						// reflection
																						// //
																						// NOI18N
										createType(dP[j]));
							}
						} catch (SecurityException e) {
							// can't get parameter types
							e.printStackTrace();
						}

						// get thrown exceptions - don't restrict to classes
						// level
						JCClass[] exceptions = JavaCompletion.EMPTY_CLASSES;
						try {
							Class[] dE = dM[i].getExceptionTypes();
							exceptions = new JCClass[dE.length];
							for (int j = 0; j < dE.length; j++) {
								exceptions[j] = createSimpleClass(dE[j]);
							}
						} catch (SecurityException e) {
							// can't get exception types
							e.printStackTrace();
						}

						lst.add(new JavaCompletion.BaseMethod(this, dM[i].getName(), dM[i].getModifiers(), createType(dM[i].getReturnType()), parameters,
								exceptions));
					}
				}
				body.methods = new JCMethod[lst.size()];
				lst.toArray(body.methods);
				lst.clear();
			} catch (SecurityException e) {
				// can't access declared methods
				e.printStackTrace();
			}

			c = null; // can free this reference now
		}

	}

}
