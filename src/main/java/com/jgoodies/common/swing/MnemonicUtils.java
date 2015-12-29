/*
 * Copyright (c) 2009-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.common.swing;

import static com.jgoodies.common.base.Preconditions.checkNotNull;
import static com.jgoodies.common.internal.Messages.MUST_NOT_BE_NULL;

import java.awt.event.KeyEvent;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JLabel;

import com.jgoodies.common.base.Strings;


/**
 * Configures the text, mnemonic and mnemonic index in Actions, JLabels,
 * and AbstractButtons. The term <em>marked text</em> is used for Strings
 * that contain a marker character that marks both the mnemonic and
 * its index in the resulting <em>plain text</em>.<p>
 *
 * To set a mnemonic, add an ampersand ('&amp;') before the character
 * that should be the mnemonic, for example "&amp;Save".
 * If your text has multiple ampersands, only the first one is used to mark
 * the mnemonic; the other ampersands appear as normal text.
 * For example "&amp;Look&amp;Feel" has the first 'L' has mnemonic.
 * If you want to use the ampersand itself in the text, use two consecutive
 * ampersands for any ampersand that precede the one that is used to mark
 * the mnemonic. For example the marked text "R&amp;&amp;D D&amp;epartment"
 * has "R&amp;D Department" as text and the mnemonic is 'E'.<p>
 *
 * Examples:
 * <table border="1">
 * <tr>
 *     <td><b>Marked Text</b></td>
 *     <td><b>Plain Text</b></td>
 *     <td><b>Mnemonic</b></td>
 *     <td><b>Mnemonic Index</b></td>
 *     <td><b>Comment</b></td>
 * </tr>
 * <tr>
 *     <td>Save</td>
 *     <td>Save</td>
 *     <td>0</td>
 *     <td>-1</td>
 *     <td>No mnemonic</td>
 * </tr>
 * <tr>
 *     <td>&amp;Save</td>
 *     <td>Save</td>
 *     <td>'S'</td>
 *     <td>0</td>
 *     <td>&nbsp;</td>
 * </tr>
 * <tr>
 *     <td>Save &amp;as</td>
 *     <td>Save as</td>
 *     <td>'A'</td>
 *     <td>5</td>
 *     <td>Second 'a' marked</td>
 * </tr>
 * <tr>
 *     <td>Look&amp;Feel</td>
 *     <td>LookFeel</td>
 *     <td>'F'</td>
 *     <td>4</td>
 *     <td>&amp; should be quoted</td>
 * </tr>
 * <tr>
 *     <td>Look&amp;&amp;Feel</td>
 *     <td>Look&amp;Feel</td>
 *     <td>0</td>
 *     <td>-1</td>
 *     <td>&amp; is quoted</td>
 * </tr>
 * <tr>
 *     <td>&amp;Look&amp;Feel</td>
 *     <td>Look&amp;Feel</td>
 *     <td>L</td>
 *     <td>0</td>
 *     <td>Second &amp; needs no quote</td>
 * </tr>
 * <tr>
 *     <td>Look &amp; Feel</td>
 *     <td>Look &amp; Feel</td>
 *     <td>0</td>
 *     <td>-1</td>
 *     <td>Whitespace cannot be marked</td>
 * </tr>
 * <tr>
 *     <td>R&amp;&amp;D D&amp;epartment</td>
 *     <td>R&amp;D Department</td>
 *     <td>'E'</td>
 *     <td>5</td>
 *     <td>First &amp; is quoted</td>
 * </tr>
 * <tr>
 *     <td>&lt;html&gt;a&b&lt;/html&gt;</td>
 *     <td>&lt;html&gt;a&lt;u&gt;b&lt;/u&gt;&lt;/html&gt;</td>
 *     <td>'B'</td>
 *     <td>-1</td>
 *     <td>'b' is underlined</td>
 * </tr>
 * <tr>
 *     <td>&lt;html&gt;R&amp;amp;D D&ep.&lt;/html&gt;</td>
 *     <td>&lt;html&gt;R&amp;amp;D D&lt;u&gt;e&lt;/u&gt;p.&lt;/html&gt;</td>
 *     <td>'B'</td>
 *     <td>-1</td>
 *     <td>HTML &amp;amp; doesn't mark</td>
 * </tr>
 * </table>
 *
 * @author Karsten Lentzsch
 */
public final class MnemonicUtils {

    /**
     * The single mnemonic marker. Future versions of this class shall
     * support other markers, for example the underline char ('_').
     */
    static final char MNEMONIC_MARKER   = '&';


    // Instance Creation ******************************************************

    private MnemonicUtils() {
        // Suppresses default constructor, prevents instantiation.
    }


    // Setting Text, Mnemonic and Index ***************************************

    /**
     * Configures the text, mnemonic and mnemonic index for {@code target}
     * using the given text that can be marked with the mnemonic marker '&amp'.
     * For example if {@code markedText} is &quot;Save &as&quot;, the text
     * will be set to &quot;Save as&quot;, the mnemonic is 'a', and the
     * mnemonic index is 5.
     *
     * @param target       the button to be configured
     * @param markedText   the text with optional mnemonic marker
     * @throws NullPointerException if {@code target} is {@code null}
     */
	public static void configure(AbstractButton target, String markedText) {
        checkNotNull(target, MUST_NOT_BE_NULL, "target");
	    configure0(target, new MnemonicText(markedText, MNEMONIC_MARKER));
	}


    /**
     * Configures the text, mnemonic and mnemonic index for {@code target}
     * using the given text that can be marked with the mnemonic marker '&amp'.
     * For example if {@code markedText} is &quot;Save &as&quot;, the text
     * will be set to &quot;Save as&quot;, the mnemonic is 'a', and the
     * mnemonic index is 5.
     *
     * @param target       the Action to be configured
     * @param markedText   the text with optional mnemonic marker
     * @throws NullPointerException if {@code target} is {@code null}
     */
    public static void configure(Action target, String markedText) {
        checkNotNull(target, MUST_NOT_BE_NULL, "target");
        configure0(target, new MnemonicText(markedText, MNEMONIC_MARKER));
    }


    /**
     * Configures the text, mnemonic and mnemonic index for {@code target}
     * using the given text that can be marked with the mnemonic marker '&amp'.
     * For example if {@code markedText} is &quot;Save &as&quot;, the text
     * will be set to &quot;Save as&quot;, the mnemonic is 'a', and the
     * mnemonic index is 5.
     *
     * @param target       the label to be configured
     * @param markedText   the text with optional mnemonic marker
     * @throws NullPointerException if {@code target} is {@code null}
     */
    public static void configure(JLabel target, String markedText) {
        checkNotNull(target, MUST_NOT_BE_NULL, "target");
        configure0(target, new MnemonicText(markedText, MNEMONIC_MARKER));
    }


    // Misc *******************************************************************

    /**
     * Returns the plain text for the given marked text by removing
     * the mnemonic marker and marker quotes - if any. If the marked text
     * is HTML, the plain text has the mnemonic character underlined.
     *
     * See the {@link MnemonicUtils} class comment for information
     * about how to mark a mnemonic and how to quote a marker.
     *
     * <pre>
     * MnemonicUtils.plainText("Save")             == "Save"
     * MnemonicUtils.plainText("&Save")            == "Save"
     * MnemonicUtils.plainText("&Look&Feel")       == "Look&Feel"
     * MnemonicUtils.plainText("Look & Feel")      == "Look & Feel"
     * MnemonicUtils.plainText("R&&D D&epartment") == "R&D Department"
     * MnemonicUtils.plainText("&lt;html&gt;a&b&lt;/html&gt;") == "&lt;html&gt;a&lt;u&gt;b&lt;/u&gt;&lt;/html&gt;"
     * </pre>
     *
     * @param markedText   the text that may contain a mnemonic marker
     * @return the text without mnemonic marker and marker quotes
     */
    public static String plainText(String markedText) {
        return new MnemonicText(markedText, MNEMONIC_MARKER).text;
    }


    // Testing API ************************************************************

    /**
     * Extracts and returns the mnemonic from the given marked text.
     *
     * @param markedText   the text with optional mnemonic marker
     * @return the mnemonic or {@code 0} if no mnemonic is marked
     */
    static int mnemonic(String markedText) {
        return new MnemonicText(markedText, MNEMONIC_MARKER).key;
    }


    /**
     * Finds and returns the index of the mnemonic in the marked text.
     *
     * @param markedText   the text with optional mnemonic marker
     * @return the mnemonic index or {@code -1} if there's no mnemonic
     *     or the text is HTML
     */
    static int mnemonicIndex(String markedText) {
        return new MnemonicText(markedText, MNEMONIC_MARKER).index;
    }


    // Implementation *********************************************************

    private static void configure0(AbstractButton button, MnemonicText mnemonicText) {
	    button.setText(mnemonicText.text);
        button.setMnemonic(mnemonicText.key);
        button.setDisplayedMnemonicIndex(mnemonicText.index);
    }


    /*
     * We set the mnemonic only if non-null. This works around a bug in
     * AbstractButton that can configure itself from an Action with mnemonic
     * set to null but throws an NPE if the Action's mnemonic becomes
     * null afterwards.
     *
     * @see AbstractButton.ButtonActionPropertyChangeListener
     */
   private static void configure0(Action action, MnemonicText mnemonicText) {
        Integer keyValue = Integer.valueOf(mnemonicText.key);
        Integer indexValue = mnemonicText.index == -1
            ? null
            : Integer.valueOf(mnemonicText.index);
        action.putValue(Action.NAME, mnemonicText.text);
        action.putValue(Action.MNEMONIC_KEY, keyValue);
        action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, indexValue);
    }

    private static void configure0(JLabel label, MnemonicText mnemonicText) {
        label.setText(mnemonicText.text);
        label.setDisplayedMnemonic(mnemonicText.key);
        label.setDisplayedMnemonicIndex(mnemonicText.index);
    }


	// Helper Code ************************************************************

    private static final class MnemonicText {

        String text;
        int key;
        int index;

        private MnemonicText(String markedText, char marker) {
            int i;
            if (   markedText == null
                || markedText.length() <= 1
                || (i = markedText.indexOf(marker)) == -1) {
                text = markedText;
                key = KeyEvent.VK_UNDEFINED;
                index = -1;
                return;
            }
            boolean html = Strings.startsWithIgnoreCase(markedText, "<html>");
            StringBuilder builder = new StringBuilder();
            int begin = 0;
            int quotedMarkers = 0;
            int markerIndex = -1;
            boolean marked = false;
            char markedChar = 0;
            CharacterIterator sci = new StringCharacterIterator(markedText);
            do {
                builder.append(markedText.substring(begin, i));
                char current = sci.setIndex(i);
                char next = sci.next();
                if (html) {
                    int entityEnd = indexOfEntityEnd(markedText, i);
                    if (entityEnd == -1) {
                        marked = true;
                        builder.append("<u>").append(next).append("</u>");
                        begin = i+2;
                        markedChar = next;
                    } else {
                        builder.append(markedText.substring(i, entityEnd));
                        begin = entityEnd;
                    }
                } else {
                    if (next == marker) {                       // "Look&&Feel"
                        builder.append(next);
                        begin = i+2;
                        quotedMarkers++;
                    } else if (Character.isWhitespace(next)) {  // "Look & Feel"
                        builder.append(current).append(next);
                        begin = i+2;
                    } else if (next == CharacterIterator.DONE) {
                        builder.append(current);
                        begin = i+2;
                    } else {
                        builder.append(next);
                        begin = i+2;
                        markerIndex = i - quotedMarkers;
                        marked = true;
                        markedChar = next;
                    }
                }
                i = markedText.indexOf(marker, begin);
            } while (i != -1 && !marked);
            if (begin < markedText.length()) {
                builder.append(markedText.substring(begin));
            }
            text = builder.toString();
            index = markerIndex;
            if (marked) {
                key = mnemonicKey(markedChar);
            } else {
                key = KeyEvent.VK_UNDEFINED;
            }
        }

        private static int indexOfEntityEnd(String htmlText, int start) {
            CharacterIterator sci = new StringCharacterIterator(htmlText, start);
            char c;
            do {
                c = sci.next();
                if (c == ';') {
                    return sci.getIndex();
                }
                if (!Character.isLetterOrDigit(c)) {
                    return -1;
                }
            } while (c != CharacterIterator.DONE);
            return -1;
        }

        /* A general purpose way to map from a char to a KeyCode is needed.  An
         * AWT RFE has been filed:
         * http://bt2ws.central.sun.com/CrPrint?id=6559449
         * CR 6559449 java/classes_awt Support for converting from char to KeyEvent VK_ keycode
         */
        private static int mnemonicKey(char c) {
            int vk = c;
            if (vk >= 'a' && vk <= 'z') {
                vk -= 'a' - 'A';
            }
            return vk;
        }
    }


}
