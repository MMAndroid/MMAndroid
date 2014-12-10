package br.unb.mobileMedia.core.audioPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;

public class AudioPlayerList implements MediaPlayer.OnCompletionListener {
	private static volatile AudioPlayerList uniqueInstance;
	private Context context;
	private MediaPlayer player;
	private List<Audio> audioList;
	private boolean repeat = false;
	private boolean shuffle = false;
	private boolean isPlaying = false;
	private int currentSongIndex = 0;
	private MediaExtractor audioExtractor;

	private AudioPlayerList() {
	}

	/**
	 * Default constructor expecting just the application context.
	 * @param context the application context.
	 */
	private AudioPlayerList(Context c) {
		this.context = c;
		this.player = new MediaPlayer();
		this.audioList = new ArrayList<Audio>();
		setRepeat(false);
		this.player.setOnCompletionListener(this);
		
		this.audioExtractor = new DefaultAudioExtractor(this.context);
	}

	/**
	 * A constructor that expects both application context and an array of audio.
	 * @param context the application context.
	 * @param audioArray an array of musics that the user might choose to play
	 */
	private AudioPlayerList(Context context, Audio[] audioArray) {
		this(context);
		if (audioArray == null) {
			audioList = new ArrayList<Audio>();
		}
		for (Audio audio : audioArray) {
			audioList.add(audio);
		}
	}

	public static AudioPlayerList getInstance(Context context,
			Audio[] audioArray) {
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
			
//			Log.i("UriEncode: ", Uri.encode(audioList.get(indexFile).getUrl()));
//			Log.i("UriDecode: ", Uri.decode(audioList.get(indexFile).getUrl()));
//			this.audioExtractor.setMMR(audioList.get(indexFile).getUrl()); 
			
			player.reset();
			player.setDataSource(context, Uri.parse(Uri.encode(audioList.get(indexFile).getUrl())));
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

	public void newPlaylist(List<Audio> executionList) {
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

	public void addMusic(Audio newMusic) {
		audioList.add(newMusic);
	}

	/**
	 * 
	 * @return name of current music
	 */
	public String getTitleSong() {
		return audioExtractor.getTitle(audioList.get(currentSongIndex).getUrl());
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
	
	
	public Bitmap getAlbumArt(){
		
		byte[] imageArt = audioExtractor.getAlbumArt(audioList.get(currentSongIndex).getUrl());
		
		if(imageArt != null){
			return BitmapFactory.decodeByteArray(imageArt, 0, imageArt.length);
		}
		
		return null;
	}
	
	
	public String getAuthor(){
		return audioExtractor.getAuthor(audioList.get(currentSongIndex).getUrl());
	}
	
	public String getAlbum(){
		return audioExtractor.getAlbum(audioList.get(currentSongIndex).getUrl());
	}
	
	
	public String getGenre(){
		return audioExtractor.getGenre(audioList.get(currentSongIndex).getUrl());
	}
}