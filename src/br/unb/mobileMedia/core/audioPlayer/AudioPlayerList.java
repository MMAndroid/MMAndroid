package br.unb.mobileMedia.core.audioPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
<<<<<<< HEAD
import br.unb.mobileMedia.core.domain.Audio;
=======
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.AudioOld;
import br.unb.mobileMedia.core.manager.Manager;
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696

public class AudioPlayerList implements MediaPlayer.OnCompletionListener {

	private static volatile AudioPlayerList uniqueInstance;

	private Context context;
	private MediaPlayer player;
<<<<<<< HEAD
	private List<Audio> audioList;
=======
	private List<AudioOld> audioList;
	private int current;
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696
	private boolean repeat = false;
	private boolean shuffle = false;
	private boolean isPlaying = false;
	private int currentSongIndex = 0; 
	
	
	
	private AudioPlayerList() {
	}

	/**
	 * Default constructor expecting just the application context.
	 * @param context the application context.
	 */
	private AudioPlayerList(Context context) {
		this.context = context;

		
		player = new MediaPlayer();
<<<<<<< HEAD
		
		audioList = new ArrayList<Audio>();
=======
		current = 0;
		audioList = new ArrayList<AudioOld>();
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696

		setRepeat(false);

		player.setOnCompletionListener(this);
	}

	/**
	 * A constructor that expects both application context and an array of
	 * audio.
	 * @param context the application context.
	 * @param audioArray an array of musics that the user might choose to play
	 */
	private AudioPlayerList(Context context, AudioOld[] audioArray) {
		this(context);

<<<<<<< HEAD
		if (audioArray == null) {
			audioList = new ArrayList<Audio>();
		}

		for (Audio audio : audioArray) {
=======
		if (audioArray == null){
			audioList = new ArrayList<AudioOld>();
		}

		for(AudioOld audio : audioArray)  {
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696
			audioList.add(audio);
		}
	}

<<<<<<< HEAD
	public static AudioPlayerList getInstance(Context context, Audio[] audioArray) {
		if (uniqueInstance == null) {
=======
	public static AudioPlayerList getInstance (Context context, AudioOld[] audioArray) {
		if (uniqueInstance == null){
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696
			synchronized (AudioPlayerList.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new AudioPlayerList(context, audioArray);
				}
			}
		}
		return uniqueInstance;
	}

	public static AudioPlayerList getInstance(Context context) {
		if (uniqueInstance == null) {
			synchronized (AudioPlayerList.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new AudioPlayerList(context);
				}
			}
		}
		return uniqueInstance;
	}

<<<<<<< HEAD

	public void play(int indexFile){
=======
	/**
	 * Return the number of elements of the list.
	 * @return the number of elements of the list.
	 */
	public int size() {
		return audioList.size();
	}

	/**
	 * Returns an iterator on the elements of the list. 
	 * @return an iterator on the elements of the list.
	 */
	public Iterator<AudioOld> iterator() {
		return audioList.iterator();
	}

	public int current() {
		return current;
	}

	/**
	 * Play the current audio on the audio play list.
	 */
	public void play() throws RuntimeException {
		if(current >= 0 && current < audioList.size()) {
			if (isPlaying) {
				return; 
			} else if (isPaused) {
				player.start();
				isPaused = false;
				isPlaying = true;
				return;
			}else {				
				AudioOld audio = audioList.get(current);
				try {
					player.setDataSource(context, Uri.parse(audio.getURI().toString()));
					player.prepare();
					player.start();
					isPlaying = true;
				}
				catch(Exception e) {
					Log.v(AudioPlayerList.class.getCanonicalName(), e.getMessage());
					throw new RuntimeException(e);
				}
			}
		} 
	}


	public void onCompletion(MediaPlayer mp) {
		//TODO: this is a poor example of exception handling.
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696
		try {
			
			Log.i("Player: ", audioList.get(indexFile).getURI().toString() );
			
			player.reset();
			player.setDataSource(context, Uri.parse(audioList.get(indexFile).getURI().toString()));
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

	public void onCompletion(MediaPlayer mp) {
		
		if(repeat){
			play(currentSongIndex);
		}else if(isShuffle()){
			currentSongIndex = random();
			play(currentSongIndex);
		}else{
			if(currentSongIndex < (audioList.size()-1)){
				play(currentSongIndex +1);
				currentSongIndex++;
			}else{
				play(0);
				currentSongIndex = 0;
			}
		}
	
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean Shuffle) {
		shuffle = Shuffle;
	}

	public void stop() {
		if (isPlaying) {
			player.stop();
			player.reset();
			isPlaying = false;
		}
	}

	public void killPLaylist() {
		audioList.removeAll(audioList);
	}

<<<<<<< HEAD
	public void newPlaylist(List<Audio> executionList) {
		audioList = executionList;
=======
	public void newPlaylist(AudioOld[] executionList) {
		for(AudioOld audio : executionList)  {
			audioList.add(audio);
		}
		current = 0;
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696
	}

	public void playPause() {
		if(isPlaying()){
			player.pause();
		}else{
			player.start();
		}
	}

	public void nextTrack() {
		if(currentSongIndex < (audioList.size() -1)){
			if(isShuffle()){
				currentSongIndex = random();
			}else{
				currentSongIndex++;
			}
			
			play(currentSongIndex);
			
		}else{
			currentSongIndex = 0;
			play(currentSongIndex);
		}
	}

	public void previousTrack() {
		if(currentSongIndex > 0){
			
			if(isShuffle()){
				currentSongIndex = random();
			}else{
				currentSongIndex--;
			}
			
			currentSongIndex--;
			play(currentSongIndex);
		}else{
			currentSongIndex = random();
			play(currentSongIndex);
		}
	}
<<<<<<< HEAD

	public void reset() {
		player.reset();
	}

	public void addMusic(Audio newMusic) {
=======
	
	public void addMusic (AudioOld newMusic) {
>>>>>>> decd2c463fec42b8b5ab27b6ec760833b7bd1696
		audioList.add(newMusic);
	}

	/**
	 * 
	 * @return name of current music
	 */
	public String getTitleSong() {
		
	    String str1 = audioList.get(currentSongIndex).getTitle();  
	    
		return str1.substring (0, str1.indexOf ("."));
	}

	public int getDuration() {
		return player.getDuration();
	}

	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	public void seekTo(int currentPosition) {
		player.seekTo(currentPosition);
	}

	public boolean isPlaying() {
		return player.isPlaying();
	}
	
	

	/**
	 * @return the number of elements of the list.
	 */
	private int sizeOfAudioList() {
		return audioList.size();
	}
	
	/**
	 * @return a number random based in length of audioList how range.
	 */
	private int random(){
		return new Random().nextInt(sizeOfAudioList()-1);
	}

}
