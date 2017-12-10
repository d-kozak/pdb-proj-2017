package cz.vutbr.fit.pdb.entity.geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PolygonDetails {
    private DoubleProperty area = new SimpleDoubleProperty();
    private DoubleProperty circumference = new SimpleDoubleProperty();
    private ObservableList<String> entitiesInside = FXCollections.observableArrayList();

    public double getArea() {
        return area.get();
    }

    public void setArea(double area) {
        this.area.set(area);
    }

    public DoubleProperty areaProperty() {
        return area;
    }

    public double getCircumference() {
        return circumference.get();
    }

    public void setCircumference(double circumference) {
        this.circumference.set(circumference);
    }

    public DoubleProperty circumferenceProperty() {
        return circumference;
    }

    public ObservableList<String> getEntitiesInside() {
        return entitiesInside;
    }

    public void setEntitiesInside(ObservableList<String> entitiesInside) {
        this.entitiesInside = entitiesInside;
    }
}
