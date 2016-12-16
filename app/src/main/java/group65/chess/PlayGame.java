package group65.chess;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import group65.chess.model.Bishop;
import group65.chess.model.Board;
import group65.chess.model.King;
import group65.chess.model.Knight;
import group65.chess.model.Pawn;
import group65.chess.model.Piece;
import group65.chess.model.Queen;
import group65.chess.model.Rook;

public class PlayGame extends AppCompatActivity implements OnItemClickListener {
    private GridView chessboard;
    private ImageAdapter adapter;
    private View[] squareClick;
    private int[] squarePosition;
    private TextView turn;
    private int[] prevMove;

    boolean gameStarted = false;
    boolean enPassant = false;
    boolean illegal = false;
    boolean enPassantMove = false;
    boolean castleMove = false;
    boolean pawnPromoted = false;
    Piece checkPiece = null;
    Board board;
    int player = 0;
    int startRow, startCol, endRow, endCol;
    String promotion;
    String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // action bar (up navigation)
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);
        turn = (TextView)findViewById(R.id.textView);

        if(!gameStarted) {
            squareClick = new View[2];
            squarePosition = new int[2];
            adapter = new ImageAdapter(this);
            board = new Board();
            board.fill();
            board.fillList();
            gameStarted = true;
            prevMove = new int[2];
            prevMove[0] = -2;
        }

        // grid view (chessboard and pieces)
        GridView gridview = (GridView)findViewById(R.id.gridview);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);
        this.chessboard = gridview;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(squareClick[0] == null) { // first click
            Piece piece = board.pieces[position/8][position%8];

            if(piece == null)
                return;
            if(piece.getText().charAt(0) == 'w' && player == 1)
                return;
            if(piece.getText().charAt(0) == 'b' && player == 0)
                return;

            squareClick[0] = view;
            squarePosition[0] = position;

            view.setBackgroundColor(Color.DKGRAY);
        } else { // second click
            squareClick[1] = view;
            squarePosition[1] = position;

            int startRow = squarePosition[0]/8;
            int startCol = squarePosition[0]%8;
            int endRow = squarePosition[1]/8;
            int endCol = squarePosition[1]%8;

            int t1 = startRow*8 + startCol%8;
            int move = chess(startRow, startCol, endRow, endCol, player, board);

            if(move == 0) {
                Toast.makeText(PlayGame.this, "Checkmate", Toast.LENGTH_SHORT).show();
                adapter.move(squarePosition[0], squarePosition[1]);
                end_checkmate();
            }
            else if(move == 1)
                Toast.makeText(PlayGame.this, "Illegal move", Toast.LENGTH_SHORT).show();
            else {
                //Toast.makeText(PlayGame.this, "Legal move", Toast.LENGTH_SHORT).show();
                adapter.move(squarePosition[0], squarePosition[1]);
                adapter.notifyDataSetChanged();
                chessboard.setAdapter(adapter);

                if(enPassantMove) {
                    adapter.remove(startRow*8 + endCol%8);
                    enPassantMove = false;
                } else if(castleMove) {
                    int startPos, endPos;
                    startPos = startRow*8;
                    if(startCol+2 == endCol)
                        startPos += 7;
                    endPos = startRow*8 + ((startCol+endCol)/2);
                    adapter.move(startPos, endPos);
                    castleMove = false;
                } else if(pawnPromoted) {
                    adapter.promote(squarePosition[1], player);
                    pawnPromoted = false;
                }

                prevMove[0] = squarePosition[0];
                prevMove[1] = squarePosition[1];

                changeTurn();

                board.display();
            }

            squareClick[0].setBackgroundColor(updateColor(squarePosition[0]));
            squareClick[0] = null;
            squareClick[1] = null;
        }


    }

    private void end_checkmate() {
        String title;
        if(player == 0)
            title = "White";
        else
            title = "Black";

        title += " Wins!";
        endDialogs(title);
    }

    private int updateColor(int position) {
        if((position/8)%2 == 0) {
            if(position%2 == 0)
                return Color.parseColor("#ffce9e");
            else
                return Color.parseColor("#d18b47");
        } else {
            if(position%2 == 0)
                return Color.parseColor("#d18b47");
            else
                return Color.parseColor("#ffce9e");
        }

    }

    @Override
    // inflate menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    // override callback for event handling
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_undo:
                undo();
                return true;
            case R.id.action_ai:
                // ai
                return true;
            case R.id.action_draw:
                draw();
                return true;
            case R.id.action_resign:
                resign();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void undo() {
        if(prevMove[0] > -1) {
            adapter.move(prevMove[1], prevMove[0]);
            adapter.notifyDataSetChanged();
            chessboard.setAdapter(adapter);

            int startRow = prevMove[1]/8;
            int startCol = prevMove[1]%8;
            int endRow = prevMove[0]/8;
            int endCol = prevMove[0]%8;
            Piece piece = board.getPiece(startRow, startCol);
            board.movePiece(piece, endRow, endCol);

            prevMove[0] = -1;
            changeTurn();
        } else {
            if(prevMove[0] == -2)
                Toast.makeText(PlayGame.this, "No move to undo", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(PlayGame.this, "You can only undo once", Toast.LENGTH_SHORT).show();
        }
    }

    private void endDialogs(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("Would you like to save this game?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                AlertDialog.Builder alert = new AlertDialog.Builder(PlayGame.this);
                alert.setTitle("Save game");
                alert.setMessage("Enter a game title");

                // Set an EditText view to get user input
                final EditText input = new EditText(PlayGame.this);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameName = input.getText().toString();

                        AlertDialog.Builder builder = new AlertDialog.Builder(PlayGame.this);
                        builder.setTitle("Want to play again?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                gameStarted = false;
                                Intent intent = getIntent();
                                startActivity(intent);
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(PlayGame.this, MainActivity.class));
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                alert.show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(PlayGame.this);
                builder.setTitle("Want to play again?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gameStarted = false;
                        Intent intent = getIntent();
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(PlayGame.this, MainActivity.class));
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void draw() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PlayGame.this);
        alert.setTitle("Draw?");
        alert.setMessage("A draw was offered.  Would you like to accept?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               endDialogs("Draw");
           }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void resign() {
        endDialogs("Resign");
    }

    /*
    private void getPromotion() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PlayGame.this);
        alert.setTitle("Pawn Promotion");
        alert.setMessage("Enter Q for queen, B for bishop, N for knight, or R for rook");

        final EditText input = new EditText(PlayGame.this);
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                promotion = input.getText().toString();
            }
        });

        alert.show();
    }
    */

    /*
   0 = Checkmate
   1 = Bad input
   2 = Normal
   white player = 0, black player = 1
   */
    public int chess(int startRow, int startCol, int endRow, int endCol, int player, Board board){
        Piece piece = board.getPiece(startRow, startCol);

        illegal = true;
        // checks for correct color piece
        if(piece != null && ((player == 0 && piece.getText().charAt(0) != 'w')
                || (player == 1 && piece.getText().charAt(0) != 'b')) ) {
            return 1;
        }
        // checks for basic move
        else if(piece != null && piece.isValidMove(board, endRow, endCol)) {
            enPassant = false;

            // move piece if valid move is entered
            board.movePiece(piece, endRow, endCol);

            illegal = false;
            if(checkPiece != null) {
                if(check(checkPiece, board) == 1) {
                    board.movePiece(piece, startRow, startCol);
                    return 1;
                }
                else
                    checkPiece = null;
            }

            //CHECK DRAW



            // checks for promotion (promote to queen)
            if(piece.getText().charAt(1) == 'p') {
                if(endRow == 0 || endRow == 7) {
                    if(endRow == 0)
                        board.pieces[endRow][endCol] = new Queen(endRow, endCol, "wQ");
                    else
                        board.pieces[endRow][endCol] = new Queen(endRow, endCol, "bQ");
                    pawnPromoted = true;
                }
            }

            // check if en passant move is possible on next turn
            if(player == 0 && piece.getText() == "wp" && startRow == 6 && endRow == 4)
                enPassant = true;
            if(player == 1 && piece.getText() == "bp" && startRow == 1 && endRow == 3)
                enPassant = true;

            // checks for check and checkmate
            if(check(piece, board) == 1) {
                Toast.makeText(PlayGame.this, "Check", Toast.LENGTH_SHORT).show();
                checkPiece = piece;
            }
            else if(check(piece, board) == 2) {
                //System.out.println("\nCheckmate\n");
                if(player == 0)
                    //System.out.println("\nWhite wins");
                    return 0;
                else
                    //System.out.println("\nBlack wins");
                    return 0;
            }
        }
        // checks for en Passant move
        else if(enPassant == true && board.pieces[startRow][endCol] != null) {
            if(player == 0 && board.pieces[startRow][startCol].getText() == "wp") {
                if(board.pieces[startRow][endCol].getText() == "bp") {
                    board.movePiece(piece, endRow, endCol);
                    board.removePiece(startRow, endCol);
                    enPassantMove = true;
                    enPassant = false;
                    return 2;
                }
            } else if(piece != null && board.pieces[startRow][startCol].getText() == "bp") {
                if(board.pieces[startRow][endCol].getText() == "wp") {
                    board.movePiece(piece, endRow, endCol);
                    board.removePiece(startRow, endCol);
                    enPassantMove = true;
                    enPassant = false;
                    return 2;
                }
            }
            enPassant = false;
            enPassantMove = false;
        }
        // checks for castling move
        else if(piece != null && piece.getText().charAt(1) == 'K' && piece.hasMoved == false) {
            if(startRow == endRow && startCol+2 == endCol && board.pieces[endRow][7] != null) {
                if(board.pieces[endRow][7].hasMoved == false) {
                    board.movePiece(piece, endRow, endCol);
                    Piece rook = board.pieces[endRow][7];
                    board.movePiece(rook, endRow, 5);
                    castleMove = true;
                    return 2;
                } else {
                    //System.out.println("\nIllegal move, try again\n");
                    return 1;
                }
            } else if(startRow == endRow && startCol-2 == endCol && board.pieces[endRow][0] != null) {
                if(board.pieces[endRow][0].hasMoved == false) {
                    board.movePiece(piece, endRow, endCol);
                    Piece rook = board.pieces[endRow][0];
                    board.movePiece(rook, endRow, 3);
                    castleMove = true;
                    return 2;
                } else {
                    //System.out.println("\nIllegal move, try again\n");
                    return 1;
                }
            }
        }

        if (!illegal)
            return 2;
        else
            return 1;
    }

    /**
     * check determines if a moved piece puts its opponent's king in check
     * @param piece	is the piece that was just moved
     * @param board is the board with all of its pieces
     * @return
     */
    static int check(Piece piece, Board board) {
        // fields to represent information for opponent's king
        Piece king = null;
        String kingText;
        int kingRow = 0, kingCol = 0;

        if(piece.getText().charAt(0) == 'w')
            kingText = "bK";
        else
            kingText = "wK";

        // find opponent's king and store it along with its location
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board.pieces[i][j] != null) {
                    if(board.pieces[i][j].getText().equals(kingText)) {
                        king = board.pieces[i][j];
                        kingRow = i;
                        kingCol = j;
                    }
                }
            }
        }


        if(piece.isValidMove(board, kingRow, kingCol)) {
            if(checkmate(board, king, piece)) {
                return 2;
            }
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * checkmate determines if a moved piece put its opponent's king in checkmate
     * @param board	is the board and all of its pieces
     * @param king	is the opponent's king
     * @param piece	is the piece that was moved
     * @return
     */
    static boolean checkmate(Board board, Piece king, Piece piece) {

        int row = king.getRow();
        int col = king.getCol();
        int pRow = piece.getRow();
        int pCol = piece.getCol();
        String pText = piece.getText();
        Piece p = null;
        boolean w = false;
        int n = 5;

    /*  N KEY:
        0 = knight
        1 = pawn
        2 = queen
        3 = bishop
        4 = rook
     */

        if (king.getText().charAt(0) == 'w')
            w = true;

        if (piece instanceof Knight)
            n = 0;
        if (piece instanceof Pawn)
            n = 1;
        if (piece instanceof Queen)
            n = 2;
        if (piece instanceof Bishop)
            n = 3;
        if (piece instanceof Rook)
            n = 4;

        if (n == 5)
            System.out.println("ERROR: N ISN'T THE CORRECT VALUE");



        //Whenever a king has a valid move, count is incremented and if a opposing piece can attack that valid move,
        //Check every route for every valid move the king has and determine if a friendly piece can go there
        //count is decremented. count == 0 -> checkmate
        int count = 0;
        //Stores every location that leads to an enemy piece
        boolean[][] storage = new boolean[8][8];

        if (row > 0 && col > 0 && king.isValidMove(board,row-1,col-1)){    //topleft
            board.pieces[pRow][pCol] = null;
            count++;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row-1, col-1)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row-1, col-1)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (row > 0 && king.isValidMove(board,row-1,col)) {       //topmid
            board.pieces[pRow][pCol] = null;
            count++;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row-1, col)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row-1, col)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (row > 0 && col < 7 && king.isValidMove(board,row-1,col+1)) {   //topright
            board.pieces[pRow][pCol] = null;
            count++;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row-1, col+1)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row-1, col+1)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (col > 0 && king.isValidMove(board,row,col-1)) {       //midleft
            board.pieces[pRow][pCol] = null;
            count++;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row, col-1)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row, col-1)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (col < 7 && king.isValidMove(board,row,col+1)) {       //midright
            board.pieces[pRow][pCol] = null;
            count++;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row, col+1)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row, col+1)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (row < 7 && col > 0 && king.isValidMove(board,row+1,col-1)) {   //bottomleft
            count++;
            board.pieces[pRow][pCol] = null;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row+1, col-1)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row+1, col-1)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (row < 7 && king.isValidMove(board,row+1,col)) {       //bottommid
            count++;
            board.pieces[pRow][pCol] = null;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row+1, col)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row+1, col)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (row < 7 && col < 7 && king.isValidMove(board,row+1,col+1)) {   //bottomright
            count++;
            board.pieces[pRow][pCol] = null;
            for (int i = 0; i < 16; i++){
                if (w){
                    if (board.list[1][i].isValidMove(board, row+1, col+1)){
                        count--;
                        switch (n){
                            case 0: {
                                p = new Knight(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 1: {
                                p = new Pawn(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 2: {
                                p = new Queen(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 3: {
                                p = new Bishop(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                            case 4: {
                                p = new Rook(pRow, pCol, pText);
                                board.addPiece(p);
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (board.list[0][i].isValidMove(board, row+1, col+1)){
                    count--;
                    switch (n){
                        case 0: {
                            p = new Knight(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 1: {
                            p = new Pawn(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 2: {
                            p = new Queen(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 3: {
                            p = new Bishop(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                        case 4: {
                            p = new Rook(pRow, pCol, pText);
                            board.addPiece(p);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        //Black king is in check -> w = false
        //Check knight and Pawn, then 2D, then 1D
        //in list, 1 -> white,
        if (count == 0){
            int count2 = 0;

            if (w){
                for (int i = 0; i < 16; i++){
                    if (board.list[1][i].isValidMove(board, row, col))
                        count2++;
                    if (count2 == 2)
                        return true;
                }
            }
            else {
                for (int i = 0; i < 16; i++){
                    if (board.list[0][i].isValidMove(board, row, col))
                        count2++;
                    if (count2 == 2)
                        return true;
                }
            }

            boolean dontRun = false;
            boolean skipToEnd = false;

            if (n == 0 || n == 1){
                storage[piece.getRow()][piece.getCol()] = true;
                dontRun = true;
            }

            //Black king in check -> w = false
            if (!dontRun){
                outerloop:
                for (int j = row+1, k = col+1; j < 8; j++, k++){
                    if(k == 8)
                        break;
                        // Bottom-Right

                    else if (board.pieces[j][k] == null)
                        storage[j][k] = true;
                    else if (board.pieces[j][k] instanceof Queen || board.pieces[j][k] instanceof Bishop){
                        if (w){
                            if (board.pieces[j][k].getText().charAt(0) == 'b'){
                                storage [j][k] = true;
                                dontRun = true;
                                break outerloop;
                            }
                        } else if (board.pieces[j][k].getText().charAt(0) == 'w'){
                            storage [j][k] = true;
                            dontRun = true;
                            break outerloop;
                        }
                    }

                    else {
                        break outerloop;
                    }

                }
                if(!dontRun)
                    clear(storage);
            }
            if (!dontRun){
                outerloop:
                for (int j = row+1, k = col-1; j < 8; j++, k--){
                    if(k == -1)
                        break;
                        // Bottom-Left
                    else if (board.pieces[j][k] == null)
                        storage[j][k] = true;
                    else if (board.pieces[j][k] instanceof Queen || board.pieces[j][k] instanceof Bishop){
                        if (w){
                            if (board.pieces[j][k].getText().charAt(0) == 'b'){
                                storage [j][k] = true;
                                dontRun = true;
                                break outerloop;
                            }
                        } else if (board.pieces[j][k].getText().charAt(0) == 'w'){
                            storage [j][k] = true;
                            dontRun = true;
                            break outerloop;
                        }
                    }

                    else {
                        break outerloop;
                    }

                }
                if(!dontRun)
                    clear(storage);
            }

            if (!dontRun){
                outerloop:
                for (int j = row-1, k = col+1; j >= 0; k++, j--){
                    // Top-Right
                    if(k == 8)
                        break;
                    else if (board.pieces[j][k] == null)
                        storage[j][k] = true;
                    else if (board.pieces[j][k] instanceof Queen || board.pieces[j][k] instanceof Bishop){
                        if (w){
                            if (board.pieces[j][k].getText().charAt(0) == 'b'){
                                storage [j][k] = true;
                                dontRun = true;
                                break outerloop;
                            }
                        } else if (board.pieces[j][k].getText().charAt(0) == 'w'){
                            storage [j][k] = true;
                            dontRun = true;
                            break outerloop;
                        }
                    }

                    else {
                        break outerloop;
                    }

                }
                if(!dontRun)
                    clear(storage);
            }
            if (!dontRun){
                outerloop:
                for (int j = row-1, k = col-1; j >= 0; j--, k--){
                    // Top-Left
                    if(k == -1)
                        break;

                    else if (board.pieces[j][k] == null)
                        storage[j][k] = true;
                    else if (board.pieces[j][k] instanceof Queen || board.pieces[j][k] instanceof Bishop){
                        if (w){
                            if (board.pieces[j][k].getText().charAt(0) == 'b'){
                                storage [j][k] = true;
                                dontRun = true;
                                break outerloop;
                            }
                        } else if (board.pieces[j][k].getText().charAt(0) == 'w'){
                            storage [j][k] = true;
                            dontRun = true;
                            break outerloop;
                        }
                    }

                    else {
                        break outerloop;
                    }

                }
                if(!dontRun)
                    clear(storage);
            }

            if (!dontRun){
                for (int j = row; j >= 0; j--){          // Up
                    if (board.pieces[j][col] != null)
                        storage[j][col] = true;
                    else if (board.pieces[j][col] instanceof Queen || board.pieces[j][col] instanceof Rook){
                        if (w){
                            if (board.pieces[j][col].getText().charAt(0) == 'b'){
                                storage [j][col] = true;
                                dontRun = true;
                                break;
                            }
                        } else if (board.pieces[j][col].getText().charAt(0) == 'w'){
                            storage [j][col] = true;
                            dontRun = true;
                            break;
                        }
                        else {
                            if(j == row)
                                break;
                            dontRun = true;
                            break;
                        }
                    }
                }
                if(!dontRun)
                    clear(storage);
            }
            if (!dontRun){
                for (int j = col; j <= 7; j++){          // Right
                    if (board.pieces[row][j] != null)
                        storage[row][j] = true;
                    else if (board.pieces[row][j] instanceof Queen || board.pieces[row][j] instanceof Rook){
                        if (w){
                            if (board.pieces[row][j].getText().charAt(0) == 'b'){
                                storage [row][j] = true;
                                dontRun = true;
                                break;
                            }
                        } else if (board.pieces[row][j].getText().charAt(0) == 'w'){
                            storage [row][j] = true;
                            dontRun = true;
                            break;
                        }
                        else {
                            if(j == col)
                                break;
                            dontRun = true;
                            break;
                        }
                    }
                }
                if(!dontRun)
                    clear(storage);
            }

            if (!dontRun){
                for (int j = row; j <= 7; j++){          // Down
                    if (board.pieces[j][col] != null)
                        storage[j][col] = true;
                    else if (board.pieces[j][col] instanceof Queen || board.pieces[j][col] instanceof Rook){
                        if (w){
                            if (board.pieces[j][col].getText().charAt(0) == 'b'){
                                storage [j][col] = true;
                                dontRun = true;
                                break;
                            }
                        } else if (board.pieces[j][col].getText().charAt(0) == 'w'){
                            storage [j][col] = true;
                            dontRun = true;
                            break;
                        }
                        else {
                            if(j == row)
                                break;
                            dontRun = true;
                            break;
                        }
                    }
                }
                if(!dontRun)
                    clear(storage);
            }

            if (!dontRun){
                for (int j = col; j >= 0; j--){          // Left
                    if (board.pieces[row][j] != null)
                        storage[row][j] = true;
                    else if (board.pieces[row][j] instanceof Queen || board.pieces[row][j] instanceof Rook){
                        if (w){
                            if (board.pieces[row][j].getText().charAt(0) == 'b'){
                                storage [row][j] = true;
                                dontRun = true;
                                break;
                            }
                        } else if (board.pieces[row][j].getText().charAt(0) == 'w'){
                            storage [row][j] = true;
                            dontRun = true;
                            break;
                        }
                        else {
                            if(j == col)
                                break;
                            dontRun = true;
                            break;
                        }
                    }
                }
                if(!dontRun)
                    clear(storage);
            }

            //Black king in check, w = false

            if (cmHelper(board,storage,w))
                return false;
            else
                return true;
        }

        return false;
    }

    /**
     * clear initializes a boolean array to false
     * @param b is the array to be cleared
     */
    static void clear(boolean[][] b){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                b[i][j] = false;
            }
        }
    }

    /**
     * cmHelper checks if every allied piece can take you out of check
     * @param board	is the game board with all its pieces
     * @param b		is a 2D boolean array
     * @param w		is the opponent's color
     * @return
     */
    static boolean cmHelper(Board board, boolean[][] b, boolean w){

        boolean guarded = false;
        if (w){
            for (int i = 0; i < 8; i++){
                for (int j = 0; j < 8; j++){
                    if (b[i][j]){
                        for (int k = 0; k < 16; k++){
                            if (board.list[0][k].isValidMove(board, i, j) && !(board.list[0][k] instanceof King))
                                return true;
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < 8; i++){
                for (int j = 0; j < 8; j++){
                    if (b[i][j]){
                        for (int k = 0; k < 16; k++){
                            if (board.list[1][k].isValidMove(board, i, j) && !(board.list[1][k] instanceof King))
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void changeTurn() {
        player++;
        player %= 2;

        if (turn.getText().toString().compareTo(getResources().getString(R.string.white_turn)) == 0)
            turn.setText(getResources().getString(R.string.black_turn));
        else
            turn.setText(getResources().getString(R.string.white_turn));
    }
}