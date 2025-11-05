//This is the main program to execute the model & run the game.

import java.util.Scanner;

public class mainProg {
   public static void main(String[] args) {
      // Turn order logic. Any arg makes player second
      int turn = 1;
      int color = 2;
      if (args.length >= 1) {
         turn = 2;
         color = 1;
      }

      // Sets board
      board curBoard = new board();
      curBoard.setBoard();

      // Set up scanner for input
      Scanner scan = new Scanner(System.in);

      // Main game loop
      while (true) {

         curBoard.printBoard();
         if (turn == 1) {
            System.out.print("Enter player move: ");
            boolean moveAccepted = false;
            while (!moveAccepted) {
               moveAccepted = curBoard.makeMove(curBoard.moveReader(scan.next()), color);
               if (!moveAccepted) {
                  System.out.print("Move rejected. Please enter a valid move in the format B3 or e7:");
               }
            }
            color = turn;
            turn = 2;
         } else {
            // Impliment model here as opposite as player turn
         }

      }
   }
}
