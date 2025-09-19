package backgammon.model;

import java.util.ArrayList;
import java.util.List;

import backgammon.controller.BoardController;

public class GameMaster {

	private BoardController boardController;
	public GameStates gameState;
	private Board board;
	public List<Turn> turns;
	
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
		turns.add(new Turn(this));
		gameState = GameStates.awaitingCheckerSelection;
	}

	
	
	public Board getBoard(){
		return board;
	}
	
	public BoardController getBoardController(){
		return boardController;
	}
}
