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
import com.jeta.forms.gui.effects.LinearGradientPainter;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.effects.GradientProperty;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;

/**
 * @author Jeff Tassin
 */
public class GradientView extends JETAPanel implements PaintView {
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
	private LinearGradientPainter m_painter = new LinearGradientPainter();

	private GradientProperty m_gp = new GradientProperty();

	/**
	 * ctor
	 */
	public GradientView(GridView preview) {
		this(preview, null);
	}

	/**
	 * ctor
	 */
	public GradientView(GridView preview, PaintProperty gp) {
		m_preview = preview;
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/effects/gradientProperties.frm");

		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JSpinner sp = (JSpinner) m_view.getSpinner(GradientNames.ID_MAGNITUDE_SPINNER);
		sp.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
		sp.setValue(new Integer(100));

		if (gp != null) {
			initialize((GradientProperty) gp.getPaintDelegate());
		}
		setController(new GradientViewController(this));
	}

	/**
	 * Initializes the view
	 */
	private void initialize(GradientProperty gp) {
		JETAController controller = getController();
		try {
			controller.enableEvents(false);
			ColorSelector view = (ColorSelector) m_view.getComponentByName(GradientNames.ID_START_COLOR_SELECTOR);
			view.setColorProperty(gp.getStartColor());
			view = (ColorSelector) m_view.getComponentByName(GradientNames.ID_END_COLOR_SELECTOR);
			view.setColorProperty(gp.getEndColor());
			setDirection(gp.getDirection());
			setMagnitude(gp.getMagnitude());
		} finally {
			controller.enableEvents(true);
		}
	}

	/**
	 * @return the selection gradient direction (e.g.
	 *         GradientProperty.HORIZONTAL, VERTICAL, ... )
	 */
	public int getDirection() {
		String sval = (String) m_view.getSelectedItem(GradientNames.ID_DIRECTION_COMBO);
		if ("TOP_BOTTOM".equals(sval))
			return GradientProperty.TOP_BOTTOM;
		else if ("BOTTOM_TOP".equals(sval))
			return GradientProperty.BOTTOM_TOP;
		else if ("LEFT_RIGHT".equals(sval))
			return GradientProperty.LEFT_RIGHT;
		else if ("RIGHT_LEFT".equals(sval))
			return GradientProperty.RIGHT_LEFT;
		else if ("UP_RIGHT".equals(sval))
			return GradientProperty.UP_RIGHT;
		else if ("UP_LEFT".equals(sval))
			return GradientProperty.UP_LEFT;
		else if ("DOWN_RIGHT".equals(sval))
			return GradientProperty.DOWN_RIGHT;
		else if ("DOWN_LEFT".equals(sval))
			return GradientProperty.DOWN_LEFT;
		else
			return GradientProperty.TOP_BOTTOM;
	}

	public float getMagnitude() {
		SpinnerNumberModel spm = (SpinnerNumberModel) m_view.getSpinner(GradientNames.ID_MAGNITUDE_SPINNER).getModel();
		Integer ival = (Integer) spm.getValue();
		float mag = ((float) ival.intValue()) / 100.0f;
		return mag;
	}

	public void setMagnitude(float mag) {
		SpinnerNumberModel spm = (SpinnerNumberModel) m_view.getSpinner(GradientNames.ID_MAGNITUDE_SPINNER).getModel();
		spm.setValue(new Integer((int) (mag * 100.0f)));
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
	public GradientProperty getGradientProperty() {
		ColorSelector view = (ColorSelector) m_view.getComponentByName(GradientNames.ID_START_COLOR_SELECTOR);
		GradientProperty gp = new GradientProperty();
		gp.setStartColor(view.getColorProperty());
		view = (ColorSelector) m_view.getComponentByName(GradientNames.ID_END_COLOR_SELECTOR);
		gp.setEndColor(view.getColorProperty());
		gp.setDirection(getDirection());
		gp.setMagnitude(getMagnitude());
		return gp;
	}

	/**
	 * @return the
	 */
	public PaintProperty getPaintProperty() {
		return new PaintProperty(getGradientProperty());
	}

	/**
	 * Sets the property for the view
	 */
	public void setPaintProperty(PaintProperty pp) {
		if (pp.getPaintDelegate() instanceof GradientProperty) {
			GradientProperty gp = (GradientProperty) pp.getPaintDelegate();
			m_gp.setValue(gp);
			initialize(m_gp);
			updatePreview();
		}
	}

	/**
	 * Sets the gradient direction in the direction combo
	 */
	public void setDirection(int dir) {
		if (dir == GradientProperty.TOP_BOTTOM)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "TOP_BOTTOM");
		else if (dir == GradientProperty.BOTTOM_TOP)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "BOTTOM_TOP");
		else if (dir == GradientProperty.LEFT_RIGHT)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "LEFT_RIGHT");
		else if (dir == GradientProperty.RIGHT_LEFT)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "RIGHT_LEFT");
		else if (dir == GradientProperty.UP_RIGHT)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "UP_RIGHT");
		else if (dir == GradientProperty.UP_LEFT)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "UP_LEFT");
		else if (dir == GradientProperty.DOWN_RIGHT)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "DOWN_RIGHT");
		else if (dir == GradientProperty.DOWN_LEFT)
			m_view.setSelectedItem(GradientNames.ID_DIRECTION_COMBO, "DOWN_LEFT");
	}

	/**
	 * Updates the preview view
	 */
	public void updatePreview() {
		m_preview.setBackgroundPainter(getPainter());
	}

}
