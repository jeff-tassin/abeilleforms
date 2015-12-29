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

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.BadLocationException;

/**
 * DocCacheSupport is a base class for caching document which is got by some
 * reader and enable its editing.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

abstract class DocCacheSupport {

	private static final int SAVE_BUFFER_LEN = 16384;

	/** Current document length in characters */
	protected int docLen;

	/** Count of characters read */
	long statCharsRead;
	/** Count of characters written */
	long statCharsWritten;

	/**
	 * Reading from the temporary storage into destination buffer.
	 * 
	 * @param pos
	 *            position in temp storage
	 * @param buffer
	 *            target storage
	 * @param offset
	 *            offset in buffer
	 * @param len
	 *            length of data in buffer to write
	 */
	public abstract void read(int pos, char buffer[], int offset, int len) throws BadLocationException;

	/**
	 * Write buffer at the specified position. Rewrites part of the document. It
	 * can write beyond the end of the document, however the initial position
	 * must be inside or at the end of the document.
	 * 
	 * @param pos
	 *            position in temp storage
	 * @param buffer
	 *            buffer with source data
	 * @param offset
	 *            offset of data in buffer
	 * @param len
	 *            length of data in buffer
	 */
	public abstract void write(int pos, char buffer[], int offset, int len) throws BadLocationException;

	/**
	 * Insert buffer into the document at specified position.
	 * 
	 * @param pos
	 *            insertion position in temp storage document
	 * @param buffer
	 *            source buffer with insertion data
	 * @param offset
	 *            offset of data in the buffer
	 * @param len
	 *            length of data in buffer
	 */
	public abstract void insert(int pos, char buffer[], int offset, int len) throws BadLocationException;

	/**
	 * Insert string into the document at specified position.
	 * 
	 * @param pos
	 *            insertion position in temp storage document
	 * @param text
	 *            text to insert
	 * @param offset
	 *            offset of data in the buffer
	 * @param len
	 *            length of data in buffer
	 */
	public abstract void insertString(int pos, String text, int offset, int len) throws BadLocationException;

	/**
	 * Delete a specified count of chars at specified position.
	 * 
	 * @param pos
	 *            position of removal in temp storage
	 * @param len
	 *            length of data to remove
	 */
	public abstract void remove(int pos, int len) throws BadLocationException;

	/**
	 * Helps to do less allocations if the initial file size is known. By
	 * default it's not implemented.
	 * 
	 * @param capacity
	 *            space that will be preallocated
	 */
	public void ensureCapacity(int capacity) {
	}

	/**
	 * Saving the tepmorary document to a writer.
	 * 
	 * @param writer
	 *            writer where to write data from temp storage
	 */
	public void save(Writer writer) throws IOException {
		char saveBuffer[] = new char[SAVE_BUFFER_LEN];
		int actPos = 0, len;
		while (actPos < docLen) {
			len = Math.min(docLen - actPos, SAVE_BUFFER_LEN);
			try {
				read(actPos, saveBuffer, 0, len);
			} catch (BadLocationException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			}
			writer.write(saveBuffer, 0, len);
			actPos += len;
		}
		writer.flush(); // flush the stream at the end
	}

	/**
	 * Destroy the CacheSupport (delete temp file etc.)
	 */
	public void destroy() {
	}

	/** Getter: Document length */
	public final int getDocLength() {
		return docLen;
	}

	public boolean supportsDirectMode() {
		return false;
	}

	public char[] getDirectModeBuffer() {
		return null;
	}

}
