package cg;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class TemplateModel implements Serializable{
	private static final long serialVersionUID = -2449470950754148706L;
	
	int OFF_TEMPLATE_COLOUR = 0xFF000000;
	int OFF_TEMPLATE_RED = 0;
	int OFF_TEMPLATE_GREEN = 0;
	int OFF_TEMPLATE_BLUE = 0;

	File templateFile;
	int templateX;
	int templateY;
	boolean overlay;	// set true if the template is shown over the content
	int intensity;
	int templateScale = 1000;
	
	// pixels from file
transient	int[] templatePixels;
transient	int templateWidth;
transient	int templateHeight;
transient	int templateSize;	
	
	// pixels created by rescaling template
transient	int[] red;
transient	int[] green;
transient	int[] blue;
transient	int[] pixels;
	int editScreenHeight;
	int editScreenWidth;
	int editScreenSize;
	
	// default constructor
	TemplateModel()
	{
		initialiseTemplateModel();
	}
	
	/** initialise fields used by clients to default values*/
	private void initialiseTemplateModel()
	{
		editScreenSize 		= ContentGenerator.EDIT_SCREEN_SIZE;
		editScreenHeight 	= ContentGenerator.EDIT_SCREEN_HEIGHT;
		editScreenWidth 	= ContentGenerator.EDIT_SCREEN_WIDTH;
		
		// create a load of initially black pixels
		red 			= new int[editScreenSize];
		green 		= new int[editScreenSize];
		blue 		= new int[editScreenSize];
		pixels 		= new int[editScreenSize];		
		for(int i = 0 ; i < editScreenSize; i++)pixels[i] = OFF_TEMPLATE_COLOUR;	
	}
	
	
	/** Copy constructor. Creates a copy of the state of the passed in prototype for cancel buttons etc */
	static TemplateModel copy(TemplateModel prototype)
	{
		TemplateModel retval = new TemplateModel();
		retval.templateFile = prototype.templateFile;
		retval.templateX = prototype.templateX;
		retval.templateY = prototype.templateY;
		retval.overlay = prototype.overlay;	
		retval.intensity = prototype.intensity;
		retval.templateScale = prototype.templateScale;
		return retval;
	}
	
	/** Sets the state of this TemplateModel using a 
	previously savedState */
	void restore(TemplateModel savedState)
	{
		this.templateFile = savedState.templateFile;
		this.templateX = savedState.templateX;
		this.templateY = savedState.templateY;
		this.overlay = savedState.overlay;	
		this.intensity = savedState.intensity;
		this.templateScale = savedState.templateScale;
		this.loadFile();
		this.generatePixels();
	}
	
	void loadFile()
	{
		System.out.println("loadFile called");
		if(templateFile != null)System.out.println("template file=" + templateFile.getAbsolutePath());
		if(templateFile == null)
		{
			System.out.println("template file null while loading file");
			return;
		}
		
		// create URL from templateFile
		URL url;
		try
		{
			url = templateFile.toURI().toURL();
		}
		catch(MalformedURLException e)
		{
			System.out.println("malformed URL exception creating template image");
			return;
		}
        if(url == null)
        {
        	System.out.println("template image file " + url + " could not be found");
        }
        
    	Image ii = new javax.swing.ImageIcon(url).getImage();
        BufferedImage i = new BufferedImage(ii.getWidth(null),ii.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics2D osg = i.createGraphics();
        osg.drawImage(ii, 0, 0, null);
        templateHeight = i.getHeight();
        templateWidth = i.getWidth();
        templatePixels = i.getRGB(0, 0, templateWidth, templateHeight, null, 0, templateWidth);	
        System.out.println("load looks to have worked:" + templatePixels.length);
	}
	
	void generatePixels()
	{
		if(templateFile == null || templatePixels == null)
		{
			initialiseTemplateModel();
			return;
		}
		
		int halfHeight = editScreenHeight / 2;
		int halfWidth = editScreenWidth / 2;
		
		
		int offsetX;
		int offsetY;
		
		int editIndex;
		
		for(int editY = 0 ; editY < editScreenHeight; editY++)
		{
			for(int editX = 0 ; editX < editScreenWidth; editX++)
			{
				editIndex = editY * editScreenWidth + editX;
				
				// set pixel initially to off template colour
				// so it changes to that if we invoke continue				
				red[editIndex] = OFF_TEMPLATE_RED;
				green[editIndex] = OFF_TEMPLATE_GREEN;
				blue[editIndex] = OFF_TEMPLATE_BLUE;
				pixels[editIndex] = OFF_TEMPLATE_COLOUR;
				
				// calculate offsetX and check if it hits the template
				offsetX = editX - halfWidth;
				offsetX = (offsetX * 1000) / templateScale;
				offsetX = offsetX + templateX;
				if(offsetX < 0)continue;			// missed the template
				if(offsetX >= templateWidth)continue;	// missed the template
				
				// calculate offsetY and check if it hits the template
				offsetY = editY - halfHeight;
				offsetY = (offsetY * 1000) / templateScale;
				offsetY = offsetY + templateY;
				if(offsetY < 0)continue;	// missed the template
				if(offsetY >= templateHeight)continue;	// missed the template
				
				int fileIndex = offsetY * templateWidth + offsetX;
				int fileColour = templatePixels[fileIndex];
				doColour(editIndex,red,green,blue,pixels,intensity,fileColour); 			
			}
		}
	}
	
	final void doColour(final int index, final int[] redArray, final int[] greenArray, final int[] blueArray, int[] colourArray, int intensity, int fileColour)
	{
		// convert file colours to RGB
		int blue 	= (fileColour & 0x0000FF);
		int green 	= (fileColour & 0x00FF00) >> 8;
		int red 	= (fileColour & 0xFF0000) >> 16;
		
		// re-scale RGB using intensity
		blue 	= (blue * intensity) / 100;
		green 	= (green * intensity) / 100;
		red 	= (red * intensity) / 100;
		
		int retval = 0xFF000000 | (red << 16) | (green << 8) | blue;
		
		redArray[index] = red;
		greenArray[index] = green;
		blueArray[index] = blue;
		colourArray[index] = retval;
	}
	
	/** overlays the template onto the rendered pixels */
	void overlayTemplate(int[] pixels, int[] zbuffer)
	{
		if(overlay == false)return;
		int clearVal = Integer.MIN_VALUE;
		int pixelRed,pixelGreen,pixelBlue;
		int pixelColour;
		for(int x = 0; x < editScreenSize; x++)
		{
			if(zbuffer[x] == clearVal)continue;
			pixelColour = pixels[x];
			// extract the pixel colour
			pixelBlue 	= (pixelColour & 0x0000FF);
			pixelGreen = (pixelColour & 0x00FF00) >> 8;
			pixelRed 	= (pixelColour& 0xFF0000) >> 16;
			
			pixelBlue += blue[x];
			pixelGreen += green[x];
			pixelRed += red[x];
			
			if(pixelBlue > 255) pixelBlue = 255;
			if(pixelGreen > 255) pixelGreen = 255;
			if(pixelRed > 255) pixelRed = 255;
			
			pixels[x] = 0xFF000000 | (pixelRed << 16) | (pixelGreen << 8) | pixelBlue;	
		}
	}
	
	void setUpTransientFields()
	{
		initialiseTemplateModel();
		loadFile();
		generatePixels();
	}
	
	
}
