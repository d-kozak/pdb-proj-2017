package cz.vutbr.fit.pdb.component.rightbar.geometry.polygoninfo;

import cz.vutbr.fit.pdb.component.rightbar.geometry.AbstractPointListBasedInfoPresenter;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import cz.vutbr.fit.pdb.entity.geometry.PolygonGeometry;
import javafx.collections.ObservableList;

public class PolygonInfoPresenter extends AbstractPointListBasedInfoPresenter {
    @Override
    public EntityGeometry createGeometry(ObservableList<Point> points) {
        return new PolygonGeometry(points);
    }
}
