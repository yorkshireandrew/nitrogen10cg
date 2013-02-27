/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bombheadgames.nitrogen2;

import java.io.Serializable;

/**
 *
 * @author andrew
 */
final public class Renderer_LitSimpleSingleColour implements Renderer,Serializable{

private static final long serialVersionUID = -7435141406825586043L;

private static final int ALPHA = 0xFF000000;
private static final int SHIFT = 20;
private static final int ZSHIFT = 20;
private static final int NUM = 1 << SHIFT;

private static final int LIGHT_SHIFT = 10;
private static final int LIGHT_NUM = 1 << LIGHT_SHIFT;

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
	System.out.println("lit simple render called");
	int colour = -1; // white
	if(polyData != null)colour = polyData[0] | ALPHA;
	
	// do lighting calculation
	final int lightVal = (int)(lightingValue * LIGHT_NUM);
	colour = litPixelValue(lightVal, colour);
	
	// **************** initialise nitrogen context references ***********************
	final int[] contextPixels = context.pix;
	final int[] contextZBuffer = context.zbuff;
	final int contextWidth = context.w;
	
	
	// **************** Initialise Left start position and calculate delta ***********
	int 	bigLeftSX 		= leftN2V.intSX << SHIFT;
	int 	leftSY			= leftN2V.intSY;
	int 	leftSZ			= leftN2V.intSZ;
	long 	bigLeftSZ		= ((long)leftSZ) << ZSHIFT;

	int 	bigLeftDestSX 	= leftDestN2V.intSX << SHIFT;
	int 	leftDestSY 		= leftDestN2V.intSY;
	long 	bigLeftDestSZ	= ((long)leftDestN2V.intSZ) << ZSHIFT;
		
	int 	leftDeltaSX; 
	int 	leftDeltaSY;
	long 	leftDeltaSZ;
	
	leftDeltaSY = leftDestSY - leftSY;
	
	if(leftDeltaSY > 0)
	{
			leftDeltaSX = (bigLeftDestSX - bigLeftSX)/leftDeltaSY;
			leftDeltaSY = (leftDestSY - leftSY);	// down counter
			leftDeltaSZ = (bigLeftDestSZ - bigLeftSZ)/leftDeltaSY;		
	}
	else
	{
			leftDeltaSX = 0;
			leftDeltaSY = 0;
			leftDeltaSZ = 0;
	}
	
	// **************** Initialise Right start position and calculate delta ***********
	int 	bigRightSX 		= rightN2V.intSX << SHIFT;
	int 	rightSY			= rightN2V.intSY;
	int 	rightSZ			= rightN2V.intSZ;
	long 	bigRightSZ		= ((long)rightSZ) << SHIFT;

	int 	bigRightDestSX 	= rightDestN2V.intSX << SHIFT;
	int 	rightDestSY 	= rightDestN2V.intSY;
	long 	bigRightDestSZ	= ((long)rightDestN2V.intSZ) << ZSHIFT;
		
	int 	rightDeltaSX; 
	int 	rightDeltaSY;
	long 	rightDeltaSZ;
	
	rightDeltaSY = rightDestSY - rightSY;
	
	if(rightDeltaSY > 0)
	{
			rightDeltaSX = (bigRightDestSX - bigRightSX) / rightDeltaSY;
			rightDeltaSY = (rightDestSY - rightSY);	// down counter
			rightDeltaSZ = (bigRightDestSZ - bigRightSZ) / rightDeltaSY;		
	}
	else
	{
			rightDeltaSX = 0;
			rightDeltaSY = 0;
			rightDeltaSZ = 0;
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
				
				colour,
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
			System.out.println("handling leftDeltaSY <= 0");
			bigLeftSX		= bigLeftDestSX;
			leftSY 			= leftDestSY;
			bigLeftSZ		= bigLeftDestSZ;			
			
			System.out.println("completed leftDestN2V is " + leftDestN2V);
			// find a new destination
			leftDestN2V = Nitrogen2UntexturedRenderer.findLeftDestN2V(leftDestN2V);
			
			if(leftDestN2V == null)
			{
				System.out.println("handling (leftDestN2V == null)");
				leftDeltaSX = 0;
				leftDeltaSY = 0;
				leftDeltaSZ = 0;
				trucking = false;
			}
			else
			{
				System.out.println("new leftDest is" + leftDestN2V);
				leftDestSY = leftDestN2V.intSY;
				bigLeftDestSX = leftDestN2V.intSX << SHIFT;
				bigLeftDestSZ = ((long)leftDestN2V.intSZ) << ZSHIFT;
				
				leftDeltaSY = leftDestSY - leftSY;
				System.out.println("new leftDeltaSY = " + leftDeltaSY );
				
				if(leftDeltaSY > 0)
				{
					leftDeltaSX = (bigLeftDestSX - bigLeftSX)/leftDeltaSY;
					leftDeltaSY = (leftDestSY - leftSY);	// down counter
					leftDeltaSZ = (bigLeftDestSZ - bigLeftSZ)/leftDeltaSY;	
				}
				else
				{
						System.out.println("leftDeltaSY = 0");
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
			System.out.println("handling rightDeltaSY <= 0");

			bigRightSX		= bigRightDestSX;
			rightSY 		= rightDestSY;
			bigRightSZ		= bigRightDestSZ;	
			
			System.out.println("completed rightDestN2V is " + rightDestN2V);
			
			// find a new destination
			rightDestN2V = Nitrogen2UntexturedRenderer.findRightDestN2V(rightDestN2V);
			
			if(rightDestN2V == null)
			{
				System.out.println("handling (rightDestN2V == null)");
				rightDeltaSX = 0;
				rightDeltaSY = 0;
				rightDeltaSZ = 0;
				trucking = false;
			}
			else
			{
				System.out.println("new rightDest is" + rightDestN2V);
				rightDestSY = rightDestN2V.intSY;
				bigRightDestSX = rightDestN2V.intSX << SHIFT;
				bigRightDestSZ = ((long)rightDestN2V.intSZ) << ZSHIFT;
				
				rightDeltaSY = rightDestSY - rightSY;
				System.out.println("new rightDeltaSY = " + rightDeltaSY );
				if(rightDeltaSY > 0)
				{
					rightDeltaSX = (bigRightDestSX - bigRightSX)/rightDeltaSY;
					rightDeltaSY = (rightDestSY - rightSY);	// down counter
					rightDeltaSZ = (bigRightDestSZ - bigRightSZ)/rightDeltaSY;		
				}
				else
				{
						System.out.println("rightDeltaSY = 0");
						rightDeltaSX = 0;
						rightDeltaSY = 0;
						rightDeltaSZ = 0;
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
		final int 	bigLeftSX, 
		long 		bigLeftSZ, 
		final int 	indexOffset,
		
		final int 	bigRightSX,
		final long 	bigRightSZ,

		final int 	colour,
		final int[] contextPixels,
		final int[] contextZBuffer,
		final int contextWidth
		)
{
	int lineStart 	= bigLeftSX >> SHIFT;
	int lineFinish 	= bigRightSX >> SHIFT;	
			
	System.out.println("rendering line " + lineStart + "->" + lineFinish);
	
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
		// ***************** RENDER PIXEL ****************
		int pixelZ = (int)(bigLeftSZ >> ZSHIFT);
		int index = indexOffset + lineStart;
		
		if(pixelZ > contextZBuffer[index])
		{
			contextZBuffer[index] = pixelZ;
			contextPixels[index] = colour;
		}
		
		// ***********************************************
		bigLeftSZ += zDelta;
		lineStart++;
		lineLength--;	
	}
}




//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
//*****************************************************************************
/** calculate the lit colour integer */
final private int litPixelValue(int lightVal, int colour)
{
	// convert to RGB
	int red = (colour 	& 0xFF0000) >> 16;
	int green = (colour & 0x00FF00) >> 8;
	int blue = (colour & 0x0000FF);
	
	// calculate lit RGB
	red = (red * lightVal) >> LIGHT_SHIFT;
	green = (green * lightVal) >> LIGHT_SHIFT;
	blue = (blue * lightVal) >> LIGHT_SHIFT;
	
	// clamp
	if(red > 255)		red = 255;
	if(green > green)	green = 255;
	if(blue > 255)		blue = 255;
	
	return (ALPHA | (red << 16) | (green << 8) | blue);
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

public boolean isTextured(){return false;}

public boolean allowsHLP(){return false;}

}// end of class
