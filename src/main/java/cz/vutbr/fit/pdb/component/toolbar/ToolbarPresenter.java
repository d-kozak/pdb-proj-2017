package cz.vutbr.fit.pdb.component.toolbar;

import cz.vutbr.fit.pdb.configuration.AppMode;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DrawingMode;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.utils.StringEntityConverter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

public class ToolbarPresenter implements Initializable {

    @FXML
    private ComboBox<Entity> entitiesComboBox;

    @FXML
    private ComboBox<String> colors;

    @Inject
    private Configuration configuration;

    @Inject
    private EntityService entityService;

    @Inject
    private SelectedEntityService selectedEntityService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Entity> entities = entityService.getEntities();
        entitiesComboBox.setItems(entities);
        entitiesComboBox.setConverter(StringEntityConverter.INSTANCE);
        entitiesComboBox.getSelectionModel()
                        .selectedItemProperty()
                        .addListener((observable, oldValue, newValue) -> {
                            selectedEntityService.setEntityProperty(newValue);
                        });
        selectedEntityService.entityPropertyProperty()
                             .addListener((observable, oldValue, newValue) -> {
                                 if (newValue != null) {
                                     entitiesComboBox.getSelectionModel()
                                                     .select(newValue);
                                 }
                             });

        if (!entities.isEmpty()) {
            entitiesComboBox.getSelectionModel()
                            .select(0);
        }

        colors.setItems(Configuration.colors);
        colors.setCellFactory((list) -> new ColorRectCell());
        colors.setButtonCell(new ColorRectCell());
        colors.getSelectionModel()
              .selectedItemProperty()
              .addListener((observable, oldValue, newValue) -> {
                  configuration.setDrawingColor(Color.web(newValue));
              });
        colors.getSelectionModel()
              .select(0);
    }

    public void onPoint(ActionEvent event) {
        configuration.setDrawMode(DrawingMode.POINT);
    }

    public void onLine(ActionEvent event) {
        configuration.setDrawMode(DrawingMode.LINE);
    }

    public void onCircle(ActionEvent event) {
        configuration.setDrawMode(DrawingMode.CIRCLE);
    }

    public void onPolygon(ActionEvent event) {
        configuration.setDrawMode(DrawingMode.POLYGON);
    }

    public void onModeView(ActionEvent event) {
        configuration.setAppMode(AppMode.VIEW);
    }

    public void onModeEdit(ActionEvent event) {
        configuration.setAppMode(AppMode.EDIT);
    }

    public void onModeBlindMap(ActionEvent event) {
        configuration.setAppMode(AppMode.BLIND_MAP);
    }

    public void onFinishDrawing(ActionEvent event) {
        configuration.setDrawingFinished(true);
    }

    public void onDeleteSelected(ActionEvent event) {
        Entity selectedEntity = selectedEntityService.getEntityProperty();
        entityService.removeEntity(
                selectedEntity,
                () -> {
                    showInfo("Entity removed", "Entity removed successfully");
                    selectedEntityService.setEntityProperty(null);
                    configuration.getMapRenderer()
                                 .redraw();
                },
                () -> {
                    showError("Database error", "Could not remove entity, please try again");
                });
    }

    static class ColorRectCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            Circle circle = new Circle(0, 0, 7);
            if (item != null) {
                circle.setFill(Color.web(item));
                setGraphic(circle);
            }
        }
    }
}
