package br.unb.mobileMedia.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An utility class for manipulating files.
 * 
 * @author Paula Fernandes - paulag6@gmail.com
 */
public class FileUtility {

	/**
	 * List all files recursively, starting from the <b>file</b> directory and
	 * considering that the file has the <b>extension</b>.
	 * 
	 * @param file
	 *            a root directory.
	 * @param extension
	 *            that is used as a filter
	 * 
	 * @return A list of files with the <b>extension</b> within the <b>file</b>
	 *         direcotry.
	 */
	public static List<File> listFiles(File file, String extension) {
		List<File> list = new ArrayList<File>();
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					list.addAll(listFiles(files[i], extension));
				}
			} else {
				if (file.getName().endsWith(extension)) {
					list.add(file);
				}
			}
		}
		return list;
	}

}
