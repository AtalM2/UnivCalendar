/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.view;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class DefaultGridBagConstraints extends GridBagConstraints {
	public DefaultGridBagConstraints() {
		super(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}
	public DefaultGridBagConstraints(int gridx, int gridy) {
		super(gridx, gridy, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}
	public DefaultGridBagConstraints(int gridx, int gridy, int width, int height) {
		super(gridx, gridy, width, height, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}
	public DefaultGridBagConstraints(int gridx, int gridy, int width, int height, double weightx, double weighty) {
		super(gridx, gridy, width, height, weightx, weighty, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}
	public DefaultGridBagConstraints(int gridx, int gridy, int width, int height, int anchor, int fill) {
		super(gridx, gridy, width, height, 1.0, 1.0, anchor, fill, new Insets(0, 0, 0, 0), 0, 0);
	}
}
