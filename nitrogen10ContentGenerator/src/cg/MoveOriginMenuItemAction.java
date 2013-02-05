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

import modified_nitrogen1.ImmutableBackside;
import modified_nitrogen1.ImmutableVertex;

public class MoveOriginMenuItemAction implements ActionListener {
	
	ContentGenerator cg;
	
	MoveOriginMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ContentGenerator cgL = cg;
		WorkingVertexModel wvm = cg.workingVertexModel;
		float x = wvm.x;
		float y = wvm.y;
		float z = wvm.z;
			
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		
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
		
		// Move CollisionVertexes
		List<ImmutableVertex> ivclin = cgSISI.collisionVertexList;
		List<ImmutableVertex> ivclout = new ArrayList<ImmutableVertex>();
		Iterator<ImmutableVertex> ivcin_it = ivclin.iterator();
		while(ivcin_it.hasNext())
		{
			ImmutableVertex ivc_element = ivcin_it.next();
			ImmutableVertex ivc_element_out = new ImmutableVertex(
					ivc_element.is_x - x,
					ivc_element.is_y - y,
					ivc_element.is_z - z
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
		
		Iterator<ImmutableVertex> ivout_it = ivclout.iterator();
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
		cg.cgc.updateGeneratedItemAndEditArea();
	}
}
