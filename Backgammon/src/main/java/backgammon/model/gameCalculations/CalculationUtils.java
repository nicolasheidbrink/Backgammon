package backgammon.model.gameCalculations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import backgammon.model.game.Board;
import backgammon.model.game.CheckerColors;
import backgammon.model.game.Move;
import backgammon.model.game.MoveSequence;

public class CalculationUtils {
	
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
				toBeAdded.addAll(possibleMoves(moveSequence));
			}
			if(!toBeAdded.isEmpty()){
				result.addAll(toBeAdded);
				toBeAdded.clear();
			} else nothingNew = true;
		}
		if(result.equals(Set.of(exposition))) return new HashSet<MoveSequence>();
		
		int minimumRemainingRolls = result.stream()
			.mapToInt(ms -> ms.remainingRolls().size())
			.min()
			.orElseThrow();
		
		Set<MoveSequence> resultWithoutUnusedRolls = result.stream()
			.filter(ms -> ms.remainingRolls().size() == minimumRemainingRolls)
			.collect(Collectors.toSet());
		
		if(minimumRemainingRolls == 1){
			int minimumUnusedRoll = resultWithoutUnusedRolls.stream()
					.map(ms -> ms.remainingRolls().get(0))
					.min(Integer::compareTo)
					.orElseThrow();
			return resultWithoutUnusedRolls.stream()
					.filter(ms -> ms.remainingRolls().get(0).equals(minimumUnusedRoll))
					.collect(Collectors.toSet());
		}
		return resultWithoutUnusedRolls;
	}
	
	public static Set<MoveSequence> possibleMoves(MoveSequence moveSequence){
		Set<MoveSequence> possibleNextMove = new HashSet<>();
		if(moveSequence.board().barO > 0){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				if(moveSequence.board().points[24-roll].occupiedBy != CheckerColors.X || moveSequence.board().points[24-roll].amtCheckers == 1){
					List<Move> tempMoves = new ArrayList<>();
					tempMoves.addAll(moveSequence.moves());
					tempMoves.add(new Move(CheckerColors.O, roll, 24, 24-roll));
					List<Integer> remainingRolls = new ArrayList<>();
					remainingRolls.addAll(moveSequence.remainingRolls());
					remainingRolls.remove(Integer.valueOf(roll));
					possibleNextMove.add(new MoveSequence(tempMoves, doMoveForO(moveSequence.board(), 24, 24-roll), remainingRolls));
				}
			}
		}
		else if(checkIfEndgameForO(moveSequence.board())){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				boolean moveAlreadyUsed = false;
				for(int i = 5; i >= 0; i--){
					boolean rollTooBig = (i + 1 < roll);
					if(moveSequence.board().points[i].occupiedBy == CheckerColors.O){
						if(i-roll >= 0 && (moveSequence.board().points[i-roll].occupiedBy != CheckerColors.X || moveSequence.board().points[i-roll].amtCheckers == 1)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move(CheckerColors.O, roll, i, i-roll));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, doMoveForO(moveSequence.board(), i, i-roll), remainingRolls));
							moveAlreadyUsed = true;
						}
						if(i-roll == -1 || (rollTooBig && !moveAlreadyUsed)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move(CheckerColors.O, roll, i, -1));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, doMoveForO(moveSequence.board(), i, -1), remainingRolls));
							moveAlreadyUsed = true;
						}
						if(i-roll >= 0 && moveSequence.board().points[i-roll].occupiedBy == CheckerColors.X && moveSequence.board().points[i-roll].amtCheckers > 1) moveAlreadyUsed = true;
					}
				}
			}
		}
		else{
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				for(int i = 0; i < 24; i++){
					if(moveSequence.board().points[i].occupiedBy == CheckerColors.O){
						if(i-roll >= 0 && (moveSequence.board().points[i-roll].occupiedBy != CheckerColors.X || moveSequence.board().points[i-roll].amtCheckers == 1)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move(CheckerColors.O, roll, i, i-roll));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, doMoveForO(moveSequence.board(), i, i-roll), remainingRolls));
						}
					}
				}
			}
		}
		return possibleNextMove;
	}
	
	public static boolean checkIfEndgameForO(Board board){
		for(int i = 6; i < 24; i++){
			if(board.points[i].occupiedBy == CheckerColors.O) return false;
		}
		if(board.barO > 0) return false;
		return true;
	}
	
	public static Board doMoveForO(Board before, int from, int to){
		Board after = before.clone();
		if(from == 24 && after.barO > 0 && after.points[to].occupiedBy != CheckerColors.X){
			after.barO--;
			after.points[to].amtCheckers++;
			after.points[to].occupiedBy = CheckerColors.O;
			return after;
		}
		if(from == 24 && after.barO > 0 && after.points[to].occupiedBy == CheckerColors.X){
			if(after.points[to].amtCheckers > 1) return null;
			after.barO--;
			after.points[to].occupiedBy = CheckerColors.O;
			after.barX++;
			return after;
		}
		if(after.points[from].occupiedBy != CheckerColors.O || after.points[from].amtCheckers == 0) return null;
		if(to == -1){
			if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
			after.trayO++;
			return after;
		}
		if(after.points[to].occupiedBy == CheckerColors.X && after.points[to].amtCheckers > 1) return null;
		if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
		if(after.points[to].occupiedBy == CheckerColors.X){
			after.points[to].occupiedBy = CheckerColors.O;
			after.barX++;
			return after;
		}
		else{
			after.points[to].occupiedBy = CheckerColors.O;
			after.points[to].amtCheckers++;
			return after;
		}
	}
}
