package cz.vutbr.fit.pdb.entity.concurent.geometry;

import cz.vutbr.fit.pdb.db.Spatial;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.utils.JavaFXUtils;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class SelectEntitiesAtTask extends Task<ObservableList<Integer>> {
    private Point point;
    private static final int TOLERANCE = 10;

    @Override
    protected ObservableList<Integer> call() throws Exception {
        JavaFXUtils.startWithTimeout(3000, this);
        return Spatial.entitiesContainingPoint(point, TOLERANCE);

    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
