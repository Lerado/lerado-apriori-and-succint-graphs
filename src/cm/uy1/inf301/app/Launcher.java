package cm.uy1.inf301.app;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Launcher extends Application{
	
	public void start(Stage primaryStage) throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("app.fxml"));
		primaryStage = loader.load();
		Image applicationIcon = new Image(getClass().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(applicationIcon);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		
		launch(args);
	}
}
