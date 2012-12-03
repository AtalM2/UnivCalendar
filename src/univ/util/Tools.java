package univ.util;

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
}
