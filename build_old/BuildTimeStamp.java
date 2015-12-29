import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Properties;

/**
 * This class generates the file com.jeta.abeille.commmon.Abeille.java It is
 * used during the build process to create the necessary time stamps, version,
 * and build number for abeille. This class is invoked by the build process.
 * 
 * @author Jeff Tassin
 */
public class BuildTimeStamp {
	/**
	 * The main entry point
	 */
	public static void main(String[] args) {
		try {
			Properties props = System.getProperties();

			String path = props.getProperty("JETA.SRC.PATH");
			String buildnum = props.getProperty("JETA.BUILD.NUMBER");
			String majorver = props.getProperty("JETA.MAJOR.VERSION");
			String minorver = props.getProperty("JETA.MINOR.VERSION");
			String subminorver = props.getProperty("JETA.SUBMINOR.VERSION");
			String milestone = props.getProperty("JETA.MILESTONE");

			StringBuffer pathbuff = new StringBuffer();
			pathbuff.append(path);
			pathbuff.append(File.separatorChar + "com");
			pathbuff.append(File.separatorChar + "jeta");
			pathbuff.append(File.separatorChar + "forms");
			pathbuff.append(File.separatorChar + "support");

			path = pathbuff.toString();
			File dir = new File(path);
			File f = new File(path + File.separatorChar + "AbeilleForms.txt");
			if (dir.exists() && f.exists()) {
				generateBuildStamp(path, buildnum, majorver, minorver, subminorver, milestone);
			}
			else {
				System.out.println("********* ERROR ***** BuildTimeStamp unable to location AbeilleForms.txt file: " + path);
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		// don't exit here because this will cause Ant to exit
	}

	public static void generateBuildStamp(String path, String buildNum, String majorVer, String minorVer, String subMinorVer, String milestone)
			throws IOException {

		StringBuffer verbuff = new StringBuffer();
		verbuff.append(majorVer);
		verbuff.append(".");
		verbuff.append(minorVer);
		verbuff.append(".");
		verbuff.append(subMinorVer);
		if ((milestone != null) && (!"".equals(milestone))) {
			verbuff.append(" ").append(milestone);
		}

		System.out.println("BuildTimeStamp.running  buildnumber = " + buildNum + " version = " + verbuff);

		File f = new File(path + File.separatorChar + "AbeilleForms.txt");

		StringBuffer buff = new StringBuffer();

		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		while (true) {
			String str = br.readLine();
			if (str == null)
				break;

			buff.append(str);
			buff.append("\n");
		}

		buff.append("\n");
		buff.append("     public static int MAJOR_VERSION = ");
		buff.append(majorVer);
		buff.append(";\n");

		buff.append("     public static int MINOR_VERSION = ");
		buff.append(minorVer);
		buff.append(";\n");

		buff.append("     public static int SUBMINOR_VERSION = ");
		buff.append(subMinorVer);
		buff.append(";\n");

		buff.append("     public static String MILESTONE = ");
		buff.append("\"").append((milestone != null && !"".equals(milestone) ? milestone : "")).append("\"");
		buff.append(";\n");

		buff.append("     public static int BUILD_NUMBER = ");
		buff.append(buildNum);
		buff.append(";\n");

		String dateformat = "MM-dd-yyyy HH:mm:ss";
		buff.append("     public static String DATE_FORMAT = \"");
		buff.append(dateformat);
		buff.append("\";\n");

		SimpleDateFormat format = new SimpleDateFormat(dateformat);
		Calendar c = Calendar.getInstance();
		String date = format.format(c.getTime());

		buff.append("     public static String BUILD_DATE = \"");
		buff.append(date);
		buff.append("\";\n");

		buff.append("\n\n}");

		String outfile = path + File.separatorChar + "AbeilleForms.java";

		FileOutputStream fos = new FileOutputStream(outfile);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(buff.toString());
		bw.close();
	}
}
