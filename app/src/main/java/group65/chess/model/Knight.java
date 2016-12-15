package group65.chess.model;

/**
 * Knight class
 * @author Aziz Rahman
 * @author Amy Guinto
 *
 */

public class Knight extends Piece {
	/**
	 * Knight constructor
	 * @param row	indicates rank of Knight
	 * @param col	indicates file of Knight
	 * @param text	indicates color of Knight
	 */
	public Knight(int row, int col, String text) {
		super(row, col, text);
	}

	/**
	 * isValidMove implements inherited abstract method as needed for Knight
	 */
	public boolean isValidMove(Board board, int newRow, int newCol) {
		int row = getRow();
		int col = getCol();
		if (row == newRow && col == newCol)
			return false;
		if(board.pieces[newRow][newCol] != null) {
			if (board.pieces[newRow][newCol].getText().charAt(0) == getText().charAt(0))
			return false;
		}
		
		//Knight moving vertically
		if ((row + 2 == newRow || row - 2 == newRow) && (col + 1 == newCol || col - 1 == newCol))
			return true;
		//Knight moving horizontally
		else if ((row + 1 == newRow || row - 1 == newRow) && (col + 2 == newCol || col - 2 == newCol))
			return true;
		else
			return false;
	
	}
}