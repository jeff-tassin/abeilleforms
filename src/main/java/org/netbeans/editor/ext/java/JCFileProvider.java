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

import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.FileStorage;
import org.netbeans.editor.ext.StringCache;

/**
 * Java completion resolver that operates over two files. One is skeleton file
 * and it's read at once during the build() methods. The other file is class
 * body file and it's read lazily as necessary.
 * 
 * File structures: Skeleton file: class skeletons: String: class name String:
 * package name int: body seek offset int: body len
 * 
 * Body file: class bodies: int: modifiers String: super class name String:
 * super class package name int: field count field count * field body field
 * body: !!! dodelat
 * 
 * 
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JCFileProvider extends JavaCompletion.AbstractProvider {

	/** Version of parser DB file */
	static final int VERSION = 2;

	static final int OPCODE_ADD = 1; // adding new class

	public static final String SKEL_FILE_EXT = "jcs"; // NOI18N
	public static final String BODY_FILE_EXT = "jcb"; // NOI18N

	/** Global cache saving the string creations */
	private static final StringCache strCache = new StringCache(200, 5003);

	static {
		// pre-cache standard strings
		strCache.putSurviveString(""); // NOI18N
		Iterator i = JavaCompletion.getPrimitiveClassIterator();
		while (i.hasNext()) {
			strCache.putSurviveString(((JCClass) i.next()).getName());
		}
	}

	FileStorage skels;

	FileStorage bodies;

	HashMap classes;

	int fileVersion;

	public JCFileProvider(String fileNamePrefix) {
		this(fileNamePrefix + "." + SKEL_FILE_EXT, fileNamePrefix + "." + BODY_FILE_EXT);
	}

	public JCFileProvider(String fileNameSkels, String fileNameBodies) {
		skels = new FileStorage(fileNameSkels, strCache);
		bodies = new FileStorage(fileNameBodies, strCache);
	}

	public synchronized void reset() {
		boolean passedOK = false;
		try {
			skels.resetFile();
			bodies.resetFile();

			// write version to skels
			skels.open(true);
			skels.setVersion(1);
			skels.putInteger(VERSION);
			fileVersion = VERSION;
			setVersion(fileVersion);
			skels.write();
			skels.close();

			// write version to bodies
			if (VERSION > 1) {
				bodies.open(true);
				bodies.setVersion(1);
				bodies.putInteger(VERSION);
				setVersion(fileVersion);
				bodies.write();
				bodies.close();
			}

			passedOK = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (passedOK == false) {
				// some error occured, we have to reset unwritten bytes.
				skels.resetBytes();
				bodies.resetBytes();
			}
		}
	}

	private void setVersion(int ver) {
		skels.setVersion(ver);
		bodies.setVersion(ver);
	}

	protected boolean appendClass(JCClass c) {
		try {
			skels.putInteger(OPCODE_ADD);
			writeClass(c);
			skels.write();
			bodies.write();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public synchronized boolean append(JCClassProvider cp) {
		boolean passedOK = false;
		try {
			if (skels.getFileLength() <= 0) { // reset if necessary
				reset();
			}

			skels.open(true);
			bodies.open(true);

			if (!super.append(cp)) {
				return false;
			}

			passedOK = true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable t) {
			System.err.println("Error occurred during updating parser DB: " + this); // NOI18N
			t.printStackTrace();
			if (t instanceof OutOfMemoryError)
				throw (OutOfMemoryError) t;
			return false;
		} finally {
			boolean ok = true;
			try {
				skels.close();
			} catch (IOException e) {
				e.printStackTrace();
				ok = false;
			}

			try {
				bodies.close();
			} catch (IOException e) {
				e.printStackTrace();
				ok = false;
			}

			if ((passedOK == false) || (ok == false)) {
				// some error occured, we have to reset unwritten bytes.
				skels.resetBytes();
				bodies.resetBytes();
			}
			passedOK = ok;
		}
		return passedOK;
	}

	public synchronized Iterator getClasses() {
		int skelsFileLen;
		try {
			skels.open(false);
			skels.seek(0);
			skelsFileLen = skels.getFileLength();
			if (skelsFileLen < 4) { // file exists but was not reset
				// reset();
				return new ArrayList().iterator(); // return empty iterator
			}
			skels.read(skelsFileLen);
		} catch (IOException e) {
			if (!skels.fileNotFound) { // show this info only once for
										// appropriate file.
				JOptionPane.showMessageDialog((Component) Utilities.getLastActiveComponent(), MessageFormat.format(
						LocaleSupport.getString("pd-file-not-found"), // NOI18N
						new Object[] { bodies.toString() }), LocaleSupport.getString("pd-file-not-found-title"), // NOI18N
						JOptionPane.WARNING_MESSAGE);
				skels.fileNotFound = true;
			}
			return new ArrayList().iterator();
		} finally {
			try {
				skels.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setVersion(1); // Version of file is always saved in version 1 encoding
		fileVersion = skels.getInteger();
		setVersion(fileVersion);

		ArrayList clsList = new ArrayList();
		while (skels.getOffset() < skelsFileLen) { // till the last class
			int opcode = skels.getInteger();
			if (opcode == OPCODE_ADD) {
				Cls cls = new Cls();
				clsList.add(cls);
				if (cls.fullName.equals(JavaCompletion.OBJECT_CLASS.fullName)) {
					// make clone of Object and add a new public final int
					// length field into it
					clsList.add(cls.makeClone());
				}
			}
			else {
				break; // Unsupported operation code
			}
		}

		skels.resetBytes(); // GC possibly large skels bytes array
		return clsList.iterator();
	}

	void writeClass(JCClass c) throws IOException {
		// write body
		bodies.putInteger(c.getTagOffset());
		writeClassName(c.getSuperclass(), bodies);

		// Write implemented interfaces
		JCClass[] interfaces = c.getInterfaces();
		bodies.putInteger(interfaces.length);
		for (int i = 0; i < interfaces.length; i++) {
			writeClassName(interfaces[i], bodies);
		}

		// Write declared fields
		JCField[] fields = c.getFields();
		bodies.putInteger(fields.length);
		for (int i = 0; i < fields.length; i++) {
			writeField(fields[i]);
		}

		// Write constructors
		JCConstructor[] constructors = c.getConstructors();
		bodies.putInteger(constructors.length);
		for (int i = 0; i < constructors.length; i++) {
			writeConstructor(constructors[i]);
		}

		// Write methods
		JCMethod[] methods = c.getMethods();
		bodies.putInteger(methods.length);
		for (int i = 0; i < methods.length; i++) {
			writeMethod(methods[i]);
		}

		// write skeleton
		writeClassName(c, skels);
		int modifiers = c.getModifiers();
		if (c.isInterface()) {
			modifiers |= JavaCompletion.INTERFACE_BIT;
		}
		skels.putInteger(modifiers);

		skels.putInteger(bodies.getFilePointer());
		skels.putInteger(bodies.getOffset());
		/*
		 * Cls updatedCls = new Cls(c, bodies.getFilePointer(),
		 * bodies.getOffset()); if (updatedCls!=null){ // update class in memory
		 * JavaCompletion.getFinder().append(new
		 * JavaCompletion.SingleProvider(updatedCls)); }
		 */
	}

	void writeType(JCType t) {
		writeClassName(t.getClazz(), bodies);
		bodies.putInteger(t.getArrayDepth());
	}

	void writeParameter(JCParameter p) {
		bodies.putString(p.getName());
		writeType(p.getType());
	}

	void writeField(JCField f) {
		bodies.putString(f.getName());
		writeType(f.getType());
		bodies.putInteger(f.getTagOffset());
		bodies.putInteger(f.getModifiers());
	}

	void writeConstructor(JCConstructor c) {
		bodies.putInteger(c.getTagOffset());
		bodies.putInteger(c.getModifiers());

		JCParameter[] parameters = c.getParameters();
		bodies.putInteger(parameters.length);
		for (int i = 0; i < parameters.length; i++) {
			writeParameter(parameters[i]);
		}

		JCClass[] exceptions = c.getExceptions();
		bodies.putInteger(exceptions.length);
		for (int i = 0; i < exceptions.length; i++) {
			writeClassName(exceptions[i], bodies);
		}
	}

	void writeMethod(JCMethod m) {
		writeConstructor(m);
		bodies.putString(m.getName());
		writeType(m.getReturnType());
	}

	/** Write name and package of the given class */
	void writeClassName(JCClass c, FileStorage fs) {
		fs.putString(c.getFullName());
		fs.putInteger(c.getPackageName().length());
	}

	private JCClass getSimpleClass(String fullName, int packageNameLen) {
		JCClass c = null;
		if (packageNameLen == 0) {
			c = JavaCompletion.getPrimitiveClass(fullName);
		}
		if (c == null) {
			String fullNameIntern = fullName.intern();
			if (fullName != fullNameIntern) { // update cache with interned
												// string
				strCache.putSurviveString(fullNameIntern);
			}
			c = JavaCompletion.getSimpleClass(fullNameIntern, packageNameLen);
		}
		return c;
	}

	JCClass readSimpleClass(FileStorage fs) {
		String fullName = fs.getString();
		int packageNameLen = fs.getInteger();
		return getSimpleClass(fullName, packageNameLen);
	}

	final class Cls extends JavaCompletion.AbstractClass {

		/** Seek position in the file of the class body */
		int bodySeekPointer;

		/** Length of the class body in the file */
		int bodyLen;

		/** A flag determining whether this class is an interface */
		boolean isInterface = false;

		public Cls() {
			JCClass c = readSimpleClass(skels);
			fullName = c.getFullName();
			name = c.getName();
			packageName = c.getPackageName();
			modifiers = skels.getInteger();
			bodySeekPointer = skels.getInteger();
			bodyLen = skels.getInteger();
		}

		public Cls(JCClass c, int seekPointer, int len) {
			fullName = c.getFullName();
			name = c.getName();
			packageName = c.getPackageName();
			modifiers = c.getModifiers();
			isInterface = c.isInterface();
			bodySeekPointer = seekPointer;
			bodyLen = len;
		}

		/** Fill clone with new parameters. Add a public final int length to it */
		private Cls(Cls original) {
			this.fullName = JavaCompletion.OBJECT_CLASS_ARRAY.fullName;
			this.name = JavaCompletion.OBJECT_CLASS_ARRAY.name;
			this.packageName = JavaCompletion.OBJECT_CLASS_ARRAY.packageName;
			this.body = new Body();
			original.init();
			this.body.superClass = original.body.superClass;
			this.body.interfaces = original.body.interfaces;
			this.body.constructors = original.body.constructors;
			this.body.methods = original.body.methods;
			this.body.fields = new JCField[1];
			this.body.fields[0] = new JavaCompletion.BaseField(this, "length", // NOI18N
					JavaCompletion.INT_TYPE, (Modifier.PUBLIC | Modifier.FINAL));
		}

		public boolean isInterface() {
			if (isInterface) {
				return true;
			}
			else {
				return super.isInterface();
			}
		}

		/** makeClone of java.lang.Object */
		public Cls makeClone() {
			return new Cls(this);
		}

		/** Init internal representation */
		protected void init() {
			synchronized (JCFileProvider.this) {
				body = new Body();
				// set the right seek position and read
				try {
					bodies.open(false);
					bodies.seek(bodySeekPointer);
					bodies.read(bodyLen);
					bodies.close();
				} catch (IOException e) {
					if (!bodies.fileNotFound) { // show this info only once for
												// appropriate file.
						JOptionPane.showMessageDialog((Component) Utilities.getLastActiveComponent(), MessageFormat.format(LocaleSupport
								.getString("pd-file-not-found"), // NOI18N
								new Object[] { bodies.toString() }), LocaleSupport.getString("pd-file-not-found-title"), // NOI18N
								JOptionPane.WARNING_MESSAGE);
						bodies.fileNotFound = true;
						skels.fileNotFound = true;
					}
					body.tagOffset = -1;
					body.superClass = JavaCompletion.INVALID_CLASS;
					body.interfaces = JavaCompletion.EMPTY_CLASSES;
					body.fields = JavaCompletion.EMPTY_FIELDS;
					body.constructors = JavaCompletion.EMPTY_CONSTRUCTORS;
					body.methods = JavaCompletion.EMPTY_METHODS;
					return;
				}

				body.tagOffset = bodies.getInteger();
				body.superClass = readSimpleClass(bodies);

				int cnt = bodies.getInteger();
				body.interfaces = (cnt > 0) ? new JCClass[cnt] : JavaCompletion.EMPTY_CLASSES;
				for (int i = 0; i < cnt; i++) {
					body.interfaces[i] = readSimpleClass(bodies);
				}

				cnt = bodies.getInteger();
				body.fields = (cnt > 0) ? new JCField[cnt] : JavaCompletion.EMPTY_FIELDS;
				for (int i = 0; i < cnt; i++) {
					body.fields[i] = new Fld(this);
				}

				cnt = bodies.getInteger();
				body.constructors = (cnt > 0) ? new JCConstructor[cnt] : JavaCompletion.EMPTY_CONSTRUCTORS;
				for (int i = 0; i < cnt; i++) {
					body.constructors[i] = new Ctr(this);
				}

				cnt = bodies.getInteger();
				body.methods = (cnt > 0) ? new JCMethod[cnt] : JavaCompletion.EMPTY_METHODS;
				for (int i = 0; i < cnt; i++) {
					body.methods[i] = new Mtd(this);
				}

				try {
					bodies.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	final class Typ extends JavaCompletion.BaseType {

		Typ() {
			clazz = readSimpleClass(bodies);
			arrayDepth = bodies.getInteger();
		}

	}

	/** Description of the declared field */
	final class Fld extends JavaCompletion.BaseField {

		Fld(JCClass clazz) {
			this.clazz = clazz;
			name = bodies.getString();
			type = new Typ();
			tagOffset = bodies.getInteger();
			modifiers = bodies.getInteger();
		}

	}

	private void readBC(JavaCompletion.BaseConstructor bc) {
		bc.tagOffset = bodies.getInteger();
		bc.modifiers = bodies.getInteger();

		int cnt = bodies.getInteger();
		bc.parameters = (cnt > 0) ? new JCParameter[cnt] : JavaCompletion.EMPTY_PARAMETERS;
		for (int i = 0; i < cnt; i++) {
			bc.parameters[i] = new Prm();
		}

		cnt = bodies.getInteger();
		bc.exceptions = (cnt > 0) ? new JCClass[cnt] : JavaCompletion.EMPTY_CLASSES;
		for (int i = 0; i < cnt; i++) {
			bc.exceptions[i] = readSimpleClass(bodies);
		}
	}

	/** Read constructor */
	final class Ctr extends JavaCompletion.BaseConstructor {

		Ctr(JCClass clazz) {
			this.clazz = clazz;
			readBC(this);
		}

	}

	/** Read method */
	final class Mtd extends JavaCompletion.BaseMethod {

		Mtd(JCClass clazz) {
			this.clazz = clazz;
			readBC(this);

			name = bodies.getString();
			returnType = new Typ();

		}

	}

	/** Description of the method parameter */
	public class Prm extends JavaCompletion.BaseParameter {

		Prm() {
			name = bodies.getString();
			type = new Typ();
		}

	}

	public String toString() {
		return "Skeleton: " + skels + " , Body: " + bodies;
		// return "strCache=" + strCache; // NOI18N
	}

}
