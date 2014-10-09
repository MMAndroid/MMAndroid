package br.unb.mobileMedia.core.db;

import android.content.Context;

/**
 * A default implementation of the abstract class DBFactory.
 * @author rbonifacio
 */
public class DefaultDBFactory extends DBFactory {
	
	public DefaultDBFactory(Context context) {
		super(context);
	}

	@Override
	public AuthorDAOOld createAuthorDAO() {
		return new DefaultAuthorDAO(context);
	}

	@Override
	public PlaylistDAOOld createPlaylistDAO() {
		return new DefaultPlaylistDAO(context);
	}

}
