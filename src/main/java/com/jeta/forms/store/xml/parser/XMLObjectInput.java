package com.jeta.forms.store.xml.parser;

import java.io.IOException;

import com.jeta.forms.store.JETAObjectInput;

public class XMLObjectInput implements JETAObjectInput {

	private JETAPersistableHandler m_handler;

	public XMLObjectInput(JETAPersistableHandler handler) {
		m_handler = handler;
		assert (m_handler != null);
	}

	public int readVersion() throws IOException {
		// return readInt( "version" );
		return Integer.MAX_VALUE;
	}

	public int readInt(String propName) throws IOException {
		Object pvalue = m_handler.getProperty(propName);
		return (pvalue == null ? 0 : Integer.parseInt(pvalue.toString()));
	}

	public Object readObject(String propName) throws ClassNotFoundException, IOException {
		return m_handler.getProperty(propName);
	}

	public String readString(String propName) throws IOException {
		Object pvalue = m_handler.getProperty(propName);
		return (pvalue == null ? null : pvalue.toString());
	}

	public boolean readBoolean(String propName) throws IOException {
		Object pvalue = m_handler.getProperty(propName);
		return (pvalue == null ? false : Boolean.valueOf(pvalue.toString()).booleanValue());
	}

	public float readFloat(String propName) throws IOException {
		Object pvalue = m_handler.getProperty(propName);
		return (pvalue == null ? 0.0f : Float.parseFloat(pvalue.toString()));
	}

	public JETAObjectInput getSuperClassInput() {
		return new XMLObjectInput((JETAPersistableHandler) m_handler.getSuperClassHandler());
	}

	public int readInt(String propName, int defaultValue) throws IOException {
		Object pvalue = m_handler.getProperty(propName);
		return (pvalue == null ? defaultValue : Integer.parseInt(pvalue.toString()));
	}

	public boolean readBoolean(String propName, boolean defaultValue) throws IOException {
		Object pvalue = m_handler.getProperty(propName);
		return (pvalue == null ? defaultValue : Boolean.valueOf(pvalue.toString()).booleanValue());
	}

	public float readFloat(String propName, float defaultValue) throws IOException {
		Object pvalue = m_handler.getProperty(propName);
		return (pvalue == null ? defaultValue : Float.parseFloat(pvalue.toString()));
	}

}
