//This is our implimentation of a the othello game board.

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class board {
   public int[][] curBoard;
   int size = 8;
   String rows = "abcdefgh"; // Needed for moveReader

   // A two int coord Pair
   public record CoordPair(int x, int y) {
   }

   /**
    * Constructs a new Othello board with all cells set to empty.
    * Initializes an 8x8 board.
    */
   board() {
      curBoard = new int[size][size];
      for (int i = 0; i < size; i++) {
         for (int j = 0; j < size; j++) {
            curBoard[i][j] = 0;
         }
      }
   }

   /**
    * Prints the current state of the board to standard output, displaying columns
    * as letters (A-H) and rows as numbers (1-8). Empty cells show as '#', white as
    * 'W', black as 'B'.
    */
   public void printBoard() {
      // Prints the x axis & letters
      for (int i = size; i > 0; i--) {
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
         System.out.println();
      }
      System.out.print(" ABCDEFGH\n");
   }

   /**
    * Resets the board to the standard starting position for an Othello game
    */
   public void setBoard() {
      curBoard[3][3] = 1;
      curBoard[3][4] = 2;
      curBoard[4][4] = 1;
      curBoard[4][3] = 2;
   }

   /**
    * Parses a move string (e.g., "A4" or "f2") into a board coordinate pair.
    *
    * @param move The move string in the format letter+number (e.g., "A4").
    * @return A CoordPair representing the board coordinates for the move.
    */
   public CoordPair moveReader(String move) {
      CoordPair out = new CoordPair(
            rows.indexOf(Character.toLowerCase(move.charAt(0))),
            Integer.parseInt(move.substring(1, 2)) - 1);
      return out;
   }

   /**
    * Checks if a move is valid for the specified color.
    *
    * @param move       The coordinate pair for the proposed move.
    * @param checkRange If true, checks that the move is within bounds.
    * @param color      The color making the move (1 for white, 2 for black).
    * @return True if the move is valid, false otherwise.
    */
   public boolean moveChecker(CoordPair move, boolean checkRange, int color) {
      // check in bounds
      if (checkRange &&
            (move.x < 0 || move.x >= size || move.y < 0 || move.y >= size)) {
         return false;
      }

      // check empty
      if (curBoard[move.x][move.y] != 0) {
         return false;
      }
      // check if move captures in any direction
      return canCaptureInAnyDirection(move, color);
   }

   /**
    * Checks if they can capture in any direction. Does not give direction or how
    * many
    *
    * @param move
    * @param color
    * @return
    */
   private boolean canCaptureInAnyDirection(CoordPair move, int color) {
      int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
      int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };
      for (int dir = 0; dir < 8; dir++) {
         if (capturesInDirectionTest(move, color, dx[dir], dy[dir])) {
            return true;
         }
      }
      return false;
   }

   /**
    * Checks if a move in a given direction results in capturing any opponent's
    * pieces. Used as a helper in move checking logic.
    *
    * @param move  The coordinate pair representing the move.
    * @param color The color making the move (1 for white, 2 for black).
    * @param dx    The horizontal direction delta.
    * @param dy    The vertical direction delta.
    * @return true if the move captures any pieces in this direction; false
    *         otherwise.
    */
   private boolean capturesInDirectionTest(CoordPair move, int color, int dx, int dy) {
      int opposite = (color == 1) ? 2 : 1;
      int x = move.x + dx;
      int y = move.y + dy;
      boolean foundOpponent = false;
      // First, must find at least one opponent piece
      while (x >= 0 && x < size && y >= 0 && y < size) {
         if (curBoard[x][y] == opposite) {
            foundOpponent = true;
         } else if (curBoard[x][y] == color && foundOpponent) {
            return true; // Sequence: our piece after opponent(s)
         } else {
            break;
         }
         x += dx;
         y += dy;
      }
      return false;
   }

   /**
    * Sets the contents of a board slot.
    *
    * @param move  The coordinate pair of the slot to modify.
    * @param state The state to set (0 = empty, 1 = white, 2 = black).
    */
   public void setSlot(CoordPair move, int state) {
      curBoard[move.x][move.y] = state;
   }

   /**
    * Attempts to make a move for the given color, updating the board if the move
    * is valid.
    *
    * @param move  The coordinate pair for the move.
    * @param color The color making the move (1 for white, 2 for black).
    * @return True if the move was valid and made, false otherwise.
    */
   public Boolean makeMove(CoordPair move, int color) {
      if (moveChecker(move, true, color)) {
         curBoard[move.x][move.y] = color; // Place piece
         flipCapturedInAnyDirection(move, color); // Flip captured pieces!
         return true;
      }
      return false;
   }

   /**
    * Flips any pieces captured by the last move in all directions.
    * Called after placing a valid move to update the board.
    *
    * @param move  The coordinate pair for the move.
    * @param color The color that just made the move.
    */
   private void flipCapturedInAnyDirection(CoordPair move, int color) {
      int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
      int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };
      for (int dir = 0; dir < 8; dir++) {
         flipCapturedInDirection(move, color, dx[dir], dy[dir]);
      }
   }

   /**
    * Flips any captured opponent pieces in the specified direction after a move.
    *
    * @param move  The coordinate pair for the move.
    * @param color The color making the move.
    * @param dx    The horizontal direction delta.
    * @param dy    The vertical direction delta.
    */
   private void flipCapturedInDirection(CoordPair move, int color, int dx, int dy) {
      int opposite = (color == 1) ? 2 : 1;
      int x = move.x + dx;
      int y = move.y + dy;
      java.util.List<CoordPair> toFlip = new java.util.ArrayList<>();
      // First find a stretch of opponent pieces
      while (x >= 0 && x < size && y >= 0 && y < size && curBoard[x][y] == opposite) {
         toFlip.add(new CoordPair(x, y));
         x += dx;
         y += dy;
      }
      // If next cell is player's own piece, flip collected
      if (x >= 0 && x < size && y >= 0 && y < size && curBoard[x][y] == color) {
         for (CoordPair c : toFlip) {
            curBoard[c.x][c.y] = color;
         }
      }
   }

   /**
    * Sets the board state using a 64-character string of digits ('0', '1', '2')
    * in row-major order, filling curBoard accordingly.
    *
    * @param state The string representation of the board state.
    */
   public void setState(String state) {
      for (int i = 0; i < 8; i++) {
         for (int j = 0; j < 8; j++) {
            curBoard[j][i] = Character.getNumericValue(state.charAt(i * 8 + j));
         }
      }
   }

   /**
    * Outputs a 64-character string representation of the board,
    * suitable for restoring the board via setState. The string is in row-major
    * order.
    *
    * @return the current state string of the board
    */
   public String getState() {
      StringBuilder out = new StringBuilder(64);
      for (int i = 0; i < 8; i++) {
         for (int j = 0; j < 8; j++) {
            out.append(curBoard[j][i]);
         }
      }
      return out.toString();
   }

   /**
    * Gives a list of CoordPairs that boarder a given color & may be possible
    * moves.
    *
    * @param color the color you want to find borders for
    * @return An ArrayList<CoordPair> containg possible spots
    */
   public ArrayList<CoordPair> getBorderingEmptySlots(int color) {
      ArrayList<CoordPair> bordering = new ArrayList<>();
      Set<String> seen = new HashSet<>();
      int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
      int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };
      for (int x = 0; x < size; x++) {
         for (int y = 0; y < size; y++) {
            if (curBoard[x][y] == color) {
               for (int dir = 0; dir < 8; dir++) {
                  int nx = x + dx[dir];
                  int ny = y + dy[dir];
                  if (nx >= 0 && nx < size && ny >= 0 && ny < size && curBoard[nx][ny] == 0) {
                     String key = nx + "," + ny;
                     if (!seen.contains(key)) {
                        seen.add(key);
                        bordering.add(new CoordPair(nx, ny));
                     }
                  }
               }
            }
         }
      }
      return bordering;
   }

}
