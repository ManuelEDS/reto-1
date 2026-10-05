package com.example.androidtictactoe;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TicTacToeGameTest {

    private TicTacToeGame mGame;

    @Before
    public void setUp() {
        mGame = new TicTacToeGame();
    }

    @Test
    public void testClearBoard() {
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 0);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 1);
        mGame.clearBoard();
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            assertEquals(TicTacToeGame.OPEN_SPOT, mGame.getBoardOccupant(i));
        }
    }

    @Test
    public void testSetMove() {
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 4);
        assertEquals(TicTacToeGame.HUMAN_PLAYER, mGame.getBoardOccupant(4));
        
        // Cannot overwrite occupied spot
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 4);
        assertEquals(TicTacToeGame.HUMAN_PLAYER, mGame.getBoardOccupant(4));
    }

    @Test
    public void testCheckForWinnerHorizontal() {
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 0);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 1);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 2);
        assertEquals(2, mGame.checkForWinner());
    }

    @Test
    public void testCheckForWinnerVertical() {
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 1);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 4);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 7);
        assertEquals(3, mGame.checkForWinner());
    }

    @Test
    public void testCheckForWinnerDiagonal() {
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 0);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 4);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 8);
        assertEquals(2, mGame.checkForWinner());
    }

    @Test
    public void testTieGame() {
        // X O X
        // X O X
        // O X O
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 0);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 1);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 2);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 3);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 4);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 5);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 6);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 7);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 8);
        assertEquals(1, mGame.checkForWinner());
    }

    @Test
    public void testComputerWinningMove() {
        // Computer has 0 and 1, should take 2 to win
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 0);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 1);
        int move = mGame.getComputerMove();
        assertEquals(2, move);
    }

    @Test
    public void testComputerBlockingMove() {
        // Human has 3 and 4, computer should block at 5
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 3);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 4);
        int move = mGame.getComputerMove();
        assertEquals(5, move);
    }

    @Test
    public void testGetAndSetBoardState() {
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 0);
        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, 4);
        mGame.setMove(TicTacToeGame.HUMAN_PLAYER, 8);

        char[] state = mGame.getBoardState();
        assertNotNull(state);
        assertEquals(TicTacToeGame.BOARD_SIZE, state.length);
        assertEquals(TicTacToeGame.HUMAN_PLAYER, state[0]);
        assertEquals(TicTacToeGame.COMPUTER_PLAYER, state[4]);
        assertEquals(TicTacToeGame.HUMAN_PLAYER, state[8]);

        // Create new game and restore state
        TicTacToeGame newGame = new TicTacToeGame();
        newGame.setBoardState(state);
        assertEquals(TicTacToeGame.HUMAN_PLAYER, newGame.getBoardOccupant(0));
        assertEquals(TicTacToeGame.COMPUTER_PLAYER, newGame.getBoardOccupant(4));
        assertEquals(TicTacToeGame.HUMAN_PLAYER, newGame.getBoardOccupant(8));
        assertEquals(TicTacToeGame.OPEN_SPOT, newGame.getBoardOccupant(1));
    }
}
