package backgammon.selfPlay;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import backgammon.application.model.engines.Engine;
import backgammon.application.model.engines.EngineTypes;
import backgammon.application.model.gameCalculations.GameCalculation;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;

public class SelfPlayer {

	// EDIT THESE: 
	private static int n = 500;
	private static EngineTypes engineOType = EngineTypes.NEURAL_NETWORK_ENGINE_WITHOUT_EXPLORATION;
	private static EngineTypes engineXType = EngineTypes.RULE_BASED_ENGINE;
	
	
	private static Engine engineO;
	private static Engine engineX;
	
	private static int scoreO = 0;
	private static int scoreX = 0;
	
	private static List<Integer> results = new ArrayList<>();
	private static List<Integer> moveCount = new ArrayList<>();
	
	private static int movesInOneGame = 0;
	
	private static Board board;
	private static CheckerColors currentTurn;
	
	public static void main(String[] args){
		initialize();
		
		for(int i = 0; i < n; i++){
			playGame(i);
		}
		
		conclusion();
	}

	private static void initialize(){
		engineO = engineOType.createEngine();
		engineX = engineXType.createEngine();
		
		board = new Board();
		currentTurn = CheckerColors.O;
	}

	
	private static void playGame(int i){
		System.out.println("\n\n\n\ni: "+i+"\n");
		movesInOneGame = 0;
		playTurn();
		System.out.println("That game had "+movesInOneGame+" moves\n\n");

	}
	
	private static void playTurn(){
		movesInOneGame++;
		int leftRoll = (int) (Math.random() * 6.0 + 1);
		int rightRoll = (int) (Math.random() * 6.0 + 1);
		
		if(currentTurn == CheckerColors.O){
			board = engineO.doComputedMove(CheckerColors.O, board, leftRoll, rightRoll);
			if(board.getTray(currentTurn) == 15){
				int winFactor = GameCalculation.calculateWinFactor(board, CheckerColors.O);
				scoreO += winFactor;
				moveCount.add(movesInOneGame);
				results.add(winFactor);
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
				moveCount.add(movesInOneGame);
				results.add(-winFactor);
				System.out.println("\n*****X WINS WITH "+winFactor+"*****\n");
				System.out.println("ScoreO: "+scoreO+"\nScoreX: "+scoreX + "\n\n\n");
				board = new Board();
				return;
			}
			currentTurn = CheckerColors.O;
			playTurn();
		}
	}
	
	private static void conclusion(){
		double X_n = ((double) (scoreO - scoreX)) / n;
		double s = Math.sqrt(
						results.stream()
								.mapToDouble(x -> Math.pow(x - X_n, 2))
								.sum() / (n-1)
					);
		double avgPointsPerGame = (double) (scoreO + scoreX) / n;
		
		int numberOfOVictories = (int) results.stream()
									.filter(x -> x > 0)
									.count();
 
		double t = (X_n - 0) / (s/Math.sqrt(n));
		
		double avgMovesPerGame = moveCount.stream()
				.mapToDouble(x -> Double.valueOf(x))
				.average()
				.orElse(-1);
				
		double pValue = new NormalDistribution(0, 1).cumulativeProbability(t);
		System.out.println("After "+n+" games, EngineO won "+scoreO+" points and EngineX won "+scoreX+" points"+
				"\nThe amount of games won by O is "+numberOfOVictories+
				"\n\nThe average absolute score per game is "+avgPointsPerGame+
				"\nThe average score per game that O won is "+(double) scoreO / numberOfOVictories+
				"\nThe average score per game that X won is "+(double) scoreX / (n-numberOfOVictories)+
				"\n\n*#*#*#*#*#*#*#*#*#* The mean score is "+X_n+" *#*#*#*#*#*#*#*#*#*"+ 
				"\nThe t Value is "+t+
				"\nThe standard deviation is "+s+
				"\nThe p Value of O being better than X is "+pValue+
				"\nThe average amount of moves per game was "+avgMovesPerGame);

	}
}
