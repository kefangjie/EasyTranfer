package com.im;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.im.util.IConstant;
import com.im.util.Utils;
/**
 * @author xblia
 * 2014Äê12ÔÂ12ÈÕ
 */
public class TranferServer extends Thread
{
	private ISocketOberser iSocketOberser;
	private IFileTransObserver fileTransObserver;
	
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private ServerSocket serverSocket;
	private SocketChannel socketChannel;
	private FileReciveTask fileReciveTask;
	private FileSendTask fileSendTask;

	private boolean isRunning;
	private boolean isConnect;

	public TranferServer(ISocketOberser iSocketOberser,
	        IFileTransObserver fileTransObserver)
	{
		super();
		this.iSocketOberser = iSocketOberser;
		this.fileTransObserver = fileTransObserver;
		this.isRunning = true;
		this.isConnect = false;
	}

	//@Override
	public void run()
	{
		try
		{
			iSocketOberser.notifyDescript("begin bind port:" + IConstant.PORT_SERVER_DEFAULT);
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(IConstant.PORT_SERVER_DEFAULT));
			selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			iSocketOberser.notifyDescript("listener port " + IConstant.PORT_SERVER_DEFAULT + " success.");
			iSocketOberser.notifyDescript("Local ip: " + Utils.getLocalHostIp());
			while (isRunning)
			{
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while (iterator.hasNext())
				{
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					handleKey(selectionKey);
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void handleKey(SelectionKey selectionKey) throws IOException
	{
		if (selectionKey.isAcceptable())
		{
			if(null != socketChannel)
			{
				socketChannel.close();
			}
			if(null != fileReciveTask)
			{
				fileReciveTask.shutDown();
			}
			if(null != fileSendTask)
			{
				fileSendTask.shutDown();
			}
			ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
			socketChannel = server.accept();
			socketChannel.configureBlocking(true);
			
			fileReciveTask = new FileReciveTask(fileTransObserver, socketChannel);
			fileReciveTask.start();
			fileSendTask = new FileSendTask(fileTransObserver, socketChannel);
			fileSendTask.start();
			
			iSocketOberser.notifyConn(true);
			iSocketOberser.notifyDescript("have connect : " + socketChannel.socket().getInetAddress().getHostName());
		}
	}

	public boolean isConnect()
	{
		return isConnect;
	}

	public void shutdown()
	{
		iSocketOberser.notifyDescript("will be close server.");
		try
		{
			this.interrupt();
			if (null != serverSocketChannel)
			{
				serverSocketChannel.close();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if(null != serverSocket)
		{
			try
            {
	            serverSocket.close();
            } catch (IOException e)
            {
	            e.printStackTrace();
            }
		}
		
		if(null != fileReciveTask)
		{
			fileReciveTask.shutDown();
		}
		if(null != fileSendTask)
		{
			fileSendTask.shutDown();
		}
		iSocketOberser.notifyDescript("server has closed.");
	}
}