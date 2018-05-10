package pl.si.put.poznan.edge.magic.total.labeling;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLController implements Initializable{
    
    
    @FXML 
    private TextField number;
    
    @FXML 
    private ComboBox<String> w1Combo; 
    
    @FXML
    private void handleButtonConfirm(ActionEvent event) throws IOException {
        System.out.println("You clicked me!");
        System.out.println(number.getText());
        

        Parent home_page_parent = FXMLLoader.load(getClass().getResource("/fxml/mainpage.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                
        app_stage.setScene(home_page_scene);
        app_stage.show();  
           
    }
    
    @FXML
    private void handleButtonEdge(ActionEvent event) throws IOException {
        System.out.println("Dodano krawedź!");
  
           
    }
    
    
    @FXML
    private void handleButtonSolve(ActionEvent event) throws IOException {
        System.out.println("Rozwiazywanie...");
  
           
    }
    
    
    @FXML
    private void handleButtonClear(ActionEvent event) throws IOException {
        System.out.println("Czyszczenie...");
  
           
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
