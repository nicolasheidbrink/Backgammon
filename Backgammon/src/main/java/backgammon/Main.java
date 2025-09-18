package backgammon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/Board.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Backgammon");
			primaryStage.setScene(scene);
			Image icon = new Image(getClass().getResource("/images/backgammonIcon.png").toExternalForm());
			primaryStage.getIcons().add(icon);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
