package com.im;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.im.util.IConstant;
import com.im.util.TimeUnit;
import com.im.util.Utils;
/**
 * @author xblia
 * 2014Äê12ÔÂ12ÈÕ
 */
public class FileSendTask extends Thread
{

	private IFileTransObserver fileTranObserver;
	private SocketChannel socketChannel;
	
	private FilesMgr filesMgr;
	private boolean isRunning;
	
	public FileSendTask(IFileTransObserver fileTranObserver, SocketChannel socketChannel)
    {
	    super();
	    this.fileTranObserver = fileTranObserver;
	    this.socketChannel = socketChannel;
	    this.isRunning = true;
	    this.filesMgr = FilesMgr.getInstance();
    }

	//@Override
	public void run()
	{
		while(isRunning)
		{
			List<File> files = filesMgr.getFiles();
			if(files.isEmpty())
			{
				try
                {
	                Thread.sleep(TimeUnit.ONE_SECOND);
                } catch (InterruptedException e)
                {
	                e.printStackTrace();
                }
				continue;
			}
			boolean isMsg = false;
			for (int i = 0; i < files.size(); i++)
            {
				if(isRunning)
				{
					if(files.get(i).getName().contains(IConstant.MSG_FILE_IDENTIFIED))
					{
						String msg = files.get(i).getName().replace(IConstant.MSG_FILE_IDENTIFIED, "");
						outputMsg(msg);
						fileTranObserver.notifyMsg(msg, false);
						isMsg = true;
					}else
					{
						fileTranObserver.notifyNextFile(files.get(i).getAbsolutePath());
						fileTranObserver.notifyProgress(files.size(), i+1, ((i+1)*1.0)/files.size() * 100);
						scannChildAndTrans(files.get(i), files.get(i).getParent());
					}
				}
            }
			if(!isMsg)
			{
				fileTranObserver.notifyFinished("");
			}
			files.clear();
		}
	}
	
	private void scannChildAndTrans(File file, String baseFolder)
    {
		if(file.isDirectory())
		{
			File [] files = file.listFiles();
			for (File file2 : files)
            {
				scannChildAndTrans(file2, baseFolder);
            }
		}else
		{
			outputFile(file, baseFolder);
		}
    }
	
	private void outputMsg(String msg)
    {
		byte []data = msg.getBytes();
		ByteBuffer buff = ByteBuffer.allocate(data.length + (Integer.SIZE / 8) * 2);
		buff.putInt(IConstant.MSG_HEADER_IDENTIFIED);// identified.
		buff.putInt(data.length);
		buff.put(data);
		buff.flip();
		try
        {
	        sendMsg(socketChannel, buff);
        } catch (Exception e)
        {
	        e.printStackTrace();
        }
    }

	private void outputFile(File file, String basePath)
	{
		FileInputStream fin = null;
		try
		{
			{//Header.
				String fileRelativePath = file.getAbsolutePath().replace(basePath, "");
				
				ByteBuffer buff = ByteBuffer.allocate(255 + 4 + 8);
				buff.putInt(IConstant.FILE_HEADER_IDENTIFIED);// identified.
				buff.putInt(fileRelativePath.getBytes().length);
				buff.put(fileRelativePath.getBytes());
				buff.putLong(file.length());
				buff.flip();
				sendMsg(socketChannel, buff);
			}
			
			// FileRead and socket write
			fin = new FileInputStream(file);
			int iReadLen = -1;
			do
			{
				ByteBuffer fileData = ByteBuffer.allocate(1024);
				FileChannel fileChannel = fin.getChannel();
				iReadLen = fileChannel.read(fileData);
				fileData.flip();
				sendMsg(socketChannel, fileData);
			} while (iReadLen != -1);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			Utils.closeResource(fin);
		}
	}
	
	public void sendMsg(SocketChannel channel, ByteBuffer buff) throws Exception
	{
		int iTotalLen = buff.limit();
		int iSendLen = 0;
		while(iSendLen < iTotalLen)
		{
			iSendLen += channel.write(buff);
			buff.position(iSendLen);
		}
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
