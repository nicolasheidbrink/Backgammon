package backgammon.model.gameModels;

public class Point {

	public CheckerColors occupiedBy;
	public int amtCheckers;
	
	public Point(CheckerColors occupiedBy, int amtCheckers){
		this.occupiedBy = occupiedBy;
		this.amtCheckers = amtCheckers;
	}
}
