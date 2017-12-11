package cz.vutbr.fit.pdb.component.rightbar.geometry.pointinfo;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.concurent.geometry.GetPointDetailsTask;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PointDetails;
import cz.vutbr.fit.pdb.entity.geometry.PointGeometry;
import cz.vutbr.fit.pdb.utils.Listeners;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.ExceptionUtils.printException;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

public class PointInfoPresenter implements Initializable {

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;

    @FXML
    private VBox vbox;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private EntityService entityService;

    @Inject
    private Configuration configuration;

    private Point editedPoint;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedEntityService.getEntityProperty()
                             .geometryProperty()
                             .addListener((observable, oldValue, newValue) -> {
                                 initForPoint(newValue);
                                 realoadDetails();
                             });
        initForPoint(selectedEntityService.getEntityProperty()
                                          .getGeometry());

        loadDetails();

        Listeners.addRedrawListener(configuration.getMapRenderer(), xField.textProperty(), yField.textProperty());
    }

    private void realoadDetails() {
        loadDetails();
    }

    private void loadDetails() {
        ObservableList<Node> children = vbox.getChildren();
        if (children.size() > 1)
            children.remove(1, children.size());

        GetPointDetailsTask pointDetailsTask = new GetPointDetailsTask();
        pointDetailsTask.setEntity(selectedEntityService.getEntityProperty());
        pointDetailsTask.setOnSucceeded(event -> {
            PointDetails pointDetails = pointDetailsTask.getValue();
            Text nearestRiver = new Text("Nearest river: " + pointDetails.getNearestRiver());
            nearestRiver.setWrappingWidth(150);
            vbox.getChildren()
                .addAll(nearestRiver);
        });
        pointDetailsTask.setOnFailed(event -> {
            printException(pointDetailsTask.getException());
            showError("Database error", "Could not load details");
        });
        Configuration.THREAD_POOL.submit(pointDetailsTask);
    }

    private void initForPoint(EntityGeometry entityGeometry) {
        Point originalPoint = (Point) entityGeometry.getDescription();
        if (editedPoint != null) {
            xField.textProperty()
                  .unbindBidirectional(editedPoint.xProperty());
            yField.textProperty()
                  .unbindBidirectional(editedPoint.yProperty());
        }
        editedPoint = new Point(originalPoint.getX(), originalPoint.getY());

        xField.textProperty()
              .bindBidirectional(editedPoint.xProperty(), new StringNumConverter());
        yField.textProperty()
              .bindBidirectional(editedPoint.yProperty(), new StringNumConverter());

        EventHandler<ActionEvent> pointUpdateListener = (event) -> {
            Entity copy = selectedEntityService.getEntityProperty()
                                               .copyOf();
            copy.setGeometry(new PointGeometry(editedPoint));

            entityService.updateEntity(copy, "geometry",
                    (newEntity) -> {
                        originalPoint.setX(editedPoint.getX());
                        originalPoint.setY(editedPoint.getY());
                        configuration.getMapRenderer()
                                     .redraw();
                        showInfo("Entity updated", "Entity updated successfully");
                    }, () -> {
                        editedPoint.setX(originalPoint.getX());
                        editedPoint.setY(originalPoint.getY());
                        showError("Database error", "Could not update entity");
                    });
        };

        xField.setOnAction(pointUpdateListener);
        yField.setOnAction(pointUpdateListener);
    }
}
