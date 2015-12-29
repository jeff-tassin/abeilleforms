package com.jeta.forms.store.jml;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.dom.DefaultXMLDocument;
import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;
import com.jeta.forms.store.xml.parser.CustomSAXException;
import com.jeta.forms.store.xml.parser.MainHandler;

public class JMLUtils {

	public static void verifyObjectType(Object obj, Class cls) throws JMLException {
		if (obj != null && obj.getClass() != cls) {
			throw new JMLException("Verify object class failed.  Expecting: " + cls + "  but got: " + obj.getClass());
		}
	}

	public static JMLNode writeObject(Object obj) throws JMLException {
		try {
			JMLSerializer serializer = JMLSerializerFactory.getInstance().createSerializer(obj);
			assert (serializer != null);
			return serializer.serialize(new DefaultXMLDocument(), obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JMLException(e);
		}
	}

	public static Object readObject(InputStream istream) throws JMLException {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			MainHandler handler = new MainHandler();
			parser.parse(istream, handler);
			return handler.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new JMLException(e);
		}
	}

	public static JMLNode createObjectNode2(JMLDocument document, String className) {
		JMLNode e = document.createNode("object");
		if (className == null)
			e.setAttribute("classname", "null");
		else
			e.setAttribute("classname", className);

		return e;
	}

	public static JMLNode createObjectNode(JMLDocument document, Object obj) {
		JMLNode e = document.createNode("object");
		if (obj == null)
			e.setAttribute("classname", "null");
		else
			e.setAttribute("classname", obj.getClass().getName());

		return e;
	}

	public static JMLNode createSuperClassNode(JMLDocument document, Class superClass) {
		JMLNode e = document.createNode("super");
		if (superClass != null)
			e.setAttribute("classname", superClass.getName());

		return e;
	}

	public static JMLNode createPropertyNode(JMLDocument document, String propName, Object pvalue) throws JMLException {
		JMLNode e = document.createNode("at");
		e.setAttribute("name", propName);
		if (pvalue != null) {
			JMLSerializerFactory factory = JMLSerializerFactory.getInstance();
			JMLSerializer serializer = factory.createSerializer(pvalue);
			if (serializer == null) {
				System.out.println("    xmlutils.createPropertyNode failed   propName: " + propName + "  value: "
						+ (pvalue == null ? "NULL" : pvalue.getClass().getName()));
				System.exit(0);
			}
			e.appendChild(serializer.serialize(document, pvalue));
			if (serializer instanceof InlineJMLSerializer)
				e.setAttribute("object", ((InlineJMLSerializer) serializer).getObjectName());
		}
		return e;
	}

	public static JMLNode createPropertiesNode(JMLDocument document) {
		return document.createNode("properties");
	}

	/**
	 * If the object is a Java Object primitive type, we need to store a holder
	 * to the type. This is needed when storing collections because we simply
	 * don't want to emit the primitive value. When we deserialize the
	 * collection, there would be no way to determine the class for the value.
	 * So we use holders instead. If the specified object is not a Java
	 * primtive, then we simply return it.
	 */
	public static Object getPrimitiveHolder(Object obj) {
		if (isPrimitive(obj)) {
			return new PrimitiveHolder(obj);
		}
		else
			return obj;

	}

	public static SAXException createSAXException(String msg) {
		return createSAXException(msg, null);
	}

	public static SAXException createSAXException(Exception e) {
		return createSAXException(null, e);
	}

	public static SAXException createSAXException(String msg, Exception e) {
		if (e instanceof SAXException)
			return (SAXException) e;
		else {

			StringBuffer sbuff = new StringBuffer();
			if (msg != null)
				sbuff.append(msg);
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter(writer);
			if (e == null)
				new Exception().printStackTrace(pw);
			else {
				sbuff.append(e.getMessage());
				e.printStackTrace(pw);
			}

			sbuff.append("\n");
			sbuff.append(writer.toString());
			sbuff.append("\n");

			if (e == null)
				return CustomSAXException.create(msg, sbuff.toString());
			else
				return CustomSAXException.create(msg, sbuff.toString());
		}

	}

	public static boolean isPrimitive(Object obj) {
		return (obj instanceof Byte || obj instanceof Boolean || obj instanceof Short || obj instanceof Character || obj instanceof Integer
				|| obj instanceof Float || obj instanceof Double);
	}

}
