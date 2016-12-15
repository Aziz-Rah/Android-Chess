package group65.chess.model;

/**
 * Rook class
 * @author Aziz Rahman
 * @author Amy Guinto
 * 
 */

public class Rook extends Piece{

	/**
	 * Rook constructor
	 * @param row	indicates rank of Rook
	 * @param col	indicates file of Rook
	 * @param text	indicates color of Rook
	 */
	public Rook(int row, int col, String text) {
		super(row, col, text);	
	}

	/**
	 * isValidMove	implements inherited abstract method as needed for Rook
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
		
		//Determines if there is a piece before the final position
		if (newRow == row){
			if (col < newCol){
				for (int i = col+1; i < newCol; i++){
					if (board.pieces[newRow][i] != null)
						return false;
				}
				return true;
			}
			else {
				for (int i = col-1; i > newCol; i--){
					if (board.pieces[newRow][i] != null)
						return false;
				}
				return true;
			}
		}
		else if (newCol == col){
			if (row < newRow){
				for (int i = row+1; i < newRow; i++){
					if (board.pieces[i][newCol] != null)
						return false;
				}
				return true;
			}
			else {
				for (int i = row-1; i > newRow; i--){
					if (board.pieces[i][newCol] != null)
						return false;
				}
				return true;
			}
		}
		
		return false;
		
	}
}