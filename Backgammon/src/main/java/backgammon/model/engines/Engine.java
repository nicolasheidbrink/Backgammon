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
	public default Board doComputedMove(CheckerColors color, Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, color, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(color, board, possibleMoves);
		Board result = board;
		if(chosenMove == null) return result;
		for(Move move : chosenMove.moves()){
			result = result.doMove(color, move.from, move.to);
		}
		return result;
	}
	
	public default List<Board> doComputedMoveWithSteps(CheckerColors color, Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, color, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(color, board, possibleMoves);
		List<Board> inBetweenSteps = new ArrayList<>();
		if(chosenMove == null) {
			List<Board> listOfOnlyBoard = new ArrayList<>();
			listOfOnlyBoard.add(board);
			return listOfOnlyBoard;
		}
		Board temp = board;
		for(Move move : chosenMove.moves()){
			temp = temp.clone().doMove(color, move.from, move.to);
			inBetweenSteps.add(temp);
		}
		return inBetweenSteps;
	}

	public MoveSequence calculateMove(CheckerColors color, Board board, Set<MoveSequence> possibleMoves);
}
