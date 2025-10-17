package backgammon.model.selfPlay;

import backgammon.model.engines.Engine;
import backgammon.model.engines.randomMove.RandomMoveEngine;
import backgammon.model.engines.ruleBased.RuleBasedEngine;
import backgammon.model.gameCalculations.GameCalculation;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;

public class SelfPlayer {

	Engine engineO;
	Engine engineX;
	
	int scoreO = 0;
	int scoreX = 0;
	
	Board board;
	CheckerColors currentTurn;
	
	public SelfPlayer(){
		engineO = new RuleBasedEngine();
		engineX = new RuleBasedEngine();
		
		this.board = new Board();
		currentTurn = CheckerColors.O;
		for(int i = 0; i < 1000; i++){
			System.out.println("i: "+i+"\n\n");
			playTurn();
		}
	}
	
	public void playTurn(){
		int leftRoll = (int) (Math.random() * 6.0 + 1);
		int rightRoll = (int) (Math.random() * 6.0 + 1);
		
		if(currentTurn == CheckerColors.O){
			board = engineO.doComputedMoveAsO(board, leftRoll, rightRoll);
			System.out.println(board);
			if(board.getTray(currentTurn) == 15){
				int winFactor = GameCalculation.calculateWinFactor(board, CheckerColors.O);
				scoreO += winFactor;
				System.out.println("\n\n\n\n\n*****O WINS WITH "+winFactor+"*****\n\n\n\n\n");
				System.out.println("ScoreO: "+scoreO+"\nScoreX: "+scoreX + "\n\n\n");
				board = new Board();
				return;
			}
			currentTurn = CheckerColors.X;
			playTurn();
		}
		else{
			board = engineX.doComputedMove(board, leftRoll, rightRoll);
			System.out.println(board);
			if(board.getTray(currentTurn) == 15){
				int winFactor = GameCalculation.calculateWinFactor(board, CheckerColors.X);
				scoreX += winFactor;
				System.out.println("\n\n\n\n\n*****X WINS WITH "+winFactor+"*****\n\n\n\n\n");
				System.out.println("ScoreO: "+scoreO+"\nScoreX: "+scoreX + "\n\n\n");
				board = new Board();
				return;
			}
			currentTurn = CheckerColors.O;
			playTurn();
		}
	}
	
}
