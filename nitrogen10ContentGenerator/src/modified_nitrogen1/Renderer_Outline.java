package modified_nitrogen1;

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
			int[] polyData, float lightingValue, NitrogenContext context) {


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
        int colour = ALPHA & COLOUR;
        
        while(y_counter < y_max)
        {

            //***********************************************
            //****                                       ****
            //****    START OF CODE TO RENDER A LINE     ****
            //****                                       ****
            //***********************************************

            linestart = y_counter * pixelBufferWidth;         // pre-calculate this constant value

            // increment local line start point (st_*) using passed in parameters
            int ptA = st_x;
            
            st_x    += st_dx;  
            st_z    += st_dz;
            st_aux1   += st_daux1;
            st_aux2   += st_daux2;
            
            if(st_dx > 0)
            {
            	for(int x = ptA; x <= st_x; x++)
            	{
            		p[linestart + x] = colour;
            	}
            }
            else
            {
            	for(int x = st_x; x <= ptA; x++)
            	{
            		p[linestart + x] = colour;
            	}           	
            }

            // increment local line finish point (fin_*) using passed in parameters
            int ptB = fin_x;
            fin_x    += fin_dx;
            fin_z    += fin_dz;
            fin_aux1   += fin_daux1;
            fin_aux2   += fin_daux2;
            
            if(fin_dx > 0)
            {
            	for(int x = ptB; x <= fin_x; x++)
            	{
            		p[linestart + x] = colour;
            	}
            }
            else
            {
            	for(int x = fin_x; x <= ptB; x++)
            	{
            		p[linestart + x] = colour;
            	}           	
            }
            
            

            // move to next line
            y_counter++;
    }//end of y_counter while loop

	}

	@Override
	public boolean usesHLPBreak() {
		// TODO Auto-generated method stub
		return false;
	}

}
