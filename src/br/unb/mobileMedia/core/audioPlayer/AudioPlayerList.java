package br.unb.mobileMedia.core.audioPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.manager.Manager;

public class AudioPlayerList implements MediaPlayer.OnCompletionListener {

	private static volatile AudioPlayerList uniqueInstance;

	private Context context;
	private MediaPlayer player;
	private List<Audio> audioList;
	private int current;
	private boolean repeat = false;
	private boolean shuffle = false;
	private boolean isPlaying = false;
	private boolean isPaused = false;

	private AudioPlayerList () {}

	/**
	 * Default constructor expecting just the application context.
	 * @param context the application context.
	 */
	private AudioPlayerList(Context context) {
		this.context = context;
		player = new MediaPlayer();
		current = 0;
		audioList = new ArrayList<Audio>();

		setRepeat(false);

		player.setOnCompletionListener(this);
	}

	/**
	 * A constructor that expects both application context and an array of audio.
	 * @param context the application context.
	 * @param audioArray an array of musics that the user might choose to play
	 */
	private AudioPlayerList(Context context, Audio[] audioArray) {
		this(context);

		if (audioArray == null)
			audioList = new ArrayList<Audio>();

		for(Audio audio : audioArray)  {
			audioList.add(audio);
		}
	}

	public static AudioPlayerList getInstance (Context context, Audio[] audioArray) {
		if (uniqueInstance == null){
			synchronized (AudioPlayerList.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new AudioPlayerList(context, audioArray);
				}
			}
		}
		return uniqueInstance;
	}

	public static AudioPlayerList getInstance (Context context) {
		if (uniqueInstance == null){
			synchronized (AudioPlayerList.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new AudioPlayerList(context);
				}
			}
		}
		return uniqueInstance;
	}

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
	public Iterator<Audio> iterator() {
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
				Audio audio = audioList.get(current);
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
		try {
			Log.v(AudioPlayerList.class.getCanonicalName(), "PK: " + audioList.get(current).getPrimaryKey());
			Manager.instance().registerExecution(context, audioList.get(current));
		}
		catch(DBException e) {
			throw new RuntimeException(e);
		}

		mp.reset();
		//if Shuffle off:
		if(!shuffle){
			if(current >= 0 && current < audioList.size()) {
				current++;
				play();
			}
			else {
				current = 0;
				if(repeat) {
					play();
				}
				else{
					mp.release();
					Log.v(AudioPlayerList.class.getCanonicalName(), " done !");
				}
			}
		}
		//if Shuffle on:
		else{

			Random rand = new Random();
			current = rand.nextInt(audioList.size());
			play();

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

	public boolean isPlaying () {
		return isPlaying;
	}

	public void setShuffle(boolean Shuffle) {
		this.shuffle = Shuffle;
	}

	public void stop() {
		if (isPlaying) {
			player.stop();
			player.reset();
			isPlaying = false;
			isPaused = false;
		}
	}

	public void killPLaylist() {
		audioList.removeAll(audioList);
	}

	public void newPlaylist(Audio[] executionList) {
		for(Audio audio : executionList)  {
			audioList.add(audio);
		}
		current = 0;
	}

	public void pause() {
		player.pause();
		isPlaying = false;
		isPaused = true;
	}

	public void nextTrack() {
		if (current >= (audioList.size()-1)) {
			stop();
			current = 0;
		} else {
			current++;
			stop();
			play();
		}
	}

	public void previousTrack() {
		if (player.getCurrentPosition() >= 3000) {
			stop();
			play();
		} else {
			if (current > 0) {
				stop();
				current--;
				play();
			} else {
				stop();
				play();
			}
		}
	}
	
	public void addMusic (Audio newMusic) {
		audioList.add(newMusic);
	}

}
