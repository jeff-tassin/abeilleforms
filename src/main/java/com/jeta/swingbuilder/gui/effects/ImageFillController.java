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

import com.jeta.open.gui.framework.DelegateAction;
import com.jeta.swingbuilder.gui.images.ImagePropertiesController;
import com.jeta.swingbuilder.gui.images.ImagePropertiesNames;

public class ImageFillController extends ImagePropertiesController {
	private ImageFillView m_view;

	public ImageFillController(ImageFillView view) {
		super(view);
		m_view = view;
		assignAction(ImageFillNames.ID_VERTICAL_ALIGNMENT, new UpdatePreviewAction());
		assignAction(ImageFillNames.ID_HORIZONTAL_ALIGNMENT, new UpdatePreviewAction());
		assignAction(ImagePropertiesNames.ID_FILE_BUTTON, new FileDelegateAction());
	}

	public class FileDelegateAction extends DelegateAction {
		public FileDelegateAction() {
			super(getAction(ImagePropertiesNames.ID_FILE_BUTTON));
		}

		public void actionPerformed(ActionEvent evt) {
			super.actionPerformed(evt);
			m_view.updatePreview();
		}
	}

	public class UpdatePreviewAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.updatePreview();
		}
	}
}
