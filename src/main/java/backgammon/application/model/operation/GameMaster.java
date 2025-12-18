package backgammon.application.model.operation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import backgammon.application.controller.BoardController;
import backgammon.application.model.engines.Engine;
import backgammon.application.model.engines.EngineTypes;
import backgammon.application.model.engines.neuralNetwork.NeuralNetworkEngine;
import backgammon.application.model.gameCalculations.GameCalculation;
import backgammon.application.model.gameCalculations.LegalMoveCalculation;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.gameModels.MoveSequence;

public class GameMaster extends GamemodeMaster {

	private ProgramMaster programMaster;
	private BoardController boardController;
	public GameStates gameState;
	private Board board;
	private int selectedChecker = Integer.MIN_VALUE;
	private int moveWithinTurn;
	private Set<MoveSequence> possibleMoves;
	private Engine engine;
	private List<Board> betweenBoards;

	
	public GameMaster(ProgramMaster programMaster, BoardController boardController){
		this.programMaster = programMaster;
		boardController.setGamemodeMaster(this);
		this.boardController = boardController;
		this.engine = programMaster.engineXType.createEngine();
	}
	
	public void startGame(){
		boardController.setDiceColorGreen(true);
		board = new Board();
		boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
		boardController.updateEval(GameCalculation.calculateRuleBasedEval(board), GameCalculation.calculateNeuralNetworkEval(board));
		boardController.updateBoard(board);
		if(programMaster.lastWinner == CheckerColors.NA) gameState = GameStates.AWAITING_FIRST_ROLL;
		else if(programMaster.lastWinner == CheckerColors.O) {
			board.turn = CheckerColors.O;
			gameState = GameStates.AWAITING_ROLL;
		}
		else if(programMaster.lastWinner == CheckerColors.X){
			board.turn = CheckerColors.X;
			turnFinished();
		}
	}
	
	@Override
	public void diceButtonClicked(){
		if(gameState == GameStates.AWAITING_ROLL) rollDice();
		else if(gameState == GameStates.AWAITING_FIRST_ROLL) rollFirstDice();
		else if(gameState == GameStates.AWAITING_NEXT) nextClicked();
		else if(gameState == GameStates.AWAITING_CHECKER_SELECTION && possibleMoves.size() == 0) turnFinished();

	}
	
	public void nextClicked(){
		if(betweenBoards.isEmpty() || betweenBoards == null){
			board.turn = CheckerColors.O;
			gameState = GameStates.AWAITING_ROLL;
			boardController.setDiceColorGreen(true);
		}
		else {
			board = betweenBoards.getFirst();
			boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
			boardController.updateBoard(board);
			if(checkIfWon(board)) return;
			betweenBoards.removeFirst();
			if(betweenBoards.isEmpty()){
				gameState = GameStates.AWAITING_ROLL;
				boardController.updateEval(GameCalculation.calculateRuleBasedEval(board), GameCalculation.calculateNeuralNetworkEval(board));
			}
		}
	}
	
	public void rollDice(){
		boardController.setDiceColorGreen(true);
		board.leftDie = (int) (6*Math.random() + 1);
		board.rightDie = (int) (6*Math.random() + 1);
		boardController.updateBoard(board);
		possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, CheckerColors.O, board.leftDie, board.rightDie);
		gameState = GameStates.AWAITING_CHECKER_SELECTION;
	}
	
	public void rollFirstDice(){
		board.leftDie = 0;
		board.rightDie = 0;
		while(board.leftDie == board.rightDie){
			board.leftDie = (int) (6*Math.random() + 1);
			board.rightDie = (int) (6*Math.random() + 1);
		}
		boardController.updateBoard(board);
		if(board.leftDie > board.rightDie){
			board.turn = CheckerColors.O;
			possibleMoves = LegalMoveCalculation.calculateAllPossibleMoveSequences(board, CheckerColors.O, board.leftDie, board.rightDie);
			gameState = GameStates.AWAITING_CHECKER_SELECTION;
		}
		else{
			board.turn = CheckerColors.X;
			boardController.setDiceColorGreen(false);
			engineMove(board.leftDie, board.rightDie);
		}

	}
	
	@Override
	public void checkerClicked(int i){
		if(gameState != GameStates.AWAITING_CHECKER_SELECTION && gameState != GameStates.AWAITING_DESTINATION_SELECTION) return;
		if(0 <= i && i <= 24 &&
				possibleMoves.stream()
					.map(ms -> ms.moves().get(moveWithinTurn).from)
					.collect(Collectors.toSet())
					.contains(Integer.valueOf(i))){
			selectedChecker = i;
			boardController.updateBoard(board, selectedChecker, possibleMoves, moveWithinTurn);
			gameState = GameStates.AWAITING_DESTINATION_SELECTION;
		}
	}

	@Override
	public void pointClicked(int i){
		if(gameState != GameStates.AWAITING_DESTINATION_SELECTION) return;
		if((i == CheckerColors.O.trayInt || (0 <= i && i < 24)) && 
				possibleMoves.stream()
					.map(ms -> ms.moves().get(moveWithinTurn))
					.filter(move -> move.from == selectedChecker)
					.filter(move -> move.to == i)
					.collect(Collectors.toList())
					.size() > 0){
			board = board.doMove(CheckerColors.O, selectedChecker, i);
			possibleMoves = possibleMoves.stream()
				.filter(ms -> ms.moves().get(moveWithinTurn).from == selectedChecker)
				.filter(ms -> ms.moves().get(moveWithinTurn).to == i)
				.collect(Collectors.toSet());
			selectedChecker = 67;
			boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
			boardController.updateBoard(board, selectedChecker, possibleMoves, moveWithinTurn);
			moveWithinTurn++;
			for(MoveSequence ms : possibleMoves){
				if(ms.moves().size() == moveWithinTurn || board.trayO == 15){
					turnFinished();
					break;
				}
			}
		}
	}

	public void turnFinished(){
		if(checkIfWon(board)) return;
		board.turn = CheckerColors.X;
		boardController.setDiceColorGreen(false);
		gameState = GameStates.AWAITING_COMPUTER;
		moveWithinTurn = 0;
		boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
		boardController.updateEval(GameCalculation.calculateRuleBasedEval(board), GameCalculation.calculateNeuralNetworkEval(board));
		engineMove();
	}
	
	public void engineMove(){
		int leftDie = (int) (6*Math.random() + 1);
		int rightDie = (int) (6*Math.random() + 1);
		engineMove(leftDie, rightDie);
	}
	
	public void engineMove(int leftDie, int rightDie){
		board.leftDie = leftDie;
		board.rightDie = rightDie;
		boardController.updateBoard(board);
		betweenBoards = engine.doComputedMoveWithSteps(CheckerColors.X, board, leftDie, rightDie);
		gameState = GameStates.AWAITING_NEXT;
		if(checkIfWon(board)) return;
	}
	
	public boolean checkIfWon(Board board){
		if(board.trayO == 15){
			int factor = GameCalculation.calculateWinFactor(board, CheckerColors.O);
			programMaster.gameDone(CheckerColors.O, factor);
			return true;
		}
		if(board.trayX == 15){
			int factor = GameCalculation.calculateWinFactor(board, CheckerColors.X);
			programMaster.gameDone(CheckerColors.X, factor);
			return true;
		}
		return false;
	}
	
	public BoardController getBoardController(){
		return boardController;
	}
}
