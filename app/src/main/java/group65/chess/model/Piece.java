package group65.chess.model;

/**
 * abstract Piece class provides all necessary fields and methods for all types of pieces
 * @author Amy Guinto
 * @author Aziz Rahman
 * 
 */

public abstract class Piece {
	private int row;
	private int col;
	private String text;
	public boolean hasMoved;
	
	/**
	 * Piece constructor
	 * @param row	indicates rank of piece
	 * @param col	indicates file of piece
	 * @param text	indicates color and type of piece
	 */
	public Piece(int row, int col, String text){
		this.row = row;
		this.col = col;
		this.text = text;
		hasMoved = false;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	/**
	 * setPos updates the file and rank of a piece
	 * @param row	indicates new rank of piece
	 * @param col	indicates new file of piece
	 */
	public void setPos(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	/**
	 * getText retrieves the text representation of the piece
	 * @return	indicates the color and type of piece
	 */
	public String getText(){
		return text;
	}
	
	/**
	 * isValidMove determines whether a piece can move to a given spot
	 * @param board	is the game board with all of its pieces
	 * @param row	is the rank the piece is attempting to move to
	 * @param col	is the file the piece is attempting to move to
	 * @return boolean	returns true if the move is valid, false if the move is invalid
	 */
	public abstract boolean isValidMove(Board board, int row, int col);
}