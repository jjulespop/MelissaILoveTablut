package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.BitSet;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import melissaILoveTablut.*;
import melissaILoveTablut.heuristics.MILTWhiteEvaluator;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

/**
 * 
 * @author Alessio Bennati
 *
 */
public class MILTClient extends TablutClient {

	private int game;
	private MILTGame miltGame;

	public MILTClient(String player, String name, int gameChosen, int timeout, String ipAddress)
			throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
		game = gameChosen;
		miltGame = new MILTGame();
	}

	public MILTClient(String player, String name, int timeout, String ipAddress)
			throws UnknownHostException, IOException {
		this(player, name, 4, timeout, ipAddress);
	}

	public MILTClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
		this(player, "MelissaILoveTablut", 4, timeout, ipAddress);
	}

	public MILTClient(String player) throws UnknownHostException, IOException {
		this(player, "MelissaILoveTablut", 4, 60, "localhost");
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int gametype = 4;
		String role = "";
		String name = "MelissaILoveTablut";
		String ipAddress = "localhost";
		int timeout = 60;

		System.out.println(Arrays.toString(args));
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else if (args.length == 1) {
			System.out.println(args[0]);
			role = (args[0]);
		} else if (args.length == 2) {
			System.out.println(args[0]);
			role = (args[0]);
			System.out.println("timeout: " + args[1]);
			timeout = Integer.parseInt(args[1]);
		} else if (args.length == 3) {
			System.out.println(args[0]);
			role = (args[0]);
			System.out.println("timeout: " + args[1]);
			timeout = Integer.parseInt(args[1]);
			System.out.println("ip: " + args[2]);
			ipAddress = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		MILTClient client = new MILTClient(role, name, gametype, timeout, ipAddress);
		client.run();
	}

	@Override
	public void run() {

		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (this.game != 4) {
			System.out.println("Error in game selection");
			System.exit(4);
		}

		System.out.println("You are player " + this.getPlayer().toString() + "!");

		State state = null;
		BitSet whites = new BitSet(MILTState.BOARD_SIZE * MILTState.BOARD_SIZE);
		BitSet blacks = new BitSet(MILTState.BOARD_SIZE * MILTState.BOARD_SIZE);
		BitSet king = new BitSet(MILTState.BOARD_SIZE * MILTState.BOARD_SIZE);

		while (true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			state = this.getCurrentState();
			System.out.println(state.toString());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			king.clear();
			whites.clear();
			blacks.clear();

			if (this.getPlayer().equals(Turn.WHITE)) {
				// Mio turno
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
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
					MILTWhiteEvaluator evaluator = new MILTWhiteEvaluator();
					MILTState miltState = new MILTState(MILTState.Turn.WHITE, whites, blacks, king,state.getTurn());
					MILTSearch miltSearch = new MILTSearch(miltGame, timeout - 2);
					MILTAction miltBestAction = miltSearch.makeDecision(miltState);
					try {
						this.write(miltBestAction.toAction());
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// Turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// ho perso
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			} else {

				// Mio turno
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
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
					MILTState miltState = new MILTState(MILTState.Turn.BLACK, whites, blacks, king,state.getTurn());
					MILTSearch miltSearch = new MILTSearch(miltGame, timeout - 2);
					MILTAction miltBestAction = miltSearch.makeDecision(miltState);
					try {
						this.write(miltBestAction.toAction());
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				} else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			}
		}

	}
}