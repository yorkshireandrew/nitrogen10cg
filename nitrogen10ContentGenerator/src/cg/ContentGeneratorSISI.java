package cg;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;

import com.bombheadgames.nitrogen1.ImmutableBackside;
import com.bombheadgames.nitrogen1.ImmutableCollisionVertex;
import com.bombheadgames.nitrogen1.ImmutablePolygon;
import com.bombheadgames.nitrogen1.ImmutableVertex;
import com.bombheadgames.nitrogen1.NitrogenCreationException;
import com.bombheadgames.nitrogen1.PolygonVertexData;
import com.bombheadgames.nitrogen1.RendererHelper;
import com.bombheadgames.nitrogen1.RendererTriplet;
import com.bombheadgames.nitrogen1.SharedImmutableSubItem;
import com.bombheadgames.nitrogen1.TexMap;


/** encapsulates the ContentGenerators perspective of the Items SISI. This is used to create the generatedSISI */
public class ContentGeneratorSISI implements Serializable{
	private static final long serialVersionUID = 9053868722541601300L;

	List<ImmutableVertex> immutableVertexList;
	
	List<ImmutableCollisionVertex> collisionVertexList;	
	
	/** contains named VertexData used for ImmutablePolygon generation */
    Map<String,PolygonVertexData> polygonVertexDataMap;
    
    /**  contains named polygonData used for ImmutablePolygon generation */
    Map<String,int[]> polygonDataMap;
    
    /** contains named textureMap used for ImmutablePolygon generation */
    Map<String,ContentGeneratorTextureMap> textureMapMap;
    Map<String,String> textureMapFullPathMap;
    
    /** maps for backsideName */
    Map<String, ImmutableBackside> immutableBacksideMap;
    
    /**  maps for ContentGeneratorPolygon, these are used to generate the ImmutablePolygons */
    Map<String,ContentGeneratorPolygon> contentGeneratorPolygonMap;
    
    // Initialise fields to safe default values
    
    // read bounding radius initially zero
    // recalculate later
	float boundingRadius 	= 0; // initally zero, we will recompute later

	// force near renderer
	float nearRendererDist = 1E6f;
	float farRendererDist  = 1E6f;
	
	// force hlp breaking
	float hlpBreakingDist  = 0;
	
	// no billboarding
	float billboardOrientationDist = 1E6f;
	// set SISI far plane to be very far away
	float farPlane = 1E6f;
	
	// initially no polygons to render
	int normalDetailPolyStart 		= 0;
	int improvedDetailPolyStart 	= 0;
	int normalDetailPolyFinish 		= 0;
	int improvedDetailPolyFinish 	= 0;

	// force improved detail
	float improvedDetailDist 		= 1E6f; 
	boolean hasCollisionVertexes 	= false;
	
    ContentGeneratorSISI()
    {
    	immutableVertexList			= new ArrayList<ImmutableVertex>();
    	collisionVertexList			= new ArrayList<ImmutableCollisionVertex>();   	
    	polygonVertexDataMap 		= new HashMap<String,PolygonVertexData>();
        polygonDataMap 				= new HashMap<String,int[]>();
        textureMapMap 				= new HashMap<String,ContentGeneratorTextureMap>();
        textureMapMap.put("null", null);
        textureMapFullPathMap 		= new HashMap<String,String>();
        textureMapMap.put("null", null);
        immutableBacksideMap 		= new HashMap<String,ImmutableBackside>(); 
  
        // its important we preserve the order in polygon map
        contentGeneratorPolygonMap 	= new LinkedHashMap<String,ContentGeneratorPolygon>();
 }
    
    SharedImmutableSubItem generateSISI()
    {
    	SharedImmutableSubItem retval = new SharedImmutableSubItem();
    	
    	// initialise fields 
    	retval.boundingRadius 				=  boundingRadius;
    	System.out.println("generated SISI bounding radius " + retval.boundingRadius);
    	retval.nearRendererDist 			= nearRendererDist;
    	retval.farRendererDist  			= farRendererDist;
    	retval.hlpBreakingDist  			= hlpBreakingDist;
    	retval.billboardOrientationDist 	= billboardOrientationDist;
    	retval.farPlane 					= farPlane;
    	retval.normalDetailPolyStart 		= normalDetailPolyStart;
    	retval.improvedDetailPolyStart 		= improvedDetailPolyStart;
    	retval.normalDetailPolyFinish 		= normalDetailPolyFinish;
    	retval.improvedDetailPolyFinish 	= improvedDetailPolyFinish;
    	retval.improvedDetailDist 			= improvedDetailDist;  	
    	retval.hasCollisionVertexes 		= hasCollisionVertexes;
    	
    	//calculate hysteresis distances
    	retval.nearRendererDistPlus = nearRendererDist * SharedImmutableSubItem.HYSTERESIS;
    	retval.farRendererDistPlus = farRendererDist * SharedImmutableSubItem.HYSTERESIS;	
    	retval.hlpBreakingDistPlus = hlpBreakingDist * SharedImmutableSubItem.HYSTERESIS;
    	retval.billboardOrientationDistPlus = billboardOrientationDist * SharedImmutableSubItem.HYSTERESIS;   
    	retval.improvedDetailDistPlus = improvedDetailDist * SharedImmutableSubItem.HYSTERESIS;
		
		// create arrays
    	retval.immutableVertexes = immutableVertexList.toArray(new ImmutableVertex[0]);
    	retval.immutableCollisionVertexes = collisionVertexList.toArray(new ImmutableCollisionVertex[0]);	    	 
    	ImmutableBackside[] immutableBacksidesL = immutableBacksideMap.values().toArray(new ImmutableBackside[0]);
    	retval.immutableBacksides = immutableBacksidesL;
    	int immutableBacksideLength = immutableBacksidesL.length;
    	int immutablePolygonSize = contentGeneratorPolygonMap.size();
    	ImmutablePolygon[] immutablePolygonsL = new ImmutablePolygon[immutablePolygonSize];
    	List<ImmutableVertex> immutableVertexListL = immutableVertexList;
    	Map<String,PolygonVertexData> polygonVertexDataMapL = polygonVertexDataMap;
    	
    	Iterator<Entry<String,ContentGeneratorPolygon>> it = contentGeneratorPolygonMap.entrySet().iterator();
    	try{
    		int index = 0;
	    	while(it.hasNext())
	    	{
	    		ContentGeneratorPolygon cgp = it.next().getValue();
	    		int c1 = immutableVertexListL.indexOf(cgp.c1);
	    		int c2 = immutableVertexListL.indexOf(cgp.c2);
	    		int c3 = immutableVertexListL.indexOf(cgp.c3);
	    		int c4 = immutableVertexListL.indexOf(cgp.c4);
	    		PolygonVertexData pvd_c1 = polygonVertexDataMapL.get(cgp.pvd_c1_name);
	    		PolygonVertexData pvd_c2 = polygonVertexDataMapL.get(cgp.pvd_c2_name);
	    		PolygonVertexData pvd_c3 = polygonVertexDataMapL.get(cgp.pvd_c3_name);
	    		PolygonVertexData pvd_c4 = polygonVertexDataMapL.get(cgp.pvd_c4_name);
	    		int[] polyData = polygonDataMap.get(cgp.polyData_name);
	    		RendererTriplet rendererTriplet;
				rendererTriplet = RendererHelper.getRendererTriplet(cgp.rendererTriplet_name);
	    		
	    		// find the index of the backside in the array
	    		ImmutableBackside backsideToFind = immutableBacksideMap.get(cgp.backside_name);
	    		int backsideIndex = 0;
	    		for(backsideIndex = 0; backsideIndex <immutableBacksideLength; backsideIndex++)
	    		{
	    			if (immutableBacksidesL[backsideIndex] == backsideToFind)
	    			{
	    				break;
	    			}
	    		}
	    		
	    		TexMap textureMap = textureMapMap.get(cgp.textureMap_name).textureMap;
	    		ImmutablePolygon element = new ImmutablePolygon(
				c1,
				c2,
				c3,
				c4,
				pvd_c1,
				pvd_c2,
				pvd_c3,
				pvd_c4,
				polyData,
				rendererTriplet,
				textureMap,
				backsideIndex,
				cgp.isBacksideCulled,
				cgp.isTransparent);
	    		
	    		immutablePolygonsL[index] = element;
	    		index++;
	    	}	    	
	    	
	    	retval.immutablePolygons = immutablePolygonsL;
	    	return retval;
	    	
    	}
    	catch(Exception e)
    	{
    		System.out.println("renderer triplet exception generating SISI from ContentGeneratorSISI");
    		e.printStackTrace();
    		return null;
    	}  	
    }
    
    final private boolean isImmutableVertexUsed(ImmutableVertex iv)
    {
    	ContentGeneratorPolygon[] cgps = contentGeneratorPolygonMap.values().toArray(new ContentGeneratorPolygon[0]);
    	
    	for(ContentGeneratorPolygon cgp: cgps)
    	{
    		if(cgp.c1 == iv)return true;
    		if(cgp.c2 == iv)return true;
    		if(cgp.c3 == iv)return true;
    		if(cgp.c4 == iv)return true;
    	}
    	return false;
    }
    
    final private boolean isVertexDataUsed(String name)
    {
    	ContentGeneratorPolygon[] cgps = contentGeneratorPolygonMap.values().toArray(new ContentGeneratorPolygon[0]);
    	
    	for(ContentGeneratorPolygon cgp: cgps)
    	{
    		if(cgp.pvd_c1_name.equals(name))return true;
    		if(cgp.pvd_c2_name.equals(name))return true;
    		if(cgp.pvd_c3_name.equals(name))return true;
    		if(cgp.pvd_c4_name.equals(name))return true;
    	}
    	return false;
    }
    
    final private boolean isPolygonDataUsed(String name)
    {
    	ContentGeneratorPolygon[] cgps = contentGeneratorPolygonMap.values().toArray(new ContentGeneratorPolygon[0]);
    	
    	for(ContentGeneratorPolygon cgp: cgps)
    	{
    		if(cgp.polyData_name == null)continue;
    		if(cgp.polyData_name.equals(name))return true;
    	}
    	return false;
    }
    
    final private boolean isTextureMapUsed(String name)
    {
    	ContentGeneratorPolygon[] cgps = contentGeneratorPolygonMap.values().toArray(new ContentGeneratorPolygon[0]);
    	
    	for(ContentGeneratorPolygon cgp: cgps)
    	{
    		if(cgp.textureMap_name == null)continue;
    		if(cgp.textureMap_name.equals(name))return true;
    	}
    	return false;
    }
    
    final private boolean isImmutableBacksideUsed(String name)
    {
    	ContentGeneratorPolygon[] cgps = contentGeneratorPolygonMap.values().toArray(new ContentGeneratorPolygon[0]);
    	
    	for(ContentGeneratorPolygon cgp: cgps)
    	{
    		if(cgp.backside_name == null)continue;
    		if(cgp.backside_name.equals(name))return true;
    	}
    	return false;
    }
    
    final void removeUnusedImmutableVertexes()
    {
    	Iterator<ImmutableVertex> it =immutableVertexList.iterator();	
    	while(it.hasNext())
    	{
    		ImmutableVertex iv = it.next();
    		if(!isImmutableVertexUsed(iv))it.remove();
    	}
    }
    
    final void removeUnusedVertexData()
    {
    	Set<Entry<String,PolygonVertexData>> set = polygonVertexDataMap.entrySet();
    	
    	Iterator<Entry<String,PolygonVertexData>> it = set.iterator();
    	while(it.hasNext())
    	{
    		Entry<String,PolygonVertexData> e = it.next();
    		String name = (String)e.getKey();
    		if(!isVertexDataUsed(name))it.remove();
    	}
    }
    
    final void removeUnusedPolygonData()
    {
    	Set<Entry<String,int[]>> set = polygonDataMap.entrySet();
    	
    	Iterator<Entry<String,int[]>> it = set.iterator();
    	while(it.hasNext())
    	{
    		Entry<String,int[]> e = it.next();
    		String name = (String)e.getKey();
    		if(!isPolygonDataUsed(name))it.remove();
    	}
    }
    
    final void removeUnusedTextureMaps()
    {
    	Set<Entry<String,ContentGeneratorTextureMap>> set = textureMapMap.entrySet();
    	Set<Entry<String,String>> fullPathSet = textureMapFullPathMap.entrySet();
    	
    	Iterator<Entry<String,ContentGeneratorTextureMap>> it = set.iterator();
    	Iterator<Entry<String,String>> rootPathIt = fullPathSet.iterator();
    	
    	while(it.hasNext())
    	{
    		Entry<String,ContentGeneratorTextureMap> element = it.next();
    		rootPathIt.next();
    		
    		String name = (String)element.getKey();
    		if(!isTextureMapUsed(name))
    		{
    			it.remove();
    			rootPathIt.remove();
    		}
    	}
    }
    
    final void removeUnusedImmutableBacksides()
    {
    	Set<Entry<String,ImmutableBackside>> set = immutableBacksideMap.entrySet();
    	
    	Iterator<Entry<String,ImmutableBackside>> it = set.iterator();
    	while(it.hasNext())
    	{
    		Entry<String,ImmutableBackside> e = it.next();
    		String name = (String)e.getKey();
    		if(!isImmutableBacksideUsed(name))it.remove();
    	}
    }
    
    /** read object called during un-serialisation, implemented to load texture maps */
    final public void setUpTransientFields(ContentGenerator cg) throws IOException, ClassNotFoundException
    {  	
    	Iterator<Entry<String,ContentGeneratorTextureMap>> it = cg.contentGeneratorSISI.textureMapMap.entrySet().iterator();
    	
    	boolean hasChoosen = false;
    	boolean useContentGeneratorPath = false;
    	
    	try{
    	
    	while(it.hasNext())
    	{
    		Entry<String,ContentGeneratorTextureMap> ent = it.next();
    		
    		ContentGeneratorTextureMap cgtm = ent.getValue();
    		if(cgtm == null)continue;
    		if(cgtm.path == null)continue;
    		if(cgtm.path.equals(cg.resourceURL))
    		{
    			ent.setValue(new ContentGeneratorTextureMap(cgtm.path, cgtm.file,new TexMap(cgtm.path, cgtm.file)));
    		}
    		else
    		{
    			// ok path of ContentGeneratorTextureMap differs from ContentGenerator resourcePath
    			
    			if(!hasChoosen)
    			{
    				if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
							cg, 
							"Loading " + cgtm.file + " texture map. The path differ from ContentGenerators resource path .Override and use ContentGenerators resource path?", 
							"TextureMap path difference"
							,JOptionPane.YES_NO_OPTION)
							)
    				{
    					hasChoosen = true;
    					useContentGeneratorPath = true;
    				}
    				else
    				{
    					hasChoosen = true;
    					useContentGeneratorPath = false;  					
    				}
    			}// end of !hasChoosen if
    			
    			if(useContentGeneratorPath)
    			{
    				cgtm.textureMap = new TexMap(cg.resourceURL, cgtm.file);
    				ent.setValue(new ContentGeneratorTextureMap(cg.resourceURL, cgtm.file, new TexMap(cg.resourceURL, cgtm.file)));
    			}
    			else
    			{
    				cgtm.textureMap = new TexMap(cgtm.path, cgtm.file);
    				ent.setValue(new ContentGeneratorTextureMap(cgtm.path, cgtm.file, new TexMap(cgtm.path, cgtm.file))); 				
    			}
    		}// end of path checking if-else
    	}//end of while	
    	
    	}
    	catch(NitrogenCreationException nce)
    	{
    		JOptionPane.showMessageDialog(cg, "Nitrogen creation exception loading a texture map " + nce.getMessage() , "Error",JOptionPane.ERROR_MESSAGE);
    	}
    }    
}
