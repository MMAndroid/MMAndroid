package br.unb.mobileMedia.core.db;


import java.util.List;

import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.PlaylistMedia;

public interface IPlaylistMediaDao {

	public void addMediaToPlaylist(Integer idAudio, Integer playlistId) throws DBException;
	
	public List<PlaylistMedia> getMusicFromPlaylist(Integer idPlaylist) throws DBException;
	
	public PlaylistMedia getPlaylistByMediaInPlaylist(Audio audio, Playlist playlist) throws DBException;
	
	public void removeMediaFromPlaylist(Integer idMediaPlaylist) throws DBException;
}
