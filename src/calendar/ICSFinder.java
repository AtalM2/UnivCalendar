package calendar;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		
		FileReader file;
		file = new FileReader(path);
		
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			ret.add(scanner.nextLine());
		}
		return ret;
	}

	public static ArrayList<String> getURL(String path) throws MalformedURLException, IOException {
		ArrayList<String> ret = new ArrayList<>();
		
		URL u = new URL(path);
		InputStreamReader isr = new InputStreamReader(u.openStream());
		BufferedReader br = new BufferedReader(isr);

		Scanner scanner = new Scanner(br);
		while (scanner.hasNextLine()) {
			ret.add(scanner.nextLine());
		}
		
		return ret;
	}
}
