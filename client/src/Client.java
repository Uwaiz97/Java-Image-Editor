import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Uwais M
 *
 */
public class Client extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//create ClientPane
		ClientPane root = new ClientPane(primaryStage);
		//set Scene
		Scene scene = new Scene(root, 800, 600);
		//set Stage
		primaryStage.setScene(scene);
		//set Title
		primaryStage.setTitle("Prac X");
		//show Stage
		primaryStage.show();	
	}

}
