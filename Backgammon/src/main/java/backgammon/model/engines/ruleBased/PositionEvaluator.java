package backgammon.model.engines.ruleBased;

import backgammon.model.gameCalculations.GameCalculation;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;

public class PositionEvaluator {

	public static double evaluatePosition(Board board){
		double runningTotal = 0;
		
		int pipDifference = - GameCalculation.calculatePips(board, CheckerColors.O) + GameCalculation.calculatePips(board, CheckerColors.X);
		runningTotal += pipDifference;
		
		
		double barPenalty = (2.0 + 4.0 * GameCalculation.calculateAmountOfTowersInHome(board, CheckerColors.O)) * board.barX
						- (2.0 + 4.0 * GameCalculation.calculateAmountOfTowersInHome(board, CheckerColors.X)) * board.barO;
		runningTotal += barPenalty;
		
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
}
