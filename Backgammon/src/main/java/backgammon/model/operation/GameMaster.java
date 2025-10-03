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
		boardController.setDiceColorGreen(true);
		board = new Board();
		turns = new ArrayList<Turn>();
		boardController.updateBoard(board);
	}
	
	public void rollDice(){
		if(gameState != GameStates.awaitingRoll) return;
		board.leftDie = (int) (6*Math.random() + 1);
		board.rightDie = (int) (6*Math.random() + 1);
		currentTurn = new Turn(this, board.leftDie, board.rightDie);
		turns.add(currentTurn);
		gameState = GameStates.awaitingCheckerSelection;
		boardController.setDiceColorGreen(false);
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
		gameState = GameStates.awaitingComputer;
		moveWithinTurn = 0;
		engineMove();
	}
	
	public void engineMove(){
		board.leftDie = (int) (6*Math.random() + 1);
		board.rightDie = (int) (6*Math.random() + 1);
		board = engine.doComputedMove(board, board.leftDie, board.rightDie);
		boardController.updateBoard(board);
		if(checkIfWon(board)) return;
		gameState = GameStates.awaitingRoll;
		boardController.setDiceColorGreen(true);
		
	}
	
	public boolean checkIfWon(Board board){
		if(board.trayO == 15){
			int factor = CalculationUtils.calculateWinFactor(board, CheckerColors.O);
			programMaster.gameDone(CheckerColors.O, factor);
			return true;
		}
		if(board.trayX == 15){
			int factor = CalculationUtils.calculateWinFactor(board, CheckerColors.X);
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
