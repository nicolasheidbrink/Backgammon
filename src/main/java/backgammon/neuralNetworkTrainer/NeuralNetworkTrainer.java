package backgammon.neuralNetworkTrainer;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import backgammon.application.model.engines.Engine;
import backgammon.application.model.engines.neuralNetwork.NeuralNetworkEngine;
import backgammon.application.model.engines.ruleBased.RuleBasedEngine;
import backgammon.application.model.gameCalculations.GameCalculation;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.selfPlay.SelfPlayer;

public class NeuralNetworkTrainer {

	public static Engine engineO;
	public static Engine engineX;
			
	public static Board board;

	public static List<Game> games;

	
	public static void main(String[] args){
		
		engineO = new NeuralNetworkEngine(true);
		engineX = new NeuralNetworkEngine(true);

		int ooo = 1;
		
		for(int i = 0; i < Integer.MAX_VALUE; i++){

			if(ooo++ % 20 == 0) SelfPlayer.main(args);;
			System.out.println("Java data generation starting");
			games = new ArrayList<>();
			
			for(int j = 0; j < 5; j++){
				System.out.println(i + "; " + j);
				try {
					playGame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try(FileWriter fw = new FileWriter("C:\\Users\\nicol\\Downloads\\training_data.csv")){
				gson.toJson(games, fw);
				System.out.println("generated data written to file");
			}
			catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("calling python trainer code");
			ProcessBuilder pb = new ProcessBuilder("python", "src/main/python/nn_trainer.py");
			pb.redirectErrorStream(true);
			try {
				Process process = pb.start();
				BufferedReader reader = new BufferedReader(
				        new InputStreamReader(process.getInputStream())
				);
				String line;
				while ((line = reader.readLine()) != null) {
				    System.out.println("python " + i + ": " + line);
				}
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void playGame() throws Exception{
		board = new Board();
		board.turn = (Math.random() < 0.5) ? CheckerColors.O : CheckerColors.X;
		List<State> positions = new ArrayList<>();
		int gameScore = 0;
		int amountOfMoves = 0;
		while(gameScore == 0){
//			if(board.turn == CheckerColors.O) 
				positions.add(new State(board.parametrizeWithFlags(), false));
//			else positions.add(new State(board.canonifyParametrizeWithFlags(), true));
			gameScore = playTurn();
			if(amountOfMoves++ == 500) break;
		}
		if(amountOfMoves < 500){
			games.add(new Game(positions, determineGameScoreVector(gameScore)));
		}
		else System.out.println("Game had too many moves and was not recorded");
	}
	
	public static int playTurn(){
		int leftRoll = (int) (Math.random() * 6.0 + 1);
		int rightRoll = (int) (Math.random() * 6.0 + 1);

		if(board.turn == CheckerColors.O){
			board = engineO.doComputedMove(CheckerColors.O, board, leftRoll, rightRoll);
			if(board.getTray(CheckerColors.O) == 15){
				return GameCalculation.calculateWinFactor(board, CheckerColors.O);
			}
		}
		else{
			board = engineX.doComputedMove(CheckerColors.X, board, leftRoll, rightRoll);
			if(board.getTray(CheckerColors.X) == 15){
				return - GameCalculation.calculateWinFactor(board, CheckerColors.X);
			}
		}
		return 0;
	}
	
	private static List<Integer> determineGameScoreVector(int gameScore){
		switch (gameScore){
			case 3:
				return List.of(1, 1, 1, 0, 0, 0);
			case 2:
				return List.of(0, 1, 1, 0, 0, 0);
			case 1:
				return List.of(0, 0, 1, 0, 0, 0);
			case -1:
				return List.of(0, 0, 0, 1, 0, 0);
			case -2:
				return List.of(0, 0, 0, 1, 1, 0);
			case -3:
				return List.of(0, 0, 0, 1, 1, 1);
			default: return null;
		}
	}
}
