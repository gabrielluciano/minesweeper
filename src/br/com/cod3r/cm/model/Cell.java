package br.com.cod3r.cm.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {

	private final int line;
	private final int column;
	
	private boolean open = false;
	private boolean mine = false;
	private boolean flag = false;
	
	private List<Cell> neighbors = new ArrayList<>();
	private List<CellObserver> observers = new ArrayList<>();
	// private List<BiConsumer<Cell, CellEvent>> observers = new ArrayList<>();
	
	Cell(int line, int column) {
		this.line = line;
		this.column = column;
	}
	
	public void subscribe(CellObserver observer) {
		observers.add(observer);
	}
	
	private void notify(CellEvent event) {
		observers.forEach(o -> o.cellUpdated(this, event));
	}

	boolean addNeighbor(Cell cell) {
		if (isNeighbor(cell)) {			
			this.neighbors.add(cell);
			return true;
		}
		return false;
	}
	
	private boolean isNeighbor(Cell cell) {
		boolean differentLine = line != cell.line;
		boolean differentColumn = column != cell.column;
		boolean diagonal = differentLine && differentColumn;
		
		int lineDelta = Math.abs(line - cell.line);
		int columnDelta = Math.abs(column - cell.column);
		int totalDelta = lineDelta + columnDelta;
		
		return totalDelta == 1 && !diagonal || totalDelta == 2 && diagonal;
	}
	
	public void toggleFlag() {
		if(isClosed()) {
			flag = !flag;
			
			if (isFlagged()) {
				notify(CellEvent.FLAGGED);
			} else {
				notify(CellEvent.UNFLAGGED);
			}
		}
	}
	
	public boolean open() {
		if (isClosed() && !isFlagged()) {		
			if (hasMine()) {
				notify(CellEvent.EXPLOSION);
				return true;
			}
			
			setOpen(true);
			
			if (isNeighborhoodSafe()) {
				neighbors.forEach(n -> n.open());
			}
			
			return true;
		}
		return false;
	}
	
	public boolean isNeighborhoodSafe() {
		return neighbors.stream().noneMatch(n -> n.hasMine());
	}
	
	public boolean isFlagged() {
		return flag;
	}
	
	void undermine() {
		if (!hasMine()) {
			mine = true;
		}
	}
	
	public boolean isOpen() {
		return open;
	}
	
	void setOpen(boolean open) {
		this.open = open;
		
		if (isOpen()) {
			notify(CellEvent.OPEN);
		}
	}
	
	public boolean isClosed() {
		return !isOpen();
	}
	
	public boolean hasMine() {
		return mine;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
	
	boolean goalAchieved() {
		boolean unraveled = !hasMine() && isOpen();
		boolean isProtected = hasMine() && isFlagged();
		
		return unraveled || isProtected;
	}
	
	public int getNumberOfMinesInNeighborhood() {
		return (int) neighbors.stream().filter(n -> n.hasMine()).count();
	}
	
	void restart() {
		open = false;
		mine = false;
		flag = false;
		notify(CellEvent.RESTART);
	}
}
