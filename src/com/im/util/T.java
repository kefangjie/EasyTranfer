package com.im.util;

import java.net.InetAddress;


/**
 * @author xblia
 * 2014Äê12ÔÂ12ÈÕ
 */
public class T
{

	public static void main(String[] args) throws Exception
    {
	/*	try
        {
	        InetAddress addr = InetAddress.getLocalHost();
	        System.out.println(addr.toString());
        } catch (UnknownHostException e)
        {
	        e.printStackTrace();
        }*/
		
		/*String str = "aaaaaa";
		System.out.println(str.getBytes().length);
		
		Process process = Runtime.getRuntime().exec("ping cats.sh.intel.com");
		InputStream in = process.getInputStream();
		int iLen = -1;
		byte []data = new byte[1024];
		do
		{
			iLen = in.read(data);
			if(iLen != -1)
			{
				System.out.println(new String(data, 0, iLen));
			}
		}while(iLen > 0);*/
		
		InetAddress inetHost = InetAddress.getByName("cats.sh.intel.com");
        String hostName = inetHost.getHostName();
        System.out.println("The host name was: " + hostName);
        System.out.println("The hosts IP address is: " + inetHost.getHostAddress());
		
    }
}
