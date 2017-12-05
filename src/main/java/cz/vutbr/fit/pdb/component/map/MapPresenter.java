package cz.vutbr.fit.pdb.component.map;

import cz.vutbr.fit.pdb.configuration.AppMode;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.painter.Painter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class MapPresenter implements Initializable, MapRenderer {

    @FXML
    private VBox vbox;

    @FXML
    private Canvas canvas;

    @Inject
    private EntityService entityService;

    @Inject
    private Configuration configuration;

    private Painter painter;

    @Override
    public void redraw() {
        canvas.getGraphicsContext2D()
              .clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        painter.paintAll(entityService.getEntities());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.canvas = new ResizableCanvas();
        vbox.getChildren()
            .add(canvas);
        canvas.widthProperty()
              .bind(vbox.widthProperty());
        canvas.heightProperty()
              .bind(vbox.heightProperty());

        this.painter = new Painter(canvas.getGraphicsContext2D(), entityService, configuration);
        this.canvas.setOnMouseClicked(this::onMouseClicked);

        this.entityService.initDataLoadedProperty()
                          .addListener((observable, oldValue, newValue) -> {
                              if (newValue) {
                                  this.painter.paintAll(entityService.getEntities());
                              }
                          });

        configuration.setMapRenderer(this);

        configuration.drawingFinishedProperty()
                     .addListener((observable, oldValue, drawingFinished) -> {
                         if (drawingFinished) {
                             painter.drawingFinished();
                             configuration.setDrawingFinished(false);
                         }
                     });

        configuration.canvasWidthProperty()
                     .bindBidirectional(canvas.widthProperty());
        configuration.canvasHeightProperty()
                     .bindBidirectional(canvas.heightProperty());

        configuration.setCanvas(canvas);
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        log.info(String.format("Clicked on canvas at[%f,%f]", x, y));
        if (configuration.getAppMode() == AppMode.VIEW)
            entityService.tryToSelectEntityAt(x, y);
        else if (configuration.getAppMode() == AppMode.EDIT)
            painter.clicked(x, y);
        else throw new RuntimeException("Not impl yet");
    }
}
