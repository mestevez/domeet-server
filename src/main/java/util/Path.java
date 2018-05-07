package util;


import java.io.File;
import java.util.UUID;

public class Path {

	private static String ROOTPATH;

	public static String getRootPath() {
		if (ROOTPATH == null) {
			File rootDir = null;
			File testDir = new File(System.getProperty("user.dir"));
			while (rootDir == null && testDir.exists() && testDir.isDirectory()) {
				File[] listFiles = testDir.listFiles();
				for (int i = 0; i < listFiles.length && rootDir == null; i++) {
					if (listFiles[i].getName().equals("build.gradle"))
						rootDir = testDir;
				}
				if (rootDir == null)
					testDir = testDir.getParentFile();
			}
			ROOTPATH = rootDir.getAbsolutePath();
		}

		return ROOTPATH;
	}

	public static String getConfPath(String relativePath) {
		return getRootPath() + "/conf/" + relativePath;
	}

	public static String getTempPathFile(String dir, String fileName, String extension) {
		File directory = new File(getRootPath() + "/build/tmp/" + dir);
		if (!directory.exists())
			directory.mkdir();

		return getRootPath() + "/build/tmp/" + dir + "/" + fileName + "." + extension;
	}

	public static String getTempPathFile(String dir, String extension) {
		return getTempPathFile(dir, UUID.randomUUID().toString(), extension);
	}
}
