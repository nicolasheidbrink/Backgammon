package backgammon.model.gameModels;

public class Move {

	public CheckerColors color;
	public int roll;
	public int from;
	public int to;
	
	public Move(CheckerColors color, int roll, int from, int to){
		this.color = color;
		this.roll = roll;
		this.from = from;
		this.to = to;
	}
}
