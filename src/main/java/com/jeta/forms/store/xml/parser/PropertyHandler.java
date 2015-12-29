package com.jeta.forms.store.xml.parser;

import org.xml.sax.SAXException;

import com.jeta.forms.store.jml.JMLUtils;
import com.jeta.forms.store.jml.dom.JMLAttributes;
import com.jeta.forms.store.xml.XMLUtils;

public class PropertyHandler implements XMLHandler {

	private String m_propname;
	private ObjectHandler m_parent;
	private ObjectHandler m_objectHandler;
	private String m_propValue;
	private JMLAttributes m_attribs;

	public PropertyHandler(ObjectHandler parent) {
		m_parent = parent;
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if (m_propValue == null)
			m_propValue = XMLUtils.unescape(new String(ch, start, length));
		else
			m_propValue = m_propValue + XMLUtils.unescape(new String(ch, start, length));
	}

	public void startElement(XMLNodeContext ctx) throws SAXException {
		try {
			String nodename = ctx.getQualifiedName();
			if ("at".equalsIgnoreCase(nodename)) {
				assert (m_propname == null);
				m_attribs = XMLUtils.toJMLAttributes(ctx.getAttributes());
				m_propname = ctx.getAttributes().getValue("name");
				/** check for inline object definition */
				String object = ctx.getAttributes().getValue("object");
				if (object != null) {
					m_objectHandler = (InlineObjectHandler) XMLHandlerFactory.getInstance().getHandler(object);
				}
				ctx.push(this);
			}
			else if ("object".equalsIgnoreCase(nodename)) {
				assert (m_objectHandler == null);
				m_objectHandler = (ObjectHandler) XMLHandlerFactory.getInstance().getHandler(ctx.getAttributes().getValue("classname"));
				assert (m_objectHandler != null);
				m_objectHandler.startElement(ctx);
			}
			else {
				throw JMLUtils.createSAXException("Invalid tag.  Expecting <at name=\"...\">.  Got instead: " + nodename);
			}
		} catch (Exception e) {
			throw JMLUtils.createSAXException(e);
		}
	}

	public void endElement(XMLNodeContext ctx) throws SAXException {
		if ("at".equalsIgnoreCase(ctx.getQualifiedName())) {
			String prop = m_propValue;
			if (m_objectHandler instanceof InlineObjectHandler) {
				/**
				 * handle case for inline objects. for example a color: <at
				 * name="foreground" object="color">0,0,255</at>
				 */
				try {
					InlineObjectHandler inlineHandler = (InlineObjectHandler) m_objectHandler;
					Object object = inlineHandler.instantiateObject(m_attribs, prop);
					m_parent.setProperty(m_propname, object, m_attribs);
				} catch (Exception e) {
					e.printStackTrace();
					throw new SAXException("Unable to create inline object for: " + m_attribs.getValue("object") + "   value: " + prop);
				}
			}
			else if (m_objectHandler == null) {
				/**
				 * this case handles standard properties that are not considered
				 * objects: Strings, Java primitives
				 */
				m_parent.setProperty(m_propname, prop, m_attribs);
			}
			else {
				/** standard case. object property: <at name="border"><object>...</object></at> */
				m_parent.setProperty(m_propname, m_objectHandler.getObject(), m_attribs);
			}

			ctx.pop(this);
		}
		else {
			throw JMLUtils.createSAXException("Invalid tag.  Expecting </at> Got instead: " + ctx.getQualifiedName());
		}
	}
}
