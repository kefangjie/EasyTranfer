package com.im;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.im.util.IConstant;
/**
 * @author xblia
 * 2014Äê12ÔÂ12ÈÕ
 */
public class TranferClient
{
	private ISocketOberser iSocketOberser;
	private IFileTransObserver fileTranObserver;
	private boolean isConn = false;
	
	private SocketChannel socketChannel;
	private FileSendTask fileSendTask;
	private FileReciveTask fileReciveTask;
	
	public TranferClient(ISocketOberser iSocketOberser, IFileTransObserver fileTranObserver)
    {
	    super();
	    this.iSocketOberser = iSocketOberser;
	    this.fileTranObserver = fileTranObserver;
    }
	
	public boolean connect(String ip)
	{
		try
		{
		 	socketChannel = SocketChannel.open();
	        socketChannel.configureBlocking(true);
	        isConn = socketChannel.connect(new InetSocketAddress(ip,IConstant.PORT_SERVER_DEFAULT));
	        fileSendTask = new FileSendTask(fileTranObserver, socketChannel);
	        fileSendTask.start();
	        fileReciveTask = new FileReciveTask(fileTranObserver, socketChannel);
	        fileReciveTask.start();
	        iSocketOberser.notifyDescript("connect to " + ip + " success.");
	        iSocketOberser.notifyConn(true);
        } catch (IOException e)
        {
	        e.printStackTrace();
	        isConn = false;
	        iSocketOberser.notifyDescript("connect fail: " + e.getMessage());
        }
		
		return isConn;
	}

	public void shutDown()
	{
		iSocketOberser.notifyDescript("begin close client connect.");
		if(null != socketChannel)
		{
			try
            {
	            socketChannel.close();
            } catch (IOException e)
            {
	            e.printStackTrace();
            }
		}
		if(null != fileSendTask)
		{
			fileSendTask.shutDown();
		}
		if(null != fileReciveTask)
		{
			fileReciveTask.shutDown();
		}
		iSocketOberser.notifyDescript("end close client connect.");
	}
}
