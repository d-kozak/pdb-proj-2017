package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class SelectEntitiesAtTask extends Task<ObservableList<Integer>> {
    private Point point;

    @Override
    protected ObservableList<Integer> call() throws Exception {
        return FXCollections.observableArrayList();
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
