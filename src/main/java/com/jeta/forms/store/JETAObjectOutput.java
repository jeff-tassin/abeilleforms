package com.jeta.forms.store;

import java.io.IOException;

/**
 * An interface for writing primitives and objects to a persistent store. This
 * interface is similar to java.io.DataOutputStream but is slightly modified to
 * support writing named primitives and objects (to support tagged formats such
 * as XML). For those formats that don't use tags (i.e. standard Java
 * serialization), the tag names are ignored.
 * 
 * @author Jeff Tassin
 */
public interface JETAObjectOutput {

	/**
	 * Helper method that simply forwards the call to write("version", version)
	 */
	public void writeVersion(int version) throws IOException;

	/**
	 * Writes an integer with the specified name to the store.
	 * 
	 * @param tagName
	 *            the name of the integer.
	 * @param value
	 *            the value to write
	 */
	public void writeInt(String tagName, int value) throws IOException;

	/**
	 * Writes an integer with the specified name to the store. If the value is
	 * the same as the default value, the store has the option of not saving the
	 * value.
	 * 
	 * @param tagName
	 * @param value
	 * @param defaultValue
	 */
	public void writeInt(String string, int value, int defaultValue) throws IOException;

	/**
	 * Writes an object with the specified name to the store.
	 * 
	 * @param tagName
	 *            the name of the object.
	 * @param obj
	 *            the object to write
	 */
	public void writeObject(String tagName, Object obj) throws IOException;

	/**
	 * Writes a boolean with the specified name to the store.
	 * 
	 * @param tagName
	 *            the name of the boolean.
	 * @param bval
	 *            the value to write
	 */
	public void writeBoolean(String string, boolean bval) throws IOException;

	/**
	 * Writes a boolean with the specified name to the store. If the value is
	 * the same as the default value, the store has the option of not saving the
	 * value.
	 * 
	 * @param tagName
	 *            the name of the boolean.
	 * @param bval
	 *            the value to write
	 */
	public void writeBoolean(String string, boolean bval, boolean defaultValue) throws IOException;

	/**
	 * Writes an float with the specified name to the store.
	 * 
	 * @param tagName
	 *            the name of the float.
	 * @param fval
	 *            the value to write
	 */
	public void writeFloat(String string, float fval) throws IOException;

	/**
	 * Writes a float with the specified name to the store. If the value is the
	 * same as the default value, the store has the option of not saving the
	 * value.
	 * 
	 * @param tagName
	 * @param value
	 * @param defaultValue
	 */
	public void writeFloat(String string, float value, float defaultValue) throws IOException;

	/**
	 * Returns a JETAObjectOutput instance for writing a super class of the
	 * object that is currently being written. This is needed those storage
	 * formats that don't automatically handle inheritance (i.e. XML). Java
	 * Serialization handles this automatically, so this is a no-op method and
	 * simply returns the same JETAObjectOutput instance.
	 * 
	 * @param superClass
	 *            an optional super class. This is mainly used for making the
	 *            XML a little clearer.
	 */
	public JETAObjectOutput getSuperClassOutput(Class superClass);

}
