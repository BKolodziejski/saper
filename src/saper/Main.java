package saper;
 
import javafx.application.Application;
import javafx.stage.Stage;
import saper.scores.SQLManager;
 
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	
    	DifficultyLevel diff = XMLManager.loadDifficulty();
 
        
    	MineField mf = new MineField(diff);
    	primaryStage.setOnCloseRequest(e -> 
    	{
    		SQLManager.saveScores(mf.getDifficulty());
    		XMLManager.saveDifficulty(mf.getDifficulty());
    	});
    	
    	View view = new View(primaryStage, diff.X_TILES, diff.Y_TILES);
    	new Controller(mf, view);
    }
}
