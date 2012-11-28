package univ.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class MainFrame extends JFrame {

	private JCalendarWeek jWeek;
	
	public MainFrame() {
		super();
		
		buildLookAndFeel();
		buildFrame();
		buildMenu();
		buildContent();
	}

	private void buildLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// ON NE FAIT RIEN DE SPECIAL
		}
	}
	
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
	
	private void buildContent() {
		JPanel content = new JPanel(new MigLayout("wrap 1"));
		add(content);
		content.setBackground(Color.yellow);
		
		// Panel top
		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
		top.setBackground(Color.GREEN);
		content.add(top,"center");
		
		JPanel topContent = new JPanel(new FlowLayout());
		top.add(topContent);
		
		JButton left = new JButton("<");
		topContent.add(left);
		
		JPanel week = new JPanel(new GridLayout(0,1));
		JLabel weekNumber = new JLabel("Semaine 45");
		weekNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		week.add(weekNumber);
		JLabel weekDetail = new JLabel("Du 5/11 au 15/11");
		weekDetail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		week.add(weekDetail);		
		topContent.add(week);
		
		JButton right = new JButton(">");
		topContent.add(right);
					
		// Panel bottom
		JPanel bottom = new JPanel(new MigLayout());
		bottom.setBackground(Color.ORANGE);
		content.add(bottom,"push, grow");
		
		jWeek = new JCalendarWeek();
		bottom.add(jWeek, "push, grow");
	}
	
	public JCalendarWeek getJWeek() {
		return jWeek;
	}

	
}
