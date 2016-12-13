package com.im.ui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * @author xblia
 * 2014年12月12日
 */
public class TextAreaByMenu extends JTextPaneEx implements MouseListener
{
    private static final long serialVersionUID = 3063210188188542991L;
	private JPopupMenu pop = null;
	private JMenuItem clear = null;
	private JMenuItem copy = null;
	private JMenuItem paste = null;
	private JMenuItem transfer = null;
	private JMenuItem cut = null;
	
	private ITextAreaTask iTextAreaTask;

	public TextAreaByMenu(ITextAreaTask iTextAreaTask)
	{
		super();
		this.iTextAreaTask = iTextAreaTask;
		init();
	}

	private void init()
	{
		this.addMouseListener(this);
		pop = new JPopupMenu();
		pop.add(clear = new JMenuItem("Clear"));
		pop.add(copy = new JMenuItem("Copy"));
		pop.add(paste = new JMenuItem("Paste"));
		pop.add(cut = new JMenuItem("Cut"));
		pop.add(transfer = new JMenuItem("Transfer"));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
		cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
		
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				action(e);
			}
		});
		copy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				action(e);
			}
		});
		paste.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				action(e);
			}
		});
		cut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				action(e);
			}
		});
		transfer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				action(e);
			}
		});
		this.add(pop);
	}

	public void action(ActionEvent e)
	{
		String str = e.getActionCommand();
		if (str.equals(clear.getText()))
		{
			this.setText("");
		} else if (str.equals(copy.getText()))
		{ // 复制
			this.copy();
		} else if (str.equals(paste.getText()))
		{ // 粘贴
			this.paste();
		} else if (str.equals(cut.getText()))
		{ // 剪切
			this.cut();
		}else if(str.equals(transfer.getText()))
		{
			iTextAreaTask.transferFile(null);
		}
	}

	public JPopupMenu getPop()
	{
		return pop;
	}

	public void setPop(JPopupMenu pop)
	{
		this.pop = pop;
	}

	public boolean isClipboardString()
	{
		boolean b = false;
		Clipboard clipboard = this.getToolkit().getSystemClipboard();
		Transferable content = clipboard.getContents(this);
		try
		{
			if (content.getTransferData(DataFlavor.stringFlavor) instanceof String)
			{
				b = true;
			}
		} catch (Exception e)
		{
		}
		return b;
	}

	public boolean isCanCopy()
	{
		boolean b = false;
		int start = this.getSelectionStart();
		int end = this.getSelectionEnd();
		if (start != end)
			b = true;
		return b;
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			clear.setEnabled(!this.getText().isEmpty());
			copy.setEnabled(isCanCopy());
			paste.setEnabled(isClipboardString());
			cut.setEnabled(isCanCopy());
			pop.show(this, e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent e)
	{
	}
}