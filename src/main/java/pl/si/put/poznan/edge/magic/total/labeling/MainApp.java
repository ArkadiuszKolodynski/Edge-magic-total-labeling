package pl.si.put.poznan.edge.magic.total.labeling;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/startpage.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("Edge-magic-total-labeling");
        stage.setScene(scene);
        stage.show();
    }
    
 
    public static void main(String[] args) {
        launch(args);
    }

}
