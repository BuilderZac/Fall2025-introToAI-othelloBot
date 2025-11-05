
import java.util.ArrayList;
import java.io.*;

public class boardTest {

   static void assertEquals(int expected, int actual, String msg) {
      if (expected != actual)
         throw new RuntimeException(msg + " (expected " + expected + ", got " + actual + ")");
   }

   static void assertEquals(String expected, String actual, String msg) {
      if (!expected.equals(actual))
         throw new RuntimeException(msg + " (expected '" + expected + "', got '" + actual + "')");
   }

   static void assertTrue(boolean cond, String msg) {
      if (!cond)
         throw new RuntimeException(msg + " (condition false)");
   }

   static void assertFalse(boolean cond, String msg) {
      if (cond)
         throw new RuntimeException(msg + " (condition true)");
   }

   public static void main(String[] args) {
      int pass = 0, fail = 0;

      // testSetBoardInitialPosition
      try {
         board game = new board();
         game.setBoard();
         assertEquals(1, game.curBoard[3][3], "Initial board D4 should be white");
         assertEquals(2, game.curBoard[3][4], "Initial board D5 should be black");
         assertEquals(2, game.curBoard[4][3], "Initial board E4 should be black");
         assertEquals(1, game.curBoard[4][4], "Initial board E5 should be white");
         for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
               if (!((i == 3 || i == 4) && (j == 3 || j == 4)))
                  assertEquals(0, game.curBoard[i][j], "All other cells should be empty");
         System.out.println("testSetBoardInitialPosition: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testSetBoardInitialPosition: FAIL " + t);
         fail++;
      }

      // testPrintBoardOutput
      try {
         board game = new board();
         game.setBoard();
         ByteArrayOutputStream outContent = new ByteArrayOutputStream();
         PrintStream orig = System.out;
         System.setOut(new PrintStream(outContent));
         game.printBoard();
         System.setOut(orig); // restore
         String output = outContent.toString();
         assertTrue(output.contains("ABCDEFGH"), "Board header should have letters");
         assertTrue(output.contains("4###WB###") || output.contains("4#WWBB###"),
               "Should display row 4 with white and black"); // flexible for formatting
         System.out.println("testPrintBoardOutput: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testPrintBoardOutput: FAIL " + t);
         fail++;
      }

      // testMoveReaderVariousInputs
      try {
         board game = new board();
         board.CoordPair moveA1 = game.moveReader("A1");
         assertEquals(0, moveA1.x(), "MoveReader A1 x");
         assertEquals(0, moveA1.y(), "MoveReader A1 y");
         board.CoordPair moveH8 = game.moveReader("h8");
         assertEquals(7, moveH8.x(), "MoveReader H8)");
         assertEquals(7, moveH8.y(), "MoveReader H8 y");
         System.out.println("testMoveReaderVariousInputs: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testMoveReaderVariousInputs: FAIL " + t);
         fail++;
      }

      // testMoveCheckerInvalidOccupied
      try {
         board game = new board();
         game.setBoard();
         assertFalse(game.moveChecker(new board.CoordPair(3, 3), true, 1), "MoveChecker on occupied cell");
         System.out.println("testMoveCheckerInvalidOccupied: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testMoveCheckerInvalidOccupied: FAIL " + t);
         fail++;
      }

      // testMoveCheckerOutOfBounds
      try {
         board game = new board();
         assertFalse(game.moveChecker(new board.CoordPair(-1, 0), true, 1), "MoveChecker bounds -1");
         assertFalse(game.moveChecker(new board.CoordPair(8, 8), true, 2), "MoveChecker bounds 8,8");
         System.out.println("testMoveCheckerOutOfBounds: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testMoveCheckerOutOfBounds: FAIL " + t);
         fail++;
      }

      // testMoveCheckerValidInitialMove
      try {
         board game = new board();
         game.setBoard();
         assertTrue(game.moveChecker(new board.CoordPair(2, 3), true, 2), "MoveChecker C4 black");
         assertFalse(game.moveChecker(new board.CoordPair(2, 3), true, 1), "MoveChecker C4 white");
         System.out.println("testMoveCheckerValidInitialMove: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testMoveCheckerValidInitialMove: FAIL " + t);
         fail++;
      }

      // testSetSlotAndGet
      try {
         board game = new board();
         board.CoordPair c = new board.CoordPair(0, 0);
         game.setSlot(c, 1);
         assertEquals(1, game.curBoard[0][0], "setSlot then check for 1");
         game.setSlot(c, 2);
         assertEquals(2, game.curBoard[0][0], "setSlot then check for 2");
         game.setSlot(c, 0);
         assertEquals(0, game.curBoard[0][0], "setSlot then check for 0");
         System.out.println("testSetSlotAndGet: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testSetSlotAndGet: FAIL " + t);
         fail++;
      }

      // testMakeMoveValidMove
      try {
         board game = new board();
         game.setBoard();
         board.CoordPair move = new board.CoordPair(2, 3);
         assertTrue(game.makeMove(move, 2), "makeMove valid black");
         assertEquals(2, game.curBoard[2][3], "makeMove places a stone");
         assertEquals(2, game.curBoard[3][3], "makeMove flips a piece");
         System.out.println("testMakeMoveValidMove: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testMakeMoveValidMove: FAIL " + t);
         fail++;
      }

      // testMakeMoveInvalidMove
      try {
         board game = new board();
         game.setBoard();
         assertFalse(game.makeMove(new board.CoordPair(3, 3), 2), "makeMove on occupied");
         assertFalse(game.makeMove(new board.CoordPair(-1, 0), 2), "makeMove out of bounds");
         System.out.println("testMakeMoveInvalidMove: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testMakeMoveInvalidMove: FAIL " + t);
         fail++;
      }

      // testFlipCapturedPieces
      try {
         board game = new board();
         game.setBoard();
         board.CoordPair move = new board.CoordPair(2, 3);
         game.makeMove(move, 2);
         assertEquals(2, game.curBoard[2][3], "flipCaptured, stone placed");
         assertEquals(2, game.curBoard[3][3], "flipCaptured, piece flipped");
         System.out.println("testFlipCapturedPieces: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testFlipCapturedPieces: FAIL " + t);
         fail++;
      }

      // testSetStateAndPrintStateRoundTrip
      try {
         board game = new board();
         String emptyState = "0".repeat(64);
         game.setState(emptyState);
         assertEquals(emptyState, game.getState(), "board state roundtrip, all empty");
         String blackState = "2".repeat(64);
         game.setState(blackState);
         assertEquals(blackState, game.getState(), "board state roundtrip, all black");
         StringBuilder setup = new StringBuilder();
         for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
               if ((i == 3 && j == 3) || (i == 4 && j == 4))
                  setup.append("1");
               else if ((i == 3 && j == 4) || (i == 4 && j == 3))
                  setup.append("2");
               else
                  setup.append("0");
         String setupStr = setup.toString();
         game.setState(setupStr);
         assertEquals(setupStr, game.getState(), "board state roundtrip, std setup");
         System.out.println("testSetStateAndPrintStateRoundTrip: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testSetStateAndPrintStateRoundTrip: FAIL " + t);
         fail++;
      }

      try {
         board game = new board();
         String s0 = "0000000000000000000000000001200000021000000000000000000000000000";
         String s1 = "0000000000000000000200000002200000021000000000000000000000000000";
         String s2 = "0000000000000000001200000001200000021000000000000000000000000000";
         // Set initial state (black to move)
         game.setBoard();
         assertEquals(s0, game.getState(), "Initial state load");

         // Black plays d3 (column 3, row 2 in 0-based: c=2, d=3; 3rd row = 2)
         board.CoordPair d3 = game.moveReader("D3");
         boolean move1 = game.makeMove(d3, 2); // 2: black
         assertTrue(move1, "Black d3 should be valid");
         assertEquals(s1, game.getState(), "After d3 by black");

         // White plays c3 (column 2, row 2: c=2, 2nd row = 2)
         board.CoordPair c3 = game.moveReader("C3");
         boolean move2 = game.makeMove(c3, 1); // 1: white
         assertTrue(move2, "White c3 should be valid");
         assertEquals(s2, game.getState(), "After c3 by white");

         System.out.println("testStateGamePlayProgression: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testStateGamePlayProgression: FAIL " + t);
         fail++;
      }

      try {
         board game = new board();
         // Stress/fantasy state: mass capture on e5!
         String s0 = "0000000000000000001111100012221000120210001222100011111000000000";
         String s1 = "0000000000000000001111100011111000111110001111100011111000000000";
         game.setState(s0);
         assertEquals(s0, game.getState(), "Stress initial state load");

         // Black plays e5 (e=4, 5th row, so y=4), so (4,4)
         board.CoordPair e5 = game.moveReader("E5");
         boolean didMove = game.makeMove(e5, 1);
         assertTrue(didMove, "White e5 should be valid move");
         assertEquals(s1, game.getState(), "Stress state after e5");

         System.out.println("testStressMassCapture: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testStressMassCapture: FAIL " + t);
         fail++;
      }

      try {
         board game = new board();
         String s0 = "1200012022000000000000000000000000000000000000000000000000000000";
         String s1 = "1110012022000000000000000000000000000000000000000000000000000000";
         String s2 = "1110011122000000000000000000000000000000000000000000000000000000";
         game.setState(s0);
         assertEquals(s0, game.getState(), "Stress initial state load");

         board.CoordPair c1 = game.moveReader("C1");
         boolean didMove = game.makeMove(c1, 1);
         assertTrue(didMove, "White C1 should be valid move");
         assertEquals(s1, game.getState(), "Stress state after C1");

         board.CoordPair h1 = game.moveReader("h1");
         boolean didMoveAgain = game.makeMove(h1, 1);
         assertTrue(didMoveAgain, "White H1 should be valid move");
         assertEquals(s2, game.getState(), "Stress state after H1");

         System.out.println("testCornerCaptrue: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testCornerCaptrue: FAIL " + t);
         fail++;
      }

      try {
         board game = new board();
         String s0 = "1200012022000000000001100002221000020210001222100011111000000000";
         game.setState(s0);
         assertEquals(s0, game.getState(), "Board initial state set");

         ArrayList<board.CoordPair> knownSubset = new ArrayList<board.CoordPair>();
         knownSubset.add(new board.CoordPair(4, 0));
         knownSubset.add(new board.CoordPair(4, 1));
         knownSubset.add(new board.CoordPair(5, 1));
         knownSubset.add(new board.CoordPair(2, 7));
         knownSubset.add(new board.CoordPair(7, 7));
         knownSubset.add(new board.CoordPair(7, 4));
         knownSubset.add(new board.CoordPair(7, 5));
         knownSubset.add(new board.CoordPair(4, 2));
         knownSubset.add(new board.CoordPair(1, 5));

         ArrayList<board.CoordPair> badSubset = new ArrayList<board.CoordPair>();
         badSubset.add(new board.CoordPair(0, 3));
         badSubset.add(new board.CoordPair(7, 0));
         badSubset.add(new board.CoordPair(2, 1));
         badSubset.add(new board.CoordPair(4, 4));
         badSubset.add(new board.CoordPair(2, 0));

         ArrayList<board.CoordPair> markedSet = game.getBorderingEmptySlots(1);
         assertTrue(markedSet.containsAll(knownSubset), "The returned list contains known good points");
         assertTrue(!markedSet.containsAll(badSubset), "The returned list avoids known bad points");

         System.out.println("testMarkedSet: PASS");
         pass++;
      } catch (Throwable t) {
         System.out.println("testMarkedSet: FAIL " + t);
         fail++;
      }

      System.out.println();
      System.out.println("Tests passed: " + pass + ", failed: " + fail);
   }
}
// 01234567
// 0 12000120
// 1 22000000
// 2 00000110
// 3 00022210
// 4 00020210
// 5 00122210
// 6 00111110
// 7 00000000
