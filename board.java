//This is our implimentation of a the othello game board.
public class board {
   public int[][] curBoard;
   int size = 8;
   String rows = "ABCDEFGH"; // Needed for moveReader

   board() {
      curBoard = new int[size][size];
      for (int i = 0; i < size; i++) {
         for (int j = 0; j < size; j++) {
            curBoard[i][j] = 0;
         }
      }
   }

   public void printBoard() {
      // Prints the x axis & letters
      System.out.print("Board Print:\n ABCDEFGH\n");
      for (int i = 1; i < size + 1; i++) {
         System.out.print(i);
         // Prints the y axis & numbers
         for (int j = 0; j < size; j++) {
            if (curBoard[j][i - 1] == 0) {
               System.out.print("#");
            } else if (curBoard[j][i - 1] == 1) {
               System.out.print("W");
            } else {
               System.out.print("B");
            }
         }
         System.out.print("\n");
      }
   }

   public void setBoard() {
      curBoard[3][3] = 1;
      curBoard[3][4] = 2;
      curBoard[4][4] = 1;
      curBoard[4][3] = 2;
   }

   public int moveReader(String move) {
      int out = rows.indexOf(Character.toLowerCase(move.charAt(0))) * 10;
      out += Integer.parseInt(move.substring(1, 2));
      return out;
   }

   public boolean moveChecker(int move) {
      if (curBoard[move / 10][move % 10] != 0) {
         return false;
      }
      return true;
   }
}
