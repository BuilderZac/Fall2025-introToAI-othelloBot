/*
This is an Othello Artificial Intelligence created to play Othello as optimally as possible

Authors: Zach Lavoie and Calvin Rutherford
*/
import java.util.*;

public class model
{
   // Board constants
   private static final int EMPTY = 0;
   private static final int WHITE = 1;
   private static final int BLACK = 2;
   private static final int SIZE = 8;

   // Positional weights as a 64-character string
   private static final String WEIGHT_STRING =
   "95777759" + // ROW 1
   "55333355" + // ROW 2
   "73222237" + // ROW 3
   "73211237" + // ROW 4
   "73211237" + // ROW 5
   "73222237" + // ROW 6
   "55333355" + // ROW 7
   "95777759";  // ROW 8
   
   // Get weight for a specific board position
   private static int getWeightAt(int x, int y)
   {
      return Character.getNumericValue(WEIGHT_STRING.charAt(y * 8 + x));
   }

   // Difficulty state adaptability
   private static int currentDepth = 5;
   private static int minDepth = 4;
   private static int maxDepth = 8;
   private static List<Integer> recentScoreDifferences = new ArrayList<>();
   private static int movesPlayed = 0;

   // Find the best move using min-max with alpha-beta pruning and adjust difficulty to opponent performance
   public static board.CoordPair findBestMove(board gameBoard, int player)
   {
      movesPlayed++;

      // Determine search depth based on adapt difficulty
      int depth = getAdaptiveDepth(gameBoard, player);

      List<board.CoordPair> moves = getLegalMoves(gameBoard, player);

      if (moves.isEmpty())
      {
         return null; // No legal moves
      }

      board.CoordPair bestMove = null;
      int bestScore = Integer.MIN_VALUE;
      int alpha = Integer.MIN_VALUE;
      int beta = Integer.MAX_VALUE;

      // Move ordering: corners and high-value positions first
      moves.sort((m1, m2) -> Integer.compare(getWeightAt(m2.x(), m2.y()), getWeightAt(m1.x(), m1.y())));

      for (board.CoordPair move : moves)
      {
         // Create a copy of the board to make move on
         board testBoard = copyBoard(gameBoard);
         testBoard.makeMove(move, player);

         // Evaluate whether the move is optimal
         int score = minimax(testBoard, depth - 1, alpha, beta, false, player);

         if (score > bestScore)
         {
            bestScore = score;
            bestMove = move;
         }
         alpha = Math.max(alpha, bestScore);
      }

      // Update difficulty based on move
      updateDifficultyTracking(gameBoard, player);

      return bestMove;
   }

   // Adapt search depth based on opponent performance
   private static int getAdaptiveDepth(board gameBoard, int aiColor)
   {
      // 6 moves of easy difficulty
      if (movesPlayed < 6)
      {
         return minDepth;
      }

      return currentDepth;
   }

   // Update difficulty based on current game state
   private static void updateDifficultyTracking(board gameBoard, int aiColor)
   {
      int opponentColor = (aiColor == WHITE) ? BLACK : WHITE;

      // Calculate score difference (positive = opponent winning)
      int opponentScore = 0;
      int aiScore = 0;

      for (int i = 0; i < SIZE; i++)
      {
         for (int j = 0; j < SIZE; j++)
         {
            if (gameBoard.curBoard[i][j] == opponentColor)
            {
               opponentScore++;
            }
            else if (gameBoard.curBoard[i][j] == aiColor)
            {
               aiScore++;
            }
         }
      }

      int scoreDiff = opponentScore - aiScore;
      recentScoreDifferences.add(scoreDiff);

      // Track only last 5 score differences
      if (recentScoreDifferences.size() > 5)
      {
         recentScoreDifferences.remove(0);
      }

      // Adjust difficulty every 3 moves after initial grace period 
      if (movesPlayed > 6 && movesPlayed % 3 == 0)
      {
         adjustDifficulty();
      }
   }

   // Adjust AI based on recent opponent performance
   private static void adjustDifficulty()
   {
      if (recentScoreDifferences.isEmpty()) return;

      // Calculate average score difference
      double avgDiff = recentScoreDifferences.stream().mapToInt(Integer::intValue).average().orElse(0.0);

      // Opponent is winning by a lot - increase difficulty
      if (avgDiff > 8)
      {
         if (currentDepth < maxDepth)
         {
            currentDepth++;
         }
      }
      // Opponent is losing badly - decrease difficulty
      else if (avgDiff < -8)
      {
         if (currentDepth > minDepth)
         {
            currentDepth--;
         }
      }
      // Game is close, gain an edge in the end game (if possible)
      else
      {
         if (movesPlayed > 20 && currentDepth < maxDepth)
         {
            currentDepth++;
         }
      }
   }

   // Min-max algorithm with alpha-beta pruning
   private static int minimax(board gameBoard, int depth, int alpha, int beta, boolean isMaximizing, int player)
   {
      if (depth == 0 || isGameOver(gameBoard))
      {
         return evaluate(gameBoard, player);
      }

      int opponent = (player == WHITE) ? BLACK : WHITE;
      int currentPlayer = isMaximizing ? player : opponent;
      List<board.CoordPair> moves = getLegalMoves(gameBoard, currentPlayer);

      // If no legal moves, pass to opponent
      if (moves.isEmpty())
      {
         return minimax(gameBoard, depth - 1, alpha, beta, !isMaximizing, player);
      }

      if (isMaximizing)
      {
         int maxScore = Integer.MIN_VALUE;
         for (board.CoordPair move : moves)
         {
            board testBoard = copyBoard(gameBoard);
            testBoard.makeMove(move, currentPlayer);
            int score = minimax(testBoard, depth - 1, alpha, beta, false, player);
            maxScore = Math.max(maxScore, score);
            alpha = Math.max(alpha, score);
            if (beta <= alpha) break; // Beta cutoff
         }
         return maxScore;
      }
      else
      {
         int minScore = Integer.MAX_VALUE;
         for (board.CoordPair move : moves)
         {
            board testBoard = copyBoard(gameBoard);
            testBoard.makeMove(move, currentPlayer);
            int score = minimax(testBoard, depth - 1, alpha, beta, true, player);
            minScore = Math.min(minScore, score);
            beta = Math.min(beta, score);
            if (beta <= alpha) break; // Alpha cutoff
         }
         return minScore;
      }
   }

   // Evaluate the current board position for given player (higher = better for player)
   private static int evaluate(board gameBoard, int player)
   {
      int opponent = (player == WHITE) ? BLACK : WHITE;
      int score = 0;

      // Positional weight evaluation
      for (int i = 0; i < SIZE; i++)
      {
         for (int j = 0; j < SIZE; j++)
         {
            if (gameBoard.curBoard[i][j] == player) {
               score += getWeightAt(i, j);
            }
            else if (gameBoard.curBoard[i][j] == opponent)
            {
               score -= getWeightAt(i, j);
            }
         }
      }

      // Mobility (legal moves)
      int playerMobility = getLegalMoves(gameBoard, player).size();
      int opponentMobility = getLegalMoves(gameBoard, opponent).size();
      score += (playerMobility - opponentMobility);

      return score;
   }

   // Get all legal moves for a player
   private static List<board.CoordPair> getLegalMoves(board gameBoard, int player)
   {
      List<board.CoordPair> moves = new ArrayList<>();
      for (int i = 0; i < SIZE; i++)
      {
         for (int j = 0; j < SIZE; j++)
         {
            board.CoordPair move = new board.CoordPair(i, j);
            if (gameBoard.moveChecker(move, true, player))
            {
               moves.add(move);
            }
         }
      }
      return moves;
   }

   // Create a deep copy of board
   private static board copyBoard(board original)
   {
      board copy = new board();
      copy.setState(original.getState());
      return copy;
   }

   // Check if game is over (no legal moves for either player)
   private static boolean isGameOver(board gameBoard)
   {
      return getLegalMoves(gameBoard, WHITE).isEmpty() && getLegalMoves(gameBoard, BLACK).isEmpty();
   }

   // Count pieces of a specific color on the board
   private static int countPieces(board gameBoard, int player)
   {
      int count = 0;
      for (int i = 0; i < SIZE; i++)
      {
         for (int j = 0; j < SIZE; j++)
         {
            if (gameBoard.curBoard[i][j] == player)
            {
               count++;
            }
         }
      }
      return count;
   }

   // Count empty squares
   private static int countEmpty(board gameBoard)
   {
      int count = 0;
      for (int i = 0; i < SIZE; i++)
      {
         for (int j = 0; j < SIZE; j++)
         {
            if (gameBoard.curBoard[i][j] == EMPTY)
            {
               count++;
            }
         }
      }
      return count;
   }

   // Convert Coordinate Pair to algebraic notation (EX: "A4", "E7", "B3")
   public static String moveToString(board.CoordPair move)
   {
      if (move == null) return "PASS";
      char col = (char)('A' + move.x());
      int row = move.y() + 1;
      return "" + col + row;
   }

   // Reset adaptive difficulty for new games
   public static void resetDifficulty()
   {
      currentDepth = 5; // Middle ground difficulty
      recentScoreDifferences.clear();
      movesPlayed = 0;
   }
}