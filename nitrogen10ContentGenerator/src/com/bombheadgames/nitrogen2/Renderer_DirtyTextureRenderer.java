package com.bombheadgames.nitrogen2;

import java.io.Serializable;

/**
 *
 * @author andrew
 */
final public class Renderer_DirtyTextureRenderer implements Renderer,Serializable{

private static final long serialVersionUID = -9205815146173706598L;

static final int SH = 20;
static final int NUM = 1 << SH;

static final int OUTER_HOLE_FILL_CONSTANT = 32;
static final int INNER_HOLE_FILL_CONSTANT = 4;

static final int AFFINE_SH = 20;
static final int AFFINE_NUM = 1 << SH;
static final float AFFINE_NUM_FLOAT = (float)AFFINE_NUM;
static final int AFFINE_CONSTANT = 16;
static final int AFFINE_RECIPROCAL = AFFINE_NUM / AFFINE_CONSTANT;
static final float AFFINE_RECIPROCAL_FLOAT = AFFINE_NUM_FLOAT / (float)(AFFINE_CONSTANT);

/**
 * 
 * @param st_aux1	texture map x coord for line start point
 * @param st_aux2	texture map y coord for line start point
 * @param st_aux3	not used in SimpleTextureRenderer
 * 
 * @param st_daux1	increment of texture map x coord (for line start point) for a one pixel step along the y axis
 * @param st_daux2	increment of texture map y coord (for line start point) for a one pixel step along the y axis
 * @param st_daux3	not used in SimpleTextureRenderer
 * 
 * @param fin_aux1	texture map x coord for line finish point
 * @param fin_aux2	texture map y coord for line finish point
 * @param fin_aux3	not used in SimpleTextureRenderer
 * 
 * @param fin_daux1	increment of texture map x coord (for line finish point) for a one pixel step along the y axis
 * @param fin_daux2	increment of texture map y coord (for line finish point) for a one pixel step along the y axis
 * @param fin_daux3	not used in SimpleTextureRenderer
 */
	final public void renderTrapezoidHLP(
	final NitrogenContext context,
	final Vertex a, final Vertex b, final Vertex c, final Vertex d, 
	final int[] polyData, 
	final TexMap texMap,
	final float lightingValue
     )
	{
		
		// Calculate outer loop count
		int startDeltaX = Math.abs((b.sx - a.sx));
		int startDeltaY = Math.abs((b.sy - a.sy));
		int startDelta = startDeltaX + startDeltaY;
		
		int finishDeltaX = Math.abs((d.sx - c.sx));
		int finishDeltaY = Math.abs((d.sy - c.sy));
		int finishDelta = finishDeltaX + finishDeltaY;
		
		int outerDelta = startDelta;
		if(finishDelta > outerDelta)outerDelta = finishDelta;
		
		//kill degenerate polygons
		if(outerDelta == 0)return;
		
		outerDelta = outerDelta * OUTER_HOLE_FILL_CONSTANT;
		outerDelta = outerDelta >> 4;

		// Calculate inner loop count
		int bcDeltaX = Math.abs((c.sx - b.sx));
		int bcDeltaY = Math.abs((c.sy - b.sy));
		int bcDelta = bcDeltaX + bcDeltaY;

		int adDeltaX = Math.abs((d.sx - a.sx));
		int adDeltaY = Math.abs((d.sy - a.sy));
		int adDelta = adDeltaX + adDeltaY;

		int innerDelta = bcDelta;
		if(adDelta > innerDelta)innerDelta = adDelta;
		
		//kill degenerate polygons
		if(innerDelta == 0)return;

		innerDelta = innerDelta * INNER_HOLE_FILL_CONSTANT;
		innerDelta = innerDelta >> 4;
		
		// calculate outer step values
		float outerReciprocal = 1.0f / (float)outerDelta;
		
		float startXStep = (b.vs_x - a.vs_x)* outerReciprocal;
		float startYStep = (b.vs_y - a.vs_y)* outerReciprocal;
		float startZStep = (b.vs_z - a.vs_z)* outerReciprocal;
		
		float startTXStep = (b.aux1 - a.aux1)* outerReciprocal;
		float startTYStep = (b.aux2 - a.aux2)* outerReciprocal;

		float finishXStep = (c.vs_x - d.vs_x)* outerReciprocal;
		float finishYStep = (c.vs_y - d.vs_y)* outerReciprocal;
		float finishZStep = (c.vs_z - d.vs_z)* outerReciprocal;
		
		float finishTXStep = (c.aux1 - d.aux1)* outerReciprocal;
		float finishTYStep = (c.aux2 - d.aux2)* outerReciprocal;
		
		// Calculate inner reciprocal
		float innerReciprocal = 1.0f / (float)innerDelta;
			
		float startX = a.vs_x;
		float startY = a.vs_y;
		float startZ = a.vs_z;
		float startTX = a.aux1;
		float startTY = a.aux2;
		
		float finishX = d.vs_x;
		float finishY = d.vs_y;
		float finishZ = d.vs_z;
		float finishTX = d.aux1;
		float finishTY = d.aux2;
		
		for(int outerCount = 0; outerCount < outerDelta; outerCount++)
		{
			// calculate inner step values
			performInnerProcess(
					startX,
					startY,
					startZ,
					startTX,
					startTY,
					
					finishX,
					finishY,
					finishZ,
					finishTX,
					finishTY,
					
					innerDelta,
					innerReciprocal,
					
					context,
					polyData, 
					texMap,
					lightingValue
					);
			startX += startXStep;
			startY += startYStep;
			startZ += startZStep;
			startTX += startTXStep;
			startTY += startTYStep;
			
			finishX += finishXStep;
			finishY += finishYStep;
			finishZ += finishZStep;
			finishTX += finishTXStep;
			finishTY += finishTYStep;
		}
	}




		final static private void 	performInnerProcess(
		final float startX,
		final float startY,
		final float startZ,
		final float startTX,
		final float startTY,
		
		final float finishX,
		final float finishY,
		final float finishZ,
		final float finishTX,
		final float finishTY,
		
		final int innerDelta,
		final float innerReciprocal,
		
		final NitrogenContext context,
		final int[] polyData, 
		final TexMap texMap,
		final float lightingValue
		)
		{	
			int old_sx = Integer.MAX_VALUE;
			int old_sy = Integer.MAX_VALUE;
			
			float magnification = context.magnification;
			int minValue = Integer.MIN_VALUE;
			float zk = context.zk;
			int midh = context.midh;
			int midw = context.midw;
			boolean contentGeneratorForcesNoPerspective = context.contentGeneratorForcesNoPerspective;
			int contextWidth = context.w;
			int[] contextPixels = context.pix;
			int[] contextZBuffer = context.zbuff;
			int textureWidth = texMap.w;
			int[] texture = texMap.tex;
			
			
			final float innerXStep = (finishX - startX)* innerReciprocal;
			final float innerYStep = (finishY - startY)* innerReciprocal;
			final float innerZStep = (finishZ - startZ)* innerReciprocal;		
			final float innerTXStep = (finishTX - startTX)* innerReciprocal;
			final float innerTYStep = (finishTY - startTY)* innerReciprocal;
    		int affineTXStep = (int)(innerTXStep * AFFINE_RECIPROCAL_FLOAT);
    		int affineTYStep = (int)(innerTXStep * AFFINE_RECIPROCAL_FLOAT);
			
			float innerStartX = startX; 
			float innerStartY = startY; 
			float innerStartZ = startZ; 
			float innerStartTX = startTX; 
			float innerStartTY = startTY;
		 	
			int innerStartSX;
			int innerStartSY;
			int innerStartSZ;
			
			// calculate screen coordinate
    		if(contentGeneratorForcesNoPerspective)
    		{
    			innerStartSZ = minValue - (int)(zk/innerStartZ);   		 		
        		innerStartSX = (int)(innerStartX) + midw;
        		innerStartSY = midh - (int)(innerStartY);
    		}
    		else
    		{
    			innerStartSZ = minValue - (int)(context.zk/innerStartZ);   		 		
    			innerStartSX = (int)((magnification * innerStartX)/(-innerStartZ)) + midw;
    			innerStartSY = midh - (int)((magnification * innerStartY)/(-innerStartZ));  			
    		}
   		
			for(int innerCount = 0; innerCount <= innerDelta; innerCount++)
			{
				float innerFinishX = innerStartX += innerXStep;
				float innerFinishY = innerStartY += innerYStep;
				float innerFinishZ = innerStartZ += innerZStep;
				float innerFinishTX = innerStartTX += innerTXStep;
				float innerFinishTY = innerStartTY += innerTYStep;
				int innerFinishSX;
				int innerFinishSY;
				int innerFinishSZ;
				
	    		if(contentGeneratorForcesNoPerspective)
	    		{
	    			innerFinishSZ = minValue - (int)(zk/innerFinishZ);   		 		
	        		innerFinishSX = (int)(innerFinishX) + midw;
	        		innerFinishSY = midh - (int)(innerFinishY);
	    		}
	    		else
	    		{
	    			innerFinishSZ = minValue - (int)(context.zk/innerFinishZ);   		 		
	    			innerFinishSX = (int)((magnification * innerFinishX)/(-innerFinishZ)) + midw;
	    			innerFinishSY = midh - (int)((magnification * innerFinishY)/(-innerFinishZ));  			
	    		}
				
				performAffineLine(
						innerStartSX,
						innerStartSY,
						innerStartSZ,
						innerStartTX,
						innerStartTY,
						
						innerFinishSX,
						innerFinishSY,
						innerFinishSZ,
						
						affineTXStep,
						affineTYStep,

						contextWidth,
						contextPixels,
						contextZBuffer,
						textureWidth,
						texture,
						old_sx,
						old_sy
						);
				
				innerStartX = innerFinishX;
				innerStartY = innerFinishY;
				innerStartZ = innerFinishZ;
				innerStartTX = innerFinishTX;
				innerStartTY = innerFinishTY;	
				
				innerStartSX = innerFinishSX;
				innerStartSY = innerFinishSY;
				innerStartSZ = innerFinishSZ;
			}
		}
		
		final private static void performAffineLine(
				final int innerStartSX,
				final int innerStartSY,
				final int innerStartSZ,
				final float innerStartTX,
				final float innerStartTY,
				
				final int innerFinishSX,
				final int innerFinishSY,
				final int innerFinishSZ,
				
				final int affineTXStep,
				final int affineTYStep,
				
				final int contextWidth,
				final int[] contextPixels,
				final int[] contextZBuffer,
				final int textureWidth,
				final int[] texture,
				
				int old_sx,
				int old_sy
				)
		{
			int af_innerStartSX = innerStartSX << AFFINE_SH;
			int af_innerStartSY = innerStartSY << AFFINE_SH;
			long af_innerStartSZ = ((long)innerStartSZ) << AFFINE_SH;
			int af_innerStartTX = ((int)(AFFINE_NUM_FLOAT * innerStartTX));
			int af_innerStartTY = ((int)(AFFINE_NUM_FLOAT * innerStartTY));
			
			int af_SXStep = AFFINE_RECIPROCAL * (int)(innerFinishSX - innerStartSX);
			int af_SYStep = AFFINE_RECIPROCAL * (int)(innerFinishSY - innerStartSY);
			long af_SZStep = AFFINE_RECIPROCAL * (long)(innerFinishSZ - innerStartSZ);
			
			for(int x = 0; x <= AFFINE_CONSTANT ; x++)
			{
				int sx = af_innerStartSX >> AFFINE_SH;
				int sy = af_innerStartSY >> AFFINE_SH;
				int sz = (int)(af_innerStartSZ >> AFFINE_SH);
		
				if((sx == old_sx)&&(sy == old_sy))
				{
					// we have already plotted this
					// move on
					af_innerStartSX += af_SXStep;
					af_innerStartSY += af_SYStep;
					af_innerStartSZ += af_SZStep;
					af_innerStartTX += affineTXStep;
					af_innerStartTY += affineTYStep;
					continue;
				}
				
				int index = sy * contextWidth + sx;
				old_sx = sx;
				old_sy = sy;
				
				// *********************************************************************
				// *********************************************************************
				// ************************* PLOT THE PIXEL ****************************
				// *********************************************************************
				// *********************************************************************
				
				if( sz >= contextZBuffer[index])
				{
                    contextPixels[index] = texture[((af_innerStartTY >> SH)*textureWidth + (af_innerStartTX >> SH))];
                    contextZBuffer[index] = sz;
				}
				
				// *********************************************************************
				// *********************************************************************
				// *********************************************************************
				// *********************************************************************
				// *********************************************************************
				
				af_innerStartSX += af_SXStep;
				af_innerStartSY += af_SYStep;
				af_innerStartSZ += af_SZStep;
				af_innerStartTX += affineTXStep;
				af_innerStartTY += affineTYStep;
	
			}		
		}	
		
		final public void renderTrapezoid(

		        // line start point
		        int st_x,   long st_z,
		        int st_aux1,
		        int st_aux2,
		        int st_aux3,
		        
		        // start point increment
		        final int st_dx,   final long st_dz,
		        final int st_daux1,
		        final int st_daux2,
		        final int st_daux3,
		        
		        // line finish point
		        int fin_x,   long fin_z,
		        int fin_aux1,
		        int fin_aux2,
		        final int fin_aux3,                    

		        // finish point increment
		        final int fin_dx,   final long fin_dz,
		        final int fin_daux1,
		        final int fin_daux2,
		        final int fin_daux3,
		        
		        // start and finish y values
		        // note the last line y_max is not rendered
		        int y_counter,   int y_max,

		        // pixel buffer
		        int[] p,
		        
		        // z buffer
		        int[] z,

		        // texture buffer
		        int[] tex,

		        // output image width
		        int pixelBufferWidth,

		        // input texture width
		        int textureBufferWidth,
		        
		        // global parameters array - eg. the colour for a single colour polygon
		        int[] polyData,
		        float lightingValue,
		        NitrogenContext context,
		        boolean hlp,
		        Vertex a,
		        Vertex b,
		        Vertex c,
		        Vertex d
		        )


		{

		        // local variables used to step along a line

		        // line rendering fields
		        int srl_st_x;
		        int srl_fin_x;
		        int srl_tx;
		        int srl_ty;
		        long srl_z;

		        // line rendering fields used for stepping
		        int srl_dx;
		        int srl_dtx;
		        int srl_dty;
		        long srl_dz;

		        int linestart;      // value that is constant for a line - gets precalculated

		        // fields used for pixel rendering
		        int srl_z2;         // integer version of srl_z
		        int index;          // index into colour and z buffer arrays

		        long temp;          // used to do a faster divide
		        int rec;

		        while(y_counter < y_max)
		        {

		            //***********************************************
		            //****                                       ****
		            //****    START OF CODE TO RENDER A LINE     ****
		            //****                                       ****
		            //***********************************************

		            // set line rendering fields to start of line
		            srl_st_x = (st_x >> SH);
		            srl_fin_x = (fin_x >> SH);
		            srl_dx = srl_fin_x - srl_st_x;
		            srl_tx = st_aux1;
		            srl_ty = st_aux2;
		            srl_z = st_z;

		            // prevent zero srl_dx case throwing div0
		            if(srl_dx == 0) srl_dx = 1;

		            // calculate line rendering fields used for stepping
		            rec = NUM / srl_dx;

		            temp = (fin_aux1 - st_aux1);
		            srl_dtx = (int) ((temp * rec) >> SH);

		            temp = (fin_aux2 - st_aux2);
		            srl_dty = (int) ((temp * rec) >> SH);

		            temp = (fin_z - st_z);
		            srl_dz = ((temp * rec) >> SH);

		            linestart = y_counter * pixelBufferWidth;         // pre-calculate this constant value

		            while(srl_st_x <= srl_fin_x)
		            {
		                // *******************************************
		                // *******************************************
		                // ****                                   ****
		                // ****     START OF RENDER A PIXEL       ****
		                // ****                                   ****
		                // *******************************************
		                // *******************************************

		                srl_z2 = (int)(srl_z >> SH);
		                index = (linestart + srl_st_x);

		                if(srl_z2 >= z[index])
		                {
		                    p[index] = tex[((srl_ty >> SH)*textureBufferWidth + (srl_tx >> SH))];
		                    z[index] = srl_z2;
		                    //p[index] = (int)(0xFF000000 + (srl_z2));    // test
		                }

		                // *******************************************
		                // *******************************************
		                // ****                                   ****
		                // ****       END OF RENDER A PIXEL       ****
		                // ****                                   ****
		                // *******************************************
		                // *******************************************

		                srl_st_x++;
		                srl_z   += srl_dz;
		                srl_tx  += srl_dtx;
		                srl_ty  += srl_dty;

		            }// end of line rendering loop
		            //***********************************************
		            //****                                       ****
		            //****      END OF CODE TO RENDER A LINE     ****
		            //****                                       ****
		            //***********************************************

		            //increment local st_* and fin_* values by passed in parameters

		            // increment local line start point (st_*) using passed in parameters
		            st_x    += st_dx;
		            st_z    += st_dz;
		            st_aux1   += st_daux1;
		            st_aux2   += st_daux2;

		            // increment local line finish point (fin_*) using passed in parameters
		            fin_x    += fin_dx;
		            fin_z    += fin_dz;
		            fin_aux1   += fin_daux1;
		            fin_aux2   += fin_daux2;

		            // move to next line
		            y_counter++;
		    }//end of y_counter while loop
		}
		
		
		public boolean isTextured(){return true;}
		
		
}
			

