package com.example.androidtictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToeActivity extends Activity {

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Custom view representing the board
    private BoardView mBoardView;

    // Media players for sound effects
    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;

    // Handler for delayed computer moves
    private Handler mHandler = new Handler();

    // Various text displayed
    private TextView mInfoTextView;

    // Extra Challenge: TextViews for score tracking
    private TextView mHumanScoreTextView;
    private TextView mTieScoreTextView;
    private TextView mComputerScoreTextView;
    private TextView mAndroidScoreTextView;

    // SharedPreferences for saving persistent info
    private SharedPreferences mPrefs;

    // Game state tracking
    private boolean mGameOver;
    private boolean mHumanTurn = true;
    private int mHumanWins = 0;
    private int mTies = 0;
    private int mComputerWins = 0;

    // Starter flag: HUMAN_PLAYER or COMPUTER_PLAYER
    private char mGoFirst = TicTacToeGame.HUMAN_PLAYER;
    private boolean mHumanStarts = true;

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_DOWN) {
                return false;
            }

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && mHumanTurn && pos >= 0 && pos < TicTacToeGame.BOARD_SIZE && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mHumanTurn = false;
                    mInfoTextView.setText(R.string.turn_computer);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mGameOver) return;
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            int compWinner = mGame.checkForWinner();
                            if (compWinner == 0) {
                                mInfoTextView.setText(R.string.turn_human);
                                mHumanTurn = true;
                            } else {
                                displayWinner(compWinner);
                            }
                        }
                    }, 1000);
                } else {
                    displayWinner(winner);
                }
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

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

        mInfoTextView = (TextView) findViewById(R.id.information);

        // Score views
        mHumanScoreTextView = (TextView) findViewById(R.id.human_score);
        mTieScoreTextView = (TextView) findViewById(R.id.ties_score);
        mComputerScoreTextView = (TextView) findViewById(R.id.android_score);
        mAndroidScoreTextView = mComputerScoreTextView;

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Restore the scores
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);

        // Extra Challenge 1: Restore difficulty level
        int difficulty = mPrefs.getInt("mDifficultyLevel", TicTacToeGame.DifficultyLevel.Expert.ordinal());
        if (difficulty >= 0 && difficulty < TicTacToeGame.DifficultyLevel.values().length) {
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[difficulty]);
        }

        if (savedInstanceState == null) {
            startNewGame();
        } else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mGoFirst = savedInstanceState.getChar("mGoFirst", TicTacToeGame.HUMAN_PLAYER);
            mHumanStarts = (mGoFirst == TicTacToeGame.HUMAN_PLAYER);
            mHumanTurn = savedInstanceState.getBoolean("mHumanTurn", true);

            // Extra Challenge 2: If orientation changed before the computer moved, make move
            if (!mGameOver && !mHumanTurn) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mGameOver) return;
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        int compWinner = mGame.checkForWinner();
                        if (compWinner == 0) {
                            mInfoTextView.setText(R.string.turn_human);
                            mHumanTurn = true;
                        } else {
                            displayWinner(compWinner);
                        }
                    }
                }, 1000);
            }
        }
        displayScores();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGoFirst);
        outState.putBoolean("mHumanTurn", mHumanTurn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mGoFirst = savedInstanceState.getChar("mGoFirst", TicTacToeGame.HUMAN_PLAYER);
        mHumanStarts = (mGoFirst == TicTacToeGame.HUMAN_PLAYER);
        mHumanTurn = savedInstanceState.getBoolean("mHumanTurn", true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores and difficulty
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.putInt("mDifficultyLevel", mGame.getDifficultyLevel().ordinal());
        ed.commit();
    }

    private void displayScores() {
        mHumanScoreTextView.setText(getString(R.string.human_score, mHumanWins));
        mComputerScoreTextView.setText(getString(R.string.android_score, mComputerWins));
        mTieScoreTextView.setText(getString(R.string.ties_score, mTies));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sword);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.swish);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        if (mHumanMediaPlayer != null) {
            mHumanMediaPlayer.release();
            mHumanMediaPlayer = null;
        }
        if (mComputerMediaPlayer != null) {
            mComputerMediaPlayer.release();
            mComputerMediaPlayer = null;
        }
    }

    // Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        mHandler.removeCallbacksAndMessages(null);
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board

        // Extra Challenge: alternate who gets to go first
        if (mHumanStarts) {
            mHumanTurn = true;
            mGoFirst = TicTacToeGame.HUMAN_PLAYER;
            mInfoTextView.setText(R.string.first_human);
        } else {
            mHumanTurn = false;
            mGoFirst = TicTacToeGame.COMPUTER_PLAYER;
            mInfoTextView.setText(R.string.first_android);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mGameOver) return;
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mInfoTextView.setText(R.string.turn_human);
                    mHumanTurn = true;
                }
            }, 1000);
        }
    }

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            try {
                if (player == TicTacToeGame.HUMAN_PLAYER) {
                    if (mHumanMediaPlayer != null) {
                        mHumanMediaPlayer.start();
                    }
                } else {
                    if (mComputerMediaPlayer != null) {
                        mComputerMediaPlayer.start();
                    }
                }
            } catch (Exception e) {
                // Ignore media player exceptions when activity is transitioning
            }
            return true;
        }
        return false;
    }

    private void displayWinner(int winner) {
        if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie);
            mTies++;
            mGameOver = true;
            mHumanStarts = !mHumanStarts;
            mGoFirst = mHumanStarts ? TicTacToeGame.HUMAN_PLAYER : TicTacToeGame.COMPUTER_PLAYER;
        } else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins);
            mHumanWins++;
            mGameOver = true;
            mHumanStarts = !mHumanStarts;
            mGoFirst = mHumanStarts ? TicTacToeGame.HUMAN_PLAYER : TicTacToeGame.COMPUTER_PLAYER;
        } else if (winner == 3) {
            mInfoTextView.setText(R.string.result_computer_wins);
            mComputerWins++;
            mGameOver = true;
            mHumanStarts = !mHumanStarts;
            mGoFirst = mHumanStarts ? TicTacToeGame.HUMAN_PLAYER : TicTacToeGame.COMPUTER_PLAYER;
        }
        displayScores();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_game) {
            startNewGame();
            return true;
        } else if (id == R.id.ai_difficulty) {
            showDialog(DIALOG_DIFFICULTY_ID);
            return true;
        } else if (id == R.id.reset_scores) {
            mHumanWins = 0;
            mComputerWins = 0;
            mTies = 0;
            displayScores();
            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putInt("mHumanWins", mHumanWins);
            ed.putInt("mComputerWins", mComputerWins);
            ed.putInt("mTies", mTies);
            ed.commit();
            return true;
        } else if (id == R.id.about) {
            showDialog(DIALOG_ABOUT_ID);
            return true;
        } else if (id == R.id.quit) {
            showDialog(DIALOG_QUIT_ID);
            return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                    getResources().getString(R.string.difficulty_easy),
                    getResources().getString(R.string.difficulty_harder),
                    getResources().getString(R.string.difficulty_expert)
                };

                // selected is the radio button that should be selected
                int selected = mGame.getDifficultyLevel().ordinal();

                builder.setSingleChoiceItems(levels, selected,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            dialog.dismiss(); // Close dialog

                            // Set the diff level of mGame based on which item was selected
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);

                            // Save difficulty level to SharedPreferences (Extra Challenge 1)
                            SharedPreferences.Editor ed = mPrefs.edit();
                            ed.putInt("mDifficultyLevel", item);
                            ed.commit();

                            // Display the selected difficulty level
                            Toast.makeText(getApplicationContext(), levels[item],
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AndroidTicTacToeActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;

            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton(R.string.ok, null);
                dialog = builder.create();
                break;
        }

        return dialog;
    }
}
