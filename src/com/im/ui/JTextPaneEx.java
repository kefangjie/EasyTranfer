package com.im.ui;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class JTextPaneEx extends JTextPane
{
    private static final long serialVersionUID = 1L;

	public void insert(String str, AttributeSet attrSet)
	{
		Document doc = this.getDocument();
		try
		{
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	public void setDocs(String str, Color col, boolean bold, int fontSize)
	{
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, col);
		// 颜色
		if (bold == true)
		{
			StyleConstants.setBold(attrSet, true);
		}// 字体类型
		StyleConstants.setFontSize(attrSet, fontSize);
		// 字体大小
		insert(str, attrSet);
	}
	
	public void append(String info, Color color)
	{
		setDocs(info, color, false, 15);
	}
}