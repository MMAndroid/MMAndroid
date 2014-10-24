package br.unb.mobileMedia.core.db;


import java.util.List;

import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.PlaylistMedia;

public interface IPlaylistMediaDao {

	void addAudioToPlaylist(Audio audio, Playlist playlist) throws DBException;

	void addToPlaylist(int idPlaylist, List<Integer> mediaList); 
	
	List<PlaylistMedia> getMusicFromPlaylist(Playlist playlist) throws DBException;
}
