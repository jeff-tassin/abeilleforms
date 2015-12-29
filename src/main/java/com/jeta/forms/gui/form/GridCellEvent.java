/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.gui.form;

/**
 * A <code>GridCellEvent</code> gets fired when a GridComponent changes or is
 * selected. The GridComponent is responsible for firing the event. This event
 * is only required during design mode.
 * 
 * @author Jeff Tassin
 */
public class GridCellEvent {
	/**
	 * event ids
	 */
	public static final int CELL_CHANGED = 1;
	public static final int EDIT_COMPONENT = 2;
	public static final int CELL_SELECTED = 3;

	/**
	 * common commands
	 */
	public static final String COMPONENT_NAME_CHANGED = "component.name.changed";

	/**
	 * The id for the event
	 */
	private int m_id;

	/** the source for the event */
	private GridComponent m_source;

	/**
	 * A specific command for the event. This allows for greater specificity of
	 * a given event id.
	 */
	private String m_command;

	/**
	 * Creates a <code>GridCellEvent</code> with the specified id and grid
	 * component.
	 */
	public GridCellEvent(int id, GridComponent gc) {
		m_id = id;
		m_source = gc;
	}

	/**
	 * Creates a <code>GridCellEvent</code> with the specified id and grid
	 * component.
	 * 
	 * @param id
	 *            the id for this event
	 * @param gc
	 *            the GridComponent associated with this event
	 * @param cmd
	 *            an optional command to associate with this event.
	 */
	public GridCellEvent(int id, GridComponent gc, String cmd) {
		m_id = id;
		m_source = gc;
		m_command = cmd;
	}

	/**
	 * Returns the event identifier. either: CELL_CHANGED, EDIT_COMPONENT,
	 * CELL_SELECTED
	 */
	public int getId() {
		return m_id;
	}

	/**
	 * Returns an optional command associated with the event id. Can be null.
	 */
	public String getCommand() {
		return m_command;
	}

	/**
	 * @return the source for the grid component
	 */
	public GridComponent getSource() {
		return m_source;
	}

	/**
	 * Sets the source for this event.
	 */
	void setSource(GridComponent src) {
		m_source = src;
	}
}
