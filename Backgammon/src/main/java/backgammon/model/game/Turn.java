package backgammon.model.game;

import java.util.List;
import java.util.Set;

import backgammon.model.gameCalculations.CalculationUtils;
import backgammon.model.operation.GameMaster;

public class Turn {

	public List<Move> moves;
	private Board board;
	public Set<MoveSequence> possibleMoves;
	
	public Turn(GameMaster gameMaster, int leftDie, int rightDie){
		this.board = gameMaster.getBoard();
		gameMaster.getBoardController().updateBoard(gameMaster.getBoard());
		possibleMoves = CalculationUtils.calculateAllPossibleMoveSequences(board, CheckerColors.O, leftDie, rightDie);
	}
}
