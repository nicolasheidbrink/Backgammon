package backgammon.neuralNetworkTrainer;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

	public static Engine[] engineO;
	public static Engine[] engineX;
			
	public static Board board;


	public static int amtOfCores = 7;
	
	
	public static void main(String[] args){
		engineO = new Engine[amtOfCores];
		engineX = new Engine[amtOfCores];
		for(int i = 0; i < amtOfCores; i++){
			engineO[i] = new NeuralNetworkEngine(true);
			engineX[i] = new NeuralNetworkEngine(true);
		}

				
		for(int i = 0; i < Integer.MAX_VALUE; i++){
			int ii = i;
			System.out.println("\n╠═════════════════════════════ Set of games #"+ii+" ═════════════════════════════╣");
			if(ii % 20 == 0) SelfPlayer.main(args);
			
			Runnable[] gameGenerationTask = new Runnable[amtOfCores];
			for(int j = 0; j < amtOfCores; j++){
				int jj = j;
				gameGenerationTask[j] = () -> {
					System.out.println("thread "+jj+": Java data generation starting on thread "+Thread.currentThread().getName());
					List<Game> gamesX = new ArrayList<>();
					
					for(int k = 0; k < 5; k++){
						System.out.println("set of games "+ii+ "; thread "+jj+"; game " + k);
						try {
							playGame(gamesX, jj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					try(FileWriter fw = new FileWriter("C:\\Users\\nicol\\Downloads\\training_data_"+jj+".csv")){
						gson.toJson(gamesX, fw);
						System.out.println("thread "+jj+": generated data written to file");
					}
					catch(Exception e){
						e.printStackTrace();
					}
				};
			}
			
			CompletableFuture<?>[] futures = Arrays.stream(gameGenerationTask)
		            .map(CompletableFuture::runAsync)
		            .toArray(CompletableFuture[]::new);
	
	        CompletableFuture.allOf(futures).join();
				
			System.out.println("calling python trainer code");
			
			ProcessBuilder pb = new ProcessBuilder("python", "src/main/python/nn_trainer_multicore.py");
			pb.redirectErrorStream(true);
			try {
				Process process = pb.start();
				BufferedReader reader = new BufferedReader(
				        new InputStreamReader(process.getInputStream())
				);
				String line;
				while ((line = reader.readLine()) != null) {
				    System.out.println("python set of games " + ii + ": " + line);
				}
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////
		
		for(int i = 0; i < Integer.MAX_VALUE; i++){

			//if(ooo++ % 20 == 0) SelfPlayer.main(args);
			System.out.println("Java data generation starting");
			List<Game> games = new ArrayList<>();
			
			for(int j = 0; j < 5; j++){
				System.out.println(i + "; " + j);
				try {
					playGame(games, 0);  //////////////////////////////////// 0 param was added while tesing threads
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
	
	public static void playGame(List<Game> games, int threadNr) throws Exception{
		board = new Board();
		board.turn = (Math.random() < 0.5) ? CheckerColors.O : CheckerColors.X;
		List<State> positions = new ArrayList<>();
		int gameScore = 0;
		int amountOfMoves = 0;
		while(gameScore == 0){
//			if(board.turn == CheckerColors.O) 
				positions.add(new State(board.parametrizeWithFlags(), false));
//			else positions.add(new State(board.canonifyParametrizeWithFlags(), true));
			gameScore = playTurn(threadNr);
			if(amountOfMoves++ == 500) break;
		}
		if(amountOfMoves < 500){
			games.add(new Game(positions, determineGameScoreVector(gameScore)));
		}
		else System.out.println("Game had too many moves and was not recorded");
	}
	
	public static int playTurn(int threadNr){
		int leftRoll = (int) (Math.random() * 6.0 + 1);
		int rightRoll = (int) (Math.random() * 6.0 + 1);

		if(board.turn == CheckerColors.O){
			board = engineO[threadNr].doComputedMove(CheckerColors.O, board, leftRoll, rightRoll);
			if(board.getTray(CheckerColors.O) == 15){
				return GameCalculation.calculateWinFactor(board, CheckerColors.O);
			}
		}
		else{
			board = engineX[threadNr].doComputedMove(CheckerColors.X, board, leftRoll, rightRoll);
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
