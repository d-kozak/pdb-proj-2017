package cz.vutbr.fit.pdb.component.rightbar.geometry.circleinfo;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.CircleGeometry;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.Listeners;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

public class CircleInfoPresenter implements Initializable {
    @FXML
    private TextField centerXField;
    @FXML
    private TextField centerYField;
    @FXML
    private TextField radiusField;

    @Inject
    private Configuration configuration;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private EntityService entityService;

    private Point backUpCenter;
    private SimpleDoubleProperty backUpRadius;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedEntityService.getEntityProperty()
                             .geometryProperty()
                             .addListener((observable, oldValue, newValue) -> {
                                 initForCircle(newValue);
                             });
        initForCircle(selectedEntityService.getEntityProperty()
                                           .getGeometry());


        Listeners.addRedrawListener(configuration.getMapRenderer(), centerXField.textProperty(), centerYField.textProperty(), radiusField.textProperty());
    }

    private void initForCircle(EntityGeometry entityGeometry) {
        Object[] description = (Object[]) entityGeometry.getDescription();
        Point center = ((Point) description[0]);
        DoubleProperty radius = (DoubleProperty) description[1];

        if (backUpCenter != null) {
            centerXField.textProperty()
                        .unbindBidirectional(backUpCenter.xProperty());
            centerYField.textProperty()
                        .unbindBidirectional(backUpCenter.yProperty());
        }

        if (backUpRadius != null) {
            radiusField.textProperty()
                       .unbindBidirectional(backUpRadius);
        }

        backUpCenter = new Point(center.getX(), center.getY());
        backUpRadius = new SimpleDoubleProperty(radius.get());

        centerXField.textProperty()
                    .bindBidirectional(backUpCenter.xProperty(), new StringNumConverter());
        centerYField.textProperty()
                    .bindBidirectional(backUpCenter.yProperty(), new StringNumConverter());
        radiusField.textProperty()
                   .bindBidirectional(backUpRadius, new StringNumConverter());

        centerXField.setOnAction(event -> {
            Entity copy = selectedEntityService.getEntityProperty()
                                               .copyOf();
            copy.setGeometry(new CircleGeometry(backUpCenter.getX(), center.getY(), radius.get()));
            entityService.updateEntity(copy, "geometry",
                    () -> {
                        center.setX(backUpCenter.getX());
                        configuration.getMapRenderer()
                                     .redraw();
                        showInfo("Entity updated", "Entity updated successfully");
                    },
                    () -> {
                        centerXField.setText("" + center.getX());
                        configuration.getMapRenderer()
                                     .redraw();
                        showError("Database error", "Could not update entity");
                    });
        });

        centerYField.setOnAction(event -> {
            Entity copy = selectedEntityService.getEntityProperty()
                                               .copyOf();
            copy.setGeometry(new CircleGeometry(center.getX(), backUpCenter.getY(), radius.get()));
            entityService.updateEntity(copy, "geometry",
                    () -> {
                        center.setY(backUpCenter.getY());
                        configuration.getMapRenderer()
                                     .redraw();
                        showInfo("Entity updated", "Entity updated successfully");
                    },
                    () -> {
                        centerYField.setText("" + center.getY());
                        configuration.getMapRenderer()
                                     .redraw();
                        showError("Database error", "Could not update entity");
                    });
        });


        radiusField.setOnAction(event -> {
            Entity copy = selectedEntityService.getEntityProperty()
                                               .copyOf();
            copy.setGeometry(new CircleGeometry(center.getX(), center.getY(), backUpRadius.get()));
            entityService.updateEntity(copy, "geometry",
                    () -> {
                        radius.set(backUpRadius.get());
                        configuration.getMapRenderer()
                                     .redraw();
                        showInfo("Entity updated", "Entity updated successfully");
                    },
                    () -> {
                        radiusField.setText("" + radius.get());
                        configuration.getMapRenderer()
                                     .redraw();
                        showError("Database error", "Could not update entity");
                    });
        });
    }
}
