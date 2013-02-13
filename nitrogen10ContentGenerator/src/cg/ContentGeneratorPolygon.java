package cg;

import java.io.Serializable;

import com.bombheadgames.nitrogen2.ImmutableVertex;


public class ContentGeneratorPolygon implements Serializable{
	private static final long serialVersionUID = -50807080139615148L;
	
	int numberOfVertexes;
	
	/** vertexes of this polygon */
	ImmutableVertex[] vertexes;
	
	/** string used to reference vertex data to be associated with vertexes of this polygon */
	String[] pvd_names;
	
	/** string used to reference Information to pass to the renderer, for example the polygons colour */
	String polyData_name;
	
	/** string used to reference The renderer triplet to use to render the polygon */
	String rendererTriplet_name;
	
	/** string used to reference The texture map */
	String textureMap_name;
	
	/** string used to reference The backside used to determine which side is being viewed */
	String backside_name;
	
	/** True if the polygon uses backside culling */
	boolean isBacksideCulled;
	
	/** True if the polygon is transparent, and gets rendered during a transparent render pass */
	boolean isTransparent;
	
	ContentGeneratorPolygon()
	{
		
	}
	
	// copy constructor
	ContentGeneratorPolygon(ContentGeneratorPolygon p)
	{
		numberOfVertexes = p.numberOfVertexes;
		int numberOfVertexesL = numberOfVertexes;
		
		vertexes = new ImmutableVertex[numberOfVertexesL];
		pvd_names = new String[numberOfVertexesL];
		
		for(int x = 0; x < numberOfVertexesL; x++)
		{
			vertexes[x] = p.vertexes[x];
			pvd_names[x] = p.pvd_names[x];
		}
			
		polyData_name 			= p.polyData_name;		
		rendererTriplet_name 	= p.rendererTriplet_name;
		textureMap_name 		= p.textureMap_name;	
		backside_name 			= p.backside_name;
		isBacksideCulled 		= p.isBacksideCulled;
		isTransparent			= p.isTransparent;
	}
}
