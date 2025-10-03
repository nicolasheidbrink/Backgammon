package backgammon.controller;

import backgammon.model.game.CheckerColors;
import backgammon.model.operation.ProgramMaster;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class MenuController {

	ProgramMaster programMaster;
	
	@FXML
	private Text endGameMessage, scoreText;
	
	public void setProgramMaster(ProgramMaster programMaster){
		this.programMaster = programMaster;
	}
	
	public void updateScore(int scoreO, int scoreX){
		scoreText.setText("YOU " + scoreO + " - " + scoreX + " COMPUTER");
	}
	
	public void showWinner(CheckerColors winner){
		if(winner == CheckerColors.O){
			endGameMessage.setText("YOU WIN");
			endGameMessage.setLayoutX(296.0);
		}
		if(winner == CheckerColors.X){
			endGameMessage.setText("YOU LOSE");
			endGameMessage.setLayoutX(282.0);
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
}
