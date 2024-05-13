package melissaILoveTablut.test;

import java.util.BitSet;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import melissaILoveTablut.MILTAction;
import melissaILoveTablut.MILTGame;
import melissaILoveTablut.MILTSearch;
import melissaILoveTablut.MILTState;
import melissaILoveTablut.heuristics.MILTWhiteEvaluator;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class Tester {

	public static void main(String[] args) {
		try {
			State state = new StateTablut();
			GameAshtonTablut game = new GameAshtonTablut(state, 0, 0, "logs", "testerW", "testerB");
			Action action;
			state.setTurn(Turn.WHITE);

//			action = new Action("e3", "h3", Turn.WHITE);
//			state = game.checkMove(state, action);
//
//			action = new Action("e2", "e3", Turn.BLACK);
//			state = game.checkMove(state, action);
//
//			action = new Action("e5", "e4", Turn.WHITE);
//			state = game.checkMove(state, action);
//
//			action = new Action("f1", "f3", Turn.BLACK);
//			state = game.checkMove(state, action);

//			action = new Action("e4", "e3", Turn.WHITE);
//			state = game.checkMove(state, action);

//			action = new action("d1", "d3", turn.black);
//			state = game.checkmove(state, action);

			System.out.println(state.boardString());
			BitSet whites = new BitSet(MILTState.BOARD_SIZE * MILTState.BOARD_SIZE);
			BitSet blacks = new BitSet(MILTState.BOARD_SIZE * MILTState.BOARD_SIZE);
			BitSet king = new BitSet(MILTState.BOARD_SIZE * MILTState.BOARD_SIZE);

			king.clear();
			whites.clear();
			blacks.clear();
			Pawn p;
			for (int i = 0; i < MILTState.BOARD_SIZE; i++) {
				for (int j = 0; j < MILTState.BOARD_SIZE; j++) {
					p = state.getPawn(i, j);
					switch (p) {
					case KING -> {
						king.set(i * MILTState.BOARD_SIZE + j);
					}

					case WHITE -> {
						whites.set(i * MILTState.BOARD_SIZE + j);

					}

					case BLACK -> {
						blacks.set(i * MILTState.BOARD_SIZE + j);
					}
					default -> {
					}
					}
				}
			}
			System.out.println("starting test");
			MILTWhiteEvaluator whiteEvaluator=new MILTWhiteEvaluator();
			MILTState miltState = new MILTState(MILTState.Turn.WHITE, whites, blacks, king,state.getTurn());
			System.out.println("whites under attack: " + miltState.getWhitePawnsThreatened());
			System.out.println("blacks under attack: " + miltState.getBlackPawnsThreatened());
			System.out.println("king threatened: " + miltState.isKingThreatened());
			System.out.println("terminal: " + miltState.isTerminal());
			MILTGame miltGame=new MILTGame();
			MILTSearch miltSearch=new MILTSearch(miltGame,1);
			MILTAction bestAction=miltSearch.makeDecision(miltState);
			miltState=miltState.apply(bestAction);
			
			
			for (MILTAction a : miltState.getAvailableActions()) {
				System.out.println(a);
			}
			System.out.println("evaluation: "+miltState.evaluation(whiteEvaluator));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}