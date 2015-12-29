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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.open.gui.framework.JETAController;

public class PaintAssignmentController extends JETAController {
	private PaintAssignmentView m_view;

	public PaintAssignmentController(PaintAssignmentView view) {
		super(view);
		m_view = view;

		assignAction(PaintNames.ID_SOLID_FILL, new FillAction(SolidView.class));
		assignAction(PaintNames.ID_TEXTURE_FILL, new FillAction(TextureView.class));
		assignAction(PaintNames.ID_LINEAR_GRADIENT_FILL, new FillAction(GradientView.class));
		assignAction(PaintNames.ID_RADIAL_GRADIENT_FILL, new FillAction(RadialView.class));
		assignAction(PaintNames.ID_IMAGE_FILL, new FillAction(ImageFillView.class));
		assignAction(PaintNames.ID_NO_FILL, new FillAction(NoFillView.class));
	}

	public class FillAction implements ActionListener {
		private Class m_view_class;

		public FillAction(Class viewClass) {
			m_view_class = viewClass;
		}

		public void actionPerformed(ActionEvent evt) {
			m_view.loadView(m_view_class);
		}
	}
}
