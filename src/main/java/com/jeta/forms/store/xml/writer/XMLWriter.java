/*
 * Copyright (c) 2005 Jeff Tassin.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.store.xml.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import com.jeta.forms.store.jml.dom.JMLNode;
import com.jeta.forms.store.jml.dom.TextJMLNode;
import com.jeta.forms.store.xml.XMLUtils;

/**
 * A helper class that writes an JMlNode and all of its children to an XML file.
 * 
 * @author Jeff Tassin
 */
public class XMLWriter {
	private Writer m_writer;

	/** the number of spaces to indent between nested tags */
	private static final int DEFAULT_INDENT = 1;

	private int m_indent_pos = -1;

	/**
	 * ctor
	 */
	public XMLWriter() {

	}

	/**
	 * Writes the JMLNode to the writer associated with this object.
	 * 
	 * @param node
	 *            the node to convert to XML
	 * @param writer
	 *            A writer where the XML will be output to.
	 * @throws IOException
	 */
	public void write(Writer writer, JMLNode node) throws IOException {
		m_writer = writer;
		m_writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write(node);
	}

	/**
	 * Writes the JMLNode to the writer associated with this object.
	 * 
	 * @param node
	 * @throws IOException
	 */
	private void write(JMLNode node) throws IOException {
		assert (node != null);

		indent();

		if (node instanceof TextJMLNode) {
			TextJMLNode txtnode = (TextJMLNode) node;
			if (txtnode.getTextValue() != null)
				m_writer.write(XMLUtils.escape(txtnode.getTextValue()));

		}
		else {
			m_writer.write('\n');

			for (int index = 0; index < m_indent_pos; index++)
				m_writer.write(' ');

			m_writer.write('<');
			m_writer.write(node.getNodeName());
			m_writer.write(' ');
			writeAttributes(node);
			if (node.getChildCount() == 0) {
				m_writer.write('/');
				m_writer.write(">");
			}
			else {

				m_writer.write(">");
				for (int index = 0; index < node.getChildCount(); index++) {
					JMLNode childnode = node.getNode(index);
					write(childnode);
				}

				if (node.getChildCount() > 0 && !(node.getNode(node.getChildCount() - 1) instanceof TextJMLNode)) {
					m_writer.write('\n');
					for (int index = 0; index < m_indent_pos; index++)
						m_writer.write(' ');
				}

				m_writer.write("</");
				m_writer.write(node.getNodeName());
				m_writer.write('>');
			}

		}
		unindent();
	}

	private void writeAttributes(JMLNode node) throws IOException {
		Collection anames = node.getAttributeNames();
		Iterator iter = anames.iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			String value = node.getAttribute(name);
			m_writer.write(name);
			m_writer.write("=\"");
			m_writer.write(XMLUtils.escape(value));
			m_writer.write("\"");
			if (iter.hasNext())
				m_writer.write(' ');
		}
	}

	private void indent() {
		m_indent_pos++;

	}

	private void unindent() {
		m_indent_pos--;
	}
}
