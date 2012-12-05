package univ.view;

import univ.view.listener.ActionWeekChooserListener;
import univ.view.listener.ActionSyncListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.miginfocom.swing.MigLayout;
import univ.calendar.Calendar;
import univ.calendar.Week;
import univ.google.GGLAction;
import univ.util.DateTime;

/**
 * Classe gérant l'affichage de la fenêtre principale
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class MainFrame extends JFrame {

	private Calendar calendar;
	private JCalendarWeek jWeek;
	public JLabel weekNumber;
	public JLabel weekDetail;

	public MainFrame() {
		super();
		ToolTipManager.sharedInstance().setDismissDelay(1000000);
		buildLookAndFeel();
		buildFrame();
		buildMenu();
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
		setSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(800, 600));
		setLocationRelativeTo(null);
		//setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon("img/icon-app.jpg");
		setIconImage(icon.getImage());
		setLayout(new BorderLayout());
	}

	/**
	 * Création du menu
	 */
	private void buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(menuBar.getWidth(), 26));
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("Fichier");
		menuBar.add(fileMenu);
		JMenuItem disconnectAction = new JMenuItem("Déconnexion");
		ImageIcon iconDisconnect = new ImageIcon("img/icon-disconnect.png");
		disconnectAction.setIcon(iconDisconnect);
		JMenuItem exitAction = new JMenuItem("Quitter");
		ImageIcon iconExit = new ImageIcon("img/icon-exit.png");
		exitAction.setIcon(iconExit);
		fileMenu.add(disconnectAction);
		fileMenu.add(exitAction);
	}

	/**
	 * Création du contenu
	 */
	private void buildContent() {
		JPanel content = new JPanel(new MigLayout("wrap 1"));
		add(content);

		// Panel top
		JPanel top = new JPanel(new MigLayout());
		content.add(top, "grow");

		JPanel topContent = new JPanel(new FlowLayout());
		top.add(topContent, "dock center");
		JButton btnSync = new JButton("Synchroniser");
		top.add(btnSync, "dock east");
		btnSync.addActionListener(new ActionSyncListener(this));

		JButton left = new JButton("<");
		left.addActionListener(new ActionWeekChooserListener(this));
		topContent.add(left);

		JPanel week = new JPanel(new GridLayout(0, 1));
		setWeekNumber(new JLabel("Semaine 45"));
		getWeekNumber().setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		week.add(getWeekNumber());
		setWeekDetail(new JLabel("Du 5/11 au 15/11"));
		getWeekDetail().setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		week.add(getWeekDetail());
		topContent.add(week);

		JButton right = new JButton(">");
		right.addActionListener(new ActionWeekChooserListener(this));
		topContent.add(right);

		// Panel bottom
		JPanel bottom = new JPanel(new MigLayout());
		bottom.setBackground(Color.ORANGE);
		content.add(bottom, "push, grow");

		jWeek = new JCalendarWeek();
		bottom.add(jWeek, "push, grow");
	}

	public void setWeek(Week w) {
		weekNumber.setText("Semaine " + w.getStartDate().getWeekOfYear());
		weekDetail.setText("Du " + w.getStartDate().toString() + " au " + w.getEndDate().toString());
		jWeek.setWeek(w);
		build();
	}

	public void build() {
		jWeek.build();
	}

	public JCalendarWeek getJWeek() {
		return jWeek;
	}

	public JLabel getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(JLabel weekNumber) {
		this.weekNumber = weekNumber;
	}

	public JLabel getWeekDetail() {
		return weekDetail;
	}

	public void setWeekDetail(JLabel weekDetail) {
		this.weekDetail = weekDetail;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		setWeek(this.calendar.findWeek(new DateTime()));
	}

	public ArrayList<GGLAction> getSyncAction() {
		return jWeek.getSyncAction();
	}
}
