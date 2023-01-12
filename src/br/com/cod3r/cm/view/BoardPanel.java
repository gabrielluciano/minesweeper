package br.com.cod3r.cm.view;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import br.com.cod3r.cm.model.Board;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	public BoardPanel(Board board) {
		
		setLayout(new GridLayout(
				board.getLines(), board.getColumns()));
		
		
		board.forEachCell(c -> add(new CellButon(c)));
		board.subscribe(win -> {
			SwingUtilities.invokeLater(() -> {				
				if(win) {
					JOptionPane.showMessageDialog(this, "You win!");
				} else {
					JOptionPane.showMessageDialog(this, "You lose!");				
				}
				board.restart();
			});
		});
	}
	
}
