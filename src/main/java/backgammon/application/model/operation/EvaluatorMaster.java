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
	private int selectedChecker = 0;

	public EvaluatorMaster(ProgramMaster programMaster, BoardController boardController) {
		this.programMaster = programMaster;
		this.boardController = boardController;
		boardController.setGamemodeMaster(this);
		this.board = new Board();
		
		alwaysShowBarCheckers = true;
		boardController.updateBoard(board);
		boardController.updateEval(GameCalculation.calculateRuleBasedEval(board), GameCalculation.calculateNeuralNetworkEval(board));
		boardController.updateBoard(board, Integer.MAX_VALUE, Set.of(new MoveSequence(List.of(new Move(CheckerColors.O, 23, Integer.MAX_VALUE, 24)), null, null)), 0);
		gameState = GameStates.AWAITING_CHECKER_SELECTION;
	}

	@Override
	public void pointClicked(int i) {

		if(gameState == GameStates.AWAITING_CHECKER_SELECTION && i == -1 && selectedColor != CheckerColors.O){
			selectedColor = CheckerColors.O;
			selectedChecker = -1;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, Integer.MAX_VALUE, Set.of(new MoveSequence(List.of(new Move(CheckerColors.X, 0, Integer.MAX_VALUE, 24)), null, null)), 0);
			return;
		}
		if(gameState == GameStates.AWAITING_CHECKER_SELECTION && i == 24 && selectedColor != CheckerColors.X){
			selectedColor = CheckerColors.X;
			selectedChecker = -1;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, Integer.MAX_VALUE, Set.of(new MoveSequence(List.of(new Move(CheckerColors.O, 23, Integer.MAX_VALUE, 24)), null, null)), 0);
			return;
		}
		if(gameState != GameStates.AWAITING_DESTINATION_SELECTION) return;
		tryDoingMove(selectedColor, selectedChecker, i);
	}

	@Override
	public void checkerClicked(int i) {
		if(gameState == GameStates.AWAITING_DESTINATION_SELECTION && i == -1 && selectedColor == CheckerColors.X){
			tryDoingMove(selectedColor, selectedChecker, i);
			return;
		}
		if(gameState == GameStates.AWAITING_DESTINATION_SELECTION && i == 24 && selectedColor == CheckerColors.O){
			tryDoingMove(selectedColor, selectedChecker, i);
			return;
		}
		
		if(gameState != GameStates.AWAITING_CHECKER_SELECTION) return;
		if(i == -1){
			if(board.barX == 0) return;
			selectedColor = CheckerColors.X;
			selectedChecker = i;
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
			boardController.updateBoard(board, i, Set.of(), 0);
		}
		else if(i == 24){
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
		boardController.updateBoard(board);
		selectedChecker = 0;
		selectedColor = CheckerColors.NA;
		gameState = GameStates.AWAITING_CHECKER_SELECTION;

	}
	
	@Override
	public void startGame() {
	}
	@Override
	public void diceButtonClicked() {		
	}
}
