package cz.vutbr.fit.pdb.component.rightbar.geometry.pointinfo;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PointGeometry;
import cz.vutbr.fit.pdb.utils.Listeners;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

public class PointInfoPresenter implements Initializable {

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private EntityService entityService;

    @Inject
    private Configuration configuration;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Point originalPoint = (Point) selectedEntityService.getEntityProperty()
                                                           .getGeometry()
                                                           .getDescription();
        Point editedPoint = new Point(originalPoint.getX(), originalPoint.getY());

        xField.textProperty()
              .bindBidirectional(editedPoint.xProperty(), new StringNumConverter());
        yField.textProperty()
              .bindBidirectional(editedPoint.yProperty(), new StringNumConverter());

        EventHandler<ActionEvent> pointUpdateListener = (event) -> {
            Entity copy = selectedEntityService.getEntityProperty()
                                               .copyOf();
            copy.setGeometry(new PointGeometry(editedPoint));

            entityService.updateEntity(copy, "geometry",
                    () -> {
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

        Listeners.addRedrawListener(configuration.getMapRenderer(), xField.textProperty(), yField.textProperty());
    }
}
