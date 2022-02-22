package Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {

    List<Node> nodes = new ArrayList<>(33);
    final int SIZE_OF_BOARD = 33;
    final int EDGE_JUMP = 8;
    PieceType turn = PieceType.BLACK;
    int forcedJumpLocation = 0;
    int blackPieces = 12;
    int redPieces = 12;
    PieceType winner = PieceType.EMPTY;
    boolean forcedJump = false;

    public Board() {
        nodes.add(null);
        for (int i = 1; i < 5; i++) {
            Set<Integer> upNodes = new HashSet<>();
            upNodes.add(i + 4);
            if (i != 4) {
                upNodes.add(i + 5);
            }
            nodes.add(new Node(PieceType.BLACK, upNodes,null, false, i));
        }
        int sideCounter = 0;
        int adjustCount = 0;
        int adjustment = 2;
        for (int i = 5; i < 29; i++) {
            Set<Integer> upNodes = new HashSet<>();
            Set<Integer> downNodes = new HashSet<>();
            upNodes.add(i + 4);
            downNodes.add(i - 4);

            boolean jumpable = false;
            if (sideCounter == 7) {
                sideCounter = 0;
            } else {
                if (sideCounter != 0) {
                    upNodes.add(i + 5 - adjustment);
                    downNodes.add(i - 3 - adjustment);
                    jumpable = true;
                }
                sideCounter++;
            }
            PieceType pieceType = getPieceType(i);
            nodes.add(new Node(pieceType, upNodes, downNodes, jumpable, i));

            adjustCount++;
            if(adjustCount == 4) {
                adjustment = (-(adjustment - 1)) + 1; //Toggle between 0 and 2
                adjustCount = 0;
            }
        }

        for (int i = 29; i < 34; i ++) {
            Set<Integer> downNodes = new HashSet<>();
            downNodes.add(i - 4);
            if (i != 29) {
                downNodes.add(i - 5);
            }
            nodes.add(new Node(PieceType.RED, null, downNodes, false, i));
        }
        //boardTest();
    }
    public PieceType getWinner() {
        return winner;
    }

    public boolean makeMove(int from, int to) {

        if (from  < 1 || to < 1) {
            System.out.println("Square does not exist.");
            return false;
        }
        if (from > 32 || to > 32) {
            System.out.println("Square does not exist.");
            return false;
        }

        Node startNode = nodes.get(from);
        Node toNode = nodes.get(to);

        Piece startPiece = startNode.piece;
        Piece endSpace = nodes.get(to).piece;
        if(startPiece.pieceType != turn) {
            System.out.println("Your piece is not on that square.");
            return false;
        }
        if (endSpace.pieceType != PieceType.EMPTY) {
            System.out.println("Your piece can not land there.");
            return false;
        }

        if (forcedJumpLocation > 0) {
            if (forcedJumpLocation != from) {
                System.out.println("You still have a jump.");
                return false;
            }
        }

        if(forcedJumpLocation > 0 || forcedJump) {
            int jumpedLocation = -1;
            if ( (jumpedLocation = isValidJump(startNode, toNode)) > 0) {
                finalizeJump(startNode, toNode, nodes.get(jumpedLocation));
                checkWin();
                return true;
            } else {
                System.out.println("You have a jump!");
                return false;
            }
        } else if (!isAdjacentSpace(startNode, to)) {
            System.out.println("You must move to an adjacent square.");
            return false;
        }
        //King, king moves check, check for forced jump correctly
        finalizeMove(startNode, toNode);
        checkWin();
        return true;
    }

    private void checkWin() {
        if(redPieces == 0){
            winner = PieceType.RED;
        } else if (blackPieces == 0) {
            winner = PieceType.BLACK;
        } else if (hasJump()) {
            forcedJump = true;
            return;
        }

        for (int i = 1; i < SIZE_OF_BOARD; i++) {
            Node currentNode = nodes.get(i);
            if (currentNode.piece.pieceType != turn){
                continue;
            }
            if (pieceHasMove(currentNode)) {
                return;
            }
        }
        if (turn == PieceType.RED) { //Turn has already changed
            winner = PieceType.BLACK;
        } else {
            winner = PieceType.RED;
        }
    }

    private boolean pieceHasMove(Node currentNode) {

        if (currentNode.piece.isKing) {
            for (int i : currentNode.upNodes) {
                if (nodes.get(i).piece.pieceType == PieceType.EMPTY) {
                    return true;
                }
            }
            for (int i : currentNode.downNodes) {
                if (nodes.get(i).piece.pieceType == PieceType.EMPTY) {
                    return true;
                }
            }
        } else if (currentNode.piece.pieceType == PieceType.BLACK) {
           for (int i : currentNode.upNodes) {
               if (nodes.get(i).piece.pieceType == PieceType.EMPTY) {
                   return true;
               }
           }
        } else if (currentNode.piece.pieceType == PieceType.RED) {
            for (int i : currentNode.downNodes) {
                if (nodes.get(i).piece.pieceType == PieceType.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private void finalizeJump(Node startNode, Node toNode, Node jumpedNode) {

        if (jumpedNode.piece.pieceType == PieceType.BLACK) {
            blackPieces--;
        } else {
            redPieces--;
        }

        jumpedNode.piece  = new Piece(PieceType.EMPTY);
        toNode.piece = new Piece(turn, startNode.piece.isKing);
        startNode.piece = new Piece(PieceType.EMPTY);

        if (!toNode.piece.isKing) {
            if(madeKing(toNode)) {
                forcedJumpLocation = 0;
                turn = switchType(turn);
                return;
            }
        }

        Piece piece = toNode.piece;
        boolean moreJumps = false;
        if(piece.isKing) {
            moreJumps = pieceHasJump(toNode.location, true);
            if(!moreJumps) {
                moreJumps = pieceHasJump(toNode.location, false);
            }
        } else if (piece.pieceType == PieceType.BLACK) {
            moreJumps = pieceHasJump(toNode.location, true);
        } else {//Is Red
            moreJumps = pieceHasJump(toNode.location, false);
        }
        if (moreJumps) {
            forcedJumpLocation = toNode.location;
        } else {
            forcedJumpLocation = 0;
            turn = switchType(turn);
        }
    }

    private boolean madeKing(Node toNode) {
        if(turn == PieceType.BLACK) {
            if (toNode.location > 28) {
                toNode.piece.isKing = true;
                return true;
            }
        } else if (toNode.location < 5){
            toNode.piece.isKing = true;
            return true;
        }
        return false;
    }

    private int isValidJump(Node startNode, Node toNode) {
        if (startNode.piece.isKing){
            if (startNode.location - toNode.location < 0) {
                return checkJump(startNode, toNode, true);
            }
            return checkJump(startNode, toNode, false);
        } else if (startNode.piece.pieceType == PieceType.BLACK){
            return checkJump(startNode, toNode, true);
        } else { //Is Red
            return checkJump(startNode, toNode, false);
        }
    }

    private int checkJump(Node startNode, Node toNode, boolean upJump){
        Set<Integer> middleNodesFromStart = startNode.upNodes;
        Set<Integer> middleNodesFromEnd = toNode.downNodes;
        if (!upJump) {
            middleNodesFromStart = startNode.downNodes;
            middleNodesFromEnd = toNode.upNodes;
        }
        if (middleNodesFromEnd == null) {
            return -1;
        }
        if (middleNodesFromStart == null) {
            return -1;
        }
        for (int i : middleNodesFromStart) {
            for (int j : middleNodesFromEnd) {
                if (i == j) {
                    if( Math.abs(startNode.location - toNode.location) == EDGE_JUMP) {
                        System.out.println("You must jump in a straight line.");
                        return -1;
                    }
                    if(checkJumpedSpace(nodes.get(i))){
                        return i;
                    }
                }
            }
        }
        System.out.println("Not a valid jump.");
        return -1;
    }

    private boolean checkJumpedSpace(Node jumpedNode) {
        PieceType oppType = switchType(turn);
        Piece jumpedPiece = jumpedNode.piece;
        if (jumpedPiece.pieceType != oppType) {
            System.out.println("You cannot jump your own piece!");
            return false;
        }
        if (!jumpedNode.jumpable) {
            System.out.println("You cannot jump a piece on the edge.");
            return false;
        }
        forcedJump = false;
        return true;
    }


    private void finalizeMove(Node startNode, Node toNode) {
        toNode.piece = new Piece(turn, startNode.piece.isKing);
        startNode.piece = new Piece(PieceType.EMPTY);
        madeKing(toNode);
        turn = switchType(turn);
    }

    private boolean isAdjacentSpace(Node startNode, int to) {
        if (startNode.piece.isKing) {
            boolean isAdjacent = false;
            if (startNode.upNodes != null) {
                isAdjacent = startNode.upNodes.contains(to);
            }
            if (!isAdjacent) {
                if (startNode.downNodes != null) {
                    isAdjacent = startNode.downNodes.contains((to));
                }
            }
            return isAdjacent;
        }
        if (turn == PieceType.BLACK)  {
            return startNode.upNodes.contains(to);
        } else { //Is red
            return startNode.downNodes.contains(to);
        }
    }
    private PieceType switchType(PieceType toSwitch) {
        if (toSwitch == PieceType.BLACK) {
            return PieceType.RED;
        } else {
            return PieceType.BLACK;
        }
    }

    private boolean hasJump() {

        boolean upJump = true;

        for (int i = 1; i < SIZE_OF_BOARD; i++) {
            Piece currentPiece = nodes.get(i).piece;

            if (currentPiece.pieceType != turn) {
                continue;
            }
            if (currentPiece.isKing) {
                if (pieceHasJump(i, upJump) || pieceHasJump(i, !upJump)) {
                    return true;
                }
            } else if (turn == PieceType.BLACK) {
                if (pieceHasJump(i, upJump)) {
                    return true;
                }
            } else {
                if(pieceHasJump(i, !upJump)) {
                    return true;
                }
            }
        }
        return false;
    }
    /*
     * Checks if there is a jump at a piece, in one direction or another.
     * If a node connected to the location is jumpable, has the opposite piece type,
     * And if the
     */
    private boolean pieceHasJump(int location, boolean upJump) {
        Node currentNode = nodes.get(location);
        PieceType oppType = switchType(turn);

        Set<Integer> jumpedLayer = currentNode.upNodes;
        if (!upJump) {
            jumpedLayer = currentNode.downNodes;
        }
        if (jumpedLayer == null) {
            return false;
        }
        for (Integer i : jumpedLayer) {
            Node jumpedNode = nodes.get(i);
            if (!jumpedNode.jumpable) {
                continue;
            }
            if (jumpedNode.piece.pieceType != oppType) {
                continue;
            }

            Set<Integer> landingLayer = jumpedNode.upNodes;
            if (!upJump) {
                landingLayer = jumpedNode.downNodes;
            }
            for (Integer j : landingLayer) {
                Node landingNode = nodes.get(j);
                if (Math.abs(currentNode.location - landingNode.location) == EDGE_JUMP) {
                    continue;
                }
                if (landingNode.piece.pieceType == PieceType.EMPTY){
                    return true;
                }
            }
        }

        return false;
    }

    private PieceType getPieceType(int location) {
        if(location < 13) {
            return PieceType.BLACK;
        } else if(location > 20) {
            return PieceType.RED;
        }
        return PieceType.EMPTY;
    }

    private class Node {
        Set<Integer> upNodes = new HashSet<>(2);
        Set<Integer> downNodes = new HashSet<>(2);
        Piece piece;
        boolean jumpable;
        int location;
        Node(PieceType pieceType, Set<Integer> upNodes, Set<Integer> downNodes, boolean jumpable, int location) {
            piece = new Piece(pieceType);
            this.upNodes = upNodes;
            this.downNodes = downNodes;
            this.jumpable = jumpable;
            this.location = location;
        }
    }

    public List<Piece> getPieces() {
        List<Piece> pieceTypes = new ArrayList<>();

        for (int i = 1; i < SIZE_OF_BOARD; i++) {
            pieceTypes.add(nodes.get(i).piece);
        }
        return pieceTypes;
    }
    private void boardTest() {
        for (int i = 1; i <= 32; i++) {
            System.out.println(i);
            Node currentNode = nodes.get(i);
            if(currentNode.piece.pieceType == PieceType.BLACK) {
                System.out.println("Black");
            } else if (currentNode.piece.pieceType == PieceType.RED) {
                System.out.println("Red");
            } else if (currentNode.piece.pieceType == PieceType.EMPTY) {
                System.out.println("Empty");
            }
            System.out.print("Up: ");
            if (currentNode.upNodes != null) {
                for (int j : currentNode.upNodes) {
                    System.out.print(j + ", ");
                }
            }
            System.out.print("\nDown: ");
            if (currentNode.downNodes != null) {
                for (int j : currentNode.downNodes) {
                    System.out.print(j + ", ");
                }
            }
            if (currentNode.jumpable) {
                System.out.println(("\nJumpable"));
            } else {
                System.out.println("\nNO");
            }
            System.out.println("");
        }
    }

}
