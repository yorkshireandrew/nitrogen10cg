package cg;

import java.io.File;

public class TemplateModel {

	File templateFile;
	int leftRightOffset;
	int downUpOffset;
	boolean over;	// set true if the template is shown over the content
	double intensity;
	double oppositeSideIntensity;
	double scale;
	TemplateModel oppositeSideTemplateModel;
	
	TemplateModel(ContentGenerator cg)
	{
		// TO DO
	}
}
