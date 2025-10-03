package backgammon.model.game;

public enum CheckerColors {

    O, X, NA;

    public CheckerColors opposite;

    static {
        O.opposite = X;
        X.opposite = O;
        NA.opposite = NA;
    }
}
