package backgammon.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalculationUtils {
	
	
	//Create a move with each roll being used on each occupied point. do it again
	public static Set<List<Move>> calculatePossibleMovesForO(Board board, int leftDie, int rightDie){
		Set<List<Move>> result = new HashSet<>();
		int amtMoves = (leftDie == rightDie) ? 4 : 2;
		
		if(board.barO > 0){
			if(board.points[24-leftDie].occupiedBy != 'X'){
				List<Move[]> temp = new ArrayList<Move[]>();
				temp.add(new Move)
				result.add(temp);
			}
		}
		
		return result;
	}
	
	public static Board cloneBoard(Board board){
		Board clone = new Board();
		clone.barO = board.barO;
		clone.barX = board.barX;
		clone.trayO = board.trayO;
		clone.trayX = board.trayX;
		for(int i = 0; i < 24; i++){
			clone.points[i].amtCheckers = board.points[i].amtCheckers;
			clone.points[i].occupiedBy = board.points[i].occupiedBy;
		}
		return clone;
	}
}
