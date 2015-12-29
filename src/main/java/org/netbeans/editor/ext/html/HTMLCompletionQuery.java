/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.editor.ext.html;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.CompletionQuery;
import org.netbeans.editor.ext.html.dtd.DTD;

/**
 * HTML completion results finder
 * 
 * @author Petr Nejedly
 * @version 1.00
 */
public class HTMLCompletionQuery implements CompletionQuery {

	/**
	 * Perform the query on the given component. The query usually gets the
	 * component's document, the caret position and searches back to examine
	 * surrounding context. Then it returns the result.
	 * 
	 * @param component
	 *            the component to use in this query.
	 * @param offset
	 *            position in the component's document to which the query will
	 *            be performed. Usually it's a caret position.
	 * @param support
	 *            syntax-support that will be used during resolving of the
	 *            query.
	 * @return result of the query or null if there's no result.
	 */
	public CompletionQuery.Result query(JTextComponent component, int offset, SyntaxSupport support) {
		BaseDocument doc = (BaseDocument) component.getDocument();
		if (doc.getLength() == 0)
			return null; // nothing to examine
		HTMLSyntaxSupport sup = (HTMLSyntaxSupport) support.get(HTMLSyntaxSupport.class);
		if (sup == null)
			return null;// No SyntaxSupport for us, no hint for user
		DTD dtd = sup.getDTD();
		if (dtd == null)
			return null; // We have no knowledge about the structure!

		try {
			TokenItem item = null;
			TokenItem prev = null;
			// are we inside token or between tokens
			boolean inside = false;

			item = sup.getTokenChain(offset, offset + 1);
			if (item != null) { // inside document
				prev = item.getPrevious();
				inside = item.getOffset() < offset;
			}
			else { // @ end of document
				prev = sup.getTokenChain(offset - 1, offset); // !!!
			}
			boolean begin = (prev == null && !inside);
			/*
			 * if( prev == null && !inside ) System.err.println( "Beginning of
			 * document, first token = " + item.getTokenID() ); else if( item ==
			 * null ) System.err.println( "End of document, last token = " +
			 * prev.getTokenID() ); else if( ! inside ) System.err.println(
			 * "Between tokens " + prev.getTokenID() + " and " +
			 * item.getTokenID() ); else System.err.println( "Inside token " +
			 * item.getTokenID() );
			 */

			if (begin)
				return null;

			TokenID id = null;
			List l = null;
			int len = 1;
			int itemOffset = 0;
			String preText = null;

			if (inside) {
				id = item.getTokenID();
				preText = item.getImage().substring(0, offset - item.getOffset());
				itemOffset = item.getOffset();
			}
			else {
				id = prev.getTokenID();
				preText = prev.getImage().substring(0, offset - prev.getOffset());
				itemOffset = prev.getOffset();
			}

			/*
			 * Here are completion finders, each have its own set of rules and
			 * source of results They are now written just for testing rules, I
			 * will rewrite them to more compact and faster, tree form, as soon
			 * as i'll have them all.
			 */

			/* Character reference finder */
			if ((id == HTMLTokenContext.TEXT || id == HTMLTokenContext.VALUE) && preText.endsWith("&")) {
				l = translateCharRefs(offset - len, len, dtd.getCharRefList(""));
			}
			else if (id == HTMLTokenContext.CHARACTER) {
				if (inside || !preText.endsWith(";")) {
					len = offset - itemOffset;
					l = translateCharRefs(offset - len, len, dtd.getCharRefList(preText.substring(1)));
				}
				/* Tag finder */
			}
			else if (id == HTMLTokenContext.TEXT && preText.endsWith("<")) {
				// There will be lookup for possible StartTags, in SyntaxSupport
				// l = translateTags( offset-len, len, sup.getPossibleStartTags
				// ( offset-len, "" ) );
				l = translateTags(offset - len, len, dtd.getElementList(""));

				// System.err.println("l = " + l );
			}
			else if (id == HTMLTokenContext.TAG && preText.startsWith("<") && !preText.startsWith("</")) {
				len = offset - itemOffset;
				l = translateTags(offset - len, len, dtd.getElementList(preText.substring(1)));
				// l = translateTags( offset-len, len, sup.getPossibleStartTags
				// ( offset-len, preText.substring( 1 ) ) );
				/* EndTag finder */
			}
			else if (id == HTMLTokenContext.TEXT && preText.endsWith("</")) {
				len = 2;
				l = sup.getPossibleEndTags(offset, "");
			}
			else if (id == HTMLTokenContext.TAG && preText.startsWith("</")) {
				len = offset - itemOffset;
				l = sup.getPossibleEndTags(offset, preText.substring(2));

				/* Argument finder */
				/*
				 * TBD: It is possible to have arg just next to quoted value of
				 * previous arg, these rules doesn't match start of such arg
				 * this case because of need for matching starting quote
				 */
			}
			else if (id == HTMLTokenContext.WS || id == HTMLTokenContext.ARGUMENT) {
				SyntaxElement elem = null;
				try {
					elem = sup.getElementChain(offset);
				} catch (BadLocationException e) {
					return null;
				}

				if (elem == null)
					return null;

				if (elem.getType() == SyntaxElement.TYPE_TAG) { // not endTags
					SyntaxElement.Tag tagElem = (SyntaxElement.Tag) elem;

					String tagName = tagElem.getName().toUpperCase();
					DTD.Element tag = dtd.getElement(tagName);

					if (tag == null)
						return null; // unknown tag

					String prefix = (id == HTMLTokenContext.ARGUMENT) ? preText : "";
					len = prefix.length();

					List possible = tag.getAttributeList(prefix); // All
																	// attribs
																	// of given
																	// tag

					Collection existing = tagElem.getAttributes(); // Attribs
																	// already
																	// used

					String wordAtCursor = "";
					try {
						wordAtCursor = Utilities.getWord(doc, Utilities.getWordStart(doc, offset));
					} catch (BadLocationException e) {
					}

					l = new ArrayList();
					for (Iterator i = possible.iterator(); i.hasNext();) {
						DTD.Attribute attr = (DTD.Attribute) i.next();
						String aName = attr.getName();

						if (aName.equals(prefix) || !existing.contains(aName) || wordAtCursor.equals(aName))
							l.add(attr);
					}
					l = translateAttribs(offset - len, len, l);
				}

				/* Value finder */
				/*
				 * Suggestion - find special-meaning attributes ( IMG src, A
				 * href, color,.... - may be better resolved by attr type, may
				 * be moved to propertysheet
				 */
			}
			else if (id == HTMLTokenContext.VALUE || id == HTMLTokenContext.OPERATOR || id == HTMLTokenContext.WS
					&& (inside ? prev : prev.getPrevious()).getTokenID() == HTMLTokenContext.OPERATOR) {
				SyntaxElement elem = null;
				try {
					elem = sup.getElementChain(offset);
				} catch (BadLocationException e) {
					return null;
				}

				if (elem == null)
					return null;

				// between Tag and error - common state when entering OOTL, e.g.
				// <BDO dir=>
				if (elem.getType() == SyntaxElement.TYPE_ERROR) {
					elem = elem.getPrevious();
					if (elem == null)
						return null;
				}

				if (elem.getType() == SyntaxElement.TYPE_TAG) {
					SyntaxElement.Tag tagElem = (SyntaxElement.Tag) elem;

					String tagName = tagElem.getName().toUpperCase();
					DTD.Element tag = dtd.getElement(tagName);
					if (tag == null)
						return null; // unknown tag

					TokenItem argItem = prev;
					while (argItem != null && argItem.getTokenID() != HTMLTokenContext.ARGUMENT)
						argItem = argItem.getPrevious();
					if (argItem == null)
						return null; // no ArgItem
					String argName = argItem.getImage();

					DTD.Attribute arg = tag.getAttribute(argName);
					if (arg == null || arg.getType() != DTD.Attribute.TYPE_SET)
						return null;

					if (id != HTMLTokenContext.VALUE) {
						len = 0;
						l = translateValues(offset - len, len, arg.getValueList(""));
					}
					else {
						len = offset - itemOffset;
						l = translateValues(offset - len, len, arg.getValueList(preText));
					}
				}
			}

			// System.err.println("l = " + l );
			if (l == null)
				return null;
			else
				return new CompletionQuery.DefaultResult(component, "Results for DOCTYPE " + dtd.getIdentifier(), l, offset, len);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}

	List translateCharRefs(int offset, int length, List refs) {
		List result = new ArrayList(refs.size());
		for (Iterator i = refs.iterator(); i.hasNext();) {
			result.add(new CharRefItem(((DTD.CharRef) i.next()).getName(), offset, length));
		}
		return result;
	}

	List translateTags(int offset, int length, List tags) {
		List result = new ArrayList(tags.size());
		for (Iterator i = tags.iterator(); i.hasNext();) {
			result.add(new TagItem(((DTD.Element) i.next()).getName(), offset, length));
		}
		return result;
	}

	List translateAttribs(int offset, int length, List attribs) {
		List result = new ArrayList(attribs.size());
		for (Iterator i = attribs.iterator(); i.hasNext();) {
			DTD.Attribute attrib = (DTD.Attribute) i.next();
			String name = attrib.getName();
			switch (attrib.getType()) {
			case DTD.Attribute.TYPE_BOOLEAN:
				result.add(new BooleanAttribItem(name, offset, length, attrib.isRequired()));
				break;
			case DTD.Attribute.TYPE_SET:
				result.add(new SetAttribItem(name, offset, length, attrib.isRequired()));
				break;
			case DTD.Attribute.TYPE_BASE:
				result.add(new PlainAttribItem(name, offset, length, attrib.isRequired()));
				break;
			}
		}
		return result;
	}

	List translateValues(int offset, int length, List values) {
		if (values == null)
			return new ArrayList(0);
		List result = new ArrayList(values.size());
		for (Iterator i = values.iterator(); i.hasNext();) {
			result.add(new ValueItem(((DTD.Value) i.next()).getName(), offset, length));
		}
		return result;
	}

	// Implementation of ResultItems for completion
	/**
	 * The simple result item operating over an instance of the string, it is
	 * lightweight in the mean it doesn't allocate any new instances of anything
	 * and every data creates lazily on request to avoid creation of lot of
	 * string instances per completion result.
	 */
	private static abstract class HTMLResultItem implements CompletionQuery.ResultItem {
		/** The Component used as a rubberStamp for painting items */
		static javax.swing.JLabel rubberStamp = new javax.swing.JLabel();
		static {
			rubberStamp.setOpaque(true);
		}

		/** The String on which is this ResultItem defined */
		String baseText;
		/** the remove and insert point for this item */
		int offset;
		/** The length of the text to be removed */
		int length;

		public HTMLResultItem(String baseText, int offset, int length) {
			this.baseText = baseText;
			this.offset = offset;
			this.length = length;
		}

		boolean replaceText(JTextComponent component, String text) {
			BaseDocument doc = (BaseDocument) component.getDocument();
			doc.atomicLock();
			try {
				doc.remove(offset, length);
				doc.insertString(offset, text, null);
			} catch (BadLocationException exc) {
				return false; // not sucessfull
			} finally {
				doc.atomicUnlock();
			}
			return true;
		}

		public boolean substituteCommonText(JTextComponent c, int a, int b, int subLen) {
			return replaceText(c, getItemText().substring(0, subLen));
		}

		public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
			return replaceText(c, getItemText());
		}

		/** @return Properly colored JLabel with text gotten from <CODE>getPaintText()</CODE>. */
		public java.awt.Component getPaintComponent(javax.swing.JList list, boolean isSelected, boolean cellHasFocus) {
			// The space is prepended to avoid interpretation as HTML Label
			rubberStamp.setText(" " + getPaintText()); // NOI18N
			if (isSelected) {
				rubberStamp.setBackground(list.getSelectionBackground());
				rubberStamp.setForeground(list.getSelectionForeground());
			}
			else {
				rubberStamp.setBackground(list.getBackground());
				rubberStamp.setForeground(getPaintColor());
			}
			return rubberStamp;
		}

		/**
		 * The string used in painting by <CODE>getPaintComponent()</CODE>.
		 * It defaults to delegate to <CODE>getItemText()</CODE>.
		 * 
		 * @return The String to be painted in Completion View.
		 */
		String getPaintText() {
			return getItemText();
		}

		abstract Color getPaintColor();

		/**
		 * @return The String used for looking up the common part of multiple
		 *         items and for default way of replacing the text
		 */
		public String getItemText() {
			return baseText;
		}
	}

	static class EndTagItem extends HTMLResultItem {

		public EndTagItem(String baseText, int offset, int length) {
			super(baseText, offset, length);
		}

		Color getPaintColor() {
			return Color.blue;
		}

		public String getItemText() {
			return "</" + baseText + ">";
		} // NOI18N

		public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
			return super.substituteText(c, a, b, shift);
		}
	}

	private static class CharRefItem extends HTMLResultItem {

		public CharRefItem(String name, int offset, int length) {
			super(name, offset, length);
		}

		Color getPaintColor() {
			return Color.red.darker();
		}

		public String getItemText() {
			return "&" + baseText + ";";
		} // NOI18N
	}

	private static class TagItem extends HTMLResultItem {

		public TagItem(String name, int offset, int length) {
			super(name, offset, length);
		}

		public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
			replaceText(c, "<" + baseText + (shift ? " >" : ">")); // NOI18N
			if (shift) {
				Caret caret = c.getCaret();
				caret.setDot(caret.getDot() - 1);
			}
			return !shift; // flag == false;
		}

		Color getPaintColor() {
			return Color.blue;
		}

		public String getItemText() {
			return "<" + baseText + ">";
		} // NOI18N
	}

	private static class SetAttribItem extends HTMLResultItem {
		boolean required;

		public SetAttribItem(String name, int offset, int length, boolean required) {
			super(name, offset, length);
			this.required = required;
		}

		Color getPaintColor() {
			return required ? Color.red : Color.green.darker();
		}

		String getPaintText() {
			return baseText;
		}

		public String getItemText() {
			return baseText + "=";
		} // NOI18N

		public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
			super.substituteText(c, 0, 0, shift);
			return false; // always refresh
		}
	}

	private static class BooleanAttribItem extends HTMLResultItem {

		boolean required;

		public BooleanAttribItem(String name, int offset, int length, boolean required) {
			super(name, offset, length);
			this.required = required;
		}

		Color getPaintColor() {
			return required ? Color.red : Color.green.darker();
		}

		public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
			replaceText(c, shift ? baseText + " " : baseText);
			return false; // always refresh
		}
	}

	private static class PlainAttribItem extends HTMLResultItem {

		boolean required;

		public PlainAttribItem(String name, int offset, int length, boolean required) {
			super(name, offset, length);
			this.required = required;
		}

		Color getPaintColor() {
			return required ? Color.red : Color.green.darker();
		}

		public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
			replaceText(c, baseText + "=''"); // NOI18N
			if (shift) {
				Caret caret = c.getCaret();
				caret.setDot(caret.getDot() - 1);
			}
			return false; // always refresh
		}
	}

	private static class ValueItem extends HTMLResultItem {

		public ValueItem(String name, int offset, int length) {
			super(name, offset, length);
		}

		Color getPaintColor() {
			return Color.magenta;
		}

		public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
			replaceText(c, shift ? baseText + " " : baseText); // NOI18N
			return !shift;
		}
	}
}
