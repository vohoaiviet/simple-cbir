package cbir.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import cbir.Utils;
import cbir.image.Descriptor;
import cbir.image.DescriptorType;
import cbir.image.Image;

public class FireReader {
	
	/**
	 * reads all the corresponding fire descriptors from a specific type into the according image datastructure
	 * note: the descriptors have to be located in the same directory as the images
	 * @param images
	 * @param type
	 * @throws IOException
	 */
	public void readDescriptors(List<Image> images,DescriptorType type) throws IOException{
		String suffix = Utils.getSuffix(type);
		for(Image curr: images)
			curr.addDescriptor(readDescriptorFile(curr.getFilename()+suffix,type));
	}
	
	/**
	 * reads a descriptorfile
	 * @param file
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public Descriptor readDescriptorFile(String file, DescriptorType type) throws IOException{
		List<Double> values = null;
		double maxValue = 1;
		boolean readMaxValue = false;
		
		InputStream fileStream = new FileInputStream(file);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader reader = new BufferedReader(decoder);
		String line;
		/*parse color histo*/
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split("\\s+");
			for(String token : tokens){
				if(token.equals("counter")){
					readMaxValue = true;
					continue;
				}
				if(token.equals("data")){
					values = new LinkedList<Double>();
					continue;
				}
				if(readMaxValue){
					readMaxValue = false;
					maxValue = Double.parseDouble(token);
				}
				if(values != null)
					values.add(Double.parseDouble(token));
			}
		}
		reader.close();
		
		return new Descriptor(type,Utils.doubleFromDouble(values),maxValue);
	}
}
