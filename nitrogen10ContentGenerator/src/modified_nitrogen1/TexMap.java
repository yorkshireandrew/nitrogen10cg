package modified_nitrogen1;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andrew
 */

import java.awt.Graphics2D;
import java.awt.Image;
//import java.awt.Component;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TexMap implements Serializable{
	private static final long serialVersionUID = 3915774142992302906L;

	private static Map<String,TexMap> textures;
	
	private String resourceName;
	public transient int[] tex;
    public transient int w, h;
    
    static{ textures = new HashMap<String,TexMap>();}
    
    final static TexMap getTexture(String st) throws NitrogenCreationException
    {
    	if(textures == null)System.out.println("TexMap textures is null");
    	System.out.println("seeing if we hava texture called :"+ st);
    	if(textures.containsKey(st))
    	{
    		System.out.println("yes we do");
    		return(textures.get(st));
    	}
    	System.out.println("no we dont");
    	return(new TexMap(st));
    }

    /** altered constructor that reads files rather than embedded resources (but acts as though it was from a resource if serialised to disk)*/
    public TexMap(String fullPath, String resourcePath) throws NitrogenCreationException
    { 	
 //   	URL url = getClass().getResource(st);
    	File f = new File(fullPath);
        if(!f.exists())throw new NitrogenCreationException("TexMap resource " + fullPath + " could not be found");
    	Image ii = new javax.swing.ImageIcon(fullPath).getImage();
        BufferedImage i = new BufferedImage(ii.getWidth(null),ii.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics2D osg = i.createGraphics();
        osg.drawImage(ii, 0, 0, null);
        h = i.getHeight();
        w = i.getWidth();
        tex = i.getRGB(0, 0, w, h, null, 0, w);
        
        // ensure we write the resource path, not the full path
        // so the load works when the SISI is used the a real environment
        resourceName = resourcePath;   
    }
    
    private TexMap(String st) throws NitrogenCreationException
    { 	
    	URL url = getClass().getResource(st);
        if(url == null)throw new NitrogenCreationException("TexMap resource " + st + " could not be found");
    	Image ii = new javax.swing.ImageIcon(getClass().getResource(st)).getImage();
        BufferedImage i = new BufferedImage(ii.getWidth(null),ii.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics2D osg = i.createGraphics();
        osg.drawImage(ii, 0, 0, null);
        h = i.getHeight();
        w = i.getWidth();
        tex = i.getRGB(0, 0, w, h, null, 0, w);
        resourceName = st;   
    }
    
    final int getRGB(int x, int y)
    {
        return(tex[(x+y*w)]);
    }
    
    final int[] getTex()
    {
        return tex;
    }
    
    final int getWidth()
    {
        return w;
    }

    final int getHeight()
    {
        return h;
    }
    

    final private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
    	System.out.println("TexMap.readObject called");
    	in.defaultReadObject();
    	System.out.println("ok the resource is called :" + resourceName);
    	if(textures.containsKey(resourceName))
    		{
    			System.out.println("we already have that resource");
    			System.out.println("I bet we should do something here");
    			TexMap loadedTexture = textures.get(resourceName);
    			tex = loadedTexture.tex;
       			h = loadedTexture.h;
       			w = loadedTexture.w;
       		    return; // we already have loaded the texture
    		}
    	URL url = getClass().getResource(resourceName);
        if(url == null)throw new IOException("TexMap resource " + resourceName + " could not be found");
    	Image ii = new javax.swing.ImageIcon(getClass().getResource(resourceName)).getImage();
        BufferedImage i = new BufferedImage(ii.getWidth(null),ii.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics2D osg = i.createGraphics();
        osg.drawImage(ii, 0, 0, null);
        h = i.getHeight();
        w = i.getWidth();
        tex = i.getRGB(0, 0, w, h, null, 0, w);
        textures.put(resourceName, this);
    }
    
    /** Purges the collection of loaded textures, All SharedImmutableSubItems that use textures must be reloaded */ 
    final public void purgeTextures()
    {
    	// create a new empty hashmap
    	textures = new HashMap<String,TexMap>();
    }


}
