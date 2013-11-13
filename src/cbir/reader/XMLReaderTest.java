package cbir.reader;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;

import cbir.image.Image;

/**
 * A simple testing class for XML reader.
 * 
 * @author Stanic Matej
 * 
 */
public class XMLReaderTest {

	public static void main(String[] args) {
		// Parse given XML file
		XMLReader reader = new XMLReader();
		File file = new File("C:/Users/Stanic/Desktop/lentos_color_ehd_2.xml");
		try {
			reader.parseXMLFile(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		// get list of parsed images
		List<Image> imageList = reader.getImageList();
		// print list
		System.out.println("test");
		Iterator<Image> it = imageList.iterator();
		while (it.hasNext()) {
			Image temp = it.next();
			System.out.println("Image: " + temp.getFilename());
			double[] edgeHisto = temp.getEdgeHisto().getValues();
			double[] colorHisto = temp.getColorHisto().getValues();
			System.out.print("EHD: ");
			for (int i = 0; i < edgeHisto.length; i++)
				System.out.print(edgeHisto[i] + " ");
			System.out.println();
			System.out.print("COLOR: ");
			for (int i = 0; i < colorHisto.length; i++)
				System.out.print(colorHisto[i] + " ");
			System.out.println();
		}
		System.out.println("total: " + imageList.size());
	}

}
