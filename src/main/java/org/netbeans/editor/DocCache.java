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

import javax.swing.text.BadLocationException;

/**
 * Document cache The cache is used to perform insert/remove/read/find
 * operations over the document. The document data are partly stored in cache
 * fragments and partly in cache support. Cache can contain several
 * non-overlapping fragments. At all times there is one fragment called default
 * fragment. It's used for all operations of callers that don't pass valid
 * fragment information to the insert/remove and other methods.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

class DocCache {

	/**
	 * Old contents of the fragment will be reused if backward overlapping is at
	 * least this len
	 */
	private static final int MIN_FRAGMENT_BACK_OVERLAP_LEN = 256;

	/**
	 * CacheSupport that this cache uses. Cache support holds the whole
	 * document. There are two obvious storages - char array held in memory and
	 * file based storage. When there are changes made to the cache, they are
	 * held in fragments of the cache until they become so big that they must be
	 * flushed to the support.
	 */
	private DocCacheSupport support;

	/** Array of all fragments that this cache uses */
	private Fragment[] frags;

	/**
	 * Default fragment. It is used when null is passed as segment to
	 * insert/remove and other operations. It's also used when docLen is 0 to be
	 * the only one fragment available for insertion.
	 */
	private Fragment defFrag;

	/**
	 * Direct mode allowing to forward all operations directly to cache support.
	 * This is useful when support is memory based. No cache fragments are
	 * created for direct mode to save memory.
	 */
	private boolean directMode;

	/**
	 * Document length difference of all the fragments against the length of
	 * document that is given by support document len. Formula: support document
	 * len + docLenDelta = total document len
	 */
	private int docLenDelta = 0;

	/* Statistics */
	public int statRead = 0;
	public int statInsert = 0;
	public int statRemove = 0;
	public int statReadFragCnt = 0;
	public int statWriteFragCnt = 0;
	public int statOverlapCnt = 0;
	public int statBackOverlapCnt = 0;
	public int statFragSwitchCnt = 0;
	public int statFragSetEmpty = 0;

	/**
	 * Create the new cache with the specified size of default fragment.
	 * 
	 * @param support
	 *            CacheSupport to use for the document
	 * @param len
	 *            Length of the default fragment
	 * @param directMode
	 *            whether all operations should be routed to support
	 */
	public DocCache(DocCacheSupport support, int len, boolean directMode) {
		this.support = support;
		if (directMode && !support.supportsDirectMode()) {
			directMode = false;
		}
		this.directMode = directMode;
		if (!directMode) {
			defFrag = addFragment(len);
		}
	}

	/**
	 * Set initial content of the cache. This function may be called only once,
	 * after construction, before the data in cache are read or manipulated.
	 * Otherwise the cache content will be damaged. The reason for using this
	 * function is that content that's read from reader and that should be put
	 * into support can be also put directly into cache which saves whole
	 * support read.
	 * 
	 * @param initCache
	 *            initial cache data
	 * @param offset
	 *            first valid offset in initial cache
	 * @param cacheLen
	 *            length of data initial cache
	 */
	void initCacheContent(char initCache[], int offset, int cacheLen) {
		if (directMode) {
			return;
		}
		if (initCache != null) {
			defFrag.fragLen = Math.min(defFrag.buffer.length, cacheLen - offset);
			defFrag.origLen = defFrag.fragLen;
			System.arraycopy(initCache, offset, defFrag.buffer, 0, defFrag.fragLen);
			defFrag.startPos = 0;
		}
	}

	public synchronized Fragment addFragment(int fragLen) {
		if (directMode) {
			return null;
		}
		Fragment f = new Fragment(fragLen);
		if (frags != null) {
			Fragment[] tmpFrags = new Fragment[frags.length + 1];
			System.arraycopy(frags, 0, tmpFrags, 0, frags.length);
			tmpFrags[frags.length] = f;
			frags = tmpFrags;
		}
		else {
			frags = new Fragment[1];
			frags[0] = f;
		}
		return f;
	}

	/** Flush the whole cache to support */
	public synchronized void flush() {
		if (directMode) {
			return;
		}
		for (int i = 0; i < frags.length; i++) {
			if (frags[i].valid) {
				frags[i].write();
			}
		}
	}

	/**
	 * Read fragment and ensure that specified position will be inside (or at
	 * the end) of the fragment's cache.
	 * 
	 * @param frag
	 *            fragment to read
	 * @param pos
	 *            position that must be inside the fragment
	 */
	private void readFrag(Fragment frag, int pos) {
		int len; // len of chars to be read
		Fragment f;

		if (frag.modified) {
			frag.write(); // flush this fragment if modified
		}

		int mantLow = 0, mantHigh = getDocLenImpl(); // mantinels
		int csDelta = 0;
		int i;
		for (i = 0; i < frags.length; i++) {
			f = frags[i];
			if (f.valid && f != frag) {
				if (f.startPos > pos) {
					mantHigh = f.startPos;
					break;
				}
				else {
					csDelta += f.origLen - f.fragLen;
					mantLow = f.startPos + f.fragLen;
				}
			}
		}

		// count position from which should be read
		pos = Math.max(pos - frag.buffer.length / 2, mantLow);
		len = Math.min(mantHigh - pos, frag.buffer.length);
		frag.read(pos, len, csDelta);

		// repair the fragment's position in frags
		if (i == frags.length || (frags[i] != frag && (i == 0 || frags[i - 1] != frag))) {
			statFragSwitchCnt++;
			int fragInd;
			for (fragInd = 0; fragInd < frags.length; fragInd++) {
				if (frags[fragInd] == frag) {
					break;
				}
			}
			if (fragInd < i) { // frag before i
				for (int j = fragInd + 1; j < i; j++) {
					frags[j - 1] = frags[j];
				}
				frags[i - 1] = frag;
			}
			else { // frag after i
				for (int j = fragInd; j > i; j--) {
					frags[j] = frags[j - 1];
				}
				frags[i] = frag;
			}
		}
		frag.valid = true;
	}

	/**
	 * Get the correct fragment for a given position. Search all the fragments
	 * if there's one that contains given position. If not get or create
	 * optional fragment and read into it.
	 * 
	 * @param pos
	 *            position that the fragment should contain
	 * @param frag
	 *            current fragment
	 * @param wantInsert
	 *            want to perform insert on got fragment
	 * @return fragment that will contain position
	 */
	private Fragment getFrag(int pos, Fragment frag, boolean wantInsert) {
		Fragment f;
		for (int i = 0; i < frags.length; i++) {
			f = frags[i];
			if (f.valid) {
				if (wantInsert) {
					if (f.isIn(pos)) {
						if (pos == f.startPos + f.fragLen) { // right at the
																// end of frag
							i++;
							while (i < frags.length) {
								if (frags[i].valid) {
									if (frags[i].startPos == pos) { // successive
																	// frag
										f = frags[i];
										break;
									}
								}
								i++;
							}
						}
						// test if fragment full for insertions
						if (f.modified && f.lastMod == f.buffer.length) {
							readFrag(f, pos);
						}
						return f;
					}
				}
				else {
					if (f.isInside(pos)) {
						return f;
					}
				}
			}
		}

		// check for zero document len
		if (getDocLenImpl() == 0) { // should be just for inserts
			for (int i = 0; i < frags.length; i++) {
				f = frags[i];
				if (f.valid) {
					return f; // in fact there should be any valid
				}
			}
			// no valid fragment, make defFrag active
			defFrag.setEmptyValid();
			return defFrag;
		}

		// get the fragment and read into it
		if (frag != null) {
			f = frag.actFrag = frag;
		}
		else {
			f = defFrag;
		}
		readFrag(f, pos);
		return f;
	}

	/**
	 * Update starting positions of all fragments that have higher start
	 * positions than the given one
	 * 
	 * @param frag
	 *            fragment that will not be updated
	 * @param pos
	 *            position over which the fragments will be updated
	 * @param delta
	 *            how much increase/decrease starting positions
	 */
	private void updateStartPos(Fragment frag, int pos, int delta) {
		for (int i = frags.length - 1; i >= 0; i--) {
			Fragment f = frags[i];
			if (f.valid) {
				if (f.startPos >= pos) {
					if (f != frag) {
						f.startPos += delta;
					}
				}
				else {
					break; // reached lowest position
				}
			}
		}
	}

	/**
	 * Insert the array of chars at some position. It can be done in one or more
	 * cycles.
	 * 
	 * @param pos
	 *            from which position
	 * @param text
	 *            text to insert
	 * @param frag
	 *            dedicated fragment (can be null)
	 */
	public synchronized void insert(int pos, char text[], Fragment frag) throws BadLocationException {
		if (pos < 0 || pos > getDocLenImpl()) {
			throwPosException(pos);
		}
		int restLen = text.length; // rest of len to insert
		if (restLen == 0) {
			return;
		}
		if (directMode) {
			support.insert(pos, text, 0, restLen);
			return;
		}
		int wrLen; // write len in one cycle
		int bufPos; // relative position from the start of cache
		int moveLen; // how much data will be moved inside cache to make
						// space
		Fragment f = (frag == null) ? defFrag : frag.actFrag;
		boolean bufOK = false;
		if (f.isInside(pos) && (!f.modified || f.lastMod < f.buffer.length)) {
			bufOK = true; // pos inside and buffer not full for insertion
		}

		while (restLen > 0) { // till all the data will be inserted
			// is pos in current fragment
			if (bufOK) {
				bufOK = false;
			}
			else {
				f = getFrag(pos, frag, true); // get fragment for inserting
			}
			bufPos = pos - f.startPos;
			if (!f.modified) {
				f.modified = true;
				f.firstMod = f.lastMod = bufPos;
			}
			wrLen = f.buffer.length - Math.max(bufPos, f.lastMod);
			wrLen = Math.min(wrLen, restLen);
			// find how many bytes should be moved inside the cache
			moveLen = Math.min(f.fragLen - bufPos, f.buffer.length - (bufPos + wrLen));
			if (moveLen > 0) { // now make space for inserted data
				System.arraycopy(f.buffer, bufPos, f.buffer, bufPos + wrLen, moveLen);
			}
			// move the data from text array to the cache
			System.arraycopy(text, text.length - restLen, f.buffer, bufPos, wrLen);
			f.updatePos(bufPos, wrLen); // correct positions in the fragment
			docLenDelta += wrLen;
			if (frags.length > 1) {
				updateStartPos(f, pos, wrLen); // correct positions of other
												// frags
			}
			restLen -= wrLen;
			pos += wrLen;
		}

		if (frag != null) {
			frag.actFrag = f;
		}
		statInsert += text.length; // update insert statistics
	}

	/**
	 * Insert the string at some position. It can be done in one or more cycles.
	 * 
	 * @param pos
	 *            from which position
	 * @param text
	 *            text to insert
	 * @param frag
	 *            dedicated fragment (can be null)
	 */
	public synchronized void insertString(int pos, String text, Fragment frag) throws BadLocationException {
		if (pos < 0 || pos > getDocLenImpl()) {
			throwPosException(pos);
		}
		int textLen = text.length();
		int restLen = textLen; // rest of len to insert
		if (restLen == 0) {
			return;
		}
		if (directMode) {
			support.insertString(pos, text, 0, restLen);
			return;
		}
		int wrLen; // write len in one cycle
		int bufPos; // relative position from the start of cache
		int moveLen; // how much data will be moved inside cache to make
						// space
		Fragment f = (frag == null) ? defFrag : frag.actFrag;
		boolean bufOK = false;
		if (f.isInside(pos) && (!f.modified || f.lastMod < f.buffer.length)) {
			bufOK = true; // pos inside and buffer not full for insertion
		}

		while (restLen > 0) { // till all the data will be inserted
			// is pos in current fragment
			if (bufOK) {
				bufOK = false;
			}
			else {
				f = getFrag(pos, frag, true); // get fragment for inserting
			}
			bufPos = pos - f.startPos;
			if (!f.modified) {
				f.modified = true;
				f.firstMod = f.lastMod = bufPos;
			}
			wrLen = f.buffer.length - Math.max(bufPos, f.lastMod);
			wrLen = Math.min(wrLen, restLen);
			// find how many bytes should be moved inside the cache
			moveLen = Math.min(f.fragLen - bufPos, f.buffer.length - (bufPos + wrLen));
			if (moveLen > 0) { // now make space for inserted data
				System.arraycopy(f.buffer, bufPos, f.buffer, bufPos + wrLen, moveLen);
			}
			// move the data from text to the cache
			text.getChars(textLen - restLen, textLen - restLen + wrLen, f.buffer, bufPos);
			f.updatePos(bufPos, wrLen); // correct positions in the fragment
			docLenDelta += wrLen;
			if (frags.length > 1) {
				updateStartPos(f, pos, wrLen); // correct positions of other
												// frags
			}
			restLen -= wrLen;
			pos += wrLen;
		}

		if (frag != null) {
			frag.actFrag = f;
		}
		statInsert += textLen; // update insert statistics
	}

	/**
	 * Remove the specified count of chars from the specified position.
	 * 
	 * @param pos
	 *            position from which remove the text
	 * @param len
	 *            length of the data to be removed
	 * @param frag
	 *            dedicated fragment (can be null)
	 * @returns the removed text
	 */
	public synchronized void remove(int pos, int len, Fragment frag) throws BadLocationException {
		int removeLen; // remove length inside the cache
		int restLen = len; // remaining len to remove
		int bufPos;
		if (len == 0) {
			return;
		}
		if (pos < 0) {
			throwPosException(pos);
		}
		if (pos + len > getDocLenImpl()) {
			throwPosException(pos + len);
		}
		if (directMode) {
			support.remove(pos, len);
			return;
		}

		// get the fragment
		Fragment f = (frag == null) ? defFrag : frag.actFrag;
		// first check if the removed block is fully in the cache
		if (pos < 0 || pos + len > f.startPos + f.fragLen) {
			// test position correctness
		}

		while (restLen > 0) {
			// get the correct fragment
			if (!f.isInside(pos)) {
				f = getFrag(pos, f, false);
			}
			// get the positions
			bufPos = pos - f.startPos;
			removeLen = Math.min(restLen, f.fragLen - bufPos);
			System.arraycopy(f.buffer, bufPos + removeLen, f.buffer, bufPos, f.fragLen - (bufPos + removeLen));
			if (!f.modified) {
				f.modified = true;
				f.firstMod = f.lastMod = bufPos;
			}
			f.updatePos(bufPos, -removeLen);
			docLenDelta -= removeLen;
			if (frags.length > 1) {
				updateStartPos(f, pos, -removeLen); // correct positions of
													// other frags
			}
			restLen -= removeLen;
		}

		if (frag != null) {
			frag.actFrag = f;
		}
		statRemove += len;
	}

	/**
	 * Read the data from the cache. Output array must be provided.
	 * 
	 * @param pos
	 *            position from which read starts
	 * @param ret
	 *            char array where the data should be put. There must be enough
	 *            space in array to hold len requested characters
	 * @param offset
	 *            offset in return array where data will be written
	 * @param len
	 *            len to read
	 * @param frag
	 *            dedicated fragment (can be null)
	 */
	public synchronized void read(int pos, char ret[], int offset, int len, Fragment frag) throws BadLocationException {
		if (pos < 0 || len < 0) {
			throwPosException(pos);
		}
		if (pos + len > getDocLenImpl()) {
			throwPosException(pos + len);
		}
		if (directMode) {
			support.read(pos, ret, offset, len);
			return;
		}
		int getLen, bufPos;
		int restLen = len; // the rest to retrieve
		// get the fragment
		Fragment f = (frag == null) ? defFrag : frag.actFrag;
		while (restLen > 0) {
			// check if pos is inside fragment
			if (!f.isInside(pos)) {
				f = getFrag(pos, frag, false);
			}
			bufPos = pos - f.startPos; // position in cache
			getLen = Math.min(f.fragLen - bufPos, restLen);
			// copy into return array
			System.arraycopy(f.buffer, bufPos, ret, len - restLen + offset, getLen);
			pos += getLen;
			restLen -= getLen;
		}

		if (frag != null) {
			frag.actFrag = f;
		}
		statRead += len;
	}

	/**
	 * Read the data from the cache and return reults as char array.
	 * 
	 * @param pos
	 *            position from which read starts
	 * @param len
	 *            len to read
	 * @param frag
	 *            dedicated fragment (can be null)
	 * @return char array with data from document
	 */
	public final char[] read(int pos, int len, Fragment frag) throws BadLocationException {
		if (len < 0) {
			throwPosException(pos);
		}
		char ret[] = new char[len];
		read(pos, ret, 0, len, frag);
		return ret;
	}

	/**
	 * Find something in the cache using specified <CODE>Finder</CODE>. It
	 * covers both forward and backward searches. To do backward search, specify
	 * endPos < startPos. The following position intervals are searched: forward
	 * search: <startPos, endPos) - from startPos to endPos - 1 backward search:
	 * <endPos, startPos) - from startPos - 1 to endPos Both startPos and endPos
	 * can be -1 to indicate end of document position
	 * 
	 * @param finder
	 *            finder that will be used for searching
	 * @param startPos
	 *            position from which search starts. For backward search this
	 *            value is greater or equal than <CODE>endPos</CODE>.
	 * @param endPos
	 *            limit position of search area For backward search this value
	 *            is lower than <CODE>startPos</CODE>.
	 * @param frag
	 *            dedicated fragment (can be null)
	 * @return position where the found string begins or -1 if the text was not
	 *         found
	 */
	public synchronized int find(Finder finder, int startPos, int endPos, Fragment frag) throws BadLocationException {
		int docLen = getDocLenImpl();
		if (startPos == -1) {
			startPos = docLen;
		}
		if (endPos == -1) {
			endPos = docLen;
		}

		// check bounds
		if (startPos < 0 || startPos > docLen) {
			throwPosException(startPos);
		}
		if (endPos < 0 || endPos > docLen) {
			throwPosException(endPos);
		}

		finder.reset(); // initialize finder
		if (startPos == endPos) { // immediate return for void search
			return -1;
		}
		boolean forward = (startPos < endPos);
		int pos = forward ? startPos : (startPos - 1);

		if (directMode) {
			while (true) {
				pos = finder.find(0, support.getDirectModeBuffer(), forward ? startPos : endPos, forward ? endPos : startPos, pos, endPos);

				if (finder.isFound()) {
					if (forward) {
						if (pos < startPos || pos > endPos) {
							return -1; // invalid position returned
						}
					}
					else { // searching backward
						if (pos < endPos || pos > startPos) {
							return -1; // invalid position returned
						}
					}
					return pos;

				}
				else { // not yet found

					// Check position correctness. It eliminates
					// also the equalities because the empty buffer
					// would be pzssed in these cases to the finder
					if (forward) { // searching forward
						if (pos < startPos || pos >= endPos) {
							return -1;
						}
					}
					else { // searching backward
						if (pos < endPos || pos >= startPos) {
							return -1; // not found
						}
					}
				}
			}
		}
		// get the fragment
		Fragment f = (frag == null) ? defFrag : frag.actFrag;
		while (true) {
			// check if pos is inside fragment
			if (!f.isInside(pos)) {
				f = getFrag(pos, frag, false);
			}
			int offset1 = Math.max(forward ? startPos : endPos, f.startPos) - f.startPos;
			int offset2 = Math.min(forward ? endPos : startPos, f.startPos + f.fragLen) - f.startPos;

			pos = finder.find(f.startPos, f.buffer, offset1, offset2, pos, endPos);

			// check found position correctness
			if (finder.isFound()) {
				if (forward) {
					if (pos < startPos || pos > endPos) {
						return -1; // invalid position returned
					}
				}
				else { // searching backward
					if (pos < endPos || pos > startPos) {
						return -1; // invalid position returned
					}
				}
				break;

			}
			else { // not yet found
				// Check position correctness. It eliminates
				// also the equalities because the empty buffer
				// would be pzssed in these cases to the finder
				if (forward) { // searching forward
					if (pos < startPos || pos >= endPos) {
						return -1;
					}
				}
				else { // searching backward
					if (pos < endPos || pos >= startPos) {
						return -1; // not found
					}
				}
			}
		}

		if (frag != null) {
			frag.actFrag = f;
		}
		return pos; // return found position
	}

	/** Get the total length of the document. */
	public synchronized final int getDocLength() {
		return getDocLenImpl();
	}

	private int getDocLenImpl() {
		return support.getDocLength() + docLenDelta;
	}

	/** Fragment of the cache */
	private class Fragment {

		/** buffer space of the fragment */
		char buffer[];

		/** Position of the first character cached in the fragment */
		int startPos = -1;

		/**
		 * Number of chars in the buffer of this fragment. If this value is 0,
		 * it means that the fragment is invalid.
		 */
		int fragLen;

		/** Original len when the buffer was read */
		int origLen;

		/** First modified char in the buffer */
		int firstMod;

		/** Offset of last modified char int the buffer */
		int lastMod;

		/** Was the buffer modified or not */
		boolean modified;

		/**
		 * Is this fragment valid? Fragment becomes invalid when it contains no
		 * data.
		 */
		boolean valid;

		/**
		 * Actual fragment which is either this fragment or when the last search
		 * didn't succeeded in this fragment, some other fragment.
		 */
		Fragment actFrag;

		/**
		 * Construct new fragment with specified length
		 * 
		 * @param len
		 *            len of the constructed fragment
		 */
		Fragment(int len) {
			buffer = new char[len];
			actFrag = this;
		}

		/** Is the position inside this fragment? */
		boolean isInside(int pos) {
			return (pos >= startPos) && (pos < startPos + fragLen);
		}

		/** Is the position inside or at the end of this fragment? */
		boolean isIn(int pos) {
			return (pos >= startPos) && (pos <= startPos + fragLen);
		}

		/** Flush the fragment to support */
		void write() {
			if (!modified) {
				return;
			}

			int endModPos = startPos + lastMod; // last modification
			int writeLen;

			// count the support delta
			int csDelta = 0;
			for (int i = 0; i < frags.length; i++) {
				Fragment f = frags[i];
				if (f.valid) {
					if (f.startPos >= this.startPos) {
						break;
					}
					csDelta += f.origLen - f.fragLen;
				}
			}

			try {
				if (fragLen != origLen) { // will either enlarge or shrink
											// document
					int delta = fragLen - origLen;
					if (delta > 0) { // delta > 0; need to enlarge document
						support.insert(endModPos - delta + csDelta, buffer, lastMod - delta, delta);
						writeLen = lastMod - firstMod - delta;
					}
					else { // delta < 0; need to shrink the document
						support.remove(endModPos + csDelta, -delta);
						writeLen = lastMod - firstMod;
					}
				}
				else { // delta is zero i.e. length of the buffer is the same
					writeLen = lastMod - firstMod;
				}
				if (writeLen > 0) {
					try {
						support.write(startPos + firstMod + csDelta, buffer, firstMod, writeLen);
					} catch (BadLocationException e) {
						if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
							e.printStackTrace();
						}
					}
				}
			} catch (BadLocationException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			}

			docLenDelta += origLen - fragLen;
			origLen = fragLen;
			modified = false;
			statWriteFragCnt++;
		}

		/**
		 * Read the fragment to contain specified position
		 * 
		 * @param pos
		 *            start position
		 * @param len
		 *            length to read
		 * @param csDelta
		 *            delta by which the reading position must be corrected when
		 *            reading through support
		 */
		void read(int pos, int len, int csDelta) {
			int overlap;
			if (pos < startPos) { // read position is lower than start of frag
				overlap = Math.min(pos + len - startPos, fragLen);
				if (overlap > MIN_FRAGMENT_BACK_OVERLAP_LEN) {
					statBackOverlapCnt++;
					System.arraycopy(buffer, 0, buffer, startPos - pos, overlap);
					try {
						support.read(pos + csDelta, buffer, 0, startPos - pos);
					} catch (BadLocationException e) {
						if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
							e.printStackTrace();
						}
					}

					overlap = startPos + overlap; // now overlap means pos of
													// end of block
					if (overlap < pos + len) {
						try {
							support.read(startPos + fragLen + csDelta, buffer, overlap - pos, pos + len - overlap);
						} catch (BadLocationException e) {
							if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
								e.printStackTrace();
							}
						}
					}

				}
				else { // no or small overlap
					try {
						support.read(pos + csDelta, buffer, 0, len);
					} catch (BadLocationException e) {
						if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
							e.printStackTrace();
						}
					}
				}
			}
			else { // pos >= startPos
				overlap = startPos + fragLen - pos;
				if (overlap > 0) { // here any overlap is fine !!!
					statOverlapCnt++;
					System.arraycopy(buffer, pos - startPos, buffer, 0, overlap);
					try {
						support.read(pos + overlap + csDelta, buffer, overlap, len - overlap);
					} catch (BadLocationException e) {
						if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
							e.printStackTrace();
						}
					}
				}
				else { // no overlap
					try {
						support.read(pos + csDelta, buffer, 0, len);
					} catch (BadLocationException e) {
						if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
							e.printStackTrace();
						}
					}
				}
			}

			startPos = pos;
			fragLen = origLen = len;
			valid = true;
			statReadFragCnt++;
		}

		/** Invalidate segment so it will no longer be used for operations */
		void invalidate() {
			write(); // flush the fragment
			valid = false;
			startPos = -1; // important for isIn() and others
			fragLen = origLen = 0;
		}

		/**
		 * Set this segment as valid even when it has zero size. It is done only
		 * when total doc len is 0.
		 */
		void setEmptyValid() {
			statFragSetEmpty++;
			fragLen = origLen = 0;
			startPos = 0;
			valid = true;
		}

		/**
		 * Update various positions in fragment after modification.
		 * 
		 * @param bufPos
		 *            position in the buffer where the modification was done
		 * @param delta
		 *            of the change
		 */
		void updatePos(int bufPos, int delta) {
			if (bufPos < firstMod) {
				firstMod = bufPos;
			}

			if (delta > 0) { // insert was done
				if (bufPos > lastMod) {
					lastMod = bufPos + delta;
				}
				else {
					lastMod += delta;
				}
				fragLen += delta;
				if (fragLen > buffer.length) {
					origLen -= fragLen - buffer.length;
					fragLen = buffer.length;
				}
			}
			else { // remove was done
				if (bufPos - delta <= lastMod) { // if whole block below
													// lastMod
					lastMod += delta;
				}
				else {
					lastMod = bufPos;
				}
				fragLen += delta;
				if (fragLen == 0) { // empty fragment must be invalidated
					invalidate();
				}
			}
		}

		public String toString() {
			int i;
			for (i = 0; i < frags.length; i++) {
				if (frags[i] == this) {
					break;
				}
			}
			return "Frag[" + i + "] valid=" + valid + ", startPos=" + startPos // NOI18N
					+ ", fragLen=" + fragLen + ", origLen=" + origLen // NOI18N
					+ ", fMod=" + firstMod + ", lMod=" + lastMod // NOI18N
					+ ", mod=" + modified; // NOI18N
		}

	}

	/** Throw position exception */
	final void throwPosException(int pos) throws BadLocationException {
		throw new BadLocationException("DocCache: Invalid offset " + pos // NOI18N
				+ ". Document length is " + getDocLenImpl(), pos); // NOI18N
	}

	public String toString() {
		String ret = "support=" + support; // NOI18N
		if (directMode) {
			ret += ", Direct mode, no fragments\n"; // NOI18N
		}
		else {
			ret += ", fragment count=" + frags.length + "\n"; // NOI18N
			for (int i = 0; i < frags.length; i++) {
				ret += frags[i] + "\n"; // NOI18N
			}
		}

		ret += " getDocLength()=" + getDocLength() + ", docLenDelta=" + docLenDelta // NOI18N
				+ ", statRead=" + statRead + ", statInsert=" // NOI18N
				+ statInsert + ", statRemove=" + statRemove // NOI18N
				+ ", statReadFragCnt=" + statReadFragCnt // NOI18N
				+ ", statWriteFragCnt=" + statWriteFragCnt // NOI18N
				+ ", statOverlapCnt=" + statOverlapCnt // NOI18N
				+ ", statBackOverlapCnt=" + statBackOverlapCnt // NOI18N
				+ ", statFragSwitchCnt=" + statFragSwitchCnt // NOI18N
				+ ", statFragSetEmpty=" + statFragSetEmpty; // NOI18N

		return ret;
	}

}
