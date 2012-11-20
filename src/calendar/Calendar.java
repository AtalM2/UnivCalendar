package calendar;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class Calendar {

	public static void main(String[] args) {
		try {
			System.out.println(ICSFinder.getLocal("toto.txt"));
			System.out.println(ICSFinder.getURL("http://www.edt-sciences.univ-nantes.fr/g78030.ics"));
		} catch (Exception e) {
			System.out.println("Error : " + e.getLocalizedMessage());
		}
	}
}
