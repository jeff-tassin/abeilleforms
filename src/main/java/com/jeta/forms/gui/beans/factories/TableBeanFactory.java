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

package com.jeta.forms.gui.beans.factories;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;

/**
 * Factory for instantiating a JTable bean.
 * 
 * @author Jeff Tassin
 */
public class TableBeanFactory extends JComponentBeanFactory {
	public TableBeanFactory() {
		super(JTable.class);
		setScrollable(true);
	}

	public JETABean createBean(String compName, boolean instantiateBean, boolean setDefaults) throws FormException {
		JETABean jbean = super.createBean(compName, instantiateBean, setDefaults);
		Component comp = jbean.getDelegate();
		if (comp instanceof JTable) {
			JTable table = (JTable) comp;
			Object model = table.getModel();
			/**
			 * Add a few default columns and rows to give the table a little
			 * more identity on the form. Do this only if this is not a
			 * specialized type of JTable where the table model might be set in
			 * the derived class.
			 */
			if (model instanceof DefaultTableModel) {
				DefaultTableModel tmodel = (DefaultTableModel) model;
				if (tmodel.getColumnCount() == 0) {
					tmodel.addColumn("A");
					tmodel.addColumn("B");
					tmodel.addRow(new Object[] { "", "" });
					tmodel.addRow(new Object[] { "", "" });
				}
			}
		}
		return jbean;
	}
}
