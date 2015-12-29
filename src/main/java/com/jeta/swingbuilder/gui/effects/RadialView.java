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

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jeta.forms.components.colors.ColorSelector;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.gui.effects.RadialGradientPainter;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.forms.store.properties.effects.RadialGradientProperty;
import com.jeta.open.gui.framework.JETAPanel;

/**
 * @author Jeff Tassin
 */
public class RadialView extends JETAPanel implements PaintView {
	/**
	 * The fillProperties form.
	 */
	private FormPanel m_view;

	/**
	 * The preview for the gradient settings
	 */
	private GridView m_preview;

	/**
	 * Responsible for rendering the gradient in the parent view
	 */
	private RadialGradientPainter m_painter = new RadialGradientPainter();

	private RadialGradientProperty m_gp = new RadialGradientProperty();

	/**
	 * ctor
	 */
	public RadialView(GridView preview) {
		this(preview, null);
	}

	/**
	 * ctor
	 */
	public RadialView(GridView preview, PaintProperty pp) {
		m_preview = preview;
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/effects/radialGradientProperties.frm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JSpinner sp = (JSpinner) m_view.getSpinner(RadialNames.ID_MAGNITUDE_SPINNER);
		sp.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
		sp.setValue(new Integer(100));

		if (pp != null) {
			initialize((RadialGradientProperty) pp.getPaintDelegate());
		}

		setController(new RadialViewController(this));
	}

	/**
	 * Initializes the view
	 */
	private void initialize(RadialGradientProperty gp) {
		ColorSelector view = (ColorSelector) m_view.getComponentByName(RadialNames.ID_START_COLOR_SELECTOR);
		view.setColorProperty(gp.getStartColor());
		view = (ColorSelector) m_view.getComponentByName(RadialNames.ID_END_COLOR_SELECTOR);
		view.setColorProperty(gp.getEndColor());

		// setColorProperty( RadialNames.ID_START_COLOR, gp.getStartColor() );
		// setColorProperty( RadialNames.ID_END_COLOR, gp.getEndColor() );
		setPosition(gp.getPosition());
		setMagnitude(gp.getMagnitude());
	}

	public int getMagnitude() {
		JSpinner sp = (JSpinner) m_view.getSpinner(RadialNames.ID_MAGNITUDE_SPINNER);
		SpinnerNumberModel model = (SpinnerNumberModel) sp.getModel();
		Integer ival = (Integer) model.getValue();
		return ival.intValue();
	}

	public void setPosition(int pos) {
		String pos_name = "CENTER";
		if (pos == RadialGradientProperty.TOP_LEFT) {
			pos_name = "TOP_LEFT";
		}
		else if (pos == RadialGradientProperty.TOP_CENTER) {
			pos_name = "TOP_CENTER";
		}
		else if (pos == RadialGradientProperty.TOP_RIGHT) {
			pos_name = "TOP_RIGHT";
		}
		else if (pos == RadialGradientProperty.BOTTOM_LEFT) {
			pos_name = "BOTTOM_LEFT";
		}
		else if (pos == RadialGradientProperty.BOTTOM_CENTER) {
			pos_name = "BOTTOM_CENTER";
		}
		else if (pos == RadialGradientProperty.BOTTOM_RIGHT) {
			pos_name = "BOTTOM_RIGHT";
		}
		else if (pos == RadialGradientProperty.LEFT_CENTER) {
			pos_name = "LEFT_CENTER";
		}
		else if (pos == RadialGradientProperty.RIGHT_CENTER) {
			pos_name = "RIGHT_CENTER";
		}
		m_view.setSelectedItem(RadialNames.ID_POSITION_COMBO, pos_name);
	}

	public void setMagnitude(int mag) {
		JSpinner sp = (JSpinner) m_view.getSpinner(RadialNames.ID_MAGNITUDE_SPINNER);
		sp.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
		sp.setValue(new Integer(mag));
	}

	/**
	 * @return a painter object for this the properties in this view
	 */
	public Painter getPainter() {
		m_painter.setGradientProperty(getGradientProperty());
		return m_painter;
	}

	/**
	 * @return the
	 */
	public RadialGradientProperty getGradientProperty() {
		RadialGradientProperty rp = new RadialGradientProperty();
		rp.setPosition(getPosition());

		ColorSelector view = (ColorSelector) m_view.getComponentByName(RadialNames.ID_START_COLOR_SELECTOR);
		rp.setStartColor(view.getColorProperty());
		view = (ColorSelector) m_view.getComponentByName(RadialNames.ID_END_COLOR_SELECTOR);
		rp.setEndColor(view.getColorProperty());
		rp.setMagnitude(getMagnitude());
		return rp;
	}

	/**
	 * @return the
	 */
	public PaintProperty getPaintProperty() {
		return new PaintProperty(getGradientProperty());
	}

	/**
	 * @return the position of the gradient center
	 */
	private int getPosition() {
		String pos = (String) m_view.getSelectedItem(RadialNames.ID_POSITION_COMBO);
		if ("TOP_LEFT".equals(pos))
			return RadialGradientProperty.TOP_LEFT;
		if ("TOP_CENTER".equals(pos))
			return RadialGradientProperty.TOP_CENTER;
		if ("TOP_RIGHT".equals(pos))
			return RadialGradientProperty.TOP_RIGHT;

		if ("BOTTOM_LEFT".equals(pos))
			return RadialGradientProperty.BOTTOM_LEFT;
		if ("BOTTOM_CENTER".equals(pos))
			return RadialGradientProperty.BOTTOM_CENTER;
		if ("BOTTOM_RIGHT".equals(pos))
			return RadialGradientProperty.BOTTOM_RIGHT;

		if ("LEFT_CENTER".equals(pos))
			return RadialGradientProperty.LEFT_CENTER;
		if ("RIGHT_CENTER".equals(pos))
			return RadialGradientProperty.RIGHT_CENTER;

		return RadialGradientProperty.CENTER;
	}

	/**
	 * Sets the property for the view
	 */
	public void setPaintProperty(PaintProperty pp) {
		if (pp.getPaintDelegate() instanceof RadialGradientProperty) {
			RadialGradientProperty gp = (RadialGradientProperty) pp.getPaintDelegate();
			m_gp.setValue(gp);
			initialize(m_gp);
			updatePreview();
		}
	}

	/**
	 * Updates the preview view
	 */
	public void updatePreview() {
		m_preview.setBackgroundPainter(getPainter());
	}

}
