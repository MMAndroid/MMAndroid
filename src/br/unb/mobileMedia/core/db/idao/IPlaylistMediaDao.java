package br.unb.mobileMedia.core.db.idao;


import java.util.List;

import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.PlaylistMedia;

public interface IPlaylistMediaDao {

	void addAudioToPlaylist(Long idAudio, Playlist playlist) throws DBException;

	void addToPlaylist(int idPlaylist, List<Integer> mediaList); 
	
	List<PlaylistMedia> getMusicFromPlaylist(Long idPlaylist) throws DBException;
	List<PlaylistMedia> getMusicFromPlaylistPaginate(Playlist playlist, int init, int limit) throws DBException;
	
	
	PlaylistMedia getPlaylistByMediaInPlaylist(Audio audio, Playlist playlist) throws DBException;
	
	void removeMediaFromPlaylist(Long idMediaPlaylist) throws DBException;
}
