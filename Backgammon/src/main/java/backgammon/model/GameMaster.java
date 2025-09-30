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
	private Engine engine;
	
	public GameMaster(BoardController boardController){
		this.boardController = boardController;
		this.engine = new Engine();
		
	}
	
	public void startGame(){
		gameState = GameStates.awaitingRoll;
		board = new Board();
		turns = new ArrayList<Turn>();
		boardController.updateBoard(board);
	}
	
	public void rollDice(){
		if(gameState != GameStates.awaitingRoll) return;
		currentTurn = new Turn(this);
		turns.add(currentTurn);
		gameState = GameStates.awaitingCheckerSelection;
		System.out.println("rollDice done");
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
			System.out.println("selected checker: "+i);
		} else System.out.println(currentTurn.possibleMoves.stream()
					.map(ms -> ms.moves().get(moveWithinTurn).from)
					.collect(Collectors.toSet()));
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
				if(ms.moves().size() == moveWithinTurn){
					turnFinished();
				}
			}
		}
	}

	public void turnFinished(){
		gameState = GameStates.awaitingComputer;
		board = engine.doComputedMove(board);
		boardController.updateBoard(board);
		gameState = GameStates.awaitingRoll;
		moveWithinTurn = 0;
	}
	
	public Board getBoard(){
		return board;
	}
	
	public BoardController getBoardController(){
		return boardController;
	}
}
