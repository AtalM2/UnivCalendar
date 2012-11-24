package calendar;


/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class GoogleCalendar {
	
	private String host;
	private String port;
	private String protocol;
	private String user;
	private String home;
	private String password;
	private String collection;

	public int test() {
		this.host = "www.google.com";
		this.port = "443";
		this.protocol = "https";
		this.user = "atal.univ.nantes@gmail.com";
		this.home = "/calendar/dav/" + this.user + "/";
		this.password = "jnatal44";
		this.collection = "events/";
		
		
		
		
		return 0;
	}
}
