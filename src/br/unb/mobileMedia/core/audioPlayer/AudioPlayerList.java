package br.unb.mobileMedia.core.audioPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import br.unb.mobileMedia.core.domain.AudioOld;

public class AudioPlayerList implements MediaPlayer.OnCompletionListener {
	private static volatile AudioPlayerList uniqueInstance;
	private Context context;
	private MediaPlayer player;
	private List<AudioOld> audioList;
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
		audioList = new ArrayList<AudioOld>();
		setRepeat(false);
		player.setOnCompletionListener(this);
	}

	/**
	 * A constructor that expects both application context and an array of audio.
	 * @param context the application context.
	 * @param audioArray an array of musics that the user might choose to play
	 */
	private AudioPlayerList(Context context, AudioOld[] audioArray) {
		this(context);
		if (audioArray == null) {
			audioList = new ArrayList<AudioOld>();
		}
		for (AudioOld audio : audioArray) {
			audioList.add(audio);
		}
	}

	public static AudioPlayerList getInstance(Context context,
			AudioOld[] audioArray) {
		if (uniqueInstance == null) {
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

	public void play(int indexFile) {
		try {
			
			Log.i("UriEncode: ", audioList.get(indexFile).getURI().toString() );
			Log.i("UriDecode: ", Uri.decode(audioList.get(indexFile).getURI().toString()));
			
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
		if (repeat) {
			play(currentSongIndex);
		} else if (isShuffle()) {
			currentSongIndex = random();
			play(currentSongIndex);
		} else {
			if (currentSongIndex < (audioList.size() - 1)) {
				play(currentSongIndex + 1);
				currentSongIndex++;
			} else {
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

	public void newPlaylist(List<AudioOld> executionList) {
		audioList = executionList;
	}

	public void playPause() {
		if (isPlaying()) {
			player.pause();
		} else {
			player.start();
		}
	}

	public void nextTrack() {
		if (currentSongIndex < (audioList.size() - 1)) {
			if (isShuffle()) {
				currentSongIndex = random();
			} else {
				currentSongIndex++;
			}
			play(currentSongIndex);
		} else {
			currentSongIndex = 0;
			play(currentSongIndex);
		}
	}

	public void previousTrack() {
		if (currentSongIndex > 0) {
			if (isShuffle()) {
				currentSongIndex = random();
			} else {
				currentSongIndex--;
			}
			currentSongIndex--;
			play(currentSongIndex);
		} else {
			currentSongIndex = random();
			play(currentSongIndex);
		}
	}

	public void reset() {
		player.reset();
	}

	public void addMusic(AudioOld newMusic) {
		audioList.add(newMusic);
	}

	/**
	 * 
	 * @return name of current music
	 */
	public String getTitleSong() {
		String str1 = audioList.get(currentSongIndex).getTitle();
		return str1.substring(0, str1.indexOf("."));
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
	private int random() {
		return new Random().nextInt(sizeOfAudioList() - 1);
	}
}