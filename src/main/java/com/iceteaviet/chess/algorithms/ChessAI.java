package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.board.Move;
import main.java.com.iceteaviet.chess.gui.Table;
import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;
import main.res.values.string;

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
            int searchDepth = Table.getInstance().getGameSetupDialog().getSearchDepth();
            final AlphaBetaPruning strategy =
                    new AlphaBetaPruning(searchDepth);
            return strategy.eval(Table.getInstance().getGameBoard(), searchDepth);
        } else {
            //Use MiniMax
            final MoveAlgorithm minimax = new MiniMax(Table.getInstance().getGameSetupDialog().getSearchDepth());
            return minimax.eval(Table.getInstance().getGameBoard(), Table.getInstance().getGameSetupDialog().getSearchDepth());
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
                Table.getInstance().updateComputerMove(bestMove);
                Table.getInstance().updateGameBoard(Table.getInstance().getGameBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.getInstance().getMoveLog().addMove(bestMove);
                Table.getInstance().getGameHistoryPanel().redo(Table.getInstance().getGameBoard(), Table.getInstance().getMoveLog());
                Table.getInstance().getTakenPiecesPanel().redo(Table.getInstance().getMoveLog());
                Table.getInstance().getBoardPanel().drawBoard(Table.getInstance().getGameBoard());
                Table.getInstance().moveMadeUpdate(Table.PlayerType.COMPUTER);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
