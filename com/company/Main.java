package com.company;

import Model.Board;
import Model.PieceType;
import View.TextInterface;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Board board = new Board();
        TextInterface ti = new TextInterface();
        ti.printBoard(board.getPieces());
        Scanner scanner = new Scanner(System.in);

        while(board.getWinner() == PieceType.EMPTY) {

            try{
                System.out.print("\nStart: ");
                int start = scanner.nextInt();
                System.out.print("End: ");
                int end = scanner.nextInt();
                if(!board.makeMove(start, end)) {
                    System.out.println("Invalid move. Try again");
                } else {
                    ti.printBoard(board.getPieces());
                }
            } catch (Exception ex) {
                System.out.println("Enter a number from 1 to 32.");
            }

        }

        if(board.getWinner() == PieceType.BLACK) {
            System.out.println("BLACK WINS!");
        } else {
            System.out.println("RED WINS!");
        }
    }
}

