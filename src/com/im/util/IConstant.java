package com.im.util;

/**
 * @author xblia
 * 2014Äê12ÔÂ12ÈÕ
 */
public interface IConstant
{
	int PORT_SERVER_DEFAULT = 11585;
	
	int MSG_HEADER_IDENTIFIED = 11585667;
	int FILE_HEADER_IDENTIFIED = 11585668;
	String MSG_FILE_IDENTIFIED = "0x11585668@intel"; 
	
	int LEN_INT = Integer.SIZE / 8;
	int LEN_LONG = Long.SIZE / 8;
	int LEN_DOUBLE = Double.SIZE / 8;
	int LEN_FLOAT = Float.SIZE / 8;
	
	String SEPARATOR_LINE = System.getProperty("line.separator");
	String BASE_PATH = System.getProperty("user.dir");
}
