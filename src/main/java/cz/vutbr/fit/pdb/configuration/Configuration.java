package cz.vutbr.fit.pdb.configuration;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Configuration {
    private IntegerProperty year = new SimpleIntegerProperty();
    private ObjectProperty<DrawingMode> drawMode = new SimpleObjectProperty<>(DrawingMode.POINT);
    private ObjectProperty<AppMode> appMode = new SimpleObjectProperty<>(AppMode.EDIT);

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
}
