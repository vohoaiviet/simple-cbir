package cbir.reader;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;

import cbir.image.DescriptorType;
import cbir.image.ImageContainer;

/**
 * A simple testing class for XML reader. "file" specifies the path of the XML
 * file (containing only MPEG-EHD & CEDD) generated by img(rummager). For
 * testing purposes.
 * 
 * @author Matej Stanic
 * 
 */
public class XMLReaderTest {

	public static void main(String[] args) {
		// Parse given XML file
		XMLReader reader = new XMLReader();
		File file = new File("C:\\MBOX\\signed_all\\corel_ehd_cedd.xml");
		try {
			reader.parseXMLFile(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		// get list of parsed images
		List<ImageContainer> imageList = reader.getImageList();
		// print list
		System.out.println("test");
		Iterator<ImageContainer> it = imageList.iterator();
		while (it.hasNext()) {
			ImageContainer temp = it.next();
			System.out.println("Image: " + temp.getFilename());
			double[] edgeHisto = temp.getDescriptor(DescriptorType.MPEG_EHD)
					.getValues();
			double[] cedd = temp.getDescriptor(DescriptorType.CEDD).getValues();
			System.out.print("EHD: ");
			for (int i = 0; i < edgeHisto.length; i++)
				System.out.print(edgeHisto[i] + " ");
			System.out.println();
			System.out.print("CEDD: ");
			for (int i = 0; i < cedd.length; i++)
				System.out.print(cedd[i] + " ");
			System.out.println();
		}
		System.out.println("total: " + imageList.size());
	}
}
