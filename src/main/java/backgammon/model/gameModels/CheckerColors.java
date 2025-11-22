package backgammon.model.gameModels;

import java.util.Set;

public enum CheckerColors {

	O(24, -1, -1, Set.of(0,1,2,3,4,5)),
	X(-1, 24, 1, Set.of(18,19,20,21,22,23)),
    NA(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Set.of());

    public CheckerColors opposite;
    public int barInt;
    public int trayInt;
    public int direction;
    public Set<Integer> homePoints;

    static {
        O.opposite = X;
        X.opposite = O;
        NA.opposite = NA;
    }
    
    CheckerColors(int barInt, int trayInt, int direction, Set<Integer> homePoints){
    	this.barInt = barInt;
    	this.trayInt = trayInt;
    	this.direction = direction;
    	this.homePoints = homePoints;
    }
}
