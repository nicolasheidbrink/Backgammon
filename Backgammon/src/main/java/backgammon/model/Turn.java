package backgammon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Turn {

	public List<Move> moves;
	public int leftDie;
	public int rightDie;
	private Board board;
	private int movesLeft;
	private Set<List<Move>> possibleMoves;
	
	public Turn(GameMaster gameMaster){
		this.board = gameMaster.getBoard();
		this.leftDie = (int) (6*Math.random() + 1);
		this.rightDie = (int) (6*Math.random() + 1);
		gameMaster.getBoardController().showDiceRoll(leftDie, rightDie);
		movesLeft = (leftDie == rightDie) ? 4 : 2;
		possibleMoves = CalculationUtils.calculatePossibleMovesForO(board, leftDie, rightDie);
	}
}
