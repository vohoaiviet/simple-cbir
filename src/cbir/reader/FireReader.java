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
/**
 * A very simple reader that is able to read histograms of the given format:
 * + (optional) The histograms can contain a delimiter "counter" followed by one double value, if the "counter"
 *   delimiter is used it has to be located in front of the data part. 
 * * The histogram files have to be compressed using gzip.
 * * The histograms have to contain the delimiter "data" followed by an arbitrary amount of
 *   double values.
 * * The descriptors have to be located in the image directory.
 * @author Chris Wendler
 */

public class FireReader {
	
	/**
	 * Reads all the corresponding descriptors from a specific type into the according image data-structure.
	 * note: the descriptors have to be located in the same directory as the images.
	 * @param images a list of images that can be generated using the XMLReader.
	 * @param type the type determines where the descriptors get stored in the image data-structure.
	 * @throws IOException when a descriptor file is not found.
	 */
	public void readDescriptors(List<Image> images,DescriptorType type) throws IOException{
		String suffix = Utils.getSuffix(type);
		for(Image curr: images)
			curr.addDescriptor(readDescriptorFile(curr.getFilename()+suffix,type));
	}
	
	/**
	 * Reads a single descriptor from a given file.
	 * @param file is the filename of the descriptor file.
	 * @param type the type determines where the descriptors get stored in the image data-structure.
	 * @throws IOException when a descriptor file is not found.
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
