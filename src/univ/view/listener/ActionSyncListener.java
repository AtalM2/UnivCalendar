/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import univ.google.GGLAction;
import univ.view.MainFrame;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class ActionSyncListener implements ActionListener {
	
	private MainFrame main;

	public ActionSyncListener(MainFrame m) {
		main = m;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// On récupère la liste des actions à effectuer pour la synchronisation
		ArrayList<GGLAction> array = new ArrayList<>();
		array = main.jWeek.getSyncAction();
		
		// CODE A RAJOUTE POUR EXECUTER LES GGLACTIONS CONTENUES DANS ARRAY //
		
		JOptionPane.showMessageDialog(main,"Synchronisation terminée","Synchronisation",JOptionPane.INFORMATION_MESSAGE);
	}
	
}
