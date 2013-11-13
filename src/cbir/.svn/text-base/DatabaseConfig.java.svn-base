package cbir;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dom4j.DocumentException;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.reader.FireReader;
import cbir.reader.XMLReader;

public class DatabaseConfig {
	private String indexFile;
	private List<String> imageNames;
	private List<Image> queries;
	private List<Image> database;
	
	public DatabaseConfig(){
		super();
	}
	
	public DatabaseConfig(String indexFile, List<String> imageNames) {
		super();
		this.indexFile = indexFile;
		this.imageNames = imageNames;
	}
	
	public void initialize(String indexFile, List<String> queries) throws DocumentException, IOException{
		this.indexFile = indexFile;
		database = new XMLReader().parseXMLFile(new File(indexFile));
		new FireReader().readDescriptors(database,
				DescriptorType.COLOR_HISTO);
		
		
		
	}
	
	
	
}
