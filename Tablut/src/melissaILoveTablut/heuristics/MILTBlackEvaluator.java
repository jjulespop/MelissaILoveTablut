package melissaILoveTablut.heuristics;

import melissaILoveTablut.MILTState;

public class MILTBlackEvaluator implements MILTEvaluator {

	public double evaluate(MILTState state) {
		int val = 0;
		if (state.isTerminal()) {
			val = (state.isWhiteWin()) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		} else {
			val += 10 * (state.getBlacksWhitesDiff() - 8);
			val += state.getWhitePawnsThreatened();
			val -= 2*state.getBlackPawnsThreatened();
		}
		return val;
	}
}