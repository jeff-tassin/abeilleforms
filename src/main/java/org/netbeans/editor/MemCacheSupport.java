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

/**
 * Manages in-memory character cache to store contents of the document.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

class MemCacheSupport extends DocCacheSupport {

	/** Increment for extending the cache */
	private static final int CACHE_INCREMENT = 2048;

	/** Memory cache array */
	char buffer[] = Analyzer.EMPTY_CHAR_ARRAY;

	/** Construct new memory cacheSupport with zero size */
	public MemCacheSupport() {
	}

	/**
	 * Construct new memory cacheSupport. Initialize the memory array.
	 * 
	 * @param ensureCapacity
	 *            how much space preallocate
	 */
	public MemCacheSupport(int ensureCapacity) {
		buffer = new char[ensureCapacity];
	}

	/**
	 * check if there's enough space in the cache
	 * 
	 * @param reqLen
	 *            how many chars has to be available
	 */
	private void checkSpace(int reqLen) {
		if (buffer.length < reqLen) {
			char tempCache[] = new char[reqLen + CACHE_INCREMENT + reqLen / 8];
			System.arraycopy(buffer, 0, tempCache, 0, getDocLength());
			buffer = tempCache;
		}
	}

	public void ensureCapacity(int capacity) {
		checkSpace(capacity);
	}

	public void read(int pos, char cache[], int offset, int len) {
		if (len == 0) {
			return; // immediate return for void operations
		}
		System.arraycopy(buffer, pos, cache, offset, len); // copy part of the
															// cache
	}

	public void write(int pos, char cache[], int offset, int len) {
		if (len == 0) {
			return; // immediate return for void operations
		}
		checkSpace(pos + len);
		System.arraycopy(cache, offset, buffer, pos, len);
		docLen = Math.max(docLen, pos + len);
	}

	public void insert(int pos, char cache[], int offset, int len) {
		if (len == 0) {
			return; // immediate return for void operations
		}
		checkSpace(getDocLength() + len);
		System.arraycopy(buffer, pos, buffer, pos + len, getDocLength() - pos);
		System.arraycopy(cache, offset, buffer, pos, len);
		docLen += len;
	}

	public void insertString(int pos, String text, int offset, int len) {
		if (len == 0) {
			return; // immediate return for void operations
		}
		checkSpace(getDocLength() + len);
		System.arraycopy(buffer, pos, buffer, pos + len, getDocLength() - pos);
		text.getChars(offset, offset + len, buffer, pos);
		docLen += len;
	}

	public void remove(int pos, int len) {
		if (len == 0) {
			return; // immediate return for void operations
		}
		System.arraycopy(buffer, pos + len, buffer, pos, getDocLength() - (pos + len));
		docLen -= len;
	}

	public boolean supportsDirectMode() {
		return true;
	}

	public char[] getDirectModeBuffer() {
		return buffer;
	}

}
