package univ.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class MainFrame extends JFrame {

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
		setSize(800, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon("img/icon-app.jpg");
		setIconImage(icon.getImage());
	}

	private void buildMenu() {
		JMenuBar menuBar = new JMenuBar();
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
		JPanel content = new JPanel();
		add(content);
		
		// Panel top
		JButton top = new JButton();
		top.setPreferredSize(new Dimension(780,130));
		content.add(top, BorderLayout.PAGE_START);
		
		// Panel bottom
		JButton bottom = new JButton();
		bottom.setPreferredSize(new Dimension(780,380));
		content.add(bottom, BorderLayout.PAGE_END);
		
		
	}
	
}
