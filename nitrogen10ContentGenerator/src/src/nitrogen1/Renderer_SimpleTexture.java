/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nitrogen1;

import java.io.Serializable;

/**
 *
 * @author andrew
 */
final public class Renderer_SimpleTexture implements Renderer,Serializable{

private static final long serialVersionUID = -9205815146173706598L;

static final int SH = 20;
static final int NUM = 1 << SH;
/*
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
        NitrogenContext context
        ){
		if(tex == null){System.out.println("null texture!");}
		else{System.out.println("has texture!");}
}
*/
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
        NitrogenContext context
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

public boolean usesHLPBreak(){return true;}

}// end of class
