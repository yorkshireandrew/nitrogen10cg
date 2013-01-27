package modified_nitrogen1;

public class ContentGeneratorPolygon {
	
	/** index of first vertex of polygon */
	int c1;	
	/** index of second vertex of polygon */
	int c2;	
	/** index of third vertex of polygon */
	int c3;	
	/** index of fourth vertex of polygon */
	int c4;
	
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
	
	

}
