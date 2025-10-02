package backgammon.controller;

import backgammon.model.ProgramMaster;
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
	
	@FXML
	private void startGameClicked(){
		programMaster.gameStarted();
	}
	
	@FXML
	private void tbdClicked(){
		
	}
}
