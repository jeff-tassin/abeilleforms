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

import com.jeta.forms.gui.effects.TexturePainter;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.forms.store.properties.effects.TextureProperty;
import com.jeta.swingbuilder.gui.images.ImagePropertiesController;
import com.jeta.swingbuilder.gui.images.ImagePropertiesNames;
import com.jeta.swingbuilder.gui.images.ImagePropertiesView;

/**
 * @author Jeff Tassin
 */
public class TextureView extends ImagePropertiesView implements PaintView {
	/**
	 * The preview for the gradient settings
	 */
	private GridView m_preview;

	/**
	 * The texture property
	 */
	private TextureProperty m_texture_prop = new TextureProperty();
	private TexturePainter m_painter = new TexturePainter();

	/**
	 * ctor
	 */
	public TextureView(GridView preview) {
		this(preview, null);
	}

	/**
	 * ctor
	 */
	public TextureView(GridView preview, PaintProperty pp) {
		super("com/jeta/swingbuilder/gui/images/imageProperties.frm", null);
		m_preview = preview;
		setController(new TextureViewController());
		if (pp != null) {
			initialize((TextureProperty) pp.getPaintDelegate());
		}
	}

	/**
	 * @return the property for this view
	 */
	public PaintProperty getPaintProperty() {
		return new PaintProperty(getTextureProperty());
	}

	public TextureProperty getTextureProperty() {
		m_texture_prop.setIconProperty(getIconProperty());
		return m_texture_prop;
	}

	/**
	 * Initializes the view
	 */
	public void initialize(TextureProperty tp) {
		if (tp != null) {
			setIconProperty(tp.getIconProperty());
			updatePreview();
		}
	}

	/**
	 * Sets the property for the view
	 */
	public void setPaintProperty(PaintProperty pp) {
		if (pp.getPaintDelegate() instanceof TextureProperty) {
			TextureProperty tp = (TextureProperty) pp.getPaintDelegate();
			m_texture_prop.setValue(tp);
			initialize(m_texture_prop);
			updatePreview();
		}
	}

	public void updatePreview() {
		m_painter.setTextureProperty(getTextureProperty());
		m_preview.setBackgroundPainter(m_painter);
	}

	public class TextureViewController extends ImagePropertiesController {
		private ActionListener m_delegate;

		public TextureViewController() {
			super(TextureView.this);
			m_delegate = getAction(ImagePropertiesNames.ID_FILE_BUTTON);
			assignAction(ImagePropertiesNames.ID_FILE_BUTTON, new FileAction());
		}

		public class FileAction implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				if (m_delegate != null)
					m_delegate.actionPerformed(evt);
				updatePreview();
			}
		}
	}

}
