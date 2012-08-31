package br.unb.projetopositivo.mm.playlist;

public interface PlayListDBConstants {
	
	/** TODO: not sure if the following two constants are specific to playlists */
	public String DATABASE_NAME = "MMUnBDB";
	public int DATABASE_VERSION = 1;
	
	public static final String QUERY = "SELECT NAME, DESCRIPTION FROM PLAYLIST";
	public static final String NAME_COLUMN = "NAME";
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";
	public static final String PLAYLIST_TABLE = "PLAYLIST";
}