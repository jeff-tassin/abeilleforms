package com.jeta.forms.store;

import java.io.IOException;

/**
 * An interface for reading primitives and objects from a persistent store. This
 * interface is similar to java.io.DataInputStream but is slightly modified to
 * support reading named primitives and objects (to support tagged formats such
 * as XML). For those formats that don't use tags (i.e. standard Java
 * serialization), the tag names are ignored.
 * 
 * @author Jeff Tassin
 */
public interface JETAObjectInput {

	/**
	 * Helper method that simply forwards the call to read("version")
	 */
	public int readVersion() throws IOException;

	/**
	 * Reads an integer with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the integer.
	 * @return the integer value with the specified name. Zero is returned if a
	 *         value is not found with specified name.
	 */
	public int readInt(String tagName) throws IOException;

	/**
	 * Reads an integer with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the integer.
	 * @param defaultValue
	 *            if the value is not found, the default value is returned.
	 * @return the integer value with the specified name (or the default value)
	 */
	public int readInt(String tagName, int defaultValue) throws IOException;

	/**
	 * Reads an object with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the object.
	 * @return the object with the specified name. Null is returned if a value
	 *         is not found with the specified name.
	 */
	public Object readObject(String tabName) throws ClassNotFoundException, IOException;

	/**
	 * Reads an String object with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the object.
	 * @return the String with the specified name. Null is returned if a value
	 *         is not found with the specified name.
	 */
	public String readString(String string) throws IOException;

	/**
	 * Reads a boolean with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the boolean.
	 * @return the boolean with the specified name. False is returned if a value
	 *         is not found with the specified name.
	 */
	public boolean readBoolean(String string) throws IOException;

	/**
	 * Reads a boolean with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the integer.
	 * @param defaultValue
	 *            if the value is not found, the default value is returned.
	 * @return the boolean value with the specified name (or the default value)
	 */
	public boolean readBoolean(String tagName, boolean defaultValue) throws IOException;

	/**
	 * Reads a float with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the float.
	 * @return the float with the specified name. Zero is returned if a value is
	 *         not found with the specified name.
	 */
	public float readFloat(String string) throws IOException;

	/**
	 * Reads a float with the specified name from the store.
	 * 
	 * @param tagName
	 *            the name of the integer.
	 * @param defaultValue
	 *            if the value is not found, the default value is returned.
	 * @return the float value with the specified name (or the default value)
	 */
	public float readFloat(String tagName, float defaultValue) throws IOException;

	/**
	 * Returns a JETAObjectInput instance for reading a super class of the
	 * object that is currently being read. This is needed those storage formats
	 * that don't automatically handle inheritance (i.e. XML). Java
	 * Serialization handles this automatically, so this is a no-op method and
	 * simply returns the same JETAObjectInput instance.
	 */
	public JETAObjectInput getSuperClassInput();

}
