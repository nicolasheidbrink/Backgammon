package backgammon.model.gameCalculations;

import java.util.stream.Collectors;

import backgammon.model.game.Board;
import backgammon.model.game.CheckerColors;

public class GameCalculation {
	public static boolean checkIfEndgame(Board board, CheckerColors checkerColor){
		if(checkerColor == CheckerColors.O){
			for(int i = 6; i < 24; i++){
				if(board.points[i].occupiedBy == CheckerColors.O) return false;
			}
			if(board.barO > 0) return false;
		}
		else if(checkerColor == CheckerColors.X){
			for(int i = 0; i < 18; i++){
				if(board.points[i].occupiedBy == CheckerColors.X) return false;
			}
			if(board.barX > 0) return false;			
		}
		return true;
	}
	
	public static int calculateWinFactor(Board board, CheckerColors winner){
		if(board.getTray(winner.opposite) == 0 &&
				(board.getBar(winner.opposite) > 0
					|| winner.homePoints.stream()
						.map(i -> board.points[i].occupiedBy)
						.collect(Collectors.toSet())
						.contains(CheckerColors.X)))
			return 3;
		if(board.getTray(winner.opposite) == 0)
			return 2;
		return 1;
	}
	
	public static int calculatePips(Board board, CheckerColors color){
		int runningTotal = 0;
		for(int i = 0; i < 24; i++){
			if(board.points[i].occupiedBy == color) runningTotal += board.points[i].amtCheckers * Math.abs(i - color.trayInt);
		}
		if(color == CheckerColors.O) runningTotal += 25 * board.barO;
		if(color == CheckerColors.X) runningTotal += 25 * board.barX;
		return runningTotal;
	}
}
