package com.bombheadgames.nitrogen1;

public class Renderer_Null implements Renderer {

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
            Vertex d) 
	{
		// render nothing
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
