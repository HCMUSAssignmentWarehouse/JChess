package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.player.Alliance;
import main.java.com.iceteaviet.chess.core.player.Move;
import main.java.com.iceteaviet.chess.gui.ChessGameWatcher;
import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;
import main.java.com.iceteaviet.chess.properties.values.string;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * ChessAI is used for managing connection between chess game and AI worker
 * <p>
 * Created by Genius Doan on 6/20/2017.
 */
public class ChessAI {
    public static final boolean DEFAULT_ALPHA_BETA_ENABLED = true;
    private static ChessAI mInstance = null;
    private boolean isAlphaBetaStock = DEFAULT_ALPHA_BETA_ENABLED;

    private ChessAI() {
        //For singleton
    }

    public static ChessAI getInstance(boolean enableAlphaBeta) {
        if (mInstance == null)
            mInstance = new ChessAI();

        mInstance.isAlphaBetaStock = enableAlphaBeta;
        return mInstance;
    }

    public void move() {
        final AIAsyncWorker task = new AIAsyncWorker();
        task.execute();
    }

    //Find best move for AI
    private Move getBestMove(boolean isAlphaBetaStock) {
        if (isAlphaBetaStock) {
            int searchDepth = ChessGameWatcher.getInstance().getGameSetupDialog().getSearchDepth();
            final AlphaBetaPruning strategy =
                    new AlphaBetaPruning(searchDepth);
            return strategy.eval(ChessGameWatcher.getInstance().getGameBoard(), searchDepth);
        } else {
            //Use MiniMax
            final MoveAlgorithm minimax = new MiniMax(ChessGameWatcher.getInstance().getGameSetupDialog().getSearchDepth());
            return minimax.eval(ChessGameWatcher.getInstance().getGameBoard(), ChessGameWatcher.getInstance().getGameSetupDialog().getSearchDepth());
        }
    }


    private static class AIAsyncWorker extends SwingWorker<Move, String> {
        private AIAsyncWorker() {
        }

        @Override
        protected Move doInBackground() throws Exception {
            if (mInstance == null) {
                MessageBox.showError(string.error_moving_ai, string.chess_ai);
                return Move.MoveFactory.createMove(null, -1, -1);
            }
            return mInstance.getBestMove(mInstance.isAlphaBetaStock);
        }

        @Override
        protected void done() {
            try {
                final Move bestMove = get();
                ChessGameWatcher.getInstance().updateAIMove(bestMove);
                ChessGameWatcher.getInstance().updateGameBoard(ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                ChessGameWatcher.getInstance().getMoveLog().addMove(bestMove);
                ChessGameWatcher.getInstance().getGameHistoryPanel().redo(ChessGameWatcher.getInstance().getGameBoard(), ChessGameWatcher.getInstance().getMoveLog());
                ChessGameWatcher.getInstance().getTakenPiecesPanel().redo(ChessGameWatcher.getInstance().getMoveLog());
                ChessGameWatcher.getInstance().getBoardPanel().drawBoard(ChessGameWatcher.getInstance().getGameBoard());
                ChessGameWatcher.getInstance().moveMadeUpdate(ChessGameWatcher.PlayerType.COMPUTER);
                //Switch the clock
                if (bestMove.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
                    //White finished moving, now turn on the black clock
                    ChessGameWatcher.getInstance().getRightMenuPanel().getChronometerW().pause();
                    ChessGameWatcher.getInstance().getRightMenuPanel().getChronometerB().startOrResume();
                } else {
                    ChessGameWatcher.getInstance().getRightMenuPanel().getChronometerB().pause();
                    ChessGameWatcher.getInstance().getRightMenuPanel().getChronometerW().startOrResume();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
