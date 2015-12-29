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

import java.awt.Container;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.effects.ImagePainter;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.ImageProperty;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.swingbuilder.gui.images.ImagePropertiesView;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * @author Jeff Tassin
 */
public class ImageFillView extends ImagePropertiesView implements PaintView {
	/**
	 * The preview for the gradient settings
	 */
	private GridView m_preview;

	/**
	 * The texture property
	 */
	private ImageProperty m_image_prop = new ImageProperty();
	private ImagePainter m_painter = new ImagePainter();

	/**
	 * ctor
	 */
	public ImageFillView(GridView preview) {
		this(preview, null);
	}

	/**
	 * ctor
	 */
	public ImageFillView(GridView preview, PaintProperty pp) {
		super("com/jeta/swingbuilder/gui/images/imageProperties.frm", null);
		FormPanel view = getView();
		Container form = view.getFormContainer();
		FormLayout layout = (FormLayout) form.getLayout();
		layout.appendRow(new RowSpec("2dlu"));
		layout.appendRow(new RowSpec("pref"));
		CellConstraints cc = new CellConstraints();

		JComboBox hbox = new JComboBox(new Object[] { "LEFT", "CENTER", "RIGHT" });
		hbox.setName(ImageFillNames.ID_HORIZONTAL_ALIGNMENT);
		form.add(new JLabel("Horizontal Alignment"), cc.xy(1, layout.getRowCount()));
		form.add(hbox, cc.xy(3, layout.getRowCount()));

		layout.appendRow(new RowSpec("2dlu"));
		layout.appendRow(new RowSpec("pref"));

		JComboBox vbox = new JComboBox(new Object[] { "TOP", "CENTER", "BOTTOM" });
		vbox.setName(ImageFillNames.ID_VERTICAL_ALIGNMENT);
		form.add(new JLabel("Vertical Alignment"), cc.xy(1, layout.getRowCount()));
		form.add(vbox, cc.xy(3, layout.getRowCount()));

		m_preview = preview;
		setController(new ImageFillController(this));
		if (pp != null) {
			setPaintProperty(pp);
		}
	}

	/**
	 * @return the property for this view
	 */
	public PaintProperty getPaintProperty() {
		return new PaintProperty(getImageProperty());
	}

	public ImageProperty getImageProperty() {
		m_image_prop.setValue(getIconProperty());
		m_image_prop.setVerticalAlignment(getVerticalAlignment());
		m_image_prop.setHorizontalAlignment(getHorizontalAlignment());
		return m_image_prop;
	}

	public int getHorizontalAlignment() {
		String item = (String) getSelectedItem(ImageFillNames.ID_HORIZONTAL_ALIGNMENT);
		if ("CENTER".equals(item))
			return ImageProperty.CENTER;
		else if ("RIGHT".equals(item))
			return ImageProperty.RIGHT;
		else
			return ImageProperty.LEFT;
	}

	public int getVerticalAlignment() {
		String item = (String) getSelectedItem(ImageFillNames.ID_VERTICAL_ALIGNMENT);
		System.out.println("ImageFillView.getVerticalAlignment: " + item);
		if ("CENTER".equals(item))
			return ImageProperty.CENTER;
		else if ("BOTTOM".equals(item))
			return ImageProperty.BOTTOM;
		else
			return ImageProperty.TOP;
	}

	/**
	 * Sets the property for the view
	 */
	public void setPaintProperty(PaintProperty pp) {
		try {
			getController().enableEvents(false);
			if (pp.getPaintDelegate() instanceof ImageProperty) {
				ImageProperty tp = (ImageProperty) pp.getPaintDelegate();
				m_image_prop.setValue(tp);
				setIconProperty(tp);
				updatePreview();
			}
		} finally {
			getController().enableEvents(true);
		}
	}

	public void updatePreview() {
		ImageProperty iprop = getImageProperty();
		m_painter.setIcon(iprop);
		m_painter.setHorizontalAlignment(iprop.getHorizontalAlignment());
		m_painter.setVerticalAlignment(iprop.getVerticalAlignment());
		m_preview.setBackgroundPainter(m_painter);
	}

}
