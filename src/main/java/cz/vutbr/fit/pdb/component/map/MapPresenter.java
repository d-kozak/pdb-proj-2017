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
import java.util.logging.Level;

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

    private Painter painter;

    @Override
    public void redraw() {

        if(leaflet == null) {
            return;
        }
        log.info("Redraw!");

        leaflet.call("clearAll");
        ObservableList<Entity> entities = entityService.getEntities(configuration.getYear());
        for (Entity entity : entities) {
            EntityGeometry geometry = entity.getGeometry();
            leaflet.call("draw", entity);
            log.info(gson.toJson(geometry));
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
                    log.info("Bridge Java VM -> JS VM is up.");
                } else {
                    log.severe("Bridge is not up!");
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

        configuration.setMapRenderer(this);
        configuration.yearProperty()
                .addListener((observable, oldValue, newValue) -> {
                    redraw();
                });
        this.entityService.initDataLoadedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        redraw();
                    }
                });

        /*
        this.canvas.setOnMouseClicked(this::onMouseClicked);

        configuration.drawingFinishedProperty()
                     .addListener((observable, oldValue, drawingFinished) -> {
                         if (drawingFinished) {
                             painter.drawingFinished();
                             configuration.setDrawingFinished(false);
                         }
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
        final WebViewLevel lvl = new WebViewLevel("WEBVIEW", 850);
        /**
         * called when the JS side wants a String to be converted.
         *
         * @param value
         *         the String to convert
         */
        public void log(String msg) {
            log.log(lvl, msg);
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

    class WebViewLevel extends Level {
        private WebViewLevel(String name, int level) {
            super(name, level);
        }
    }
}
