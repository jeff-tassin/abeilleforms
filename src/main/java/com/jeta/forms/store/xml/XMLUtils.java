package com.jeta.forms.store.xml;

import org.xml.sax.Attributes;

import com.jeta.forms.store.jml.dom.DefaultAttributes;
import com.jeta.forms.store.jml.dom.JMLAttributes;

public class XMLUtils {

	/**
	 * Escape the "special" characters as required for inclusion in XML elements
	 * Replaces all incidences of & with & < with < > with > " with " ' with
	 * &apos;
	 * 
	 * @param s
	 *            The string to scan
	 * @return String
	 */
	public static String escape(String s) {
		char[] array = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			switch (array[i]) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			default:
				sb.append(array[i]);
			}
		}
		return sb.toString();
	}

	public static String unescape(String str) {
		// < &lt;
		// > &gt;
		// & &amp;
		// " &quot;
		// ' &apos;

		if (str == null)
			return null;

		StringBuffer sbuff = new StringBuffer();
		for (int index = 0; index < str.length(); index++) {
			char c = str.charAt(index);

			if (c == '&') {
				if (index + 3 < str.length()) {
					char c1 = str.charAt(index + 1);
					char c2 = str.charAt(index + 2);
					char c3 = str.charAt(index + 3);

					if (c1 == 'l' && c2 == 't' && c3 == ';') {
						sbuff.append('<');
						index += 3;
						continue;
					}
					else if (c1 == 'g' && c2 == 't' && c3 == ';') {
						sbuff.append('>');
						index += 3;
						continue;
					}
					else {
						if (index + 4 < str.length()) {
							char c4 = str.charAt(index + 4);
							if (c1 == 'a' && c2 == 'm' && c3 == 'p' && c4 == ';') {
								sbuff.append('&');
								index += 4;
								continue;
							}
							else {
								if (index + 5 < str.length()) {
									char c5 = str.charAt(index + 5);
									if (c1 == 'q' && c2 == 'u' && c3 == 'o' && c4 == 't' && c5 == ';') {
										sbuff.append('\"');
										index += 5;
										continue;
									}
									else if (c1 == 'a' && c2 == 'p' && c3 == 'o' && c4 == 's' && c5 == ';') {
										sbuff.append('\'');
										index += 5;
										continue;
									}
								}
							}
						}
					}
				}
			}
			sbuff.append(c);
		}
		return sbuff.toString();
	}

	/**
	 * Converts a SAX Attributes to a JML Attributes
	 * 
	 * @param attributes
	 * @return
	 */
	public static JMLAttributes toJMLAttributes(Attributes attributes) {
		DefaultAttributes da = new DefaultAttributes();
		for (int index = 0; index < attributes.getLength(); index++) {
			String qname = attributes.getQName(index);
			da.setAttribute(qname, attributes.getValue(qname));
		}
		return da;
	}

}
