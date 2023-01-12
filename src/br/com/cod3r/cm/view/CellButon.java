package br.com.cod3r.cm.view;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import br.com.cod3r.cm.model.Cell;
import br.com.cod3r.cm.model.CellEvent;
import br.com.cod3r.cm.model.CellObserver;

@SuppressWarnings("serial")
public class CellButon extends JButton 
	implements CellObserver, MouseListener {

	private final Color DEFAULT_BG = new Color(184, 184, 184);
	private final Color FLAGGED_BG = new Color(8, 179, 247);
	private final Color EXPLODED_BG = new Color(189, 66, 68);
	private final Color TEXT_GREEN = new Color(0, 100, 0);
	
	private Cell cell;
	
	public CellButon(Cell cell) {
		this.cell = cell;
		applyDefaultStyle();
		setOpaque(true);
				
		addMouseListener(this);
		cell.subscribe(this);
	}

	@Override
	public void cellUpdated(Cell cell, CellEvent event) {
		switch (event) {
		case OPEN:
			applyOpenStyle();
			break;
		case FLAGGED:
			applyFlaggedStyle();
			break;
		case EXPLOSION:
			applyExplodedStyle();
			break;
		default:
			applyDefaultStyle();
		}
		
		SwingUtilities.invokeLater(() -> {
			repaint();
			validate();
		});
	}

	private void applyFlaggedStyle() {
		setBackground(FLAGGED_BG);
		setText("M");
	}

	private void applyOpenStyle() {
		
		if (cell.hasMine()) {
			setBackground(EXPLODED_BG);
			return;
		}
		
		setBackground(DEFAULT_BG);
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		switch (cell.getNumberOfMinesInNeighborhood()) {
		case 1:
			setForeground(TEXT_GREEN);
			break;
		case 2:
			setForeground(Color.BLUE);
			break;
		case 3:
			setForeground(Color.YELLOW);
			break;
		case 4:
		case 5:
		case 6:
			setForeground(Color.RED);
			break;
		default:
			setForeground(Color.PINK);
		}
		
		String value = !cell.isNeighborhoodSafe() ?
				cell.getNumberOfMinesInNeighborhood() + "" : "";
		setText(value);
	}
	
	private void applyExplodedStyle() {
		setBackground(EXPLODED_BG);
		setForeground(Color.BLACK);
		setText("X");
	}
	
	private void applyDefaultStyle() {
		setBackground(DEFAULT_BG);
		setBorder(BorderFactory.createBevelBorder(0));
		setText("");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1) {
			cell.open();
		} else {
			cell.toggleFlag();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
