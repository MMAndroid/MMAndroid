package br.unb.mobileMedia.core.db;

/**
 * Use to signal database exceptions.
 * 
 * @author rbonifacio.
 */
public class DBException extends Exception {

	private static final long serialVersionUID = 1L;
	private String msg;
	
	public DBException(String msg){
		this.msg = msg;
	}
	
	public String getMessage(){
		return this.msg;
	}
	
}
