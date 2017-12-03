package cz.vutbr.fit.pdb.component.toolbar;

import cz.vutbr.fit.pdb.configuration.AppMode;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DrawingMode;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.SelectedEntityService;
import cz.vutbr.fit.pdb.utils.StringEntityConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarPresenter implements Initializable {

    @FXML
    private HBox drawingModeButtons;

    @FXML
    private ChoiceBox<Entity> entitiesChoiceBox;

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
        entitiesChoiceBox.setItems(entityService.getEntities());
        entitiesChoiceBox.setConverter(StringEntityConverter.INSTANCE);
        entitiesChoiceBox.getSelectionModel()
                         .selectedItemProperty()
                         .addListener((observable, oldValue, newValue) -> {
                             selectedEntityService.setEntityProperty(newValue);
                         });
        colors.setItems(Configuration.colors);
        colors.setCellFactory((list) -> new ColorRectCell());
        colors.getSelectionModel()
              .selectedItemProperty()
              .addListener((observable, oldValue, newValue) -> {
                  configuration.setDrawingColor(Color.web(newValue));
              });
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
        disableDrawingButtons();
    }

    public void onModeEdit(ActionEvent event) {
        configuration.setAppMode(AppMode.EDIT);
        enableDrawingButtons();
    }

    public void onModeBlindMap(ActionEvent event) {
        configuration.setAppMode(AppMode.BLIND_MAP);
        disableDrawingButtons();
    }

    private void enableDrawingButtons() {
        drawingModeButtons.setDisable(false);
    }

    private void disableDrawingButtons() {
        drawingModeButtons.setDisable(true);
    }

    public void onFinishDrawing(ActionEvent event) {
        configuration.setDrawingFinished(true);
    }

    public void onDeleteSelected(ActionEvent event) {
        Entity selectedEntity = selectedEntityService.getEntityProperty();
        entityService.removeEntity(selectedEntity);
        selectedEntityService.setEntityProperty(null);
        configuration.getMapRenderer()
                     .redraw();
    }

    static class ColorRectCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            Rectangle rect = new Rectangle(100, 20);
            if (item != null) {
                rect.setFill(Color.web(item));
                setGraphic(rect);
            }
        }
    }
}
