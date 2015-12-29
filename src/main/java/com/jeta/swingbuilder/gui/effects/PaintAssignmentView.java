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
import java.awt.Dimension;
import java.util.HashMap;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormAccessor;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.ImageProperty;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.forms.store.properties.effects.GradientProperty;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.forms.store.properties.effects.RadialGradientProperty;
import com.jeta.forms.store.properties.effects.SolidProperty;
import com.jeta.forms.store.properties.effects.TextureProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;
import com.jeta.swingbuilder.gui.images.ImagePropertiesValidator;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Displays the fill effects view. Currently we support solid, gradient, and
 * texture fills for panels and cells.
 * 
 * @author Jeff Tassin
 */
public class PaintAssignmentView extends JETAPanel {
	/**
	 * The fillProperties form.
	 */
	private FormPanel m_view;

	/**
	 * The current fill type view: Solid, Gradient, Texture
	 */
	private PaintViewProxy m_current_view;

	/**
	 * A map of PaintViews keyed on their class. m_views<Class,PaintView>
	 */
	private HashMap m_views = new HashMap();

	/**
	 * The preview panel
	 */
	private GridView m_preview;

	/**
	 * ctor
	 */
	public PaintAssignmentView() {
		this(null);
	}

	/**
	 * ctor
	 */
	public PaintAssignmentView(PaintProperty fp) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/effects/paintProperties.frm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setController(new PaintAssignmentController(this));

		m_preview = (GridView) m_view.getComponentByName(PaintNames.ID_PREVIEW_PANEL);
		if (fp == null || fp.getPaintDelegate() == null) {
			loadView(NoFillView.class);
		}
		else {
			JETAProperty pp = fp.getPaintDelegate();
			if (pp instanceof TextureProperty) {
				loadView(TextureView.class);
			}
			else if (pp instanceof GradientProperty) {
				loadView(GradientView.class);
			}
			else if (pp instanceof RadialGradientProperty) {
				loadView(RadialView.class);
			}
			else if (pp instanceof SolidProperty) {
				loadView(SolidView.class);
			}
			else if (pp instanceof ImageProperty) {
				loadView(ImageFillView.class);
			}
			else {
				loadView(NoFillView.class);
			}
			m_current_view.setPaintProperty(fp);
		}
	}

	/**
	 * @return the preferred size for this panel
	 */
	public Dimension getPreferredSize() {
		return FormDesignerUtils.getWindowDimension(this, 350, 200);
	}

	/**
	 * @return the property for this view
	 */
	public PaintProperty getPaintProperty() {
		return m_current_view.getPaintProperty();
	}

	/**
	 * Loads the GradientView into this view
	 */
	void loadView(Class viewClass) {
		JETAController controller = getController();

		try {
			controller.enableEvents(false);
			FormAccessor form_access = m_view.getFormAccessor(PaintNames.ID_SETTINGS_PANEL);
			form_access.removeBean(PaintNames.ID_MAIN_VIEW);

			/*
			 * if ( m_current_view != null ) { FormPanel.removeFromParent(
			 * (java.awt.Component)m_current_view.getView() ); }
			 */

			PaintViewProxy fillview = (PaintViewProxy) m_views.get(viewClass);
			if (fillview == null) {
				if (viewClass == GradientView.class) {
					fillview = new PaintViewProxy(new GradientView(m_preview), null);
					m_view.setSelected(PaintNames.ID_LINEAR_GRADIENT_FILL, true);

				}
				else if (viewClass == TextureView.class) {
					fillview = new PaintViewProxy(new TextureView(m_preview), new ImagePropertiesValidator());
					m_view.setSelected(PaintNames.ID_TEXTURE_FILL, true);
				}
				else if (viewClass == SolidView.class) {
					fillview = new PaintViewProxy(new SolidView(m_preview), null);
					m_view.setSelected(PaintNames.ID_SOLID_FILL, true);
				}
				else if (viewClass == RadialView.class) {
					fillview = new PaintViewProxy(new RadialView(m_preview), null);
					m_view.setSelected(PaintNames.ID_RADIAL_GRADIENT_FILL, true);
				}
				else if (viewClass == ImageFillView.class) {
					fillview = new PaintViewProxy(new ImageFillView(m_preview), null);
					m_view.setSelected(PaintNames.ID_IMAGE_FILL, true);
				}
				else {
					fillview = new PaintViewProxy(new NoFillView(m_preview), null);
					m_view.setSelected(PaintNames.ID_NO_FILL, true);
				}
				m_views.put(viewClass, fillview);
			}

			m_current_view = fillview;
			java.awt.Component comp = (java.awt.Component) fillview.getView();
			comp.setName(PaintNames.ID_MAIN_VIEW);
			form_access.addBean(comp, new CellConstraints(2, 2));
			m_view.revalidate();

			revalidate();
			repaint();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			controller.enableEvents(true);
		}

	}

	/**
	 * This class associates a nested paint view with its validator.
	 * 
	 */
	private static class PaintViewProxy implements PaintView, JETARule {
		private PaintView m_view;
		private JETARule m_validator;

		/**
		 * ctor
		 */
		public PaintViewProxy(PaintView view, JETARule viewValidator) {
			m_view = view;
			m_validator = viewValidator;
		}

		public PaintView getView() {
			return m_view;
		}

		/**
		 * PaintView implementation
		 */
		public PaintProperty getPaintProperty() {
			return m_view.getPaintProperty();
		}

		/**
		 * PaintView implementation
		 */
		public void setPaintProperty(PaintProperty pp) {
			m_view.setPaintProperty(pp);
		}

		/**
		 * JETARule implementation
		 */
		public RuleResult check(Object[] params) {
			if (m_validator == null)
				return RuleResult.SUCCESS;
			else {
				params = new Object[] { m_view };
				return m_validator.check(params);
			}
		}
	}

}
