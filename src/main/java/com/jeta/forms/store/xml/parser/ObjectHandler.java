package com.jeta.forms.store.xml.parser;

import java.util.HashMap;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.dom.JMLAttributes;
import com.jeta.forms.store.xml.XMLUtils;

public class ObjectHandler implements XMLHandler {

	private Object m_object = null;
	private HashMap m_properties = new HashMap();
	/** for debugging */
	private String m_classname;

	/**
	 * Handler that represents the properties for any superclasses of the object
	 * we are persisting.
	 */
	private ObjectHandler m_superClassHandler;

	public Object getProperty(String propName) {
		return m_properties.get(propName);
	}

	protected void setProperty(Object name, Object value, JMLAttributes attribs) throws SAXException {
		if ("version".equalsIgnoreCase(name.toString())) {
			try {
				int ival = Integer.parseInt(value.toString());
			} catch (Exception e) {
				System.out.println("ObjectHandler version failed!!!   Classname: " + m_classname);
				e.printStackTrace();
			}
		}

		if (m_properties.containsKey(name)) {
			System.out.println("ObjectHandler already has property: " + name + "  oldvalue: " + m_properties.get(name));
		}

		m_properties.put(name, value);
	}

	public void startElement(XMLNodeContext ctx) throws SAXException {

		if ("object".equalsIgnoreCase(ctx.getQualifiedName())) {
			if (m_classname != null) {
				System.out.println("Found non-null class name in ObjectHandler.startElement  existing name: " + m_classname + "   new name: "
						+ ctx.getAttributes().getValue("classname"));
				assert (false);
			}
			assert (m_object == null);
			m_classname = ctx.getAttributes().getValue("classname");
			ctx.push(this);
			try {
				m_object = instantiateObject(XMLUtils.toJMLAttributes(ctx.getAttributes()));
			} catch (Exception e) {
				throw new SAXException(e.getMessage() + " - ObjectHandler unable to instantiate object for classname: "
						+ ctx.getAttributes().getValue("classname"), e);
			}
		}
		else if ("super".equalsIgnoreCase(ctx.getQualifiedName())) {
			assert (m_superClassHandler == null);
			m_superClassHandler = new JETAPersistableHandler();
			ctx.push(m_superClassHandler);
			// don't call start element for <super>
		}
		else if ("at".equalsIgnoreCase(ctx.getQualifiedName())) {
			PropertyHandler phandler = new PropertyHandler(this);
			phandler.startElement(ctx);
		}
		else {
			throw new SAXException("Invalid tag.  Expecting <object classname=\"...\">.  Got instead: " + ctx.getQualifiedName());
		}
	}

	protected Object instantiateObject(JMLAttributes attribs) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String value = attribs.getValue("classname");
		if ("null".equalsIgnoreCase(value) || value.length() == 0)
			return null;
		else
			return Class.forName(value).newInstance();
	}

	public void endElement(XMLNodeContext ctx) throws SAXException {
		if ("object".equalsIgnoreCase(ctx.getQualifiedName())) {
			ctx.pop(this);
		}
		else if ("super".equalsIgnoreCase(ctx.getQualifiedName())) {
			ctx.pop(this);
		}
		else {
			throw new SAXException("Invalid tag.  Expecting <object classname=\"...\">.  Got instead: " + ctx.getQualifiedName());
		}
	}

	public Object getObject() {
		return m_object;
	}

	public ObjectHandler getSuperClassHandler() {
		return m_superClassHandler == null ? this : m_superClassHandler;
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		// ignore
	}

}
