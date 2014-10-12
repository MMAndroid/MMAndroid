package br.unb.mobileMedia.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import br.unb.mobileMedia.core.domain.AudioFormats;
import br.unb.mobileMedia.core.domain.AudioOld;

public class ListAllFiles {

	private File sdcard, extSdcard;
	private List<AudioOld> result;

	public ListAllFiles() {
		
		result = new ArrayList<AudioOld>();
		sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		extSdcard = new File("/mnt/extSdCard/");
	
	}

	
	public List<AudioOld> getAllMusic(){
		
		try {
			listAllDirs(sdcard);
			listAllDirs(extSdcard);
		} catch (IOException e) {
			Log.e(ListAllFiles.class.getCanonicalName(),
					e.getLocalizedMessage());
		} catch (Exception e) {
			Log.e(ListAllFiles.class.getCanonicalName(),
					e.getLocalizedMessage());
		}

		return result;

	}
	
	
	private void listAllDirs(File file) throws IOException, URISyntaxException {
		
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
							try{
								result.add(new AudioOld(null, temp.getName(), new URI(Uri.encode(temp.getPath()))));	
							}catch(URISyntaxException e){
								throw e;
							}
								
							
						}
					}
				}
			}
		}

		return;

	}

}
