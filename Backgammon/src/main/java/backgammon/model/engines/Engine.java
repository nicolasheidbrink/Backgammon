package backgammon.model.engines;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import backgammon.model.gameCalculations.LegalMoveCalculation;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;
import backgammon.model.gameModels.Move;
import backgammon.model.gameModels.MoveSequence;

public interface Engine {
	public default Board doComputedMove(Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, CheckerColors.X, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(board, possibleMoves);
		Board result = board;
		if(chosenMove == null) return result;
		for(Move move : chosenMove.moves()){
			result = result.doMove(CheckerColors.X, move.from, move.to);
		}
		return result;
	}
	
	public default Board doComputedMoveAsO(Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, CheckerColors.O, leftDie, rightDie);
		MoveSequence chosenMove = calculateMoveAsO(board, possibleMoves);
		Board result = board;
		if(chosenMove == null) return result;
		for(Move move : chosenMove.moves()){
			result = result.doMove(CheckerColors.O, move.from, move.to);
		}
		return result;
	}
	
	public default List<Board> doComputedMoveWithSteps(Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, CheckerColors.X, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(board, possibleMoves);
		List<Board> inBetweenSteps = new ArrayList<>();
		if(chosenMove == null) {
			List<Board> listOfOnlyBoard = new ArrayList<>();
			listOfOnlyBoard.add(board);
			return listOfOnlyBoard;
		}
		Board temp = board;
		for(Move move : chosenMove.moves()){
			temp = temp.clone().doMove(CheckerColors.X, move.from, move.to);
			inBetweenSteps.add(temp);
		}
		return inBetweenSteps;
	}

	public MoveSequence calculateMove(Board board, Set<MoveSequence> possibleMoves);
	public MoveSequence calculateMoveAsO(Board board, Set<MoveSequence> possibleMoves);

}
