package backgammon.model.gameModels;

import java.util.List;

public record MoveSequence(List<Move> moves, Board board, List<Integer> remainingRolls) {

}
