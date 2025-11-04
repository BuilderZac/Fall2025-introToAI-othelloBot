//This is our implimentation of a the othello game board.
public class board {
   public int[][] curBoard;
   int size = 8;
   String rows = "ABCDEFGH"; // Needed for moveReader

   // A two int coord Pair
   public record CoordPair(int x, int y) {
   }

   board() {
      curBoard = new int[size][size];
      for (int i = 0; i < size; i++) {
         for (int j = 0; j < size; j++) {
            curBoard[i][j] = 0;
         }
      }
   }

   /**
    * @return void prints the current board in systemOut
    **/
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

   /**
    * @return void sets a blank board to the starting board state
    **/
   public void setBoard() {
      curBoard[3][3] = 1;
      curBoard[3][4] = 2;
      curBoard[4][4] = 1;
      curBoard[4][3] = 2;
   }

   /**
    * @exception String A move string in the format like A4 or f2
    * @return CoordPair A formated CoordPair that is in acurate to the grid
    **/
   public CoordPair moveReader(String move) {
      CoordPair out = new CoordPair(
            rows.indexOf(Character.toLowerCase(move.charAt(0))) * 10,
            Integer.parseInt(move.substring(1, 2)));
      return out;
   }

   /**
    * @exception CoordPair It needs an int CoordPair
    * @exception Boolean if range checking is needed
    * @exception int color of who is try to move
    * @return Boolean if the move can be done
    **/
   public boolean moveChecker(CoordPair move, boolean checkRange, int color) {
      // checks empty
      return curBoard[move.x][move.y] == 0 &&
      // checks in bounds if needed for example AI wont try a out of bounds move
            !checkRange || // This uses short circuit logic to reduce checks
            (move.x > 0 && move.x < size && move.y > 0 && move.x < size) &&
            // Checks valid addition
                  true; // WIP
   }

}
