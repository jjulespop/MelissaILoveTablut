package melissaILoveTablut;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import melissaILoveTablut.heuristics.MILTEvaluator;
import melissaILoveTablut.heuristics.MILTWhiteEvaluator;

public class MILTState {

	public enum Turn {
		WHITE("W"), BLACK("B");

		private final String turn;

		private Turn(String s) {
			turn = s;
		}

		public boolean equalsTurn(String otherName) {
			return (otherName == null) ? false : turn.equals(otherName);
		}

		public String toString() {
			return turn;
		}
	}

	public enum PieceType {
		WHITE_PAWN("WP"), BLACK_PAWN("BP"), WHITE_KING("WK");

		private String name;

		private PieceType(String name) {
			this.name = name;
		}

		private String getName() {
			return name;
		}
	}

	public static final int BOARD_SIZE = 9;

	private static BitSet initWhiteStarts() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(22);
		result.set(31);
		result.set(38,40);
		result.set(41,43);
		result.set(49);
		result.set(58);
		return result;

	}
	
	private static BitSet initEscapes() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(1, 3);
		result.set(6, 8);
		result.set(9);
		result.set(17, 19);
		result.set(26);
		result.set(54);
		result.set(62, 64);
		result.set(71);
		result.set(73, 75);
		result.set(78, 80);
		return result;

	}

	private static BitSet initCampLeft() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(27);
		result.set(36, 38);
		result.set(45);
		return result;
	}

	private static BitSet initCampRight() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(35);
		result.set(43, 45);
		result.set(53);
		return result;
	}

	private static BitSet initCampUp() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(3, 6);
		result.set(13);
		return result;
	}

	private static BitSet initCampDown() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(67);
		result.set(75, 78);
		return result;
	}

	private static BitSet initCamps() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(3, 6);
		result.set(13);
		result.set(27);
		result.set(35, 38);
		result.set(43, 46);
		result.set(53);
		result.set(67);
		result.set(75, 78);
		return result;
	}

	private static BitSet initThrone() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(40);
		return result;
	}

	private static BitSet initAroundThrone() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(31);
		result.set(39);
		result.set(41);
		result.set(49);
		return result;
	}

	private static BitSet initAroundThroneSurroundedLeft() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(30);
		result.set(38);
		result.set(48);
		return result;
	};

	private static BitSet initAroundThroneSurroundedRight() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(32);
		result.set(42);
		result.set(50);
		return result;
	};

	private static BitSet initAroundThroneSurroundedUp() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(30);
		result.set(22);
		result.set(32);
		return result;
	};

	private static BitSet initAroundThroneSurroundedDown() {
		BitSet result = new BitSet(BOARD_SIZE * BOARD_SIZE);
		result.set(48);
		result.set(58);
		result.set(50);
		return result;
	};

	public static MILTState getInitialState() {

		// white setup
		BitSet whites = new BitSet(MILTState.BOARD_SIZE * MILTState.BOARD_SIZE);
		whites.set(22);
		whites.set(31);
		whites.set(38);
		whites.set(39);
		whites.set(41);
		whites.set(42);
		whites.set(49);
		whites.set(58);

		// black setup
		BitSet blacks = initCamps();

		// king setup
		BitSet king = initThrone();

		return new MILTState(Turn.WHITE, whites, blacks, king);
	}

	public static final BitSet whiteStarts= initWhiteStarts();
	public static final BitSet escapes = initEscapes();
	public static final BitSet campLeft = initCampLeft();
	public static final BitSet campRight = initCampRight();
	public static final BitSet campUp = initCampUp();
	public static final BitSet campDown = initCampDown();
	public static final BitSet camps = initCamps();
	public static final BitSet throne = initThrone();
	public static final BitSet aroundThrone = initAroundThrone();
	public static final BitSet aroundThroneSurroundedLeft = initAroundThroneSurroundedLeft();
	public static final BitSet aroundThroneSurroundedRight = initAroundThroneSurroundedRight();
	public static final BitSet aroundThroneSurroundedUp = initAroundThroneSurroundedUp();
	public static final BitSet aroundThroneSurroundedDown = initAroundThroneSurroundedDown();

	private Turn turn;
	private BitSet king;
	private BitSet whites;
	private BitSet blacks;
	private List<MILTAction> availableActions;
	private int whitePawnsThreatened;
	private int blackPawnsThreatened;
	private boolean kingThreatened;

	private boolean isSquareUnderLeftAttackOf(PieceType toCheck, int pos, int row, int col) {
		int newPos = -1;

		switch (toCheck) {
		case WHITE_PAWN -> {
			for (int j = 1; j <= col; j++) {
				newPos = pos - j;
				if (king.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (whites.get(newPos)) {
					return true;
				}
			}

		}
		case WHITE_KING -> {
			for (int j = 1; j <= col; j++) {
				newPos = pos - j;
				if (whites.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (king.get(newPos)) {
					return true;
				}
			}

		}
		case BLACK_PAWN -> {
			for (int j = 1; j <= col; j++) {
				newPos = pos - j;
				if (king.get(newPos) || whites.get(newPos) || throne.get(newPos)) {
					break;
				} else if (blacks.get(newPos)) {
					return true;
				}
			}
		}
		}
		return false;

	}

	private boolean isSquareUnderRightAttackOf(PieceType toCheck, int pos, int row, int col) {
		int newPos = -1;

		switch (toCheck) {
		case WHITE_PAWN -> {
			for (int j = 1; j < BOARD_SIZE - col; j++) {
				newPos = pos + j;
				if (king.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (whites.get(newPos)) {
					return true;
				}

			}

		}
		case WHITE_KING -> {
			for (int j = 1; j < BOARD_SIZE - col; j++) {
				newPos = pos + j;
				if (whites.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (king.get(newPos)) {
					return true;
				}

			}

		}
		case BLACK_PAWN -> {
			for (int j = 1; j < BOARD_SIZE - col; j++) {
				newPos = pos + j;
				if (king.get(newPos) || whites.get(newPos) || throne.get(newPos)) {
					break;
				} else if (blacks.get(newPos)) {
					return true;
				}
			}
		}
		}
		return false;

	}

	private boolean isSquareUnderDownAttackOf(PieceType toCheck, int pos, int row, int col) {
		int newPos = -1;

		switch (toCheck) {
		case WHITE_PAWN -> {
			for (int j = 1; j <= row; j++) {
				newPos = pos + j * BOARD_SIZE;
				if (king.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (whites.get(newPos)) {
					return true;
				}

			}

		}
		case WHITE_KING -> {
			for (int j = 1; j <= row; j++) {
				newPos = pos + j * BOARD_SIZE;
				if (whites.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (king.get(newPos)) {
					return true;
				}
			}

		}
		case BLACK_PAWN -> {
			for (int j = 1; j <= row; j++) {
				newPos = pos + j * BOARD_SIZE;
				if (king.get(newPos) || whites.get(newPos) || throne.get(newPos)) {
					break;
				} else if (blacks.get(newPos)) {
					return true;
				}
			}
		}
		}
		return false;

	}

	private boolean isSquareUnderUpAttackOf(PieceType toCheck, int pos, int row, int col) {
		int newPos = -1;

		switch (toCheck) {
		case WHITE_PAWN -> {
			for (int j = 1; j <= row; j++) {
				newPos = pos - j * BOARD_SIZE;
				if (king.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (whites.get(newPos)) {
					return true;
				}

			}

		}
		case WHITE_KING -> {
			for (int j = 1; j <= row; j++) {
				newPos = pos - j * BOARD_SIZE;
				if (whites.get(newPos) || blacks.get(newPos) || throne.get(newPos) || camps.get(newPos)) {
					break;
				} else if (king.get(newPos)) {
					return true;
				}
			}

		}
		case BLACK_PAWN -> {
			for (int j = 1; j <= row; j++) {
				newPos = pos - j * BOARD_SIZE;
				if (king.get(newPos) || whites.get(newPos) || throne.get(newPos)) {
					break;
				} else if (blacks.get(newPos)) {
					return true;
				}
			}
		}
		}
		return false;

	}

	private boolean isSquareUnderAttackOf(PieceType toCheck, int pos, int row, int col) {
		if (blacks.get(pos) || whites.get(pos) || king.get(pos)) {
			return false;
		}
		return this.isSquareUnderLeftAttackOf(toCheck, pos, row, col)
				|| this.isSquareUnderRightAttackOf(toCheck, pos, row, col)
				|| this.isSquareUnderUpAttackOf(toCheck, pos, row, col)
				|| this.isSquareUnderDownAttackOf(toCheck, pos, row, col);
	}

	private boolean isBlackThreatened(int pos, int row, int col) {
		if (camps.get((row * BOARD_SIZE) + col)) {
			return false;
		}

		if (col < BOARD_SIZE - 1 && col > 0) {
			// right threat
			if (whites.get(pos - 1) || king.get(pos - 1) || camps.get(pos - 1) || throne.get(pos - 1)) {
				if (isSquareUnderAttackOf(PieceType.WHITE_KING, pos + 1, row, col + 1)
						|| isSquareUnderAttackOf(PieceType.WHITE_PAWN, pos + 1, row, col + 1)) {
					return true;
				}
			}
			// left threat
			if (whites.get(pos + 1) || king.get(pos + 1) || camps.get(pos + 1) || throne.get(pos + 1)) {
				if (isSquareUnderAttackOf(PieceType.WHITE_KING, pos - 1, row, col - 1)
						|| isSquareUnderAttackOf(PieceType.WHITE_PAWN, pos - 1, row, col - 1)) {
					return true;
				}
			}
		}

		if (row > 0 && row < BOARD_SIZE - 1) {
			// down threat
			if (whites.get(pos - BOARD_SIZE) || king.get(pos - BOARD_SIZE) || camps.get(pos - BOARD_SIZE)
					|| throne.get(pos - BOARD_SIZE)) {
				if (isSquareUnderAttackOf(PieceType.WHITE_KING, pos + BOARD_SIZE, row + 1, col)
						|| isSquareUnderAttackOf(PieceType.WHITE_PAWN, pos + BOARD_SIZE, row + 1, col)) {
					return true;
				}

			}
			// up threat
			if (whites.get(pos + BOARD_SIZE) || king.get(pos + BOARD_SIZE) || camps.get(pos + BOARD_SIZE)
					|| throne.get(pos + BOARD_SIZE)) {
				if (isSquareUnderAttackOf(PieceType.WHITE_KING, pos - BOARD_SIZE, row - 1, col)
						|| isSquareUnderAttackOf(PieceType.WHITE_PAWN, pos - BOARD_SIZE, row - 1, col)) {
					return true;
				}
			}

		}
		return false;
	}

	private boolean isWhiteThreatened(int pos, int row, int col) {

		if (col < BOARD_SIZE - 1 && col > 0) {
			// right threat
			if (blacks.get(pos - 1) || camps.get(pos - 1) || throne.get(pos - 1)) {
				if (isSquareUnderAttackOf(PieceType.BLACK_PAWN, pos + 1, row, col + 1)) {
					return true;
				}
			}
			// left threat
			if (blacks.get(pos + 1) || camps.get(pos + 1) || throne.get(pos + 1)) {
				if (isSquareUnderAttackOf(PieceType.BLACK_PAWN, pos - 1, row, col - 1)) {
					return true;
				}
			}
		}

		if (row > 0 && row < BOARD_SIZE - 1) {
			// down threat
			if (blacks.get(pos - BOARD_SIZE) || camps.get(pos - BOARD_SIZE) || throne.get(pos - BOARD_SIZE)) {
				if (isSquareUnderAttackOf(PieceType.BLACK_PAWN, pos + BOARD_SIZE, row + 1, col)) {
					return true;
				}
			}
			// up threat
			if (blacks.get(pos + BOARD_SIZE) || camps.get(pos + BOARD_SIZE) || throne.get(pos + BOARD_SIZE)) {
				if (isSquareUnderAttackOf(PieceType.BLACK_PAWN, pos - BOARD_SIZE, row - 1, col)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isKingThreatened(int pos, int row, int col) {

		int toCheck = -1;
		int toCheckRow = -1;
		int toCheckCol = -1;

		// trono
		if (this.king.intersects(throne)) {
			BitSet aroundThroneAndBlacks = (BitSet) aroundThrone.clone();
			aroundThroneAndBlacks.and(this.blacks);
			if (aroundThroneAndBlacks.cardinality() == 4) {
				return true;
			} else if (aroundThroneAndBlacks.cardinality() == 3) {
				aroundThroneAndBlacks.xor(aroundThrone);
				toCheck = aroundThroneAndBlacks.nextSetBit(0);
				toCheckRow = toCheck / BOARD_SIZE;
				toCheckCol = toCheck - toCheckRow * BOARD_SIZE;
				return isSquareUnderAttackOf(PieceType.BLACK_PAWN, toCheck, toCheckRow, toCheckCol);
			}
		}

		// attorno al trono
		else if (this.king.intersects(aroundThrone)) {

			BitSet surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedLeft.clone();
			surroundedThroneAndBlack.and(this.blacks);
			if (surroundedThroneAndBlack.cardinality() == 2) {
				surroundedThroneAndBlack.xor(aroundThroneSurroundedLeft);
				toCheck = surroundedThroneAndBlack.nextSetBit(0);
				toCheckRow = toCheck / BOARD_SIZE;
				toCheckCol = toCheck - toCheckRow * BOARD_SIZE;
				return isSquareUnderAttackOf(PieceType.BLACK_PAWN, toCheck, toCheckRow, toCheckCol);
			}

			surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedRight.clone();
			surroundedThroneAndBlack.and(this.blacks);
			if (surroundedThroneAndBlack.cardinality() == 2) {
				surroundedThroneAndBlack.xor(aroundThroneSurroundedRight);
				toCheck = surroundedThroneAndBlack.nextSetBit(0);
				toCheckRow = toCheck / BOARD_SIZE;
				toCheckCol = toCheck - toCheckRow * BOARD_SIZE;
				return isSquareUnderAttackOf(PieceType.BLACK_PAWN, toCheck, toCheckRow, toCheckCol);
			}

			surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedUp.clone();
			surroundedThroneAndBlack.and(this.blacks);
			if (surroundedThroneAndBlack.cardinality() == 2) {
				surroundedThroneAndBlack.xor(aroundThroneSurroundedUp);
				toCheck = surroundedThroneAndBlack.nextSetBit(0);
				toCheckRow = toCheck / BOARD_SIZE;
				toCheckCol = toCheck - toCheckRow * BOARD_SIZE;
				return isSquareUnderAttackOf(PieceType.BLACK_PAWN, toCheck, toCheckRow, toCheckCol);
			}

			surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedDown.clone();
			surroundedThroneAndBlack.and(this.blacks);
			if (surroundedThroneAndBlack.cardinality() == 2) {
				surroundedThroneAndBlack.xor(aroundThroneSurroundedDown);
				toCheck = surroundedThroneAndBlack.nextSetBit(0);
				toCheckRow = toCheck / BOARD_SIZE;
				toCheckCol = toCheck - toCheckRow * BOARD_SIZE;
				return isSquareUnderAttackOf(PieceType.BLACK_PAWN, toCheck, toCheckRow, toCheckCol);
			}
		}
		// vicino barriera/campo + posizione generica
		return this.isWhiteThreatened(pos, row, col);
	}

	private void initAvaliableActionsAndStats() {
		this.availableActions = new ArrayList<>();
		this.whitePawnsThreatened = 0;
		this.blackPawnsThreatened = 0;

		int pos = -1;
		int row = -1;
		int col = -1;
		int newPos = -1;

		BitSet whiteInvalid = new BitSet(BOARD_SIZE * BOARD_SIZE);
		whiteInvalid.or(throne);
		whiteInvalid.or(king);
		whiteInvalid.or(whites);
		whiteInvalid.or(camps);
		whiteInvalid.or(blacks);

		BitSet blackBaseInvalid = new BitSet(BOARD_SIZE * BOARD_SIZE);
		blackBaseInvalid.or(throne);
		blackBaseInvalid.or(king);
		blackBaseInvalid.or(whites);

		BitSet blackInvalid = new BitSet(BOARD_SIZE * BOARD_SIZE);

		if (turn == Turn.WHITE) {
			// king
			// System.out.println("analyzing king");
			pos = this.king.nextSetBit(0);
			if (pos >= 0) {
				row = pos / BOARD_SIZE;
				col = pos - row * BOARD_SIZE;

				// checking if king is threatened
				kingThreatened = isKingThreatened(pos, row, col);

				// checking moves
				// moving right
				for (int j = 1; j < BOARD_SIZE - col; j++) {
					newPos = pos + j;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_KING, pos, newPos));
				}

				// moving left
				for (int j = 1; j <= col; j++) {
					newPos = pos - j;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_KING, pos, newPos));
				}

				// moving down
				for (int j = 1; j < BOARD_SIZE - row; j++) {
					newPos = pos + j * BOARD_SIZE;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_KING, pos, newPos));
				}

				// moving up
				for (int j = 1; j <= row; j++) {
					newPos = pos - j * BOARD_SIZE;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_KING, pos, newPos));
				}
			}

			// System.out.println(whites.cardinality());
			// white pawns
			for (pos = whites.nextSetBit(0); pos >= 0; pos = whites.nextSetBit(pos + 1)) {
				if (pos == Integer.MAX_VALUE) {
					break;
				}
				// System.out.println("white analyzing " + pos);
				row = pos / BOARD_SIZE;
				col = pos - row * BOARD_SIZE;

				// checking if pawn is threatened
				if (isWhiteThreatened(pos, row, col)) {
					whitePawnsThreatened++;
					// System.out.println("threatened");
				}

				// moving right
				for (int j = 1; j < BOARD_SIZE - col; j++) {
					newPos = pos + j;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_PAWN, pos, newPos));
				}

				// moving left
				for (int j = 1; j <= col; j++) {
					newPos = pos - j;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_PAWN, pos, newPos));
				}

				// moving down
				for (int j = 1; j < BOARD_SIZE - row; j++) {
					newPos = pos + j * BOARD_SIZE;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_PAWN, pos, newPos));
				}

				// moving up
				for (int j = 1; j <= row; j++) {
					newPos = pos - j * BOARD_SIZE;
					if (whiteInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.WHITE_PAWN, pos, newPos));
				}
			}

			// check black captures
			for (pos = blacks.nextSetBit(0); pos >= 0; pos = blacks.nextSetBit(pos + 1)) {
				if (pos == Integer.MAX_VALUE) {
					break;
				}
				// System.out.println("black analyzing " + pos);
				row = pos / BOARD_SIZE;
				col = pos - row * BOARD_SIZE;

				// checking if pawn is threatened
				if (isBlackThreatened(pos, row, col)) {
					blackPawnsThreatened++;
					// System.out.println("threatened");
				}
			}

		} else {

			// black pawns
			for (pos = blacks.nextSetBit(0); pos >= 0; pos = blacks.nextSetBit(pos + 1)) {
				if (pos == Integer.MAX_VALUE) {
					break;
				}
				// System.out.println("black analyzing " + pos);
				row = pos / BOARD_SIZE;
				col = pos - row * BOARD_SIZE;

				// checking if pawn is threatened
				if (isBlackThreatened(pos, row, col)) {
					blackPawnsThreatened++;
					// System.out.println("threatened");
				}

				// restore blackInvalid bitboard
				blackInvalid.clear();
				blackInvalid.or(blackBaseInvalid);
				blackInvalid.or(camps);
				// if startPos is inside a camp set that camp's positions to valid
				if (campRight.get(pos)) {
					blackInvalid.xor(campRight);
				} else if (campLeft.get(pos)) {
					blackInvalid.xor(campLeft);
				} else if (campUp.get(pos)) {
					blackInvalid.xor(campUp);
				} else if (campDown.get(pos)) {
					blackInvalid.xor(campDown);
				}
				// else set every camp's position to invalid
				blackInvalid.or(blacks);

				// moving right
				for (int j = 1; j < BOARD_SIZE - col; j++) {
					newPos = pos + j;
					if (blackInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.BLACK_PAWN, pos, pos + j));
				}

				// moving left
				for (int j = 1; j <= col; j++) {
					newPos = pos - j;
					if (blackInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.BLACK_PAWN, pos, newPos));
				}

				// moving down
				for (int j = 1; j < BOARD_SIZE - row; j++) {
					newPos = pos + j * BOARD_SIZE;
					if (blackInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.BLACK_PAWN, pos, newPos));
				}

				// moving up
				for (int j = 1; j <= row; j++) {
					newPos = pos - j * BOARD_SIZE;
					if (blackInvalid.get(newPos)) {
						break;
					}
					availableActions.add(new MILTAction(PieceType.BLACK_PAWN, pos, newPos));
				}
			}

			// check whites threatened
			for (pos = whites.nextSetBit(0); pos >= 0; pos = whites.nextSetBit(pos + 1)) {
				if (pos == Integer.MAX_VALUE) {
					break;
				}
				// System.out.println("white analyzing " + pos);
				row = pos / BOARD_SIZE;
				col = pos - row * BOARD_SIZE;

				// checking if pawn is threatened
				if (isWhiteThreatened(pos, row, col)) {
					whitePawnsThreatened++;
					// System.out.println("threatened");
				}
			}

			// check king threatened
			// System.out.println("analyzing king");
			pos = this.king.nextSetBit(0);
			if (pos >= 0) {
				row = pos / BOARD_SIZE;
				col = pos - row * BOARD_SIZE;

				// checking if king is threatened
				kingThreatened = isKingThreatened(pos, row, col);
			}

		}
	}

	public MILTState(Turn turn, BitSet whites, BitSet blacks, BitSet king, State.Turn state) {
		this.turn = turn;
		this.king = king;
		this.whites = whites;
		this.blacks = blacks;
		this.initAvaliableActionsAndStats();
		if (state == State.Turn.BLACKWIN) {
			king.clear();
		} else if (state == State.Turn.WHITEWIN) {
			king.clear();
			king.set(1);
		}

	}

	public MILTState(Turn turn, BitSet whites, BitSet blacks, BitSet king) {
		this.turn = turn;
		this.king = king;
		this.whites = whites;
		this.blacks = blacks;
		this.initAvaliableActionsAndStats();

	}

	public Turn getTurn() {
		return turn;
	}

	public BitSet getKing() {
		return king;
	}

	public BitSet getWhites() {
		return whites;
	}

	public BitSet getBlacks() {
		return blacks;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	public void setKing(BitSet king) {
		this.king = king;
	}

	public void setWhites(BitSet whites) {
		this.whites = whites;
	}

	public void setBlacks(BitSet blacks) {
		this.blacks = blacks;
	}

	public int getWhitePawnsThreatened() {
		return whitePawnsThreatened;
	}

	public int getBlackPawnsThreatened() {
		return blackPawnsThreatened;
	}

	public int getBlacksWhitesDiff() {
		return this.blacks.cardinality() - this.whites.cardinality();
	}

	public MILTState apply(MILTAction action) {
		BitSet newWhites = (BitSet) whites.clone();
		BitSet newBlacks = (BitSet) blacks.clone();
		BitSet newKing = (BitSet) king.clone();

		switch (action.pieceType()) {
		case WHITE_KING -> {
			newKing.clear(action.from());
			newKing.set(action.to());

			int row = action.to() / BOARD_SIZE;
			int col = action.to() - row * BOARD_SIZE;

			// capture left
			// aggiungi thrones cattura king
			if (col >= 2) {
				if (newBlacks.get(row * BOARD_SIZE + col - 1) && !camps.get(row * BOARD_SIZE + col - 1)) {
					if (newWhites.get(row * BOARD_SIZE + col - 2) || camps.get(row * BOARD_SIZE + col - 2)
							|| throne.get(row * BOARD_SIZE + col - 2)) {
						newBlacks.clear(row * BOARD_SIZE + col - 1);
					}
				}
			}

			// capture right
			if (col < BOARD_SIZE - 2) {
				if (newBlacks.get(row * BOARD_SIZE + col + 1) && !camps.get(row * BOARD_SIZE + col + 1)) {
					if (newWhites.get(row * BOARD_SIZE + col + 2) || camps.get(row * BOARD_SIZE + col + 2)
							|| throne.get(row * BOARD_SIZE + col + 2)) {
						newBlacks.clear(row * BOARD_SIZE + col + 1);
					}
				}

			}

			// capture up
			if (row >= 2) {
				if (newBlacks.get((row - 1) * BOARD_SIZE + col) && !camps.get((row - 1) * BOARD_SIZE + col)) {
					if (newWhites.get((row - 2) * BOARD_SIZE + col) || camps.get((row - 2) * BOARD_SIZE + col)
							|| throne.get((row - 2) * BOARD_SIZE + col)) {
						newBlacks.clear((row - 1) * BOARD_SIZE + col);
					}
				}

			}

			// capture down
			if (row < BOARD_SIZE - 2) {
				if (newBlacks.get((row + 1) * BOARD_SIZE + col) && !camps.get((row + 1) * BOARD_SIZE + col)) {
					if (newWhites.get((row + 2) * BOARD_SIZE + col) || camps.get((row + 2) * BOARD_SIZE + col)
							|| throne.get((row + 2) * BOARD_SIZE + col)) {
						newBlacks.clear((row + 1) * BOARD_SIZE + col);
					}
				}
			}

		}
		case WHITE_PAWN -> {
			newWhites.clear(action.from());
			newWhites.set(action.to());

			int row = action.to() / BOARD_SIZE;
			int col = action.to() - row * BOARD_SIZE;

			// capture left
			if (col >= 2) {
				if (newBlacks.get(row * BOARD_SIZE + col - 1) && !camps.get(row * BOARD_SIZE + col - 1)) {
					if (newWhites.get(row * BOARD_SIZE + col - 2)
							|| newKing.get(row * BOARD_SIZE + col - 2) || camps.get(row * BOARD_SIZE + col - 2)
							|| throne.get(row * BOARD_SIZE + col - 2)) {
						newBlacks.clear(row * BOARD_SIZE + col - 1);
					}
				}
			}

			// capture right
			if (col < BOARD_SIZE - 2) {
				if (newBlacks.get(row * BOARD_SIZE + col + 1) && !camps.get(row * BOARD_SIZE + col + 1)) {
					if (newWhites.get(row * BOARD_SIZE + col + 2)
							|| newKing.get(row * BOARD_SIZE + col + 2) || camps.get(row * BOARD_SIZE + col + 2)
							|| throne.get(row * BOARD_SIZE + col + 2)) {
						newBlacks.clear(row * BOARD_SIZE + col + 1);
					}
				}

			}

			// capture up
			if (row >= 2) {
				if (newBlacks.get((row - 1) * BOARD_SIZE + col) && !camps.get((row - 1) * BOARD_SIZE + col)) {
					if (newWhites.get((row - 2) * BOARD_SIZE + col)
							|| newKing.get((row - 2) * BOARD_SIZE + col)
							|| camps.get((row - 2) * BOARD_SIZE + col) || throne.get((row - 2) * BOARD_SIZE + col)) {
						newBlacks.clear((row - 1) * BOARD_SIZE + col);
					}
				}

			}

			// capture down
			if (row < BOARD_SIZE - 2) {
				if (newBlacks.get((row + 1) * BOARD_SIZE + col) && !camps.get((row + 1) * BOARD_SIZE + col)) {
					if (newWhites.get((row + 2) * BOARD_SIZE + col)
							|| newKing.get((row + 2) * BOARD_SIZE + col)
							|| camps.get((row + 2) * BOARD_SIZE + col) || throne.get((row + 2) * BOARD_SIZE + col)) {
						newBlacks.clear((row + 1) * BOARD_SIZE + col);
					}
				}
			}

		}
		case BLACK_PAWN -> {
			newBlacks.clear(action.from());
			newBlacks.set(action.to());

			int row = action.to() / BOARD_SIZE;
			int col = action.to() - row * BOARD_SIZE;

			// capture pawns
			// capture left
			if (col >= 2) {
				if (newWhites.get(row * BOARD_SIZE + col - 1)) {
					if (newBlacks.get(row * BOARD_SIZE + col - 2) || camps.get(row * BOARD_SIZE + col - 2)
							|| throne.get(row * BOARD_SIZE + col - 2)) {
						newWhites.clear(row * BOARD_SIZE + col - 1);
					}
				}
			}

			// capture right
			if (col < BOARD_SIZE - 2) {
				if (newWhites.get(row * BOARD_SIZE + col + 1)) {
					if (newBlacks.get(row * BOARD_SIZE + col + 2) || camps.get(row * BOARD_SIZE + col + 2)
							|| camps.get(row * BOARD_SIZE + col + 2)) {
						newWhites.clear(row * BOARD_SIZE + col + 1);
					}
				}

			}

			// capture up
			if (row >= 2) {
				if (newWhites.get((row - 1) * BOARD_SIZE + col)) {
					if (newBlacks.get((row - 2) * BOARD_SIZE + col) || camps.get((row - 2) * BOARD_SIZE + col)
							|| throne.get((row - 2) * BOARD_SIZE + col)) {
						newWhites.clear((row - 1) * BOARD_SIZE + col);
					}
				}

			}

			// capture down
			if (row < BOARD_SIZE - 2) {
				if (newWhites.get((row + 1) * BOARD_SIZE + col)) {
					if (newBlacks.get((row + 2) * BOARD_SIZE + col) || camps.get((row + 2) * BOARD_SIZE + col)
							|| throne.get((row + 2) * BOARD_SIZE + col)) {
						newWhites.clear((row + 1) * BOARD_SIZE + col);
					}
				}
			}

			// capture king
			if (king.intersects(throne)) {
				if (aroundThrone.get(action.to())) {
					BitSet aroundThroneAndBlacks = (BitSet) throne.clone();
					aroundThroneAndBlacks.and(newBlacks);
					if (aroundThroneAndBlacks.cardinality() == 4) {
						newKing.clear();
					}

				}

			} else if (king.intersects(aroundThrone)) {
				if (aroundThroneSurroundedLeft.get(action.to())) {
					BitSet surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedLeft.clone();
					surroundedThroneAndBlack.and(newBlacks);
					if (surroundedThroneAndBlack.cardinality() == 3) {
						newKing.clear();
					}
				}
				if (aroundThroneSurroundedRight.get(action.to())) {
					BitSet surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedRight.clone();
					surroundedThroneAndBlack.and(newBlacks);
					if (surroundedThroneAndBlack.cardinality() == 3) {
						newKing.clear();
					}
				}
				if (aroundThroneSurroundedUp.get(action.to())) {
					BitSet surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedUp.clone();
					surroundedThroneAndBlack.and(newBlacks);
					if (surroundedThroneAndBlack.cardinality() == 3) {
						newKing.clear();
					}
				}
				if (aroundThroneSurroundedDown.get(action.to())) {
					BitSet surroundedThroneAndBlack = (BitSet) aroundThroneSurroundedDown.clone();
					surroundedThroneAndBlack.and(newBlacks);
					if (surroundedThroneAndBlack.cardinality() == 3) {
						newKing.clear();
					}
				}

			} else {
				// capture left
				if (col >= 2) {
					if (newKing.get(row * BOARD_SIZE + col - 1)) {
						if (newBlacks.get(row * BOARD_SIZE + col - 2)
								|| camps.get(row * BOARD_SIZE + col - 2)) {
							newKing.clear();
						}
					}
				}

				// capture right
				if (col < BOARD_SIZE - 2) {
					if (newKing.get(row * BOARD_SIZE + col + 1)) {
						if (newBlacks.get(row * BOARD_SIZE + col + 2)
								|| camps.get(row * BOARD_SIZE + col + 2)) {
							newKing.clear();
						}
					}

				}

				// capture up
				if (row >= 2) {
					if (newKing.get((row - 1) * BOARD_SIZE + col)) {
						if (newBlacks.get((row - 2) * BOARD_SIZE + col)
								|| camps.get((row - 2) * BOARD_SIZE + col)) {
							newKing.clear();
						}
					}

				}

				// capture down
				if (row < BOARD_SIZE - 2) {
					if (newKing.get((row + 1) * BOARD_SIZE + col)) {
						if (newBlacks.get((row + 2) * BOARD_SIZE + col)
								|| camps.get((row + 2) * BOARD_SIZE + col)
								|| throne.get((row + 2) * BOARD_SIZE + col)) {
							newKing.clear();
						}
					}
				}
			}

		}
		}

		Turn newTurn = switch (turn) {
		case WHITE -> Turn.BLACK;
		case BLACK -> Turn.WHITE;
		};

		MILTState result = new MILTState(newTurn, newWhites, newBlacks, newKing);

		return result;
	}

	public List<MILTAction> getAvailableActions() {
		return this.availableActions;
	}

	public boolean isTerminal() {
		return isWhiteWin() || isBlackWin();
	}

	public boolean isWhiteWin() {
		return escapes.intersects(king);
	}

	public boolean isBlackWin() {
		return king.isEmpty();

	}

	public double evaluation(MILTEvaluator evaluator) {
		return evaluator.evaluate(this);
	}

	
	public boolean isKingThreatened() {
		return kingThreatened;
	}

	public boolean hasKingEscapes() {

		int i = -1;
		int row = -1;
		int col = -1;
		int newPos = -1;

		BitSet whiteInvalid = new BitSet(BOARD_SIZE * BOARD_SIZE);
		whiteInvalid.or(throne);
		whiteInvalid.or(king);
		whiteInvalid.or(whites);
		whiteInvalid.or(camps);
		whiteInvalid.or(blacks);

		// king
		i = this.king.nextSetBit(0);
		if (i >= 0) {
			row = i / BOARD_SIZE;
			col = i - row * BOARD_SIZE;

			// moving right
			for (int j = 1; j < BOARD_SIZE - col; j++) {
				newPos = i + j;
				if (whiteInvalid.get(newPos)) {
					break;
				}
				if (escapes.get(newPos)) {
					return true;
				}
			}

			// moving left
			for (int j = 1; j <= col; j++) {
				newPos = i - j;
				if (whiteInvalid.get(newPos)) {
					break;
				}
				if (escapes.get(newPos)) {
					return true;
				}
			}

			// moving down
			for (int j = 1; j < BOARD_SIZE - row; j++) {
				newPos = (row + j) * BOARD_SIZE + col;
				if (whiteInvalid.get(newPos)) {
					break;
				}
				if (escapes.get(newPos)) {
					return true;
				}
			}

			// moving up
			for (int j = 1; j <= row; j++) {
				newPos = (row - j) * BOARD_SIZE + col;
				if (whiteInvalid.get(newPos)) {
					break;
				}
				if (escapes.get(newPos)) {
					return true;
				}
			}

		}
		return false;
	}

	public int getKingMovements() {
		int count = 4;

		BitSet invalid = new BitSet(BOARD_SIZE * BOARD_SIZE);
		invalid.or(camps);
		invalid.or(throne);
		invalid.or(king);
		invalid.or(whites);
		invalid.or(blacks);

		int i = this.king.nextSetBit(0);
		int row = this.king.nextSetBit(0) / BOARD_SIZE;
		int col = this.king.nextSetBit(0) - row * BOARD_SIZE;

		// moving right
		if (col < BOARD_SIZE - 1) {
			if (invalid.get(i + 1)) {
				count--;
			}
		}
		// moving left
		if (col > 0) {
			if (invalid.get(i - 1)) {
				count--;
			}
		}
		// moving down
		if (row < BOARD_SIZE - 1) {
			if (invalid.get((row + 1) * BOARD_SIZE + col)) {
				count--;
			}
		}
		// moving up
		if (row > 0) {
			if (invalid.get((row - 1) * BOARD_SIZE + col)) {
				count--;
			}
		}

		return count;
	}

}