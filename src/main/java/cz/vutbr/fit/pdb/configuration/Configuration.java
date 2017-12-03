package cz.vutbr.fit.pdb.configuration;

import cz.vutbr.fit.pdb.component.map.MapRenderer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Configuration {
    public static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(20);

    public static final ObservableList<String> colors = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList("chocolate", "salmon", "gold", "coral", "darkorchid",
            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
            "blueviolet", "brown"));


    private IntegerProperty year = new SimpleIntegerProperty();
    private ObjectProperty<DrawingMode> drawMode = new SimpleObjectProperty<>(DrawingMode.POINT);
    private ObjectProperty<AppMode> appMode = new SimpleObjectProperty<>(AppMode.EDIT);

    private ObjectProperty<Color> drawingColor = new SimpleObjectProperty<>(Color.color(0, 0, 0));

    private BooleanProperty drawingFinished = new SimpleBooleanProperty(false);

    private MapRenderer mapRenderer;

    public DrawingMode getDrawMode() {
        return drawMode.get();
    }

    public void setDrawMode(DrawingMode drawMode) {
        this.drawMode.set(drawMode);
    }

    public ObjectProperty<DrawingMode> drawModeProperty() {
        return drawMode;
    }

    public AppMode getAppMode() {
        return appMode.get();
    }

    public void setAppMode(AppMode appMode) {
        this.appMode.set(appMode);
    }

    public ObjectProperty<AppMode> appModeProperty() {
        return appMode;
    }

    public int getYear() {
        return year.get();
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public Color getDrawingColor() {
        return drawingColor.get();
    }

    public void setDrawingColor(Color drawingColor) {
        this.drawingColor.set(drawingColor);
    }

    public ObjectProperty<Color> drawingColorProperty() {
        return drawingColor;
    }

    public boolean isDrawingFinished() {
        return drawingFinished.get();
    }

    public void setDrawingFinished(boolean drawingFinished) {
        this.drawingFinished.set(drawingFinished);
    }

    public BooleanProperty drawingFinishedProperty() {
        return drawingFinished;
    }

    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public void setMapRenderer(MapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }
}
