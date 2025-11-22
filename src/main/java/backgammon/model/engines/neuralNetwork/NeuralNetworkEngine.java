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
		for(int i = 0; i < 24; i++){
			if(board.points[i].occupiedBy == CheckerColors.O) appendDoubleToSB(sb, board.points[i].amtCheckers / 15.0);
			else appendDoubleToSB(sb, 0);
		}
		for(int i = 0; i < 24; i++){
			if(board.points[i].occupiedBy == CheckerColors.X) appendDoubleToSB(sb, board.points[i].amtCheckers / 15.0);
			else appendDoubleToSB(sb, 0);
		}
		appendDoubleToSB(sb, board.barO / 15.0);
		appendDoubleToSB(sb, board.barX / 15.0);
		appendDoubleToSB(sb, board.trayO / 15.0);
		appendDoubleToSB(sb, board.trayX / 15.0);
		if(board.turn == CheckerColors.O) appendDoubleToSB(sb, 1.0);
		else if(board.turn == CheckerColors.X) appendDoubleToSB(sb, 0.0);
		else appendDoubleToSB(sb, 0.5);
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