package univ.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe outils pour ajouter des fonctions utiles
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class Tools {

	/**
	 * Permet de faire un arrondi au nombre le plus proche
	 *
	 * @param number Le nombre à arrondir
	 * @param floor L'arrondi à faire
	 * @return Le nombre arrondi
	 */
	public static int floor(int number, int floor) {
		return floor * ((number + floor / 2) / floor);
	}

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static ArrayList<String> readFile(String path) throws FileNotFoundException, IOException {
		ArrayList<String> ret = new ArrayList<>();

		FileReader file;
		file = new FileReader(path);

		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			ret.add(scanner.nextLine());
		}
		file.close();
		return ret;
	}

	public static void writeFile(String fileName, ArrayList<String> txt) throws IOException {
		//on va chercher le chemin et le nom du fichier et on me tout ca dans un String
		String filePath = System.getProperty("user.dir") + "/" + fileName;


		/**
		 * BufferedWriter a besoin d un FileWriter, les 2 vont ensemble, on
		 * donne comme argument le nom du fichier true signifie qu on ajoute
		 * dans le fichier (append), on ne marque pas par dessus *
		 */
		FileWriter fw = new FileWriter(filePath, true);

		// le BufferedWriter output auquel on donne comme argument le FileWriter fw cree juste au dessus
		BufferedWriter output = new BufferedWriter(fw);

		//on marque dans le fichier ou plutot dans le BufferedWriter qui sert comme un tampon(stream)
		for (String line : txt) {
			output.write(line + "\n");
		}

		//on peut utiliser plusieurs fois methode write

		output.flush();
		//ensuite flush envoie dans le fichier, ne pas oublier cette methode pour le BufferedWriter

		output.close();
		//et on le ferme
	}

	public static String encode(String text, String key) {
		String ret = "";
		char[] arrayText;
		char[] arrayKey;
		char ct;
		char ck;
		arrayKey = key.toCharArray();
		arrayText = text.toCharArray();
		for (int i = 0; i < arrayText.length; i++) {
			ct = arrayText[i];
			ck = arrayKey[i % arrayKey.length];
			ct = (char) (((int) ct) + ((int) ck));
			ret += ct + "";
		}
		for (int i = 0; i < arrayText.length; i++) {
			ct = arrayText[i];
			ck = arrayKey[i % arrayKey.length];
			ct = (char) (((int) ct) - ((int) ck));
			ret += ct + "";
		}
		return ret;
	}

	public static String decode(String text, String key) {
		String ret = "";
		char[] arrayText;
		char[] arrayKey;
		char ct;
		char ck;
		arrayKey = key.toCharArray();
		arrayText = text.toCharArray();
		for (int i = 0; i < arrayText.length/2; i++) {
			ct = arrayText[i];
			ck = arrayKey[i % arrayKey.length];
			ct = (char) (((int) ct) - ((int) ck));
			ret += ct + "";
		}
		return ret;
	}
}
