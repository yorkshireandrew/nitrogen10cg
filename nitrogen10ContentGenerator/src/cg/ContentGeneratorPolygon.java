package cg;

import java.io.Serializable;

import com.bombheadgames.nitrogen1.ImmutableVertex;


public class ContentGeneratorPolygon implements Serializable{
	private static final long serialVersionUID = -50807080139615148L;
	
	// the vertexes - these should also be in the ContentGeneratorSISI
	ImmutableVertex c1;	
	ImmutableVertex c2;	
	ImmutableVertex c3;	
	ImmutableVertex c4;
	
	/** string used to reference vertex data to be associated with c1 for this immutable polygon */
	String pvd_c1_name;
	/** string used to reference vertex data to be associated with c2 for this immutable polygon */	
	String pvd_c2_name;
	/** string used to reference vertex data to be associated with c3 for this immutable polygon */	
	String pvd_c3_name;
	/** string used to reference vertex data to be associated with c4 for this immutable polygon */	
	String pvd_c4_name;
	
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
		c1 = p.c1;
		c2 = p.c2;	
		c3 = p.c3;	
		c4 = p.c4;	
		
		pvd_c1_name = p.pvd_c1_name;
		pvd_c2_name = p.pvd_c2_name;
		pvd_c3_name = p.pvd_c3_name;
		pvd_c4_name = p.pvd_c4_name;
		
		polyData_name 			= p.polyData_name;		
		rendererTriplet_name 	= p.rendererTriplet_name;
		textureMap_name 		= p.textureMap_name;	
		backside_name 			= p.backside_name;
		isBacksideCulled 		= p.isBacksideCulled;
		isTransparent			= p.isTransparent;
	}
}
