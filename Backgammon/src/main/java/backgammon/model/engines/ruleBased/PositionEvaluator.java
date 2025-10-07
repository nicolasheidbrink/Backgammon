package backgammon.model.engines.ruleBased;

import backgammon.model.game.Board;
import backgammon.model.game.CheckerColors;
import backgammon.model.gameCalculations.GameCalculation;

public class PositionEvaluator {

	public static double evaluatePosition(Board board){
		int pipDifference = GameCalculation.calculatePips(board, CheckerColors.O) - GameCalculation.calculatePips(board, CheckerColors.X);
		
		int barPenalty = 6 * (board.barX - board.barO);
		
		double rewardTowers = 0.0;
		for(int i = 0; i < 24; i++){
			if(board.points[i].amtCheckers > 1){
				rewardTowers -= board.points[i].occupiedBy.direction * towerValues[Math.abs(board.points[i].occupiedBy.trayInt - i) - 1];
			}
		}
		return pipDifference + barPenalty + rewardTowers;
	}
	private static int[] towerValues = new int[]{6, 7, 8, 9, 10, 11,	8, 7, 6, 5, 5, 5,	4, 4, 4, 4, 4, 4,	2, 2, 2, 2, 2, 2};
}
