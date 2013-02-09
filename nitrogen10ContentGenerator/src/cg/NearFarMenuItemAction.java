package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.bombheadgames.nitrogen1.ImmutableBackside;
import com.bombheadgames.nitrogen1.ImmutableVertex;


public class NearFarMenuItemAction implements ActionListener {

	ContentGenerator cg;
	
	NearFarMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		NearFarDialog nfd = new NearFarDialog(cg);
		nfd.setVisible(true);
	}

}