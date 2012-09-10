package br.unb.projetopositivo.mm.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {

	public static List<File> listFiles(File file, String extension) {
		List<File> list = new ArrayList<File>();
		if (file.exists()) {
			if (file.isDirectory()) {
				System.out.println(file.getPath());
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					list.addAll(listFiles(files[i], extension));
				}
			} else {
				if(file.getName().endsWith(extension)){
					list.add(file);
				}
			}
		}
		return list;
	}

}
