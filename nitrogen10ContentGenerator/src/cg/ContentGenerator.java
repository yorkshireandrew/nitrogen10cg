package cg;

import javax.swing.*;
import javax.swing.event.*;

import modified_nitrogen1.Item;
import modified_nitrogen1.ItemFactory_Caching;
import modified_nitrogen1.NitrogenContext;
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
	
	private static final long serialVersionUID = -3085555225999484235L;
	
	// constants
	static final int APP_WIDTH = 1000;
	static final int APP_HEIGHT = 650;
	//static final int EDIT_SCREEN_WIDTH	= 700;
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
	
	/** sliders for perspective view */
	JSlider	distSlider;
	JSlider turnSlider;
	JSlider climbSlider;
	JSlider rollSlider;
	
	/** buttons for perspective view */
	JButton setNearRendererDistanceButton;
	JButton setNearRendererOnButton;
	JButton setNearRendererOffButton;
	JButton setFarRendererDistanceButton;
	JButton setImprovedDetailDistanceButton;
	JButton setHLPBreakingDistanceButton;
	JButton setHLPBreakingOnButton;
	JButton setHLPBreakingOffButton;
	JButton setBillboardOrientationDistanceButton;
	JButton setFarPlaneDistanceButton;
	
	/** ListBox for textureMap view */
	JComboBox textureMapCombo;
	JTextField textureMapX;
	JTextField textureMapY;
	
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
	String lastPolygonEdited = "";
	
	
	/** Box in which rightHandControls for orthogonal view type are kept */
	Box orthogonalViewTypeControls;
	/** Box in which rightHandControls for perspective view type are kept */
	Box perspectiveViewTypeControls;
	/** Box in which rightHandControls for texture map view type are kept */
	Box textureMapViewTypeControls;
	
	/** Box in which right hand controls are held */
	Box rightHandControls;
	
	/** Box which holds edit area (NitrogenContext) so it can be changed by a file load */
	Box editScreenBox;
	
	int[] textureMapPixels;
	int textureMapXMax;
	int textureMapYMax;

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
		nc = new NitrogenContext(EDIT_SCREEN_WIDTH,EDIT_SCREEN_HEIGHT,1f,0.85f,1, 1000000);
//		nc = new NitrogenContext(EDIT_SCREEN_WIDTH,EDIT_SCREEN_HEIGHT,1,1,1, 1000);

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
        	templateModels[i] =  new TemplateModel();
        }
		
		createMenu();
		createWorld();
		

		
		// create the controls for standard view
		createOrthogonalViewControls();
		
		// create the controls for perspective view
		createPerspectiveViewControls();
		
		// create the controls for textureMap view
		createTextureMapViewControls();
		
		// create a box to fill with various RHS controls
		rightHandControls = new Box(BoxLayout.Y_AXIS);
		
		// initially rightHandControls are for standard view
		rightHandControls.add(orthogonalViewTypeControls);
			
		// create edit screen box, so nc UI can be replaced by file load
		editScreenBox = new Box(BoxLayout.X_AXIS);
		editScreenBox.add(nc);
		
		// create whole user interface
		Box outerBox = new Box(BoxLayout.X_AXIS);
		outerBox.add(editScreenBox);
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
		orthogonalProjectionButton.setAction(cgc);
		orthogonalProjectionButton.setToolTipText("Orthogonal projection view");
		outerBox.add(orthogonalProjectionButton);
		
		perspectiveButton = new FixedSizeIconToggleButton(this,"/res/perspectiveButton.PNG","/res/perspectiveSelectedButton.PNG");
		perspectiveButton.setAction(cgc);
		perspectiveButton.setToolTipText("Perspective view");
		outerBox.add(perspectiveButton);
		
		textureButton = new FixedSizeIconToggleButton(this,"/res/textureButton.PNG","/res/textureSelectedButton.PNG");
		textureButton.setAction(cgc);
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
		showCollisionVertexesButton.setAction(new ShowCollisionVertexesToolbarAction(this));
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
		newPolygonButton.addActionListener(new PolygonToolbarAction(this));
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
    item.addActionListener(new SaveMenuItemAction(this));
    file.add(item = new JMenuItem("Load..."));
    item.addActionListener(new LoadMenuItemAction(this)); 
    file.add(item = new JMenuItem("Export..."));
    item.addActionListener(new ExportMenuItemAction(this));  
    file.add(item = new JMenuItem("Delete unused vertexes"));
    item.addActionListener(new DeleteUnusedVertexesMenuItemAction(this)); 
    file.add(item = new JMenuItem("Delete unused stuff")); 
    item.addActionListener(new DeleteUnusedStuffMenuItemAction(this));
    
    edit.add(item = new JMenuItem("Template..."));
    item.addActionListener(new TemplateMenuItemAction(this));
    edit.add(item = new JMenuItem("Circle..."));
    edit.add(item = new JMenuItem("Remove Polygon"));
    edit.add(item = new JMenuItem("Collision Vertexes..."));
    item.addActionListener(new CollisionVertexesMenuItemAction(this)); 
    edit.add(item = new JMenuItem("Near and Far Polygons..."));
    item.addActionListener(new NearFarMenuItemAction(this));  
    edit.add(item = new JMenuItem("Move Origin"));
    item.addActionListener(new MoveOriginMenuItemAction(this));  
    edit.add(item = new JMenuItem("Scale..."));
    item.addActionListener(new ScaleMenuItemAction(this)); 
    
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
	
	void renderEditArea()
	{
		
		if(viewType == ORTHOGONAL_PROJECTION)
		{
			// fill the background using template model pixels
			nc.cls(templateModels[viewDirection].pixels);

			// call render to update scene graph
			nc.transparencyPass = false;
			rootTransform.render(nc);
			nc.transparencyPass = true;
			rootTransform.render(nc);
			nc.transparencyPass = false;
			
		
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
			if(showCollisionVertexes)
			{
				showCollisionVertexes();
			}
			nc.repaint();
		}
		
		if(viewType == PERSPECTIVE)
		{
			// fill the background using template model pixels
			nc.cls(0xFF000000);
	
			// call render to update scene graph
			rootTransform.render(nc);
		
			if(viewDetail == VERTEXES_ONLY)
			{
				generatedItem.calculateVertexes();
				generatedItem.renderVertexes(nc);
			}

			showWorkingVertex();
			nc.repaint();
		}
		
		if(viewType == TEXTURE_MAP)
		{
			if(textureMapPixels == null)
			{
				nc.cls(0xFF000000);
			}
			else
			{
				nc.cls(textureMapPixels);
			}
			
			showCursor(nc,cursor_x,cursor_y);
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
	
	void writeToFile(ObjectOutputStream out) throws IOException
	{
		
		out.writeInt(viewDirection);
		out.writeInt(viewType);
		out.writeInt(viewDetail);
		out.writeBoolean(showCollisionVertexes);
		
		// Item gets constructed and added during loading
		viewDirectionTransform.remove(generatedItem);
		
		out.writeObject(rootTransform);
		out.writeObject(distTransform);
		out.writeObject(turnTransform);
		out.writeObject(climbTransform);	
		out.writeObject(rollTransform);
		out.writeObject(viewDirectionTransform); 
		
		out.writeObject(templateModels[0]);
		out.writeObject(templateModels[1]);
		out.writeObject(templateModels[2]);
		out.writeObject(templateModels[3]);
		out.writeObject(templateModels[4]);
		out.writeObject(templateModels[5]);
		
		// write this now in case polygonVertexViews depend on it
		// during loading	
		out.writeObject(contentGeneratorSISI);
		
		ImmutableVertex 
		iv = polygonVertexViews[0].pvm;
		out.writeObject(iv);
		iv = polygonVertexViews[1].pvm;
		out.writeObject(iv);
		iv = polygonVertexViews[2].pvm;
		out.writeObject(iv);
		iv = polygonVertexViews[3].pvm;
		out.writeObject(iv);	
		
		out.writeObject(workingVertexModel);
		
		out.writeObject(resourceURL);
		
		out.writeObject(polygonDialogModel);
		
		// done this but need to push down because of dependencies
		out.writeObject(nc);
	}
	
	void readFromFile(ObjectInputStream in) throws IOException
	{
		
		// read the viewDirection and set the UI
		viewDirection = in.readInt();
		frontViewButton.setSelected(false);
		leftViewButton.setSelected(false);
		backViewButton.setSelected(false);
		rightViewButton.setSelected(false);
		topViewButton.setSelected(false);
		bottomViewButton.setSelected(false);	
		if(viewDirection == FRONT) frontViewButton.setSelected(true);
		if(viewDirection == LEFT) leftViewButton.setSelected(true);
		if(viewDirection == BACK) backViewButton.setSelected(true);
		if(viewDirection == RIGHT) rightViewButton.setSelected(true);
		if(viewDirection == TOP) topViewButton.setSelected(true);
		if(viewDirection == BOTTOM) bottomViewButton.setSelected(true);
		
		// read viewType and set the UI
		viewType = in.readInt();
		orthogonalProjectionButton.setSelected(false);
		perspectiveButton.setSelected(false);
		textureButton.setSelected(false);		
		if(viewType == ORTHOGONAL_PROJECTION) orthogonalProjectionButton.setSelected(true);
		if(viewType == PERSPECTIVE) perspectiveButton.setSelected(true);
		if(viewType == TEXTURE_MAP) textureButton.setSelected(true);

		// read the viewDetail and set the UI
		viewDetail = in.readInt(); 	
		vertexesOnlyButton.setSelected(false);
		wireframeOnlyButton.setSelected(false);
		wireframeOnlyBacksideCulledButton.setSelected(false);
		fullRenderButton.setSelected(false);
		if(viewDetail == VERTEXES_ONLY)vertexesOnlyButton.setSelected(true);
		if(viewDetail == WIREFRAME )wireframeOnlyButton.setSelected(true);
		if(viewDetail == BACKSIDE_CULLED_WIREFRAME )wireframeOnlyBacksideCulledButton.setSelected(true);
		if(viewDetail == FULLY_RENDERED )fullRenderButton.setSelected(true);		
		showCollisionVertexes = in.readBoolean();
		showCollisionVertexesButton.setSelected(showCollisionVertexes);
	
		// read the transforms 
		// TO DO align the perspective UI components
		try{
			rootTransform = (Transform)in.readObject();
			distTransform = (Transform)in.readObject();
			turnTransform = (Transform)in.readObject();
			climbTransform = (Transform)in.readObject();
			rollTransform = (Transform)in.readObject();
			viewDirectionTransform = (Transform)in.readObject(); 
		} catch (ClassNotFoundException e) {e.printStackTrace();}	
		
		// read the template models
		// template dialog is created in response to UI 
		// so it will always be updated to reflect the model
		templateModels = new TemplateModel[6];
		try{
			templateModels[0] = (TemplateModel) in.readObject();			
			templateModels[1] = (TemplateModel) in.readObject();			
			templateModels[2] = (TemplateModel) in.readObject();			
			templateModels[3] = (TemplateModel) in.readObject();			
			templateModels[4] = (TemplateModel) in.readObject();			
			templateModels[5] = (TemplateModel) in.readObject();			
		} catch (ClassNotFoundException e) {e.printStackTrace();}
		templateModels[0].setUpTransientFields();
		templateModels[1].setUpTransientFields();
		templateModels[2].setUpTransientFields();
		templateModels[3].setUpTransientFields();
		templateModels[4].setUpTransientFields();
		templateModels[5].setUpTransientFields();
		
		// read and set up the the ContentGeneratorSISI 
		try {
			contentGeneratorSISI = (ContentGeneratorSISI) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		generatedSISI = contentGeneratorSISI.generateSISI();
		generatedItem = Item.createItem(generatedSISI, viewDirectionTransform);
	
		try {
			polygonVertexViews[0].pvm =(ImmutableVertex) in.readObject();
			polygonVertexViews[1].pvm =(ImmutableVertex) in.readObject();
			polygonVertexViews[2].pvm =(ImmutableVertex) in.readObject();
			polygonVertexViews[3].pvm =(ImmutableVertex) in.readObject();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}		
		polygonVertexViews[0].updateFromModel();
		polygonVertexViews[1].updateFromModel();
		polygonVertexViews[2].updateFromModel();
		polygonVertexViews[3].updateFromModel();

		try {
			workingVertexModel = (WorkingVertexModel)in.readObject();
		} catch (ClassNotFoundException e1) {e1.printStackTrace();}
		workingVertexView.workingVertexModel = workingVertexModel;
		workingVertexView.workingVertexController.workingVertexModel = workingVertexModel;
		cgc.updateCursorFromWorkingVertex();
		
		try {
			resourceURL = (String) in.readObject();
		} catch (ClassNotFoundException e1) {e1.printStackTrace();}

		try {
			polygonDialogModel = (ContentGeneratorPolygon) in.readObject();
		} catch (ClassNotFoundException e1) {e1.printStackTrace();}

		
		// read and set up the the nc 
		try {
			nc = (NitrogenContext) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// attach listener
		MouseListener[] mouseListeners = nc.getMouseListeners();
		while(mouseListeners.length > 0)
		{
			MouseListener ml = nc.getMouseListeners()[0];
			if(ml != null)nc.removeMouseListener(ml);
			mouseListeners = nc.getMouseListeners();
		}

		nc.addMouseListener(new ContentGeneratorMouseListener(this,cgc));
		nc.setUpTransientFields();
		
		// replace the nc on the UI with the loaded one
		editScreenBox.removeAll();
		editScreenBox.add(nc);
		editScreenBox.validate();
		
		cgc.updateGeneratedItemAndEditArea();
	}
	
	void  createOrthogonalViewControls()
	{	
		orthogonalViewTypeControls = new Box(BoxLayout.Y_AXIS);
		createViewButtons(orthogonalViewTypeControls);
		createWorkingVertexGUI(orthogonalViewTypeControls);
		createNewPolygonVertexGUI(orthogonalViewTypeControls);
		orthogonalViewTypeControls.add(Box.createVerticalGlue());
	}
	
	void  createPerspectiveViewControls()
	{
		// initialise the distance slider
		distSlider = new JSlider();
		distSlider.setModel(new DefaultBoundedRangeModel(1000,1,0,4000));
		distSlider.setMinorTickSpacing(500);
		distSlider.setPaintTicks(true);
		distSlider.addChangeListener(cgc);
		
		// initialise the turn slider
		turnSlider = new JSlider();
		turnSlider.setModel(new DefaultBoundedRangeModel(0,1,-1800,1800));
		turnSlider.setMinorTickSpacing(300);
		turnSlider.setPaintTicks(true);
		turnSlider.addChangeListener(cgc);
		
		// initialise the climb slider
		climbSlider = new JSlider();
		climbSlider.setModel(new DefaultBoundedRangeModel(0,1,-1800,1800));
		climbSlider.setMinorTickSpacing(300);
		climbSlider.setPaintTicks(true);
		climbSlider.addChangeListener(cgc);
		
		// initialise the roll slider
		rollSlider = new JSlider();
		rollSlider.setModel(new DefaultBoundedRangeModel(0,1,-1800,1800));
		rollSlider.setMinorTickSpacing(300);
		rollSlider.setPaintTicks(true);
		rollSlider.addChangeListener(cgc);
		
		JLabel distLabel = new JLabel("Distance");
		JLabel turnLabel = new JLabel("Turn");
		JLabel climbLabel = new JLabel("Climb");
		JLabel rollLabel = new JLabel("Roll");
		
		setNearRendererDistanceButton 	= new JButton();
		setNearRendererDistanceButton.setAction(cgc);
		setNearRendererDistanceButton.setText("Near Render");
		
		setNearRendererOffButton = new JButton();
		setNearRendererOffButton.setAction(cgc);
		setNearRendererOffButton.setText("OFF");
		setNearRendererOnButton = new JButton();
		setNearRendererOnButton.setAction(cgc);
		setNearRendererOnButton.setText("ON");
		
		setFarRendererDistanceButton 	= new JButton();
		setFarRendererDistanceButton.setAction(cgc);
		setFarRendererDistanceButton.setText("Far Render");
		
		setImprovedDetailDistanceButton = new JButton();
		setImprovedDetailDistanceButton.setAction(cgc);
		setImprovedDetailDistanceButton.setText("Level of Detail");
		
		setHLPBreakingDistanceButton	= new JButton();
		setHLPBreakingDistanceButton.setAction(cgc);
		setHLPBreakingDistanceButton.setText("HLP Breaking");
		setHLPBreakingOffButton = new JButton();
		setHLPBreakingOffButton.setAction(cgc);
		setHLPBreakingOffButton.setText("OFF");
		setHLPBreakingOnButton = new JButton();
		setHLPBreakingOnButton.setAction(cgc);
		setHLPBreakingOnButton.setText("ON");
		
		setBillboardOrientationDistanceButton = new JButton();
		setBillboardOrientationDistanceButton.setAction(cgc);
		setBillboardOrientationDistanceButton.setText("Billboarding");
		
		setFarPlaneDistanceButton 		= new JButton();
		setFarPlaneDistanceButton.setAction(cgc);
		setFarPlaneDistanceButton.setText("Far Plane");
		
		// add the sliders
		perspectiveViewTypeControls = Box.createVerticalBox();
		perspectiveViewTypeControls.add(distLabel);
		perspectiveViewTypeControls.add(distSlider);
		perspectiveViewTypeControls.add(turnLabel);
		perspectiveViewTypeControls.add(turnSlider);
		perspectiveViewTypeControls.add(climbLabel);
		perspectiveViewTypeControls.add(climbSlider);
		perspectiveViewTypeControls.add(rollLabel);
		perspectiveViewTypeControls.add(rollSlider);
		perspectiveViewTypeControls.add(Box.createVerticalGlue());
		
		// create boxes for near render and HLP render
		Box nearRenderBox = Box.createHorizontalBox();
		nearRenderBox.add(setNearRendererDistanceButton);
		nearRenderBox.add(setNearRendererOffButton);
		nearRenderBox.add(setNearRendererOnButton);
		nearRenderBox.add(Box.createHorizontalGlue());
		
		Box farRenderBox = Box.createHorizontalBox();
		farRenderBox.add(setFarRendererDistanceButton);
		farRenderBox.add(Box.createHorizontalGlue());
		
		Box improvedBox = Box.createHorizontalBox();
		improvedBox.add(setImprovedDetailDistanceButton);
		improvedBox.add(Box.createHorizontalGlue());
		
		Box hlpBox = Box.createHorizontalBox();
		hlpBox.add(setHLPBreakingDistanceButton);
		hlpBox.add(setHLPBreakingOffButton);
		hlpBox.add(setHLPBreakingOnButton);
		hlpBox.add(Box.createHorizontalGlue());
		
		Box billboardBox = Box.createHorizontalBox();
		billboardBox.add(setBillboardOrientationDistanceButton);
		billboardBox.add(Box.createHorizontalGlue());
		
		Box farBox = Box.createHorizontalBox();
		farBox.add(setFarPlaneDistanceButton);
		farBox.add(Box.createHorizontalGlue());
		
		// add the buttons
		perspectiveViewTypeControls.add(nearRenderBox);
		perspectiveViewTypeControls.add(farRenderBox);
		perspectiveViewTypeControls.add(improvedBox);
		perspectiveViewTypeControls.add(hlpBox);
		perspectiveViewTypeControls.add(billboardBox);
		perspectiveViewTypeControls.add(farBox);
		perspectiveViewTypeControls.add(Box.createVerticalGlue());		
	}
	
	void createTextureMapViewControls()
	{
		textureMapCombo = new JComboBox();
		textureMapCombo.setMaximumSize(textureMapCombo.getPreferredSize());
		textureMapCombo.setEnabled(true);
		
		textureMapX = new JTextField(6);
		textureMapY = new JTextField(6);
		textureMapX.setMaximumSize(textureMapX.getPreferredSize());
		textureMapY.setMaximumSize(textureMapY.getPreferredSize());
		
		Box whereBox = Box.createHorizontalBox();
		whereBox.add(textureMapX);
		whereBox.add(textureMapY);
		whereBox.add(Box.createHorizontalGlue());
		
		textureMapViewTypeControls = Box.createVerticalBox();
		textureMapViewTypeControls.add(textureMapCombo);
		textureMapViewTypeControls.add(whereBox);
		textureMapViewTypeControls.add(Box.createVerticalGlue());
	}
	
	void showCollisionVertexes()
	{
		ContentGeneratorSISI cgSISI = contentGeneratorSISI;
		generatedItem.setNeedsTotallyUpdating();
		generatedItem.calculateCollisionVertexes();
		generatedItem.renderCollisionVertexes(nc);	
	}
	
	

}
	

