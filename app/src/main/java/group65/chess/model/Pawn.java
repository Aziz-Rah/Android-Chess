package group65.chess.model;
/**
 * Pawn class
 * @author Aziz Rahman
 * @author Amy Guinto
 *
 */
public class Pawn extends Piece{
	
	private boolean enPassant = false;
	
	/**
	 * Pawn constructor
	 * @param row	indicates rank of Pawn
	 * @param col	indicates file of Pawn
	 * @param text	indicates color of Pawn
	 */
	public Pawn(int row, int col, String text) {
		super(row, col, text);	
	}
	
	/**
	 * isValidMove implements inherited abstract method as needed for Pawn
	 */
	public boolean isValidMove(Board board, int newRow, int newCol) {
		int row = getRow();
		int col = getCol();
		
		if (row == newRow && col == newCol)
			return false;
		
		boolean collision = false;
		if(board.pieces[newRow][newCol] != null) {
			//Collision detection
			collision = true;
			
			if (board.pieces[newRow][newCol].getText().charAt(0) == getText().charAt(0))
				return false;
		}
		
		//determines whether the piece is black or white
		boolean w = false;
		String s = getText();		
		if (s.charAt(0) == 'w')
			w = true;
		
		if(hasMoved == false) {			
			if(w) {
				if(col == newCol && newRow == 4) {
					return true;
				}
			}
			else {
				if(col == newCol && newRow == 3) {
					return true;
				}
			}
		}
		
		//Determines whether the pawn is attacking properly
		if (w && collision){
			if (row-1 == newRow && (col+1 == newCol || col-1 == newCol)) {
				hasMoved = true;
				return true;
			}
			else
				return false;
		}
		else if (!w && collision){
			if (row+1 == newRow && (col+1 == newCol || col-1 == newCol)) {
				hasMoved = true;
				return true;
			}
			else
				return false;
		}
		
		//Checks if the pawn move is valid with the enPassant
		if (enPassant && w){
			if ((board.pieces[newRow][newCol] != null) || board.pieces[newRow][newCol-1] != null)
				return false;
			if (col == newCol && (row+1 == newRow || row+2 == newRow))
				return true;
		}
		else if (w){
			if (board.pieces[newRow][newCol] != null)
				return false;
			if (row-1 == newRow && col == newCol)
				return true;
		}
		else if (enPassant && !w){
			if ((board.pieces[newRow][newCol] != null) || board.pieces[newRow][newCol+1] != null)
				return false;
			if (col == newCol && (row-1 == newRow || row-2 == newRow))
				return true;
		}
		else {
			if (board.pieces[newRow][newCol] != null)
				return false;
			if (row+1 == newRow && col == newCol)
				return true;
		}
		
		return false;
	}
	
	//Call promotion after the piece has already moved
	Piece promotion(String s){
		
		if (s.equals("Rook"))
			return new Rook(getRow(),getCol(),getText());
		if (s.equals("Knight"))
			return new Knight(getRow(),getCol(),getText());
		if (s.equals("Bishop"))
			return new Bishop(getRow(),getCol(),getText());
		if (s.equals("Queen") || s.isEmpty())
			return new Queen(getRow(),getCol(),getText());
		
		return null;
		
	}
}
