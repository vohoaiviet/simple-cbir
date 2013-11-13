package ind.kdtree;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dom4j.DocumentException;

import cbir.image.DescriptorType;
import cbir.image.Image;
import cbir.reader.FireReader;
import cbir.reader.XMLReader;

/**
 * A class for testing and debugging the k-d-tree. The path to the descriptor
 * XML file has to be specified in "xml_path". For testing purposes.
 * 
 * @author Matej Stanic
 * 
 */
public class KDTreeTest {

	public static void main(String args[]) {
		try {
			File xml_path = new File(
					"C:\\Users\\Stanic\\Desktop\\lentos_color_ehd_2.xml");

			List<Image> database = new XMLReader().parseXMLFile(xml_path);

			new FireReader().readDescriptors(database,
					DescriptorType.COLOR_HISTO);

			List<Image> list = database.subList(0, 10);
			KDTree tree = new KDTree(list, list.get(0)
					.getDescriptor(DescriptorType.MERGED).getValues().length,
					DescriptorType.MERGED);
			// System.out.println(tree.getRoot().getImage().getFilename());
			System.out.println("Initial tree, 10 nodes");
			System.out.print(TreePrinter.getString(tree));

			System.out.println("add 11");
			tree.add(database.get(10));
			System.out.print(TreePrinter.getString(tree));

			System.out.println("remove 9");
			tree.remove(database.get(8));
			System.out.print(TreePrinter.getString(tree));

			System.out
					.println("contains 1?: " + tree.contains(database.get(0)));
			System.out.println("contains 19?: "
					+ tree.contains(database.get(18)));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
