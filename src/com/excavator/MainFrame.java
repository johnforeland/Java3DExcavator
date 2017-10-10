package com.excavator;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.swing.*;
import java.awt.*;


@SuppressWarnings("serial")
public class MainFrame extends JFrame{

	public MainFrame(){
		setTitle("Gravemaskin");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GraphicsConfigTemplate gtemplets = new GraphicsConfigTemplate3D();
		GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(gtemplets);
		Canvas3D canvas3D = new Canvas3D(gcfg);
		add("Center", canvas3D);

		 new MyUniverse(canvas3D);

		setExtendedState(JFrame.MAXIMIZED_BOTH);

		setVisible(true);
		
	}
}
