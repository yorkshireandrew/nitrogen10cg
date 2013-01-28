package cg;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import modified_nitrogen1.ImmutableBackside;
import modified_nitrogen1.NitrogenContext;
import modified_nitrogen1.PolygonVertexData;
import modified_nitrogen1.RendererHelper;

public class PolygonDialog extends JDialog implements ActionListener{

	ContentGenerator cg;
	PolygonDialogModel pdm;
	
	JComboBox 	polygonNameComboBox;
	JLabel 		polygonNameLabel = new JLabel("Name ");
	JButton		fillNameButton;
	
	// polygon data UI
	JComboBox 	polygonDataComboBox;
	JLabel 		polygonDataLabel = new JLabel("Polygon Data ");
	
	// polygon vertex data UI
	JComboBox 	polygonVertexDataComboBox1;
	JLabel 		polygonVertexLabel1 = new JLabel("Vertex1 Data ");	
	JComboBox 	polygonVertexDataComboBox2;
	JLabel 		polygonVertexLabel2 = new JLabel("Vertex2 Data ");	
	JComboBox 	polygonVertexDataComboBox3;
	JLabel 		polygonVertexLabel3 = new JLabel("Vertex3 Data ");	
	JComboBox 	polygonVertexDataComboBox4;
	JLabel 		polygonVertexLabel4 = new JLabel("Vertex4 Data ");
	
	// polygon renderer UI
	JComboBox 	polygonRendererComboBox;
	JLabel 		polygonRendererLabel = new JLabel("Polygon Renderer ");
	
	// polygon backside UI
	JCheckBox	isBacksideCulledCheckBox;
	JComboBox 	polygonBacksideComboBox;
	JLabel 		polygonBacksideLabel = new JLabel("Backside ");	
	
	// polygon transparent UI
	JCheckBox	isTransparentCheckBox;
	
	JButton 	cancelButton;
	JButton 	okButton;
		
	PolygonDialog(ContentGenerator cg)
	{
		this.cg = cg;
		this.pdm = cg.polygonDialogModel;
		
		polygonNameComboBox = new JComboBox(getPolygonNames());
		polygonNameComboBox.setEditable(true);
		polygonNameComboBox.getEditor().setItem("");	
		polygonNameComboBox.addActionListener(this); // listen so we can edit existing polygons
		polygonNameComboBox.setMaximumSize(polygonNameComboBox.getPreferredSize());

		polygonDataComboBox = new JComboBox(getPolygonDataNames());
		polygonDataComboBox.setEditable(true);
		polygonDataComboBox.getEditor().setItem(getPolygonDataInitial());	
		polygonDataComboBox.setMaximumSize(polygonDataComboBox.getPreferredSize());
		
		polygonVertexDataComboBox1 = new JComboBox(getPolygonVertexDataNames());
		polygonVertexDataComboBox1.setEditable(true);
		polygonVertexDataComboBox1.getEditor().setItem(getPolygonVertexDataInitial1());
		polygonVertexDataComboBox1.setMaximumSize(polygonVertexDataComboBox1.getPreferredSize());

		polygonVertexDataComboBox2 = new JComboBox(getPolygonVertexDataNames());
		polygonVertexDataComboBox2.setEditable(true);
		polygonVertexDataComboBox2.getEditor().setItem(getPolygonVertexDataInitial2());
		polygonVertexDataComboBox2.setMaximumSize(polygonVertexDataComboBox2.getPreferredSize());

		polygonVertexDataComboBox3 = new JComboBox(getPolygonVertexDataNames());
		polygonVertexDataComboBox3.setEditable(true);
		polygonVertexDataComboBox3.getEditor().setItem(getPolygonVertexDataInitial3());
		polygonVertexDataComboBox3.setMaximumSize(polygonVertexDataComboBox3.getPreferredSize());

		polygonVertexDataComboBox4 = new JComboBox(getPolygonVertexDataNames());
		polygonVertexDataComboBox4.setEditable(true);
		polygonVertexDataComboBox4.getEditor().setItem(getPolygonVertexDataInitial4());
		polygonVertexDataComboBox4.setMaximumSize(polygonVertexDataComboBox4.getPreferredSize());
		
		polygonRendererComboBox = new JComboBox(getPolygonRendererTripletNames());
		polygonRendererComboBox.setEditable(true);
		polygonRendererComboBox.getEditor().setItem(getPolygonRendererTripletInitial());
		polygonRendererComboBox.setMaximumSize(polygonRendererComboBox.getPreferredSize());

		isBacksideCulledCheckBox = new JCheckBox("Backside culling ");
		isBacksideCulledCheckBox.setSelected(cg.polygonDialogModel.isBacksideCulled);
		
		polygonBacksideComboBox = new JComboBox(getPolygonImmutableBacksideNames());
		polygonBacksideComboBox.setEditable(true);
		polygonBacksideComboBox.getEditor().setItem(getPolygonImmutableBacksideInitial());
		polygonBacksideComboBox.setMaximumSize(polygonBacksideComboBox.getPreferredSize());

		isTransparentCheckBox = new JCheckBox("Is transparent ");
		isTransparentCheckBox.setSelected(cg.polygonDialogModel.isTransparent);
		
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						PolygonDialog.this.setVisible(false);
						PolygonDialog.this.dispose();			
					}			
				});	
		
		okButton = new JButton("OK");
		
		okButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						PolygonDialog.this.handleOK();
						PolygonDialog.this.setVisible(false);
						PolygonDialog.this.dispose();	
					}			
				});	
		
		this.setSize(450,350);
		this.setModal(true);
		this.generateContent2();
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	void handleOK()
	{
		// TO DO
		String name = (String)polygonNameComboBox.getEditor().getItem();
		cg.contentGeneratorSISI.contentGeneratorPolygonMap.put(name, null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void generateContent()
	{
		
		Box nameBox = new Box(BoxLayout.X_AXIS);
		nameBox.add(polygonNameLabel);
		nameBox.add(polygonNameComboBox);
		nameBox.add(Box.createHorizontalGlue());
		
		Box polygonDataBox = new Box(BoxLayout.X_AXIS);
		polygonDataBox.add(polygonDataLabel);
		polygonDataBox.add(polygonDataComboBox);
		polygonDataBox.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox1 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox1.add(polygonVertexLabel1);
		polygonVertexDataBox1.add(polygonVertexDataComboBox1);
		polygonVertexDataBox1.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox2 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox2.add(polygonVertexLabel2);
		polygonVertexDataBox2.add(polygonVertexDataComboBox2);
		polygonVertexDataBox2.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox3 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox3.add(polygonVertexLabel3);
		polygonVertexDataBox3.add(polygonVertexDataComboBox3);
		polygonVertexDataBox3.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox4 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox4.add(polygonVertexLabel4);
		polygonVertexDataBox4.add(polygonVertexDataComboBox4);
		polygonVertexDataBox4.add(Box.createHorizontalGlue());
		
		Box polygonRendererBox = new Box(BoxLayout.X_AXIS);
		polygonRendererBox.add(polygonRendererLabel);
		polygonRendererBox.add(polygonRendererComboBox);
		polygonRendererBox.add(Box.createHorizontalGlue());
		
		Box polygonBacksideBox = new Box(BoxLayout.X_AXIS);
		polygonBacksideBox.add(polygonBacksideLabel);
		polygonBacksideBox.add(polygonBacksideComboBox);
		polygonBacksideBox.add(isBacksideCulledCheckBox);
		polygonBacksideBox.add(Box.createHorizontalGlue());
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataOuterBox = new Box(BoxLayout.Y_AXIS);
		polygonVertexDataOuterBox.add(polygonVertexDataBox1);
		polygonVertexDataOuterBox.add(polygonVertexDataBox2);
		polygonVertexDataOuterBox.add(polygonVertexDataBox3);
		polygonVertexDataOuterBox.add(polygonVertexDataBox4);
		polygonVertexDataOuterBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		Box dialog = new Box(BoxLayout.Y_AXIS);
		dialog.add(nameBox);
		dialog.add(polygonDataBox);
		dialog.add(polygonVertexDataOuterBox);
		dialog.add(polygonRendererBox);
		dialog.add(polygonBacksideBox);
		dialog.add(isTransparentCheckBox);
		dialog.add(buttonBox);
		this.getContentPane().removeAll();
		this.getContentPane().add(dialog);
	}
	
	private void generateContent2()
	{			
		Box polygonNameBox = new Box(BoxLayout.X_AXIS);
		polygonNameBox.add(polygonNameComboBox);
		polygonNameBox.add(Box.createHorizontalGlue());
		
		Box polygonDataBox = new Box(BoxLayout.X_AXIS);
		polygonDataBox.add(polygonDataComboBox);
		polygonDataBox.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox1 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox1.add(polygonVertexDataComboBox1);
		polygonVertexDataBox1.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox2 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox2.add(polygonVertexDataComboBox2);
		polygonVertexDataBox2.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox3 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox3.add(polygonVertexDataComboBox3);
		polygonVertexDataBox3.add(Box.createHorizontalGlue());
		
		Box polygonVertexDataBox4 = new Box(BoxLayout.X_AXIS);
		polygonVertexDataBox4.add(polygonVertexDataComboBox4);
		polygonVertexDataBox4.add(Box.createHorizontalGlue());
		
		Box polygonRendererBox = new Box(BoxLayout.X_AXIS);
		polygonRendererBox.add(polygonRendererComboBox);
		polygonRendererBox.add(Box.createHorizontalGlue());
		
		Box polygonBacksideBox = new Box(BoxLayout.X_AXIS);
		polygonBacksideBox.add(polygonBacksideComboBox);
		polygonBacksideBox.add(Box.createHorizontalGlue());
		
		Box isTransparentBox = new Box(BoxLayout.X_AXIS);
		isTransparentBox.add(isTransparentCheckBox);
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createHorizontalGlue());
		
		fatten(polygonNameLabel);
		fatten(polygonDataLabel);
		fatten(polygonVertexLabel1);
		fatten(polygonVertexLabel2);
		fatten(polygonVertexLabel3);
		fatten(polygonVertexLabel4);
		fatten(polygonRendererLabel);
		fatten(polygonBacksideLabel);
		
		GridLayout gridLayout = new GridLayout(10,3);
		setLayout(gridLayout);
		
		add(polygonNameLabel);
		add(polygonNameBox);
		add(new JLabel(""));
		
		add(polygonDataLabel);
		add(polygonDataBox);
		add(new JLabel(""));
		
		add(polygonVertexLabel1);
		add(polygonVertexDataBox1);
		add(new JLabel(""));
		add(polygonVertexLabel2);
		add(polygonVertexDataBox2);
		add(new JLabel(""));
		add(polygonVertexLabel3);
		add(polygonVertexDataBox3);
		add(new JLabel(""));
		add(polygonVertexLabel4);
		add(polygonVertexDataBox4);
		add(new JLabel(""));
		
		add(polygonRendererLabel);
		add(polygonRendererBox);
		add(new JLabel(""));
		
		add(polygonBacksideLabel);
		add(polygonBacksideBox);
		add(isBacksideCulledCheckBox);
		
		add(isTransparentBox);
		add(new JLabel(""));
		add(new JLabel(""));
		
		add(buttonBox);
		add(new JLabel(""));
		add(new JLabel(""));
	}
	
	private String[] getPolygonNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorPolygon> polygonMap = cgsisi.contentGeneratorPolygonMap;
		Set<String> names = polygonMap.keySet();
		String[] nameArray = names.toArray(new String[0]);;
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private String[] getPolygonDataNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,int[]> polygonDataMap = cgsisi.polygonDataMap;
		Set<String> names = polygonDataMap.keySet();
		String[] nameArray = names.toArray(new String[0]);;
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private String getPolygonDataInitial()
	{
		String retval = cg.polygonDialogModel.polyData_name;
		if(retval == null)return("");
		return retval;
	}
	
	private String[] getPolygonVertexDataNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,PolygonVertexData> polygonDataMap = cgsisi.polygonVertexDataMap;
		Set<String> names = polygonDataMap.keySet();
		String[] nameArray = names.toArray(new String[0]);;
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private String getPolygonVertexDataInitial1()
	{
		String retval = cg.polygonDialogModel.pvd_c1_name;
		if(retval == null)return("");
		return retval;
	}
	private String getPolygonVertexDataInitial2()
	{
		String retval = cg.polygonDialogModel.pvd_c2_name;
		if(retval == null)return("");
		return retval;
	}
	private String getPolygonVertexDataInitial3()
	{
		String retval = cg.polygonDialogModel.pvd_c3_name;
		if(retval == null)return("");
		return retval;
	}
	private String getPolygonVertexDataInitial4()
	{
		String retval = cg.polygonDialogModel.pvd_c4_name;
		if(retval == null)return("");
		return retval;
	}
	
	private String[] getPolygonRendererTripletNames()
	{
		return RendererHelper.getRendererTripletNames();
	}
	
	private String getPolygonRendererTripletInitial()
	{
		String retval = cg.polygonDialogModel.rendererTriplet_name;
		if(retval == null)return("");
		return retval;
	}
	
	private String[] getPolygonImmutableBacksideNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ImmutableBackside> polygonDataMap = cgsisi.immutableBacksideMap;
		Set<String> names = polygonDataMap.keySet();
		String[] nameArray = names.toArray(new String[0]);
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private String getPolygonImmutableBacksideInitial()
	{
		String retval = cg.polygonDialogModel.backside_name;
		if(retval == null)return("");
		return retval;
	}
	
	private void fatten(JLabel label)
	{
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	

}
