package backgammon.model.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import backgammon.controller.BoardController;
import backgammon.model.engines.randomMove.RandomMoveEngine;
import backgammon.model.engines.ruleBased.RuleBasedEngine;
import backgammon.model.game.Board;
import backgammon.model.game.CheckerColors;
import backgammon.model.game.MoveSequence;
import backgammon.model.game.Turn;
import backgammon.model.gameCalculations.GameCalculation;
import backgammon.model.gameCalculations.LegalMoveCalculation;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class GameMaster {

	private ProgramMaster programMaster;
	private BoardController boardController;
	public GameStates gameState;
	private Board board;
	public List<Turn> turns;
	private int selectedChecker = Integer.MIN_VALUE;
	private int moveWithinTurn;
	private Turn currentTurn;
	private RuleBasedEngine engine; //////////////////////////////////////////////////////////////////////////////
	private List<Board> betweenBoards;

	
	public GameMaster(ProgramMaster programMaster, BoardController boardController){
		this.programMaster = programMaster;
		boardController.setGameMaster(this);
		this.boardController = boardController;
		this.engine = new RuleBasedEngine();
	}
	
	public void startGame(){
		boardController.setDiceColorGreen(true);
		board = new Board();
		turns = new ArrayList<Turn>();
		boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
		boardController.updateBoard(board);
		if(programMaster.lastWinner == CheckerColors.NA) gameState = GameStates.awaitingFirstRoll;
		else if(programMaster.lastWinner == CheckerColors.O) gameState = GameStates.awaitingRoll;
		else if(programMaster.lastWinner == CheckerColors.X){
			turnFinished();
		}
	}
	
	public void diceButtonClicked(){
		if(gameState == GameStates.awaitingRoll) rollDice();
		else if(gameState == GameStates.awaitingFirstRoll) rollFirstDice();
		else if(gameState == GameStates.awaitingNext) nextClicked();
	}
	
	public void nextClicked(){
		if(betweenBoards.isEmpty() || betweenBoards == null){
			gameState = GameStates.awaitingRoll;
			boardController.setDiceColorGreen(true);
		}
		else {
			board = betweenBoards.getFirst();
			boardController.updateBoard(board);
			betweenBoards.remove(0);
			if(betweenBoards.isEmpty()){
				gameState = GameStates.awaitingRoll;
			}
		}
	}
	
	public void rollDice(){
		boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
		boardController.setDiceColorGreen(true);
		board.leftDie = (int) (6*Math.random() + 1);
		board.rightDie = (int) (6*Math.random() + 1);
		currentTurn = new Turn(this, board.leftDie, board.rightDie);
		turns.add(currentTurn);
		gameState = GameStates.awaitingCheckerSelection;
		if(currentTurn.possibleMoves.size() == 0) turnFinished();
	}
	
	public void rollFirstDice(){
		board.leftDie = 0;
		board.rightDie = 0;
		while(board.leftDie == board.rightDie){
			board.leftDie = (int) (6*Math.random() + 1);
			board.rightDie = (int) (6*Math.random() + 1);
		}
		if(board.leftDie > board.rightDie){
			currentTurn = new Turn(this, board.leftDie, board.rightDie);
			turns.add(currentTurn);
			gameState = GameStates.awaitingCheckerSelection;
			if(currentTurn.possibleMoves.size() == 0) turnFinished();
		}
		else{
			boardController.setDiceColorGreen(false);
			engineMove(board.leftDie, board.rightDie);
		}

	}
	
	public void checkerClicked(int i){
		if(gameState != GameStates.awaitingCheckerSelection && gameState != GameStates.awaitingDestinationSelection) return;
		if(0 <= i && i <= 24 &&
				currentTurn.possibleMoves.stream()
					.map(ms -> ms.moves().get(moveWithinTurn).from)
					.collect(Collectors.toSet())
					.contains(Integer.valueOf(i))){
			selectedChecker = i;
			boardController.updateBoard(board, selectedChecker, currentTurn.possibleMoves, moveWithinTurn);
			gameState = GameStates.awaitingDestinationSelection;
		}
	}

	public void pointClicked(int i){
		if(gameState != GameStates.awaitingDestinationSelection) return;
		if(-1 <= i && i < 24 && 
				currentTurn.possibleMoves.stream()
					.map(ms -> ms.moves().get(moveWithinTurn))
					.filter(move -> move.from == selectedChecker)
					.filter(move -> move.to == i)
					.collect(Collectors.toList())
					.size() > 0){
			board = board.doMove(CheckerColors.O, selectedChecker, i);
			currentTurn.possibleMoves = currentTurn.possibleMoves.stream()
				.filter(ms -> ms.moves().get(moveWithinTurn).from == selectedChecker)
				.filter(ms -> ms.moves().get(moveWithinTurn).to == i)
				.collect(Collectors.toSet());
			selectedChecker = Integer.MIN_VALUE;
			boardController.updateBoard(board, selectedChecker, currentTurn.possibleMoves, moveWithinTurn);
			moveWithinTurn++;
			for(MoveSequence ms : currentTurn.possibleMoves){
				if(ms.moves().size() == moveWithinTurn || board.trayO == 15){
					turnFinished();
				}
			}
		}
	}

	public void turnFinished(){
		if(checkIfWon(board)) return;
		boardController.setDiceColorGreen(false);
		gameState = GameStates.awaitingComputer;
		moveWithinTurn = 0;
		boardController.updatePips(GameCalculation.calculatePips(board, CheckerColors.O), GameCalculation.calculatePips(board, CheckerColors.X));
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
		betweenBoards = engine.doComputedMoveWithSteps(board, leftDie, rightDie);
		gameState = GameStates.awaitingNext;
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
		
	public Board getBoard(){
		return board;
	}
	
	public BoardController getBoardController(){
		return boardController;
	}
}
