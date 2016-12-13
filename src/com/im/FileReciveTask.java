package com.im;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import com.im.util.IConstant;
import com.im.util.Utils;
/**
 * @author xblia
 * 2014Äê12ÔÂ12ÈÕ
 */
public class FileReciveTask extends Thread
{
	private IFileTransObserver fileTranObserver;
	private SocketChannel socketChannel;
	
	private boolean isRunning;
	
	public FileReciveTask(IFileTransObserver fileTranObserver, SocketChannel socketChannel)
    {
	    super();
	    this.fileTranObserver = fileTranObserver;
	    this.socketChannel = socketChannel;
	    this.isRunning = true;
    }

	//@Override
	public void run()
	{
		while(isRunning)
		{
			ByteBuffer headerBuff = ByteBuffer.allocate(4);
			try
            {
	            int iDentified= socketChannel.read(headerBuff);
	            if(iDentified == -1)
	            {
	            	break;
	            }
	            if(iDentified == IConstant.LEN_INT)
	            {
	            	headerBuff.flip();
	            	int iValue = headerBuff.getInt();
	            	if(iValue == IConstant.FILE_HEADER_IDENTIFIED)
	            	{
	            		parseFileHeader(socketChannel);
	            	}else if(iValue == IConstant.MSG_HEADER_IDENTIFIED)
	            	{
	            		parseMsg(socketChannel);
	            	}
	            	else
	            	{
	            		continue;
	            	}
	            }
            } catch (IOException e)
            {
	            e.printStackTrace();
	            break;
            }
			
		}
	}
	
	private void parseMsg(SocketChannel socket)
    {
		ByteBuffer buff = ByteBuffer.allocate(IConstant.LEN_INT);
		try
        {
	        socket.read(buff);
	        buff.flip();
	        int iMsgLen = buff.getInt();
	        if(iMsgLen != -1)
	        {
	        	buff = readMsg(socket, iMsgLen);
	        	
	        	byte []data = new byte[iMsgLen];
	        	buff.get(data);
	        	String strMsg = new String(data);
	        	fileTranObserver.notifyMsg(strMsg, true);
	        	
	        }
        } catch (IOException e)
        {
	        e.printStackTrace();
        } catch (Exception e)
        {
	        e.printStackTrace();
        }
    }

	private void parseFileHeader(SocketChannel socket)
    {
		ByteBuffer fileheadBuff = ByteBuffer.allocate(IConstant.LEN_INT);
		try
        {
	        socket.read(fileheadBuff);
	        fileheadBuff.flip();
	        int iFileNameLen = fileheadBuff.getInt();
	        if(iFileNameLen != -1)
	        {
	        	fileheadBuff = readMsg(socket, iFileNameLen);
	        	
	        	byte []data = new byte[iFileNameLen];
	        	fileheadBuff.get(data);
	        	String fileName = new String(data);
	        	fileTranObserver.notifyNextFile(fileName);
	        	
	        	fileheadBuff = readMsg(socket, IConstant.LEN_LONG);
	        	long fileSize = fileheadBuff.getLong();
	        	
	        	if(fileName != null && fileSize > 0)
	        	{
	        		beginSaveFile(fileName, fileSize, socketChannel);
	        		
	        	}
	        }
        } catch (IOException e)
        {
	        e.printStackTrace();
        } catch (Exception e)
        {
	        e.printStackTrace();
        }
    }
	
	private void beginSaveFile(String fileName, long fileSize, SocketChannel socket)
    {
	    String fullPath = IConstant.BASE_PATH + File.separator + "recive_folder" + File.separator + fileName;
	    File baseDir = new File(new File(fullPath).getParent());
	    if(!baseDir.exists())
	    {
	    	baseDir.mkdirs();
	    }
	    FileOutputStream fos = null;
	    FileChannel channel = null;
	    try
        {
	    	fos = new FileOutputStream(fullPath);
	    	channel = fos.getChannel();
	        long iReadLen = 0;
	        do
	        {
	        	int iNeedBuff = 1024;
	        	if((fileSize - iReadLen) < 1024)
	        	{
	        		iNeedBuff = (int)(fileSize - iReadLen);
	        	}
	        	iReadLen += iNeedBuff;
	        	fileTranObserver.notifyProgress(1, 1, ((iReadLen*1.0)/fileSize) * 100);
	        	ByteBuffer fileData = readMsg(socket, iNeedBuff);
	        	channel.write(fileData);
	        }
	        while (iReadLen < fileSize);
        } catch (FileNotFoundException e)
        {
	        e.printStackTrace();
        } catch (Exception e)
        {
	        e.printStackTrace();
        }finally
        {
        	Utils.closeResource(fos);
        }
	    
    }

	public ByteBuffer readMsg(SocketChannel channel, int iTotalLen) throws Exception
	{
		int iReadLen = 0;
		ByteBuffer buff = ByteBuffer.allocate(iTotalLen);
		while(iReadLen < iTotalLen)
		{
			iReadLen += channel.read(buff);
			buff.position(iReadLen);
		}
		buff.flip();
		return buff;
	}

	public void shutDown()
	{
		this.isRunning = false;
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
	}
}