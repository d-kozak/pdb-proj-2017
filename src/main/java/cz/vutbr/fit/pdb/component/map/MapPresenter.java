package cz.vutbr.fit.pdb.component.map;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import cz.vutbr.fit.pdb.configuration.AppMode;
import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.Entity;
import cz.vutbr.fit.pdb.entity.EntityService;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.LineGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.painter.Painter;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import com.google.gson.Gson;

@Log
public class MapPresenter implements Initializable, MapRenderer {

    @FXML
    private VBox vbox;

    @FXML
    private WebView webview;
    private JSObject leaflet;
    private VMBridge vmbridge;
    private Gson gson;

    @FXML
    private Canvas canvas;

    @Inject
    private EntityService entityService;

    @Inject
    private Configuration configuration;

    private boolean missedRedraw = false;
    private Painter painter;

    @Override
    public void redraw() {
        /*canvas.getGraphicsContext2D()
              .clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        painter.paintAll(entityService.getEntities(configuration.getYear()));*/
        if(leaflet == null) {
            // TEMP:
            missedRedraw = true;
            return;
        }
        log.severe("Redraw!");
        // TEMP:
        //ObservableList<Entity> entities = entityService.getEntities(configuration.getYear());
        leaflet.call("clearAll");
        ObservableList<Entity> entities = entityService.getEntities();
        for (Entity entity : entities) {
            EntityGeometry geometry = entity.getGeometry();
            leaflet.call("draw", entity);
            log.severe(gson.toJson(geometry));
           /* switch (entity.getGeometryType()) {
                case POINT:
                    Point desc = (Point) geometry.getDescription();
                    leaflet.call("draw", geometry);
                    break;
                case LINE:
                    //Point[] array = ((ObservableList<Point>) geometry.getDescription()).toArray(new Point[0]);
                    //log.severe(gson.toJson((LineGeometry) geometry));
                    leaflet.call("draw", geometry);
                default:
            }*/
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine webEngine = webview.getEngine();

        // set up the listener
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                // set an interface object named 'javaConnector' in the web engine's page
                leaflet = (JSObject) webEngine.executeScript("new VMBridge");

                if(leaflet.getMember("_bridgeUp").toString().equals("VeriBridge")) {
                    log.severe("Bridge Java VM -> JS VM is up.");
                } else {
                    log.warning("Bridge is not up!");
                    return;
                }

                leaflet.setMember("vm", new VMBridge());

                // get the Javascript connector object.
                leaflet.call("running");

                // TEMP:
                redraw();
            }
        });

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Point.class, new PointAdapter());
        gson = builder.create();

        webEngine.load(this.getClass().getClassLoader().getResource("leaflet.html").toExternalForm());

        /*
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
                                  this.painter.paintAll(entityService.getEntities(configuration.getYear()));
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

        configuration.yearProperty()
                     .addListener((observable, oldValue, newValue) -> {
                         redraw();
                     });
                     */
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        log.info(String.format("Clicked on canvas at[%f,%f]", x, y));
        if (configuration.getAppMode() == AppMode.VIEW)
            entityService.tryToSelectEntityAt(x, y);
        /*else if (configuration.getAppMode() == AppMode.EDIT)
            painter.clicked(x, y);
        else throw new RuntimeException("Not impl yet");*/
    }

    public class VMBridge {
        final public String _bridgeUp = "VeriBridge";
        /**
         * called when the JS side wants a String to be converted.
         *
         * @param value
         *         the String to convert
         */
        public void log(String msg) {
            log.severe(msg);
        }

        public void clickEvent(double x, double y) { // [x, y]
            log.info("You clicked the map at " + x + " " + y);
            entityService.tryToSelectEntityAt(x, y);
        }
    }

    public class PointAdapter extends TypeAdapter<Point> {
        // TEMP:
        public Point read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String xy = reader.nextString();
            String[] parts = xy.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return new Point(x, y);
        }

        public void write(JsonWriter writer, Point value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.beginArray();
            writer.value(value.getX());
            writer.value(value.getY());
            writer.endArray();
        }
    }
}
