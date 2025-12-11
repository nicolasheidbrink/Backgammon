package backgammon.model.engines.neuralNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;

import backgammon.model.engines.Engine;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;
import backgammon.model.gameModels.MoveSequence;

public class NeuralNetworkEngine implements Engine {

	private ProcessBuilder pb;
	private Process process;
	private BufferedReader br;
	private BufferedWriter bw;

	@Override
	public MoveSequence calculateMove(CheckerColors color, Board board, Set<MoveSequence> possibleMoves) {

		MoveSequence tempBestMoveSeq = null;
		double tempBestEval = Double.MAX_VALUE * color.direction;
		double currentEval = tempBestEval;
		if(Math.random() < 0.1){
			if(possibleMoves.isEmpty()) return null;
			int index = (int) (Math.random() * possibleMoves.size());
			for(MoveSequence moveSequence : possibleMoves){
				if(index-- == 0) return moveSequence;
			}
		}
		for(MoveSequence moveSequence : possibleMoves){
			try{
				currentEval = getPythonEvaluation(moveSequence.board());
			} catch(Exception e){
				System.out.println("python eval didnt work\nError message: "+e.getMessage());
			}
			if((color == CheckerColors.O && currentEval > tempBestEval)
					|| (color == CheckerColors.X && currentEval < tempBestEval)){
				tempBestEval = currentEval;
				tempBestMoveSeq = moveSequence;
			}
		}
		return tempBestMoveSeq;
	}
	
	public double getPythonEvaluation(Board board) throws Exception{
		String pythonInput = createPythonInput(board);
		bw.write(pythonInput);
		bw.newLine();
		bw.flush();

        String line = br.readLine();
        return Double.parseDouble(line);
  
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
	
	public NeuralNetworkEngine(){
		pb = new ProcessBuilder("python", "src/main/python/neural_network.py");
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
	}
}