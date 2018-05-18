package pl.si.put.poznan.edge.magic.total.labeling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class FXMLController implements Initializable {

    private static String numberOfNodes;

    private static final Graph graph = new SingleGraph("Edge-magic-total-labeling");

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

        try {
            for (int i = 1; i <= Integer.parseInt(numberOfNodes); i++) {
                String j = Integer.toString(i);
                graph.addNode(j).setAttribute("ui.label", j);
            }

            graph.setAttribute("ui.stylesheet", styleSheet);

            Parent home_page_parent = FXMLLoader.load(getClass().getResource("/fxml/mainpage.fxml"));
            Scene home_page_scene = new Scene(home_page_parent);
            Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            app_stage.setScene(home_page_scene);
            app_stage.show();
        } catch (IOException | NumberFormatException | IdAlreadyInUseException e) {
            number.setStyle("-fx-border-color: red");
        }
    }

    @FXML
    private void handleButtonEdge(ActionEvent event) throws IOException {
        if (w1Combo.getValue() != null && w2Combo.getValue() != null) {
            graph.addEdge(w1Combo.getValue() + w2Combo.getValue(), w1Combo.getValue(), w2Combo.getValue());
        }
    }

    @FXML
    private void handleButtonSolve(ActionEvent event) throws IOException {
        graph.write("graph.dgs");

        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600);
        Reader reader = new DimacsReader(solver);
        PrintWriter writer = new PrintWriter("dimacs.sol", "UTF-8");
        try {
            IProblem problem = reader.parseInstance("dimacs.cnf");
            if (problem.isSatisfiable()) {
                writer.println("SAT");
                for (int e : problem.model()) {
                    writer.print(e + " ");
                }
                writer.print("0");
                writer.close();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Wystąpił błąd");
                alert.setHeaderText("Wystąpił błąd");
                alert.setContentText("Ten problem jest nierozwiązywalny!");
                alert.showAndWait();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (ContradictionException | ParseFormatException | IOException | TimeoutException e) {
            System.out.println(e.getMessage());
        }
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
            FxViewPanel v = (FxViewPanel) viewer.addDefaultView(false);
            pane.getChildren().add(v);
        }
    }

    protected static String styleSheet
            = "node {"
            + "    size: 20px, 20px;"
            + "    fill-color: rgb(255,0,0);"
            + "}";

}
