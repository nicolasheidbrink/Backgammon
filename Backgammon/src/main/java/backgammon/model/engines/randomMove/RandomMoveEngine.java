package backgammon.model.engines.randomMove;

import java.util.Set;

import backgammon.model.engines.Engine;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;
import backgammon.model.gameModels.MoveSequence;

public class RandomMoveEngine implements Engine {

	@Override
	public MoveSequence calculateMove(CheckerColors color, Board board, Set<MoveSequence> possibleMoves){
		if(possibleMoves.size() == 0) return null;
		int index = (int) (Math.random() * (possibleMoves.size()));
		int i = 0;
		for(MoveSequence moveSequence : possibleMoves){
			if(i++ == index) return moveSequence;
		}
		return null;
	}
}
