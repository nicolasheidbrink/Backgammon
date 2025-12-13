package backgammon.application.model.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import backgammon.application.controller.BoardController;
import backgammon.application.model.engines.Engine;
import backgammon.application.model.engines.randomMove.RandomMoveEngine;
import backgammon.application.model.engines.ruleBased.RuleBasedEngine;
import backgammon.application.model.gameCalculations.GameCalculation;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;

public class SpectateMaster extends GamemodeMaster{

	private ProgramMaster programMaster;
	private BoardController boardController;
	public GameStates gameState;
	private Board board;
	private Engine engineO;
	private Engine engineX;
	private CheckerColors currentTurn;
	private List<Board> currentMS;
	private Map<CheckerColors, Engine> engines = new HashMap<>();

	
	public SpectateMaster(ProgramMaster programMaster, BoardController boardController){
		this.programMaster = programMaster;
		board = new Board();
		boardController.updateBoard(board);
		boardController.setGamemodeMaster(this);
		this.boardController = boardController;
		this.engineO = programMaster.engineOType.createEngine();
		this.engineX = programMaster.engineXType.createEngine();
		engines.put(CheckerColors.O, engineO);
		engines.put(CheckerColors.X, engineX);
	}
	
	private void calculateTurn(){
		board.leftDie = (int) (Math.random() * 6.0 + 1);
		board.rightDie = (int) (Math.random() * 6.0 + 1);
		calculateTurn(board.leftDie, board.rightDie);
	}
	
	private void calculateTurn(int leftRoll, int rightRoll){
		currentMS = engines.get(currentTurn).doComputedMoveWithSteps(currentTurn, board, leftRoll, rightRoll);
		if(currentTurn == CheckerColors.O) boardController.setDiceColorGreen(true);
		else boardController.setDiceColorGreen(false);
		boardController.showDiceRoll(leftRoll, rightRoll);
	}

	private void showMove(){
		board = currentMS.get(0);
		currentMS.remove(0);
		boardController.updateBoard(board);
	}

	@Override
	public void diceButtonClicked() {
		boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
		if(board.getTray(currentTurn) == 15) programMaster.gameDone(currentTurn, GameCalculation.calculateWinFactor(board, currentTurn));
		if(currentMS == null || currentMS.size() == 0){
			currentTurn = currentTurn.opposite;
			calculateTurn();
		}
		else showMove();
	}
	
	public void startGame(){
		if(programMaster.lastWinner == CheckerColors.NA){
			int leftRoll = 0;
			int rightRoll = 0;
			while(leftRoll == rightRoll){
				leftRoll = (int) (Math.random() * 6.0 + 1);
				rightRoll = (int) (Math.random() * 6.0 + 1);
			}
			if(leftRoll > rightRoll) currentTurn = CheckerColors.O;
			else currentTurn = CheckerColors.X;
			board.leftDie = leftRoll;
			board.rightDie = rightRoll;
			calculateTurn(leftRoll, rightRoll);
		}
		else {
			currentTurn = programMaster.lastWinner;
			calculateTurn();
		}
	}

	@Override
	public void pointClicked(int i) {
	}
	@Override
	public void checkerClicked(int i) {
	}
}
