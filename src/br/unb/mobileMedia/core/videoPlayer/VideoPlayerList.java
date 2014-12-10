package br.unb.mobileMedia.core.videoPlayer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import br.unb.mobileMedia.core.FileChooser.FileDetail;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DefaultVideoListDAO;
import br.unb.mobileMedia.core.domain.Video;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.util.ListAllFiles;

public class VideoPlayerList implements MediaPlayer.OnCompletionListener {

	private static volatile VideoPlayerList uniqueInstance;

	private Context context;
	private MediaPlayer player;
	private List<Video> videoList;
	private boolean repeat = false;
	private boolean shuffle = false;
	public boolean isPlaying = false;
	private int currentMovieIndex = 0; 
	
	
	
	public VideoPlayerList() {
	}
	
	

	/**
	 * Default constructor expecting just the application context.
	 * @param context the application context.
	 * @throws URISyntaxException 
	 * @throws DBException 
	 * @throws UnsupportedEncodingException 
	 */
	private VideoPlayerList(Context context) throws UnsupportedEncodingException, DBException, URISyntaxException {
		
		this.context = context;

		player = new MediaPlayer();
		
		videoList = new ArrayList<Video>();
		
		DefaultVideoListDAO videosBank = new DefaultVideoListDAO(context);
		videoList = videosBank.getVideosBanco();
		

		player.setOnCompletionListener(this);
	}
	
	
	public void sincronizarTodosVideos() throws DBException{
		
		ListAllFiles listaDeVideos = new ListAllFiles();
	    List<FileDetail> lista;
	    
	    DefaultVideoListDAO includeVideoInBank = new DefaultVideoListDAO(context);
    	
	    lista = listaDeVideos.getAllMovie();
	    
	    for( FileDetail item : lista )  
	    {  
	    	
	    	includeVideoInBank.adicionaVideo(item.getName(),item.getPath());
	    }
		
	}

	/**
	 * A constructor that expects both application context and an array of
	 * video.
	 * @param context the application context.
	 * @param videoArray an array of musics that the user might choose to play
	 * @throws URISyntaxException 
	 * @throws DBException 
	 * @throws UnsupportedEncodingException 
	 */
	private VideoPlayerList(Context context, Video[] videoArray) throws UnsupportedEncodingException, DBException, URISyntaxException {
		this(context);

		if (videoArray == null) {
			videoList = new ArrayList<Video>();
		}

		for (Video video : videoArray) {
			videoList.add(video);
		}
	}

	public static VideoPlayerList getInstance(Context context, Video[] videoArray) throws UnsupportedEncodingException, DBException, URISyntaxException {
		if (uniqueInstance == null) {
			synchronized (VideoPlayerList.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new VideoPlayerList(context, videoArray);
				}
			}
		}
		return uniqueInstance;
	}

	public static VideoPlayerList getInstance(Context context) throws UnsupportedEncodingException, DBException, URISyntaxException {
		if (uniqueInstance == null) {
			synchronized (VideoPlayerList.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new VideoPlayerList(context);
				}
			}
		}
		return uniqueInstance;
	}


	public void play(int indexFile){
		
		try {
						
			player.reset();
			player.setDataSource(context, Uri.parse(videoList.get(indexFile).getURI().toString()));
			player.prepare();
			player.start();
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	public String getVideo(int index) {
		
		
		if((index>=0) && (videoList.size() >0) && (index < videoList.size())){
			
			Video uniqueVideo = videoList.get(index);	
			return uniqueVideo.getURI().toString();
			
		}else{
			
			return null;
		}
					

	}

	
	public void addVideo(Video newVideo) {
		videoList.add(newVideo);
	}

	/**
	 * 
	 * @return name of current music
	 */
	public String getTitleSong() {
		
	    String str1 = videoList.get(currentMovieIndex).getTitle();  
	    
		return str1.substring (0, str1.indexOf ("."));
	}

	public int getDuration() {
		return player.getDuration();
	}

	

	public boolean isPlaying() {
		return player.isPlaying();
	}
	

	/**
	 * @return the number of elements of the list.
	 */
	private int sizeOfVideoList() {
		return videoList.size();
	}
	
	/**
	 * @return a number random based in length of audioList how range.
	 */
	private int random(){
		return new Random().nextInt(sizeOfVideoList()-1);
	}



	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
	}

}
