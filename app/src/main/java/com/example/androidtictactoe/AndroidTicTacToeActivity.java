package com.example.androidtictactoe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AndroidTicTacToeActivity extends Activity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button mBoardButtons[];

    // Various text displayed
    private TextView mInfoTextView;

    // Extra Challenge: TextViews for score tracking
    private TextView mHumanScoreTextView;
    private TextView mTieScoreTextView;
    private TextView mAndroidScoreTextView;

    // Game state tracking
    private boolean mGameOver;
    private int mHumanWins = 0;
    private int mTieCount = 0;
    private int mAndroidWins = 0;

    // Alternating starter flag: true if human starts, false if android starts
    private boolean mHumanStarts = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Ensure system bars (status bar / header) do not overlap the grid
        View rootView = findViewById(R.id.root_layout);
        if (rootView != null) {
            rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public android.view.WindowInsets onApplyWindowInsets(View v, android.view.WindowInsets insets) {
                    v.setPadding(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom()
                    );
                    return insets;
                }
            });
        }

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);

        // Extra challenge score views
        mHumanScoreTextView = (TextView) findViewById(R.id.human_score);
        mTieScoreTextView = (TextView) findViewById(R.id.ties_score);
        mAndroidScoreTextView = (TextView) findViewById(R.id.android_score);

        mGame = new TicTacToeGame();

        startNewGame();
    }

    // Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        mGame.clearBoard();

        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        // Extra Challenge: alternate who gets to go first
        if (mHumanStarts) {
            // Human goes first
            mInfoTextView.setText(R.string.first_human);
        } else {
            // Computer goes first
            mInfoTextView.setText(R.string.first_android);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.turn_human);
        }
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (!mGameOver && mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_human);
                } else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    mTieCount++;
                    mTieScoreTextView.setText(getString(R.string.ties_score, mTieCount));
                    mGameOver = true;
                    mHumanStarts = !mHumanStarts;
                } else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    mHumanWins++;
                    mHumanScoreTextView.setText(getString(R.string.human_score, mHumanWins));
                    mGameOver = true;
                    mHumanStarts = !mHumanStarts;
                } else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mAndroidWins++;
                    mAndroidScoreTextView.setText(getString(R.string.android_score, mAndroidWins));
                    mGameOver = true;
                    mHumanStarts = !mHumanStarts;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }
}
