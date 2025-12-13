package backgammon.application.model.engines.ruleBased;

import java.util.HashMap;
import java.util.Map;

import backgammon.application.model.gameCalculations.GameCalculation;
import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.gameModels.Point;

public class PositionEvaluator {

	public static double evaluatePosition(Board board){
		double runningTotal = 0;
		
		int pipDifference = - GameCalculation.calculatePips(board, CheckerColors.O) + GameCalculation.calculatePips(board, CheckerColors.X);
		runningTotal += pipDifference;
		
		
		double barPenalty = (2.0 + 3.0 * GameCalculation.calculateAmountOfTowersInHome(board, CheckerColors.O)) * board.barX
						- (2.0 + 3.0 * GameCalculation.calculateAmountOfTowersInHome(board, CheckerColors.X)) * board.barO;
		runningTotal += barPenalty;
		
		double blotPenalty = 0;
		for(int i = 0; i < 24; i++){
			if(board.points[i].amtCheckers == 1){
				double hittable = 0;
				try{
					for(int j = i + board.points[i].occupiedBy.direction; Math.abs(j-i) <= 6; j += board.points[i].occupiedBy.direction){
						try{
							if((j == board.points[i].occupiedBy.opposite.barInt && board.getBar(board.points[i].occupiedBy.opposite) > 0) 
									||  board.points[j].occupiedBy == board.points[i].occupiedBy.opposite){
								hittable += rollProbabilities.get(Math.abs(j-i));
							}
						} catch(Exception e){ break; }
					}
				} catch(Exception e){}
				blotPenalty += board.points[i].occupiedBy.direction * 2 
							+ hittable * board.points[i].occupiedBy.direction * (3 + Math.abs(i - board.points[i].occupiedBy.barInt) * 0.35) ;
			}
		}
		runningTotal += blotPenalty;
		
		double rewardTowers = 0.0;
		for(int i = 0; i < 24; i++){
			if(board.points[i].amtCheckers > 1){
				rewardTowers -= board.points[i].occupiedBy.direction * towerValues[Math.abs(board.points[i].occupiedBy.trayInt - i) - 1];
			}
		}
		runningTotal += rewardTowers;
		
		double rewardTray = (board.trayO - board.trayX) * 20;
		runningTotal += rewardTray;
		
		return runningTotal;
	}
	private static int[] towerValues = new int[]{6, 7, 8, 9, 10, 11,	8, 7, 6, 5, 5, 5,	4, 4, 4, 4, 4, 4,	2, 2, 2, 2, 2, 1};
	
	private static Map<Integer, Double> rollProbabilities = new HashMap<>();
	static{
		rollProbabilities.put(1, 11.0/36.0);
		rollProbabilities.put(2, 12.0/36.0);
		rollProbabilities.put(3, 14.0/36.0);
		rollProbabilities.put(4, 15.0/36.0);
		rollProbabilities.put(5, 15.0/36.0);
		rollProbabilities.put(6, 17.0/36.0);
		rollProbabilities.put(7, 6.0/36.0);
		rollProbabilities.put(8, 6.0/36.0);
		rollProbabilities.put(9, 5.0/36.0);
		rollProbabilities.put(10, 3.0/36.0);
		rollProbabilities.put(11, 2.0/36.0);
		rollProbabilities.put(12, 3.0/36.0);
		rollProbabilities.put(13, 0.0/36.0);
		rollProbabilities.put(14, 0.0/36.0);
		rollProbabilities.put(15, 1.0/36.0);
		rollProbabilities.put(16, 1.0/36.0);
		rollProbabilities.put(17, 0.0/36.0);
		rollProbabilities.put(18, 1.0/36.0);
		rollProbabilities.put(19, 0.0/36.0);
		rollProbabilities.put(20, 1.0/36.0);
		rollProbabilities.put(21, 0.0/36.0);
		rollProbabilities.put(22, 0.0/36.0);
		rollProbabilities.put(23, 0.0/36.0);
		rollProbabilities.put(24, 1.0/36.0);
	}
}
