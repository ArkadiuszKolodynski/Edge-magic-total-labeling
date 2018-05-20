package pl.si.put.poznan.edge.magic.total.labeling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import org.graphstream.graph.Edge;
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

    private static int numberOfNodes;
    private static int numberOfEdges;
    private static final List<String> nodes = new ArrayList<>();
    private static final List<String> edges = new ArrayList<>();

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
        try {
            numberOfNodes = Integer.parseInt(number.getText());
            numberOfEdges = 0;
            graph.clear();
            nodes.clear();
            edges.clear();

            Map<String, Object> attributes = new HashMap<>();

            for (int i = 1; i <= numberOfNodes; i++) {
                String j = Integer.toString(i);
                attributes.put("ui.label", j);
                attributes.put("ui.style", "size: 30px, 30px; text-alignment: under; text-size: 25;");
                graph.addNode(j).setAttributes(attributes);
                nodes.add("w" + j);
                attributes.clear();
            }

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
            numberOfEdges++;
            String edge = "kw" + w1Combo.getValue() + "w" + w2Combo.getValue();
            edges.add(edge);
        }
    }

    @FXML
    private void handleButtonSolve(ActionEvent event) throws IOException, InterruptedException {
        createBeeFile();
        createCnfAndMapFiles();
        createSolFile();
        solveSatProblem();
        clearDir();
    }

    @FXML
    private void handleButtonClear(ActionEvent event) throws IOException {
        for (Object e : graph.edges().toArray()) {
            graph.removeEdge((Edge) e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (numberOfNodes > 0) {
            number.setText(Integer.toString(numberOfNodes));
        }

        if (w1Combo != null && w2Combo != null) {
            for (int i = 1; i <= numberOfNodes; i++) {
                String j = Integer.toString(i);
                w1Combo.getItems().add(j);
                w2Combo.getItems().add(j);
            }
        }

        if (pane != null) {
            graph.setAttribute("ui.stylesheet", "graph { fill-color: red; }");
            Viewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            viewer.enableAutoLayout();
            FxViewPanel v = (FxViewPanel) viewer.addDefaultView(false);
            pane.getChildren().add(v);
        }
    }
    
    private void createBeeFile() throws FileNotFoundException {
        File file = new File("plik.bee");
        try (PrintWriter zapis = new PrintWriter("plik.bee")) {
            for (int i = 1; i <= numberOfNodes; i++) {
                zapis.println("new_int(w" + i + ",1," + (numberOfNodes + numberOfEdges) + ")");
            }
            for (int i = 0; i < numberOfEdges; i++) {
                zapis.println("new_int(" + edges.get(i) + ",1," + (numberOfNodes + numberOfEdges) + ")");
            }
            int magic = ((numberOfNodes + numberOfEdges) * 3) - 3;
            zapis.println("new_int(m,1," + magic + ")");

            for (int i = 0; i < numberOfEdges; i++) {
                String[] v = edges.get(i).split("w");
                zapis.println("int_array_sum_eq([" + edges.get(i) + ",w" + v[1] + ",w" + v[2] + "],m)");
            }

            String s = "int_array_allDiff([w1";
            for (int i = 2; i <= numberOfNodes; i++) {
                s = s + ",w" + i;
            }
            for (int i = 0; i < numberOfEdges; i++) {
                s = s + "," + edges.get(i);
            }
            s = s + "])";
            zapis.println(s);
            zapis.println("solve satisfy");
        }
    }

    private void createCnfAndMapFiles() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("bumblebee.exe plik.bee -dimacs dimacs.cnf dimacs.map");
        p.waitFor();
    }

    private void createSolFile() throws FileNotFoundException, UnsupportedEncodingException {
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

    private void solveSatProblem() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("bumblesol.exe dimacs.map dimacs.sol");
        p.waitFor();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
            for (int i = 0; i < numberOfNodes; i++) {
                if (s.startsWith(nodes.get(i))) {
                    graph.getNode(i).setAttribute("ui.label", s.split(" ")[2]);
                }
            }
            for (int i = 0; i < numberOfEdges; i++) {
                if (s.contains(edges.get(i))) {
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("ui.label", s.split(" ")[2]);
                    attributes.put("ui.style", "text-size: 30;");
                    graph.getEdge(i).setAttributes(attributes);
                }
            }
        }
    }

    private void clearDir() {
        List<File> files = new ArrayList<>();
        files.add(new File("dimacs.cnf"));
        files.add(new File("dimacs.map"));
        files.add(new File("dimacs.sol"));
        files.add(new File("plik.bee"));

        for (File f : files) {
            if (f.exists() && !f.isDirectory()) {
                f.delete();
            }
        }
    }

}
