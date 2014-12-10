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
import br.unb.mobileMedia.core.domain.VideoFormats;

public class ListAllFiles{

	private List<File> result;
	private File sdcard, extSdcard;
	private List<FileDetail> resultDetail;

	private static File DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private static File MUSIC_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);///mnt/sdcard/Music
	private static File DCIM_DIR =Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM);
	private static File PICTURE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	private static File MOVIES_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//	private static File EXTSDCARD_DIR = new File("/mnt/sdcard/extSdcard");
			
	public ListAllFiles(){	
		result = new ArrayList<File>();
		resultDetail = new ArrayList<FileDetail>();
		sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		extSdcard = new File("/mnt/extSdCard/");
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
	
	public List<FileDetail> getAllMovie() {
		
		try {
			listAllDirsMovie(sdcard);
			listAllDirsMovie(extSdcard);

		} catch (IOException e) {
			Log.i("IOException", e.getMessage());
		} catch (Exception e) {
			e.getStackTrace();
		}

		return resultDetail;

	}

	private void listAllDirsMovie(File file) throws IOException {
	
		if (!file.exists()) {
			throw new IOException("File " + file.getAbsolutePath() + " not exists.");
		} else if (!file.canRead()) {
			throw new IOException("Permission denied in: " + file.getAbsolutePath());
		} else {
			for (File temp : file.listFiles()) {
				if (temp.isDirectory()) {
					listAllDirsMovie(temp);
				} else {
					for (VideoFormats extensionAccepted : VideoFormats.values()) {
						if (temp.getAbsolutePath().endsWith(extensionAccepted.getFormatAsString()) && !temp.isHidden()) {
							resultDetail.add(new FileDetail(R.drawable.icon_audio, temp.getName(), "File Size: " + (temp.length() / 1000000) + "Mb", temp.getAbsolutePath()));
						}
					}
				}
			}
		}
	
		return;
	
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
