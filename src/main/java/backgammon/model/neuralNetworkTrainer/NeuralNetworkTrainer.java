package backgammon.model.neuralNetworkTrainer;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import backgammon.model.engines.Engine;
import backgammon.model.engines.neuralNetwork.NeuralNetworkEngine;
import backgammon.model.engines.ruleBased.RuleBasedEngine;
import backgammon.model.gameCalculations.GameCalculation;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;

public class NeuralNetworkTrainer {

	public static Engine engineO;
	public static Engine engineX;
			
	public static Board board;

	public static List<Game> games;

	
	public static void main(String[] args){
		
		engineO = new NeuralNetworkEngine();
		engineX = new NeuralNetworkEngine();

		for(int i = 0; i < Integer.MAX_VALUE; i++){

			System.out.println("Java data generation starting");
			games = new ArrayList<>();
			
			for(int j = 0; j < 1; j++){
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
		CheckerColors currentTurn;
		currentTurn = (Math.random() < 0.5) ? CheckerColors.O : CheckerColors.X;
		board.turn = currentTurn;
		List<State> positions = new ArrayList<>();
		List<State> reversePositions = new ArrayList<>();
		int gameScore = 0;
		int amountOfMoves = 0;
		while(gameScore == 0){
			positions.add(new State(board.parametrizeWithFlags()));
			reversePositions.add(new State(board.reverseParametrizeWithFlags()));
			gameScore = playTurn(currentTurn);
			currentTurn = currentTurn.opposite;
			if(amountOfMoves++ == 500) break;
		}
		if(amountOfMoves < 500){
			games.add(new Game(positions, gameScore));
			games.add(new Game(reversePositions, -gameScore));
		}
		else System.out.println("Game had too many moves and was not recorded");
	}
	
	public static int playTurn(CheckerColors currentTurn){
		int leftRoll = (int) (Math.random() * 6.0 + 1);
		int rightRoll = (int) (Math.random() * 6.0 + 1);

		if(currentTurn == CheckerColors.O){
			board = engineO.doComputedMove(CheckerColors.O, board, leftRoll, rightRoll);
			if(board.getTray(currentTurn) == 15){
				return GameCalculation.calculateWinFactor(board, CheckerColors.O);
			}
		}
		else{
			board = engineX.doComputedMove(CheckerColors.X, board, leftRoll, rightRoll);
			if(board.getTray(currentTurn) == 15){
				return - GameCalculation.calculateWinFactor(board, CheckerColors.X);
			}
		}
		return 0;
	}
}
