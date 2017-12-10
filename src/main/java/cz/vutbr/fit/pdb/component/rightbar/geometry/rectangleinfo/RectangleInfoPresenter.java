package cz.vutbr.fit.pdb.component.rightbar.geometry.rectangleinfo;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.RectangleGeometry;
import cz.vutbr.fit.pdb.utils.StringNumConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.extern.java.Log;
import lombok.val;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

@Log
public class RectangleInfoPresenter implements Initializable {

    @FXML
    private TextField leftUpXField;
    @FXML
    private TextField leftUpYField;
    @FXML
    private TextField rightDownXField;

    @FXML
    private TextField rightDownYField;

    private Point leftUpBinded;

    private Point rightDownBinded;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Inject
    private EntityService entityService;

    @Inject
    private Configuration configuration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Point> description = (ObservableList<Point>) selectedEntityService.getEntityProperty()
                                                                                         .getGeometry()
                                                                                         .getDescription();
        selectedEntityService.getEntityProperty()
                             .geometryProperty()
                             .addListener((observable, oldValue, newValue) -> {
                                 val newDescription = (ObservableList<Point>) newValue.getDescription();
                                 initForRectangle(newDescription.get(0), newDescription.get(1));

                             });

        initForRectangle(description.get(0), description.get(1));
    }

    private void initForRectangle(Point leftUp, Point rightDown) {
        unbindFields();
        this.leftUpBinded = leftUp.copyOf();
        this.rightDownBinded = rightDown.copyOf();

        leftUpXField.textProperty()
                    .bindBidirectional(leftUpBinded.xProperty(), new StringNumConverter());
        leftUpYField.textProperty()
                    .bindBidirectional(leftUpBinded.yProperty(), new StringNumConverter());
        rightDownXField.textProperty()
                       .bindBidirectional(rightDownBinded.xProperty(), new StringNumConverter());
        rightDownYField.textProperty()
                       .bindBidirectional(rightDownBinded.yProperty(), new StringNumConverter());

        EventHandler<ActionEvent> updateRectangleListener = (event) -> {
            Entity copy = selectedEntityService.getEntityProperty()
                                               .copyOf();
            copy.setGeometry(new RectangleGeometry(FXCollections.observableArrayList(leftUpBinded.copyOf(), rightDownBinded.copyOf())));
            entityService.updateEntity(copy, "geometry",
                    () -> {
                        leftUp.setX(leftUpBinded.getX());
                        leftUp.setY(leftUpBinded.getY());
                        rightDown.setX(rightDownBinded.getX());
                        rightDown.setY(rightDownBinded.getY());
                        configuration.getMapRenderer()
                                     .redraw();
                        showInfo("Entity updated", "Entity updated successfully");
                    },
                    () -> {
                        leftUpBinded.setX(leftUp.getX());
                        leftUpBinded.setY(leftUp.getY());
                        rightDownBinded.setX(rightDown.getX());
                        rightDownBinded.setY(rightDown.getY());
                        showError("Database error", "Could not update entity");
                    });
        };
        leftUpXField.setOnAction(updateRectangleListener);
        leftUpYField.setOnAction(updateRectangleListener);
        rightDownXField.setOnAction(updateRectangleListener);
        rightDownYField.setOnAction(updateRectangleListener);
    }

    private void unbindFields() {
        if (leftUpBinded != null) {
            leftUpXField.textProperty()
                        .unbindBidirectional(leftUpBinded.xProperty());
            leftUpYField.textProperty()
                        .unbindBidirectional(leftUpBinded.yProperty());
        }
        if (rightDownBinded != null) {
            rightDownXField.textProperty()
                           .unbindBidirectional(rightDownBinded.xProperty());
            rightDownYField.textProperty()
                           .unbindBidirectional(rightDownBinded.yProperty());
        }
    }
}
