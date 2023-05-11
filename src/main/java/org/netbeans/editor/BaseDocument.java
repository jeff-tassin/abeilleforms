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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * Document implementation
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class BaseDocument extends AbstractDocument implements SettingsChangeListener {

	/** Registry identification property */
	public static final String ID_PROP = "id"; // NOI18N

	/** Line separator property for reading files in */
	public static final String READ_LINE_SEPARATOR_PROP = DefaultEditorKit.EndOfLineStringProperty;

	/**
	 * Line separator property for writing content into files. If not set the
	 * writing defaults to the READ_LINE_SEPARATOR_PROP.
	 */
	public static final String WRITE_LINE_SEPARATOR_PROP = "write-line-separator"; // NOI18N

	/** File name property */
	public static final String FILE_NAME_PROP = "file-name"; // NOI18N

	/** Wrap search mark property */
	public static final String WRAP_SEARCH_MARK_PROP = "wrap-search-mark"; // NOI18N

	/**
	 * Undo manager property. This can be used to implement undo in a simple
	 * way. Default undo and redo actions try to get this property and perform
	 * undo and redo through it.
	 */
	public static final String UNDO_MANAGER_PROP = "undo-manager"; // NOI18N

	/**
	 * Kit class property. This can become useful for getting the settings that
	 * logicaly belonging to the document.
	 */
	public static final String KIT_CLASS_PROP = "kit-class"; // NOI18N

	/** String forward finder property */
	public static final String STRING_FINDER_PROP = "string-finder"; // NOI18N

	/** String backward finder property */
	public static final String STRING_BWD_FINDER_PROP = "string-bwd-finder"; // NOI18N

	/** Highlight search finder property. */
	public static final String BLOCKS_FINDER_PROP = "blocks-finder"; // NOI18N

	/**
	 * Maximum line width encountered during the initial read operation. This is
	 * filled by Analyzer and used by UI to set the correct initial width of the
	 * component. Values: java.lang.Integer
	 */
	public static final String LINE_LIMIT_PROP = "line-limit"; // NOI18N

	/**
	 * Size of the line batch. Line batch can be used at various places
	 * especially when processing lines by syntax scanner.
	 */
	public static final String LINE_BATCH_SIZE = "line-batch-size"; // NOI18N

	/** Line separator is marked by CR (Macintosh) */
	public static final String LS_CR = "\r"; // NOI18N

	/** Line separator is marked by LF (Unix) */
	public static final String LS_LF = "\n"; // NOI18N

	/** Line separator is marked by CR and LF (Windows) */
	public static final String LS_CRLF = "\r\n"; // NOI18N

	/**
	 * Maximum of concurrent read threads (other will wait until one of these
	 * will leave).
	 */
	private static final int MAX_READ_THREADS = 10;

	/** Write lock without write lock */
	private static final String WRITE_LOCK_MISSING = "extWriteUnlock() without extWriteLock()"; // NOI18N

	/** Debug modifications performed on the document */
	private static final boolean debug = Boolean.getBoolean("netbeans.debug.editor.document"); // NOI18N
	/** Debug the stack of calling of the insert/remove */
	private static final boolean debugStack = Boolean.getBoolean("netbeans.debug.editor.document.stack"); // NOI18N

	/**
	 * Document operations support class for this document. It presents the base
	 * synchronization level for most of the operations. Some of the operations
	 * are available through <tt>Utilities</tt> class.
	 */
	DocOp op;

	/** How many spaces should be displayed instead of '\t' character */
	private int tabSize = SettingsDefaults.defaultTabSize.intValue();

	/**
	 * Size of one indentation level. If this variable is null (value is not set
	 * in Settings, then the default algorithm will be used.
	 */
	private Integer shiftWidth;

	/** How many times current writer requested writing */
	private int writeDeep;

	/** How many times atomic writer requested writing */
	private int atomicDepth;

	/* Was the document initialized by reading? */
	protected boolean inited;

	/* Was the document modified by doing inert/remove */
	protected boolean modified;

	/** Listener list */
	protected EventListenerList listenerList = new EventListenerList();

	/** Listener to changes in find support */
	PropertyChangeListener findSupportListener;

	/** Default element - lazily inited */
	protected BaseElement defaultRootElem;

	private SyntaxSupport syntaxSupport;

	/** Layer list for document level layers */
	private DrawLayerList drawLayerList = new DrawLayerList();

	/** Chain of document level bookmarks */
	private MarkChain bookmarkChain;

	/** Reset merging next created undoable edit to the last one. */
	boolean undoMergeReset;

	/** Kit class stored here */
	Class kitClass;

	/**
	 * Undo event for atomic events is fired after the successful atomic
	 * operation is finished. The changes are stored in this variable during the
	 * atomic operation. If the operation is broken, these edits are used to
	 * restore previous state.
	 */
	private CompoundEdit atomicEdits;

	private Acceptor identifierAcceptor;

	private Acceptor whitespaceAcceptor;

	private ArrayList syntaxList = new ArrayList();

	/** List of the positions used by storePosition() */
	private ArrayList posList = new ArrayList();

	/** List of the integers marking the free positions in the posList. */
	private ArrayList posFreeList = new ArrayList();

	/** Root element of line elements representation */
	protected LineRootElement lineRootElement;

	private LeafElement composedTextElement;

	/**
	 * Last document event to be undone. The field is filled by the lastly done
	 * modification undoable edit. BaseDocumentEvent.canUndo() checks this flag.
	 */
	UndoableEdit lastModifyUndoEdit; // #8692 check last modify undo edit

	/** List of annotations for this document. */
	private Annotations annotations;

	/** List of bookmarks attached to this document */
	private Bookmarks bookmarks;

	/**
	 * Create base document with a specified syntax.
	 * 
	 * @param kitClass
	 *            class used to initialize this document with proper settings
	 *            category based on the editor kit for which this document is
	 *            created
	 * @param syntax
	 *            syntax scanner to use with this document
	 */
	public BaseDocument(Class kitClass, boolean addToRegistry) {
		this(kitClass, addToRegistry, new DocOp());
	}

	private BaseDocument(Class kitClass, boolean addToRegistry, DocOp op) {
		super(op);
		this.op = op;
		this.kitClass = kitClass;

		setDocumentProperties(new LazyPropertyMap(getDocumentProperties()));

		settingsChange(null); // initialize variables from settings
		Settings.addSettingsChangeListener(this);

		op.setDocument(this);

		// Line separators default to platform ones
		putProperty(READ_LINE_SEPARATOR_PROP, Analyzer.getPlatformLS());

		bookmarkChain = new MarkChain(this, DrawLayerFactory.BOOKMARK_LAYER_NAME);

		// Add document draw-layers
		addLayer(new DrawLayerFactory.SyntaxLayer(), DrawLayerFactory.SYNTAX_LAYER_VISIBILITY);

		addLayer(new DrawLayerFactory.HighlightSearchLayer(), DrawLayerFactory.HIGHLIGHT_SEARCH_LAYER_VISIBILITY);

		addLayer(new DrawLayerFactory.BookmarkLayer(), DrawLayerFactory.BOOKMARK_LAYER_VISIBILITY);

		// Additional initialization of the document through the kit
		BaseKit kit = BaseKit.getKit(kitClass);
		if (kit != null) {
			kit.initDocument(this);
		}

		// Possibly add the document to registry
		if (addToRegistry) {
			Registry.addDocument(this); // add if created thru the kit
		}

		// Start listen on find-support
		findSupportListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				findSupportChange(evt);
			}
		};
		FindSupport.getFindSupport().addPropertyChangeListener(findSupportListener);
		findSupportChange(null); // update doc by find settings
	}

	private void findSupportChange(PropertyChangeEvent evt) {
		// set all finders to null
		putProperty(STRING_FINDER_PROP, null);
		putProperty(STRING_BWD_FINDER_PROP, null);
		putProperty(BLOCKS_FINDER_PROP, null);

		DrawLayerFactory.HighlightSearchLayer hsl = (DrawLayerFactory.HighlightSearchLayer) findLayer(DrawLayerFactory.HIGHLIGHT_SEARCH_LAYER_NAME);

		Boolean b = (Boolean) FindSupport.getFindSupport().getPropertyNoInit(SettingsNames.FIND_HIGHLIGHT_SEARCH);
		hsl.setEnabled((b != null) ? b.booleanValue() : false);

		fireChangedUpdate(createDocumentEvent(0, getLength(), DocumentEvent.EventType.CHANGE)); // refresh
																								// whole
																								// document
	}

	/**
	 * Called when settings were changed. The method is called also in
	 * constructor, so the code must count with the evt being null.
	 */
	public void settingsChange(SettingsChangeEvent evt) {
		String settingName = (evt != null) ? evt.getSettingName() : null;

		if (settingName == null || SettingsNames.TAB_SIZE.equals(settingName)) {
			tabSize = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.TAB_SIZE, SettingsDefaults.defaultTabSize);
		}

		if (settingName == null || SettingsNames.INDENT_SHIFT_WIDTH.equals(settingName)) {
			Object shw = Settings.getValue(kitClass, SettingsNames.INDENT_SHIFT_WIDTH);
			if (shw instanceof Integer) { // currently only Integer values are
											// supported
				shiftWidth = (Integer) shw;
			}
		}

		if (settingName == null || SettingsNames.READ_BUFFER_SIZE.equals(settingName)) {
			int readBufferSize = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.READ_BUFFER_SIZE, SettingsDefaults.defaultReadBufferSize);
			putProperty(SettingsNames.READ_BUFFER_SIZE, readBufferSize);
		}

		if (settingName == null || SettingsNames.WRITE_BUFFER_SIZE.equals(settingName)) {
			int writeBufferSize = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.WRITE_BUFFER_SIZE, SettingsDefaults.defaultWriteBufferSize);
			putProperty(SettingsNames.WRITE_BUFFER_SIZE, writeBufferSize);
		}

		if (settingName == null || SettingsNames.MARK_DISTANCE.equals(settingName)) {
			int markDistance = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.MARK_DISTANCE, SettingsDefaults.defaultMarkDistance);
			putProperty(SettingsNames.MARK_DISTANCE, markDistance);
		}

		if (settingName == null || SettingsNames.MAX_MARK_DISTANCE.equals(settingName)) {
			int maxMarkDistance = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.MAX_MARK_DISTANCE, SettingsDefaults.defaultMaxMarkDistance);
			putProperty(SettingsNames.MAX_MARK_DISTANCE, maxMarkDistance);
		}

		if (settingName == null || SettingsNames.MIN_MARK_DISTANCE.equals(settingName)) {
			int minMarkDistance = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.MIN_MARK_DISTANCE, SettingsDefaults.defaultMinMarkDistance);
			putProperty(SettingsNames.MIN_MARK_DISTANCE, minMarkDistance);
		}

		if (settingName == null || SettingsNames.READ_MARK_DISTANCE.equals(settingName)) {
			int readMarkDistance = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.READ_MARK_DISTANCE, SettingsDefaults.defaultReadMarkDistance);
			putProperty(SettingsNames.READ_MARK_DISTANCE, readMarkDistance);
		}

		if (settingName == null || SettingsNames.SYNTAX_UPDATE_BATCH_SIZE.equals(settingName)) {
			int syntaxUpdateBatchSize = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.SYNTAX_UPDATE_BATCH_SIZE,
					SettingsDefaults.defaultSyntaxUpdateBatchSize);
			putProperty(SettingsNames.SYNTAX_UPDATE_BATCH_SIZE, syntaxUpdateBatchSize);
		}

		if (settingName == null || SettingsNames.LINE_BATCH_SIZE.equals(settingName)) {
			int lineBatchSize = SettingsUtil.getPositiveInteger(kitClass, SettingsNames.LINE_BATCH_SIZE, SettingsDefaults.defaultLineBatchSize);
			putProperty(SettingsNames.LINE_BATCH_SIZE, lineBatchSize);
		}

		if (settingName == null || SettingsNames.IDENTIFIER_ACCEPTOR.equals(settingName)) {
			identifierAcceptor = SettingsUtil.getAcceptor(kitClass, SettingsNames.IDENTIFIER_ACCEPTOR, AcceptorFactory.LETTER_DIGIT);
		}

		if (settingName == null || SettingsNames.WHITESPACE_ACCEPTOR.equals(settingName)) {
			whitespaceAcceptor = SettingsUtil.getAcceptor(kitClass, SettingsNames.WHITESPACE_ACCEPTOR, AcceptorFactory.WHITESPACE);
		}

		boolean stopOnEOL = SettingsUtil.getBoolean(kitClass, SettingsNames.WORD_MOVE_NEWLINE_STOP, true);
		if (settingName == null || SettingsNames.NEXT_WORD_FINDER.equals(settingName)) {
			putProperty(SettingsNames.NEXT_WORD_FINDER, SettingsUtil.getValue(kitClass, SettingsNames.NEXT_WORD_FINDER, new FinderFactory.NextWordFwdFinder(
					this, stopOnEOL, false)));
		}

		if (settingName == null || SettingsNames.PREVIOUS_WORD_FINDER.equals(settingName)) {
			putProperty(SettingsNames.PREVIOUS_WORD_FINDER, SettingsUtil.getValue(kitClass, SettingsNames.PREVIOUS_WORD_FINDER,
					new FinderFactory.PreviousWordBwdFinder(this, stopOnEOL, false)));
		}

	}

	Syntax getFreeSyntax() {
		synchronized (syntaxList) {
			int cnt = syntaxList.size();
			return (cnt > 0) ? (Syntax) syntaxList.remove(cnt - 1) : BaseKit.getKit(kitClass).createSyntax(this);
		}
	}

	void releaseSyntax(Syntax syntax) {
		synchronized (syntaxList) {
			syntaxList.add(syntax);
		}
	}

	/** Get the formatter for this document. */
	public Formatter getFormatter() {
		return Formatter.getFormatter(kitClass);
	}

	public SyntaxSupport getSyntaxSupport() {
		if (syntaxSupport == null) {
			syntaxSupport = BaseKit.getKit(kitClass).createSyntaxSupport(this);
		}
		return syntaxSupport;
	}

	/**
	 * Perform any generic text processing. The advantage of this method is that
	 * it allows the text to processed in line batches. The initial size of the
	 * batch is given by the SettingsNames.LINE_BATCH_SIZE. The
	 * TextBatchProcessor.processTextBatch() method is called for every text
	 * batch. If the method returns true, it means the processing should
	 * continue with the next batch of text which will have double line count
	 * compared to the previous one. This guarantees there will be not too many
	 * batches so the processing should be more efficient.
	 * 
	 * @param tbp
	 *            text batch processor to be used to process the text batches
	 * @param startPos
	 *            starting position of the processing.
	 * @param endPos
	 *            ending position of the processing. This can be -1 to signal
	 *            the end of document. If the endPos is lower than startPos then
	 *            the batches are created in the backward direction.
	 * @return the returned value from the last tpb.processTextBatch() call. The
	 *         -1 will be returned for (startPos == endPos).
	 */
	public int processText(TextBatchProcessor tbp, int startPos, int endPos) throws BadLocationException {
		if (endPos == -1) {
			endPos = getLength();
		}
		int batchLineCnt = ((Integer) getProperty(SettingsNames.LINE_BATCH_SIZE)).intValue();
		int batchStart = startPos;
		int ret = -1;
		if (startPos < endPos) { // batching in forward direction
			while (ret < 0 && batchStart < endPos) {
				int batchEnd = Math.min(Utilities.getRowStart(this, batchStart, batchLineCnt), endPos);
				if (batchEnd == -1) { // getRowStart() returned -1
					batchEnd = endPos;
				}
				ret = tbp.processTextBatch(this, batchStart, batchEnd, (batchEnd == endPos));
				batchLineCnt *= 2; // double the scanned area
				batchStart = batchEnd;
			}
		}
		else {
			while (ret < 0 && batchStart > endPos) {
				int batchEnd = Math.max(Utilities.getRowStart(this, batchStart, -batchLineCnt), endPos);
				ret = tbp.processTextBatch(this, batchStart, batchEnd, (batchEnd == endPos));
				batchLineCnt *= 2; // double the scanned area
				batchStart = batchEnd;
			}
		}
		return ret;
	}

	public boolean isIdentifierPart(char ch) {
		return identifierAcceptor.accept(ch);
	}

	public boolean isWhitespace(char ch) {
		return whitespaceAcceptor.accept(ch);
	}

	/**
	 * Length of document.
	 * 
	 * @return number of characters >= 0
	 */
	public final int getLength() {
		return op.length();
	}

	/**
	 * Create the mark for the given position and store it in the list. The
	 * position can be later retrieved through its ID.
	 */
	int storePosition(int pos) throws BadLocationException {
		Mark mark = op.insertMark(pos, false);
		int ind;
		if (posFreeList.size() > 0) {
			ind = ((Integer) posFreeList.remove(posFreeList.size() - 1)).intValue();
			posList.set(ind, mark);
		}
		else { // no free indexes
			ind = posList.size();
			posList.add(mark);
		}
		return ind;
	}

	int getStoredPosition(int posID) {
		if (posID < 0 || posID >= posList.size()) {
			return -1;
		}

		try {
			return ((Mark) posList.get(posID)).getOffset();
		} catch (InvalidMarkException e) {
			return -1;
		}
	}

	void removeStoredPosition(int posID) {
		if (posID >= 0 || posID < posList.size()) {
			Mark mark = (Mark) posList.get(posID);
			posList.set(posID, null); // clear the index
			posFreeList.add(posID);

			// Remove the mark #19429
			try {
				mark.remove();
			} catch (InvalidMarkException e) {
			}
		}
	}

	/** Inserts string into document */
	public void insertString(int offset, String text, AttributeSet a) throws BadLocationException {
		if (text == null || text.length() == 0) {
			return;
		}

		// Check offset correctness
		if (offset < 0 || offset > getLength()) {
			throw new BadLocationException("Wrong insert position", offset); // NOI18N
		}

		// possible CR-LF conversion
		text = Analyzer.convertLSToLF(text);

		// Perform the insert
		extWriteLock();
		try {

			preInsertCheck(offset, text, a);

			// Do the real insert into the content
			UndoableEdit edit = op.insertString(offset, text);

			if (debug) {
				System.err.println("BaseDocument.insertString(): doc=" + this // NOI18N
						+ (modified ? "" : " - first modification") // NOI18N
						+ ", offset=" + offset // NOI18N
						+ ", text='" + text + "'" // NOI18N
				);
			}
			if (debugStack) {
				Thread.dumpStack();
			}

			BaseDocumentEvent evt = createDocumentEvent(offset, text.length(), DocumentEvent.EventType.INSERT);
			if (edit != null) {
				evt.addEdit(edit);

				lastModifyUndoEdit = edit; // #8692 check last modify undo edit
			}

			modified = true;

			if (atomicDepth > 0) {
				if (atomicEdits == null) {
					atomicEdits = new AtomicCompoundEdit();
				}
				atomicEdits.addEdit(evt); // will be added
			}

			insertUpdate(evt, a);

			evt.end();

			fireInsertUpdate(evt);

			boolean isComposedText = ((a != null) && (a.isDefined(StyleConstants.ComposedTextAttribute)));

			if (atomicDepth == 0 && !isComposedText) { // !!! check
				fireUndoableEditUpdate(new UndoableEditEvent(this, evt));
			}
		} finally {
			extWriteUnlock();
		}
	}

	/** Removes portion of a document */
	public void remove(int offset, int len) throws BadLocationException {
		if (len > 0) {
			extWriteLock();
			try {
				int docLen = getLength();
				if (offset < 0 || offset > docLen) {
					throw new BadLocationException("Wrong remove position", offset); // NOI18N
				}
				if (offset + len > docLen) {
					throw new BadLocationException("End offset of removed text is too big", offset + len); // NOI18N
				}

				preRemoveCheck(offset, len);

				BaseDocumentEvent evt = createDocumentEvent(offset, len, DocumentEvent.EventType.REMOVE);

				removeUpdate(evt);

				UndoableEdit edit = op.remove(offset, len);
				if (edit != null) {
					evt.addEdit(edit);

					lastModifyUndoEdit = edit; // #8692 check last modify undo
												// edit
				}

				if (debug) {
					System.err.println("BaseDocument.remove(): doc=" + this // NOI18N
							+ ", offset=" + offset + ", len=" + len); // NOI18N
				}
				if (debugStack) {
					Thread.dumpStack();
				}

				if (atomicDepth > 0) { // add edits as soon as possible
					if (atomicEdits == null) {
						atomicEdits = new AtomicCompoundEdit();
					}
					atomicEdits.addEdit(evt); // will be added
				}

				postRemoveUpdate(evt);

				evt.end();

				fireRemoveUpdate(evt);
				if (atomicDepth == 0) {
					fireUndoableEditUpdate(new UndoableEditEvent(this, evt));
				}
			} finally {
				extWriteUnlock();
			}
		}
	}

	/**
	 * This method is called automatically before the document insertion occurs
	 * and can be used to revoke the insertion before it occurs by throwing the
	 * <tt>BadLocationException</tt>.
	 * 
	 * @param offset
	 *            position where the insertion will be done
	 * @param text
	 *            string to be inserted
	 * @param a
	 *            attributes of the inserted text
	 */
	protected void preInsertCheck(int offset, String text, AttributeSet a) throws BadLocationException {
	}

	/**
	 * This method is called automatically before the document removal occurs
	 * and can be used to revoke the removal before it occurs by throwing the
	 * <tt>BadLocationException</tt>.
	 * 
	 * @param offset
	 *            position where the insertion will be done
	 * @param len
	 *            length of the removal
	 */
	protected void preRemoveCheck(int offset, int len) throws BadLocationException {
	}

	public String getText(int[] block) throws BadLocationException {
		return getText(block[0], block[1] - block[0]);
	}

	public char[] getChars(int pos, int len) throws BadLocationException {
		return op.getChars(pos, len);
	}

	public char[] getChars(int[] block) throws BadLocationException {
		return getChars(block[0], block[1] - block[0]);
	}

	public void getChars(int pos, char ret[], int offset, int len) throws BadLocationException {
		op.getChars(pos, ret, offset, len);
	}

	/**
	 * Find something in document using a finder.
	 * 
	 * @param finder
	 *            finder to be used for the search
	 * @param startPos
	 *            position in the document where the search will start
	 * @param limitPos
	 *            position where the search will be end with reporting that
	 *            nothing was found.
	 */
	public int find(Finder finder, int startPos, int limitPos) throws BadLocationException {
		if (finder instanceof AdjustFinder) {
			int docLen = getLength();
			if (limitPos == -1) {
				limitPos = docLen;
			}
			if (startPos == -1) {
				startPos = docLen;
			}

			if (startPos == limitPos) { // stop immediately
				finder.reset(); // reset() should be called in all the cases
				return -1; // must stop here because wouldn't know if fwd/bwd
							// search?
			}

			boolean forward = (startPos < limitPos);
			startPos = ((AdjustFinder) finder).adjustStartPos(this, startPos);
			limitPos = ((AdjustFinder) finder).adjustLimitPos(this, limitPos);
			boolean voidSearch = (forward ? (startPos >= limitPos) : (startPos <= limitPos));
			if (voidSearch) {
				finder.reset();
				return -1;
			}
		}

		return op.find(finder, startPos, limitPos);
	}

	/** Fire the change event to repaint the given block of text. */
	public void repaintBlock(int startOffset, int endOffset) {
		BaseDocumentEvent evt = createDocumentEvent(startOffset, endOffset - startOffset, DocumentEvent.EventType.CHANGE);
		fireChangedUpdate(evt);
	}

	public void print(PrintContainer container) {
		readLock();
		try {
			EditorUI editorUI = BaseKit.getKit(kitClass).createPrintEditorUI(this);
			DrawGraphics.PrintDG printDG = new DrawGraphics.PrintDG(container);
			DrawEngine.getDrawEngine().draw(printDG, editorUI, 0, getLength(), 0, 0, Integer.MAX_VALUE);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} finally {
			readUnlock();
		}
	}

	/** Create biased position in document */
	public Position createPosition(int offset, Position.Bias bias) throws BadLocationException {
		return op.createPosition(offset, bias);
	}

	/** Return array of root elements - usually only one */
	public Element[] getRootElements() {
		Element[] elems = new Element[1];
		elems[0] = getDefaultRootElement();
		return elems;
	}

	/** Return default root element */
	public Element getDefaultRootElement() {
		if (defaultRootElem == null) {
			defaultRootElem = new org.netbeans.editor.LeafElement(this, null, null, 0, getLength(), false, false);
		}
		return defaultRootElem;
	}

	/** Runs the runnable under read lock. */
	public void render(Runnable r) {
		readLock();
		try {
			r.run();
		} finally {
			readUnlock();
		}
	}

	/**
	 * Runs the runnable under write lock. This is a stronger version of the
	 * runAtomicAsUser() method, because if there any locked sections in the
	 * documents this methods breaks the modification locks and modifies the
	 * document. If there are any excpeptions thrown during the processing of
	 * the runnable, all the document modifications are rolled back
	 * automatically.
	 */
	public void runAtomic(Runnable r) {
		runAtomicAsUser(r);
	}

	/**
	 * Runs the runnable under write lock. If there are any excpeptions thrown
	 * during the processing of the runnable, all the document modifications are
	 * rolled back automatically.
	 */
	public void runAtomicAsUser(Runnable r) {
		boolean completed = false;
		atomicLock();
		try {
			r.run();
			completed = true;
		} finally {
			try {
				if (!completed) {
					breakAtomicLock();
				}
			} finally {
				atomicUnlock();
			}
		}
	}

	/**
	 * Insert contents of reader at specified position into document.
	 * 
	 * @param reader
	 *            reader from which data will be read
	 * @param pos
	 *            on which position that data will be inserted
	 */
	public void read(Reader reader, int pos) throws IOException, BadLocationException {
		extWriteLock();
		try {

			if (pos < 0 || pos > getLength()) {
				throw new BadLocationException("BaseDocument.read()", pos); // NOI18N
			}

			if (inited || modified) { // was the document already initialized?
				Analyzer.read(this, reader, pos);
			}
			else { // not initialized yet, we can use initialRead()
				Analyzer.initialRead(this, reader, true);
				BaseDocumentEvent evt = createDocumentEvent(0, 0, DocumentEvent.EventType.INSERT);
				evt.end();
				fireInsertUpdate(evt); // fire the insert event with zero
										// length to notify about the change
				inited = true; // initialized but not modified
			}
		} finally {
			extWriteUnlock();
		}
	}

	/**
	 * Write part of the document into specified writer.
	 * 
	 * @param writer
	 *            writer into which data will be written.
	 * @param pos
	 *            from which position get the data
	 * @param len
	 *            how many characters write
	 */
	public void write(Writer writer, int pos, int len) throws IOException, BadLocationException {
		readLock();
		try {

			if ((pos < 0) || ((pos + len) > getLength())) {
				throw new BadLocationException("BaseDocument.write()", pos); // NOI18N
			}
			Analyzer.write(this, writer, pos, len);
			writer.flush();
		} finally {
			readUnlock();
		}
	}

	/**
	 * Invalidate the state-infos in all the syntax-marks in the whole document.
	 * The Syntax can call this method if it changes its internal state in the
	 * way that affects the future returned tokens. The syntax-state-info in all
	 * the marks is reset and it will be lazily restored when necessary.
	 */
	public void invalidateSyntaxMarks() {
		extWriteLock();
		try {
			op.invalidateSyntaxMarks();
			repaintBlock(0, getLength());
		} finally {
			extWriteUnlock();
		}
	}

	/**
	 * Get the number of spaces the TAB character ('\t') visually represents.
	 * This is related to <code>SettingsNames.TAB_SIZE</code> setting.
	 */
	public int getTabSize() {
		return tabSize;
	}

	/**
	 * Get the width of one indentation level. The algorithm first checks
	 * whether there's a value for the INDENT_SHIFT_WIDTH setting. If so it uses
	 * it, otherwise it uses <code>formatter.getSpacesPerTab()</code>.
	 * 
	 * @see getTabSize()
	 * @see Formatter.getSpacesPerTab()
	 */
	public int getShiftWidth() {
		if (shiftWidth != null) {
			return shiftWidth.intValue();

		}
		else {
			return getFormatter().getSpacesPerTab();
		}
	}

	public final Class getKitClass() {
		return kitClass;
	}

	/**
	 * This method prohibits merging of the next document modification with the
	 * previous one even if it would be normally possible.
	 */
	public void resetUndoMerge() {
		undoMergeReset = true;
	}

	/*
	 * Defined because of the hack for undo() in the BaseDocumentEvent.
	 */
	protected void fireChangedUpdate(DocumentEvent e) {
		super.fireChangedUpdate(e);
	}

	protected void fireInsertUpdate(DocumentEvent e) {
		super.fireInsertUpdate(e);
	}

	protected void fireRemoveUpdate(DocumentEvent e) {
		super.fireRemoveUpdate(e);
	}

	/**
	 * Extended write locking of the document allowing reentrant write lock
	 * acquiring.
	 */
	public synchronized final void extWriteLock() {
		if (Thread.currentThread() != getCurrentWriter()) {
			super.writeLock();
		}
		else { // inner locking block
			writeDeep++; // only increase write deepness
		}
	}

	/**
	 * Extended write unlocking.
	 * 
	 * @see extWriteLock()
	 */
	public synchronized final void extWriteUnlock() {
		if (Thread.currentThread() != getCurrentWriter()) {
			throw new RuntimeException(WRITE_LOCK_MISSING);
		}

		if (writeDeep == 0) { // most outer locking block
			super.writeUnlock();
		}
		else { // just inner locking block
			writeDeep--;
		}
	}

	public synchronized final void atomicLock() {
		extWriteLock();
		atomicDepth++;
	}

	public synchronized final void atomicUnlock() {
		extWriteUnlock();
		if (atomicDepth == 0) {
			return;
		}
		if (--atomicDepth == 0) { // must fire possible undo event
			if (atomicEdits != null) {
				atomicEdits.end();
				fireUndoableEditUpdate(new UndoableEditEvent(this, atomicEdits));
				atomicEdits = null;
			}
		}
	}

	/**
	 * Is the document currently atomically locked? It's not synced as this
	 * method must be called only from writer thread.
	 */
	public final boolean isAtomicLock() {
		return (atomicDepth > 0);
	}

	/**
	 * Break the atomic lock so that doc is no longer in atomic mode. All the
	 * performed changes are rolled back automatically. Even after calling this
	 * method, the atomicUnlock() must still be called. This method is not
	 * synced as it must be called only from writer thread.
	 */
	public final void breakAtomicLock() {
		atomicDepth = 0;
		if (atomicEdits != null) {
			atomicEdits.end();
			atomicEdits.undo();
			atomicEdits = null;
		}
	}

	protected final int getAtomicDepth() {
		return atomicDepth;
	}

	protected BaseDocumentEvent createDocumentEvent(int pos, int length, DocumentEvent.EventType type) {
		return new BaseDocumentEvent(this, pos, length, type);
	}

	/**
	 * Was the document modified by either insert/remove but not the initial
	 * read)?
	 */
	public boolean isModified() {
		return modified;
	}

	/** Get the layer with the specified name */
	public DrawLayer findLayer(String layerName) {
		return drawLayerList.findLayer(layerName);
	}

	public boolean addLayer(DrawLayer layer, int visibility) {
		if (drawLayerList.add(layer, visibility)) {
			BaseDocumentEvent evt = createDocumentEvent(0, 0, DocumentEvent.EventType.CHANGE);
			evt.addEdit(new BaseDocumentEvent.DrawLayerChange(layer.getName(), visibility));
			fireChangedUpdate(evt);
			return true;
		}
		else {
			return false;
		}
	}

	final DrawLayerList getDrawLayerList() {
		return drawLayerList;
	}

	/** Toggle the bookmark for the current line */
	public boolean toggleBookmark(int pos) throws BadLocationException {
		pos = Utilities.getRowStart(this, pos);
		boolean marked = bookmarkChain.toggleMark(pos);
		BaseDocumentEvent evt = createDocumentEvent(pos, 0, DocumentEvent.EventType.CHANGE);
		fireChangedUpdate(evt);
		return marked;
	}

	/**
	 * Get the position of the next bookmark.
	 * 
	 * @pos position from which to search
	 * @wrap wrap around the end of document
	 * @return position of the next bookmark or -1 if there is no mark
	 */
	public int getNextBookmark(int pos, boolean wrap) throws BadLocationException {
		try {
			pos = Utilities.getRowStart(this, pos);
			int rel = bookmarkChain.compareMark(pos);
			MarkFactory.ChainDrawMark mark = bookmarkChain.getCurMark();
			if (rel <= 0) { // right at this line, go next
				if (mark != null) {
					if (mark.next != null) {
						return mark.next.getOffset();
					}
					else { // last bookmark
						return (wrap && bookmarkChain.chain != null) ? bookmarkChain.chain.getOffset() : -1;
					}
				}
				else { // no marks
					return -1;
				}
			}
			else { // mark after pos
				return mark.getOffset();
			}
		} catch (InvalidMarkException e) {
			if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
				e.printStackTrace();
			}
			return 0;
		}
	}

	private LineRootElement getLineRootElement() {
		if (lineRootElement == null) {
			lineRootElement = new LineRootElement();
		}
		return lineRootElement;
	}

	public Element getParagraphElement(int pos) {
		return getLineRootElement().getElement(getLineRootElement().getElementIndex(pos));
	}

	/**
	 * Returns object which represent list of annotations which are attached to
	 * this document.
	 * 
	 * @return object which represent attached annotations
	 */
	public synchronized Annotations getAnnotations() {
		if (annotations == null) {
			annotations = new Annotations(this);
		}
		return annotations;
	}

	/**
	 * Returns object which represent list of annotations which are attached to
	 * this document.
	 * 
	 * @return object which represent attached annotations
	 */
	public synchronized Bookmarks getBookmarks() {
		if (bookmarks == null) {
			bookmarks = new Bookmarks();
		}
		return bookmarks;
	}

	public String toString() {
		return super.toString() + ", kitClass=" + getKitClass() // NOI18N
				+ ", docLen=" + getLength(); // NOI18N
	}

	/** Detailed debug info about the document */
	public String toStringDetail() {
		return toString();
	}

	/** Substitution for (each line = element) behavior */
	class LineRootElement implements Element {

		public Document getDocument() {
			return BaseDocument.this;
		}

		public Element getParentElement() {
			return null;
		}

		public String getName() {
			return "line-elements-root"; // NOI18N
		}

		public AttributeSet getAttributes() {
			return null;
		}

		public int getStartOffset() {
			return 0;
		}

		public int getEndOffset() {
			return getLength();
		}

		public int getElementIndex(int offset) {
			try {
				return Utilities.getLineOffset(BaseDocument.this, offset);
			} catch (BadLocationException e) {
				return 0;
			}
		}

		public int getElementCount() {
			return Utilities.getRowCount(BaseDocument.this);
		}

		public Element getElement(int index) {
			if (index < 0) {
				// throwing IOOBE to be compatible with swing's AbstractDocument
				// that does not check index < 0
				throw new IndexOutOfBoundsException("Line index=" + index + " must be >= 0"); // NOI18N
			}

			try {
				int startPos = Utilities.getRowStartFromLineOffset(BaseDocument.this, index);

				if (startPos < 0) { // line index was invalid
				/*
				 * throw new IllegalArgumentException("Line index=" + index //
				 * NOI18N + " is invalid. Maximum index is " // NOI18N +
				 * (Utilities.getRowCount(BaseDocument.this) - 1) + " in the
				 * document " // NOI18N +
				 * Utilities.debugDocument(BaseDocument.this) + "." // NOI18N );
				 */

					return getElement(Utilities.getRowCount(BaseDocument.this) - 1); // last
																						// line

				}

				LineElement elem = null;
				MarkFactory.LineMark mark = (MarkFactory.LineMark) op.getOffsetMark(startPos, MarkFactory.LineMark.class);
				if (mark != null) {
					elem = mark.lineElemRef; // non-weak ref due to #17351
				}

				if (elem == null) {
					// Create the mark if necessary
					if (mark == null) {
						mark = new MarkFactory.LineMark();
						elem = new LineElement(mark);
						mark.lineElemRef = elem;

						try {
							op.insertMark(mark, startPos);
						} catch (InvalidMarkException e) {
							if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
								e.printStackTrace();
							}
						}
					}

				}

				return elem;

			} catch (BadLocationException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
				return null;
			}
		}

		public boolean isLeaf() {
			return false;
		}

	}

	/** Line element representation */
	class LineElement implements Element {

		/** Mark at the begining of the line */
		MarkFactory.LineMark startMark;

		LineElement(MarkFactory.LineMark startMark) throws BadLocationException {
			this.startMark = startMark;
		}

		public Document getDocument() {
			return BaseDocument.this;
		}

		public int getStartOffset() {
			try {
				return startMark.getOffset();
			} catch (InvalidMarkException e) {
				return 0;
			}
		}

		public int getEndOffset() {
			try {
				return op.getEOLNL(getStartOffset());
			} catch (BadLocationException e) {
				return 0;
			}
		}

		public Element getParentElement() {
			return lineRootElement;
		}

		public String getName() {
			return "line-element"; // NOI18N
		}

		public AttributeSet getAttributes() {
			return null;
		}

		public int getElementIndex(int offset) {
			return 0;
		}

		public int getElementCount() {
			return 0;
		}

		public Element getElement(int index) {
			return null;
		}

		public boolean isLeaf() {
			return true;
		}

		public void finalize() throws Throwable {
			try {
				startMark.remove();
			} catch (InvalidMarkException e) {
			}
			super.finalize();
		}

		public String toString() {
			return "getStartOffset()=" + getStartOffset() // NOI18N
					+ ", getEndOffset()=" + getEndOffset() // NOI18N
					+ ", getParentElement()=" + getParentElement(); // NOI18N
		}

	}

	/**
	 * Compound edit that write-locks the document for the whole processing of
	 * its undo operation.
	 */
	class AtomicCompoundEdit extends CompoundEdit {

		public void undo() throws CannotUndoException {
			extWriteLock();
			try {
				super.undo();
			} finally {
				extWriteUnlock();
			}
		}

		public void redo() throws CannotRedoException {
			extWriteLock();
			try {
				super.redo();
			} finally {
				extWriteUnlock();
			}
		}

	}

	/**
	 * Property evaluator is useful for lazy evaluation of properties of the
	 * document when
	 * {@link javax.swing.text.Document#getProperty(java.lang.String)} is
	 * called.
	 */
	public interface PropertyEvaluator {

		/** Get the real value of the property */
		public Object getValue();

	}

	private class LazyPropertyMap extends Hashtable {

		LazyPropertyMap(Dictionary dict) {
			super(5);

			Enumeration en = dict.keys();
			while (en.hasMoreElements()) {
				Object key = en.nextElement();
				put(key, dict.get(key));
			}
		}

		public Object get(Object key) {
			Object val = super.get(key);
			if (val instanceof PropertyEvaluator) {
				val = ((PropertyEvaluator) val).getValue();
			}

			return val;
		}

	}

}
