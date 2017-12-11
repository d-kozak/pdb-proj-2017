package cz.vutbr.fit.pdb.entity.geometry;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PointDetails {
    private StringProperty nearestRiver = new SimpleStringProperty();

    public String getNearestRiver() {
        return nearestRiver.get();
    }

    public void setNearestRiver(String nearestRiver) {
        this.nearestRiver.set(nearestRiver);
    }

    public StringProperty nearestRiverProperty() {
        return nearestRiver;
    }
}
