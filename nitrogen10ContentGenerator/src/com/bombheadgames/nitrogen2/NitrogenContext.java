package com.bombheadgames.nitrogen2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.io.Serializable;

/**
 * 
 * @author andrew
 * This holds key information about the screen 
 * and world in which 3D items are rendered
 *
 */

import javax.swing.*;
// import java.awt.*;  // needed for Dimension class
// import java.awt.image.MemoryImageSource; // for memory image source
// import nitrogen1.TexMap;
// import nitrogen1.Vert;

public class NitrogenContext extends JPanel implements Serializable{
	private static final long serialVersionUID = -7001666928630703466L;

	public boolean contentGeneratorForcesNoCulling = false;
	public boolean contentGeneratorForcesNoPerspective = false;
	
	static final int SHIFT = 20;	// shift used when rendering textures
	
	/** Stuff added for basic lighting effect */
	public static float lightingDown = 0.5f;
	public static float lightingRight = 0.5f;
	public static float lightingAmbient = 0.25f;
	
	/** multiplier used in perspective magnifying the image so that the xclip value equals the right of button */
	final float magnification;
	
	/** value used in mapping view space z values to screen space z values */
	final float zk;
	
	/** value of x/z that the image is clipped at */
	final float xClip;
	
	/** value of y/z that the image is clipped at */
	final float yClip;
	
	/** distance between viewpoint and near clip plane (a positive value) */
	public final float nearClip;
	
	/** distance between viewpoint and far clip plane (a positive value) */
	final float farClip;
	
	/** Value setting the quality of high level perspective (HLP) breaking. 
	 * Setting this to a value closer to one improves quality but also slows the rendering of HLP polygons 
	 */
	float qualityOfHLP = 1.2f;
	
	/** width in pixels of the NitrogenContext */
    public int w = 0;		// width
    /** height in pixels of the NitrogenContext */
    public int h = 0;		// height
    /** Number of pixels in the NitrogenContext */
    public int s = 0;		// size = width * height
    /** Middle of width */
    int midw = w/2;
    /** Middle of height */
    int midh = h/2;
    
    /** The pixel buffer */
    transient public int[] pix;	
    
    /** The z buffer */
    transient public int[] zbuff; 
    
    /** Image that monitors source */
    transient Image im; 
    
    /** A MemoryImageSource that monitors pix */
    transient MemoryImageSource source;   
    
    /** If true only transparent polygons are rendered, otherwise only non-transparent polygons are rendered. Used for double pass rendering where the scene contains transparent polygons */
    public boolean transparencyPass = false;
    
    /** Stuff added for picking */
    public boolean isPicking = false;
    public boolean pickDetected = false;
    public Item pickedItem = null;
    Item currentItem = null;
    public int pickX;
    public int pickY;
    
    public int currentPolygon;
    public int pickedPolygon;
    
    /** Stuff added for performance evaluation */
    int itemsRendered = 0;
    int polygonsRendered = 0;
    int clippedPolygonsRendered = 0;
    int polygonRendererCalls = 0;
    long linesRendered = 0;
    
    public void setUpTransientFields()
    {
        // create array to store pixels
        s = w * h;
        pix = new int[s];
        zbuff = new int[s];
    	
    	// create an MemoryImageSource bound to the pixel array
	    source = new MemoryImageSource(w, h, pix, 0, w);
	    source.setAnimated(true);           // source will be used for multiframe animation
	    source.setFullBufferUpdates(true);  // source uses a complete buffer of pixels for updates
	    im = this.createImage(source);  	// this way of creating a BufferedImage avoids calling toolkit which may need permissions     
	}
    
    /** Constructs a Nitrogen Context to render things into 
     * 
     * @param width	Width of the view-port in pixels
     * @param height Height of the view-port in pixels
     * @param xClip The x/z ratio the view-port clips at 
     * @param yClip The y/z ratio the view-port clips at. Caller must ensure yClip/xClip ratio equals the height/width ratio
     * @param nearClip The distance between the viewer and the near Clip plane
     * @param farClip  The distance between the viewer and the far Clip plane
     */
    public NitrogenContext(int width, int height, float xClip, float yClip, float nearClip, float farClip)
    {
    	this.xClip = xClip;
    	this.yClip = yClip;
    	this.nearClip = nearClip;
    	this.farClip = farClip;
    	this.midw = (width / 2);
    	this.midh = (height / 2);
    	
    	magnification = ((float)(midw-1)) / xClip;
    	zk = 0.5f * nearClip * (float)(Integer.MAX_VALUE - 2);
        
    	w = width;
    	h = height;
    	
        // create array to store pixels
        s = w * h;
        pix = new int[s];
        zbuff = new int[s];

        // create an MemoryImageSource bound to the pixel array
        source = new MemoryImageSource(w, h, pix, 0, w);
        source.setAnimated(true);           // source will be used for multiframe animation
        source.setFullBufferUpdates(true);  // source uses a complete buffer of pixels for updates
        im = this.createImage(source);  	// this way of creating a BufferedImage avoids calling toolkit which may need permissions     
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        source.newPixels();         // causes source to send whole new buffer of pixels to listeners ... im
        g.drawImage(im,0,0,null);   // draws an image bound to pix in top-lh corner
    }
    
    final public void cls(int col)
    {
        int[] pixloc = pix;   // local copys
        int[] zloc = zbuff;

        // fill in first line
        for(int x = 0; x < w; x++)   pixloc[x]= col;
        int y = 1;

        // copy the first line over remaining lines
        while(y < h)
        {
           System.arraycopy(pixloc, 0, pixloc, (y * w), w);
           y++;
        }

        // repeat for z buffer setting to most -ve value possible
        int clearVal = Integer.MIN_VALUE;
        for(int x = 0; x < w; x++)   
        {
            
            zloc[x] = clearVal;
        }
        y = 1;

        // copy the first line over remaining lines
        while(y < h)
        {
           System.arraycopy(zloc, 0, zloc, (y * w), w);
           y++;
        }

    }
    
    final public void clearZBuffer()
    {
        int clearVal = Integer.MIN_VALUE;
    	int[] zloc = zbuff;
        int y;

        for(int x = 0; x < w; x++)   
        {
            
            zloc[x] = clearVal;
        }
        y = 1;

        // copy the first line over remaining lines
        while(y < h)
        {
           System.arraycopy(zloc, 0, zloc, (y * w), w);
           y++;
        }

    }    
    
@Override
public Dimension getMinimumSize()
{
    return new Dimension(w,h);
}

@Override
public Dimension getPreferredSize()
{
    return new Dimension(w,h);
}

@Override
public Dimension getMaximumSize()
{
    return new Dimension(w,h);
}

public void zeroPerformanceCounts()
{
    itemsRendered = 0;
    polygonsRendered = 0;
    clippedPolygonsRendered = 0;
    polygonRendererCalls = 0;
    linesRendered = 0;
}

/** cls that accepts an array which currently overwrites pixels only for testing */
final public void cls(int[] array)
{
       System.arraycopy(array, 0, pix, 0, s);
       clearZBuffer();
}

final public void createTestSquare()
{
	for(int x = 30; x < 60; x++)
	{
		for(int y = 30; y < 60; y++)
		{
			int index = x * w + y;
			pix[index] = 0xFF808080;
			zbuff[index] = 0;
		}
	}
}

}
