package backgammon.model.selfPlay;

import backgammon.model.engines.Engine;
import backgammon.model.engines.randomMove.RandomMoveEngine;
import backgammon.model.engines.ruleBased.RuleBasedEngine;
import backgammon.model.engines.tester.RuleBasedEngineTester;
import backgammon.model.gameCalculations.GameCalculation;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;

public class SelfPlayer {

	public Engine engineO;
	public Engine engineX;
	
	int scoreO = 0;
	int scoreX = 0;
	
	int n = 1000;
	int[] results = new int[n];
	int i = 0;
	
	Board board;
	CheckerColors currentTurn;
	
	public SelfPlayer(){
		engineO = new RuleBasedEngine();
		engineX = new RuleBasedEngineTester();
		
		this.board = new Board();
		currentTurn = CheckerColors.O;
		for(int i = 0; i < n; i++){
			System.out.println("\n\n\n\ni: "+i+"\n");
			playTurn();
		}
		double X_n = ((double) (scoreO - scoreX)) / n;
		double s = Math.sqrt(
						Arrays.stream(results)
								.asDoubleStream()
								.map(x -> Math.pow(x - X_n, 2))
								.sum() / (n-1)
					);
		double avgPointsPerGame = (double) (scoreO + scoreX) / n;
		
		int numberOfOVictories = (int) Arrays.stream(results)
									.filter(x -> x > 0)
									.count();
 
		double t = (X_n - 0) / (s/Math.sqrt(n));
				
		double pValue = new NormalDistribution(0, 1).cumulativeProbability(t);
		System.out.println("After "+n+" games, EngineO won "+scoreO+" points and EngineX won "+scoreX+" points"+
				"\nThe amount of games won by O is "+numberOfOVictories+
				"\n\nThe average score per game is "+avgPointsPerGame+
				"\nThe average score per game that O won is "+(double) scoreO / numberOfOVictories+
				"\nThe average score per game that X won is "+(double) scoreX / (n-numberOfOVictories)+
				"\n\nThe mean score is "+X_n+
				"\nThe t Value is "+t+
				"\nThe p Value of O being better than X is "+pValue);
	}
	
	public void playTurn(){
		int leftRoll = (int) (Math.random() * 6.0 + 1);
		int rightRoll = (int) (Math.random() * 6.0 + 1);
		
		if(currentTurn == CheckerColors.O){
			board = engineO.doComputedMove(CheckerColors.O, board, leftRoll, rightRoll);
			if(board.getTray(currentTurn) == 15){
				int winFactor = GameCalculation.calculateWinFactor(board, CheckerColors.O);
				scoreO += winFactor;
				results[i++] = winFactor;
				System.out.println("\n*****O WINS WITH "+winFactor+"*****\n");
				System.out.println("ScoreO: "+scoreO+"\nScoreX: "+scoreX + "\n\n\n");
				board = new Board();
				return;
			}
			currentTurn = CheckerColors.X;
			playTurn();
		}
		else{
			board = engineX.doComputedMove(CheckerColors.X, board, leftRoll, rightRoll);
			if(board.getTray(currentTurn) == 15){
				int winFactor = GameCalculation.calculateWinFactor(board, CheckerColors.X);
				scoreX += winFactor;
				results[i++] = - winFactor;
				System.out.println("\n\n*****X WINS WITH "+winFactor+"*****\n");
				System.out.println("ScoreO: "+scoreO+"\nScoreX: "+scoreX + "\n\n\n");
				board = new Board();
				return;
			}
			currentTurn = CheckerColors.O;
			playTurn();
		}
	}
	
}
