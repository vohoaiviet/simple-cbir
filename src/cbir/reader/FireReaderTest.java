package cbir.reader;

import java.io.IOException;

import cbir.image.Descriptor;
import cbir.image.DescriptorType;
/**
 * To test the reader you have to specify the location of one descriptor.
 * 
 * @author Chris Wendler
 *
 */
public class FireReaderTest {
	public static void main(String [] args){
		FireReader reader = new FireReader();
		Descriptor test = null;
		try {
			test = reader.readDescriptorFile("D:/MBOX/signed_all/Lentos_signed/0_000_000_001.jpg.color.histo.gz", DescriptorType.COLOR_HISTO);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(test);
	}
}
