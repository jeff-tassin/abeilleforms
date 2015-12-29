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
 * A <code>GridViewEvent</code> is fired by a GridView when the view changes
 * or one of its components changes. This event is only required in design mode.
 * 
 * @author Jeff Tassin
 */
public class GridViewEvent {
	private int m_id;

	/**
	 * The source of this event
	 */
	private GridView m_source;

	/**
	 * If the GridViewEvent was triggered by one of its child components.
	 */
	private GridCellEvent m_comp_event;

	public static final int ROW_ADDED = 1;
	public static final int ROW_DELETED = 2;
	public static final int ROW_SPEC_CHANGED = 3;
	public static final int COLUMN_ADDED = 4;
	public static final int COLUMN_DELETED = 5;
	public static final int COLUMN_SPEC_CHANGED = 7;
	public static final int CELL_CHANGED = 8;
	public static final int EDIT_COMPONENT = 9;
	public static final int CELL_SELECTED = 10;
	public static final int ROW_GROUPS_CHANGED = 11;
	public static final int COLUMN_GROUPS_CHANGED = 12;

	public GridViewEvent(GridView src, int id) {
		m_id = id;
		m_source = src;
	}

	public GridViewEvent(GridView src, int id, GridComponent gc) {
		m_id = id;
		m_source = src;
		m_comp_event = new GridCellEvent(id, gc, null);
	}

	/**
	 * 
	 */
	public GridViewEvent(GridView src, int id, GridCellEvent srcEvent) {
		m_id = id;
		m_source = src;
		m_comp_event = srcEvent;
	}

	public int getId() {
		return m_id;
	}

	public String getCommand() {
		if (m_comp_event != null)
			return m_comp_event.getCommand();
		else
			return null;
	}

	public GridComponent getComponent() {
		if (m_comp_event != null)
			return m_comp_event.getSource();
		else
			return null;
	}

	/**
	 * Return the component event. This is non-null only if the original source
	 * of this event was a GridComponent.
	 */
	public GridCellEvent getComponentEvent() {
		return m_comp_event;
	}

	public GridView getSource() {
		return m_source;
	}

	public void print() {
		String sid = "UNKNOWN";
		if (m_id == ROW_ADDED)
			sid = "ROW_ADDED";
		else if (m_id == ROW_DELETED)
			sid = "ROW_DELETED";
		else if (m_id == ROW_SPEC_CHANGED)
			sid = "ROW_SPEC_CHANGED";
		else if (m_id == COLUMN_ADDED)
			sid = "COLUMN_ADDED";
		else if (m_id == COLUMN_DELETED)
			sid = "COLUMN_DELETED";
		else if (m_id == COLUMN_SPEC_CHANGED)
			sid = "COLUMN_SPEC_CHANGED";
		else if (m_id == CELL_CHANGED)
			sid = "CELL_CHANGED";
		else if (m_id == EDIT_COMPONENT)
			sid = "EDIT_COMPONENT";
		else if (m_id == CELL_SELECTED)
			sid = "CELL_SELECTED";
		else if (m_id == ROW_GROUPS_CHANGED)
			sid = "ROW_GROUPS_CHANGED";
		else if (m_id == COLUMN_GROUPS_CHANGED)
			sid = "COLUMN_GROUPS_CHANGED";

		System.out.println("GridViewEvent:  id = " + sid + "  component: " + getComponent());
	}
}
