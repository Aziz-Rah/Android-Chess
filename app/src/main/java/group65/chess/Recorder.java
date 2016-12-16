package group65.chess;

public class Recorder {

    public int[][] startList = new int[2][1000];
    public int[][] endList = new int[2][1000];
    String gameTitle;

    public Recorder(){

        for (int i = 0; i < startList.length; i++){
            startList[0][i] = -1;
            startList[1][i] = -1;
            endList[0][i] = -1;
            endList[1][i] = -1;
        }
    }

    public void addToRecorder(int startRow, int startCol, int endRow, int endCol){

        startList[0][getIndex(startList)] = startRow;
        startList[1][getIndex(startList)] = startCol;
        endList[0][getIndex(endList)] = endRow;
        endList[1][getIndex(endList)] = endCol;

    }

    private int getIndex(int[][] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[0][i] == -1)
                return i;
        }
        return 0;
    }
}
