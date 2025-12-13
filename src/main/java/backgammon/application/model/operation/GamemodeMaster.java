package backgammon.application.model.operation;

import backgammon.application.model.gameModels.Board;

public abstract class GamemodeMaster {

	private Board board;
	
	public abstract void diceButtonClicked();
	public abstract void pointClicked(int i);
	public abstract void checkerClicked(int i);
	public abstract void startGame();
	
	public Board getBoard(){
		return board;
	}
}
