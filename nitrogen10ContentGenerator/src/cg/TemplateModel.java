package cg;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import modified_nitrogen1.NitrogenCreationException;

public class TemplateModel {
	
	int OFF_TEMPLATE_COLOUR = 0xFF000000;
	int OFF_TEMPLATE_RED = 0;
	int OFF_TEMPLATE_GREEN = 0;
	int OFF_TEMPLATE_BLUE = 0;

	File templateFile;
	int leftRightOffset;
	int downUpOffset;
	boolean over;	// set true if the template is shown over the content
	int intensity;
	int scale = 1000;
	
	// pixels from file
	int[] filePixels;
	int fileWidth;
	int fileHeight;
	int fileSize;
	boolean fileLoaded = false;
	
	
	// pixels created by rescaling template
	int[] red;
	int[] green;
	int[] blue;
	int[] pixels;
	int editScreenHeight;
	int editScreenWidth;
	int editScreenSize;
	
	
	TemplateModel(ContentGenerator cg)
	{
		editScreenSize 		= cg.EDIT_SCREEN_SIZE;
		editScreenHeight 	= cg.EDIT_SCREEN_HEIGHT;
		editScreenWidth 	= cg.EDIT_SCREEN_WIDTH;
		
		// create a load of initially black pixels
		red 		= new int[editScreenSize];
		green 		= new int[editScreenSize];
		blue 		= new int[editScreenSize];
		pixels 		= new int[editScreenSize];
	}
	
	// default constructor
	TemplateModel()
	{
	}
	
	/** Copy constructor. Creates a copy of the state of the passed in prototype for cancel buttons etc */
	static TemplateModel copy(TemplateModel prototype)
	{
		TemplateModel retval = new TemplateModel();
		retval.templateFile = prototype.templateFile;
		retval.leftRightOffset = prototype.leftRightOffset;
		retval.downUpOffset = prototype.downUpOffset;
		retval.over = prototype.over;	
		retval.intensity = prototype.intensity;
		retval.scale = prototype.scale;
		return retval;
	}
	
	/** Sets the state of this TemplateModel using a 
	previously savedState */
	void restore(TemplateModel savedState)
	{
		this.templateFile = savedState.templateFile;
		this.leftRightOffset = savedState.leftRightOffset;
		this.downUpOffset = savedState.downUpOffset;
		this.over = savedState.over;	
		this.intensity = savedState.intensity;
		this.scale = savedState.scale;
	}
	
	void loadFile()
	{
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
        fileHeight = i.getHeight();
        fileWidth = i.getWidth();
        filePixels = i.getRGB(0, 0, fileWidth, fileHeight, null, 0, fileWidth);	
        fileLoaded = true;
        System.out.println("file Load looks to have worked");
	}
	
	void generatePixels()
	{
		if(fileLoaded == false)
		{
			// create a load of initially black pixels
			red 		= new int[editScreenSize];
			green 		= new int[editScreenSize];
			blue 		= new int[editScreenSize];
			pixels 		= new int[editScreenSize];
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
				offsetX = offsetX - leftRightOffset;
				if(offsetX < 0)continue;	// missed the template
				offsetX = (offsetX * 1000) / scale;
				if(offsetX >= fileWidth)continue;	// missed the template
				
				// calculate offsetY and check if it hits the template
				offsetY = editY - halfHeight;
				offsetY = offsetY + downUpOffset;
				if(offsetY < 0)continue;	// missed the template
				offsetY = (offsetY * 1000) / scale;
				if(offsetY >= fileHeight)continue;	// missed the template
				
				int fileIndex = offsetY * fileWidth + offsetX;
				int fileColour = filePixels[fileIndex];
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
	
	
}
