package backgammon.model.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import backgammon.controller.BoardController;
import backgammon.model.engine.Engine;
import backgammon.model.game.Board;
import backgammon.model.game.CheckerColors;
import backgammon.model.game.MoveSequence;
import backgammon.model.game.Turn;
import backgammon.model.gameCalculations.CalculationUtils;

public class GameMaster {

	private ProgramMaster programMaster;
	private BoardController boardController;
	public GameStates gameState;
	private Board board;
	public List<Turn> turns;
	private int selectedChecker = Integer.MIN_VALUE;
	private int moveWithinTurn;
	private Turn currentTurn;
	private Engine engine;
	
	public GameMaster(ProgramMaster programMaster, BoardController boardController){
		this.programMaster = programMaster;
		boardController.setGameMaster(this);
		this.boardController = boardController;
		this.engine = new Engine();
	}
	
	public void startGame(){
		gameState = GameStates.awaitingRoll;
		boardController.setDiceColor(true);
		board = new Board();
		turns = new ArrayList<Turn>();
		boardController.updateBoard(board);
	}
	
	public void rollDice(){
		if(gameState != GameStates.awaitingRoll) return;
		currentTurn = new Turn(this);
		turns.add(currentTurn);
		gameState = GameStates.awaitingCheckerSelection;
		boardController.setDiceColor(false);
		if(currentTurn.possibleMoves.size() == 0) turnFinished();
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
			board = CalculationUtils.doMoveForO(board, selectedChecker, i);
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
		gameState = GameStates.awaitingComputer;
		board = engine.doComputedMove(board);
		boardController.updateBoard(board);
		if(checkIfWon(board)) return;
		gameState = GameStates.awaitingRoll;
		boardController.setDiceColor(true);
		moveWithinTurn = 0;
	}
	
	public boolean checkIfWon(Board board){
		if(board.trayO == 15){
			int factor = calculateWinFactor(board, CheckerColors.O);
			programMaster.gameDone(CheckerColors.O, factor);
			return true;
		}
		if(board.trayX == 15){
			int factor = calculateWinFactor(board, CheckerColors.X);
			programMaster.gameDone(CheckerColors.X, factor);
			return true;
		}
		return false;
	}
	
	public int calculateWinFactor(Board board, CheckerColors winner){
		if(winner == CheckerColors.O){
			if(board.barX > 0
					|| IntStream.range(0, 6)
						.mapToObj(i -> board.points[i].occupiedBy)
						.collect(Collectors.toSet())
						.contains(CheckerColors.X))
				return 3;
			if(IntStream.range(6, 18)
						.mapToObj(i -> board.points[i].occupiedBy)
						.collect(Collectors.toSet())
						.contains(CheckerColors.X))
				return 2;
			return 1;
					
		}
		if(winner == CheckerColors.X){
			if(board.barO > 0
					|| IntStream.range(18, 24)
						.mapToObj(i -> board.points[i].occupiedBy)
						.collect(Collectors.toSet())
						.contains(CheckerColors.O))
				return 3;
			if(IntStream.range(6, 18)
						.mapToObj(i -> board.points[i].occupiedBy)
						.collect(Collectors.toSet())
						.contains(CheckerColors.O))
				return 2;
			return 1;
					
		}
		return 0;
	}
	
	public Board getBoard(){
		return board;
	}
	
	public BoardController getBoardController(){
		return boardController;
	}
}
