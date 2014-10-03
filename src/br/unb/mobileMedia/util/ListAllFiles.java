package br.unb.mobileMedia.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.FileChooser.FileDetail;
import br.unb.mobileMedia.core.domain.AudioFormats;

public class ListAllFiles {

	private File sdcard, extSdcard;
	private List<FileDetail> result;

	public ListAllFiles() {
		
		result = new ArrayList<FileDetail>();
		sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		extSdcard = new File("/mnt/extSdCard/");
	
	}

	
	public List<FileDetail> getAllMusic() {
		
		try {
			listAllDirs(sdcard);
			listAllDirs(extSdcard);

		} catch (IOException e) {
			Log.i("IOException", e.getMessage());
		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;

	}
	
	
	private void listAllDirs(File file) throws IOException {

		if (!file.exists()) {
			throw new IOException("File " + file.getAbsolutePath() + " not exists.");
		} else if (!file.canRead()) {
			throw new IOException("Permission denied in: " + file.getAbsolutePath());
		} else {
			for (File temp : file.listFiles()) {
				if (temp.isDirectory()) {
					listAllDirs(temp);
				} else {
					for (AudioFormats extensionAccepted : AudioFormats.values()) {
						if (temp.getAbsolutePath().endsWith(extensionAccepted.getFormatAsString()) && !temp.isHidden()) {
							Log.i("", temp.getAbsolutePath());
							result.add(new FileDetail(R.drawable.icon_audio, temp.getName(), "File Size: " + (temp.length() / 1000000) + "Mb",temp.getAbsolutePath()));
						}
					}
				}
			}
		}

		return;

	}

}
