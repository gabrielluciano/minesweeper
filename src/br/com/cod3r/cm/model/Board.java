package br.com.cod3r.cm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Board implements CellObserver {

	private final int lines;
	private final int columns;
	private final int mines;
	
	private final List<Cell> cells = new ArrayList<>();
	private final List<Consumer<Boolean>> observers = new ArrayList<>();

	public Board(int lines, int columns, int mines) {
		this.lines = lines;
		this.columns = columns;
		this.mines = mines;
		
		generateCells();
		associateNeighbors();
		raffleMines();
	}
	
	public void subscribe(Consumer<Boolean> observer) {
		observers.add(observer);
	}
	
	public void notify(boolean win) {
		observers.forEach(o -> o.accept(win));
	}
	
	public void forEachCell(Consumer<Cell> action) {
		cells.forEach(action);
	}
	
	public int getLines() {
		return lines;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public void openCell(int line, int column) { 
		cells.parallelStream()
			.filter(c -> c.getLine() == line && c.getColumn() == column)
			.findFirst()
			.ifPresent(c -> c.open());
	}
	
	public void toggleCellFlag(int line, int column) { 
		cells.parallelStream()
		.filter(c -> c.getLine() == line && c.getColumn() == column)
		.findFirst()
		.ifPresent(c -> c.toggleFlag());
	}

	private void generateCells() {
		for (int l = 0; l < lines; l++) {
			for (int c = 0; c < columns; c++) {
				Cell cell = new Cell(l, c);
				cell.subscribe(this);
				cells.add(cell);
			}
		}
	}
	
	private void associateNeighbors() {
		for (Cell c1 : cells) {
			for (Cell c2 : cells) {
				c1.addNeighbor(c2);
			}
		}
	}
	
	private void raffleMines() {
		int minesLefting = mines;
		
		while(minesLefting > 0) {
			int randomCellIndex = (int) (Math.random() * cells.size());
			Cell drawnCell = cells.get(randomCellIndex);
			minesLefting = undermineCell(drawnCell, minesLefting);
		}
	}
	
	private int undermineCell(Cell cell, int minesLefting) {
		if (!cell.hasMine() && minesLefting > 0) {
			cell.undermine();
			minesLefting--;
		}
		return minesLefting;
	}
	
	public boolean goalAchieved() {
		return cells.stream().allMatch(c -> c.goalAchieved());
	}
	
	public void restart() {
		cells.forEach(c -> c.restart());
		raffleMines();
	}

	@Override
	public void cellUpdated(Cell cell, CellEvent event) {
		if (event == CellEvent.EXPLOSION) {
			showMines();
			notify(false);
		} else if(goalAchieved()) {
			notify(true);
		}
	}	
	
	private void showMines() {
		cells.stream()
			.filter(c -> c.hasMine() && !c.isFlagged())
			.forEach(c -> c.setOpen(true));
	}
}
