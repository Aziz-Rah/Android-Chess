package group65.chess.model;

/**
 * Bishop class
 * @author Aziz Rahman
 * @author Amy Guinto
 *
 */
public class Bishop extends Piece {

	/**
	 * Bishop constructor
	 * @param row	indicates rank of Bishop
	 * @param col	indicates file of Bishop
	 * @param text	indicates color of Bishop
	 */
	public Bishop(int row, int col, String text) {
		super(row, col, text);
	}

	/**
	 * isValidMove implements inherited abstract method as needed for Bishop
	 */
	public boolean isValidMove(Board board, int newRow, int newCol) {
		int row = getRow();
		int col = getCol();
		if (row == newRow || col == newCol)
			return false;
		
		int diffR = newRow-row;
		int diffC = newCol-col;
		diffR = Math.abs(diffR);
		diffC = Math.abs(diffC);
		if (diffR != diffC)
			return false;
		
		if(board.pieces[newRow][newCol] != null) {
			if (board.pieces[newRow][newCol].getText().charAt(0) == getText().charAt(0))
				return false;
		}
		
		//Check if a piece exists before the final position
		//Up-Right
		if (row < newRow && col < newCol){
			for (int i = row+1, j = col+1; i < newRow; i++, j++){
				if (board.pieces[i][j] != null)
					return false;
			}
			return true;
		}
		//Up-Left
		if (row < newRow && col > newCol){
			for (int i = row+1, j = col-1; i < newRow; i++, j--){
				if (board.pieces[i][j] != null)
					return false;
			}
			return true;
		}
		//Down-Right
		if (row > newRow && col < newCol){
			for (int i = row-1, j = col+1; i > newRow; i--, j++){
				if (board.pieces[i][j] != null)
					return false;
			}
			return true;
		}
		//Down-Left
		if (row > newRow && col > newCol){
			for (int i = row-1, j = col-1; i > newRow; i--, j--){
				if (board.pieces[i][j] != null)
					return false;
			}
			return true;
		}
		
		return false;
	}
}