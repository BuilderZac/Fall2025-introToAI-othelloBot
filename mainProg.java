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
         
         int whiteScore = 0;
         int blackScore = 0;
         for (int i = 0; i < 8; i++)
         {
            for (int j = 0; j < 8; j++)
            {
               if (curBoard.curBoard[i][j] == 1)
               {
                  whiteScore++;
               }
               else if (curBoard.curBoard[i][j] == 2)
               {
                  blackScore++;
               }
            }
         }
         System.out.println("Score - WHITE: " + whiteScore + " | BLACK: " + blackScore);
         System.out.println();

         if (turn == 1) {
            System.out.print("Enter player move: ");
            boolean moveAccepted = false;
            while (!moveAccepted) {
               moveAccepted = curBoard.makeMove(curBoard.moveReader(scan.next()), color);
               if (!moveAccepted) {
                  System.out.print("Move rejected. Please enter a valid move in the format B3 or e7:");
               }
            }
            turn = 2;
         } else {
            board.CoordPair aiMove = model.findBestMove(curBoard, 3 - color);

            if (aiMove == null)
            {
               System.out.println("AI has no legal moves. Passing turn.");
            }
            else
            {
               curBoard.makeMove(aiMove, 3 - color);
               System.out.println("AI plays: " + model.moveToString(aiMove));
            }
            turn = 1;
         }
      }
   }
}
