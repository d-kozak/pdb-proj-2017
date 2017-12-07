package cz.vutbr.fit.pdb.configuration;

import cz.vutbr.fit.pdb.component.map.MapRenderer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import lombok.extern.java.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
public class Configuration {
    public static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(20);

    public static final ObservableList<String> colors = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(
            "chocolate", "salmon", "gold", "coral", "darkorchid",
            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
            "blueviolet", "brown"));


    private ObjectProperty<DBConfiguration> dbConfiguration = new SimpleObjectProperty<>(new DBConfiguration());

    private IntegerProperty year = new SimpleIntegerProperty(2000);
    private ObjectProperty<DrawingMode> drawMode = new SimpleObjectProperty<>(DrawingMode.POINT);
    private ObjectProperty<AppMode> appMode = new SimpleObjectProperty<>(AppMode.VIEW);

    private ObjectProperty<Color> drawingColor = new SimpleObjectProperty<>(Color.color(0, 0, 0));

    private BooleanProperty drawingFinished = new SimpleBooleanProperty(false);

    private IntegerProperty canvasWidth = new SimpleIntegerProperty();
    private IntegerProperty canvasHeight = new SimpleIntegerProperty();

    private IntegerProperty mininumYear = new SimpleIntegerProperty(-1000);
    private IntegerProperty maximumYear = new SimpleIntegerProperty(3000);

    private ObjectProperty<Canvas> canvas = new SimpleObjectProperty<>();

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

    public DBConfiguration getDbConfiguration() {
        return dbConfiguration.get();
    }

    public void setDbConfiguration(DBConfiguration dbConfiguration) {
        this.dbConfiguration.set(dbConfiguration);
    }

    public ObjectProperty<DBConfiguration> dbConfigurationProperty() {
        return dbConfiguration;
    }

    public int getCanvasWidth() {
        return canvasWidth.get();
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth.set(canvasWidth);
    }

    public IntegerProperty canvasWidthProperty() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight.get();
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight.set(canvasHeight);
    }

    public IntegerProperty canvasHeightProperty() {
        return canvasHeight;
    }

    public Canvas getCanvas() {
        return canvas.get();
    }

    public void setCanvas(Canvas canvas) {
        this.canvas.set(canvas);
    }

    public ObjectProperty<Canvas> canvasProperty() {
        return canvas;
    }

    public int getMininumYear() {
        return mininumYear.get();
    }

    public void setMininumYear(int mininumYear) {
        this.mininumYear.set(mininumYear);
    }

    public IntegerProperty mininumYearProperty() {
        return mininumYear;
    }

    public int getMaximumYear() {
        return maximumYear.get();
    }

    public void setMaximumYear(int maximumYear) {
        this.maximumYear.set(maximumYear);
    }

    public IntegerProperty maximumYearProperty() {
        return maximumYear;
    }
}
