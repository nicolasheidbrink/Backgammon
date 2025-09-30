package backgammon.model;

import java.util.List;
import java.util.Set;

public class Turn {

	public List<Move> moves;
	public int leftDie;
	public int rightDie;
	private Board board;
	public Set<MoveSequence> possibleMoves;
	
	public Turn(GameMaster gameMaster){
		this.board = gameMaster.getBoard();
		this.leftDie = (int) (6*Math.random() + 1);
		this.rightDie = (int) (6*Math.random() + 1);
		board.leftDie = leftDie;
		board.rightDie = rightDie;
		System.out.println("i roll a "+leftDie+" and a "+rightDie);
		gameMaster.getBoardController().updateBoard(gameMaster.getBoard());
		possibleMoves = CalculationUtils.calculatePossibleMovesForO(board, leftDie, rightDie);
	}
}
