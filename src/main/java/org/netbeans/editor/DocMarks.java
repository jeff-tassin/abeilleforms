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

import java.util.Map;

/**
 * Document marks enable to store position and line information to simplify
 * orientation in the document. They are stored in the array with a gap similar
 * like in {@link javax.swing.text.GapContent}.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

final class DocMarks {

	/** Empty array of marks used initially as marks array. */
	private static final Mark[] EMPTY = new Mark[0];

	/** Marks array with the gap. */
	private Mark[] marks;

	/** Length of the marks array minus one. */
	private int marksLengthM1;

	/** Starting index of the gap. */
	private int gapStart;

	/** End of the gap (first index after the end of the gap). */
	private int gapEnd;

	/** Starting offset of the fictional document's data gap. */
	private int dataGapStart;

	/** Ending offset of the fictional document's data gap. */
	private int dataGapEnd;

	/**
	 * Number of line separators corresponding to the fictional document's data
	 * gap.
	 */
	private int dataGapLineCount;

	/**
	 * Number of marks that are still in the marks array but that have the valid
	 * flag set to false.
	 */
	int unusedMarksCount;

	Mark startMark; // for compatibility

	DocMarks() {
		marks = EMPTY;
		marksLengthM1 = -1;
		dataGapEnd = Integer.MAX_VALUE / 2;
		dataGapLineCount = Integer.MAX_VALUE / 2;

		startMark = new Mark(true);
		insert(startMark);
	}

	synchronized int getMarksCount() {
		return marks.length - (gapEnd - gapStart);
	}

	/**
	 * Insert mark.
	 * 
	 * @param mark
	 *            mark to insert. The mark must have the valid offset and the
	 *            line filled in.
	 */
	synchronized void insert(Mark mark) {
		int offset = mark.offset;
		boolean backwardBias = mark.backwardBias;

		int ind = findInsertIndex(offset, backwardBias);

		if (gapStart == gapEnd) {
			increaseAndMoveGap(1, ind);
			ind = gapStart;

		}
		else if (ind != gapStart) {
			moveGap(ind);
			ind = gapStart;
		}

		if (offset > dataGapStart || (offset == dataGapStart && !backwardBias)) {
			mark.offset += dataGapEnd - dataGapStart;
			mark.line += dataGapLineCount;
		}

		marks[gapStart++] = mark;
		mark.valid = true;
	}

	synchronized void dispose(Mark mark) {
		mark.valid = false;
		unusedMarksCount++;

		if (unusedMarksCount > Math.max(5, marks.length - (gapEnd - gapStart))) { // GC
																					// rule
			removeDisposedMarks();
		}
	}

	synchronized void remove(Mark mark) {
		int ind = findIndex(mark);
		if (ind < 0) {
			throw new IllegalStateException("Invalid mark " + mark);
		}

		if (ind == gapEnd) {
			marks[gapEnd++] = null; // enable GC

		}
		else if (ind == gapStart - 1) {
			marks[--gapStart] = null; // enable GC

		}
		else { // need to move the gap
			moveGap(ind + 1);
			marks[--gapStart] = null; // enable GC
		}

		mark.valid = false;
	}

	private void moveGap(int ind) {
		if (ind <= gapStart) { // move gap down
			int moveSize = gapStart - ind;
			gapEnd -= moveSize;
			System.arraycopy(marks, ind, marks, gapEnd, moveSize);
			gapStart = ind;

		}
		else { // above gap
			int moveSize = ind - gapEnd;
			System.arraycopy(marks, gapEnd, marks, gapStart, moveSize);
			gapStart += moveSize;
			gapEnd += moveSize;
		}
	}

	/**
	 * Increase the size of the gap.
	 * 
	 * @param reqSize
	 *            the additional size requested. Typically equals to 1.
	 * @param ind
	 *            index to which the gap is moved.
	 * @return relocated index in the new marks array that corresponds to the
	 *         original index.
	 */
	private void increaseAndMoveGap(int reqSize, int ind) {
		int newSize = marks.length * 3 / 2 + reqSize;
		Mark[] newMarks = new Mark[newSize];
		marksLengthM1 = newSize - 1;
		if (ind <= gapStart) {
			int moveSize = marks.length - gapEnd;
			if (moveSize > 0) {
				newSize -= moveSize;
				System.arraycopy(marks, gapEnd, newMarks, newSize, moveSize);
			}
			moveSize = gapStart - ind;
			if (moveSize > 0) {
				newSize -= moveSize;
				System.arraycopy(marks, ind, newMarks, newSize, moveSize);
			}
			if (ind > 0) {
				System.arraycopy(marks, 0, newMarks, 0, ind);
			}
			gapStart = ind;
			gapEnd = newSize;

		}
		else { // ind above gap
			if (gapStart > 0) {
				System.arraycopy(marks, 0, newMarks, 0, gapStart);
			}
			int moveSize = ind - gapEnd;
			if (moveSize > 0) {
				System.arraycopy(marks, gapEnd, newMarks, gapStart, moveSize);
				gapStart += moveSize;
			}
			moveSize = marks.length - ind;
			if (moveSize > 0) {
				newSize -= moveSize;
				System.arraycopy(marks, ind, newMarks, newSize, moveSize);
			}
			gapEnd = newSize;
			ind += newMarks.length - marks.length;
		}
		marks = newMarks;
	}

	/**
	 * Document was modified. This means that the ficitonal data gap must be
	 * updated by the modifiaction. The data gap can possibly be moved and the
	 * marks in the moved area must be updated accordingly.
	 * 
	 * @param offset
	 *            offset of the modification
	 * @param line
	 *            line of modification. It is populated for removals only.
	 * @param length
	 *            length of added/removed data. If the length is positive then
	 *            the insert occured. If the length is negative then the removal
	 *            has occured.
	 * @param lineCount
	 *            number of line separators in the added/removed part. It is
	 *            &lt;0 for removals.
	 */
	synchronized void update(int offset, int line, int length, int lineCount) {

		if (length < 0) { // removal occured
			offset -= length; // move offset after end of removal (length < 0)
		}

		// First move the gap if necessary
		int dataGapSize = dataGapEnd - dataGapStart;
		int ind; // index of the first mark with offset over the data gap
		if (dataGapStart < offset) { // current data gap below offset
			ind = findInsertIndex(dataGapStart, true);

			int offsetAfterGap = offset + dataGapSize;
			int bound = (ind <= gapStart) ? (gapStart - 1) : marksLengthM1;
			while (true) {
				while (ind <= bound) {
					Mark mark = marks[ind];
					if (mark.offset > offsetAfterGap || (mark.offset == offsetAfterGap && !mark.backwardBias)) {
						bound = marksLengthM1; // break to shifting of data gap
						break;
					}

					// Move mark before future data gap
					mark.offset -= dataGapSize;
					mark.line -= dataGapLineCount;

					ind++;
				}

				if (bound < marksLengthM1) { // shift the bound
					bound = marksLengthM1;
					ind = gapEnd;

				}
				else { // update gap bounds and break the infinite loop
					break;
				}
			}

			dataGapEnd += (offset - dataGapStart);
			dataGapStart = offset;

		}
		else if (dataGapStart > offset) { // current data gap over offset
			ind = findInsertIndex(dataGapStart, true);

			// Update marks possibly in two passes - above and below gap
			int bound = (ind >= gapEnd) ? gapEnd : 0; // boundary for the
														// first pass
			while (true) { // possibly two passes (above and under gap)
				while (ind > bound) {
					Mark mark = marks[--ind];
					if (mark.offset < offset || (mark.offset == offset && mark.backwardBias)) {
						bound = 0; // break to shifting of data gap
						ind++;
						break;
					}

					// Move mark after future data gap
					mark.offset += dataGapSize;
					mark.line += dataGapLineCount;
				}

				if (bound > 0 && gapStart > 0) {
					ind = gapStart;
					bound = 0;

				}
				else { // bound == 0
					break;
				}
			}

			dataGapEnd -= dataGapStart - offset;
			dataGapStart = offset;

		}
		else { // no move of the gap
			ind = (length < 0) ? findInsertIndex(offset, true) : 0; // index no
																	// important
																	// for
																	// inserts
		}

		dataGapStart += length; // shrink the gap
		// possibly decrease number of lines represented by gap
		dataGapLineCount -= lineCount;

		if (length < 0) { // remove performed
			/*
			 * Marks with backward bias within the whole removed area will be
			 * moved to the offset (before data gap). It is necessary that all
			 * the backwardBias marks within the removed area must be placed
			 * before all the forwardBias marks within the removed area. The
			 * marks with the forward bias are placed after data gap.
			 */
			int lastBBMInd = -1; // index of last backward bias mark
			int bound = (ind >= gapEnd) ? gapEnd : 0; // boundary for the
														// first pass
			offset += length; // offset back to original value
			int offsetAfterGap = offset + (dataGapEnd - dataGapStart); // use
																		// current
																		// (updated)
																		// gap
																		// size
			int lineAfterGap = line + dataGapLineCount;

			while (true) { // possibly two passes (above and under gap)
				while (ind > bound) {
					Mark mark = marks[--ind];
					if (mark.offset < offset || (mark.offset == offset && mark.backwardBias)) {
						bound = 0; // break to shifting of data gap
						break;
					}

					// service the potential move because of the biases ordering
					if (mark.backwardBias) {
						// Move mark before data gap
						mark.offset = offset;
						mark.line = line;

						if (lastBBMInd < 0) {
							lastBBMInd = ind;
						}

					}
					else { // forward bias
						// Move mark after data gap
						mark.offset = offsetAfterGap;
						mark.line = lineAfterGap;

						if (lastBBMInd >= 0) { // backward bias mark(s) exist
							// exchange this fb mark with last bb mark
							marks[ind] = marks[lastBBMInd];
							marks[lastBBMInd] = mark;
							if (lastBBMInd == gapEnd) {
								lastBBMInd = gapStart;
							}
							lastBBMInd--;
						}
					}
				}

				if (bound > 0 && gapStart > 0) {
					ind = gapStart;
					bound = 0;

				}
				else { // bound == 0
					break;
				}
			}

		}
	}

	private void removeDisposedMarks() {
		int ind = 0;
		int validInd = 0;

		while (ind < gapStart) {
			Mark mark = marks[ind];
			if (!mark.valid) {
				mark.removeDisposed();

			}
			else {
				if (ind != validInd) {
					marks[validInd++] = mark;
				}
			}
			ind++;
		}
		gapStart = validInd;

		ind = marksLengthM1;
		validInd = ind + 1;
		while (ind >= gapEnd) {
			Mark mark = marks[ind];
			if (!mark.valid) {
				mark.removeDisposed();

			}
			else {
				if (ind != --validInd) {
					marks[validInd] = mark;
				}
			}
			ind--;
		}
		gapEnd = validInd;
	}

	synchronized int getOffset(Mark mark) {
		int offset = mark.offset;
		// offset == dataGapStart for backward bias marks
		// forward bias marks == dataGapEnd instead
		return (offset <= dataGapStart) ? offset : (offset - (dataGapEnd - dataGapStart));
	}

	synchronized int getLine(Mark mark) {
		// offset == dataGapStart for backward bias marks
		// forward bias marks == dataGapEnd instead
		return (mark.offset <= dataGapStart) ? mark.line : (mark.line - dataGapLineCount);
	}

	/**
	 * Find the index of the given mark.
	 * 
	 * @param mark
	 *            for which the index is being searched.
	 * @return index &gt;=0 or &lt;0 if the mark was not found.
	 */
	int findIndex(Mark mark) {
		int offset = getOffset(mark);
		int ind = findInsertIndex(offset, mark.backwardBias);
		int bound = (ind >= gapEnd) ? gapEnd : 0; // boundary for the first
													// pass
		while (true) { // possibly two passes (above and under gap)
			while (ind > bound) {
				if (marks[--ind] == mark) {
					return ind;
				}
			}

			if (bound > 0 && gapStart > 0) {
				ind = gapStart;
				bound = 0;

			}
			else { // bound == 0
				break;
			}
		}
		return -1; // not found
	}

	/**
	 * Find the index at which it's valid to perform an insert of the new mark.
	 * 
	 * @param offset
	 *            offset of the mark
	 * @param backwardBias
	 *            whether the mark has backward or forward bias.
	 * @return index &gt;= 0 and &lt;=<CODE>marks.length</CODE> in the marks
	 *         array where the insert of the mark with the given offset and bias
	 *         can be done.<BR>
	 *         If there is more marks with the same offset and bias as the
	 *         requested ones then the index after these marks is returned. The
	 *         gapStart is preferred by the method over the gapEnd
	 *         automatically.
	 */
	int findInsertIndex(int offset, boolean backwardBias) {
		if (!backwardBias) { // search one offset higher for forward bias
			offset++;
		}

		boolean belowGap = (gapStart > 0 && getOffset(marks[gapStart - 1]) >= offset);

		// Find the index by using binary search
		int low;
		int high;

		if (belowGap) {
			low = 0;
			high = gapStart - 1;

		}
		else { // over gap
			low = gapEnd;
			high = marksLengthM1;
		}

		while (low <= high) {
			int ind = (low + high) / 2; // mid in the binary search
			Mark mark = marks[ind];
			int markOffset = getOffset(mark);

			if (markOffset < offset) {
				low = ind + 1;

			}
			else if (markOffset > offset) {
				high = ind - 1;

			}
			else { // exact offset found
				if (!backwardBias) { // forward bias required
					/*
					 * Searched for offset increased by one so go to begining of
					 * all marks with (offset + 1)
					 */
					int bound = belowGap ? 0 : gapEnd;
					while (true) {
						while (--ind >= bound) {
							mark = marks[ind];
							if (getOffset(mark) < offset) {
								bound = 0;
								break;
							}
						}

						if (bound > 0) {
							bound = 0;
							ind = gapStart;

						}
						else {
							break;
						}
					}
					ind++;

				}
				else { // backward bias required
					if (!mark.backwardBias) { // mark from bin-search has fwd
												// bias
						/*
						 * Find (backward) the first mark with lower offset or
						 * same offset but backward bias and go one index back.
						 */
						int bound = belowGap ? 0 : gapEnd;
						while (true) {
							while (--ind >= bound) {
								mark = marks[ind];
								if (getOffset(mark) < offset || mark.backwardBias) {
									bound = 0;
									break;
								}
							}

							if (bound > 0) {
								bound = 0;
								ind = gapStart;

							}
							else {
								break;
							}
						}
						ind++;

					}
					else { // the mark from bin-search has bwd bias
						/*
						 * Goto the end of the marks with the same offset and
						 * backward bias by finding the first mark with the same
						 * offset but forward bias or mark with greater offset.
						 */
						int bound = belowGap ? (gapStart - 1) : marksLengthM1;
						while (true) {
							while (++ind <= bound) {
								mark = marks[ind];
								if (getOffset(mark) > offset || !mark.backwardBias) {
									bound = Integer.MAX_VALUE;
									break;
								}
							}

							if (bound < marksLengthM1) {
								bound = marksLengthM1;
								ind = gapEnd - 1;

							}
							else {
								break;
							}
						}
					}
				}

				if (ind == gapEnd) {
					ind = gapStart; // prefer gapStart over gapEnd
				}
				return ind;
			}
		}

		// not exact offset found
		if (!belowGap && low == gapEnd) {
			low = gapStart; // prefer gapStart over gapEnd
		}

		return low;
	}

	/**
	 * Get the mark with offset lower than the parameter.
	 * 
	 * @param offset
	 *            requested offset
	 * @param markClass
	 *            class of the mark to be found. It can be null to accept any
	 *            mark.
	 */
	synchronized Mark getLeftMark(int offset, Class markClass) {
		Mark ret = null;
		;
		boolean done = false; // to have just one ret stmt

		if (offset > 0) {
			int ind = findInsertIndex(offset - 1, false);
			if (ind > marksLengthM1) {
				if (gapEnd <= marksLengthM1) {
					ind--;
				}
				else {
					ind = gapStart;
				}
			}

			if (ind == gapStart) {
				if (gapStart > 0) {
					ind--;
				}
				else {
					ret = (markClass == null) ? startMark : null;
					done = true;
				}

			}
			else { // valid index
				if (ind == gapEnd) {
					ind = gapStart;
				}
				ind--; // move to startable index
				if (ind < 0) {
					ret = (markClass == null) ? startMark : null;
					done = true;
				}
			}

			if (!done) {
				int bound = (ind <= gapStart) ? 0 : gapEnd;
				while (true) {
					while (ind >= bound) {
						Mark mark = marks[ind--];
						if (markClass == null || markClass.isInstance(mark)) {
							ret = mark;
							done = true;
							bound = 0;
							break;
						}
					}

					if (bound > 0) {
						bound = 0;
						ind = gapStart - 1;

					}
					else {
						break;
					}
				}
			}
		}

		if (!done) {
			ret = (markClass == null) ? startMark : null;
		}

		// System.err.println("getLeftMark() offset=" + offset + ", markClass="
		// + markClass + ", found mark=" + ret);
		// if (ret != null) {
		// System.err.println(" markOffset=" + getOffset(ret) + ", line=" +
		// getLine(ret));
		// }
		if (ret != null && !ret.valid) {
			throw new IllegalStateException("Invalid mark");
		}
		return ret;
	}

	/**
	 * Get mark that is right at given offset or null.
	 * 
	 * @param offset
	 *            offset where the mark should be found.
	 * @param markClass
	 *            class of the mark to be found. It can be null to accept any
	 *            mark.
	 */
	synchronized Mark getOffsetMark(int offset, Class markClass) {
		int ind = findInsertIndex(offset, false);

		int bound = (ind <= gapStart) ? 0 : gapEnd;
		while (true) {
			while (--ind >= bound) {
				Mark mark = marks[ind];
				if (getOffset(mark) != offset) {
					return null;
				}
				if (markClass == null || markClass.isInstance(mark)) {
					return mark;
				}
			}

			if (bound > 0) {
				bound = 0;
				ind = gapStart;

			}
			else {
				break;
			}
		}

		return null;
	}

	/**
	 * Render marks by some <CODE>Renderer</CODE>. It is the most efficient
	 * way to handle especially multiple adjacent marks. Rendering function is
	 * called in synchronized manner, so no one will modify mark array while
	 * executing this function.
	 */
	public synchronized void render(Renderer r) {
		r.marks = this;
		r.render();
		r.marks = null;
	}

	/**
	 * Gets the nearest lower position for specified line. This method can be
	 * used when the only line information is available and the position is
	 * needed (i.e. setting breakpoints, going to line with error etc).
	 * 
	 * @param line
	 *            line offset for which we want mark
	 * @return mark with lower or equal line. Caution! When the caller gets the
	 *         mark and it usually tries to get position of returned mark.
	 *         However the mark can be removed meantime and call <CODE>getOffset()</CODE>
	 *         will throw <CODE>InvalidMarkException</CODE>. In that case
	 *         caller should call <CODE>getMarkFromLine()</CODE> again to get
	 *         another mark and retry.
	 */
	synchronized Mark getMarkFromLine(int line) {
		boolean aboveGap = (gapEnd <= marksLengthM1 && getLine(marks[gapEnd]) <= line);

		// Find the index by using binary search
		int low;
		int high;

		if (!aboveGap) {
			low = 0;
			high = gapStart - 1;

		}
		else { // over gap
			low = gapEnd;
			high = marksLengthM1;
		}

		while (low <= high) {
			int ind = (low + high) / 2; // mid in the binary search
			Mark mark = marks[ind];
			int markLine = getLine(mark);

			if (markLine < line) {
				low = ind + 1;

			}
			else if (markLine > line) {
				high = ind - 1;

			}
			else { // exact line found
				return mark;
			}
		}

		// mark with exact line not found
		return (high >= 0) ? marks[high] : startMark;
	}

	/**
	 * More efficient way of handling marks especially if there is a need to
	 * work with more than one mark at the moment.
	 */
	static abstract class Renderer {

		DocMarks marks;

		Renderer() {
		}

		DocMarks getMarks() {
			return marks;
		}

		/** Create array of all marks */
		Mark[] copyAllMarks() {
			Mark[] ret = new Mark[marks.getMarksCount()];
			System.arraycopy(marks.marks, 0, ret, 0, marks.gapStart);
			System.arraycopy(marks.marks, marks.gapEnd, ret, marks.gapStart, marks.marks.length - marks.gapEnd);
			return ret;
		}

		Mark[] getMarkArray() {
			return marks.marks;
		}

		int getMarkArrayLength() {
			return marks.marks.length;
		}

		int getMarkIndex(Mark mark) {
			if (!mark.valid) {
				throw new IllegalStateException();
			}
			return marks.findIndex(mark);
		}

		int getMarkOffset(Mark mark) {
			if (!mark.valid) {
				throw new IllegalStateException();
			}
			return marks.getOffset(mark);
		}

		int getNextIndex(int index) {
			if (++index == marks.gapStart) {
				index = marks.gapEnd;
			}
			return index;
		}

		abstract void render();

	}

	/**
	 * Check whether the marks offsets and lines are sorted correctly.
	 */
	void check() {
		int ind = 0;
		int bound = gapStart - 1;
		int lastOffset = 0;
		int lastLine = 0;
		boolean lastBackwardBias = true;

		while (true) {
			while (ind <= bound) {
				Mark mark = marks[ind++];

				if (mark.offset < lastOffset || (mark.offset == lastOffset && (mark.backwardBias && !lastBackwardBias))) {
					consistencyError(true, ind - 1); // offset error
				}

				if (mark.line < lastLine) {
					consistencyError(false, ind - 1);
				}

				lastOffset = mark.offset;
				lastLine = mark.line;
				lastBackwardBias = mark.backwardBias;

			}

			if (bound < marksLengthM1) { // shift the bound
				bound = marksLengthM1;
				ind = gapEnd;

			}
			else { // update gap bounds and break the infinite loop
				break;
			}
		}
	}

	private void consistencyError(boolean offsetError, int ind) {
		throw new IllegalStateException("DocMarks.check(): " + (offsetError ? "Offset" : "Line") + " inconsistency found at ind=" + ind + ", mark="
				+ marks[ind]);
	}

	/** List all the marks into string. */
	public String toStringDetail() {
		return toStringDetail(null);
	}

	String toStringDetail(Map testMarksMap) {
		StringBuffer sb = new StringBuffer(toString());
		sb.append('\n');

		boolean beforeGap = true;
		while (true) {
			int i = beforeGap ? 0 : gapEnd;
			int bound = beforeGap ? gapStart : marks.length;
			sb.append("Marks ");
			sb.append(beforeGap ? "before" : "after");
			sb.append(" gap:\n");

			while (i < bound) {
				Mark mark = marks[i];
				Mark testMark = null;
				if (testMarksMap != null) {
					testMark = (Mark) testMarksMap.get(mark);
					if (testMark == null) {
						throw new IllegalStateException("No test mark for mark=" + mark);
					}
				}

				try {
					sb.append("[");
					sb.append(i);
					sb.append("]: (");
					sb.append(mark.offset);
					sb.append(", ");
					sb.append(mark.line);
					sb.append(", ");
					sb.append(mark.backwardBias ? 'B' : 'F');
					sb.append(") -> (");
					sb.append(mark.getOffset());
					sb.append(", ");
					sb.append(mark.getLine());
					sb.append(')');

					if (testMark != null) {
						sb.append(" testMark: (");
						sb.append(testMark.offset);
						sb.append(", ");
						sb.append(testMark.line);
						sb.append(", ");
						sb.append(testMark.backwardBias ? 'B' : 'F');
						sb.append(')');
					}

					sb.append('\n');

				} catch (InvalidMarkException e) {
					e.printStackTrace();
					throw new IllegalStateException();
				}

				i++;
			}

			beforeGap = !beforeGap;
			if (beforeGap) {
				break;
			}
		}
		return sb.toString();
	}

	/** Get info about <CODE>DocMarks</CODE>. */
	public String toString() {
		return "marksCount=" + getMarksCount() + ", gapStart=" + gapStart + ", gapEnd=" + gapEnd + ", dataLen="
				+ (Integer.MAX_VALUE - (dataGapEnd - dataGapStart)) + ", dataGapStart=" + dataGapStart + ", dataGapEnd=" + dataGapEnd + ", dataGapLineCount="
				+ dataGapLineCount;
	}

}
