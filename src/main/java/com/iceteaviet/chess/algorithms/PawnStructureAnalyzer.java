package main.java.com.iceteaviet.chess.algorithms;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Genius Doan on 6/20/2017.
 */
public class PawnStructureAnalyzer {
    private static PawnStructureAnalyzer mInstance = null;

    public static final int ISOLATED_PAWN_PENALTY = -15;
    public static final int DOUBLED_PAWN_PENALTY = -35;

    private PawnStructureAnalyzer() {
    }

    public static PawnStructureAnalyzer getInstance() {
        if (mInstance == null)
            mInstance = new PawnStructureAnalyzer();
        return mInstance;
    }

    public int isolatedPawnPenalty(final Player player) {
        return calculateIsolatedPawnPenalty(createPawnColumnTable(calculatePlayerPawns(player)));
    }

    public int doubledPawnPenalty(final Player player) {
        return calculatePawnColumnStack(createPawnColumnTable(calculatePlayerPawns(player)));
    }

    public int pawnStructureScore(final Player player) {
        final ListMultimap<Integer, Piece> pawnsOnColumnTable = createPawnColumnTable(calculatePlayerPawns(player));
        return calculatePawnColumnStack(pawnsOnColumnTable) + calculateIsolatedPawnPenalty(pawnsOnColumnTable);
    }

    private static Collection<Piece> calculatePlayerPawns(final Player player) {
        final List<Piece> playerPawnLocations = new ArrayList<>(8);
        for(final Piece piece : player.getActivePiece()) {
            if(piece.getPieceType().equals(Piece.PieceType.PAWN)) {
                playerPawnLocations.add(piece);
            }
        }
        return ImmutableList.copyOf(playerPawnLocations);
    }

    private static int calculatePawnColumnStack(final ListMultimap<Integer, Piece> pawnsOnColumnTable) {
        int pawnStackPenalty = 0;
        for(final Integer i : pawnsOnColumnTable.keySet()) {
            int pawnStackSize = pawnsOnColumnTable.get(i).size();
            if(pawnStackSize > 1) {
                pawnStackPenalty += pawnStackSize;
            }
        }
        return pawnStackPenalty * DOUBLED_PAWN_PENALTY;
    }

    private static int calculateIsolatedPawnPenalty(final ListMultimap<Integer, Piece> pawnsOnColumnTable) {
        int numIsolatedPawns = 0;
        for(final Integer i : pawnsOnColumnTable.keySet()) {
            if((pawnsOnColumnTable.get(i-1).isEmpty() && pawnsOnColumnTable.get(i+1).isEmpty())) {
                numIsolatedPawns += pawnsOnColumnTable.get(i).size();
            }
        }
        return numIsolatedPawns * ISOLATED_PAWN_PENALTY;
    }

    private static ListMultimap<Integer, Piece> createPawnColumnTable(final Collection<Piece> playerPawns) {
        final ListMultimap<Integer, Piece> table = ArrayListMultimap.create(8, 5);
        for(final Piece playerPawn : playerPawns) {
            table.put(playerPawn.getPiecePosition() % 8, playerPawn);
        }
        return table;
    }
}
