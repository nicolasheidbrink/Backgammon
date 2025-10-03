package backgammon.model.gameCalculations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import backgammon.model.game.Board;
import backgammon.model.game.CheckerColors;
import backgammon.model.game.Move;
import backgammon.model.game.MoveSequence;

public class CalculationUtils {
	
	public static Set<MoveSequence> calculateAllPossibleMoveSequences(Board board, CheckerColors color, int leftDie, int rightDie){
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
				toBeAdded.addAll(calculatePossibleNextMoves(color, moveSequence));
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
	
	public static Set<MoveSequence> calculatePossibleNextMoves(CheckerColors color, MoveSequence moveSequence){
		Set<MoveSequence> possibleNextMove = new HashSet<>();
		if(moveSequence.board().getBar(color) > 0){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				if(moveSequence.board().points[color.barInt + color.direction * roll].occupiedBy != color.opposite
						|| moveSequence.board().points[color.barInt + color.direction * roll].amtCheckers == 1){
					List<Move> tempMoves = new ArrayList<>();
					tempMoves.addAll(moveSequence.moves());
					tempMoves.add(new Move(color, roll, color.barInt, color.barInt + color.direction * roll));
					List<Integer> remainingRolls = new ArrayList<>();
					remainingRolls.addAll(moveSequence.remainingRolls());
					remainingRolls.remove(Integer.valueOf(roll));
					possibleNextMove.add(new MoveSequence(tempMoves, moveSequence.board().doMove(color, color.barInt, color.barInt + color.direction * roll), remainingRolls));
				}
			}
		}
		else if(checkIfEndgame(moveSequence.board(), color)){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				boolean moveAlreadyUsed = false;
				for(int i = color.trayInt - color.direction * 6; -color.direction * i > -color.direction * color.trayInt; i += color.direction){
					boolean rollTooBig = (-color.direction * i - roll < -color.direction * color.trayInt);
					if(moveSequence.board().points[i].occupiedBy == color){
						if(-color.direction * i - roll > -color.direction * color.trayInt && 
								(moveSequence.board().points[i + color.direction * roll].occupiedBy != color.opposite 
									|| moveSequence.board().points[i + color.direction * roll].amtCheckers == 1)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move(color, roll, i, i + color.direction * roll));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, moveSequence.board().doMove(color, i, i + color.direction * roll), remainingRolls));
							moveAlreadyUsed = true;
						}
						if(i + color.direction * roll == color.trayInt || (rollTooBig && !moveAlreadyUsed)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move(color, roll, i, color.trayInt));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, moveSequence.board().doMove(color, i, color.trayInt), remainingRolls));
							moveAlreadyUsed = true;
						}
						if(-color.direction * i - roll > -color.direction * color.trayInt 
								&& moveSequence.board().points[i + color.direction * roll].occupiedBy == color.opposite 
								&& moveSequence.board().points[i + color.direction * roll].amtCheckers > 1) 
							moveAlreadyUsed = true;
					}
				}
			}
		}
		else{
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				for(int i = 0; i < 24; i++){
					if(moveSequence.board().points[i].occupiedBy == color){
						if(-color.direction * i - roll > -color.direction * color.trayInt 
								&& (moveSequence.board().points[i + color.direction * roll].occupiedBy != color.opposite 
									|| moveSequence.board().points[i + color.direction * roll].amtCheckers == 1)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move(color, roll, i, i + color.direction * roll));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, moveSequence.board().doMove(color, i, i + color.direction * roll), remainingRolls));
						}
					}
				}
			}
		}
		return possibleNextMove;
	}
	
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

}
