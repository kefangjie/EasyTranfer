package com.im.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @author xblia
 * 2014Äê12ÔÂ12ÈÕ
 */
public class Utils
{
	public static void closeResource(OutputStream out, InputStream in, Socket socket)
	{
		try
        {
	        if(null != out)
	        {
	        	out.close();
	        }
	        if(null != in)
	        {
	        	in.close();
	        }
	        if(null != socket)
	        {
	        	socket.close();
	        }
        } catch (IOException e)
        {
	        e.printStackTrace();
        }
	}
	
	public static void closeResource(OutputStream out)
	{
		if(null != out)
		{
			try
            {
	            out.close();
            } catch (IOException e)
            {
	            e.printStackTrace();
            }
		}
	}
	
	public static void closeResource(InputStream in)
	{
		if(null != in)
		{
			try
            {
				in.close();
            } catch (IOException e)
            {
	            e.printStackTrace();
            }
		}
	}
	
	public static void closeResource(Socket socket)
	{
		if(null != socket)
		{
			try
            {
				socket.close();
            } catch (IOException e)
            {
	            e.printStackTrace();
            }
		}
	}
	
	public static String getLocalHostIp()
	{
		String ip = "";
		try
        {
	        InetAddress addr = InetAddress.getLocalHost();
	        ip = addr.getHostAddress();
        } catch (UnknownHostException e)
        {
	        e.printStackTrace();
        }
		
		return ip;
	}
	
	public static String getDate()
	{
		return new SimpleDateFormat("MM-dd HH:mm").format(new Date());
	}
}
