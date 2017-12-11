package cz.vutbr.fit.pdb.component.rightbar.geometry.lineinfo;

import cz.vutbr.fit.pdb.component.rightbar.geometry.AbstractPointListBasedInfoPresenter;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.concurent.geometry.GetLineDetailsTask;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.LineDetails;
import cz.vutbr.fit.pdb.entity.geometry.LineGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.ExceptionUtils.printException;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;

public class LineInfoPresenter extends AbstractPointListBasedInfoPresenter {

    @FXML
    private VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        loadDetails();
    }

    private void loadDetails() {
        ObservableList<Node> children = vbox.getChildren();
        if (children.size() > 2)
            children.remove(2, children.size());

        GetLineDetailsTask lineDetailsTask = new GetLineDetailsTask();
        lineDetailsTask.setEntity(getSelectedEntity());
        lineDetailsTask.setOnSucceeded(event -> {
            LineDetails lineDetails = lineDetailsTask.getValue();
            Text text = new Text("Length of the line is: " + lineDetails.getLength());
            text.setWrappingWidth(150);
            vbox.getChildren()
                .add(text);
        });
        lineDetailsTask.setOnFailed(event -> {
            printException(lineDetailsTask.getException());
            showError("Database error", "Could not load details");
        });
        Configuration.THREAD_POOL.submit(lineDetailsTask);
    }

    @Override
    public EntityGeometry createGeometry(ObservableList<Point> points) {
        return new LineGeometry(points);
    }

    @Override
    protected void reloadDetails() {
        loadDetails();
    }
}
