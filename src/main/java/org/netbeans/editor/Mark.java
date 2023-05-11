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

import javax.swing.text.Position;

/**
 * Marks hold the relative position in the document.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

/**
 * Class defining basic type of mark. This is a mark used most frequently. It's
 * instances are inserted into the leaf plane of the tree.
 */
public class Mark {

	/** Document marks to which this mark belongs. */
	private DocOp op;

	/** Offset at which the mark is located in the document. */
	int offset;

	/** Line at which the mark is located in the document. */
	int line;

	/**
	 * Bias of the mark. It is either false for
	 * {@link javax.swing.text.Position.Bias.Forward} or true for
	 * {@link javax.swing.text.Position.Bias.Backward}
	 */
	boolean backwardBias;

	/** The mark is valid if it's inserted in the marks structure */
	boolean valid;

	/** Construct new mark with forward bias. */
	public Mark() {
		this(Position.Bias.Forward);
	}

	public Mark(Position.Bias bias) {
		this(bias == Position.Bias.Backward);
	}

	/**
	 * Construct new mark.
	 * 
	 * @param backwardBias
	 *            whether the inserts performed right at the position of this
	 *            mark will go after this mark i.e. this mark will not move
	 *            forward when inserting right at its position. This flag
	 *            corresponds to <tt>Position.Bias.Backward</tt>.
	 */
	public Mark(boolean backwardBias) {
		this.backwardBias = backwardBias;
	}

	/** Package private constructor for cover marks */
	Mark(int offset, int line, boolean backwardBias) {
		this.offset = offset;
		this.line = line;
		this.backwardBias = backwardBias;
	}

	/** Get the position of this mark */
	public final int getOffset() throws InvalidMarkException {
		try {
			DocOp lop = op; // local copy
			synchronized (lop) {
				if (lop == op) {
					return lop.marks.getOffset(this);

				}
				else {
					throw new InvalidMarkException();
				}
			}
		} catch (NullPointerException e) {
			throw new InvalidMarkException(e.toString());
		}
	}

	/** Get the line number of this mark */
	public final int getLine() throws InvalidMarkException {
		try {
			DocOp lop = op; // local copy
			synchronized (lop) {
				if (lop == op) {
					return lop.marks.getLine(this);

				}
				else {
					throw new InvalidMarkException();
				}
			}
		} catch (NullPointerException e) {
			throw new InvalidMarkException(e.toString());
		}
	}

	/**
	 * Get the insertAfter flag. Replaced by {@link #getBackwardBias()}
	 * 
	 * @deprecated
	 */
	@Deprecated
	public final boolean getInsertAfter() {
		return backwardBias;
	}

	/**
	 * @return true if the mark has backward bias or false if it has forward
	 *         bias.
	 */
	public final boolean getBackwardBias() {
		return getInsertAfter();
	}

	/**
	 * @return the bias of this mark. It will be either
	 *         {@link javax.swing.text.Position.Bias.Forward} or
	 *         {@link javax.swing.text.Position.Bias.Backward}.
	 */
	public final Position.Bias getBias() {
		return backwardBias ? Position.Bias.Backward : Position.Bias.Forward;
	}

	final void setOp(DocOp op) {
		this.op = op;
	}

	/**
	 * Mark will no longer represent a valid place in the document. Although it
	 * will not be removed from the structure that holds the marks it will be
	 * done later automatically.
	 */
	public final void dispose() {
		try {
			DocOp lop = op;
			synchronized (lop) {
				if (lop == op && valid) {
					lop.marks.dispose(this);

				}
				else { // invalid mark
					throw new IllegalStateException();
				}
			}
		} catch (NullPointerException e) {
			throw new IllegalStateException(e.toString());
		}
	}

	/**
	 * Remove mark from the structure holding the marks. The mark can be
	 * inserted again into some document.
	 */
	public final void remove() throws InvalidMarkException {
		try {
			DocOp lop = op;
			synchronized (op) {
				if (lop == op && valid) {
					op.marks.remove(this);
					op = null;

				}
				else { // invalid mark
					throw new InvalidMarkException();
				}
			}
		} catch (NullPointerException e) {
			throw new InvalidMarkException(e.toString());
		}
	}

	final void removeDisposed() {
		op = null;
	}

	/**
	 * Compare this mark to some position.
	 * 
	 * @param pos
	 *            tested position
	 * @return zero - if the marks have the same position less than zero - if
	 *         this mark is before the position greater than zero - if this mark
	 *         is after the position
	 */
	public final int compare(int pos) throws InvalidMarkException {
		return getOffset() - pos;
	}

	/**
	 * This function is called from removeUpdater when mark occupies the removal
	 * area. The mark can decide what to do next. If it doesn't redefine this
	 * method it will be simply moved to the begining of removal area. It is
	 * valid to add or remove other mark from this method. It is even possible
	 * (but not very useful) to add the mark to the removal area. However that
	 * mark will not be notified about current removal.
	 * 
	 * @deprecated It will not be supported in the future.
	 */
	@Deprecated
	protected void removeUpdateAction(int pos, int len) {
	}

	/**
	 * @return true if this mark is currently inserted in the document or false
	 *         otherwise.
	 */
	public final boolean isValid() {
		try {
			synchronized (op) {
				return valid;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	/** Get info about <CODE>Mark</CODE>. */
	public String toString() {
		return "rawOffset=" + offset // NOI18N
				+ ", rawLine=" + line // NOI18N
				+ ", backwardBias=" + backwardBias; // NOI18N
	}

}
