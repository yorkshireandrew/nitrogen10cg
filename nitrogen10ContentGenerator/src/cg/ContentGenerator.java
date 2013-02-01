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
	static final int EDIT_SCREEN_WIDTH	= 700;
	static final int EDIT_SCREEN_HEIGHT = 600;
	static final int CONSTRAINED_BORDER_WIDTH = 10;
	static final int CONSTRAINED_BORDER_COLOUR = 0xFFFF0000;
	static final int CURSOR = 50;	// colour change caused by cursor

	// view direction enumeration
	int viewDirection;
	static final int FRONT 	= 0;
	static final int LEFT 	= 1;
	static final int BACK 	= 2;
	static final int RIGHT 	= 3;
	static final int TOP 	= 4;
	static final int BOTTOM = 5;
	
	// view type enumeration
	int viewType;
	static final int ORTHOGONAL_PROJECTION 	= 0;
	static final int PERSPECTIVE 			= 1;
	static final int TEXTURE_MAP 			= 2;

	// view type enumeration
	int viewDetail;
	static final int VERTEXES_ONLY 				= 0;
	static final int WIREFRAME 					= 1;
	static final int BACKSIDE_CULLED_WIREFRAME	= 2;	
	static final int FULLY_RENDERED				= 3;
	
	// do we display collision vertexes
	boolean showCollisionVertexes;
	
	// derived constants
	static final int EDIT_SCREEN_SIZE 	= EDIT_SCREEN_WIDTH * EDIT_SCREEN_HEIGHT;
	static final int EDIT_SCREEN_MIDX = EDIT_SCREEN_WIDTH / 2;
	static final int EDIT_SCREEN_MIDY = EDIT_SCREEN_HEIGHT / 2;
	
	/** the main screen */
	NitrogenContext nc;
	
	/** The SharedImuutableSubItem content being generated */
	SharedImmutableSubItem generatedSISI;
	/** Item used to render the generatedSISI. Gets re-constructed if the generatedSISI is altered */
	Item generatedItem;
	
	/** transform chain to alter view of generatedItem */
    Transform rootTransform;	
    Transform distTransform;   
    Transform turnTransform;   
    Transform climbTransform;    
    Transform rollTransform	;
    Transform viewDirectionTransform; 
     
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
	
	/** buttons for selecting view detail */
	FixedSizeIconToggleButton vertexesOnlyButton;
	FixedSizeIconToggleButton wireframeOnlyButton;
	FixedSizeIconToggleButton wireframeOnlyBacksideCulledButton;
	FixedSizeIconToggleButton fullRenderButton;
	FixedSizeIconToggleButton showCollisionVertexesButton;
	ButtonGroup viewDetailButtonGroup;
	
	/** buttons for selecting view type */
	FixedSizeIconToggleButton orthogonalProjectionButton;
	FixedSizeIconToggleButton perspectiveButton;
	FixedSizeIconToggleButton textureButton;
	ButtonGroup viewTypeButtonGroup;
	
	/** buttons for creating or editing */	
	FixedSizeButton moveVertexButton;
	FixedSizeButton newVertexButton;
	FixedSizeButton newVertexDataButton;
	FixedSizeButton newPolygonDataButton;
	FixedSizeButton newBacksideButton;
	FixedSizeButton newTextureMapButton;
	FixedSizeButton newPolygonButton;
	
	/** the template models controlled by the template dialog */
	TemplateModel[] templateModels = new TemplateModel[6];
	
	/** the polygon vertex UI */
	PolygonVertexView[] 	polygonVertexViews = new PolygonVertexView[4] ;

	WorkingVertexView 		workingVertexView;
	WorkingVertexModel 		workingVertexModel;
	
	int cursor_x = EDIT_SCREEN_MIDX;
	int cursor_y = EDIT_SCREEN_MIDY;
	
	ContentGeneratorController cgc;
	
	ContentGeneratorSISI contentGeneratorSISI;
	
	// default location of texture maps
	String resourceURL = "C:\\Documents and Settings\\andrew\\My Documents\\bombhead games";
	
	/** variable to hold polygon dialog choices between dialog openings */
	ContentGeneratorPolygon polygonDialogModel;
	
	ContentGenerator()
	{

		super("Content Generator");
        setSize(APP_WIDTH,APP_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //create a blank content
        contentGeneratorSISI = new ContentGeneratorSISI();
        
        cgc = new ContentGeneratorController(this);
        
        polygonDialogModel = new ContentGeneratorPolygon();
        		
		// create nitrogen context
		//int width, int height, float xClip, float yClip, float nearClip, float farClip
		nc = new NitrogenContext(EDIT_SCREEN_WIDTH,EDIT_SCREEN_HEIGHT,1,1,1, 1000);
        nc.cls(0xFF000000);        
        nc.repaint();
        
        // initially on ORTHOGONAL_PROJECTION
    	nc.contentGeneratorForcesNoPerspective = true;
    	
    	// initially on VERTEXES_ONLY
    	nc.contentGeneratorForcesNoCulling = true;
    	RendererTriplet.setPickingRenderer(new Renderer_Null());
    	nc.isPicking = true;
        
        // create templateModels for all six views
        for(int i = 0 ; i < 6 ; i++)
        {
        	templateModels[i] =  new TemplateModel(this);
        }
		
		createMenu();
		createWorld();
		
		// create a box to fill with various RHS controls
		Box rightHandControls = new Box(BoxLayout.Y_AXIS);
		
		// create the controls for standard view
		Box standardViewControls = new Box(BoxLayout.Y_AXIS);
		createViewButtons(standardViewControls);
		createWorkingVertexGUI(standardViewControls);
		createNewPolygonVertexGUI(standardViewControls);
		standardViewControls.add(Box.createVerticalGlue());
		
		// initially rightHandControls are for standard view
		rightHandControls.add(standardViewControls);
		
		// create whole user interface
		Box outerBox = new Box(BoxLayout.X_AXIS);
		outerBox.add(nc);
		outerBox.add(Box.createHorizontalGlue());
		outerBox.add(rightHandControls);
		getContentPane().add(outerBox);
		getContentPane().validate();	
		nc.addMouseListener(new ContentGeneratorMouseListener(this,cgc));
	
		// render edit area
		cgc.updateCursorFromWorkingVertex();
		renderEditArea();		
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
		topViewButton.addActionListener(cgc);	
		topBox.add(topViewButton);
		topBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		topBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		topBox.add(Box.createHorizontalGlue());
		
		// create mid box
		frontViewButton = new FixedSizeIconToggleButton(this,"/res/frontViewButton.PNG","/res/frontViewButtonSelected.PNG");
		frontViewButton.addActionListener(cgc);	
		midBox.add(frontViewButton);
		
		leftViewButton = new FixedSizeIconToggleButton(this,"/res/leftViewButton.PNG","/res/leftViewButtonSelected.PNG");
		leftViewButton.addActionListener(cgc);
		midBox.add(leftViewButton);
		
		backViewButton = new FixedSizeIconToggleButton(this,"/res/backViewButton.PNG","/res/backViewButtonSelected.PNG");
		backViewButton.addActionListener(cgc);
		midBox.add(backViewButton);
		
		rightViewButton = new FixedSizeIconToggleButton(this,"/res/rightViewButton.PNG","/res/rightViewButtonSelected.PNG");
		rightViewButton.addActionListener(cgc);
		midBox.add(rightViewButton);
		midBox.add(Box.createHorizontalGlue());		
		
		// create bottom box
		bottomBox.add(new FixedSizeButton("/res/greySpacer.PNG"));
		bottomViewButton = new FixedSizeIconToggleButton(this,"/res/bottomViewButton.PNG","/res/bottomViewButtonSelected.PNG");
		bottomViewButton.addActionListener(cgc);
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
		
		// add view type buttons
		orthogonalProjectionButton = new FixedSizeIconToggleButton(this,"/res/orthogonalProjectionButton.PNG","/res/orthogonalProjectionSelectedButton.PNG");
		orthogonalProjectionButton.setToolTipText("Orthogonal projection view");
		outerBox.add(orthogonalProjectionButton);
		
		perspectiveButton = new FixedSizeIconToggleButton(this,"/res/perspectiveButton.PNG","/res/perspectiveSelectedButton.PNG");
		perspectiveButton.setToolTipText("Perspective view");
		outerBox.add(perspectiveButton);
		
		textureButton = new FixedSizeIconToggleButton(this,"/res/textureButton.PNG","/res/textureSelectedButton.PNG");
		textureButton.setToolTipText("Texture map view");
		outerBox.add(textureButton);
		
		outerBox.add(Box.createHorizontalGlue());
		
		// add view detail buttons
		vertexesOnlyButton = new FixedSizeIconToggleButton(this,"/res/vertexesOnlyButton.PNG","/res/vertexesOnlySelectedButton.PNG");
		vertexesOnlyButton.setAction(new VertexesOnlyToolbarAction(this));
		vertexesOnlyButton.setToolTipText("Vertexes only");
		outerBox.add(vertexesOnlyButton);
		
		wireframeOnlyButton = new FixedSizeIconToggleButton(this,"/res/wireframeOnlyButton.PNG","/res/wireframeOnlySelectedButton.PNG");
		wireframeOnlyButton.setAction(new FullWireFrameToolbarAction(this));
		wireframeOnlyButton.setToolTipText("Full wireframe");
		outerBox.add(wireframeOnlyButton);
		
		wireframeOnlyBacksideCulledButton = new FixedSizeIconToggleButton(this,"/res/wireframeOnlyBacksideCulledButton.PNG","/res/wireframeOnlyBacksideCulledSelectedButton.PNG");
		wireframeOnlyBacksideCulledButton.setAction(new BacksideCulledWireFrameToolbarAction(this));
		wireframeOnlyBacksideCulledButton.setToolTipText("Backside culled wireframe");
		outerBox.add(wireframeOnlyBacksideCulledButton);
		
		fullRenderButton = new FixedSizeIconToggleButton(this,"/res/fullRenderButton.PNG","/res/fullRenderSelectedButton.PNG");
		fullRenderButton.setAction(new FullyRenderedToolbarAction(this));	
		fullRenderButton.setToolTipText("Fully rendered");
		outerBox.add(fullRenderButton);
		
		showCollisionVertexesButton = new FixedSizeIconToggleButton(this,"/res/showCollisionVertexesButton.PNG","/res/showCollisionVertexesSelectedButton.PNG");
		showCollisionVertexesButton.setToolTipText("Show collision vertexes");
		outerBox.add(showCollisionVertexesButton);
		outerBox.add(Box.createHorizontalGlue());
		
		viewTypeButtonGroup = new ButtonGroup();
		viewTypeButtonGroup.add(orthogonalProjectionButton);
		viewTypeButtonGroup.add(perspectiveButton);	
		viewTypeButtonGroup.add(textureButton);	
		orthogonalProjectionButton.setSelected(true);
		
		viewDetailButtonGroup = new ButtonGroup();
		viewDetailButtonGroup.add(vertexesOnlyButton);
		viewDetailButtonGroup.add(wireframeOnlyButton);
		viewDetailButtonGroup.add(wireframeOnlyBacksideCulledButton);	
		viewDetailButtonGroup.add(fullRenderButton);
		vertexesOnlyButton.setSelected(true);
		
		container.add(outerBox);	
	}
	
	void createPickingButtons(Container container)
	{
		Box outerBox = new Box(BoxLayout.X_AXIS);
		
		pickFrontVertexButton = new FixedSizeButton("/res/pickFrontVertexButton.PNG");
		pickFrontVertexButton.addActionListener(cgc);
		pickFrontVertexButton.setToolTipText("Pick the for-most vertex under the cursor");
		outerBox.add(pickFrontVertexButton);
		
		pickBackVertexButton = new FixedSizeButton("/res/pickBackVertexButton.PNG");
		pickBackVertexButton.addActionListener(cgc);
		pickBackVertexButton.setToolTipText("Pick the rear-most vertex under the cursor");
		outerBox.add(pickBackVertexButton);
		
		pickXYZVertexButton = new FixedSizeButton("/res/pickXYZVertexButton.PNG");
		pickXYZVertexButton.addActionListener(cgc);
		pickXYZVertexButton.setToolTipText("Pick the vertex that is closest in XYZ terms to the cursor");
		outerBox.add(pickXYZVertexButton);
			
		pickPolygonButton = new FixedSizeButton("/res/pickPolygonButton.PNG");
		pickPolygonButton.addActionListener(cgc);
		pickPolygonButton.setToolTipText("Pick the polygon under the cursor");
		outerBox.add(pickPolygonButton);
		outerBox.add(Box.createHorizontalGlue());
		container.add(outerBox);	
	}
	
	void createNewItemComponentButtons(Container container)
	{
		Box outerBox = new Box(BoxLayout.X_AXIS);
		
		// new vertex button
		newVertexButton = new FixedSizeButton("/res/newVertexButton.PNG");
		newVertexButton.addActionListener(cgc);
		newVertexButton.setToolTipText("Create a new vertex");	
		outerBox.add(newVertexButton);
		
		// move vertex button
		moveVertexButton = new FixedSizeButton("/res/moveVertexButton.PNG");
		moveVertexButton.addActionListener(cgc);
		moveVertexButton.setToolTipText("Move the selected vertex");
		outerBox.add(moveVertexButton);
		
		// new vertex data button
		newVertexDataButton = new FixedSizeButton("/res/newVertexDataButton.PNG");
		newVertexDataButton.addActionListener( new VertexDataToolbarAction(this));
		newVertexDataButton.setToolTipText("Create a new vertex data datablock");
		outerBox.add(newVertexDataButton);
		
		// new polygon data button
		newPolygonDataButton = new FixedSizeButton("/res/newPolygonDataButton.PNG");
		newPolygonDataButton.addActionListener( new PolygonDataToolbarAction(this));
		newPolygonDataButton.setToolTipText("Create a new polygon data datablock");
		outerBox.add(newPolygonDataButton);
		
		// new backside button
		newBacksideButton = new FixedSizeButton("/res/newBacksideButton.PNG");
		newBacksideButton.addActionListener(new BacksideToolbarAction(this));
		newBacksideButton.setToolTipText("Create a new backside, used for culling");
		outerBox.add(newBacksideButton);
		
		// new polygon button
		newPolygonButton = new FixedSizeButton("/res/newPolygonButton.PNG");
		newPolygonButton.addActionListener(new polygonToolbarAction(this));
		newPolygonButton.setToolTipText("Create a new polygon");
		outerBox.add(newPolygonButton);
		
		// new texture map button
		newTextureMapButton = new FixedSizeButton("/res/newTextureMapButton.PNG");
		newTextureMapButton.addActionListener(new TextureMapToolbarAction(this));
		newTextureMapButton.setToolTipText("Create a new texture map reference");
		outerBox.add(newTextureMapButton);
		
		outerBox.add(Box.createHorizontalGlue());
		container.add(outerBox);	
	}
	
	void createNewPolygonVertexGUI(Container container)
	{
		Box outerouterbox = new Box(BoxLayout.X_AXIS);
		Box outerBox = new Box(BoxLayout.Y_AXIS);
		
		for(int i = 0 ; i < 4; i++)
		{		
			polygonVertexViews[i] = new PolygonVertexView(this);	
			polygonVertexViews[i].createPolygonGUI(outerBox);
		}

		outerBox.setBorder(BorderFactory.createLineBorder(Color.black));
		outerouterbox.add(outerBox);
		outerouterbox.add(Box.createHorizontalGlue());
		container.add(outerouterbox);
	}
	
	void createWorkingVertexGUI(Container container)
	{
		workingVertexModel = new WorkingVertexModel();
		workingVertexView = new WorkingVertexView(this, workingVertexModel);
		workingVertexView.createWorkingVertexGUI(container);
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
    item.addActionListener(new TemplateMenuItemAction(this));  
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
	
	/*
	// created for test perposes during early development
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
		
		addConstrainedBorder(nc);
		addCursor(nc,100,100);
		nc.repaint();	
	}
	*/
	
	void renderEditArea()
	{
		
		if(viewType == ORTHOGONAL_PROJECTION)
		{
			// fill the background using template model pixels
			nc.cls(templateModels[viewDirection].pixels);
			if(viewDetail != VERTEXES_ONLY)
			{
				System.out.println("trace my arse");
			}
			// call render to update scene graph
			rootTransform.render(nc);
		
			if(viewDetail == VERTEXES_ONLY)
			{
				generatedItem.calculateVertexes();
				generatedItem.renderVertexes(nc);
			}
		
			// overlay the template on the rendered item
			templateModels[viewDirection].overlayTemplate(
				nc.pix, nc.zbuff);
		
			showConstrainedBorder(nc);
			showCursor(nc,cursor_x,cursor_y);
			showWorkingVertex();
			nc.repaint();
		}
	}
	
	/** adds the constrained border onto edit area */
	void showConstrainedBorder(NitrogenContext nc)
	{
		int[] pixels = nc.pix;
		int width = nc.w;
		int height = nc.h;
		
		int[] toprow = new int[width];
		for(int i = 0; i < width; i++)toprow[i] = CONSTRAINED_BORDER_COLOUR;
		for(int y = 0; y < CONSTRAINED_BORDER_WIDTH; y++)
		{
			System.arraycopy(toprow, 0, pixels, (width * y), width);			
		}
		
		for(int y = 0; y < height; y++)
		{
			System.arraycopy(toprow, 0, pixels, (width * y), CONSTRAINED_BORDER_WIDTH);
		}	
		
		for(int y = 0; y < CONSTRAINED_BORDER_WIDTH; y++)
		{
			// zero markers
			pixels[width * y + EDIT_SCREEN_MIDX] = 0xFFFFFFFF;
			pixels[width * EDIT_SCREEN_MIDY + y] = 0xFFFFFFFF;		
		}
	}
	
	/** add cursor onto edit area */
	void showCursor(NitrogenContext nc, int x_in, int y_in)
	{
		int[] pixels = nc.pix;
		int width = nc.w;
		int height = nc.h;
		
		// generate vertical cursor
		for(int y = 0; y < height; y++)
		{
			int index = y*width + x_in;
			pixels[index] = cursorColour(pixels[index]);
		}
		
		// generate horizontal cursor
		for(int x = 0; x < width; x++)
		{
			int index = y_in*width + x;
			pixels[index] = cursorColour(pixels[index]);
		}		
		
		
	}
	
	/** Generates pixel colours for cursor, called by addCursor */
	int cursorColour(int pixelColour)
	{
		// extract the pixel colour
		int pixelBlue 	= (pixelColour & 0x0000FF);
		int pixelGreen = (pixelColour & 0x00FF00) >> 8;
		int pixelRed 	= (pixelColour& 0xFF0000) >> 16;
		
		int pixelBluePlus = pixelBlue + CURSOR;
		int pixelGreenPlus = pixelGreen + CURSOR;
		int pixelRedPlus = pixelRed + CURSOR;
		
		int vote = 0;
		if(pixelBluePlus > 255)
		{
			pixelBluePlus = 255;
			vote++;
		}
		if(pixelGreenPlus > 255)
		{
			pixelGreenPlus = 255;
			vote++;
		}
		if(pixelRedPlus > 255) 
		{
			pixelRedPlus = 255;
			vote++;
		}
		
		if(vote < 2)
		{
			int retval = 0xFF000000 | (pixelRedPlus << 16) | (pixelGreenPlus << 8) | pixelBluePlus;	
			return retval;
		}
		
		int pixelBlueMinus = pixelBlue - CURSOR;
		int pixelGreenMinus = pixelGreen - CURSOR;
		int pixelRedMinus = pixelRed - CURSOR;		
		
		if(pixelBlueMinus < 0)pixelBlueMinus = 0;
		if(pixelGreenMinus < 0)pixelGreenMinus = 0;
		if(pixelRedMinus < 0)pixelRedMinus = 0;
		
		int retval = 0xFF000000 | (pixelRedMinus << 16) | (pixelGreenMinus << 8) | pixelBlueMinus;	
		return retval;
	}
	
	void showWorkingVertex()
	{
		if(workingVertexModel.pickedVertex != null)
		{
			generatedItem.renderVertex(nc, contentGeneratorSISI.immutableVertexList.indexOf(workingVertexModel.pickedVertex));
		}
	}
	
	// initialises the world that the edit area NitrogenContext renders
	void createWorld()
	{	
		// create transform chain with generatedItem on top
		// To alter the view we alter these transforms
	    rootTransform	= new 	Transform(
				null,
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
		
	    distTransform	= new 	Transform(
				rootTransform,
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, -500f);
	    
	    turnTransform	= new 	Transform(
				distTransform,
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
	    
	    climbTransform	= new 	Transform(
				turnTransform,
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
	    
	    rollTransform	= new 	Transform(
				climbTransform,
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
	    
	    viewDirectionTransform	= new 	Transform(
				rollTransform,
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f); 
	    // create Renderers
	    createRenderers();
		// create basic (mutable) SISI
		generatedSISI = new SharedImmutableSubItem();	
		// Let garbage collector be responsible for dead Items              
        Item.setItemFactory(new ItemFactory_Default());
        // create default Item
        generatedItem = Item.createItem(generatedSISI,viewDirectionTransform);
        generatedItem.setVisibility(true);
        generatedItem.setName("default Item");
	}
	
	/** method to set up all the RendererTriplets
	 * available in the ContentGenerator. Amend this
	 * if you need to add in more RendererTriplets
	 */
	void createRenderers()
	{
        // RendererTriplet using just the SimpleTexture renderer
		Renderer_SimpleTexture str = new Renderer_SimpleTexture();
        RendererTriplet rt = new RendererTriplet(str);
        try
        {
        	RendererHelper.addRendererTriplet("str",rt);
        }
        catch(Exception e){System.out.println(e.getMessage());}

        // RendererTriplet using just the SimpleSingleColour renderer 
        Renderer_SimpleSingleColour sscr = new Renderer_SimpleSingleColour();           
        RendererTriplet sscrt = new RendererTriplet(sscr);
        try
        {
        	RendererHelper.addRendererTriplet("sscr",sscrt);
        }
        catch(Exception e){System.out.println(e.getMessage());}		
	}
	

}
	

