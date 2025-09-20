package backgammon.model;

import java.util.List;

public record MoveSequence(List<Move> moves, Board board, List<Integer> remainingRolls) {

}
