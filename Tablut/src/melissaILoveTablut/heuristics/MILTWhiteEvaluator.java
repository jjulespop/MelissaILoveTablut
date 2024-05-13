package melissaILoveTablut.heuristics;

import java.util.BitSet;

import melissaILoveTablut.MILTState;
import melissaILoveTablut.MILTState.Turn;

public class MILTWhiteEvaluator implements MILTEvaluator {

	private final static int[] kingPosVals = { 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 2, 3, -1, 0, -1, 3, 2, 0, 
			0, 3, 4, 2, 2, 2, 4, 3, 0, 
			0, -1, 2, 0, 0, 0, 2, -1, 0, 
			0, 0, 2, 0, 0, 0, 2, 0, 0, 
			0, -1, 2, 0, 0, 0, 2, -1, 0, 
			0, 3, 4, 2, 2, 2, 4, 3, 0,
			0, 2, 3, -1, 0, -1, 3, 2, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private double evaluateKingPos(MILTState state) {
		int kingPos = state.getKing().nextSetBit(0);
		double val = 0;
		if (kingPos >= 0) {
			if (state.getTurn() == Turn.WHITE) {
				if (state.hasKingEscapes()) {
					val = Double.POSITIVE_INFINITY;
				} else if (state.isKingThreatened()) {
					val = -30;
				} else {
					val = kingPosVals[kingPos] * 5;
					val += state.getKingMovements()*10;
				}

			} else {
				if (state.isKingThreatened()) {
					val = Double.NEGATIVE_INFINITY;
				} else {
					val = kingPosVals[kingPos] * 5;
					val += state.getKingMovements()*10;
				}
			}
		}
		return val;

	}
	
	private double evaluateFreeWays(MILTState state) {
		BitSet temp=new BitSet(MILTState.BOARD_SIZE*MILTState.BOARD_SIZE);
		temp.or(state.getWhites());
		temp.or(state.getBlacks());
		temp.and(MILTState.whiteStarts);
		return 20*(8-temp.cardinality());
	}

	public double evaluate(MILTState state) {
		double val = 0;
		if (state.isTerminal()) {
			val = (state.isWhiteWin()) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		} else {
			val -= 30 * state.getBlacksWhitesDiff();
			val += 10*state.getBlackPawnsThreatened();
			val += this.evaluateKingPos(state);
			val += this.evaluateFreeWays(state);
			val -= 5*state.getWhitePawnsThreatened();
		}
		return val;
	}

}