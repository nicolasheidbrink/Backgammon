package backgammon.application.model.engines;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import backgammon.application.model.gameCalculations.LegalMoveCalculation;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.gameModels.Move;
import backgammon.application.model.gameModels.MoveSequence;

public interface Engine {
	public default Board doComputedMove(CheckerColors color, Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, color, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(color, board, possibleMoves);
		Board result = board;
		if(chosenMove == null) {
			board.turn = color.opposite;
			return result;
		}
		for(Move move : chosenMove.moves()){
			result = result.doMove(color, move.from, move.to);
		}
		result.turn = color.opposite;
		return result;
	}
	
	public default List<Board> doComputedMoveWithSteps(CheckerColors color, Board board, int leftDie, int rightDie){
		Set<MoveSequence> possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, color, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(color, board, possibleMoves);
		List<Board> inBetweenSteps = new ArrayList<>();
		if(chosenMove == null) {
			List<Board> listOfOnlyBoard = new ArrayList<>();
			listOfOnlyBoard.add(board);
			board.turn = color.opposite;
			return listOfOnlyBoard;
		}
		Board temp = board;
		temp.turn = color.opposite;
		for(Move move : chosenMove.moves()){
			temp = temp.clone().doMove(color, move.from, move.to);
			inBetweenSteps.add(temp);
		}
		return inBetweenSteps;
	}

	public MoveSequence calculateMove(CheckerColors color, Board board, Set<MoveSequence> possibleMoves);
}
