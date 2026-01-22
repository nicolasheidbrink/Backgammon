package backgammon.application.model.gameModels;

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
		clone.turn = this.turn;
		return clone;
	}
	
	public Board doMove(CheckerColors color, int from, int to){
		Board after = this.clone();
		
		if(from == color.trayInt){
			if(getTray(color) == 0) return null;
			if(to == color.trayInt){
				return after;
			}
			if(to == color.barInt){
				after.setTray(color, getTray(color) - 1);
				after.setBar(color, getBar(color) + 1);
				return after;
			}
			if(to >= 0 && to < 24){
				if(points[to].occupiedBy == color.opposite && points[to].amtCheckers > 1) return null;
				if(points[to].occupiedBy == color.opposite){
					after.setTray(color, getTray(color) - 1);
					after.points[to].occupiedBy = color;
					after.setBar(color.opposite, getBar(color.opposite) + 1);
					return after;
				}
				else{
					after.setTray(color, getTray(color) - 1);
					after.points[to].occupiedBy = color;
					after.points[to].amtCheckers++;
					return after;
				}
			}
			else return null;
		}
		
		if(from == color.barInt){
			if(getBar(color) == 0) return null;
			if(to == color.trayInt){
				after.setBar(color, getBar(color) - 1);
				after.setTray(color, getTray(color) + 1);
				return after;
			}
			if(to == color.barInt){
				return after;
			}
			if(to >= 0 && to < 24){
				if(points[to].occupiedBy == color.opposite && points[to].amtCheckers > 1) return null;
				if(points[to].occupiedBy == color.opposite){
					after.setBar(color, getBar(color) - 1);
					after.points[to].occupiedBy = color;
					after.setBar(color.opposite, getBar(color.opposite) + 1);
					return after;
				}
				else{
					after.setBar(color, getBar(color) - 1);
					after.points[to].occupiedBy = color;
					after.points[to].amtCheckers++;
					return after;
				}
			}
			else return null;
		}
		
		if(from >= 0 && from < 24){
			if(points[from].occupiedBy != color) return null;
			if(to == color.trayInt){
				if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
				after.setTray(color, getTray(color) + 1);
				return after;
			}
			if(to == color.barInt){
				if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
				after.setBar(color, getBar(color) + 1);
				return after;
			}
			if(to >= 0 && to < 24){
				if(points[to].occupiedBy == color.opposite && points[to].amtCheckers > 1) return null;
				if(points[to].occupiedBy == color.opposite){
					if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
					after.points[to].occupiedBy = color;
					after.setBar(color.opposite, getBar(color.opposite) + 1);
					return after;
				}
				else{
					if(--(after.points[from].amtCheckers) == 0) after.points[from].occupiedBy = CheckerColors.NA;
					after.points[to].occupiedBy = color;
					after.points[to].amtCheckers++;
					return after;
				}
			}
			else return null;
		}
		else return null;
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
	
	public double[] parametrize(){
		double[] paras = new double[53];
		
		for(int i = 0; i < 24; i++){
			if(points[i].occupiedBy == CheckerColors.O) paras[i] = points[i].amtCheckers / 15.0;
			else paras[i] = 0.0;
		}
		for(int i = 24; i < 48; i++){
			if(points[i-24].occupiedBy == CheckerColors.X) paras[i] = points[i-24].amtCheckers / 15.0;
			else paras[i] = 0.0;
		}
		paras[48] = barO / 15.0;
		paras[49] = barX / 15.0;
		paras[50] = trayO / 15.0;
		paras[51] = trayX / 15.0;
		if(turn == CheckerColors.O) paras[52] = 1.0;
		else if(turn == CheckerColors.X) paras[52] = 0.0;
		else paras[52] = 0.5;
		
		return paras;
	}
	
	public double[] canonifyParametrizeWithFlags(){
		return mirror().parametrizeWithFlags();
	}
	
	public Board mirror(){
		Board mirror = new Board();
		for(int i = 0; i < 24; i++){
			mirror.points[i].occupiedBy = this.points[23-i].occupiedBy.opposite;
			mirror.points[i].amtCheckers = this.points[23-i].amtCheckers;
		}
		mirror.barO = this.barX;
		mirror.barX = this.barO;
		mirror.trayO = this.trayX;
		mirror.trayX = this.trayO;
		mirror.turn = this.turn.opposite;
		return mirror;
	}
	
	public double[] parametrizeWithFlags(){
		double[] paras = new double[(24 + 1)*2*4 + 2 + 1];
		
		for(int i = 0; i < 24*4; i++){
			if(points[i/4].occupiedBy == CheckerColors.O && i % 4 == 3) paras[i] = Math.max(0, (points[i/4].amtCheckers - 3.0) / 2.0);
			else if(points[i/4].occupiedBy == CheckerColors.O && points[i/4].amtCheckers > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}
		for(int i = 24*4; i < 2*24*4; i++){
			if(points[(i-24*4)/4].occupiedBy == CheckerColors.X && i % 4 == 3) paras[i] = Math.max(0, (points[(i-24*4)/4].amtCheckers - 3.0) / 2.0);
			else if(points[(i-24*4)/4].occupiedBy == CheckerColors.X && points[(i-24*4)/4].amtCheckers > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}
		
		for(int i = 2*24*4; i < 2*24*4 + 4; i++){
			if(i % 4 == 3) paras[i] = Math.max(0, (barO - 3.0)/2.0);
			else if(barO > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}

		for(int i = 2*24*4 + 4; i < 2*24*4 + 8; i++){
			if(i % 4 == 3) paras[i] = Math.max(0, (barX - 3.0)/2.0);
			else if(barX > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}
		
		paras[200] = trayO / 15.0;
		paras[201] = trayX / 15.0;

		if(turn == CheckerColors.O) paras[202] = 1.0;
		else if(turn == CheckerColors.X) paras[202] = 0.0;
		else paras[202] = 0.5;
		
		return paras;
	}
	
	public double[] reverseParametrizeWithFlags(){
		double[] paras = new double[(24 + 1)*2*4 + 2 + 1];
		
		for(int i = 0; i < 24*4; i++){
			if(points[i/4].occupiedBy == CheckerColors.X && i % 4 == 3) paras[i] = Math.max(0, (points[i/4].amtCheckers - 3.0) / 2.0);
			else if(points[i/4].occupiedBy == CheckerColors.X && points[i/4].amtCheckers > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}
		for(int i = 24*4; i < 2*24*4; i++){
			if(points[(i-24*4)/4].occupiedBy == CheckerColors.O && i % 4 == 3) paras[i] = Math.max(0, (points[(i-24*4)/4].amtCheckers - 3.0) / 2.0);
			else if(points[(i-24*4)/4].occupiedBy == CheckerColors.O && points[(i-24*4)/4].amtCheckers > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}
		
		for(int i = 2*24*4; i < 2*24*4 + 4; i++){
			if(i % 4 == 3) paras[i] = Math.max(0, (barX - 3.0)/2.0);
			else if(barX > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}

		for(int i = 2*24*4 + 4; i < 2*24*4 + 8; i++){
			if(i % 4 == 3) paras[i] = Math.max(0, (barO - 3.0)/2.0);
			else if(barO > (i % 4)) paras[i] = 1.0;
			else paras[i] = 0.0;
		}

		if(turn == CheckerColors.X) paras[202] = 1.0;
		else if(turn == CheckerColors.O) paras[202] = 0.0;
		else paras[202] = 0.5;
		
		paras[200] = trayX / 15.0;
		paras[201] = trayO / 15.0;
		
		return paras;
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
