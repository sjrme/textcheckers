package Model;

public class Piece {
    PieceType pieceType;
    boolean isKing = false;
    Piece (PieceType pieceType) {
        this.pieceType = pieceType;
    }
    Piece (PieceType pieceType, boolean isKing) {
        this.pieceType = pieceType;
        this.isKing = isKing;
    }

    public boolean isKing() {
        return isKing;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

}