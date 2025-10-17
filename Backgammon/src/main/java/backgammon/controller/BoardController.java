package backgammon.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import backgammon.model.gameCalculations.GameCalculation;
import backgammon.model.gameModels.Board;
import backgammon.model.gameModels.CheckerColors;
import backgammon.model.gameModels.MoveSequence;
import backgammon.model.operation.GameMaster;
import backgammon.model.operation.ProgramMaster;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BoardController {

	private ProgramMaster programMaster;
	private GameMaster gameMaster;
	
	@FXML
	private Circle checker1, checker2, checker3, checker4, checker5, checker6, checker7, checker8, 
				checker9, checker10, checker11, checker12, checker13, checker14, checker15, checker16, 
				checker17, checker18, checker19, checker20, checker21, checker22, checker23, checker24,
				checker1_2, checker2_2, checker3_2, checker4_2, checker5_2, checker6_2, checker7_2, checker8_2, 
				checker9_2, checker10_2, checker11_2, checker12_2, checker13_2, checker14_2, checker15_2, checker16_2, 
				checker17_2, checker18_2, checker19_2, checker20_2, checker21_2, checker22_2, checker23_2, checker24_2,
				checker1_3, checker2_3, checker3_3, checker4_3, checker5_3, checker6_3, checker7_3, checker8_3, 
				checker9_3, checker10_3, checker11_3, checker12_3, checker13_3, checker14_3, checker15_3, checker16_3, 
				checker17_3, checker18_3, checker19_3, checker20_3, checker21_3, checker22_3, checker23_3, checker24_3,
				checker1_4, checker2_4, checker3_4, checker4_4, checker5_4, checker6_4, checker7_4, checker8_4, 
				checker9_4, checker10_4, checker11_4, checker12_4, checker13_4, checker14_4, checker15_4, checker16_4, 
				checker17_4, checker18_4, checker19_4, checker20_4, checker21_4, checker22_4, checker23_4, checker24_4,
				checker1_5, checker2_5, checker3_5, checker4_5, checker5_5, checker6_5, checker7_5, checker8_5, 
				checker9_5, checker10_5, checker11_5, checker12_5, checker13_5, checker14_5, checker15_5, checker16_5, 
				checker17_5, checker18_5, checker19_5, checker20_5, checker21_5, checker22_5, checker23_5, checker24_5,
				checkerXBar, checkerOBar, diceCircle;
	
	@FXML
	private Label checkerLabel1, checkerLabel2, checkerLabel3, checkerLabel4, checkerLabel5, checkerLabel6, 
				checkerLabel7, checkerLabel8, checkerLabel9, checkerLabel10, checkerLabel11, checkerLabel12, 
				checkerLabel13, checkerLabel14, checkerLabel15, checkerLabel16, checkerLabel17, checkerLabel18, 
				checkerLabel19, checkerLabel20, checkerLabel21, checkerLabel22, checkerLabel23, checkerLabel24,
				checkerLabelXBar, checkerLabelOBar, leftDie, rightDie, trayXLabel, trayOLabel, pipO, pipX, scoreO, scoreX;
	
	@FXML
	private Polygon point1, point2, point3, point4, point5, point6, point7, point8,
				point9, point10, point11, point12, point13, point14, point15, point16,
				point17, point18, point19, point20, point21, point22, point23, point24;
	
	@FXML
	private Rectangle trayO, trayX, leftDieRect, rightDieRect;
	
	private Circle[][] checkers;
	private Label[] checkerLabels;
	private Polygon[] points;
	
	@FXML
	public void initialize(){
		checkers = new Circle[][]{
			new Circle[]{checker1, checker1_2, checker1_3, checker1_4, checker1_5},
			new Circle[]{checker2, checker2_2, checker2_3, checker2_4, checker2_5},
			new Circle[]{checker3, checker3_2, checker3_3, checker3_4, checker3_5},
			new Circle[]{checker4, checker4_2, checker4_3, checker4_4, checker4_5},
			new Circle[]{checker5, checker5_2, checker5_3, checker5_4, checker5_5},
			new Circle[]{checker6, checker6_2, checker6_3, checker6_4, checker6_5},
			new Circle[]{checker7, checker7_2, checker7_3, checker7_4, checker7_5},
			new Circle[]{checker8, checker8_2, checker8_3, checker8_4, checker8_5},
			new Circle[]{checker9, checker9_2, checker9_3, checker9_4, checker9_5},
			new Circle[]{checker10, checker10_2, checker10_3, checker10_4, checker10_5},
			new Circle[]{checker11, checker11_2, checker11_3, checker11_4, checker11_5},
			new Circle[]{checker12, checker12_2, checker12_3, checker12_4, checker12_5},
			new Circle[]{checker13, checker13_2, checker13_3, checker13_4, checker13_5},
			new Circle[]{checker14, checker14_2, checker14_3, checker14_4, checker14_5},
			new Circle[]{checker15, checker15_2, checker15_3, checker15_4, checker15_5},
			new Circle[]{checker16, checker16_2, checker16_3, checker16_4, checker16_5},
			new Circle[]{checker17, checker17_2, checker17_3, checker17_4, checker17_5},
			new Circle[]{checker18, checker18_2, checker18_3, checker18_4, checker18_5},
			new Circle[]{checker19, checker19_2, checker19_3, checker19_4, checker19_5},
			new Circle[]{checker20, checker20_2, checker20_3, checker20_4, checker20_5},
			new Circle[]{checker21, checker21_2, checker21_3, checker21_4, checker21_5},
			new Circle[]{checker22, checker22_2, checker22_3, checker22_4, checker22_5},
			new Circle[]{checker23, checker23_2, checker23_3, checker23_4, checker23_5},
			new Circle[]{checker24, checker24_2, checker24_3, checker24_4, checker24_5}
		};
		checkerLabels = new Label[]{checkerLabel1, checkerLabel2, checkerLabel3, checkerLabel4, checkerLabel5, checkerLabel6, 
				checkerLabel7, checkerLabel8, checkerLabel9, checkerLabel10, checkerLabel11, checkerLabel12, 
				checkerLabel13, checkerLabel14, checkerLabel15, checkerLabel16, checkerLabel17, checkerLabel18, 
				checkerLabel19, checkerLabel20, checkerLabel21, checkerLabel22, checkerLabel23, checkerLabel24};
		points = new Polygon[]{point1, point2, point3, point4, point5, point6, point7, point8,
				point9, point10, point11, point12, point13, point14, point15, point16,
				point17, point18, point19, point20, point21, point22, point23, point24};
	}
	
	public void setProgramMaster(ProgramMaster programMaster){
		this.programMaster = programMaster;
	}
	
	public void setGameMaster(GameMaster gameMaster){
		this.gameMaster = gameMaster;
	}
	
	public void showDiceRoll(int leftDieRoll, int rightDieRoll){
		this.leftDie.setText(""+leftDieRoll);
		this.rightDie.setText(""+rightDieRoll);
	}
	
	public void setDiceColorGreen(boolean green){
		if(green) diceCircle.setFill(Color.GREEN);
		else diceCircle.setFill(Color.RED);
	}
	
	public void updateBoard(Board board, int selectedChecker, Set<MoveSequence> nextMoves, int moveWithinTurn){
		showDiceRoll(board.leftDie, board.rightDie);
		for(int i = 0; i < 24; i++){
			if(board.points[i].occupiedBy == CheckerColors.NA){
				for(int j = 0; j < 4; j++){
					checkers[i][j].setVisible(false);
				}
				checkerLabels[i].setVisible(false);
			}
			else if(board.points[i].occupiedBy == CheckerColors.X){
				for(int j = 0; j < 5; j++){
					if(board.points[i].amtCheckers > j){
						checkers[i][j].setVisible(true);
						checkers[i][j].setFill(Color.BLACK);
						checkers[i][j].setStroke(Color.WHITE);
					}
					else{
						checkers[i][j].setVisible(false);
					}
				}
				checkerLabels[i].setVisible(true);
				checkerLabels[i].setText("" + board.points[i].amtCheckers);
				checkerLabels[i].setTextFill(Color.WHITE);
			}
			else if(board.points[i].occupiedBy == CheckerColors.O){
				for(int j = 0; j < 5; j++){
					if(board.points[i].amtCheckers > j){
						checkers[i][j].setVisible(true);
						checkers[i][j].setFill(Color.WHITE);
						checkers[i][j].setStroke(Color.BLACK);
					}
					else{
						checkers[i][j].setVisible(false);
					}
				}
				checkerLabels[i].setVisible(true);
				checkerLabels[i].setText("" + board.points[i].amtCheckers);
				checkerLabels[i].setTextFill(Color.BLACK);
			}
		}
		if(board.barO == 0){
			checkerLabelOBar.setVisible(false);
			checkerOBar.setVisible(false);
		}
		else{
			checkerLabelOBar.setVisible(true);
			checkerOBar.setVisible(true);
			checkerLabelOBar.setText("" + board.barO);
		}
		if(board.barX == 0){
			checkerLabelXBar.setVisible(false);
			checkerXBar.setVisible(false);
		}
		else{
			checkerLabelXBar.setVisible(true);
			checkerXBar.setVisible(true);
			checkerLabelXBar.setText("" + board.barX);
		}
		
		trayOLabel.setText("" + board.trayO);
		trayXLabel.setText("" + board.trayX);
		
		checkerOBar.setStroke(Color.BLACK);
		trayO.setStroke(trayO.getFill());
		for(int i = 0; i < 24; i++){
			points[i].setStroke(points[i].getFill());
		}
		
		if(selectedChecker == 24) checkerOBar.setStroke(Color.GREEN);
		else try{
			for(int j = 0; j < 5; j++){
				checkers[selectedChecker][j].setStroke(Color.GREEN);
			}
		} catch (Exception e){}
		
		Set<Integer> toBeHighlighted = nextMoves.stream()
				.filter(ms -> ms.moves().get(moveWithinTurn).from == selectedChecker)
				.map(ms -> ms.moves().get(moveWithinTurn).to)
				.collect(Collectors.toSet());
		for(int to : toBeHighlighted){
			if(to == -1) trayO.setStroke(Color.GREEN);
			else points[to].setStroke(Color.GREEN);
		}
		leftDie.setText(""+board.leftDie);
		rightDie.setText(""+board.rightDie);
	}
	
	public void updateBoard(Board board){
		updateBoard(board, Integer.MIN_VALUE, new HashSet<MoveSequence>(), Integer.MIN_VALUE);
	}
	
	public void updateScore(int scoreO, int scoreX){
		this.scoreO.setText("Score: " + scoreO);
		this.scoreX.setText("Score: " + scoreX);
	}
	
	public void updatePips(int pipO, int pipX){
		this.pipO.setText("PIP: " + pipO);
		this.pipX.setText("PIP: " + pipX);

	}
	
	@FXML
	private void diceRolled(MouseEvent e){
		gameMaster.diceButtonClicked();
	}

	@FXML
	private void doubleOffered(MouseEvent e){
		System.out.println(gameMaster.getBoard());
	}

	@FXML
	private void barClicked(MouseEvent e){
	}
	
	@FXML
	private void point1Clicked(MouseEvent e){
		gameMaster.pointClicked(0);
	}
	
	@FXML
	private void point2Clicked(MouseEvent e){
		gameMaster.pointClicked(1);
	}

	@FXML
	private void point3Clicked(MouseEvent e){
		gameMaster.pointClicked(2);
	}

	@FXML
	private void point4Clicked(MouseEvent e){
		gameMaster.pointClicked(3);
	}

	@FXML
	private void point5Clicked(MouseEvent e){
		gameMaster.pointClicked(4);
	}

	@FXML
	private void point6Clicked(MouseEvent e){
		gameMaster.pointClicked(5);
	}

	@FXML
	private void point7Clicked(MouseEvent e){
		gameMaster.pointClicked(6);
	}

	@FXML
	private void point8Clicked(MouseEvent e){
		gameMaster.pointClicked(7);
	}

	@FXML
	private void point9Clicked(MouseEvent e){
		gameMaster.pointClicked(8);
	}

	@FXML
	private void point10Clicked(MouseEvent e){
		gameMaster.pointClicked(9);
	}

	@FXML
	private void point11Clicked(MouseEvent e){
		gameMaster.pointClicked(10);
	}

	@FXML
	private void point12Clicked(MouseEvent e){
		gameMaster.pointClicked(11);
	}

	@FXML
	private void point13Clicked(MouseEvent e){
		gameMaster.pointClicked(12);
	}

	@FXML
	private void point14Clicked(MouseEvent e){
		gameMaster.pointClicked(13);
	}
	
	@FXML
	private void point15Clicked(MouseEvent e){
		gameMaster.pointClicked(14);
	}

	@FXML
	private void point16Clicked(MouseEvent e){
		gameMaster.pointClicked(15);
	}

	@FXML
	private void point17Clicked(MouseEvent e){
		gameMaster.pointClicked(16);
	}

	@FXML
	private void point18Clicked(MouseEvent e){
		gameMaster.pointClicked(17);
	}

	@FXML
	private void point19Clicked(MouseEvent e){
		gameMaster.pointClicked(18);
	}

	@FXML
	private void point20Clicked(MouseEvent e){
		gameMaster.pointClicked(19);
	}

	@FXML
	private void point21Clicked(MouseEvent e){
		gameMaster.pointClicked(20);
	}

	@FXML
	private void point22Clicked(MouseEvent e){
		gameMaster.pointClicked(21);
	}

	@FXML
	private void point23Clicked(MouseEvent e){
		gameMaster.pointClicked(22);
	}

	@FXML
	private void point24Clicked(MouseEvent e){
		gameMaster.pointClicked(23);
	}

	@FXML
	private void checker1Clicked(MouseEvent e){
		gameMaster.checkerClicked(0);
	}
	
	@FXML
	private void checker2Clicked(MouseEvent e){
		gameMaster.checkerClicked(1);
	}

	@FXML
	private void checker3Clicked(MouseEvent e){
		gameMaster.checkerClicked(2);
	}

	@FXML
	private void checker4Clicked(MouseEvent e){
		gameMaster.checkerClicked(3);
	}

	@FXML
	private void checker5Clicked(MouseEvent e){
		gameMaster.checkerClicked(4);
	}

	@FXML
	private void checker6Clicked(MouseEvent e){
		gameMaster.checkerClicked(5);
	}

	@FXML
	private void checker7Clicked(MouseEvent e){
		gameMaster.checkerClicked(6);
	}

	@FXML
	private void checker8Clicked(MouseEvent e){
		gameMaster.checkerClicked(7);
	}

	@FXML
	private void checker9Clicked(MouseEvent e){
		gameMaster.checkerClicked(8);
	}

	@FXML
	private void checker10Clicked(MouseEvent e){
		gameMaster.checkerClicked(9);
	}

	@FXML
	private void checker11Clicked(MouseEvent e){
		gameMaster.checkerClicked(10);
	}

	@FXML
	private void checker12Clicked(MouseEvent e){
		gameMaster.checkerClicked(11);
	}

	@FXML
	private void checker13Clicked(MouseEvent e){
		gameMaster.checkerClicked(12);
	}

	@FXML
	private void checker14Clicked(MouseEvent e){
		gameMaster.checkerClicked(13);
	}
	
	@FXML
	private void checker15Clicked(MouseEvent e){
		gameMaster.checkerClicked(14);
	}

	@FXML
	private void checker16Clicked(MouseEvent e){
		gameMaster.checkerClicked(15);
	}

	@FXML
	private void checker17Clicked(MouseEvent e){
		gameMaster.checkerClicked(16);
	}

	@FXML
	private void checker18Clicked(MouseEvent e){
		gameMaster.checkerClicked(17);
	}

	@FXML
	private void checker19Clicked(MouseEvent e){
		gameMaster.checkerClicked(18);
	}

	@FXML
	private void checker20Clicked(MouseEvent e){
		gameMaster.checkerClicked(19);
	}

	@FXML
	private void checker21Clicked(MouseEvent e){
		gameMaster.checkerClicked(20);
	}

	@FXML
	private void checker22Clicked(MouseEvent e){
		gameMaster.checkerClicked(21);
	}

	@FXML
	private void checker23Clicked(MouseEvent e){
		gameMaster.checkerClicked(22);
	}

	@FXML
	private void checker24Clicked(MouseEvent e){
		gameMaster.checkerClicked(23);
	}
	
	@FXML
	private void checkerOBarClicked(MouseEvent e){
		gameMaster.checkerClicked(24);
	}

	@FXML
	private void checkerXBarClicked(MouseEvent e){
	}
	
	@FXML
	private void trayOClicked(MouseEvent e){
		gameMaster.pointClicked(-1);
	}

	@FXML
	private void trayXClicked(MouseEvent e){
	}
}