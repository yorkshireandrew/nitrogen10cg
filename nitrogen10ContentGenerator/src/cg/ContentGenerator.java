package cg;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class ContentGenerator extends JFrame{
	
	/** buttons for selecting the view */
	/*
	final FixedSizeButton frontViewButton;
	final FixedSizeButton leftViewButton;
	final FixedSizeButton backViewButton;
	final FixedSizeButton rightViewButton;
	final FixedSizeButton topViewButton;
	final FixedSizeButton bottomViewButton;
	final FixedSizeButton greySpacer;
	final ButtonGroup viewButtonGroup;
	*/
	
	/** buttons for picking vertexes */
	/*
	final FixedSizeButton pickFrontVertexButton;
	final FixedSizeButton pickBackVertexButton;
	final FixedSizeButton pickXYZVertexButton;
	final FixedSizeButton pickPolygonButton;
	
	/** buttons for selecting view type */
	/* final FixedSizeButton vertexesOnlyButton;
	final FixedSizeButton wireframeOnlyButton;
	final FixedSizeButton fullRenderButton;
	final FixedSizeButton showCollisionVertexesButton;
	final FixedSizeButton perspectiveButton;
	final FixedSizeButton textureButton;
	final ButtonGroup viewTypeButtonGroup;
	*/
	
	/** buttons for creating or editing */
	/*
	final FixedSizeButton moveVertexButton;
	final FixedSizeButton newVertexDataButton;
	final FixedSizeButton newPolygonDataButton;
	final FixedSizeButton newTextureMapButton;
	final FixedSizeButton newPolygonButton;
	*/
	
	/** buttons for adding vertexes to working polygon */
	/*
	final FixedSizeButton addPolygonVertex1;
	final FixedSizeButton addPolygonVertex2;
	final FixedSizeButton addPolygonVertex3;
	final FixedSizeButton addPolygonVertex4;
	*/
	
	/** buttons for moving working vertex to a polygon vertex */
	/*
	final FixedSizeButton moveToPolygonVertex1;
	final FixedSizeButton moveToPolygonVertex2;
	final FixedSizeButton moveToPolygonVertex3;
	final FixedSizeButton moveToPolygonVertex4;	
	*/
	
	FixedSizeIconToggleButton test1;
	FixedSizeIconToggleButton test2;
	final ButtonGroup testButtonGroup;	

	ContentGenerator()
	{
        super("Content Generator");
        setSize(1000,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
		testButtonGroup = new ButtonGroup();
		test1 = new FixedSizeIconToggleButton(this,
				"/res/frontViewButton.PNG",
				"/res/frontViewButtonSelected.PNG"
				);
		
		test2 = new FixedSizeIconToggleButton(
//				new ImageIcon(getClass().getResource("/res/frontViewButton.PNG")),
//				new ImageIcon(getClass().getResource("/res/frontViewSelectedButton.PNG"))
			new ImageIcon(getClass().getResource("/res/leftViewButton.PNG")),
			new ImageIcon(getClass().getResource("/res/leftViewButtonSelected.PNG"))
			);		
//		JButton test3 = new JButton(new ImageIcon(getClass().getResource("/res/frontViewButton.PNG")));

		
		testButtonGroup.add(test1);
		testButtonGroup.add(test2);
		Box testBox = new Box(BoxLayout.Y_AXIS);
		testBox.add(test1);
		testBox.add(test2);
		getContentPane().add(testBox);
		getContentPane().validate();
		
	}
	
	
	
	/** main method allowing class to be run */
	public static void main(String[] args) {
		ContentGenerator myContentGenerator = new ContentGenerator();
		myContentGenerator.setVisible(true);
	}

}
