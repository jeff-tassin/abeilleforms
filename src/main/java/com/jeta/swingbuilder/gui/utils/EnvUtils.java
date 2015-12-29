package com.jeta.swingbuilder.gui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Utility class utilized for backwards compatibility when handling OS
 * environment variables.
 * 
 * @author Todd Viegut
 * @since Abeille 2.1 M1
 * @version 1.0, 08.28.2007
 */
public class EnvUtils {

	private static EnvUtils INSTANCE = new EnvUtils();
	private boolean isLoaded;
	private Map vars;

	private EnvUtils() {
		load();
	}

	public static synchronized EnvUtils getInstance() {
		return INSTANCE;
	}

	public void refresh() {
		vars = null;
		isLoaded = false;
		load();
	}

	public Map getEnvVars() {
		if (!isLoaded)
			load();
		return vars;
	}

	public String getEnvVar(String name) {
		return (String) vars.get(name);
	}

	public String[] getEnvVarNames() {
		return (String[]) (String[]) (new TreeSet(vars.keySet())).toArray(new String[vars.size()]);
	}

	private void load() {
		try {
			if (!JREUtils.isJava5OrLater()) {
				// Mac, Linux, Solaris, etc. should be covered by this default
				// command...
				String command = "env";
				if (OSUtils.isWindows())
					command = System.getProperty("os.name").toLowerCase().startsWith("windows 9") ? "command.com /vars set" : "cmd.exe /vars set";
				vars = new HashMap();
				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec(command);
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = bufferedreader.readLine()) != null) {
					int i = line.indexOf('=');
					String s2 = line.substring(0, i);
					String s3 = line.substring(i + 1);
					vars.put(s2, s3);
				}
			} else {
				try {
					vars = (Map) System.getenv();
				} catch (Exception exception) {
				}
			}
			isLoaded = true;
		} catch (IOException ioexception) {
			isLoaded = false;
		}
	}
}