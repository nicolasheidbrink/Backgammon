package backgammon.model;

public class Board {
	
	public Point[] points;
	public int barO;
	public int barX;
	public int trayO;
	public int trayX;
	
	public Board(){
		defaultSetup();
	}
	
	private void defaultSetup(){
		this.points = new Point[24];
		initializePoints();
		this.barO = 0;
		this.barX = 0;
		this.trayO = 0;
		this.trayX = 0;
	}
	
	private void initializePoints(){
		points[0] = new Point('X', 2);
		points[1] = new Point('-', 0);
		points[2] = new Point('-', 0);
		points[3] = new Point('-', 0);
		points[4] = new Point('-', 0);
		points[5] = new Point('O', 5);
		points[6] = new Point('-', 0);
		points[7] = new Point('O', 3);
		points[8] = new Point('-', 0);
		points[9] = new Point('-', 0);
		points[10] = new Point('-', 0);
		points[11] = new Point('X', 5);
		points[12] = new Point('O', 5);
		points[13] = new Point('-', 0);
		points[14] = new Point('-', 0);
		points[15] = new Point('-', 0);
		points[16] = new Point('X', 3);
		points[17] = new Point('-', 0);
		points[18] = new Point('X', 5);
		points[19] = new Point('-', 0);
		points[20] = new Point('-', 0);
		points[21] = new Point('-', 0);
		points[22] = new Point('-', 0);
		points[23] = new Point('O', 2);
	}
	
	public Board clone(){
		Board clone = new Board();
		clone.barO = this.barO;
		clone.barX = this.barX;
		clone.trayO = this.trayO;
		clone.trayX = this.trayX;
		for(int i = 0; i < 24; i++){
			clone.points[i].amtCheckers = this.points[i].amtCheckers;
			clone.points[i].occupiedBy = this.points[i].occupiedBy;
		}
		return clone;
	}
}
