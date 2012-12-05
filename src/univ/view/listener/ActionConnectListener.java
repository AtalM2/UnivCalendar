/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import univ.view.ConnectionFrame;
import univ.view.MainFrame;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class ActionConnectListener implements ActionListener {
	private ConnectionFrame frame;

	public ActionConnectListener(ConnectionFrame f) {
		frame = f;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		boolean localIcs = frame.local.isSelected();
		String ics = localIcs ? frame.fieldLocal.getText() : frame.fieldUrl.getText();
		String login = frame.fieldLogin.getText();
		String pwd = frame.fieldPwd.getText();
		
		if (!"".equals(ics) && !"".equals(login) && !"".equals(pwd)) {
			frame.connect(login, pwd, ics, localIcs);
		}
		
	}
	
}
