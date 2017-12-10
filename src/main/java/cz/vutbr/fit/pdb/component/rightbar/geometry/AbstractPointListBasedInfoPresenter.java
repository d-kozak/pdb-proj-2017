package cz.vutbr.fit.pdb.component.rightbar.geometry;

import cz.vutbr.fit.pdb.component.rightbar.pointlistitem.PointListViewCell;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.Listeners;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;


@Log
public abstract class AbstractPointListBasedInfoPresenter implements Initializable {
    @FXML
    private ListView<Point> pointsListView;
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

    private ObservableList<Point> points;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        points = (ObservableList<Point>) selectedEntityService.getEntityProperty()
                                                              .getGeometry()
                                                              .getDescription();

        selectedEntityService.getEntityProperty()
                             .geometryProperty()
                             .addListener((observable, oldValue, newValue) -> {
                                 pointsListView.setItems(((ObservableList<Point>) newValue.getDescription()));
                             });

        pointsListView.setItems(points);
        pointsListView.setCellFactory(item -> new PointListViewCell(configuration, this::tryToUpdatePoint, this::tryToRemovePoint));

        Listeners.addRedrawListener(configuration.getMapRenderer(), points);
    }

    private void tryToUpdatePoint(Point oldValue, Point newValue) {
        Entity copy = selectedEntityService.getEntityProperty()
                                           .copyOf();
        ObservableList<Point> updatedPoints = FXCollections.observableArrayList(this.points);
        updatePointInList(oldValue, newValue, updatedPoints);
        copy.setGeometry(createGeometry(updatedPoints));
        entityService.updateEntity(copy, "geometry",
                () -> {
                    updatePointInList(oldValue, newValue, points);
                    configuration.getMapRenderer()
                                 .redraw();
                    showInfo("Entity updated", "Entity updated successfully");
                }, () -> {
                    showError("Database error", "Could not update entity");
                });
    }

    private void updatePointInList(Point oldValue, Point newValue, ObservableList<Point> updatedPoints) {
        int i = updatedPoints.indexOf(oldValue);
        Point fromList = updatedPoints.get(i);
        fromList.setX(newValue.getX());
        fromList.setY(newValue.getY());
    }

    private void tryToRemovePoint(Point point) {
        Entity copy = selectedEntityService.getEntityProperty()
                                           .copyOf();
        ObservableList<Point> newPoints = FXCollections.observableArrayList(this.points);
        newPoints.remove(point);
        copy.setGeometry(createGeometry(newPoints));
        entityService.updateEntity(copy, "geometry",
                () -> {
                    points.remove(point);
                    pointsListView.refresh();
                    configuration.getMapRenderer()
                                 .redraw();
                    showInfo("Entity updated", "Entity updated successfully");
                },
                () -> {
                    showError("Database error", "Could not update entity");
                });
    }

    public void addNewPoint(ActionEvent event) {
        Optional<Point> pointOptional = Point.of(xField.getText(), yField.getText());
        pointOptional.ifPresent(newPoint -> {
            Entity copy = selectedEntityService.getEntityProperty()
                                               .copyOf();
            ObservableList<Point> pointsCopy = FXCollections.observableArrayList(this.points);
            pointsCopy.add(newPoint);
            copy.setGeometry(createGeometry(pointsCopy));
            entityService.updateEntity(copy, "geometry",
                    () -> {
                        this.points.add(newPoint);
                        xField.setText("");
                        yField.setText("");
                        configuration.getMapRenderer()
                                     .redraw();
                        showInfo("Entity updated", "Entity updated successfully");
                    }, () -> {
                        showError("Database error", "Could not update entity");
                    });
        });
    }

    public abstract EntityGeometry createGeometry(ObservableList<Point> points);
}
