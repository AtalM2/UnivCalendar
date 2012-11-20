package calendar;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class ICSFinder {

	public static ArrayList<String> getLocal(String path) throws FileNotFoundException {
		ArrayList<String> ret = new ArrayList<>();
		Scanner scanner = new Scanner(new FileReader(path));
		while (scanner.hasNextLine()) {
			ret.add(scanner.nextLine());
		}
		return ret;
	}

	public static ArrayList<String> getURL(String path) throws MalformedURLException, IOException {
		ArrayList<String> ret = new ArrayList<>();
		URL u;
		InputStream is;
		DataInputStream dis;
		String line;
		
		u = new URL(path);
		is = u.openStream();
		dis = new DataInputStream(is);
		
		line = dis.readUTF();
		while (line != null) {
			ret.add(line);
			line = dis.readUTF();
		}
		
		return ret;
	}
}
