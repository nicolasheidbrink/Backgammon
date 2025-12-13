package backgammon.application.model.engines.randomMove;

import java.util.Set;

import backgammon.application.model.engines.Engine;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.gameModels.MoveSequence;

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
