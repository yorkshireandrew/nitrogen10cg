package cg;

import javax.swing.*;
import javax.swing.event.*;

import modified_nitrogen1.Item;
import modified_nitrogen1.ItemFactory_Caching;
import modified_nitrogen1.NitrogenCreationException;
import modified_nitrogen1.RendererHelper;
import modified_nitrogen1.RendererTriplet;
import modified_nitrogen1.Renderer_SimpleSingleColour;
import modified_nitrogen1.Renderer_SimpleTexture;
import modified_nitrogen1.SharedImmutableSubItem;
import modified_nitrogen1.Transform;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import modified_nitrogen1.*;

public class ContentGenerator extends JFrame{
	
	// constants
	static final int APP_WIDTH = 1000;
	static final int APP_HEIGHT = 650;
	static final int EDIT_SCREEN_WIDTH 	= 700;
	static final int EDIT_SCREEN_HEIGHT = 600;
	static final int EDIT_SCREEN_SIZE 	= EDIT_SCREEN_WIDTH * EDIT_SCREEN_HEIGHT;

	/** the main screen */
	NitrogenContext nc;
	
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
	FixedSizeButton moveVertexButton;
	FixedSizeButton newVertexButton;
	FixedSizeButton newVertexDataButton;
	FixedSizeButton newPolygonDataButton;
	FixedSizeButton newTextureMapButton;
	FixedSizeButton newPolygonButton;
	
	/** TextBoxes containing xyz coordinates etc*/
	// working cursor/vertex related
	JTextField indexTextField;	
	JTextField xTextField;	
	JTextField yTextField;
	JTextField zTextField;
	
	// polygon vertex1
	JTextField vertex1IndexTextField;	
	JTextField vertex1xTextField;	
	JTextField vertex1yTextField;
	JTextField vertex1zTextField;
	
	/** buttons for adding vertexes to working polygon */	
	FixedSizeButton addPolygonVertex1;
	FixedSizeButton addPolygonVertex2;
	FixedSizeButton addPolygonVertex3;
	FixedSizeButton addPolygonVertex4;
	
	
	/** buttons for moving working vertex to a polygon vertex */	
	FixedSizeButton moveToPolygonVertex1;
	FixedSizeButton moveToPolygonVertex2;
	FixedSizeButton moveToPolygonVertex3;
	FixedSizeButton moveToPolygonVertex4;	
	
	
	FixedSizeIconToggleButton test1;
	FixedSizeIconToggleButton test2;
	final ButtonGroup testButtonGroup;	
	
	int viewDirection;
	static final int FRONT 	= 0;
	static final int LEFT 	= 1;
	static final int BACK 	= 2;
	static final int RIGHT 	= 3;
	static final int TOP 	= 4;
	static final int BOTTOM = 5;
	
//	String[] templateFileName = new String[6];
	TemplateModel[] templateModels = new TemplateModel[6];
	

	ContentGenerator()
	{
        super("Content Generator");
        setSize(APP_WIDTH,APP_HEIGHT);
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
		
		// create nitrogen context
		//int width, int height, float xClip, float yClip, float nearClip, float farClip
		nc = new NitrogenContext(EDIT_SCREEN_WIDTH,EDIT_SCREEN_HEIGHT,1,1,1, 1000);
        nc.cls(0xFF000000);        
        nc.repaint();
        
        // create templateModels for all six views
        for(int i = 0 ; i < 6 ; i++)
        {
        	templateModels[i] =  new TemplateModel(this);
        }
		
		createMenu();
		Box testBox = new Box(BoxLayout.Y_AXIS);
		createViewButtons(testBox);
		createNewPolygonVertexGUI(testBox);
		testBox.add(Box.createHorizontalGlue());
		Box outerBox = new Box(BoxLayout.X_AXIS);
		outerBox.add(nc);
		outerBox.add(Box.createHorizontalGlue());
		outerBox.add(testBox);
		getContentPane().add(outerBox);
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
		topBox.add(Box.createHorizontalGlue());
		// create mid box
		frontViewButton = new FixedSizeIconToggleButton(this,"/res/frontViewButton.PNG","/res/frontViewButtonSelected.PNG");
		midBox.add(frontViewButton);
		leftViewButton = new FixedSizeIconToggleButton(this,"/res/leftViewButton.PNG","/res/leftViewButtonSelected.PNG");
		midBox.add(leftViewButton);
		backViewButton = new FixedSizeIconToggleButton(this,"/res/backViewButton.PNG","/res/backViewButtonSelected.PNG");
		midBox.add(backViewButton);
		rightViewButton = new FixedSizeIconToggleButton(this,"/res/rightViewButton.PNG","/res/rightViewButtonSelected.PNG");
		midBox.add(rightViewButton);
		midBox.add(Box.createHorizontalGlue());		
		
		// create bottom box
		bottomBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		bottomViewButton = new FixedSizeIconToggleButton(this,"/res/bottomViewButton.PNG","/res/bottomViewButtonSelected.PNG");
		bottomBox.add(bottomViewButton);
		bottomBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		bottomBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		bottomBox.add(Box.createHorizontalGlue());		
		
		viewButtonGroup = new ButtonGroup();
		viewButtonGroup.add(topViewButton);
		viewButtonGroup.add(frontViewButton);
		viewButtonGroup.add(leftViewButton);
		viewButtonGroup.add(backViewButton);
		viewButtonGroup.add(rightViewButton);
		viewButtonGroup.add(bottomViewButton);
		frontViewButton.setSelected(true);
		viewDirection = FRONT;
		
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
		outerBox.add(Box.createHorizontalGlue());
		
		viewTypeButtonGroup = new ButtonGroup();
		viewTypeButtonGroup.add(vertexesOnlyButton);
		viewTypeButtonGroup.add(wireframeOnlyButton);
		viewTypeButtonGroup.add(fullRenderButton);
		vertexesOnlyButton.setSelected(true);
		
		container.add(outerBox);	
	}
	
	void createPickingButtons(Container container)
	{
		Box outerBox = new Box(BoxLayout.X_AXIS);
		
		pickFrontVertexButton = new FixedSizeButton("/res/pickFrontVertexButton.PNG");
		outerBox.add(pickFrontVertexButton);
		pickBackVertexButton = new FixedSizeButton("/res/pickBackVertexButton.PNG");
		outerBox.add(pickBackVertexButton);
		pickXYZVertexButton = new FixedSizeButton("/res/pickXYZVertexButton.PNG");
		outerBox.add(pickXYZVertexButton);
		pickPolygonButton = new FixedSizeButton("/res/pickPolygonButton.PNG");
		outerBox.add(pickPolygonButton);
		outerBox.add(Box.createHorizontalGlue());
		container.add(outerBox);	
	}
	
	void createNewItemComponentButtons(Container container)
	{
		Box outerBox = new Box(BoxLayout.X_AXIS);
		
		moveVertexButton = new FixedSizeButton("/res/moveVertexButton.PNG");
		outerBox.add(moveVertexButton);
		newVertexButton = new FixedSizeButton("/res/newVertexButton.PNG");
		outerBox.add(newVertexButton);
		newVertexDataButton = new FixedSizeButton("/res/newVertexDataButton.PNG");
		outerBox.add(newVertexDataButton);
		newPolygonButton = new FixedSizeButton("/res/newPolygonButton.PNG");
		outerBox.add(newPolygonButton);
		newPolygonDataButton = new FixedSizeButton("/res/newPolygonDataButton.PNG");
		outerBox.add(newPolygonDataButton);
		newPolygonDataButton = new FixedSizeButton("/res/newPolygonDataButton.PNG");
		outerBox.add(newPolygonDataButton);
		newTextureMapButton = new FixedSizeButton("/res/newTextureMapButton.PNG");
		outerBox.add(newTextureMapButton);
		outerBox.add(Box.createHorizontalGlue());
		container.add(outerBox);	
	}
	
	void createNewPolygonVertexGUI(Container container)
	{
		Box outerouterbox = new Box(BoxLayout.X_AXIS);
		Box outerBox = new Box(BoxLayout.Y_AXIS);
//		outerBox.setSize(300, 100);
		for(int i = 0; i < 4; i++)
		{
			PolygonVertexGUI pvgui = new PolygonVertexGUI(this, i);
			pvgui.createPolygonGUI(outerBox);		
		}
		outerBox.setBorder(BorderFactory.createLineBorder(Color.black));
		outerouterbox.add(outerBox);
		outerouterbox.add(Box.createHorizontalGlue());
		container.add(outerouterbox);
	}
	
	void createMenu()
	{
    // *** create menu ***
    JMenuBar my_menu = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenu edit = new JMenu("Edit");
    JMenuItem item;
    
    //
    file.add(item = new JMenuItem("Save..."));
//  item.addActionListener(new mySourceFileListener());  
    file.add(item = new JMenuItem("Save and clip..."));
//  item.addActionListener(new mySourceFileListener());  
    file.add(item = new JMenuItem("Load..."));
//  item.addActionListener(new mySourceFileListener());  
    file.add(item = new JMenuItem("Export..."));
//  item.addActionListener(new mySourceFileListener());  
    
    edit.add(item = new JMenuItem("Template..."));
    item.addActionListener(new TemplateAction(this));  
    edit.add(item = new JMenuItem("Circle..."));
    edit.add(item = new JMenuItem("Remove Polygon"));
    edit.add(item = new JMenuItem("Collision Vertexes..."));
    edit.add(item = new JMenuItem("Move Origin..."));
    edit.add(item = new JMenuItem("Scale..."));
    
    my_menu.add(file); 
    my_menu.add(edit);
    my_menu.add(Box.createHorizontalGlue());
    createViewTypeButtons(my_menu);
    my_menu.add(Box.createHorizontalGlue());
	createPickingButtons(my_menu);
	my_menu.add(Box.createHorizontalGlue());
	createNewItemComponentButtons(my_menu);
	
    
    this.setJMenuBar(my_menu);
	}
	
	void createLeftAlignedLabel(Container container, String st)
	{
		Box retval = new Box(BoxLayout.X_AXIS);
		retval.add(new JLabel(st));
		retval.add(Box.createHorizontalGlue());
		container.add(retval);	
	}
	
	void generatePixels()
	{
		nc.cls(templateModels[viewDirection].pixels);
		nc.createTestSquare();
		
		// test
        Transform t1	= new 	Transform(
    			null,
    			1f, 0f, 0f, 0f,
    			0f, 1f, 0f, 0f,
    			0f, 0f, 1f, 0f);
        
        Transform t2	= new 	Transform(
    			t1,
    			1f, 0f, 0f, 0f,
    			0f, 1f, 0f, 0f,
    			0f, 0f, 1f, -20f);
        
        // add renderers to RendererHelper
        Renderer_SimpleTexture str = new Renderer_SimpleTexture();
        RendererTriplet rt = new RendererTriplet(str);
        try
        {
        	RendererHelper.addRendererTriplet("str",rt);
        }
        catch(Exception e){System.out.println(e.getMessage());}

        Renderer_SimpleSingleColour sscr = new Renderer_SimpleSingleColour();           
        RendererTriplet sscrt = new RendererTriplet(sscr);
        try
        {
        	RendererHelper.addRendererTriplet("sscr",sscrt);
        }
        catch(Exception e){System.out.println(e.getMessage());}
        
        // create test item         
        SharedImmutableSubItem testItemSISI = null;        
        try{
        	
        	testItemSISI = new SharedImmutableSubItem("test1.txt");
        }
        catch(NitrogenCreationException e)
        {
        	e.printStackTrace();           	
        }
        
        Item.setItemFactory(new ItemFactory_Caching());
        
        Item i = Item.createItem(testItemSISI,t2);
        i.setVisibility(true); 
        t1.render(nc);
        
        
        
		templateModels[viewDirection].overlayTemplate(
				nc.pix, nc.zbuff);
		
		nc.repaint();
		
	}

}

class TemplateAction extends AbstractAction
{

	ContentGenerator cg;
	TemplateAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TemplateDialog td = new TemplateDialog(cg,cg.templateModels[cg.viewDirection]);
		td.setVisible(true);
	}
	
}
