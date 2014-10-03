package br.unb.mobileMedia.util;


public class UriEncodeDecode {

	public UriEncodeDecode(){}
	
	
	public String Encode( String url){
		
		StringBuilder resultStr = new StringBuilder();
		
		for(char ch : url.toCharArray()){
			if(special(ch)){
				resultStr.append(Integer.toHexString(ch));
			}else{
				resultStr.append(ch);
			}
		}
		
		return resultStr.toString();
		
	}
	
	
	
	public String Decode(String url){
		
		
		return url;
	}
	
	
	private static boolean special( char ch){
		
		if(ch > 128 || ch < 0)
			return true;
		
		return false;
	}
	
}
