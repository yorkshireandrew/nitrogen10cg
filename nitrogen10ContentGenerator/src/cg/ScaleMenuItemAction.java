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

import com.bombheadgames.nitrogen2.ImmutableBackside;
import com.bombheadgames.nitrogen2.ImmutableVertex;


public class ScaleMenuItemAction implements ActionListener {

	ContentGenerator cg;
	
	ScaleMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ScaleDialog sd = new ScaleDialog(cg);
		sd.setVisible(true);
	}

}

