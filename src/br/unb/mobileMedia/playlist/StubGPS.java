package br.unb.mobileMedia.playlist;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;


public class StubGPS {
	
public String SLat;
public String SLgt;



 // Classe StubGPS sem refatoração.
 // * Retorna Latitude e Longitudes fixas, nõa retorna os valores capturados pelo GPS do Smartphone
	public double getLongitude() {
		return 37.422006;

	}
	
	public double getLatitude() {
		return -12.084095;

	}


	
	
}

