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

package com.jeta.swingbuilder.store;

import java.io.Externalizable;
import java.io.IOException;
import java.util.HashMap;

import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.common.ComponentNames;
import com.jeta.swingbuilder.interfaces.app.ObjectStore;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * Model for code generation options for a form.
 * 
 * @author Jeff Tassin
 */
public class CodeModel implements Externalizable {
	static final long serialVersionUID = 5327987415154522473L;

	public static final String ID_CODEGEN_OPTIONS_CACHE = "code.options.cache";
	public static final String ID_MEMBER_PREFIX = "code.member.prefix";
	public static final String ID_INCLUDE_MAIN = "code.include.main";
	public static final String ID_INCLUDE_NONSTANDARD = "code.include.nonstandard.components";

	private String m_form_id;

	/**
	 * verion of this class
	 */
	public static final int VERSION = 1;

	/**
	 * attibutes local to a form
	 */
	private String m_package;
	private String m_classname = "MyForm";

	/**
	 * global attibutes
	 */
	private transient String m_member_prefix = "m_";
	private transient boolean m_include_main = true;
	private transient boolean m_include_nonstandard = true;

	public CodeModel() {
		loadGlobals();
	}

	private CodeModel(FormMemento fm) {
		m_form_id = fm.getId();
		loadGlobals();
	}

	public static CodeModel createInstance(FormMemento fm) {
		try {
			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
			HashMap cgen_options = (HashMap) os.load(ID_CODEGEN_OPTIONS_CACHE);

			CodeModel model = null;
			if (cgen_options != null) {
				model = (CodeModel) cgen_options.get(fm.getId());
			}

			if (model == null)
				model = new CodeModel(fm);
			model.loadGlobals();
			return model;
		} catch (Exception e) {
			FormsLogger.severe(e);
			return new CodeModel(fm);
		}
	}

	public static void save(CodeModel model) {
		TSUserPropertiesUtils.setString(ID_MEMBER_PREFIX, model.getMemberPrefix());
		TSUserPropertiesUtils.setBoolean(ID_INCLUDE_MAIN, model.isIncludeMain());
		TSUserPropertiesUtils.setBoolean(ID_INCLUDE_NONSTANDARD, model.isIncludeNonStandard());
		try {
			ObjectStore os = (ObjectStore) JETARegistry.lookup(ComponentNames.APPLICATION_STATE_STORE);
			HashMap cgen_options = (HashMap) os.load(ID_CODEGEN_OPTIONS_CACHE);
			if (cgen_options == null) {
				cgen_options = new HashMap();
			}
			cgen_options.put(model.getFormId(), model);
			os.store(ID_CODEGEN_OPTIONS_CACHE, cgen_options);
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

	public String getFormId() {
		return m_form_id;
	}

	public String getPackage() {
		return m_package;
	}

	public String getClassName() {
		return m_classname;
	}

	public String getMemberPrefix() {
		return m_member_prefix;
	}

	public boolean isIncludeMain() {
		return m_include_main;
	}

	public boolean isIncludeNonStandard() {
		return m_include_nonstandard;
	}

	private void loadGlobals() {
		m_member_prefix = TSUserPropertiesUtils.getString(ID_MEMBER_PREFIX, "m_");
		m_include_main = TSUserPropertiesUtils.getBoolean(ID_INCLUDE_MAIN, true);
		m_include_nonstandard = TSUserPropertiesUtils.getBoolean(ID_INCLUDE_NONSTANDARD, true);
	}

	public void setPackage(String pkg) {
		m_package = pkg;
	}

	public void setClassName(String cname) {
		m_classname = cname;
	}

	public void setMemberPrefix(String prefix) {
		m_member_prefix = prefix;
	}

	public void setIncludeMain(boolean inc) {
		m_include_main = inc;
	}

	public void setIncludeNonStandard(boolean inc) {
		m_include_nonstandard = inc;
	}

	/**
	 * Externalizable Implementation
	 */
	public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readInt();
		m_package = (String) in.readObject();
		m_classname = (String) in.readObject();
		m_form_id = (String) in.readObject();
	}

	/**
	 * Externalizable Implementation
	 */
	public void writeExternal(java.io.ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeObject(m_package);
		out.writeObject(m_classname);
		out.writeObject(m_form_id);
	}

}
