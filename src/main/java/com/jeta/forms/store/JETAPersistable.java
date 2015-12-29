package com.jeta.forms.store;

import java.io.Externalizable;
import java.io.IOException;

/**
 * Defines an interface that all persitable objects in the forms designer must
 * implement. It is similar to Exernalizable except that it adds read/write
 * methods that take JETAObjectInput and JETAObjectOutput. This is needed so
 * that our 'Serializable' objects can be stored using an arbitrary persistence
 * scheme. Currently, we support standard Java Serialization and XML. Using this
 * approach, it would be easy to added support for any other type of format.
 * 
 * @author Jeff Tassin
 */
public interface JETAPersistable extends Externalizable {

	/**
	 * Objects implement this method to restore their state. Primitives and
	 * objects can be read from the JETAObjectInput instance.
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException;

	/**
	 * Objects implement this method to store their state. Primitives and
	 * objects can be written using the JETAObjectOutput instance.
	 */
	public void write(JETAObjectOutput out) throws IOException;

}
