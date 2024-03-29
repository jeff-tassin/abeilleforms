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

package org.netbeans.editor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.swing.text.BadLocationException;

/**
 * Various text analyzes over the document
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class Analyzer {

	/** Platform default line separator */
	private static Object platformLS;

	/** Empty char array */
	public static final char[] EMPTY_CHAR_ARRAY = new char[0];

	/** Buffer filled by spaces used for spaces filling and tabs expansion */
	private static char spacesBuffer[] = new char[] { ' ' };

	/** Buffer filled by tabs used for tabs filling */
	private static char tabsBuffer[] = new char[] { '\t' };

	/** Cache up to 50 spaces strings */
	private static final int MAX_CACHED_SPACES_STRING_LENGTH = 50;

	/** Spaces strings cache. */
	private static final String[] spacesStrings = new String[MAX_CACHED_SPACES_STRING_LENGTH + 1];

	static {
		spacesStrings[0] = "";
		spacesStrings[MAX_CACHED_SPACES_STRING_LENGTH] = new String(getSpacesBuffer(MAX_CACHED_SPACES_STRING_LENGTH), 0, MAX_CACHED_SPACES_STRING_LENGTH);
	}

	private Analyzer() {
		// no instantiation
	}

	/** Get platform default line separator */
	public static Object getPlatformLS() {
		if (platformLS == null) {
			platformLS = System.getProperty("line.separator"); // NOI18N
		}
		return platformLS;
	}

	/**
	 * Test line separator on given semgment. This implementation simply checks
	 * the first line of file but it can be redefined to do more thorough test.
	 * 
	 * @param seg
	 *            segment where analyzes are performed
	 * @return line separator type found in the file
	 */
	public static String testLS(char chars[], int len) {
		for (int i = 0; i < len; i++) {
			switch (chars[i]) {
			case '\r':
				if (i + 1 < len && chars[i + 1] == '\n') {
					return BaseDocument.LS_CRLF;
				}
				else {
					return BaseDocument.LS_CR;
				}

			case '\n':
				return BaseDocument.LS_LF;
			}
		}
		return null; // signal unspecified line separator
	}

	/**
	 * Convert text with generic line separators to line feeds (LF). As the
	 * linefeeds are one char long there is no need to allocate another buffer
	 * since the only possibility is that the returned length will be smaller
	 * than previous (if there were some CRLF separators.
	 * 
	 * @param chars
	 *            char array with data to convert
	 * @param len
	 *            valid portion of chars array
	 * @return new valid portion of chars array after conversion
	 */
	public static int convertLSToLF(char chars[], int len) {
		int tgtOffset = 0;
		short lsLen = 0; // length of separator found
		int moveStart = 0; // start of block that must be moved
		int moveLen; // length of data moved back in buffer

		for (int i = 0; i < len; i++) {
			// first of all - there's no need to handle single '\n'
			if (chars[i] == '\r') { // '\r' found
				if (i + 1 < len && chars[i + 1] == '\n') { // '\n' follows
					lsLen = 2; // '\r\n'
				}
				else {
					lsLen = 1; // only '\r'
				}
			}

			if (lsLen > 0) {
				moveLen = i - moveStart;
				if (moveLen > 0) {
					if (tgtOffset != moveStart) { // will need to arraycopy
						System.arraycopy(chars, moveStart, chars, tgtOffset, moveLen);
					}
					tgtOffset += moveLen;
				}
				chars[tgtOffset++] = '\n';
				moveStart += moveLen + lsLen; // skip separator
				i += lsLen - 1; // possibly skip '\n'
				lsLen = 0; // signal no separator found
			}
		}

		// now move the rest if it's necessary
		moveLen = len - moveStart;
		if (moveLen > 0) {
			if (tgtOffset != moveStart) {
				System.arraycopy(chars, moveStart, chars, tgtOffset, moveLen);
			}
			tgtOffset += moveLen;
		}

		return tgtOffset; // return current length
	}

	/**
	 * Convert string with generic line separators to line feeds (LF).
	 * 
	 * @param text
	 *            string to convert
	 * @return new string with converted LSs to LFs
	 */
	public static String convertLSToLF(String text) {
		char[] tgtChars = null;
		int tgtOffset = 0;
		short lsLen = 0; // length of separator found
		int moveStart = 0; // start of block that must be moved
		int moveLen; // length of data moved back in buffer
		int textLen = text.length();

		for (int i = 0; i < textLen; i++) {
			// first of all - there's no need to handle single '\n'
			if (text.charAt(i) == '\r') { // '\r' found
				if (i + 1 < textLen && text.charAt(i + 1) == '\n') { // '\n'
																		// follows
					lsLen = 2; // '\r\n'
				}
				else {
					lsLen = 1; // only '\r'
				}
			}

			if (lsLen > 0) {
				if (tgtChars == null) {
					tgtChars = new char[textLen];
					text.getChars(0, textLen, tgtChars, 0); // copy whole array
				}
				moveLen = i - moveStart;
				if (moveLen > 0) {
					if (tgtOffset != moveStart) { // will need to arraycopy
						text.getChars(moveStart, moveStart + moveLen, tgtChars, tgtOffset);
					}
					tgtOffset += moveLen;
				}
				tgtChars[tgtOffset++] = '\n';
				moveStart += moveLen + lsLen; // skip separator
				i += lsLen - 1; // possibly skip '\n'
				lsLen = 0; // signal no separator found
			}
		}

		// now move the rest if it's necessary
		moveLen = textLen - moveStart;
		if (moveLen > 0) {
			if (tgtOffset != moveStart) {
				text.getChars(moveStart, moveStart + moveLen, tgtChars, tgtOffset);
			}
			tgtOffset += moveLen;
		}

		return (tgtChars == null) ? text : new String(tgtChars, 0, tgtOffset);
	}

	public static boolean isSpace(String s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			if (s.charAt(i) != ' ') {
				return false;
			}
		}
		return true;
	}

	/** Return true if the array contains only space chars */
	public static boolean isSpace(char[] chars, int offset, int len) {
		while (len > 0) {
			if (chars[offset++] != ' ') {
				return false;
			}
			len--;
		}
		return true;
	}

	/** Return true if the array contains only space or tab chars */
	public static boolean isWhitespace(char[] chars, int offset, int len) {
		while (len > 0) {
			if (!Character.isWhitespace(chars[offset])) {
				return false;
			}
			offset++;
			len--;
		}
		return true;
	}

	/** Return the first index that is not space */
	public static int findFirstNonTab(char[] chars, int offset, int len) {
		while (len > 0) {
			if (chars[offset] != '\t') {
				return offset;
			}
			offset++;
			len--;
		}
		return -1;
	}

	/** Return the first index that is not space */
	public static int findFirstNonSpace(char[] chars, int offset, int len) {
		while (len > 0) {
			if (chars[offset] != ' ') {
				return offset;
			}
			offset++;
			len--;
		}
		return -1;
	}

	/** Return the first index that is not space or tab or new-line char */
	public static int findFirstNonWhite(char[] chars, int offset, int len) {
		while (len > 0) {
			if (!Character.isWhitespace(chars[offset])) {
				return offset;
			}
			offset++;
			len--;
		}
		return -1;
	}

	/** Return the last index that is not space or tab or new-line char */
	public static int findLastNonWhite(char[] chars, int offset, int len) {
		int i = offset + len - 1;
		while (i >= offset) {
			if (!Character.isWhitespace(chars[i])) {
				return i;
			}
			i--;
		}
		return -1;
	}

	/**
	 * Count the number of line feeds in char array.
	 * 
	 * @return number of LF characters contained in array.
	 */
	public static int getLFCount(char chars[]) {
		return getLFCount(chars, 0, chars.length);
	}

	public static int getLFCount(char chars[], int offset, int len) {
		int lfCount = 0;
		while (len > 0) {
			if (chars[offset++] == '\n') {
				lfCount++;
			}
			len--;
		}
		return lfCount;
	}

	public static int getLFCount(String s) {
		int lfCount = 0;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			if (s.charAt(i) == '\n') {
				lfCount++;
			}
		}
		return lfCount;
	}

	public static int findFirstLFOffset(char[] chars, int offset, int len) {
		while (len > 0) {
			if (chars[offset++] == '\n') {
				return offset - 1;
			}
			len--;
		}
		return -1;
	}

	public static int findFirstLFOffset(String s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			if (s.charAt(i) == '\n') {
				return i;
			}
		}
		return -1;
	}

	public static int findFirstTab(char[] chars, int offset, int len) {
		while (len > 0) {
			if (chars[offset++] == '\t') {
				return offset - 1;
			}
			len--;
		}
		return -1;
	}

	public static int findFirstTabOrLF(char[] chars, int offset, int len) {
		while (len > 0) {
			switch (chars[offset++]) {
			case '\t':
			case '\n':
				return offset - 1;
			}
			len--;
		}
		return -1;
	}

	/**
	 * Reverses the order of characters in the array. It works from the begining
	 * of the array, so no offset is given.
	 */
	public static void reverse(char[] chars, int len) {
		for (int i = ((--len - 1) >> 1); i >= 0; --i) {
			char ch = chars[i];
			chars[i] = chars[len - i];
			chars[len - i] = ch;
		}
	}

	public static boolean equals(String s, char[] chars) {
		return equals(s, chars, 0, chars.length);
	}

	public static boolean equals(String s, char[] chars, int offset, int len) {
		if (s.length() != len) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			if (s.charAt(i) != chars[offset + i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Do initial reading of document. Translate any line separators found in
	 * document to line separators used by document. It also cares for elements
	 * that were already created on the empty document. Although the document
	 * must be empty there can be already marks created. Initial read is
	 * equivalent to inserting the string array of the whole document size at
	 * position 0 in the document. Therefore all the marks that are not
	 * insertAfter are removed and reinserted to the end of the document after
	 * the whole initial read is finished.
	 * 
	 * @param doc
	 *            document for which the initialization is performed
	 * @param reader
	 *            reader from which document should be read
	 * @param lsType
	 *            line separator type
	 * @param testLS
	 *            test line separator of file and if it's consistent, use it
	 * @param markDistance
	 *            the distance between the new syntax mark is put
	 */
	public static void initialRead(BaseDocument doc, Reader reader, boolean testLS) throws IOException {
		// document MUST be empty
		if (doc.getLength() > 0) {
			return;
		}

		// for valid reader read the document
		if (reader != null) {
			// Size of the read buffer
			int readBufferSize = ((Integer) doc.getProperty(SettingsNames.READ_BUFFER_SIZE)).intValue();
			// Default distance between marks
			int markDistance = ((Integer) doc.getProperty(SettingsNames.READ_MARK_DISTANCE)).intValue();

			/* buffer into which the data from file will be read */
			char readBuf[] = new char[readBufferSize];
			boolean firstRead = true; // first cycle of reading from stream
			boolean lastCR = false; // Last character in the previous buffer was
									// '\r'
			int readLen = 0; // How many chars was read within cycle
			int bufLen = 0; // Length of readBuf[] used area
			int bufStartPos = 0; // Starting position of the buffer in
									// document
			int lineOffset = 0; // Line counter
			int lineLimit = 0; // Longest line found
			int lineStartOffset = 0; // Start offset of the last line
			int nextMarkOffset = markDistance; // Buffer offset of the next
												// mark insertion
			int nextMarkPos = markDistance; // Position where the next mark
											// should be inserted
			int markCount = 0; // Total mark count

			synchronized (doc.op) {
				// array for getting mark array from renderer inner class
				final Mark origMarks[][] = new Mark[1][];
				doc.op.renderMarks(new DocMarks.Renderer() {
					public void render() {
						origMarks[0] = copyAllMarks();
					}
				});

				// now remove all the marks that are not insert after
				for (int i = 0; i < origMarks[0].length; i++) {
					Mark mark = origMarks[0][i];
					if (!(mark.getInsertAfter() || (mark instanceof MarkFactory.CaretMark))) {
						try {
							mark.remove();
						} catch (InvalidMarkException e) {
							if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
								e.printStackTrace();
							}
						}
					}
				}

				// Enter the loop where all data from file will be read
				while (true) {

					// Read part of document into buffer
					readLen = 0;
					while (readLen == 0) { // read non-zero chars for algorithm
											// to work
						readLen = reader.read(readBuf, 0, readBuf.length);
					}

					// Determine starting offset
					int moveStart = 0;
					if (lastCR) {
						if (readLen > 0 && readBuf[0] == '\n') {
							moveStart++; // force to go past the initial '\n'
						}
					}

					// check readLen value
					if (readLen == -1) { // no more characters
						break;
					}

					// check if we need to scan buffer for LS
					if (firstRead && testLS) {
						String newLS = testLS(readBuf, readLen);
						if (newLS != null) {
							doc.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, newLS);
							// if
							// (doc.getProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP)
							// == null) {
							// doc.putProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP,
							// newLS);
							// }
							// The property above is left empty so the write()
							// will default to the READ_LINE_SEPARATOR_PROP
						}
					}

					// now handle whole buffer - do syntax analyzes and create
					// marks
					int tgtOffset = 0;
					int offset;
					// Cycle through all chars in the buffer
					for (offset = 0; offset < readLen; offset++) {

						// Check whether the mark should be inserted
						if (offset == nextMarkOffset) {
							MarkFactory.SyntaxMark mark = new MarkFactory.SyntaxMark();
							try {
								doc.op.insertMark(mark, nextMarkPos, lineOffset);
							} catch (BadLocationException e) {
								if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
									e.printStackTrace();
								}
							} catch (InvalidMarkException e) {
								if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
									e.printStackTrace();
								}
							}

							markCount++;
							nextMarkOffset += markDistance;
							nextMarkPos += markDistance;
						}

						switch (readBuf[offset]) {
						case '\n':
							if (lastCR) { // ignore this LF
								if (moveStart > 0) { // move the last line
									int moveLen = offset - moveStart;
									if (moveLen > 0) { // handles initial '\n'
														// when this is -1
										System.arraycopy(readBuf, moveStart, readBuf, tgtOffset, moveLen);
										tgtOffset += moveLen;
									}

								}
								else { // chars yet start from the begining of
										// buffer
									tgtOffset = offset;
								}
								moveStart = offset + 1;
								lastCR = false;
								lineStartOffset++;
								nextMarkOffset++; // correct the removal of
													// the '\n'

							}
							else { // no CR before this LF
								lineOffset++;
								lineLimit = Math.max(lineLimit, offset - lineStartOffset);
								lineStartOffset = offset + 1;
							}

							break;

						case '\r':
							lastCR = true;
							readBuf[offset] = '\n';
							lineOffset++;
							lineLimit = Math.max(lineLimit, offset - lineStartOffset);
							lineStartOffset = offset + 1;
							break;
						}

					}

					if (moveStart > 0) {
						int moveLen = offset - moveStart;
						if (moveLen > 0) {
							System.arraycopy(readBuf, moveStart, readBuf, tgtOffset, moveLen);
							tgtOffset += moveLen;
						}
					}
					else {
						tgtOffset = offset; // no movings yet
					}

					nextMarkOffset -= offset;
					lineStartOffset -= offset;

					// store this buffer into cacheSupport
					try {
						doc.op.directCacheWrite(bufStartPos, readBuf, 0, tgtOffset);
					} catch (BadLocationException e) {
						if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
							e.printStackTrace();
						}
					}

					// initialize cache with first buffer
					if (firstRead) {
						doc.op.initCacheContent(readBuf, 0, tgtOffset);
					}

					bufStartPos += tgtOffset;
					firstRead = false;
				}

				// Now reinsert marks that were removed at begining to the end
				for (int i = 0; i < origMarks[0].length; i++) {
					Mark mark = origMarks[0][i];
					if (!(mark.getInsertAfter() || (mark instanceof MarkFactory.CaretMark))) {
						try {
							doc.op.insertMark(origMarks[0][i], bufStartPos, lineOffset);
						} catch (InvalidMarkException e) {
							if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
								e.printStackTrace();
							}
						} catch (BadLocationException e) {
							if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
								e.printStackTrace();
							}
						}
					}
				}

				// Set the line limit document property
				doc.putProperty(BaseDocument.LINE_LIMIT_PROP, lineLimit);

				// Mark the inserted area is not yet covered by syntax
				// state-infos
				doc.op.initialReadUpdate(); // refresh the document stats
			}
		}
	}

	/** Read from some reader and insert into document */
	static void read(BaseDocument doc, Reader reader, int pos) throws BadLocationException, IOException {
		int lastCR = 0;
		int thisCR;
		boolean lastRead = false;
		int readLen;
		int readBufferSize = ((Integer) doc.getProperty(SettingsNames.READ_BUFFER_SIZE)).intValue();
		char[] readBuf = new char[readBufferSize + 1];
		while (true) {
			// read part of document into buffer
			readLen = 0;
			while (readLen == 0) { // read non-zero chars for algorithm to work
				readLen = reader.read(readBuf, lastCR, readBufferSize);
			}
			thisCR = 0;
			if (readLen == -1) {
				if (lastCR == 0) {
					break;
				}
				else {
					readLen = 0;
				}
			}
			else { // some chars were read
				if (readBuf[readLen + lastCR - 1] == '\r') {
					thisCR = 1;
					readLen--;
				}
			}
			readLen += lastCR;
			readLen = convertLSToLF(readBuf, readLen);
			doc.insertString(pos, new String(readBuf, 0, readLen), null);
			pos += readLen;
			lastCR = thisCR;
		}
	}

	/** Write from document to some writer */
	static void write(BaseDocument doc, Writer writer, int pos, int len) throws BadLocationException, IOException {
		String lsType = (String) doc.getProperty(BaseDocument.WRITE_LINE_SEPARATOR_PROP);
		if (lsType == null) {
			lsType = (String) doc.getProperty(BaseDocument.READ_LINE_SEPARATOR_PROP);
			if (lsType == null) {
				lsType = BaseDocument.LS_LF;
			}
		}
		int writeBufferSize = ((Integer) doc.getProperty(SettingsNames.WRITE_BUFFER_SIZE)).intValue();
		char[] getBuf = new char[writeBufferSize];
		char[] writeBuf = new char[2 * writeBufferSize];
		int actLen = 0;

		while (len > 0) {
			actLen = Math.min(len, writeBufferSize);
			doc.getChars(pos, getBuf, 0, actLen);
			int tgtLen = convertLFToLS(getBuf, actLen, writeBuf, lsType);
			writer.write(writeBuf, 0, tgtLen);
			pos += actLen;
			len -= actLen;
		}

		// Append new-line if not the last char
		/*
		 * if (actLen > 0 && getBuf[actLen - 1] != '\n') { writer.write(new
		 * char[] { '\n' }, 0, 1); }
		 */

	}

	/** Get visual column. */
	public static int getColumn(char buffer[], int offset, int len, int tabSize, int startCol) {
		int col = startCol;
		int endOffset = offset + len;

		// Check wrong tab values
		if (tabSize <= 0) {
			new Exception("Wrong tab size=" + tabSize).printStackTrace();
			tabSize = 8;
		}

		while (offset < endOffset) {
			switch (buffer[offset++]) {
			case '\t':
				col = (col + tabSize) / tabSize * tabSize;
				break;
			default:
				col++;
			}
		}
		return col;
	}

	/**
	 * Get buffer filled with appropriate number of spaces. The buffer can have
	 * actually more spaces than requested.
	 * 
	 * @param numSpaces
	 *            number of spaces
	 */
	public static synchronized char[] getSpacesBuffer(int numSpaces) {
		// check if there's enough space in white space array
		while (numSpaces > spacesBuffer.length) {
			char tmpBuf[] = new char[spacesBuffer.length * 2]; // new buffer
			System.arraycopy(spacesBuffer, 0, tmpBuf, 0, spacesBuffer.length);
			System.arraycopy(spacesBuffer, 0, tmpBuf, spacesBuffer.length, spacesBuffer.length);
			spacesBuffer = tmpBuf;
		}

		return spacesBuffer;
	}

	/**
	 * Get string filled with space characters. There is optimization to return
	 * the same string instance for up to ceratin number of spaces.
	 * 
	 * @param numSpaces
	 *            number of spaces determining the resulting size of the string.
	 */
	public static synchronized String getSpacesString(int numSpaces) {
		if (numSpaces <= MAX_CACHED_SPACES_STRING_LENGTH) { // Cached
			String ret = spacesStrings[numSpaces];
			if (ret == null) {
				ret = spacesStrings[MAX_CACHED_SPACES_STRING_LENGTH].substring(0, numSpaces);
				spacesStrings[numSpaces] = ret;
			}

			return ret;

		}
		else { // non-cached
			return new String(getSpacesBuffer(numSpaces), 0, numSpaces);
		}
	}

	/**
	 * Get buffer of the requested size filled entirely with space character.
	 * 
	 * @param numSpaces
	 *            number of spaces in the returned character buffer.
	 */
	public static char[] createSpacesBuffer(int numSpaces) {
		char[] ret = new char[numSpaces];
		System.arraycopy(getSpacesBuffer(numSpaces), 0, ret, 0, numSpaces);
		return ret;
	}

	/**
	 * Get buffer filled with appropriate number of tabs. The buffer can have
	 * actually more tabs than requested.
	 * 
	 * @param numSpaces
	 *            number of spaces
	 */
	public static char[] getTabsBuffer(int numTabs) {
		// check if there's enough space in white space array
		if (numTabs > tabsBuffer.length) {
			char tmpBuf[] = new char[numTabs * 2]; // new buffer

			// initialize new buffer with spaces
			for (int i = 0; i < tmpBuf.length; i += tabsBuffer.length) {
				System.arraycopy(tabsBuffer, 0, tmpBuf, i, Math.min(tabsBuffer.length, tmpBuf.length - i));
			}
			tabsBuffer = tmpBuf;
		}

		return tabsBuffer;
	}

	/**
	 * Get the string that should be used for indentation of the given level.
	 * 
	 * @param indent
	 *            indentation level
	 * @param expandTabs
	 *            whether tabs should be expanded to spaces or not
	 * @param tabSize
	 *            size substituted visually for the '\t' character
	 */
	public static String getIndentString(int indent, boolean expandTabs, int tabSize) {
		return getWhitespaceString(0, indent, expandTabs, tabSize);
	}

	/**
	 * Get the string that should be used for indentation of the given level.
	 * 
	 * @param indent
	 *            indentation level
	 * @param expandTabs
	 *            whether tabs should be expanded to spaces or not
	 * @param tabSize
	 *            size of the '\t' character
	 */
	public static String getWhitespaceString(int startCol, int endCol, boolean expandTabs, int tabSize) {
		return (expandTabs || tabSize <= 0) ? getSpacesString(endCol - startCol) : new String(createWhiteSpaceFillBuffer(startCol, endCol, tabSize));
	}

	/**
	 * createWhitespaceFillBuffer() with the non-capital 's' should be used.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public static char[] createWhiteSpaceFillBuffer(int startCol, int endCol, int tabSize) {
		return createWhitespaceFillBuffer(startCol, endCol, tabSize);
	}

	/**
	 * Get buffer filled with spaces/tabs so that it reaches from some column to
	 * some other column.
	 * 
	 * @param startCol
	 *            starting visual column of the whitespace on the line
	 * @param endCol
	 *            ending visual column of the whitespace on the line
	 * @param tabSize
	 *            size substituted visually for the '\t' character
	 */
	public static char[] createWhitespaceFillBuffer(int startCol, int endCol, int tabSize) {
		if (startCol >= endCol) {
			return EMPTY_CHAR_ARRAY;
		}

		// Check wrong tab values
		if (tabSize <= 0) {
			new Exception("Wrong tab size=" + tabSize).printStackTrace();
			tabSize = 8;
		}

		int tabs = 0;
		int spaces = 0;
		int nextTab = (startCol + tabSize) / tabSize * tabSize;
		if (nextTab > endCol) { // only spaces
			spaces += endCol - startCol;
		}
		else { // at least one tab
			tabs++; // jump to first tab
			int endSpaces = endCol - endCol / tabSize * tabSize;
			tabs += (endCol - endSpaces - nextTab) / tabSize;
			spaces += endSpaces;
		}

		char[] ret = new char[tabs + spaces];
		if (tabs > 0) {
			System.arraycopy(getTabsBuffer(tabs), 0, ret, 0, tabs);
		}
		if (spaces > 0) {
			System.arraycopy(getSpacesBuffer(spaces), 0, ret, tabs, spaces);
		}
		return ret;
	}

	/**
	 * Loads the file and performs conversion of line separators to LF. This
	 * method can be used in debuging of syntax scanner or somewhere else.
	 * 
	 * @param fileName
	 *            the name of the file to load
	 * @return array of loaded characters with '\n' as line separator
	 */
	public static char[] loadFile(String fileName) throws IOException {
		File file = new File(fileName);
		char chars[] = new char[(int) file.length()];
		FileReader reader = new FileReader(file);
		reader.read(chars);
		reader.close();
		int len = Analyzer.convertLSToLF(chars, chars.length);
		if (len != chars.length) {
			char copyChars[] = new char[len];
			System.arraycopy(chars, 0, copyChars, 0, len);
			chars = copyChars;
		}
		return chars;
	}

	/**
	 * Convert text with LF line separators to text that uses line separators of
	 * the document. This function is used when saving text into the file.
	 * Segment's data are converted inside the segment's data or new segment's
	 * data array is allocated. NOTE: Source segment must have just LFs as
	 * separators! Otherwise the conversion won't work correctly.
	 * 
	 * @param src
	 *            source chars to convert from
	 * @param len
	 *            length of valid part of src data
	 * @param tgt
	 *            target chars to convert to. The array MUST have twice the size
	 *            of src otherwise index exception can be thrown
	 * @param lsType
	 *            line separator type to be used i.e. LS_LF, LS_CR, LS_CRLF
	 * @return length of valid chars in tgt array
	 */
	public static int convertLFToLS(char[] src, int len, char[] tgt, String lsType) {
		if (lsType.equals(BaseDocument.LS_CR)) { // CR instead of LF
			System.arraycopy(src, 0, tgt, 0, len);

			// now do conversion for LS_CR
			if (lsType == BaseDocument.LS_CR) { // will convert '\n' to '\r'
				char chars[] = tgt;
				for (int i = 0; i < len; i++) {
					if (chars[i] == '\n') {
						chars[i] = '\r';
					}
				}
			}
			return len;
		}
		else if (lsType.equals(BaseDocument.LS_CRLF)) {
			int tgtLen = 0;
			int moveStart = 0; // start of block that must be moved
			int moveLen; // length of chars moved

			for (int i = 0; i < len; i++) {
				if (src[i] == '\n') { // '\n' found
					moveLen = i - moveStart;
					if (moveLen > 0) { // will need to arraycopy
						System.arraycopy(src, moveStart, tgt, tgtLen, moveLen);
						tgtLen += moveLen;
					}
					tgt[tgtLen++] = '\r';
					tgt[tgtLen++] = '\n';
					moveStart = i + 1; // skip separator
				}
			}

			// now move the rest if it's necessary
			moveLen = len - moveStart;
			if (moveLen > 0) {
				System.arraycopy(src, moveStart, tgt, tgtLen, moveLen);
				tgtLen += moveLen;
			}
			return tgtLen;
		}
		else { // Using either \n or line separator is unknown
			System.arraycopy(src, 0, tgt, 0, len);
			return len;
		}
	}

	public static boolean startsWith(char[] chars, char[] prefix) {
		if (chars == null || chars.length < prefix.length) {
			return false;
		}
		for (int i = 0; i < prefix.length; i++) {
			if (chars[i] != prefix[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean endsWith(char[] chars, char[] suffix) {
		if (chars == null || chars.length < suffix.length) {
			return false;
		}
		for (int i = chars.length - suffix.length; i < chars.length; i++) {
			if (chars[i] != suffix[i]) {
				return false;
			}
		}
		return true;
	}

	public static char[] concat(char[] chars1, char[] chars2) {
		if (chars1 == null || chars1.length == 0) {
			return chars2;
		}
		if (chars2 == null || chars2.length == 0) {
			return chars1;
		}
		char[] ret = new char[chars1.length + chars2.length];
		System.arraycopy(chars1, 0, ret, 0, chars1.length);
		System.arraycopy(chars2, 0, ret, chars1.length, chars2.length);
		return ret;
	}

	public static char[] extract(char[] chars, int offset, int len) {
		char[] ret = new char[len];
		System.arraycopy(chars, offset, ret, 0, len);
		return ret;
	}

	public static boolean blocksHit(int[] blocks, int startPos, int endPos) {
		return (blocksIndex(blocks, startPos, endPos) >= 0);
	}

	public static int blocksIndex(int[] blocks, int startPos, int endPos) {
		if (blocks.length > 0) {
			int onlyEven = ~1;
			int low = 0;
			int high = blocks.length - 2;

			while (low <= high) {
				int mid = ((low + high) / 2) & onlyEven;

				if (blocks[mid + 1] <= startPos) {
					low = mid + 2;
				}
				else if (blocks[mid] >= endPos) {
					high = mid - 2;
				}
				else {
					return low; // found
				}
			}
		}

		return -1;
	}

	/**
	 * Remove all spaces from the given string.
	 * 
	 * @param s
	 *            original string
	 * @return string with all spaces removed
	 */
	public static String removeSpaces(String s) {
		int spcInd = s.indexOf(' ');
		if (spcInd >= 0) {
			StringBuffer sb = new StringBuffer(s.substring(0, spcInd));
			int sLen = s.length();
			for (int i = spcInd + 1; i < sLen; i++) {
				char ch = s.charAt(i);
				if (ch != ' ') {
					sb.append(ch);
				}
			}
			return sb.toString();
		}
		return s;
	}

}
