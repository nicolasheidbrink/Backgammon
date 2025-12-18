package backgammon.application.model.operation;

import java.util.List;
import java.util.Set;

import backgammon.application.controller.BoardController;
import backgammon.application.model.engines.neuralNetwork.NeuralNetworkEngine;
import backgammon.application.model.engines.ruleBased.RuleBasedEngine;
import backgammon.application.model.gameCalculations.GameCalculation;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.gameModels.Move;
import backgammon.application.model.gameModels.MoveSequence;

public class EvaluatorMaster extends GamemodeMaster {

	private ProgramMaster programMaster;
	private BoardController boardController;
	public GameStates gameState;
	private Board board;
	
	private CheckerColors selectedColor = CheckerColors.NA;
	private int selectedChecker = 67;

	public EvaluatorMaster(ProgramMaster programMaster, BoardController boardController) {
		this.programMaster = programMaster;
		this.boardController = boardController;
		boardController.setGamemodeMaster(this);
		this.board = new Board();
		
		alwaysShowBarCheckers = true;
		boardController.updateBoard(board);
		boardController.updateEval(GameCalculation.calculateRuleBasedEval(board), GameCalculation.calculateNeuralNetworkEval(board));
		gameState = GameStates.AWAITING_CHECKER_SELECTION;
	}

	@Override
	public void pointClicked(int i) {

		if(gameState == GameStates.AWAITING_CHECKER_SELECTION && i == CheckerColors.O.trayInt){
			selectedColor = CheckerColors.O;
			selectedChecker = CheckerColors.O.trayInt;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, selectedChecker, Set.of(), 0);
			return;
		}
		if(gameState == GameStates.AWAITING_CHECKER_SELECTION && i == CheckerColors.X.trayInt){
			selectedColor = CheckerColors.X;
			selectedChecker = CheckerColors.X.trayInt;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, selectedChecker, Set.of(), 0);
			return;
		}
		if(gameState != GameStates.AWAITING_DESTINATION_SELECTION) return;
		tryDoingMove(selectedColor, selectedChecker, i);
	}

	@Override
	public void checkerClicked(int i) {
		if(gameState == GameStates.AWAITING_DESTINATION_SELECTION && i == CheckerColors.X.barInt && selectedColor == CheckerColors.X){
			tryDoingMove(selectedColor, selectedChecker, i);
			return;
		}
		if(gameState == GameStates.AWAITING_DESTINATION_SELECTION && i == CheckerColors.O.barInt && selectedColor == CheckerColors.O){
			tryDoingMove(selectedColor, selectedChecker, i);
			return;
		}
		
		if(gameState != GameStates.AWAITING_CHECKER_SELECTION) return;
		if(i == CheckerColors.X.barInt){
			if(board.barX == 0) return;
			selectedColor = CheckerColors.X;
			selectedChecker = i;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, i, Set.of(), 0);
		}
		else if(i == CheckerColors.O.barInt){
			if(board.barO == 0) return;
			selectedColor = CheckerColors.O;
			selectedChecker = i;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, i, Set.of(), 0);
		} else {
			selectedColor = board.points[i].occupiedBy;
			selectedChecker = i;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, i, Set.of(), 0);
		}
	}

	private void tryDoingMove(CheckerColors selectedColor, int selectedChecker, int destination){
		Board newBoard = board.doMove(selectedColor, selectedChecker, destination);
		if(newBoard != null){
			board = newBoard;
			boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
			boardController.updateEval(GameCalculation.calculateRuleBasedEval(board), GameCalculation.calculateNeuralNetworkEval(board));
		}
		selectedChecker = 67;
		selectedColor = CheckerColors.NA;
		boardController.updateBoard(board);
		gameState = GameStates.AWAITING_CHECKER_SELECTION;

	}
	
	@Override
	public void startGame() {
	}
	@Override
	public void diceButtonClicked() {		
	}
}
