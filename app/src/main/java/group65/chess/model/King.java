package group65.chess.model;

/**
 * King class
 * @author Aziz Rahman
 * @author Amy Guinto
 *
 */

public class King extends Piece {
	
	/**
	 * King constructor
	 * @param row	indicates rank of King
	 * @param col	indicates file of King
	 * @param text	indicates color of King
	 */
	public King(int row, int col, String text) {
		super(row, col, text);
	}

	/**
	 * isValidMove	implements inherited abstract method as needed for King
	 */
	public boolean isValidMove(Board board, int newRow, int newCol) {
		
		int row = getRow();
		int col = getCol();
		if (row == newRow && col == newCol)
			return false;
		
		int diffR = Math.abs(newRow - row);
		int diffC = Math.abs(newCol - col);
		
		if (diffR > 1 || diffC > 1){
			if (diffR == 0){
				//This will automatically castle if it's legal. 
				//We should check if this can evaluate to true in the Chess method and then only call the isValidMove method
				if (castling(col,board))
					return true;
				else
					return false;
			}
			return false;
		}
		
		if(board.pieces[newRow][newCol] != null) {
			if (board.pieces[newRow][newCol].getText().charAt(0) == getText().charAt(0))
			return false;
		}
		
		return true;
	}

	//This moves the pieces as well
	boolean castling(int col, Board board){
		
		//determines whether the piece is black or white
		boolean w = false;
		String s = getText();		
		if (s.charAt(0) == 'w')
			w = true;
		
		if (hasMoved)
			return false;
		
		if (col > getCol()){
			
			if (((Rook)(board.pieces[getRow()][col+1])).hasMoved == true)
				return false;
			//Checking that there are no pieces in between the King and Rook
			for (int i = 1; i < 3; i++){
				if (board.pieces[getRow()][getCol()+i] != null)
					return false;
			}
			board.pieces[getRow()][col] = new King(getRow(),col,getText());
			
			if (w)
				board.pieces[getRow()][col-1] = new Rook(getRow(),col-1,"wR");
			else
				board.pieces[getRow()][col-1] = new Rook(getRow(),col-1,"bR");
			
			board.pieces[getRow()][col+1] = null;
			board.pieces[getRow()][getCol()] = null;
			hasMoved = true;
			return true;	
		}
		else if (col < getCol()){
			
			if (((Rook)(board.pieces[getRow()][col-2])).hasMoved == true)
				return false;
			//Checking that there are no pieces in between the King and Rook
			for (int i = 1; i < 4; i++){
				if (board.pieces[getRow()][getCol()-i] != null)
					return false;
			}
			board.pieces[getRow()][col] = new King(getRow(),col,getText());
			
			if (w)
				board.pieces[getRow()][col+1] = new Rook(getRow(),col-1,"wR");
			else
				board.pieces[getRow()][col+1] = new Rook(getRow(),col-1,"bR");
			
			board.pieces[getRow()][col-2] = null;
			board.pieces[getRow()][getCol()] = null;
			hasMoved = true;
			return true;
			
		}
		return false;
	}
}
