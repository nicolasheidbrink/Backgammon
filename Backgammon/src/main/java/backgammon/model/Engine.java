package backgammon.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Engine {

	public Board doComputedMove(Board board){
		int leftDie = (int) (6*Math.random() + 1);
		int rightDie = (int) (6*Math.random() + 1);
		board.leftDie = leftDie;
		board.rightDie = rightDie;
		System.out.println(leftDie + "; " + rightDie);
		Set<MoveSequence> possibleMoves = calculatePossibleMovesForX(board, leftDie, rightDie);
		MoveSequence chosenMove = calculateMove(board, possibleMoves);
		Board result = board;
		if(chosenMove == null) return result;
		for(Move move : chosenMove.moves()){
			result = doMoveForX(result, move.from, move.to);
		}
		return result;
	}
	
	public Set<MoveSequence> calculatePossibleMovesForX(Board board, int leftDie, int rightDie){
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

	public Set<MoveSequence> possibleMoves(MoveSequence moveSequence){
		Set<MoveSequence> possibleNextMove = new HashSet<>();
		if(moveSequence.board().barX > 0){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				if(moveSequence.board().points[roll].occupiedBy != 'O' || moveSequence.board().points[roll].amtCheckers == 1){
					List<Move> tempMoves = new ArrayList<>();
					tempMoves.addAll(moveSequence.moves());
					tempMoves.add(new Move('X', roll, -1, roll-1));
					List<Integer> remainingRolls = new ArrayList<>();
					remainingRolls.addAll(moveSequence.remainingRolls());
					remainingRolls.remove(Integer.valueOf(roll));
					possibleNextMove.add(new MoveSequence(tempMoves, doMoveForX(moveSequence.board(), -1, roll-1), remainingRolls));
				}
			}
		}
		else if(checkIfEndgameForX(moveSequence.board())){
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				for(int i = 18; i <=23; i++){
					boolean noMovePossible = true;
					if(moveSequence.board().points[i].occupiedBy == 'X'){
						if(i+roll <= 23 && (moveSequence.board().points[i+roll].occupiedBy != 'O' || moveSequence.board().points[i+roll].amtCheckers == 1)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move('X', roll, i, i+roll));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, doMoveForX(moveSequence.board(), i, i+roll), remainingRolls));
							noMovePossible = false;
						}
						if(i-roll == 0 || noMovePossible){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move('X', roll, i, 24));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, doMoveForX(moveSequence.board(), i, 24), remainingRolls));
							noMovePossible = false;
						}
					}
				}
			}
		}
		else{
			for(Integer roll : moveSequence.remainingRolls().stream().distinct().collect(Collectors.toList())){
				for(int i = 0; i < 24; i++){
					if(moveSequence.board().points[i].occupiedBy == 'X'){
						if(i+roll <= 23 && (moveSequence.board().points[i+roll].occupiedBy != 'O' || moveSequence.board().points[i+roll].amtCheckers == 1)){
							List<Move> tempMoves = new ArrayList<>();
							tempMoves.addAll(moveSequence.moves());
							tempMoves.add(new Move('X', roll, i, i+roll));
							List<Integer> remainingRolls = new ArrayList<>();
							remainingRolls.addAll(moveSequence.remainingRolls());
							remainingRolls.remove(Integer.valueOf(roll));
							possibleNextMove.add(new MoveSequence(tempMoves, doMoveForX(moveSequence.board(), i, i+roll), remainingRolls));
						}
					}
				}
			}
		}
		return possibleNextMove;
	}

	public boolean checkIfEndgameForX(Board board){
		for(int i = 0; i < 18; i++){
			if(board.points[i].occupiedBy == 'X') return false;
		}
		if(board.barX > 0) return false;
		return true;
	}

	public MoveSequence calculateMove(Board board, Set<MoveSequence> possibleMoves){
		if(possibleMoves.size() == 0) return null;
		int index = (int) (Math.random() * (possibleMoves.size()));
		int i = 0;
		for(MoveSequence moveSequence : possibleMoves){
			if(i++ ==index) return moveSequence;
		}
		return null;
	}
	
	public Board doMoveForX(Board before, int from, int to){
		Board after = before.clone();
		if(from == -1 && after.barX > 0 && after.points[to].occupiedBy != 'O'){
			after.barX--;
			after.points[to].amtCheckers++;
			after.points[to].occupiedBy = 'X';
			return after;
		}
		if(from == -1 && after.barX > 0 && after.points[to].occupiedBy == 'O'){
			if(after.points[to].amtCheckers > 1) return null;
			after.barX--;
			after.points[to].occupiedBy = 'X';
			after.barO++;
			return after;
		}
		if(after.points[from].occupiedBy != 'X' || after.points[from].amtCheckers == 0) return null;
		if(to == 24){
			if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = '-';
			after.trayX++;
			return after;
		}
		if(after.points[to].occupiedBy == 'O' && after.points[to].amtCheckers > 1) return null;
		if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = '-';
		if(after.points[to].occupiedBy == 'O'){
			after.points[to].occupiedBy = 'X';
			after.barO++;
			return after;
		}
		else{
			after.points[to].occupiedBy = 'X';
			after.points[to].amtCheckers++;
			return after;
		}

		
	}
}
