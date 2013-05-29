package br.unb.mobileMedia.playlist;

import android.location.Location;
import android.os.Bundle;
import android.location.LocationListener;




public final class MMUNB_GPS implements LocationListener {
	Double latitude ;        
	Double longitude;  
	

public void onLocationChanged(Location locFromGps) {
// Chamado quando o ouvinte � notificado com uma atualiza��o de localiza��o do GPS
	//Location coordenadas = locFromGps;
	updateView(locFromGps);
}


public void onProviderDisabled(String provider) {
//Chamado quando o provedor de GPS � desligado (o usu�rio desligar o GPS no telefone)
	
}


public void onProviderEnabled(String provider) {
// Chamado quando o provedor de GPS est� ligado (o usu�rio ligar o GPS no telefone)
}


public void onStatusChanged(String provider, int status, Bundle extras) {
// Chamado quando h� mudan�as do estado do provedor de GPS
	
}

public void updateView(Location locat)
{       
 latitude = locat.getLatitude();        
 longitude = locat.getLongitude();         
   
}
public double getLongitude() {
	
	return longitude;

}

public double getLatitude() {
	return latitude;

}
}