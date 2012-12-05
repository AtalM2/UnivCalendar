package univ.util;

import java.io.File;
import javax.swing.ImageIcon;

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
 
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
 
    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Tools.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
