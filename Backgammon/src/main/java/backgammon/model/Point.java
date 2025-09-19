package backgammon.model;

public class Point {

	public char occupiedBy;
	public int amtCheckers;
	
	public Point(char occupiedBy, int amtCheckers){
		this.occupiedBy = occupiedBy;
		this.amtCheckers = amtCheckers;
	}
}
