package Model;

import java.util.List;

public class TextInterface {

    int pieceNumber = 1;
    int spaceNumber = 1;
    String blank = "\u2588\u2588\u2588\u2588\u2588\u2588\u2588";
    public void printBoard(List<Piece> piece) {
        for (int i = 0;  i < 4; i ++) {


            //System.out.println( "_________________________________________________");
            System.out.println(blank + numSpace() + blank + numSpace() + blank + numSpace() + blank + numSpace() + "|");
            System.out.println(blank + getPiece(piece) + blank + getPiece(piece) + blank +
                    getPiece(piece) + blank + getPiece(piece) + "|");
            System.out.println(blank + "       " + blank + "       " + blank + "       " + blank + "       |");
            //System.out.println( "__________________________________________________");
            System.out.println(numSpace() + blank + numSpace() + blank + numSpace() + blank + numSpace() + blank);
            System.out.println(getPiece(piece) + blank + getPiece(piece) + blank +
                    getPiece(piece) + blank + getPiece(piece) + blank);
            System.out.println("       " + blank + "       " + blank + "       " + blank + "       " + blank);
        }
        pieceNumber = 1;
        spaceNumber = 1;
    }

    private String getPiece(List<Piece> pieces) {

        String pieceStr = "";
        Piece currentPiece = pieces.get(pieceNumber - 1);
        if (currentPiece.pieceType == PieceType.RED) {
            if (currentPiece.isKing) {
                pieceStr = "   R   ";
            } else {
                pieceStr = "   r   ";
            }
        } else if (currentPiece.pieceType == PieceType.BLACK){
            if (currentPiece.isKing) {
                pieceStr = "   B   ";
            } else {
                pieceStr = "   b   ";
            }
        } else {
            pieceStr = "       ";
        }
        pieceNumber++;
        return pieceStr;
    }

    private String numSpace() {
        String retVal = "";
        if (spaceNumber < 10) {
            retVal = spaceNumber + "      ";
        } else {
            retVal = spaceNumber + "     ";
        }
        spaceNumber++;
        return retVal;
    }
}
