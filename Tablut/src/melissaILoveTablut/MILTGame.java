package melissaILoveTablut;

import java.util.List;

import aima.core.search.adversarial.Game;
import melissaILoveTablut.MILTState.Turn;
import melissaILoveTablut.heuristics.MILTBlackEvaluator;
import melissaILoveTablut.heuristics.MILTEvaluator;
import melissaILoveTablut.heuristics.MILTWhiteEvaluator;

public class MILTGame implements Game<MILTState, MILTAction, MILTState.Turn> {

	public MILTGame() {
	}

	@Override
	public List<MILTAction> getActions(MILTState state) {
		return state.getAvailableActions();
	}

	@Override
	public MILTState getInitialState() {
		return MILTState.getInitialState();
	}

	@Override
	public MILTState.Turn getPlayer(MILTState state) {
		return state.getTurn();
	}

	@Override
	public MILTState.Turn[] getPlayers() {
		return MILTState.Turn.values();
	}

	@Override
	public MILTState getResult(MILTState state, MILTAction action) {
		return state.apply(action);
	}

	@Override
	public double getUtility(MILTState state, Turn turn) {
		if ((turn == Turn.BLACK && state.isBlackWin()) || (turn == Turn.WHITE && state.isWhiteWin())) {
			return Double.POSITIVE_INFINITY;
		} else if ((turn == Turn.BLACK && state.isWhiteWin()) || (turn == Turn.WHITE && state.isBlackWin())) {
			return Double.NEGATIVE_INFINITY;
		}

		MILTEvaluator evaluator = (turn == Turn.WHITE) ? new MILTWhiteEvaluator() : new MILTBlackEvaluator();
		return state.evaluation(evaluator);
	}

	@Override
	public boolean isTerminal(MILTState state) {
		return state.isTerminal();
	}

}