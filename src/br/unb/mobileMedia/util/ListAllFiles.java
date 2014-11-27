package br.unb.mobileMedia.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import br.unb.mobileMedia.core.domain.AudioFormats;

public class ListAllFiles{

	private List<File> result;

	private static File DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private static File MUSIC_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);///mnt/sdcard/Music
	private static File DCIM_DIR =Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM);
	private static File PICTURE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	private static File MOVIES_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//	private static File EXTSDCARD_DIR = new File("/mnt/sdcard/extSdcard");
			
	public ListAllFiles(){	
		result = new ArrayList<File>();
	}


	
	public List<File> getAllMusic(){
		
		try {
			
//			Log.e("Path", EXTSDCARD_DIR.getAbsolutePath());
			
			listAllDirs(DOWNLOAD_DIR);
			listAllDirs(MUSIC_DIR);
			listAllDirs(DCIM_DIR);
			listAllDirs(PICTURE_DIR);
			listAllDirs(MOVIES_DIR);
//			listAllDirs(EXTSDCARD_DIR);
			
		} catch (IOException e) {
			Log.e(ListAllFiles.class.getCanonicalName(),
					e.getLocalizedMessage());
		} catch (Exception e) {
			Log.e(ListAllFiles.class.getCanonicalName(),
					e.getLocalizedMessage());
		}


		Log.i("Music Found:", ""+result.size());
		
		return result;
	}
	
	
	private void listAllDirs(File file) throws IOException, Exception {
		
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
								result.add(new File(temp.getAbsolutePath()));								
							}catch(Exception e){
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
