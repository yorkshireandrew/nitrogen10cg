package com.bombheadgames.nitrogen2;

/** responsible for delegating rendering to other Nitrogen2 renderer classes */
public class Nitrogen2PolygonRenderer {
		
	final static void process
	(
		final NitrogenContext context,		
		final Vertex start, 	
		final Renderer renderer,
		final int[] polyData,
		final TexMap textureMap,
		final float lightingValue,
		final boolean useHLP
	)
	{
		if(renderer.isTextured())
		{
			if (useHLP && Nitrogen2TexturedRenderer.isHLP(start, context.qualityOfHLP))
			{
				Nitrogen2TexturedHLPRenderer.process
				(
						context,		
						start, 	
						renderer,
						polyData,
						textureMap,
						lightingValue						
				);
				return;
			}
			else
			{
				Nitrogen2TexturedRenderer.process
				(
						context,		
						start, 	
						renderer,
						polyData,
						textureMap,
						lightingValue						
				);
				return;				
			}
		}
		else
		{
			Nitrogen2UntexturedRenderer.process
			(
					context,		
					start, 	
					renderer,
					polyData,
					textureMap,
					lightingValue
			);
		}
	}
}
