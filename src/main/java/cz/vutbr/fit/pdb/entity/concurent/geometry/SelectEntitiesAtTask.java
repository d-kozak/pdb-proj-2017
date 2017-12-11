package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.Spatial;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class SelectEntitiesAtTask extends Task<ObservableList<Integer>> {
    private Point point;

    @Override
    protected ObservableList<Integer> call() throws Exception {
        return Spatial.entitiesContainingPoint(point);
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
