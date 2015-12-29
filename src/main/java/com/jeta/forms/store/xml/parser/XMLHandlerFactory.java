package com.jeta.forms.store.xml.parser;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import com.jeta.forms.store.JETAPersistable;
import com.jeta.forms.store.jml.JMLException;
import com.jeta.forms.store.jml.PrimitiveHolder;
import com.jeta.forms.store.jml.dom.JMLAttributes;
import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.forms.store.support.PropertyMap;

public class XMLHandlerFactory {

	/**
	 * A map of class names (String objects) to XMLHandlers.
	 */
	private HashMap m_handlers = new HashMap();

	private static XMLHandlerFactory m_singleton = new XMLHandlerFactory();

	private XMLHandlerFactory() {
		registerHandlers();
	}

	public static XMLHandlerFactory getInstance() {
		return m_singleton;
	}

	protected XMLHandler createHandler(String className) {
		Class c = (Class) m_handlers.get(className);
		if (c != null) {
			try {
				return (XMLHandler) c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			// try JETAPersistable
			try {
				c = Class.forName(className);
				if (JETAPersistable.class.isAssignableFrom(c)) {
					return new JETAPersistableHandler();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void registerHandlers() {
		m_handlers.put("Boolean", PrimitiveHandler.class);
		m_handlers.put("java.lang.Boolean", PrimitiveHandler.class);
		m_handlers.put("Byte", PrimitiveHandler.class);
		m_handlers.put("java.lang.Byte", PrimitiveHandler.class);
		m_handlers.put("Character", PrimitiveHandler.class);
		m_handlers.put("java.lang.Character", PrimitiveHandler.class);
		m_handlers.put("Short", PrimitiveHandler.class);
		m_handlers.put("java.lang.Short", PrimitiveHandler.class);
		m_handlers.put("Integer", PrimitiveHandler.class);
		m_handlers.put("java.lang.Integer", PrimitiveHandler.class);
		m_handlers.put("Long", PrimitiveHandler.class);
		m_handlers.put("java.lang.Long", PrimitiveHandler.class);
		m_handlers.put("Float", PrimitiveHandler.class);
		m_handlers.put("java.lang.Float", PrimitiveHandler.class);
		m_handlers.put("Double", PrimitiveHandler.class);
		m_handlers.put("java.lang.Double", PrimitiveHandler.class);

		m_handlers.put(PropertyMap.class.getName(), PropertyMapHandler.class);
		m_handlers.put(HashMap.class.getName(), HashMapHandler.class);
		m_handlers.put(ArrayList.class.getName(), ListHandler.class);
		m_handlers.put(LinkedList.class.getName(), ListHandler.class);
		m_handlers.put(Insets.class.getName(), InsetsHandler.class);
		m_handlers.put("insets", InsetsHandler.class);
		m_handlers.put(ColorHolder.class.getName(), ColorHolderHandler.class);
		m_handlers.put("color", ColorHolderHandler.class);
		m_handlers.put(Object[].class.getName(), ObjectArrayHandler.class);
		m_handlers.put(PrimitiveHolder.class.getName(), PrimitiveHolderHandler.class);

	}

	/**
	 * Returns true if the specified class is a Java primitive object
	 * (Long.class, Integer.class, etc )
	 */
	private boolean isPrimitive(Class c) {
		return (c == Boolean.class || c == Byte.class || c == Character.class || c == Short.class || c == Integer.class || c == Long.class || c == Float.class || c == Double.class);
	}

	/**
	 * Returns true if the specified name is a class is a Java primitive object
	 * (Long.class, Integer.class, etc )
	 */
	private boolean isPrimitive(String className) {
		if ("Boolean".equalsIgnoreCase(className) || ("java.lang.Boolean").equalsIgnoreCase(className)) {
			return true;
		}
		else if ("Byte".equalsIgnoreCase(className) || ("java.lang.Byte").equalsIgnoreCase(className)) {
			return true;
		}
		else if ("Character".equalsIgnoreCase(className) || ("java.lang.Character").equalsIgnoreCase(className)) {
			return true;
		}
		else if ("Short".equalsIgnoreCase(className) || ("java.lang.Short").equalsIgnoreCase(className)) {
			return true;
		}
		else if ("Integer".equalsIgnoreCase(className) || ("java.lang.Integer").equalsIgnoreCase(className)) {
			return true;
		}
		else if ("Long".equalsIgnoreCase(className) || ("java.lang.Long").equalsIgnoreCase(className)) {
			return true;
		}
		else if ("Float".equalsIgnoreCase(className) || ("java.lang.Float").equalsIgnoreCase(className)) {
			return true;
		}
		else if ("Double".equalsIgnoreCase(className) || ("java.lang.Double").equalsIgnoreCase(className)) {
			return true;
		}
		else {
			return false;
		}
	}

	public XMLHandler getHandler(String className) throws JMLException {
		try {

			if ("null".equalsIgnoreCase(className) || className.length() == 0)
				return new NullHandler();

			if (isPrimitive(className)) {
				if (!className.startsWith("java.lang")) {
					className = "java.lang." + className;
				}
			}
			XMLHandler handler = createHandler(className);
			if (handler == null) {
				throw new JMLException("XMLHandlerFactory  handler is null for: " + className);
			}
			return handler;
		} catch (Exception e) {
			throw new JMLException(e.getMessage());
		}
	}

	private static class NullHandler extends ObjectHandler {

		public Object getProperty(String propName) {
			return null;
		}

		protected void setProperty(Object name, Object value, JMLAttributes attribs) throws SAXException {
			assert (false);
		}

		protected Object instantiateObject(JMLAttributes attribs) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
			String className = attribs.getValue("classname");
			if ("null".equalsIgnoreCase(className) || className.length() == 0)
				return null;
			else {
				assert (false);
				return null;
			}
		}

		public Object getObject() {
			return null;
		}
	}
}
