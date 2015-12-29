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

package com.jeta.swingbuilder.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.interfaces.app.ObjectStore;
import com.jeta.swingbuilder.interfaces.resources.ResourceLoader;

/**
 * This class implements the ObjectStore interface. It is used to manage
 * serialization of object properties. The idea is to decouple application
 * components from where we are serializing the data. We store the data in two
 * directories: home/data and home/data/work The work directory is used to
 * temporarily write out the store. If an error occurs, the user can still
 * recover from the old state. If the write is successful to the work area, then
 * we re-write to the data area.
 * 
 * @author Jeff Tassin
 */
public class ApplicationStateStore implements ObjectStore {
	private HashMap m_objects; // the set of objects

	/** the directory where we store all of our objects */
	private String m_directory;

	/** the directory we use as a work area */
	private String m_workdirectory;

	public static final String PARENT_DIRECTORY = "data";

	/**
	 * An 'empty' object
	 */
	private Object EMPTY_OBJECT = new Object();

	/**
	 * Flag that indicates if store is readonl
	 */
	private boolean m_readonly = false;

	/**
	 * ctor
	 */
	public ApplicationStateStore(String directory) {
		m_directory = directory;
		m_workdirectory = m_directory + File.separatorChar + "temp";
		m_objects = new HashMap();
	}

	/**
	 * Deletes the object from the store cache as well as on disk
	 */
	private synchronized void _delete(String keyName, boolean btemp) throws IOException {
		if (isReadOnly())
			return;

		try {
			ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
			String resource = getTargetDirectory(btemp) + File.separatorChar + keyName;
			loader.deleteResource(resource);
		} catch (IOException io) {
			// eat here
		}
	}

	/**
	 * Deletes the object from the store cache as well as on disk
	 */
	public synchronized void delete(String keyName) throws IOException {
		_delete(keyName, true);
		_delete(keyName, false);
		m_objects.remove(keyName);
	}

	/**
	 * Saves the entire store to disk
	 */
	public synchronized void flush() throws IOException {
		save();
	}

	/**
	 * Saves the specified object to disk
	 */
	private synchronized void _flush(String keyName, boolean btemp) throws IOException {
		if (isReadOnly())
			return;

		ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
		// make sure the directory exists
		String targetdir = getTargetDirectory(btemp);
		loader.createSubdirectories(targetdir);

		Object obj = m_objects.get(keyName);
		if (obj != null && obj != EMPTY_OBJECT) {
			// we write each object to its own file
			OutputStream ostream = loader.getOutputStream(targetdir + File.separatorChar + keyName);

			byte[] data = (byte[]) obj;
			ostream.write(data);
			ostream.flush();
			ostream.close();
		}
	}

	/**
	 * Saves the specified object to disk
	 */
	public synchronized void flush(String keyName) throws IOException {
		// flush to temp area to verify there are no problems
		_flush(keyName, true);
		// ok, if succesful, the flush the object to the actual area
		_flush(keyName, false);
	}

	/**
	 * @return the relative path to the directory where we store our objects
	 */
	public String getTargetDirectory() {
		return getTargetDirectory(false);
	}

	/**
	 * @return the relative path to the directory where we store our objects
	 */
	public String getTargetDirectory(boolean temp) {
		if (temp)
			return PARENT_DIRECTORY + File.separatorChar + m_workdirectory;
		else
			return PARENT_DIRECTORY + File.separatorChar + m_directory;
	}

	/**
	 * @return true if the store is readonly.
	 */
	public boolean isReadOnly() {
		return m_readonly;
	}

	/**
	 * ObjectStore implementation
	 */
	public synchronized Object load(String keyName) throws IOException {
		if (m_objects == null)
			return null;
		else {
			Object obj = m_objects.get(keyName);
			if (obj == null) {
				obj = loadObject(keyName);
				if (obj != null)
					m_objects.put(keyName, obj);

			}

			if (obj == EMPTY_OBJECT)
				obj = null;

			/**
			 * all objects are currently stored as byte arrays, so we need to
			 * deserialize from the byte array to an actual object
			 */
			if (obj != null) {
				byte[] data = (byte[]) obj;
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				ObjectInputStream ois = new ObjectInputStream(bais);
				try {
					obj = ois.readObject();
				} catch (ClassNotFoundException cnfe) {
					IOException ioe = new IOException(cnfe.getLocalizedMessage());
					throw ioe;
				}
			}
			return obj;
		}
	}

	/**
	 * loads the named object from the store. This object is also deserialized
	 * at this point since the store only keeps track of serialized data
	 * 
	 * @param keyName
	 *            the unique name of the lobject to retrieve
	 * @param defaultValue
	 *            the value to return if the name is not found in the store
	 * @return the instantiated object from the store. The default value is
	 *         returned if the name is not found.
	 * @throws IOException
	 *             if an error occurs during deserialization
	 */
	public Object load(String keyName, Object defaultValue) throws IOException {
		Object result = load(keyName);
		if (result == null)
			result = defaultValue;
		return result;
	}

	/**
	 * Loads a named object from the state store and puts it in the cache
	 */
	private synchronized Object loadObject(String keyName) {
		if (isReadOnly())
			return null;

		Object result = null;
		ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);

		// make sure the directory exists
		try {
			String targetdir = getTargetDirectory();
			InputStream istream = loader.getInputStream(targetdir + File.separatorChar + keyName);
			if (istream != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				int numread = istream.read(buff);
				while (numread > 0) {
					bos.write(buff, 0, numread);
					numread = istream.read(buff);
				}
				byte[] data = bos.toByteArray();

				istream.close();
				result = data;

			}
			return result;
		} catch (FileNotFoundException fnfe) {
			result = EMPTY_OBJECT;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Writes out the data to disk
	 */
	private synchronized void _save(boolean btemp) throws IOException {
		if (isReadOnly())
			return;

		ResourceLoader loader = (ResourceLoader) JETARegistry.lookup(ResourceLoader.COMPONENT_ID);
		// make sure the directory exists
		String targetdir = getTargetDirectory(btemp);
		loader.createSubdirectories(targetdir);

		Iterator iter = m_objects.keySet().iterator();
		while (iter.hasNext()) {
			String objectname = (String) iter.next();

			Object obj = m_objects.get(objectname);
			if (obj != null && obj != EMPTY_OBJECT) {
				// we write each object to its own file
				OutputStream ostream = loader.getOutputStream(targetdir + File.separatorChar + objectname);
				byte[] data = (byte[]) obj;
				ostream.write(data);
				ostream.flush();
				ostream.close();
			}
		}
	}

	/**
	 * Writes out the data to disk
	 */
	public synchronized void save() throws IOException {
		// save to temp area to verify there are no problems
		_save(true);
		// ok, if succesful, save the object to the actual area
		_save(false);
	}

	/**
	 * Sets the flag that indicates if the store is readonly.
	 */
	public void setReadOnly(boolean readonly) {
		m_readonly = readonly;
	}

	/**
	 * ObjectStore implementation
	 */
	public synchronized void store(String keyName, Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream s = new ObjectOutputStream(baos);
		s.writeObject(obj);
		s.flush();
		m_objects.put(keyName, baos.toByteArray());
	}

}
