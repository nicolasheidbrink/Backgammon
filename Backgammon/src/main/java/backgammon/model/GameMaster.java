package backgammon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import backgammon.controller.BoardController;

public class GameMaster {

	private BoardController boardController;
	public GameStates gameState;
	private Board board;
	public List<Turn> turns;
	private int selectedChecker = Integer.MIN_VALUE;
	private int moveWithinTurn;
	private Turn currentTurn;
	
	public GameMaster(BoardController boardController){
		this.boardController = boardController;
		
	}
	
	public void startGame(){
		gameState = GameStates.awaitingRoll;
		board = new Board();
		turns = new ArrayList<Turn>();
	}
	
	public void rollDice(){
		if(gameState != GameStates.awaitingRoll) return;
		currentTurn = new Turn(this);
		turns.add(currentTurn);
		gameState = GameStates.awaitingCheckerSelection;
	}
	
	public void checkerClicked(int i){
		if(gameState != GameStates.awaitingCheckerSelection && gameState != GameStates.awaitingDestinationSelection) return;
		if(0 <= i && i < 24 &&
				currentTurn.possibleMoves.stream()
					.map(ms -> ms.moves().get(moveWithinTurn))
					.map(move -> move.from)
					.findFirst()
					.orElse(-2) 
					== i){
			selectedChecker = i;
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
		boardController.updateBoard(board, selectedChecker, currentTurn.possibleMoves, moveWithinTurn);
		
		currentTurn.possibleMoves = currentTurn.possibleMoves.stream()
			.filter(ms -> ms.moves().get(moveWithinTurn).from == selectedChecker)
			.filter(ms -> ms.moves().get(moveWithinTurn).to == i)
			.collect(Collectors.toSet());
		selectedChecker = Integer.MIN_VALUE;
	}

		
	}

	
	public Board getBoard(){
		return board;
	}
	
	public BoardController getBoardController(){
		return boardController;
	}
}
