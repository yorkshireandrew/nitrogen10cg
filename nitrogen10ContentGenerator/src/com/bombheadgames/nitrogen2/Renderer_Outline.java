package com.bombheadgames.nitrogen2;

public class Renderer_Outline implements Renderer {
	static final int SH = 20;
	static final int NUM = 1 << SH;
	static final int ALPHA = -16777216; // 0xFF000000
	static final int COLOUR = 0xFF0000; // red
	static final boolean usesHLPBreak = false;
	
	
	@Override
	public void renderTrapezoid(int st_x, long st_z, int st_aux1, int st_aux2,
			int st_aux3, int st_dx, long st_dz, int st_daux1, int st_daux2,
			int st_daux3, int fin_x, long fin_z, int fin_aux1, int fin_aux2,
			int fin_aux3, int fin_dx, long fin_dz, int fin_daux1,
			int fin_daux2, int fin_daux3, int y_counter, int y_max, int[] p,
			int[] z, int[] tex, int pixelBufferWidth, int textureBufferWidth,
			int[] polyData, float lightingValue, NitrogenContext context, boolean hlp,
            Vertex a,
            Vertex b,
            Vertex c,
            Vertex d) {


        // local variables used to step along a line

        // line rendering fields
        int srl_st_x;
        int srl_fin_x;
        long srl_z;

        // line rendering fields used for stepping
        int srl_dx;
        long srl_dz;

        int linestart;      // value that is constant for a line - gets precalculated

        // fields used for pixel rendering
        int srl_z2;         // integer version of srl_z
        int index;          // index into colour and z buffer arrays

        long temp;          // used to do a faster divide
        int rec;
        
        // colour
        int colour = ALPHA | COLOUR;
        
        if(y_counter == y_max)
        {
            //***********************************************
            //****                                       ****
            //****    START OF CODE TO RENDER A LINE     ****
            //****                                       ****
            //***********************************************

            linestart = y_counter * pixelBufferWidth;         // pre-calculate this constant value

            // increment local line start point (st_*) using passed in parameters
            int ptA = st_x >> SH;
            
            st_x    += st_dx; 
            
            int ptB = st_x >> SH;
            
            if(ptB > ptA)
            {
            	for(int x = ptA; x <= ptB; x++)
            	{
            		p[linestart + x] = colour;
            	}
            }
            else
            {
            	for(int x = ptB; x <= ptA; x++)
            	{
            		p[linestart + x] = colour;
            	}           	
            }

            // increment local line finish point (fin_*) using passed in parameters
            int ptC = fin_x >> SH;
            fin_x    += fin_dx;
            int ptD = fin_x >> SH;
            fin_z    += fin_dz;
            fin_aux1   += fin_daux1;
            fin_aux2   += fin_daux2;
            
            if(ptC < ptD)
            {
            	for(int x = ptC; x <= ptD; x++)
            	{
            		p[linestart + x] = colour;
            	}
            }
            else
            {
            	for(int x = ptD; x <= ptC; x++)
            	{
            		p[linestart + x] = colour;
            	}           	
            }
            return;
        }
        
        
        
        
        
        while(y_counter < y_max)
        {

            //***********************************************
            //****                                       ****
            //****    START OF CODE TO RENDER A LINE     ****
            //****                                       ****
            //***********************************************

            linestart = y_counter * pixelBufferWidth;         // pre-calculate this constant value

            // increment local line start point (st_*) using passed in parameters
            int ptA = st_x >> SH;
            
            st_x    += st_dx; 
            
            int ptB = st_x >> SH;
            
            if(ptB > ptA)
            {
            	for(int x = ptA; x <= ptB; x++)
            	{
            		p[linestart + x] = colour;
            	}
            }
            else
            {
            	for(int x = ptB; x <= ptA; x++)
            	{
            		p[linestart + x] = colour;
            	}           	
            }

            // increment local line finish point (fin_*) using passed in parameters
            int ptC = fin_x >> SH;
            fin_x    += fin_dx;
            int ptD = fin_x >> SH;
            fin_z    += fin_dz;
            fin_aux1   += fin_daux1;
            fin_aux2   += fin_daux2;
            
            if(ptC < ptD)
            {
            	for(int x = ptC; x <= ptD; x++)
            	{
            		p[linestart + x] = colour;
            	}
            }
            else
            {
            	for(int x = ptD; x <= ptC; x++)
            	{
            		p[linestart + x] = colour;
            	}           	
            }
            
            

            // move to next line
            y_counter++;
        }//end of y_counter while loop

	}
	
    public void renderTrapezoidHLP(
    		final NitrogenContext context,
    		final Vertex a, final Vertex b, final Vertex c, final Vertex d, 
    		final int[] polyData, 
    		final TexMap texMap,
    		final float lightingValue
    	     ){}
	
	public boolean isTextured(){return false;}
}
