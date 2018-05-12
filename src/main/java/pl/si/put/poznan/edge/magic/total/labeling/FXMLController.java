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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;

public class FXMLController implements Initializable {

    private static String numberOfNodes;

    private static final Graph graph = new SingleGraph("Tutorial 1");

    @FXML
    private TextField number;

    @FXML
    private ComboBox<String> w1Combo;

    @FXML
    private ComboBox<String> w2Combo;

    @FXML
    private Pane pane;

    @FXML
    private void handleButtonConfirm(ActionEvent event) throws IOException {
        numberOfNodes = number.getText();

        graph.clear();

        for (int i = 1; i <= Integer.parseInt(numberOfNodes); i++) {
            String j = Integer.toString(i);
            graph.addNode(j).setAttribute("ui.label", j);
        }

        Parent home_page_parent = FXMLLoader.load(getClass().getResource("/fxml/mainpage.fxml"));
        Scene home_page_scene = new Scene(home_page_parent);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        app_stage.setScene(home_page_scene);
        app_stage.show();
//        graph.display();
    }

    @FXML
    private void handleButtonEdge(ActionEvent event) throws IOException {
        if (w1Combo.getValue() != null && w2Combo.getValue() != null) {
            graph.addEdge(w1Combo.getValue() + w2Combo.getValue(), w1Combo.getValue(), w2Combo.getValue());
        }
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
        if (numberOfNodes != null) {
            number.setText(numberOfNodes);
        }

        if (w1Combo != null && w2Combo != null) {
            for (int i = 1; i <= Integer.parseInt(numberOfNodes); i++) {
                String j = Integer.toString(i);
                w1Combo.getItems().add(j);
                w2Combo.getItems().add(j);
            }
        }
        
        if (pane != null) {
            Viewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            viewer.enableAutoLayout();
            FxViewPanel v = (FxViewPanel)viewer.addDefaultView(false);
            pane.getChildren().add(v);
            pane.getChildren().get(0).resize(200, 200);
        }
    }

}
