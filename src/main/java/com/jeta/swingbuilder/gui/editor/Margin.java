/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.gui.editor;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JViewport;

import com.jeta.forms.gui.common.FormSpecAdapter;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.dnd.DesignerDragSource;
import com.jeta.swingbuilder.gui.utils.Units;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public abstract class Margin extends JComponent implements DesignerDragSource {
	static final int THUMB_WIDTH = 16;
	static final int THUMB_HEIGHT = 16;

	private boolean m_show;

	protected GridComponent m_gc;

	protected boolean m_dragging = false;

	protected JViewport m_viewport;
	protected FormComponent m_form;
	protected GridView m_view;
	protected DesignGridOverlay m_overlay;
	protected ResizeIndicator m_resize_indicator;

	private Orientation m_orientation;

	/**
	 * The object that determines the currently selected component to create
	 * when adding a component to the form.
	 */
	protected ComponentSource m_compsrc;

	/**
	 * The current size of the component
	 */
	protected double m_comp_size;

	protected String m_units = "PX";

	protected Units m_unit_converter = Units.getInstance();

	public Margin(Orientation orientation, FormComponent fc, GridView topview, ComponentSource compSrc, JViewport viewport, boolean show) {
		m_orientation = orientation;
		setPaintMargins(show);
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setOpaque(false);

		m_form = fc;
		m_view = topview;
		m_compsrc = compSrc;
		m_viewport = viewport;
		m_overlay = (DesignGridOverlay) m_view.getOverlay();
		assert (m_overlay != null);
		if (Orientation.VERTICAL.equals(orientation))
			m_resize_indicator = new ResizeIndicator(m_view, Orientation.HORIZONTAL);
		else
			m_resize_indicator = new ResizeIndicator(m_view, Orientation.VERTICAL);
	}

	/**
	 * DesignerDragSource implementation
	 */
	public void cancelDrag() {
		m_dragging = false;
		m_overlay.setResizeIndicator(null);
		m_view.repaint();
		update();
	}

	double convertPoint(int pixels, String units) {
		if ("DLU".equalsIgnoreCase(units)) {
			if (Orientation.HORIZONTAL.equals(m_orientation))
				return m_unit_converter.pixelAsDialogUnitX(pixels, this);
			else
				return m_unit_converter.pixelAsDialogUnitY(pixels, this);
		}
		else if ("PT".equalsIgnoreCase(units))
			return m_unit_converter.pixelAsPoint(pixels, this);
		else if ("IN".equalsIgnoreCase(units))
			return m_unit_converter.pixelAsInch(pixels, this);
		else if ("MM".equalsIgnoreCase(units))
			return m_unit_converter.pixelAsMillimeter(pixels, this);
		else if ("CM".equalsIgnoreCase(units))
			return m_unit_converter.pixelAsCentimeter(pixels, this);
		else
			return (double) pixels;
	}

	public boolean isDragging() {
		return m_dragging;
	}

	boolean isPaintMargin() {
		return m_show;
	}

	public void setPaintMargins(boolean show) {
		m_show = show;
		if (show)
			setPreferredSize(new Dimension(THUMB_WIDTH, THUMB_HEIGHT));
		else
			setPreferredSize(new Dimension(THUMB_WIDTH / 2, THUMB_HEIGHT / 2));

		revalidate();
	}

	protected void startDrag() {
		m_dragging = true;
		/**
		 * we need to request focus on the overlay so we can get Escape key
		 * events for cancel
		 */
		m_overlay.requestFocus();
		JETARegistry.rebind(DesignerDragSource.COMPONENT_ID, this);
		m_overlay.setResizeIndicator(m_resize_indicator);
	}

	abstract void update(GridComponent gc);

	void update() {
		update(m_gc);
	}

	private String getBeanDelegate(GridComponent gc) {
		Component comp = gc.getBeanDelegate();
		if (comp != null)
			return comp.getClass().getName();
		return "null";
	}

	public static class NewSizeAdapter extends FormSpecAdapter {
		private double m_newsize;
		private String m_units;

		public NewSizeAdapter(ColumnSpec spec, double newsize, String units) {
			super(spec);
			m_newsize = newsize;
			m_units = units;
		}

		public NewSizeAdapter(RowSpec spec, double newsize, String units) {
			super(spec);
			m_newsize = newsize;
			m_units = units;
		}

		/**
		 * @return the units (integer) (double) PX, PT, DLU IN, MM, CM
		 */
		public String getConstantUnits() {
			return m_units;
		}

		/**
		 * @return the size.
		 */
		public double getConstantSize() {
			return (double) m_newsize;
		}

		/**
		 * @return CONSTANT, COMPONENT, BOUNDED
		 */
		public String getSizeType() {
			return "CONSTANT";
		}
	}

}
