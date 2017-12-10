package cz.vutbr.fit.pdb.component.rightbar.geometry.polygoninfo;

import cz.vutbr.fit.pdb.component.rightbar.geometry.AbstractPointListBasedInfoPresenter;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.concurent.geometry.GetPolygonDetailsTask;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PolygonDetails;
import cz.vutbr.fit.pdb.entity.geometry.PolygonGeometry;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.ExceptionUtils.printException;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static java.util.stream.Collectors.joining;

public class PolygonInfoPresenter extends AbstractPointListBasedInfoPresenter {

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

        GetPolygonDetailsTask polygonDetailsTask = new GetPolygonDetailsTask();
        polygonDetailsTask.setEntity(getSelectedEntity());
        polygonDetailsTask.setOnSucceeded(event -> {
            PolygonDetails polygonDetails = polygonDetailsTask.getValue();
            Text area = new Text("Are of the polygon is: " + polygonDetails.getArea());
            Text circumference = new Text("Circumference of the polygon is: " + polygonDetails.getCircumference());
            Text entitiesInside = new Text("Contains entities: " +
                    polygonDetails.getEntitiesInside()
                                  .stream()
                                  .collect(joining(","))
            );
            vbox.getChildren()
                .addAll(area, circumference, entitiesInside);
        });
        polygonDetailsTask.setOnFailed(event -> {
            printException(polygonDetailsTask.getException());
            showError("Database error", "Could not load details");
        });
        Configuration.THREAD_POOL.submit(polygonDetailsTask);
    }

    @Override
    protected void reloadDetails() {
        loadDetails();
    }

    @Override
    public EntityGeometry createGeometry(ObservableList<Point> points) {
        return new PolygonGeometry(points);
    }


}
