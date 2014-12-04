package br.unb.mobileMedia.core.db;

public interface DBConstants {
	
	public String DATABASE_NAME = "MMAndroid";
	public int    DATABASE_VERSION = 25;
	public String DEFINICAO_PK = "ID INTEGER PRIMARY KEY AUTOINCREMENT";
	public String AUTHOR_TABLE = "AUTHOR";
	public String AUTHOR_ID_COLUMN = "ID";
	public String AUTHOR_NAME_COLUMN = "NAME";
	
	public String ALBUM_TABLE = "ALBUM";
	public String ALBUM_ID_COLUMN = "ID";
	public String ALBUM_NAME_COLUMN = "NAME";
	public String ALBUM_IMAGE_COLUNM = "IMAGE";
	public String ALBUM_FK_AUTHOR_ID = "FK_AUTHOR_ID";
	
	public String MEDIA_TABLE = "MEDIA";
	public String MEDIA_ID_COLUMN = "ID";
	public String MEDIA_URL_COLUMN = "URL";
	public String MEDIA_ALBUM_ID_COLUMN = "FK_ALBUM_ID";
	
	public String EH_TABLE = "EXECUTION_HISTORY";
	public String EH_DATE_TIME_EXECUTION_COLUMN = "DATE_TIME_EXECUTION"; 
	public String EH_FK_AUDIO_COLUMN = "FK_MEDIA";
	
	public String PLAYLIST_TABLE = "PLAYLIST";
	public String PLAYLISTID_COLUMN = "ID";
	public String PLAYLIST_NAME_COLUMN = "NAME";
	public String PLAYLIST_LOCATION_TABLE = "PLAYLIST_LOCATION";
	public String PLAYLIST_LOCATION_FK_PLAYLIST = "FK_PLAYLIST";
	public String PLAYLIST_LOCATION_LATITUDE = "LATITUDE";
	public String PLAYLIST_LOCATION_LONGITUDE = "LONGITUDE";

	
	
	public String PLAYLIST_MEDIA_TABLE = "PLAYLIST_MEDIA";
	public String PLAYLIST_MEDIA_ID_COLUNM = "ID";
	public String PLAYLIST_MEDIA_FK_PLAYLIST_COLUNM = "FK_PLAYLIST";
	public String PLAYLIST_MEDIA_FK_MEDIA_COLUNM = "FK_MEDIA";
	
	
	
	
	

	public String DROP_TABLE_STATEMENTS[] = { 
			"DROP TABLE IF EXISTS " + AUTHOR_TABLE, 
			"DROP TABLE IF EXISTS " + ALBUM_TABLE,
			"DROP TABLE IF EXISTS " + MEDIA_TABLE, 
			"DROP TABLE IF EXISTS " + EH_TABLE,
			"DROP TABLE IF EXISTS " + PLAYLIST_TABLE,
			"DROP TABLE IF EXISTS " + PLAYLIST_MEDIA_TABLE };

	public String CREATE_TABLE_STATEMENTS[] = {
			
			"CREATE TABLE "+AUTHOR_TABLE+"(" 
					+  DEFINICAO_PK +", "
					+ "NAME VARCHAR(50) NOT NULL"
					+ ");",
		
			"CREATE TABLE "+ALBUM_TABLE+" ("
					+ DEFINICAO_PK +", "
					+ "NAME VARCHAR(50) NOT NULL ,"
					+ "IMAGE BLOB, "
					+ "FK_AUTHOR_ID INTEGER NOT NULL"
					+ " );",
					
			"CREATE TABLE "+MEDIA_TABLE+" ( " 
					+ DEFINICAO_PK +", "
					+ "URL VARCHAR(255) NOT NULL, "
					+ "FK_ALBUM_ID INTEGER NOT NULL"
					+ ");",

		
					
			"CREATE TABLE "+EH_TABLE+" ( "
					+ DEFINICAO_PK + ", "
					+ "DATE_TIME_EXECUTION CHAR(14), " 
					+ "FK_MEDIA INTEGER"
					+ ");",
			
			"CREATE TABLE "+PLAYLIST_TABLE+" ( " 
					+ DEFINICAO_PK +", "
					+ "NAME VARCHAR(255) UNIQUE NOT NULL "
					+ ");",
			
			"CREATE TABLE "+PLAYLIST_MEDIA_TABLE+" ("
					+ DEFINICAO_PK + ", "
					+ "FK_PLAYLIST INTEGER," 
					+ "FK_MEDIA INTEGER );",
					
			"CREATE TABLE "+PLAYLIST_LOCATION_TABLE+" ( " 
					+ "FK_PLAYLIST INTEGER UNIQUE NOT NULL, " 
					+ "LATITUDE DOUBLE NOT NULL, "
					+ "LONGITUDE DOUBLE NOT NULL "
					+ ");"
		}; 
	
	
//##########################################################################################################################
//#####################################-------QUERYES DATABASE-------#######################################################
//##########################################################################################################################
	
	
	public String SELECT_AUTHORS = "SELECT ID, NAME FROM " + AUTHOR_TABLE;
	public String SELECT_AUTHORS_BY_ID = "SELECT ID, NAME FROM " + AUTHOR_TABLE + " WHERE ID = ?";
	public String SELECT_AUTHORS_BY_NAME = "SELECT ID, NAME FROM " + AUTHOR_TABLE + " WHERE NAME = ?";
	public String COUNT_AUTHORS = "SELECT COUNT(ID) FROM " + AUTHOR_TABLE;
	
	public String SELECT_ALBUMS_BY_AUTHOR = "SELECT ID, NAME, IMAGE, FK_AUTHOR_ID FROM "+ ALBUM_TABLE + " WHERE FK_AUTHOR_ID = ?";
	public String SELECT_ALBUMS_BY_NAME   = "SELECT ID, NAME, IMAGE, FK_AUTHOR_ID FROM "+ ALBUM_TABLE + " WHERE NAME = ?";
	public String SELECT_ALL_ALBUM = "SELECT ID, NAME, IMAGE, FK_AUTHOR_ID FROM "+ ALBUM_TABLE;
	public String COUNT_ALBUMS = "SELECT  COUNT(ID) FROM "+ ALBUM_TABLE;
	
	
	public String SELECT_MEDIAS                    = "SELECT ID, URL, FK_ALBUM_ID FROM " + MEDIA_TABLE;
	public String SELECT_MEDIA_BY_ID               = "SELECT ID, URL, FK_ALBUM_ID FROM " + MEDIA_TABLE + " WHERE ID = ?";
	public String SELECT_MEDIA_BY_ALBUM            = "SELECT ID, URL, FK_ALBUM_ID FROM " + MEDIA_TABLE + " WHERE FK_ALBUM_ID = ?";
	public String SELECT_MEDIA_BY_PATH             = "SELECT URL FROM " + MEDIA_TABLE + " WHERE URL = ?";
	public String SELECT_MEDIA_PAGINATED           = "SELECT * FROM " + MEDIA_TABLE + " LIMIT ?, ?";
	public String COUNT_MEDIA                     = "SELECT COUNT(ID) FROM " + MEDIA_TABLE + "";

	
	
	
	public String SELECT_SIMPLE_PLAYLIST = "SELECT ID, NAME FROM " + PLAYLIST_TABLE;
	public String SELECT_SIMPLE_PLAYLIST_BY_ID = "SELECT ID, NAME FROM " + PLAYLIST_TABLE + " WHERE ID = ?";
	public String SELECT_SIMPLE_PLAYLIST_BY_NAME = "SELECT ID, NAME FROM " + PLAYLIST_TABLE + " WHERE NAME = ?";
	
	
	public String SELECT_MEDIA_FROM_PLAYLIST = "SELECT ID, FK_PLAYLIST, FK_MEDIA "
			+ "FROM " + PLAYLIST_MEDIA_TABLE + " WHERE FK_PLAYLIST = ? AND FK_MEDIA = ?";
	
	
	public String SELECT_MEDIAS_FROM_PLAYLIST = "SELECT ID, FK_PLAYLIST, FK_MEDIA "
			+ "FROM " + PLAYLIST_MEDIA_TABLE + " WHERE FK_PLAYLIST = ?";
	
	
	
	public String SELECT_EXECUTION_HISTORY = 
			"SELECT AT.PK, AT.ID, AT.NAME, AU.PK, AU.ID, AU.TITLE, AU.ALBUM, AU.URL, EH.DATE_TIME_EXECUTION " +
			"FROM (AUTHOR AT INNER JOIN AUDIO AU ON AT.PK = AU.FK_AUTHOR) INNER JOIN EXECUTION_HISTORY EH " +
			" ON AU.PK = EH.FK_MEDIA " +
			"WHERE DATE_TIME_EXECUTION > ? AND DATE_TIME_EXECUTION < ?";
	
	
	
	
	public String SELECT_PLAYLISTS = "SELECT ID, NAME FROM "+ PLAYLIST_TABLE;
	public String SELECT_PLAYLIST_BY_NAME = "SELECT ID, NAME FROM " + PLAYLIST_TABLE + " WHERE NAME = ?";
	public String SELECT_PLAYLIST_BY_ID = "SELECT ID, NAME FROM " + PLAYLIST_TABLE + " WHERE ID = ?";
	public String COUNT_PLAYLISTS = "SELECT COUNT(ID) FROM " + PLAYLIST_TABLE + "";
	
	
}