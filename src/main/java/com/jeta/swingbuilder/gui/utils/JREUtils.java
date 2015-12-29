package com.jeta.swingbuilder.gui.utils;

import java.security.AccessControlException;

/**
 * Utility class for determining which JRE version Abeille is running within.
 * 
 * @author Todd Viegut
 * @since Abeille 2.1 M1
 * @version 1.0, 08.28.2007
 */
public final class JREUtils {

	private JREUtils() {
	}

	public static boolean isJava3OrLater() {
		return validateJavaVersion(1.3D);
	}

	public static boolean isJava4OrLater() {
		return validateJavaVersion(1.3999999999999999D);
	}

	public static boolean isJava4Release2OrLater() {
		String javaVersion = getJavaVersion();
		String majorVersion = javaVersion.substring(0, 5);
		return majorVersion.compareTo("1.4.2") >= 0;
	}

	public static boolean isJava5OrLater() {
		return validateJavaVersion(1.5D);
	}

	public static boolean isJava6OrLater() {
		return validateJavaVersion(1.6000000000000001D);
	}

	public static boolean isJava7OrLater() {
		return validateJavaVersion(1.7D);
	}

	public static String getProperty(String key, String defaultValue) {
		try {
			return System.getProperty(key);
		} catch (AccessControlException ace) {
			return defaultValue;
		}
	}

	public static String getJavaVersion() {
		return getProperty("java.version", "1.4.2");
	}

	private static boolean validateJavaVersion(double targetVersion) {
		try {
			String fullVersion = getJavaVersion();
			String majorVersion = fullVersion.substring(0, 3);
			double d = Double.parseDouble(majorVersion);
			return d >= targetVersion;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
}
