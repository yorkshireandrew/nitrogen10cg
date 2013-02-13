package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bombheadgames.nitrogen2.ImmutableBackside;
import com.bombheadgames.nitrogen2.ImmutableCollisionVertex;
import com.bombheadgames.nitrogen2.ImmutableVertex;


public class MoveOriginMenuItemAction implements ActionListener {
	
	ContentGenerator cg;
	
	MoveOriginMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ContentGenerator cgL = cg;
		
		cg.cgc.saveSISI();
		
		WorkingVertexModel wvm = cgL.workingVertexModel;
		float x = wvm.x;
		float y = wvm.y;
		float z = wvm.z;
			
		ContentGeneratorSISI cgSISI = cgL.contentGeneratorSISI;
		
		// Move Vertexes
		List<ImmutableVertex> ivlin = cgSISI.immutableVertexList;
		List<ImmutableVertex> ivlout = new ArrayList<ImmutableVertex>();
		Iterator<ImmutableVertex> ivin_it = ivlin.iterator();
		while(ivin_it.hasNext())
		{
			ImmutableVertex iv_element = ivin_it.next();
			ImmutableVertex iv_element_out = new ImmutableVertex(
					iv_element.is_x - x,
					iv_element.is_y - y,
					iv_element.is_z - z
					);
			ivlout.add(iv_element_out);
		}	
		updateImmutableVertexReferences(ivlin,ivlout);
		
		// Move CollisionVertexes
		List<ImmutableCollisionVertex> ivclin = cgSISI.collisionVertexList;
		List<ImmutableCollisionVertex> ivclout = new ArrayList<ImmutableCollisionVertex>();
		Iterator<ImmutableCollisionVertex> ivcin_it = ivclin.iterator();
		while(ivcin_it.hasNext())
		{
			ImmutableCollisionVertex ivc_element = ivcin_it.next();
			ImmutableCollisionVertex ivc_element_out = new ImmutableCollisionVertex(
					ivc_element.is_x - x,
					ivc_element.is_y - y,
					ivc_element.is_z - z,
					ivc_element.radius
					);
			ivclout.add(ivc_element_out);
		}
		
		// Move backsides
		Map<String, ImmutableBackside> ibm_in = cgSISI.immutableBacksideMap;
		Map<String, ImmutableBackside> ibm_out = new HashMap<String, ImmutableBackside>();
		
		Set<Entry<String, ImmutableBackside>> ibms_in = ibm_in.entrySet();
		Iterator<Entry<String, ImmutableBackside>> ibms_in_it = ibms_in.iterator();
		
		while(ibms_in_it.hasNext())
		{
			Entry<String, ImmutableBackside> in = ibms_in_it.next();
			ImmutableBackside backsideIn = in.getValue();
			
			ImmutableBackside backsideOut = new ImmutableBackside(
					backsideIn.ix - x,
					backsideIn.iy - y,
					backsideIn.iz - z,
					backsideIn.inx,
					backsideIn.iny,
					backsideIn.inz,
					backsideIn.calculateLighting
					);
			
			ibm_out.put(in.getKey(), backsideOut);	
		}
		
		// calculate new bounding radius
		float boundingRadius = 0;
		
		Iterator<ImmutableVertex> ivout_it = ivlout.iterator();
		ImmutableVertex origin = new ImmutableVertex(0,0,0);
		
		while(ivout_it.hasNext())
		{
			ImmutableVertex element = ivout_it.next();
			float dist = PolygonDialog.distBetween(element,origin);
			if (dist > boundingRadius)boundingRadius = dist;
		}
		
		// write back to cgSISI		
		cgSISI.immutableVertexList = ivlout;
		cgSISI.collisionVertexList = ivclout;
		cgSISI.immutableBacksideMap = ibm_out;
		cgSISI.boundingRadius = boundingRadius;
		
		// update the display
		cgL.workingVertexModel.pickedVertex = null;
		cgL.cgc.updateGeneratedItemAndEditArea();
	}
	
	void updateImmutableVertexReferences(List<ImmutableVertex> oldVertexes, List<ImmutableVertex> newVertexes)
	{
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorPolygon> cgcgp_in = cgSISI.contentGeneratorPolygonMap;
		Map<String,ContentGeneratorPolygon> newPolygons = new HashMap<String,ContentGeneratorPolygon>();
		
		Set<Entry<String,ContentGeneratorPolygon>> s = cgcgp_in.entrySet();
		Iterator<Entry<String,ContentGeneratorPolygon>> cgcgp_in_it = s.iterator();
		
		while(cgcgp_in_it.hasNext())
		{
			Entry<String,ContentGeneratorPolygon> element = cgcgp_in_it.next();
			ContentGeneratorPolygon cgp_in = element.getValue();
			
			int c1_index = oldVertexes.indexOf(cgp_in.c1);
			int c2_index = oldVertexes.indexOf(cgp_in.c2);
			int c3_index = oldVertexes.indexOf(cgp_in.c3);
			int c4_index = oldVertexes.indexOf(cgp_in.c4);
			
			ContentGeneratorPolygon cgp_out = new ContentGeneratorPolygon(cgp_in);
			
			cgp_out.c1 = newVertexes.get(c1_index);
			cgp_out.c2 = newVertexes.get(c2_index);
			cgp_out.c3 = newVertexes.get(c3_index);
			cgp_out.c4 = newVertexes.get(c4_index);
			
			newPolygons.put(element.getKey(), cgp_out);		
		}
		
		cgSISI.contentGeneratorPolygonMap = newPolygons;
	}
}
