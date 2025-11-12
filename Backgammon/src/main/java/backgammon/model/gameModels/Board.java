package backgammon.model.gameModels;

public class Board {
	
	public Point[] points;
	public int barO;
	public int barX;
	public int trayO;
	public int trayX;
	public int leftDie;
	public int rightDie;
	public CheckerColors turn;
	
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
		leftDie = 6;
		rightDie = 5;
	}
	
	private void initializePoints(){
		points[0] = new Point(CheckerColors.X, 2);
		points[1] = new Point(CheckerColors.NA, 0);
		points[2] = new Point(CheckerColors.NA, 0);
		points[3] = new Point(CheckerColors.NA, 0);
		points[4] = new Point(CheckerColors.NA, 0);
		points[5] = new Point(CheckerColors.O, 5);
		points[6] = new Point(CheckerColors.NA, 0);
		points[7] = new Point(CheckerColors.O, 3);
		points[8] = new Point(CheckerColors.NA, 0);
		points[9] = new Point(CheckerColors.NA, 0);
		points[10] = new Point(CheckerColors.NA, 0);
		points[11] = new Point(CheckerColors.X, 5);
		points[12] = new Point(CheckerColors.O, 5);
		points[13] = new Point(CheckerColors.NA, 0);
		points[14] = new Point(CheckerColors.NA, 0);
		points[15] = new Point(CheckerColors.NA, 0);
		points[16] = new Point(CheckerColors.X, 3);
		points[17] = new Point(CheckerColors.NA, 0);
		points[18] = new Point(CheckerColors.X, 5);
		points[19] = new Point(CheckerColors.NA, 0);
		points[20] = new Point(CheckerColors.NA, 0);
		points[21] = new Point(CheckerColors.NA, 0);
		points[22] = new Point(CheckerColors.NA, 0);
		points[23] = new Point(CheckerColors.O, 2);

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
		clone.leftDie = leftDie;
		clone.rightDie = rightDie;
		return clone;
	}
	
	public Board doMove(CheckerColors color, int from, int to){
		Board after = this.clone();
		if(from == color.barInt && after.getBar(color) > 0 && after.points[to].occupiedBy != color.opposite){
			after.setBar(color, after.getBar(color) - 1);
			after.points[to].amtCheckers++;
			after.points[to].occupiedBy = color;
			return after;
		}
		if(from == color.barInt && after.getBar(color) > 0 && after.points[to].occupiedBy == color.opposite){
			if(after.points[to].amtCheckers > 1) return null;
			after.setBar(color, after.getBar(color) - 1);
			after.points[to].occupiedBy = color;
			after.setBar(color.opposite, after.getBar(color.opposite) + 1);
			return after;
		}
		if(after.points[from].occupiedBy != color || after.points[from].amtCheckers == 0) return null;
		if(to == color.trayInt){
			if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
			after.setTray(color, after.getTray(color) + 1);
			return after;
		}
		if(after.points[to].occupiedBy == color.opposite && after.points[to].amtCheckers > 1) return null;
		if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
		if(after.points[to].occupiedBy == color.opposite){
			after.points[to].occupiedBy = color;
			after.setBar(color.opposite, after.getBar(color.opposite) + 1);
			return after;
		}
		else{
			after.points[to].occupiedBy = color;
			after.points[to].amtCheckers++;
			return after;
		}
	}

	
	public int getTray(CheckerColors color){
		if(color == CheckerColors.X) return trayX;
		if(color == CheckerColors.O) return trayO;
		return Integer.MIN_VALUE;
	}
	
	public int getBar(CheckerColors color){
		if(color == CheckerColors.X) return barX;
		if(color == CheckerColors.O) return barO;
		return Integer.MIN_VALUE;
	}

	public void setTray(CheckerColors color, int newValue){
		if(color == CheckerColors.X) trayX = newValue;
		if(color == CheckerColors.O) trayO = newValue;
	}
	
	public void setBar(CheckerColors color, int newValue){
		if(color == CheckerColors.X) barX = newValue;
		if(color == CheckerColors.O) barO = newValue;
	}
	
	@Override
	public String toString(){
		String output = "turn: " + turn
					+ "\nbarO: " + barO
					+ "\nbarX: " + barX
					+ "\ntrayO: " + trayO
					+ "\ntrayX: " + trayX;
		for(int i = 0; i < 24; i++){
			output = output + "\npoint " + (i+1) + ": " + points[i].occupiedBy + "; " + points[i].amtCheckers;
		}
		return output;
	}
	
	@Override
	public boolean equals(Object inputBoard){
		if(inputBoard.getClass() != getClass()) return false;
		Board board = (Board) inputBoard;
		if(board.turn != turn
				|| board.barO != barO
				|| board.barX != barX
				|| board.trayO != trayO
				|| board.trayX != trayX )
			return false;
		for(int i = 0; i < 24; i++){
			if(board.points[i].amtCheckers != points[i].amtCheckers
					|| board.points[i].occupiedBy != points[i].occupiedBy)
				return false;
		}
		return true;
	}
}
