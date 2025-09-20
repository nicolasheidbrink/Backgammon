package backgammon.model;

import java.util.List;

public class Move {

	public char color;
	public int roll;
	public int from;
	public int to;
	
	public Move(char color, int roll, int from, int to){
		this.color = color;
		this.roll = roll;
		this.from = from;
		this.to = to;
	}
}
