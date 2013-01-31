package cg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import modified_nitrogen1.ImmutableBackside;
import modified_nitrogen1.ImmutableCollisionVertex;
import modified_nitrogen1.ImmutablePolygon;
import modified_nitrogen1.ImmutableVertex;
import modified_nitrogen1.PolygonVertexData;
import modified_nitrogen1.RendererHelper;
import modified_nitrogen1.RendererTriplet;
import modified_nitrogen1.SharedImmutableSubItem;
import modified_nitrogen1.TexMap;

/** encapsulates the ContentGenerators perspective of the Items SISI. This is used to create the generatedSISI */
public class ContentGeneratorSISI {

	List<ImmutableVertex> immutableVertexList;
	
	List<ImmutableVertex> collisionVertexList;	
	
	/** contains named VertexData used for ImmutablePolygon generation */
    Map<String,PolygonVertexData> polygonVertexDataMap;
    
    /**  contains named polygonData used for ImmutablePolygon generation */
    Map<String,int[]> polygonDataMap;
    
    /** contains named textureMap used for ImmutablePolygon generation */
    Map<String,TexMap> textureMapMap;
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
    	collisionVertexList			= new ArrayList<ImmutableVertex>();   	
    	polygonVertexDataMap 		= new HashMap<String,PolygonVertexData>();
        polygonDataMap 				= new HashMap<String,int[]>();
        textureMapMap 				= new HashMap<String,TexMap>(); 
        textureMapFullPathMap 		= new HashMap<String,String>(); 
        immutableBacksideMap 		= new HashMap<String,ImmutableBackside>(); 
        contentGeneratorPolygonMap 	= new HashMap<String,ContentGeneratorPolygon>();
    }
    
    SharedImmutableSubItem generateSISI()
    {
    	SharedImmutableSubItem retval = new SharedImmutableSubItem();
    	
    	// initialise fields 
    	retval.boundingRadius 				=  boundingRadius;
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
    	ContentGeneratorPolygon[] cgps = contentGeneratorPolygonMap.values().toArray(new ContentGeneratorPolygon[0]);
    	List<ImmutableVertex> immutableVertexListL = immutableVertexList;
    	Map<String,PolygonVertexData> polygonVertexDataMapL = polygonVertexDataMap;
    	try{
	    	for(int x = 0; x < immutablePolygonSize; x++)
	    	{
	    		ContentGeneratorPolygon cgp = cgps[x];
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
	    		
	    		TexMap textureMap = textureMapMap.get(cgp.textureMap_name);
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
	    		
	    		immutablePolygonsL[x] = element;
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
    	Set<Entry<String,TexMap>> set = textureMapMap.entrySet();
    	Set<Entry<String,String>> fullPathSet = textureMapFullPathMap.entrySet();
    	
    	Iterator<Entry<String,TexMap>> it = set.iterator();
    	Iterator<Entry<String,String>> rootPathIt = fullPathSet.iterator();
    	
    	while(it.hasNext())
    	{
    		Entry<String,TexMap> element = it.next();
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
    
    
}
