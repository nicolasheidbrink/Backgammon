package backgammon.application.model.gameCalculations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import backgammon.application.model.gameModels.Board;
import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.gameModels.Move;
import backgammon.application.model.gameModels.MoveSequence;

public class LegalMoveCalculation {
	
	public static Set<MoveSequence> calculateAllPossibleMoveSequences(Board board, CheckerColors color, int leftDie, int rightDie){
		List<Integer> remainingMoves;
		if(leftDie != rightDie) remainingMoves = List.of(leftDie, rightDie);
		else remainingMoves = List.of(leftDie, leftDie, leftDie, leftDie);
		
		MoveSequence exposition = new MoveSequence(new ArrayList<Move>(), board,  remainingMoves);
		
		Set<MoveSequence> allPossibleMoveSequences = new HashSet<>();
		
		allPossibleMoveSequences.add(exposition);
		
		Set<MoveSequence> toBeAdded = new HashSet<>();
		Set<MoveSequence> alreadyChecked = new HashSet<>();
		boolean nothingNew = false;
		while(!nothingNew){
			for(MoveSequence moveSequence : allPossibleMoveSequences){
				if(alreadyChecked.contains(moveSequence)) continue;
				alreadyChecked.add(moveSequence);
				if(moveSequence.remainingRolls().size() == 0) continue;
				toBeAdded.addAll(calculatePossibleNextMoves(color, moveSequence));
			}
			if(!toBeAdded.isEmpty()){
				allPossibleMoveSequences.addAll(toBeAdded);
				toBeAdded.clear();
			} else nothingNew = true;
		}
		if(allPossibleMoveSequences.equals(Set.of(exposition))) return new HashSet<MoveSequence>();
		
		int minimumRemainingRolls = allPossibleMoveSequences.stream()
			.mapToInt(ms -> ms.remainingRolls().size())
			.min()
			.orElseThrow();
		
		Set<MoveSequence> moveSequencesWithoutUnusedRolls = allPossibleMoveSequences.stream()
			.filter(ms -> ms.remainingRolls().size() == minimumRemainingRolls)
			.collect(Collectors.toSet());
		
		if(minimumRemainingRolls == 1){
			int minimumUnusedRoll = moveSequencesWithoutUnusedRolls.stream()
					.map(ms -> ms.remainingRolls().get(0))
					.min(Integer::compareTo)
					.orElseThrow();
			return moveSequencesWithoutUnusedRolls.stream()
					.filter(ms -> ms.remainingRolls().get(0).equals(minimumUnusedRoll))
					.collect(Collectors.toSet());
		}
		return moveSequencesWithoutUnusedRolls;
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
		else if(GameCalculation.checkIfEndgame(moveSequence.board(), color)){
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
}
