package backgammon.model.operation;

import java.io.IOException;

import backgammon.controller.BoardController;
import backgammon.controller.MenuController;
import backgammon.model.game.CheckerColors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class ProgramMaster {

	Stage stage;
	Scene menuScene;
	Scene boardScene;
	
	BoardController boardController;
	MenuController menuController;
	
	GameMaster gameMaster;
	
	int scoreO;
	int scoreX;
	CheckerColors lastWinner = CheckerColors.NA;
	
	public ProgramMaster(Stage primaryStage) throws IOException{
		initializeStage(primaryStage);
		FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
		Parent menuRoot = menuLoader.load();
		menuController = menuLoader.getController();
		menuController.setProgramMaster(this);
		menuScene = new Scene(menuRoot);

		FXMLLoader boardLoader = new FXMLLoader(getClass().getResource("/fxml/Board.fxml"));
		Parent boardRoot = boardLoader.load();
		boardController = boardLoader.getController();
		boardController.setProgramMaster(this);
		boardScene = new Scene(boardRoot);
		
		stage.setScene(menuScene);
	}
	
	public void gameStarted(){
		stage.setScene(boardScene);
		gameMaster = new GameMaster(this, boardController);
		gameMaster.startGame();
	}

	public void gameDone(CheckerColors winner, int multiplier){
		this.lastWinner = winner;
		if(winner == CheckerColors.O) scoreO += multiplier;
		if(winner == CheckerColors.X) scoreX += multiplier;
		menuController.updateScore(scoreO, scoreX);
		boardController.updateScore(scoreO, scoreX);
		menuController.showWinner(winner);
		stage.setScene(menuScene);
	}
	
	private void initializeStage(Stage primaryStage){
		stage = primaryStage;
		stage.setX(50);
		stage.setY(50);
		primaryStage.setTitle("Backgammon");
		Image icon = new Image(getClass().getResource("/images/backgammonIcon.png").toExternalForm());
		primaryStage.getIcons().add(icon);
		primaryStage.show();
	}
	
}
