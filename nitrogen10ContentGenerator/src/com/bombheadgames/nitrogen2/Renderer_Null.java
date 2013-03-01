package com.bombheadgames.nitrogen2;

public class Renderer_Null implements Renderer {

	@Override
	public void render(
			final NitrogenContext context,	
			
			Nitrogen2Vertex leftN2V,
			Nitrogen2Vertex leftDestN2V,
			
			Nitrogen2Vertex rightN2V,
			Nitrogen2Vertex rightDestN2V,
			final Nitrogen2Vertex stopN2V,
			
			final int[] polyData,
			final TexMap textureMap,
			final float lightingValue	
			)
	{
		// do nothing
	}
	
	public void renderHLP(
			final NitrogenContext context,	
			
			final Nitrogen2Vertex leftN2V,
			final Nitrogen2Vertex leftDestN2V,
			
			final Nitrogen2Vertex rightN2V,
			final Nitrogen2Vertex rightDestN2V,	
			final Nitrogen2Vertex stopN2V,
			
			final int[] polyData,
			final TexMap textureMap,
			final float lightingValue	
			){}

	public boolean isTextured(){return false;}

	public boolean allowsHLP(){return false;}

}
