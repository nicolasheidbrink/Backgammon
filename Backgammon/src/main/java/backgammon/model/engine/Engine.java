package backgammon.model.engine;

import java.util.Set;

import backgammon.model.game.Board;
import backgammon.model.game.CheckerColors;
import backgammon.model.game.Move;
import backgammon.model.game.MoveSequence;
import backgammon.model.gameCalculations.CalculationUtils;

public class Engine {

	public Board doComputedMove(Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = CalculationUtils.calculateAllPossibleMoveSequences(board, CheckerColors.X, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(board, possibleMoves);
		Board result = board;
		if(chosenMove == null) return result;
		for(Move move : chosenMove.moves()){
			result = result.doMove(CheckerColors.X, move.from, move.to);
		}
		return result;
	}

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
