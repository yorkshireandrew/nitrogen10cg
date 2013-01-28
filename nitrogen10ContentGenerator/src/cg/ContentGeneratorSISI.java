package cg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modified_nitrogen1.ImmutableBackside;
import modified_nitrogen1.ImmutableVertex;
import modified_nitrogen1.PolygonVertexData;
import modified_nitrogen1.TexMap;

/** encapsulates the ContentGenerators perspective of the Items SISI. This is used to create the generatedSISI */
public class ContentGeneratorSISI {

	boolean compact = false;
	
	List<ImmutableVertex> immutableVertexList;
	
	/** indexes of the immutableVertexes in the compacted array */
	List<Integer> immutableVertexIndexList;
	
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
    
    ContentGeneratorSISI()
    {
    	immutableVertexList			= new ArrayList<ImmutableVertex>();
    	immutableVertexIndexList	= new ArrayList<Integer>(); 
    	
    	polygonVertexDataMap 		= new HashMap<String,PolygonVertexData>();
        polygonDataMap 				= new HashMap<String,int[]>();
        textureMapMap 				= new HashMap<String,TexMap>(); 
        textureMapFullPathMap 		= new HashMap<String,String>(); 
        immutableBacksideMap 		= new HashMap<String,ImmutableBackside>(); 
        contentGeneratorPolygonMap 	= new HashMap<String,ContentGeneratorPolygon>();
    }
}
