package cz.vutbr.fit.pdb.component.toolbar;

import cz.vutbr.fit.pdb.configuration.AppMode;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.configuration.DrawingMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import javax.inject.Inject;

public class ToolbarPresenter {
    @FXML
    private HBox drawingModeButtons;

    @Inject
    private Configuration configuration;


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
}
