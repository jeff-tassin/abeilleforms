package com.jeta.swingbuilder.gui.utils;

import java.security.AccessControlException;

/**
 * Utility class for determining what OS Abeille is running on.
 * 
 * @author Todd Viegut
 * @since Abeille 2.1 M1
 * @version 1.0, 08.28.2007
 */
public final class OSUtils {

	private static boolean isMacClassic = false;
	private static boolean isMacOSX = false;
	//
	private static boolean isWindows;
	private static boolean isWindowsNTor2000 = false;
	private static boolean isWindowsXP = false;
	private static boolean isWindows2003 = false;
	private static boolean isClassicWindows = false;
	private static boolean isWindows95 = false;
	private static boolean isWindows98 = false;
	//
	private static boolean isLinux = false;
	private static boolean isSolaris = false;

	static {
		isWindows = false;
		String s = getProperty("os.name", "Windows XP");
		isWindows = s.indexOf("Windows") != -1;
		try {
			String s1 = getProperty("os.version", "5.0");
			Float float1 = Float.valueOf(s1);
			isClassicWindows = (double) float1.floatValue() <= 4D;
		} catch (NumberFormatException numberformatexception) {
			isClassicWindows = false;
		}
		if (s.indexOf("Windows XP") != -1 || s.indexOf("Windows NT") != -1 || s.indexOf("Windows 2000") != -1)
			isWindowsNTor2000 = true;
		if (s.indexOf("Windows XP") != -1)
			isWindowsXP = true;
		if (s.indexOf("Windows 2003") != -1) {
			isWindows2003 = true;
			isWindowsXP = true;
		}
		if (s.indexOf("Windows 95") != -1)
			isWindows95 = true;
		if (s.indexOf("Windows 98") != -1)
			isWindows98 = true;
		isSolaris = s.indexOf("Solaris") != -1 || s.indexOf("SunOS") != -1;
		isLinux = s.indexOf("Linux") != -1;
		if (s.startsWith("Mac OS"))
			if (s.endsWith("X"))
				isMacOSX = true;
			else
				isMacClassic = true;
	}

	private OSUtils() {
	}

	private static String getProperty(String key, String defaultValue) {
		try {
			return System.getProperty(key);
		} catch (AccessControlException ace) {
			return defaultValue;
		}
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isClassicWindows() {
		return isClassicWindows;
	}

	public static boolean isWindowsNTor2000() {
		return isWindowsNTor2000;
	}

	public static boolean isWindowsXP() {
		return isWindowsXP;
	}

	public static boolean isWindows95() {
		return isWindows95;
	}

	public static boolean isWindows98() {
		return isWindows98;
	}

	public static boolean isWindows2003() {
		return isWindows2003;
	}

	public static boolean isMacClassic() {
		return isMacClassic;
	}

	public static boolean isMacOSX() {
		return isMacOSX;
	}

	public static boolean isAnyMac() {
		return isMacClassic || isMacOSX;
	}

	public static boolean isSolaris() {
		return isSolaris;
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isUnix() {
		return isLinux || isSolaris;
	}
}
