package melissaILoveTablut;

import java.io.IOException;

public record MILTAction(MILTState.PieceType pieceType,int from, int to) {
    public it.unibo.ai.didattica.competition.tablut.domain.Action toAction() throws IOException{
    	
        int row=from/MILTState.BOARD_SIZE +1;
        int col=from-(row-1)*MILTState.BOARD_SIZE;

        String fromStr= Character.toString((char)('a'+col)) + row;

        row=to/MILTState.BOARD_SIZE +1;
        col=to-(row-1)*MILTState.BOARD_SIZE;

        String toStr=Character.toString((char)('a'+col))+row;
        

        it.unibo.ai.didattica.competition.tablut.domain.StateTablut.Turn turn=switch(pieceType){
            case BLACK_PAWN-> it.unibo.ai.didattica.competition.tablut.domain.StateTablut.Turn.BLACK;
            default -> it.unibo.ai.didattica.competition.tablut.domain.StateTablut.Turn.WHITE;
        };
        
        return new it.unibo.ai.didattica.competition.tablut.domain.Action(fromStr,toStr,turn);
    }
    
    public MILTAction reversed() {
    	return new MILTAction(pieceType,to,from);
    }
    
}