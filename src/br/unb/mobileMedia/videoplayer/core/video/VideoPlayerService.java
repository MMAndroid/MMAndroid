package br.unb.mobileMedia.videoplayer.core.video;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class VideoPlayerService extends Service {

	public static final String BINDER_NAME = "PlayerServiceBinder";
	
	/**
	 * Executa quando alguma outra classe faz o bind do servi�o.
	 * Inicia o servi�o e faz o link com o servi�o. A comunica��o entre o cliente e a classe
	 * de servi�o � feita via interface IBinder.
	 */
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}
	
	
	public class IPlayerSerivceBinder extends Binder{
		
		
		
		@Override
		public String getInterfaceDescriptor() {
			return BINDER_NAME;
		}
	}

}
