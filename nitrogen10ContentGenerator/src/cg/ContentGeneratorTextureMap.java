package cg;

import java.io.Serializable;

import com.bombheadgames.nitrogen2.TexMap;

public class ContentGeneratorTextureMap implements Serializable{
	private static final long serialVersionUID = 4849787247414774169L;
	String path;
	String file;
	transient TexMap textureMap; // loaded by ContentGeneratorSISI
	
	ContentGeneratorTextureMap(){}
	
	ContentGeneratorTextureMap(String path, String file, TexMap textureMap){
		this.path = path;
		this.file = file;
		this.textureMap = textureMap;
	}
	
	
}
