package backgammon.model.engines.randomMove;

import java.util.Set;

import backgammon.model.engines.Engine;
import backgammon.model.game.Board;
import backgammon.model.game.MoveSequence;

public class RandomMoveEngine implements Engine {

	public MoveSequence calculateMove(Board board, Set<MoveSequence> possibleMoves){
		if(possibleMoves.size() == 0) return null;
		int index = (int) (Math.random() * (possibleMoves.size()));
		int i = 0;
		for(MoveSequence moveSequence : possibleMoves){
			if(i++ == index) return moveSequence;
		}
		return null;
	}
}
