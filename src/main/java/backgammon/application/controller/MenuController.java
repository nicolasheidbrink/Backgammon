package backgammon.application.controller;

import backgammon.application.model.gameModels.CheckerColors;
import backgammon.application.model.operation.ProgramMaster;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class MenuController {

	ProgramMaster programMaster;
	
	@FXML
	private Text endGameMessage, scoreText;
	
	@FXML
	private Circle option0O, option1O, option2O, option3O, option1X, option2X, option3X;
	
	public void setProgramMaster(ProgramMaster programMaster){
		this.programMaster = programMaster;
	}
	
	public void updateScore(int scoreO, int scoreX){
		scoreText.setText("WHITE " + scoreO + " - " + scoreX + " BLACK");
	}
	
	public void showWinner(CheckerColors winner){
		if(winner == CheckerColors.O){
			endGameMessage.setText("WHITE WINS");
		}
		if(winner == CheckerColors.X){
			endGameMessage.setText("BLACK WINS");
		}
		endGameMessage.setVisible(true);
	}
	
	@FXML
	private void startGameClicked(){
		programMaster.gameStarted();
	}
	
	@FXML
	private void tbdClicked(){
		
	}
	
	@FXML
	private void option0OClicked(){
		programMaster.optionClicked(CheckerColors.O, 0);
		option0O.setStrokeWidth(2);
		option1O.setStrokeWidth(0);
		option2O.setStrokeWidth(0);
		option3O.setStrokeWidth(0);
		option0O.setFill(Color.web("#F3E1B9"));
		option1O.setFill(Color.web("#86471D"));
		option2O.setFill(Color.web("#86471D"));
		option3O.setFill(Color.web("#86471D"));
	}

	@FXML
	private void option1OClicked(){
		programMaster.optionClicked(CheckerColors.O, 1);
		option0O.setStrokeWidth(0);
		option1O.setStrokeWidth(2);
		option2O.setStrokeWidth(0);
		option3O.setStrokeWidth(0);
		option0O.setFill(Color.web("#86471D"));
		option1O.setFill(Color.web("#F3E1B9"));
		option2O.setFill(Color.web("#86471D"));
		option3O.setFill(Color.web("#86471D"));
	}

	@FXML
	private void option2OClicked(){
		programMaster.optionClicked(CheckerColors.O, 2);
		option0O.setStrokeWidth(0);
		option1O.setStrokeWidth(0);
		option2O.setStrokeWidth(2);
		option3O.setStrokeWidth(0);
		option0O.setFill(Color.web("#86471D"));
		option1O.setFill(Color.web("#86471D"));
		option2O.setFill(Color.web("#F3E1B9"));
		option3O.setFill(Color.web("#86471D"));
	}

	@FXML
	private void option3OClicked(){
		programMaster.optionClicked(CheckerColors.O, 3);
		option0O.setStrokeWidth(0);
		option1O.setStrokeWidth(0);
		option2O.setStrokeWidth(0);
		option3O.setStrokeWidth(2);
		option0O.setFill(Color.web("#86471D"));
		option1O.setFill(Color.web("#86471D"));
		option2O.setFill(Color.web("#86471D"));
		option3O.setFill(Color.web("#F3E1B9"));
	}

	@FXML
	private void option1XClicked(){
		programMaster.optionClicked(CheckerColors.X, 1);
		option1X.setStrokeWidth(2);
		option2X.setStrokeWidth(0);
		option3X.setStrokeWidth(0);
		option1X.setFill(Color.web("#3E2514"));
		option2X.setFill(Color.web("#86471D"));
		option3X.setFill(Color.web("#86471D"));
	}
	
	@FXML
	private void option2XClicked(){
		programMaster.optionClicked(CheckerColors.X, 2);
		option1X.setStrokeWidth(0);
		option2X.setStrokeWidth(2);
		option3X.setStrokeWidth(0);
		option1X.setFill(Color.web("#86471D"));
		option2X.setFill(Color.web("#3E2514"));
		option3X.setFill(Color.web("#86471D"));
	}
	
	@FXML
	private void option3XClicked(){
		programMaster.optionClicked(CheckerColors.X, 3);
		option1X.setStrokeWidth(0);
		option2X.setStrokeWidth(0);
		option3X.setStrokeWidth(2);
		option1X.setFill(Color.web("#86471D"));
		option2X.setFill(Color.web("#86471D"));
		option3X.setFill(Color.web("#3E2514"));
	}
}
