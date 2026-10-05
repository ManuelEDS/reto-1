package com.example.androidtictactoe;

import java.util.Random;

public class TicTacToeGame {

    public static final int BOARD_SIZE = 9;

    // Characters used to represent the human, computer, and open spots
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert};

    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    private char mBoard[] = new char[BOARD_SIZE];
    private Random mRand;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
        clearBoard();
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    /**
     * Return a copy of the current board state.
     * @return char array representing the board
     */
    public char[] getBoardState() {
        return mBoard.clone();
    }

    /**
     * Restore the board state from the given char array.
     * @param board - char array representing the board
     */
    public void setBoardState(char[] board) {
        if (board != null) {
            mBoard = board.clone();
        }
    }

    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     * 
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public boolean setMove(char player, int location) {
        if (location >= 0 && location < BOARD_SIZE && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
            return true;
        }
        return false;
    }

    /** Return the occupant of the given location on the game board.
     * @param location - The location (0-8)
     * @return The character occupant ('X', 'O', or ' ')
     */
    public char getBoardOccupant(int location) {
        if (location >= 0 && location < BOARD_SIZE) {
            return mBoard[location];
        }
        return OPEN_SPOT;
    }

    /**
     * Check for a winning move for the computer.
     * @return The winning spot (0-8) or -1 if no winning move exists.
     */
    public int getWinningMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    /**
     * Check for a move that blocks the human from winning.
     * @return The blocking spot (0-8) or -1 if no blocking move exists.
     */
    public int getBlockingMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    /**
     * Generate a random valid move.
     * @return An open spot index (0-8) or -1 if none available.
     */
    public int getRandomMove() {
        int move;
        int openSpots = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                openSpots++;
            }
        }
        if (openSpots == 0) {
            return -1;
        }
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] != OPEN_SPOT);
        return move;
    }

    /** Return the best move for the computer to make based on difficulty level.
     * @return The best move for the computer to make (0-8).
     */
    public int getComputerMove() {
        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy) {
            move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Expert) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }

        return move;
    }

    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3) {
            if (mBoard[i] == HUMAN_PLAYER &&
                mBoard[i + 1] == HUMAN_PLAYER &&
                mBoard[i + 2] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                mBoard[i + 1] == COMPUTER_PLAYER &&
                mBoard[i + 2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                mBoard[i + 3] == HUMAN_PLAYER &&
                mBoard[i + 6] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                mBoard[i + 3] == COMPUTER_PLAYER &&
                mBoard[i + 6] == COMPUTER_PLAYER)
                return 3;
        }

        // Check diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
             mBoard[4] == HUMAN_PLAYER &&
             mBoard[8] == HUMAN_PLAYER) ||
            (mBoard[2] == HUMAN_PLAYER &&
             mBoard[4] == HUMAN_PLAYER &&
             mBoard[6] == HUMAN_PLAYER))
            return 2;

        if ((mBoard[0] == COMPUTER_PLAYER &&
             mBoard[4] == COMPUTER_PLAYER &&
             mBoard[8] == COMPUTER_PLAYER) ||
            (mBoard[2] == COMPUTER_PLAYER &&
             mBoard[4] == COMPUTER_PLAYER &&
             mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find an open spot, game is not over yet
            if (mBoard[i] == OPEN_SPOT)
                return 0;
        }

        // If no winner and no open spots, it's a tie
        return 1;
    }
}
