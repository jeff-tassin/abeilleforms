package com.jeta.forms.store;

import java.io.IOException;
import java.io.ObjectInput;

public class JavaExternalizableObjectInput implements JETAObjectInput {

	private ObjectInput m_in;

	public JavaExternalizableObjectInput(ObjectInput delegate) {
		m_in = delegate;
	}

	public int readVersion() throws IOException {
		return m_in.readInt();
	}

	public float readFloat(String tagName) throws IOException {
		return m_in.readFloat();
	}

	public float readFloat(String tagName, float defaultValue) throws IOException {
		return m_in.readFloat();
	}

	public int readInt(String tagName) throws IOException {
		return m_in.readInt();
	}

	public int readInt(String tagName, int defaultValue) throws IOException {
		return m_in.readInt();
	}

	public Object readObject(String tabName) throws ClassNotFoundException, IOException {
		return m_in.readObject();
	}

	public String readString(String string) throws IOException {
		try {
			return (String) m_in.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("JavaExternalizableObjectInput readString failed. ClassNotFoundException. Expected a String object");
		}
	}

	public boolean readBoolean(String string) throws IOException {
		return m_in.readBoolean();
	}

	public JETAObjectInput getSuperClassInput() {
		return this;
	}

	public boolean readBoolean(String tagName, boolean defaultValue) throws IOException {
		return readBoolean(tagName);
	}

}
