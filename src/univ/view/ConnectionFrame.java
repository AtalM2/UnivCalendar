package univ.view;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.AuthenticationException;
import univ.view.listener.ActionConnectListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.miginfocom.swing.MigLayout;
import univ.util.Filter;

/**
 * Classe gérant l'affichage de la fenêtre de connexion
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class ConnectionFrame extends JFrame {

	public JTextField fieldLocal;
	public JTextField fieldUrl;
	public JTextField fieldLogin;
	public JPasswordField fieldPwd;
	public JRadioButton url;
	public JRadioButton local;

	public ConnectionFrame() {
		super();
		buildLookAndFeel();
		buildFrame();
		buildContent();
	}

	/**
	 * Selection d'un style correspondant à l'OS
	 */
	private void buildLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// ON NE FAIT RIEN DE SPECIAL
		}
	}

	/**
	 * Création de la fenêtre générale
	 */
	private void buildFrame() {
		setTitle("UnivCalendar");
		setSize(new Dimension(350, 200));
		setPreferredSize(new Dimension(350, 200));
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon("img/icon-app.jpg");
		setIconImage(icon.getImage());
		setLayout(new BorderLayout());
	}

	/**
	 * Création du contenu
	 */
	private void buildContent() {
		JPanel icsChooser = new JPanel(new MigLayout());
		JPanel googleLogin = new JPanel(new MigLayout());

		add(icsChooser, BorderLayout.NORTH);
		icsChooser.add(new JLabel("ICS local : "));
		fieldLocal = new JTextField();
		fieldLocal.addMouseListener((new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent me) {
				fieldLocal.setText(chooseFile());
			}

			@Override
			public void mousePressed(MouseEvent me) {
			}

			@Override
			public void mouseReleased(MouseEvent me) {
			}

			@Override
			public void mouseEntered(MouseEvent me) {
			}

			@Override
			public void mouseExited(MouseEvent me) {
			}
		}));
		icsChooser.add(fieldLocal, "wrap, grow, width 300px");
		icsChooser.add(new JLabel("ICS URL : "));
		fieldUrl = new JTextField();
		icsChooser.add(fieldUrl, "wrap, grow, width 300px");
		local = new JRadioButton("Local");
		local.setSelected(true);
		url = new JRadioButton("URL");
		ButtonGroup group = new ButtonGroup();
		group.add(local);
		group.add(url);
		icsChooser.add(local);
		icsChooser.add(url);

		add(googleLogin, BorderLayout.CENTER);
		googleLogin.add(new JLabel("Email google : "));
		fieldLogin = new JTextField();
		googleLogin.add(fieldLogin, "wrap, grow, width 200px");
		googleLogin.add(new JLabel("Mot de passe : "));
		fieldPwd = new JPasswordField();
		googleLogin.add(fieldPwd, "wrap, grow, width 200px");

		JButton connect = new JButton("Connexion");
		connect.addActionListener(new ActionConnectListener(this));
		add(connect, BorderLayout.SOUTH);
	}

	public String chooseFile() {
		JFileChooser choix = new JFileChooser();
		choix.setAcceptAllFileFilterUsed(false);
		choix.addChoosableFileFilter(new Filter(new String[]{"ics"}, "Fichier ICS (*.ics)"));
		choix.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int retour = choix.showOpenDialog(this);
		if (retour == JFileChooser.APPROVE_OPTION) {
			return choix.getSelectedFile().getAbsolutePath();
		} else {
			return "";
		}
	}

	public void connect(String login, String pwd, String ics, boolean localIcs) {
		CalendarService myService = new CalendarService("");
		try {
			myService.setUserCredentials(login, pwd);
			MainFrame main = new MainFrame(login, pwd, ics, localIcs);
			main.setVisible(true);
		} catch (AuthenticationException e) {
			JOptionPane.showMessageDialog(this, "Connexion impossible", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
		}

	}
}
