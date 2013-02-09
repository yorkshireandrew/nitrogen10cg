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

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.bombheadgames.nitrogen1.ImmutableBackside;
import com.bombheadgames.nitrogen1.ImmutableVertex;


public class NearFarDialog extends JDialog implements ActionListener{
	ContentGenerator cg;
	
	JTextField normalDetailPolyStartTextField;
	JTextField improvedDetailPolyStartTextField;	
	JTextField normalDetailPolyFinishTextField;
	JTextField improvedDetailPolyFinishTextField;
	JButton okButton;
	JButton cancelButton;
	
	NearFarDialog(ContentGenerator cg)
	{
		super(cg);
		setTitle("Near Far Polygons");
		
		this.cg = cg;
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		
		JLabel normalDetailPolyStartLabel = new JLabel("Normal start polygon");
		normalDetailPolyStartTextField = new JTextField(10);
		normalDetailPolyStartTextField.setText(Integer.toString(cgSISI.normalDetailPolyStart));
		normalDetailPolyStartTextField.setEditable(true);
		normalDetailPolyStartTextField.setMaximumSize(normalDetailPolyStartTextField.getPreferredSize());

		JLabel normalDetailPolyFinishLabel = new JLabel("Normal finish polygon");		
		normalDetailPolyFinishTextField = new JTextField(10);
		normalDetailPolyFinishTextField.setText(Integer.toString(cgSISI.normalDetailPolyFinish));
		normalDetailPolyFinishTextField.setEditable(true);
		normalDetailPolyFinishTextField.setMaximumSize(normalDetailPolyFinishTextField.getPreferredSize());

		JLabel improvedDetailPolyStartLabel = new JLabel("Improved start polygon");		
		improvedDetailPolyStartTextField = new JTextField(10);
		improvedDetailPolyStartTextField.setText(Integer.toString(cgSISI.improvedDetailPolyStart));
		improvedDetailPolyStartTextField.setEditable(true);
		improvedDetailPolyStartTextField.setMaximumSize(improvedDetailPolyStartTextField.getPreferredSize());
	
		JLabel improvedDetailPolyFinishLabel = new JLabel("Improved finish polygon");				
		improvedDetailPolyFinishTextField = new JTextField(10);
		improvedDetailPolyFinishTextField.setText(Integer.toString(cgSISI.improvedDetailPolyFinish));
		improvedDetailPolyFinishTextField.setEditable(true);
		improvedDetailPolyFinishTextField.setMaximumSize(improvedDetailPolyFinishTextField.getPreferredSize());
		
		
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(this);
		Box controls = Box.createHorizontalBox();
		controls.add(okButton);
		controls.add(cancelButton);
		
		Box dialog = Box.createVerticalBox();

		dialog.add(normalDetailPolyStartLabel);
		dialog.add(normalDetailPolyStartTextField);
		
		dialog.add(normalDetailPolyFinishLabel);
		dialog.add(normalDetailPolyFinishTextField);
		
		dialog.add(improvedDetailPolyStartLabel);
		dialog.add(improvedDetailPolyStartTextField);
		
		dialog.add(improvedDetailPolyFinishLabel);
		dialog.add(improvedDetailPolyFinishTextField);
		dialog.add(controls);
		
		this.add(dialog);
		this.setSize(250,250);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cancelButton)
		{
			this.setVisible(false);
			this.dispose();	
		}
		
		if(e.getSource() == okButton)
		{
			handleOKButton();
		}
		
	}
	
	void handleOKButton()
	{
		int normStart, normFinish, impStart, impFinish;
		try{
			normStart 	= Integer.parseInt(normalDetailPolyStartTextField.getText());
			normFinish 	= Integer.parseInt(normalDetailPolyFinishTextField.getText());
			impStart 	= Integer.parseInt(improvedDetailPolyStartTextField.getText());
			impFinish 	= Integer.parseInt(improvedDetailPolyFinishTextField.getText());

		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(cg, "Please enter a positive integer", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// do a bit of validation
		if((normStart<0)||(normFinish<0)||(impStart<0)||(impFinish<0))
		{
			JOptionPane.showMessageDialog(cg, "Please enter POSITIVE integers", "Error",JOptionPane.ERROR_MESSAGE);		
			return;
		}
		
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		
		int size = cgSISI.contentGeneratorPolygonMap.size();
		
		if((normStart>size)||(normFinish>size)||(impStart>size)||(impFinish>size))
		{
			JOptionPane.showMessageDialog(cg, "Values must be in the range (0 to " + size +")", "Error",JOptionPane.ERROR_MESSAGE);		
			return;
		}
		
		if(normStart>normFinish)
		{
			JOptionPane.showMessageDialog(cg, "Normal start must be less than (or equal to) normal finish", "Error",JOptionPane.ERROR_MESSAGE);		
			return;
		}

		if(impStart>impFinish)
		{
			JOptionPane.showMessageDialog(cg, "Improved start must be less than (or equal to) improved finish", "Error",JOptionPane.ERROR_MESSAGE);		
			return;
		}
		
		cg.cgc.saveSISI();
		cgSISI.normalDetailPolyStart 	= normStart;
		cgSISI.normalDetailPolyFinish 	= normFinish;
		cgSISI.improvedDetailPolyStart 	= impStart;
		cgSISI.improvedDetailPolyFinish = impFinish;
		

		cg.cgc.updateGeneratedItemAndEditArea();
		
		this.setVisible(false);
		this.dispose();	
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
