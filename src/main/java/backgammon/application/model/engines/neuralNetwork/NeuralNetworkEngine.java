package backgammon.application.model.engines.neuralNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import backgammon.application.model.engines.Engine;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.gameModels.MoveSequence;

public class NeuralNetworkEngine implements Engine {

	private ProcessBuilder pb;
	private Process process;
	private BufferedReader br;
	private BufferedWriter bw;
	
	private boolean includeRandomExplorationMoves;
	
	private Path gitRepoPathToPython = Paths.get("src", "main", "python", "neural_network.py");
	private Path deploymentFolderPathToPython = Paths.get("python_scripts", "neural_network.py");

	@Override
	public double calculateEval(Board board){
		try{
			return getPythonEvaluation(board);
		} catch (Exception e){
			System.out.println("python eval didnt work\nError message: "+e.getMessage());
			return Double.MAX_VALUE;
		}
	}
	
	@Override
	public MoveSequence calculateMove(CheckerColors color, Board board, Set<MoveSequence> possibleMoves) {

		if(includeRandomExplorationMoves && Math.random() < 0.1){
			if(possibleMoves.isEmpty()) return null;
			int index = (int) (Math.random() * possibleMoves.size());
			for(MoveSequence moveSequence : possibleMoves){
				if(index-- == 0) return moveSequence;
			}
		}
		
		MoveSequence tempBestMoveSeq = null;
//		double tempBestEval = -Double.MAX_VALUE;
		double tempBestEval = Double.MAX_VALUE * board.turn.direction;
		double currentEval = tempBestEval;
		for(MoveSequence moveSequence : possibleMoves){
//			try{
//				if(board.turn == null){
//					System.out.println(board);
//					for(MoveSequence ms : possibleMoves) System.out.println(ms.board());
//					System.out.println("done\n\n\n\n\n");
//				}
//				if(board.turn == CheckerColors.O) currentEval = getPythonEvaluation(moveSequence.board());
//				else currentEval = getPythonEvaluation(moveSequence.board().mirror());
//			} catch(IOException e){
//				System.out.println("python eval didnt work\nError message: "+e.getMessage());
//			}
//			if(currentEval > tempBestEval){
//				tempBestEval = currentEval;
//				tempBestMoveSeq = moveSequence;
//			}
			
			try{
				if(board.turn == null) System.out.println("board.turn is null" + board);
				currentEval = getPythonEvaluation(moveSequence.board());
				if(board.turn == CheckerColors.O && currentEval > tempBestEval){
					tempBestEval = currentEval;
					tempBestMoveSeq = moveSequence;
				}
				if(board.turn == CheckerColors.X && currentEval < tempBestEval){
					tempBestEval = currentEval;
					tempBestMoveSeq = moveSequence;
				}
			} catch(IOException e){
				System.out.println("reeeeee");
			}
		}
		return tempBestMoveSeq;
	}
	
	public double getPythonEvaluation(Board board) throws IOException {
		String pythonInput = createPythonInput(board);
		bw.write(pythonInput);
		bw.newLine();
		bw.flush();

        String line = br.readLine();
        line = line.substring(1, line.length() - 1);
                
        double[] probabilities = Arrays.stream(line.replace("[", "").replace("]", "").trim().split("\\s+"))
                .mapToDouble(Double::parseDouble)
                .toArray();
        //System.out.println(board.turn + "\n" + probabilities[0] + "; " + probabilities[1] + "; " + probabilities[2] + "; " + probabilities[3] + "; " + probabilities[4] + "; " + probabilities[5] + "; " + "\n\n");
        return probabilities[0] + probabilities[1] + probabilities[2] - probabilities[3] - probabilities[4] - probabilities[5];
  
	}
	
	public String createPythonInput(Board board){
		StringBuilder sb = new StringBuilder();
		double[] inputParas = board.parametrizeWithFlags();
		
		for(double d : inputParas){
			appendDoubleToSB(sb, d);
		}
		sb.setCharAt(sb.length() - 1, ']');
		sb.setCharAt(0, '[');
		return sb.toString();
	}

	public void appendDoubleToSB(StringBuilder sb, double d){
		sb.append(' ').append(d).append(',');
	}
	
	public NeuralNetworkEngine(boolean includeRandomExplorationMoves){
		if(Files.exists(gitRepoPathToPython)){
			pb = new ProcessBuilder("python", gitRepoPathToPython.toAbsolutePath().toString());
		}
		else{
			String os = System.getProperty("os.name").toLowerCase();
			String pythonCommand;

			if (os.contains("win")) {
			    pythonCommand = "python"; // Windows
			} else {
			    pythonCommand = "python3"; // Mac and Linux
			}
			pb = new ProcessBuilder(pythonCommand, deploymentFolderPathToPython.toAbsolutePath().toString());
		}
		pb.redirectErrorStream(true);
		try {
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		br = new BufferedReader(
	            new InputStreamReader(process.getInputStream())
		        );
		bw = new BufferedWriter(
	                new OutputStreamWriter(process.getOutputStream()));
		
		this.includeRandomExplorationMoves = includeRandomExplorationMoves;
	}
}