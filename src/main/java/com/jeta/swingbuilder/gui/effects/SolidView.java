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

package com.jeta.swingbuilder.gui.effects;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.gui.effects.SolidPainter;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.forms.store.properties.effects.SolidProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.panel.SwingBuilderPanel;

/**
 * @author Jeff Tassin
 */
public class SolidView extends JETAPanel implements PaintView {
	/**
	 * The fillProperties form.
	 */
	private SwingBuilderPanel m_view;

	/**
	 * The preview for the gradient settings
	 */
	private GridView m_preview;

	/**
	 * The property
	 */
	private SolidProperty m_prop = new SolidProperty();
	private SolidPainter m_painter = new SolidPainter();

	/**
	 * ctor
	 */
	public SolidView(GridView preview) {
		m_preview = preview;
		setLayout(new BorderLayout());
		m_view = new SwingBuilderPanel("com/jeta/swingbuilder/gui/effects/solidProperties.frm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setController(new SolidViewController());
		updatePreview();
	}

	/**
	 * @return the property for this view
	 */
	public PaintProperty getPaintProperty() {
		return new PaintProperty(getSolidProperty());
	}

	public SolidProperty getSolidProperty() {
		m_prop.setColorProperty(m_view.getColorProperty(SolidViewNames.ID_COLOR_SELECTOR));
		return m_prop;
	}

	/**
	 * Sets the property for the view
	 */
	public void setPaintProperty(PaintProperty pp) {
		if (pp.getPaintDelegate() instanceof SolidProperty) {
			SolidProperty sp = (SolidProperty) pp.getPaintDelegate();
			m_prop.setValue(sp);
			m_view.setColorProperty(SolidViewNames.ID_COLOR_SELECTOR, sp.getColorProperty());
			updatePreview();
		}
	}

	public void updatePreview() {
		m_painter.setSolidProperty(getSolidProperty());
		m_preview.setBackgroundPainter(m_painter);
	}

	/**
	 * The controller for the SolidView class
	 */
	public class SolidViewController extends JETAController {
		public SolidViewController() {
			super(SolidView.this);
			assignAction(SolidViewNames.ID_COLOR_SELECTOR, new ColorChangedAction());
		}

		public class ColorChangedAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				updatePreview();
			}
		}
	}

}
