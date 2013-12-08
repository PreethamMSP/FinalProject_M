package com.common.videoAudioutility;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Debugger {
	
	public boolean DEBUG = false;
	
	
	public void DEBUG_PRINTLN(boolean flag, String ToPrint)
	{
		if(flag)
		System.out.println(ToPrint);
	}
	
	public void DEBUG_PRINT(boolean flag, String ToPrint)
	{
		if(flag)
		System.out.print(ToPrint);
	}
	
	public void DISPLAY_IMAGE(BufferedImage img)
	{
		JFrame Inputframe = new JFrame();
		 JLabel Inputlabel = new JLabel(new ImageIcon(img));
		Inputframe.getContentPane().add(Inputlabel, BorderLayout.CENTER);
		Inputframe.pack();
		Inputframe.setVisible(true);
	}
}
