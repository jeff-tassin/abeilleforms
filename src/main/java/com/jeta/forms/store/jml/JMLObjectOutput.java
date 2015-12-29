package com.jeta.forms.store.jml;

import java.io.IOException;

import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.JETAPersistable;
import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;
import com.jeta.forms.store.properties.JETAProperty;

public class JMLObjectOutput implements JETAObjectOutput {
	private JMLDocument m_document;
	private JMLNode m_objnode;

	public JMLObjectOutput(JMLDocument document, JMLNode objNode) {
		m_document = document;
		m_objnode = objNode;
	}

	/**
	 * No need to write the version number for XML since we all some fields as
	 * optional
	 */
	public void writeVersion(int version) throws IOException {
		// no op
	}

	public void writeInt(String tagName, int value) throws IOException {

		try {
			m_objnode.appendChild(JMLUtils.createPropertyNode(m_document, tagName, String.valueOf(value)));
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	public void writeObject(String tagName, Object obj) throws IOException {
		try {
			if (obj != null)
				m_objnode.appendChild(JMLUtils.createPropertyNode(m_document, tagName, obj));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

	}

	public void writeBoolean(String tagName, boolean bval) throws IOException {
		try {
			m_objnode.appendChild(JMLUtils.createPropertyNode(m_document, tagName, String.valueOf(bval)));
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	public void writeFloat(String tagName, float fval) throws IOException {
		try {
			m_objnode.appendChild(JMLUtils.createPropertyNode(m_document, tagName, String.valueOf(fval)));
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	public JETAObjectOutput getSuperClassOutput(Class superClass) {
		/**
		 * Special case for storing JETAProperty classes. The main reason is
		 * because this object is stable and we don't want to polute every
		 * property with 'super' in the XML. The only attribute we need is
		 * 'name' from JETAProperty.
		 */
		if (superClass == JETAProperty.class)
			return this;

		JMLNode supernode = JMLUtils.createSuperClassNode(m_document, superClass);
		m_objnode.appendChild(supernode);
		return new JMLObjectOutput(m_document, supernode);
	}

	public static class XMLObjectOutputSerializer implements JMLSerializer {

		/**
		 * XMLSerializer implementation
		 */
		public JMLNode serialize(JMLDocument document, Object obj) throws JMLException {

			JETAPersistable persistable = (JETAPersistable) obj;

			JMLNode node = JMLUtils.createObjectNode(document, obj);
			JMLObjectOutput xoo = new JMLObjectOutput(document, node);
			try {
				persistable.write(xoo);
				return node;
			} catch (IOException e) {
				e.printStackTrace();
				throw new JMLException(e.getMessage());
			}
		}
	}

	public void writeBoolean(String string, boolean value, boolean defaultValue) throws IOException {
		if (value != defaultValue)
			writeBoolean(string, value);
	}

	public void writeInt(String string, int value, int defaultValue) throws IOException {
		if (value != defaultValue)
			writeInt(string, value);
	}

	public void writeFloat(String string, float value, float defaultValue) throws IOException {
		if (value != defaultValue)
			writeFloat(string, value);
	}

}
