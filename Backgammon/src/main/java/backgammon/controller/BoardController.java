package backgammon.controller;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import backgammon.model.Board;
import backgammon.model.GameMaster;
import backgammon.model.MoveSequence;
import backgammon.model.ProgramMaster;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class BoardController {

	private ProgramMaster programMaster;
	private GameMaster gameMaster;
	
	@FXML
	private Circle checker1, checker2, checker3, checker4, checker5, checker6, checker7, checker8, 
				checker9, checker10, checker11, checker12, checker13, checker14, checker15, checker16, 
				checker17, checker18, checker19, checker20, checker21, checker22, checker23, checker24,
				checkerXBar, checkerOBar, diceCircle;
	
	@FXML
	private Label checkerLabel1, checkerLabel2, checkerLabel3, checkerLabel4, checkerLabel5, checkerLabel6, 
				checkerLabel7, checkerLabel8, checkerLabel9, checkerLabel10, checkerLabel11, checkerLabel12, 
				checkerLabel13, checkerLabel14, checkerLabel15, checkerLabel16, checkerLabel17, checkerLabel18, 
				checkerLabel19, checkerLabel20, checkerLabel21, checkerLabel22, checkerLabel23, checkerLabel24,
				checkerLabelXBar, checkerLabelOBar, leftDie, rightDie, trayXLabel, trayOLabel;
	
	@FXML
	private Polygon point1, point2, point3, point4, point5, point6, point7, point8,
				point9, point10, point11, point12, point13, point14, point15, point16,
				point17, point18, point19, point20, point21, point22, point23, point24;
	
	@FXML
	private Rectangle trayO, trayX;
	
	private Circle[] checkers;
	private Label[] checkerLabels;
	private Polygon[] points;
	
	@FXML
	public void initialize(){
		checkers = new Circle[]{checker1, checker2, checker3, checker4, checker5, checker6, checker7, checker8, 
				checker9, checker10, checker11, checker12, checker13, checker14, checker15, checker16, 
				checker17, checker18, checker19, checker20, checker21, checker22, checker23, checker24};
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
	
	public void setDiceColor(boolean green){
		if(green) diceCircle.setFill(Color.GREEN);
		else diceCircle.setFill(Color.RED);
	}
	
	public void updateBoard(Board board, int selectedChecker, Set<MoveSequence> nextMoves, int moveWithinTurn){
		for(int i = 0; i < 24; i++){
			if(board.points[i].occupiedBy == '-'){
				checkers[i].setVisible(false);
				checkerLabels[i].setVisible(false);
			}
			else if(board.points[i].occupiedBy == 'X'){
				checkers[i].setVisible(true);
				checkerLabels[i].setVisible(true);
				checkerLabels[i].setText("" + board.points[i].amtCheckers);
				checkers[i].setFill(Color.BLACK);
				checkers[i].setStroke(Color.WHITE);
				checkerLabels[i].setTextFill(Color.WHITE);
			}
			else if(board.points[i].occupiedBy == 'O'){
				checkers[i].setVisible(true);
				checkerLabels[i].setVisible(true);
				checkerLabels[i].setText("" + board.points[i].amtCheckers);
				checkers[i].setFill(Color.WHITE);
				checkers[i].setStroke(Color.BLACK);
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
			if(checkers[i].getFill() == Color.BLACK) checkers[i].setStroke(Color.WHITE);
			if(checkers[i].getFill() == Color.WHITE) checkers[i].setStroke(Color.BLACK);
			points[i].setStroke(points[i].getFill());
		}
		
		if(selectedChecker == 24) checkerOBar.setStroke(Color.LIME);
		else try{
			checkers[selectedChecker].setStroke(Color.LIME);
		} catch (Exception e){}
		
		Set<Integer> toBeHighlighted = nextMoves.stream()
				.filter(ms -> ms.moves().get(moveWithinTurn).from == selectedChecker)
				.map(ms -> ms.moves().get(moveWithinTurn).to)
				.collect(Collectors.toSet());
		for(int to : toBeHighlighted){
			if(to == -1) trayO.setStroke(Color.LIME);
			else points[to].setStroke(Color.LIME);
		}
		leftDie.setText(""+board.leftDie);
		rightDie.setText(""+board.rightDie);
	}
	
	public void updateBoard(Board board){
		updateBoard(board, Integer.MIN_VALUE, new HashSet<MoveSequence>(), Integer.MIN_VALUE);
	}
	
	@FXML
	private void diceRolled(MouseEvent e){
		gameMaster.rollDice();
	}

	@FXML
	private void doubleOffered(MouseEvent e){
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
