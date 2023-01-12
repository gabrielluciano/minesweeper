package br.com.cod3r.cm.view;

import javax.swing.JFrame;

import br.com.cod3r.cm.model.Board;

@SuppressWarnings("serial")
public class MainScreen extends JFrame {

	private final String TITLE = "Minesweeper";
	
	public MainScreen() {
		Board board = new Board(16, 30, 30);
		add(new BoardPanel(board));
		
		setTitle(TITLE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(690, 438);
		setLocationRelativeTo(null);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainScreen();
	}
	
}
