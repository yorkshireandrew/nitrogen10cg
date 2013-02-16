package com.bombheadgames.nitrogen2;

public final class Renderer_Picking implements Renderer{
	
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
	
	boolean trucking = true;
	
	while(trucking)
	{
		// ************ Render a line *******
		renderLine(
				bigLeftSX, 
				bigLeftSZ, 
				leftSY * contextWidth,
				leftSY,
				
				bigRightSX,
				bigRightSZ,
				
				context,
				contextPixels,
				contextZBuffer,
				contextWidth	
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
	renderLine(
			bigLeftSX, 
			bigLeftSZ, 
			leftSY * contextWidth,
			leftSY,
			
			bigRightSX,
			bigRightSZ,
			
			context,
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
		int		y,
		
		int 	bigRightSX,
		long 	bigRightSZ,

		NitrogenContext context,
		final int[] contextPixels,
		final int[] contextZBuffer,
		final int contextWidth
		)
{
	int contextPickX = context.pickX;
	
	if(y != context.pickY)return;
	
	int lineStart 	= bigLeftSX >> SHIFT;
	int lineFinish 	= bigRightSX >> SHIFT;	
	
	if(lineStart > contextPickX)return;
	if(lineFinish < contextPickX)return;
	
	int lineLength = lineFinish - lineStart;
	
	// calculate zDelta
	long zDelta;
	if(lineLength > 0)
	{
		zDelta = (bigRightSZ - bigLeftSZ) / lineLength;
	}
	else
	{
		zDelta = 0;
	}
	
	while(lineLength >= 0)
	{
		// ***************** TEST PIXEL ****************
		int pixelZ = (int)(bigLeftSZ >> SHIFT);
		int index = indexOffset + lineStart;
		
		if(lineStart == contextPickX)
		{
			if(pixelZ > contextZBuffer[index])
			{
				contextZBuffer[index] = pixelZ;
				context.pickedPolygon = context.currentPolygon;
				context.pickedItem = context.currentItem;
				context.pickDetected = true;
			}
		}
		
		// ***********************************************
		bigLeftSZ += zDelta;
		lineStart++;
		lineLength--;	
	}
}
	
	public boolean isTextured(){return false;}

}
