package backgammon.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CalculationUtils {
	
	
	//Create a move with each roll being used on each occupied point. do it again
	public static Set<MoveSequence> calculatePossibleMovesForO(Board board, int leftDie, int rightDie){
		List<Integer> remainingMoves;
		if(leftDie != rightDie) remainingMoves = List.of(leftDie, rightDie);
		else remainingMoves = List.of(leftDie, leftDie, leftDie, leftDie);
		
		MoveSequence exposition = new MoveSequence(new ArrayList<Move>(), board,  remainingMoves);
		
		Set<MoveSequence> result = new HashSet<>();
		
		result.add(exposition);
		
		Set<MoveSequence> toBeAdded = new HashSet<>();
		Set<MoveSequence> alreadyChecked = new HashSet<>();
		boolean nothingNew = false;
		while(!nothingNew){
			for(MoveSequence moveSequence : result){
				if(alreadyChecked.contains(moveSequence)) continue;
				alreadyChecked.add(moveSequence);
				if(moveSequence.remainingRolls().size() == 0) continue;
			
				result.addAll(possibleMoves(moveSequence));
			
			}
			if(!toBeAdded.isEmpty()){
				result.addAll(toBeAdded);
				toBeAdded.clear();
			} else nothingNew = true;
		}
		
		removeMoveSequencesWithNotEnoughMoves
		
		if(board.barO > 0){
			if(board.points[24-leftDie].occupiedBy != 'X'){
				List<Move> temp = new ArrayList<Move>();
				temp.add(new Move('O', leftDie, 24, 24-leftDie, false));
				result.add(temp);
			}
			if(leftDie != rightDie && board.points[24-rightDie].occupiedBy != 'X'){
				List<Move> temp = new ArrayList<Move>();
				temp.add(new Move('O', rightDie, 24, 24-rightDie, false));
				result.add(temp);
			}
			if(board.points[24-leftDie].occupiedBy == 'X' && board.points[24-leftDie].amtCheckers == 1){
				List<Move> temp = new ArrayList<Move>();
				temp.add(new Move('O', leftDie, 24, 24-leftDie, true));
				result.add(temp);
			}
			if(leftDie != rightDie && board.points[24-rightDie].occupiedBy == 'X' && board.points[24-rightDie].amtCheckers == 1){
				List<Move> temp = new ArrayList<Move>();
				temp.add(new Move('O', rightDie, 24, 24-rightDie, true));
				result.add(temp);
			}
		}
		else {
			boolean endgameForO = checkIfEndgameForO(board);
			for(int i = 0; i < 24; i++){
				if(board.points[i].occupiedBy == 'O' && i + 1 >= leftDie){
					if(board.points[i-leftDie].occupiedBy != 'X'){
						
					}
				}
			}
		}
		
		return result;
	}
	
	public static Set<MoveSequence> possibleMoves(MoveSequence moveSequence){
		Set<MoveSequence> possibleNextMove = new HashSet<>();
		if(moveSequence.board().barO > 0){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				if(moveSequence.board().points[24-roll].occupiedBy != 'X' || moveSequence.board().points[24-roll].amtCheckers == 1){
					List<Move> tempMoves = new ArrayList<>();
					tempMoves.addAll(moveSequence.moves());
					tempMoves.add(new Move('O', roll, 24, 24-roll));
					List<Integer> remainingRolls = new ArrayList<>();
					remainingRolls.addAll(moveSequence.remainingRolls());
					remainingRolls.remove(Integer.valueOf(roll));
					possibleNextMove.add(new MoveSequence(tempMoves, doMoveForO(moveSequence.board(), 24, 24-roll), remainingRolls));
				}
			}
		}
		else if(checkIfEndgameForO(moveSequence.board())){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				for(int i = 0; i < 6; i++){
					if(moveSequence.board().points[i].occupiedBy == 'O'){
						///////////////////////////
					}
				}
			}
		}
	}
	
	public static boolean checkIfEndgameForO(Board board){
		for(int i = 6; i < 25; i++){
			if(board.points[i].occupiedBy == 'O') return false;
		}
		return true;
	}
	
	public static Board doMoveForO(Board before, int from, int to){
		Board after = before.clone();
		if(from == 24 && after.barO > 0 && after.points[to].occupiedBy != 'X'){
			after.barO--;
			after.points[to].amtCheckers++;
			after.points[to].occupiedBy = 'O';
			return after;
		}
		if(from == 24 && after.barO > 0 && after.points[to].occupiedBy == 'X'){
			if(after.points[to].occupiedBy > 1) return null;
			after.barO--;
			after.points[to].occupiedBy = 'O';
			after.barX++;
			return after;
		}
		if(after.points[from].occupiedBy != 'O' || after.points[from].amtCheckers == 0) return null;
		if(to == -1){
			if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = '-';
			after.trayO++;
			return after;
		}
		if(after.points[to].occupiedBy == 'X' && after.points[to].amtCheckers > 1) return null;
		if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = '-';
		if(after.points[to].occupiedBy == 'X'){
			after.points[to].occupiedBy = 'O';
			after.barX++;
			return after;
		}
		else{
			after.points[to].occupiedBy = 'O';
			after.points[to].amtCheckers++;
			return after;
		}
	}
}
