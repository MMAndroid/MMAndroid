package br.unb.mobileMedia.core.manager;

import android.media.MediaPlayer;

public class MediaPlayerControl implements
		android.widget.MediaController.MediaPlayerControl {

	private MediaPlayer player;
	
	public boolean canPause() {
		return player.isPlaying();
	}

	public boolean canSeekBackward() {
		return true;
	}

	public boolean canSeekForward() {
		return true;
	}

	public int getBufferPercentage() {
		return 0;
	}

	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	public int getDuration() {
		return player.getDuration();
	}

	public boolean isPlaying() {
		return player.isPlaying();
	}

	public void pause() {
		player.pause();
	}

	public void seekTo(int pos) {
		player.seekTo(pos);
	}

	public void start() {
		player.start();
	}

}
