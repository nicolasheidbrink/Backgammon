package backgammon.model.engines.ruleBased;

import java.util.Set;

import backgammon.model.engines.Engine;
import backgammon.model.gameCalculations.LegalMoveCalculation;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;
import backgammon.model.gameModels.MoveSequence;

public class RuleBasedEngine implements Engine {

	@Override
	public MoveSequence calculateMove(Board board, Set<MoveSequence> possibleMoves) {
		MoveSequence tempBestMoveSeq = null;
		double tempBestEval = Double.MAX_VALUE;
		double currentEval;
		for(MoveSequence moveSequence : possibleMoves){
			currentEval = recursiveEvaluation(moveSequence.board(), 0, CheckerColors.X);
			if(currentEval < tempBestEval){
				tempBestEval = currentEval;
				tempBestMoveSeq = moveSequence;
			}
		}
		return tempBestMoveSeq;
	}
	
	public MoveSequence calculateMoveAsO(Board board, Set<MoveSequence> possibleMoves){
		MoveSequence tempBestMoveSeq = null;
		double tempBestEval = Double.MIN_VALUE;
		double currentEval;
		for(MoveSequence moveSequence : possibleMoves){
			currentEval = recursiveEvaluation(moveSequence.board(), 0, CheckerColors.O);
			if(currentEval > tempBestEval){
				tempBestEval = currentEval;
				tempBestMoveSeq = moveSequence;
			}
		}
		return tempBestMoveSeq;

	}
	
	public double recursiveEvaluation(Board board, int depth, CheckerColors mover){
		if(depth == 0) return PositionEvaluator.evaluatePosition(board);
		double evaluation = 0;
		for(int[] possibleRoll : possibleRolls){
			Set<MoveSequence> possibleMoveSequences = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, mover, possibleRoll[0], possibleRoll[1]);
			double tempBestEval = Double.MAX_VALUE * mover.direction;
			double currentEval;
			for(MoveSequence moveSequence : possibleMoveSequences){
				currentEval = recursiveEvaluation(moveSequence.board(), depth - 1, mover.opposite);
				if((mover == CheckerColors.X && currentEval < tempBestEval)
						|| (mover == CheckerColors.O && currentEval > tempBestEval)){
					tempBestEval = currentEval;
				}
			}
			evaluation += tempBestEval * possibleRoll[2] / 36;
		}
		return evaluation;
	}
	
	int[][] possibleRolls = new int[][]{
		{1,1,1},
		{1,2,2},
		{1,3,2},
		{1,4,2},
		{1,5,2},
		{1,6,2},
		{2,2,1},
		{2,3,2},
		{2,4,2},
		{2,5,2},
		{2,6,2},
		{3,3,1},
		{3,4,2},
		{3,5,2},
		{3,6,2},
		{4,4,1},
		{4,5,2},
		{4,6,2},
		{5,5,1},
		{5,6,2},
		{6,6,1}
	};

}
