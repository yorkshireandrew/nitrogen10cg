package cg;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class ContentGenerator extends JFrame{
	
	/** buttons for selecting the view */
	
	FixedSizeIconToggleButton frontViewButton;
	FixedSizeIconToggleButton leftViewButton;
	FixedSizeIconToggleButton backViewButton;
	FixedSizeIconToggleButton rightViewButton;
	FixedSizeIconToggleButton topViewButton;
	FixedSizeIconToggleButton bottomViewButton;
	ButtonGroup viewButtonGroup;
	
	
	/** buttons for picking vertexes */
	
	FixedSizeButton pickFrontVertexButton;
	FixedSizeButton pickBackVertexButton;
	FixedSizeButton pickXYZVertexButton;
	FixedSizeButton pickPolygonButton;
	
	/** buttons for selecting view type */
	FixedSizeIconToggleButton vertexesOnlyButton;
	FixedSizeIconToggleButton wireframeOnlyButton;
	FixedSizeIconToggleButton fullRenderButton;
	FixedSizeIconToggleButton showCollisionVertexesButton;
	FixedSizeIconToggleButton perspectiveButton;
	FixedSizeIconToggleButton textureButton;
	ButtonGroup viewTypeButtonGroup;
	
	
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
		createViewButtons(testBox);
		createViewTypeButtons(testBox);
		getContentPane().add(testBox);
		getContentPane().validate();
		
	}
	
	
	
	/** main method allowing class to be run */
	public static void main(String[] args) {
		ContentGenerator myContentGenerator = new ContentGenerator();
		myContentGenerator.setVisible(true);
	}
	
	/** creates the view buttons in the container supplied */
	void createViewButtons(Container container)
	{
		Box topBox = new Box(BoxLayout.X_AXIS);
		Box midBox = new Box(BoxLayout.X_AXIS);
		Box bottomBox = new Box(BoxLayout.X_AXIS);
		
		// create top box
		topBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		topViewButton = new FixedSizeIconToggleButton(this,"/res/topViewButton.PNG","/res/topViewButtonSelected.PNG");
		topBox.add(topViewButton);
		topBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		topBox.add(new FixedSizeButton("/res/greySpacer.PNG"));

		// create mid box
		frontViewButton = new FixedSizeIconToggleButton(this,"/res/frontViewButton.PNG","/res/frontViewButtonSelected.PNG");
		midBox.add(frontViewButton);
		leftViewButton = new FixedSizeIconToggleButton(this,"/res/leftViewButton.PNG","/res/leftViewButtonSelected.PNG");
		midBox.add(leftViewButton);
		backViewButton = new FixedSizeIconToggleButton(this,"/res/backViewButton.PNG","/res/backViewButtonSelected.PNG");
		midBox.add(backViewButton);
		rightViewButton = new FixedSizeIconToggleButton(this,"/res/rightViewButton.PNG","/res/rightViewButtonSelected.PNG");
		midBox.add(rightViewButton);
		
		// create bottom box
		bottomBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		bottomViewButton = new FixedSizeIconToggleButton(this,"/res/bottomViewButton.PNG","/res/bottomViewButtonSelected.PNG");
		bottomBox.add(bottomViewButton);
		bottomBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		bottomBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		
		viewButtonGroup = new ButtonGroup();
		viewButtonGroup.add(topViewButton);
		viewButtonGroup.add(frontViewButton);
		viewButtonGroup.add(leftViewButton);
		viewButtonGroup.add(backViewButton);
		viewButtonGroup.add(rightViewButton);
		viewButtonGroup.add(bottomViewButton);
		frontViewButton.setSelected(true);
		
		Box outerBox = new Box(BoxLayout.Y_AXIS);
		outerBox.add(topBox);
		outerBox.add(midBox);
		outerBox.add(bottomBox);
		container.add(outerBox);	
	}
	
	void createViewTypeButtons(Container container)
	{
		Box outerBox = new Box(BoxLayout.X_AXIS);
		
		vertexesOnlyButton = new FixedSizeIconToggleButton(this,"/res/vertexesOnlyButton.PNG","/res/vertexesOnlySelectedButton.PNG");
		outerBox.add(vertexesOnlyButton);
		wireframeOnlyButton = new FixedSizeIconToggleButton(this,"/res/wireframeOnlyButton.PNG","/res/wireframeOnlySelectedButton.PNG");
		outerBox.add(wireframeOnlyButton);
		fullRenderButton = new FixedSizeIconToggleButton(this,"/res/fullRenderButton.PNG","/res/fullRenderSelectedButton.PNG");
		outerBox.add(fullRenderButton);
		showCollisionVertexesButton = new FixedSizeIconToggleButton(this,"/res/showCollisionVertexesButton.PNG","/res/showCollisionVertexesSelectedButton.PNG");
		outerBox.add(showCollisionVertexesButton);
		perspectiveButton = new FixedSizeIconToggleButton(this,"/res/perspectiveButton.PNG","/res/perspectiveSelectedButton.PNG");
		outerBox.add(perspectiveButton);
		textureButton = new FixedSizeIconToggleButton(this,"/res/textureButton.PNG","/res/textureSelectedButton.PNG");
		outerBox.add(textureButton);
		
		viewTypeButtonGroup = new ButtonGroup();
		viewTypeButtonGroup.add(vertexesOnlyButton);
		viewTypeButtonGroup.add(wireframeOnlyButton);
		viewTypeButtonGroup.add(fullRenderButton);
		vertexesOnlyButton.setSelected(true);
		
		container.add(outerBox);	
	}

}
