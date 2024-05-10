package heuristics;

import java.util.BitSet;

import melissaILoveTablut.MILTState;

public class MILTBlackHeuristics {
	
	private MILTState state;
	
	private final int WHITE_ALIVE = 0; // White pawns still alive
    private final int BLACK_ALIVE = 1; // Black pawns still alive
    private final int BLACK_AROUND_KING = 2; // Black pawns surrounding the king
    private final int TOP_POS = 3; // Formation to prevent the king from escaping
    private final int BLOCKED_ESC = 3; // Pawns that block king escapes

    private final double PAWNS_AGGRESSION_WEIGHT = 2.0;

    // Number of tiles in top positions
    private final int TOP_POS_N = 4;

    private final Double[] earlyGameWeights;
    private final Double[] lateGameWeights;
    

    public MILTBlackHeuristics(MILTState state) {

        earlyGameWeights = new Double[4];
        earlyGameWeights[WHITE_ALIVE] = 10.0;
        earlyGameWeights[BLACK_ALIVE] = 10.0;
        earlyGameWeights[BLACK_AROUND_KING] = 10.0;
        earlyGameWeights[TOP_POS] = 10.0;

        lateGameWeights = new Double[4];
        lateGameWeights[WHITE_ALIVE] = 10.0;
        lateGameWeights[BLACK_ALIVE] = 10.0;
        lateGameWeights[BLACK_AROUND_KING] = 10.0;
        lateGameWeights[BLOCKED_ESC] = 10.0;
    }

    
    public double evaluation() {
        BitSet king = state.getKing();

        double stateValue = 0.0;
        boolean lateGame = false;

        int whitePawns = state.getWhites().cardinality();
        int blackPawns = state.getBlacks().cardinality();
        if (whitePawns <= 4)
            lateGame = true;

        // Values for the weighted sum
        double blacksAlive = (double) blackPawns * 1;
        double whitesALive = (double) whitePawns * 1;
        
        //TO DO: number of pawns necessary to capture king
        double surroundKing = (double) blackPawns;

        double whitesToBeCaptured = evalPawnsCaptures();
        if (whitesToBeCaptured > 0)
            stateValue += (whitesToBeCaptured / whitePawns) * PAWNS_AGGRESSION_WEIGHT;

        if (!lateGame) { // Early Game
            double pawnsInTopPos = (double) getPawnsInTopPos() / TOP_POS_N;

            stateValue += whitesALive * earlyGameWeights[WHITE_ALIVE];
            stateValue += blacksAlive * earlyGameWeights[BLACK_ALIVE];
            stateValue += surroundKing * earlyGameWeights[BLACK_AROUND_KING];
            stateValue += pawnsInTopPos * earlyGameWeights[TOP_POS];
            
        } else { // Late Game
            double blockingPawns = (double) getBlockingPawns();

            stateValue += whitesALive * lateGameWeights[WHITE_ALIVE];
            stateValue += blacksAlive * lateGameWeights[BLACK_ALIVE];
            stateValue += surroundKing * lateGameWeights[BLACK_AROUND_KING];
            stateValue += blockingPawns * lateGameWeights[BLOCKED_ESC];

            stateValue += canCaptureKing();
        }

        return stateValue;
    }
    
    
    public int getPawnsInTopPos() {
        int pawns = 0;
        
        //TO DO: add top position board and explore that
        
        return pawns;
    }

    
    public int getBlockingPawns() {
        return 4 - state.getKingEscapes(state).size();
    }
    
    
    private double canCaptureKing() {
        if (state.canKingBeCaptured(state))
            return 10.0;

        return 0.0;
    }

    
    private int evalPawnsCaptures() {
        int capturable = 0;
        int j = 0;

        BitSet board = state.getWhites();
        for (int i = 0; i < board.cardinality(); i++) {
            capturable += state.canPawnBeCaptured(board.nextSetBit(j)) ? 1 : 0;
            j = board.nextSetBit(j);
        }

        return capturable;
    }
    
    
    private int evalPawnsMovement() {
    	int mov = 0;
    	int j = 0;
        
        //TO DO: aggiungere pesi per le righe e le colonne favorevoli
        BitSet board = state.getBlacks();
        for (int i = 0; i < board.cardinality(); i++) {    
        	mov += state.getPawnsMovements(board.nextSetBit(j));
        	j = board.nextSetBit(j);
        }
    	
    	return mov;
    }

}
