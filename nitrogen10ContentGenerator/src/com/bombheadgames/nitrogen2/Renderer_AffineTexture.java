package com.bombheadgames.nitrogen2;

public class Renderer_AffineTexture implements Renderer{

private static final long serialVersionUID = -7435141406825586043L;

private static final int ALPHA = 0xFF000000;
private static final int SHIFT = 20;
private static final int ZSHIFT = 20;
private static final int NUM = 1 << SHIFT;
private static final int TEXTURE_SHIFT = 20; // align with Nitrogen2Vertex

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
	System.out.println("Affine render called");
	
	// **************** initialise nitrogen context references ***********************
	final int[] contextPixels = context.pix;
	final int[] contextZBuffer = context.zbuff;
	final int contextWidth = context.w;
	final int[] tex;
	final int textureWidth;
	
	if(textureMap !=  null)
	{
		tex = textureMap.tex;
		textureWidth = textureMap.w;
	}
	else{return;}
	
	
	// **************** Initialise Left start position and calculate delta ***********
	int		leftSX			= leftN2V.intSX;
	int 	bigLeftSX 		= leftN2V.intSX << SHIFT;
	int 	leftSY			= leftN2V.intSY;
	int 	leftSZ			= leftN2V.intSZ;
	long 	bigLeftSZ		= ((long)leftSZ) << ZSHIFT;
	
	int		leftTX			= leftN2V.intTX;
	int		leftTY			= leftN2V.intTY;

	int 	leftDestSX 		= leftDestN2V.intSX;
	int 	leftDestSY 		= leftDestN2V.intSY;
	long 	bigLeftDestSZ	= ((long)leftDestN2V.intSZ) << ZSHIFT;

	int		leftDestTX		= leftDestN2V.intTX;
	int		leftDestTY		= leftDestN2V.intTY;
	
	int 	leftDeltaSX; 
	int 	leftDeltaSY;
	long 	leftDeltaSZ;
	
	int		leftDeltaTX;
	int 	leftDeltaTY;
	
	leftDeltaSY = leftDestSY - leftSY;
	
	if(leftDeltaSY > 0)
	{
			int rec 	= NUM / leftDeltaSY;
			leftDeltaSX = (leftDestSX - leftSX)	* rec;
			leftDeltaSY = (leftDestSY - leftSY);	// down counter
			leftDeltaSZ = (bigLeftDestSZ - bigLeftSZ)/leftDeltaSY;
			
			leftDeltaTX = (leftDestTX - leftTX) / leftDeltaSY;
			leftDeltaTY = (leftDestTY - leftTY) / leftDeltaSY;
	}
	else
	{
			leftDeltaSX = 0;
			leftDeltaSY = 0;
			leftDeltaSZ = 0;
			
			leftDeltaTX = 0;
			leftDeltaTY = 0;		
	}
	
	// **************** Initialise Right start position and calculate delta ***********
	int		rightSX			= rightN2V.intSX;
	int 	bigRightSX 		= rightN2V.intSX << SHIFT;
	int 	rightSY			= rightN2V.intSY;
	int 	rightSZ			= rightN2V.intSZ;
	long 	bigRightSZ		= ((long)rightSZ) << SHIFT;
	
	int		rightTX			= rightN2V.intTX;
	int		rightTY			= rightN2V.intTY;

	int 	rightDestSX 	= rightDestN2V.intSX;
	int 	rightDestSY 	= rightDestN2V.intSY;
	long 	bigRightDestSZ	= ((long)rightDestN2V.intSZ) << ZSHIFT;

	int		rightDestTX			= rightDestN2V.intTX;
	int		rightDestTY			= rightDestN2V.intTY;
	
	int 	rightDeltaSX; 
	int 	rightDeltaSY;
	long 	rightDeltaSZ;
	
	int 	rightDeltaTX; 
	int 	rightDeltaTY;
	
	rightDeltaSY = rightDestSY - rightSY;
	
	if(rightDeltaSY > 0)
	{
			int rec 	= NUM / rightDeltaSY;
			rightDeltaSX = (rightDestSX - rightSX)	* rec;
			rightDeltaSY = (rightDestSY - rightSY);	// down counter
			rightDeltaSZ = (bigRightDestSZ - bigRightSZ) / rightDeltaSY;		

			rightDeltaTX = (rightDestTX - rightTX) / rightDeltaSY;
			rightDeltaTY = (rightDestTY - rightTY) / rightDeltaSY;
	}
	else
	{
			rightDeltaSX = 0;
			rightDeltaSY = 0;
			rightDeltaSZ = 0;
			
			rightDeltaTX = 0;
			rightDeltaTY = 0;
	}
	
	boolean trucking = true;
	
	int escape = 1000;
	while(trucking && (escape > 0))
	{
		escape--;
		// ************ Render a line *******
		renderLine(
				bigLeftSX, 
				bigLeftSZ, 
				leftSY * contextWidth, 
				
				bigRightSX,
				bigRightSZ,
				
				tex,
				textureWidth,
				
				leftTX,
				leftTY,
				
				rightTX,
				rightTY,
				
				contextPixels,
				contextZBuffer,
				contextWidth	
		);
		
		// *********** move by delta *******
		bigLeftSX += leftDeltaSX;
		leftSY++;
		bigLeftSZ += leftDeltaSZ;
		leftTX += leftDeltaTX;
		leftTY += leftDeltaTY;
				
		bigRightSX += rightDeltaSX;
		// rightSY++;
		bigRightSZ += rightDeltaSZ;
		rightTX += rightDeltaTX;
		rightTY += rightDeltaTY;
		
		// *********** decrement steps to destination down counters ****
		leftDeltaSY--;
		rightDeltaSY--;
		
		// *********** handle if we reach left destination ******************
		if(leftDeltaSY <= 0)
		{
			System.out.println("handling leftDeltaSY <= 0");
			leftN2V 		= leftDestN2V;
			
			// now update left to eliminate rounding errors
			leftSX			= leftN2V.intSX;
			bigLeftSX 		= leftN2V.intSX << SHIFT;
			leftSY			= leftN2V.intSY;
			leftSZ			= leftN2V.intSZ;
			bigLeftSZ		= ((long)leftSZ) << SHIFT;
			
			leftTX			= leftN2V.intTX;
			leftTY			= leftN2V.intTY;
					
			System.out.println("completed leftDestN2V is " + leftDestN2V);
			// find a new destination
			leftDestN2V = Nitrogen2UntexturedRenderer.findLeftDestN2V(leftDestN2V);
			
			if(leftDestN2V == null)
			{
				System.out.println("handling (leftDestN2V == null)");
				leftDeltaSX = 0;
				leftDeltaSY = 0;
				leftDeltaSZ = 0;
				
				leftDeltaTX = 0;
				leftDeltaTY = 0;				
				
				trucking = false;
			}
			else
			{
				System.out.println("new leftDest is" + leftDestN2V);
				leftDeltaSY = leftDestN2V.intSY - leftSY;
				System.out.println("new leftDeltaSY = " + leftDeltaSY );
				if(leftDeltaSY > 0)
				{
						int rec 	= NUM / leftDeltaSY;
						leftDeltaSX = (leftDestN2V.intSX - leftSX)	* rec;
						leftDeltaSZ = ((long)leftDestN2V.intSZ) << ZSHIFT;
						leftDeltaSZ -= bigLeftSZ;
						leftDeltaSZ = leftDeltaSZ / leftDeltaSY;
						
						leftDeltaTX = (leftDestN2V.intTX - leftTX) / leftDeltaSY;
						leftDeltaTY = (leftDestN2V.intTY - leftTY) / leftDeltaSY;
				}
				else
				{
						leftDeltaSX = 0;
						leftDeltaSY = 0;
						leftDeltaSZ = 0;
						
						leftDeltaTX = 0;
						leftDeltaTY = 0;
						
						trucking = false;
				}
			}		
		}
		
		// *********** handle if we reach right destination ******************
		if(rightDeltaSY <= 0)
		{
			System.out.println("handling rightDeltaSY <= 0");
			rightN2V 		= rightDestN2V;
			
			// now update right to eliminate rounding errors
			rightSX			= rightN2V.intSX;
			bigRightSX 		= rightN2V.intSX << SHIFT;
			rightSY			= rightN2V.intSY;
			rightSZ			= rightN2V.intSZ;
			bigRightSZ		= ((long)rightSZ) << SHIFT;
			
			rightTX			= rightN2V.intTX;
			rightTY			= rightN2V.intTY;
			
			System.out.println("completed rightDestN2V is " + rightDestN2V);
			
			// find a new destination
			rightDestN2V = Nitrogen2UntexturedRenderer.findRightDestN2V(rightDestN2V);
			
			if(rightDestN2V == null)
			{
				System.out.println("handling (rightDestN2V == null)");
				rightDeltaSX = 0;
				rightDeltaSY = 0;
				rightDeltaSZ = 0;
				
				rightDeltaTX = 0;
				rightDeltaTY = 0;
				
				trucking = false;
			}
			else
			{
				System.out.println("new rightDest is" + rightDestN2V);
				rightDeltaSY = rightDestN2V.intSY - rightSY;
				System.out.println("new rightDeltaSY = " + rightDeltaSY );
				if(rightDeltaSY > 0)
				{
						int rec 	= NUM / rightDeltaSY;
						rightDeltaSX = (rightDestN2V.intSX - rightSX)	* rec;
						rightDeltaSZ = (rightDestN2V.intSZ - rightSZ)	* rec;
						
						rightDeltaSZ = ((long)rightDestN2V.intSZ) << ZSHIFT;
						rightDeltaSZ -= bigRightSZ;
						rightDeltaSZ = rightDeltaSZ / rightDeltaSY;
						
						rightDeltaTX = (rightDestN2V.intTX - rightTX) / rightDeltaSY;
						rightDeltaTY = (rightDestN2V.intTY - rightTY) / rightDeltaSY;
				}
				else
				{
						rightDeltaSX = 0;
						rightDeltaSY = 0;
						rightDeltaSZ = 0;
						
						rightDeltaTX = 0;
						rightDeltaTY = 0;

						
						trucking = false;
				}
			}		
		}
	}//end of while loop
	
	// ************ Render final line *******
	System.out.println("render final line");
	renderLine(
			bigLeftSX, 
			bigLeftSZ, 
			leftSY * contextWidth, 
			
			bigRightSX,
			bigRightSZ,
			
			tex,
			textureWidth,
			
			leftTX,
			leftTY,
			
			rightTX,
			rightTY,
			
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
		final int 	bigLeftSX, 
		long 	bigLeftSZ, 
		final int 	indexOffset,
		
		final int 	bigRightSX,
		final long 	bigRightSZ,

		final int[] tex,
		final int textureWidth,
		
		int leftTX,
		int leftTY,
		
		final int rightTX,
		final int rightTY,
		
		final int[] contextPixels,
		final int[] contextZBuffer,
		final int contextWidth
		)
{
	int lineStart 	= bigLeftSX >> SHIFT;
	int lineFinish 	= bigRightSX >> SHIFT;
	
			
	System.out.println("rendering line " + lineStart + "->" + lineFinish);
	
	int lineLength = lineFinish - lineStart;
	
	// calculate zDelta and texture delta
	long zDelta;
	int txDelta;
	int tyDelta;
	if(lineLength > 0)
	{
		zDelta = (bigRightSZ - bigLeftSZ) / lineLength;
		txDelta = (rightTX - leftTX) / lineLength;
		tyDelta = (rightTY - leftTY) / lineLength;
	}
	else
	{
		zDelta = 0;
		txDelta = 0;
		tyDelta = 0;
	}
	
	
	
	while(lineLength >= 0)
	{
		// ***************** RENDER PIXEL ****************
		int pixelZ = (int)(bigLeftSZ >> ZSHIFT);
		int index = indexOffset + lineStart;
		
		if(pixelZ > contextZBuffer[index])
		{
			contextZBuffer[index] = pixelZ;
			contextPixels[index] = tex[(leftTY >> TEXTURE_SHIFT) * textureWidth + (leftTX >> TEXTURE_SHIFT)];
		}
		
		// ***********************************************
		bigLeftSZ += zDelta;
		leftTX += txDelta;
		leftTY += tyDelta;
		
		lineStart++;
		lineLength--;	
	}
}

//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************

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

public boolean isTextured(){return true;}

public boolean allowsHLP(){return false;}


}
