package com.bombheadgames.nitrogen2;

public class Renderer_OutlineOld implements Renderer {

	private static final int SHIFT = 20;
	private static final int NUM = 1 << SHIFT;

	public void render(
			final NitrogenContext context,	
			
			Nitrogen2Vertex leftN2V,
			Nitrogen2Vertex leftDestN2V,
			
			Nitrogen2Vertex rightN2V,
			Nitrogen2Vertex rightDestN2V,			
			
			final int[] polyData,
			final TexMap textureMap,
			final float lightingValue	
			)
	{
		
		// **************** initialise colour ********************************************
		
		int colour = 16711680; // red
		if((polyData != null) && (polyData.length > 0))colour = polyData[0];
		
		// **************** initialise nitrogen context references ***********************
		final int[] contextPixels = context.pix;
		final int[] contextZBuffer = context.zbuff;
		final int contextWidth = context.w;
		
		
		// **************** Initialise Left start position and calculate delta ***********
		int		leftSX			= leftN2V.intSX;
		int 	bigLeftSX 		= leftN2V.intSX << SHIFT;
		int 	leftSY			= leftN2V.intSY;
		int 	leftSZ			= leftN2V.intSZ;
		long 	bigLeftSZ		= ((long)leftSZ) << SHIFT;

		int 	leftDestSX 	= leftDestN2V.intSX;
		int 	leftDestSY 	= leftDestN2V.intSY;
		long 	leftDestSZ	= leftDestN2V.intSZ;
			
		int 	leftDeltaSX; 
		int 	leftDeltaSY;
		long 	leftDeltaSZ;
		
		leftDeltaSY = leftDestSY - leftSY;
		
		if(leftDeltaSY > 0)
		{
				int rec 	= NUM / leftDeltaSY;
				leftDeltaSX = (leftDestSX - leftSX)	* rec;
				leftDeltaSY = (leftDestSY - leftSY);	// down counter
				leftDeltaSZ = (leftDestSZ - leftSZ)	* rec;		
		}
		else
		{
				leftDeltaSX = 0;
				leftDeltaSY = 0;
				leftDeltaSZ = 0;
		}
		
		// **************** Initialise Right start position and calculate delta ***********
		int		rightSX			= rightN2V.intSX;
		int 	bigRightSX 		= rightN2V.intSX << SHIFT;
		int 	rightSY			= rightN2V.intSY;
		int 	rightSZ			= rightN2V.intSZ;
		long 	bigRightSZ		= ((long)rightSZ) << SHIFT;

		int 	rightDestSX 	= rightDestN2V.intSX;
		int 	rightDestSY 	= rightDestN2V.intSY;
		long 	rightDestSZ		= rightDestN2V.intSZ;
			
		int 	rightDeltaSX; 
		int 	rightDeltaSY;
		long 	rightDeltaSZ;
		
		rightDeltaSY = rightDestSY - rightSY;
		
		if(rightDeltaSY > 0)
		{
				int rec 	= NUM / rightDeltaSY;
				rightDeltaSX = (rightDestSX - rightSX)	* rec;
				rightDeltaSY = (rightDestSY - rightSY);	// down counter
				rightDeltaSZ = (rightDestSZ - rightSZ)	* rec;		
		}
		else
		{
				rightDeltaSX = 0;
				rightDeltaSY = 0;
				rightDeltaSZ = 0;
		}
		
		// ************** render top line ******************
		// renderTerminatorLine deals with cases of 
		// horisontal top & bottom lines
		// *************************************************
		
		renderTerminatorLine(
				bigLeftSX, 
				bigLeftSZ, 
				leftSY * contextWidth, 
				
				bigRightSX,
				bigRightSZ,
				
				colour,
				contextPixels,
				contextZBuffer,
				contextWidth	
		);
		
		boolean trucking = true;
		
		while(trucking)
		{
			// ************ Render a line *******
			renderLine(
					bigLeftSX, 
					bigLeftSZ, 
					leftSY * contextWidth, 
					
					bigRightSX,
					bigRightSZ,
					
					colour,
					contextPixels,
					contextZBuffer,
					contextWidth,
					
					leftDeltaSX,
					rightDeltaSX
					
			);
			
			// *********** move by delta *******
			bigLeftSX += leftDeltaSX;
			leftSY++;
			bigLeftSZ += leftDeltaSZ;
					
			bigRightSX += rightDeltaSX;
			// rightSY++;
			bigRightSZ += rightDeltaSZ;
			
			// *********** decrement steps to destination down counters ****
			leftDeltaSY--;
			rightDeltaSY--;
			
			// *********** handle if we reach left destination ******************
			if(leftDeltaSY <= 0)
			{
				leftN2V 		= leftDestN2V;
				
				// now update left to eliminate rounding errors
				leftSX			= leftN2V.intSX;
				bigLeftSX 		= leftN2V.intSX << SHIFT;
				leftSY			= leftN2V.intSY;
				leftSZ			= leftN2V.intSZ;
				bigLeftSZ		= ((long)leftSZ) << SHIFT;
				
				// find a new destination
				leftDestN2V = Nitrogen2UntexturedRenderer.findLeftDestN2V(leftDestN2V);
				
				if(leftDestN2V == null)
				{
					leftDeltaSX = 0;
					leftDeltaSY = 0;
					leftDeltaSZ = 0;
					trucking = false;
				}
				else
				{
					leftDeltaSY = leftDestSY - leftSY;
					
					if(leftDeltaSY > 0)
					{
							int rec 	= NUM / leftDeltaSY;
							leftDeltaSX = (leftDestSX - leftSX)	* rec;
							leftDeltaSY = (leftDestSY - leftSY);	// down counter
							leftDeltaSZ = (leftDestSZ - leftSZ)	* rec;		
					}
					else
					{
							leftDeltaSX = 0;
							leftDeltaSY = 0;
							leftDeltaSZ = 0;
							trucking = false;
					}
				}		
			}
			
			// *********** handle if we reach right destination ******************
			if(rightDeltaSY <= 0)
			{
				rightN2V 		= rightDestN2V;
				
				// now update right to eliminate rounding errors
				rightSX			= rightN2V.intSX;
				bigRightSX 		= rightN2V.intSX << SHIFT;
				rightSY			= rightN2V.intSY;
				rightSZ			= rightN2V.intSZ;
				bigRightSZ		= ((long)rightSZ) << SHIFT;
				
				// find a new destination
				rightDestN2V = Nitrogen2UntexturedRenderer.findRightDestN2V(rightDestN2V);
				
				if(rightDestN2V == null)
				{
					rightDeltaSX = 0;
					rightDeltaSY = 0;
					rightDeltaSZ = 0;
					trucking = false;
				}
				else
				{
					rightDeltaSY = rightDestSY - rightSY;
					
					if(rightDeltaSY > 0)
					{
							int rec 	= NUM / rightDeltaSY;
							rightDeltaSX = (rightDestSX - rightSX)	* rec;
							rightDeltaSY = (rightDestSY - rightSY);	// down counter
							rightDeltaSZ = (rightDestSZ - rightSZ)	* rec;		
					}
					else
					{
							rightDeltaSX = 0;
							rightDeltaSY = 0;
							rightDeltaSZ = 0;
							trucking = false;
					}
				}		
			}
		}//end of while loop
		
		// ************ Render final line *******
		renderTerminatorLine(
				bigLeftSX, 
				bigLeftSZ, 
				leftSY * contextWidth, 
				
				bigRightSX,
				bigRightSZ,
				
				colour,
				contextPixels,
				contextZBuffer,
				contextWidth		
		);
	}

	//*****************************************************************************
	//*****************************************************************************
	// ****************************** Render Line *********************************
	//*****************************************************************************
	//*****************************************************************************

	private final void renderLine(
			int 	bigLeftSX, 
			long 	bigLeftSZ, 
			int 	indexOffset,
			
			int 	bigRightSX,
			long 	bigRightSZ,

			int 	colour,
			final int[] contextPixels,
			final int[] contextZBuffer,
			final int contextWidth,
			
			int		leftDeltaSX,
			int		rightDeltaSX
			)
	{
		int lineStart 	= bigLeftSX >> SHIFT;
		int lineFinish 	= bigRightSX >> SHIFT;
				
		int lineStartDelta = leftDeltaSX >> SHIFT;
		int lineFinishDelta = leftDeltaSX >> SHIFT;
		
		// ********* calculate bit at left of line to draw ****
		int lineStartEnd = lineStart + lineStartDelta;
		if(lineStartEnd < 0)lineStartEnd = 0;
		if(lineStartEnd >= contextWidth)lineStartEnd = contextWidth-1;
		
		if(lineStartEnd < lineStart)
		{
			int swap = lineStart;
			lineStart = lineStartEnd;
			lineStartEnd = swap;
		}
		
		// ********* calculate bit at right of line to draw ****
		int lineFinishEnd = lineFinish + lineFinishDelta;
		if(lineFinishEnd < 0)lineFinishEnd = 0;
		if(lineFinishEnd >= contextWidth)lineFinishEnd = contextWidth-1;
		
		if(lineFinishEnd < lineFinish)
		{
			int swap = lineFinish;
			lineFinish = lineFinishEnd;
			lineFinishEnd = swap;
		}
		
		// ************* draw the bits ****************************
		for(int x = lineStart; x <= lineStartEnd; x++)
		{
			contextPixels[indexOffset + x] = colour;
		}
		
		for(int x = lineFinish; x <= lineFinishEnd; x++)
		{
			contextPixels[indexOffset + x] = colour;
		}
	}
	
	//*****************************************************************************
	//*****************************************************************************
	// ****************************** Render Terminator Line **********************
	//*****************************************************************************
	//*****************************************************************************

	private final void renderTerminatorLine(
			int 	bigLeftSX, 
			long 	bigLeftSZ, 
			int 	indexOffset,
			
			int 	bigRightSX,
			long 	bigRightSZ,

			int 	colour,
			final int[] contextPixels,
			final int[] contextZBuffer,
			final int contextWidth
			)
	{
		int lineStart 	= bigLeftSX >> SHIFT;
		int lineFinish 	= bigRightSX >> SHIFT;		
		
		int lineLength = lineFinish - lineStart;
			
		while(lineLength >= 0)
		{
			// ***************** RENDER PIXEL ****************
			int index = indexOffset + lineStart;
			
			contextPixels[index] = colour;
			
			// ***********************************************
			lineStart++;
			lineLength--;	
		}
	}
	
	
	
	
	public void renderHLP(
			final NitrogenContext context,	
			
			final Nitrogen2Vertex leftN2V,
			final Nitrogen2Vertex leftDestN2V,
			
			final Nitrogen2Vertex rightN2V,
			final Nitrogen2Vertex rightDestN2V,			
			
			final int[] polyData,
			final TexMap textureMap,
			final float lightingValue	
			){}

	public boolean isTextured(){return false;}

	public boolean allowsHLP(){return false;}
	
}
