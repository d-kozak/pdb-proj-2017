package cz.vutbr.fit.pdb.component.rightbar.geometry.lineinfo;

import cz.vutbr.fit.pdb.component.rightbar.geometry.AbstractPointListBasedInfoPresenter;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import cz.vutbr.fit.pdb.entity.geometry.LineGeometry;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.collections.ObservableList;

public class LineInfoPresenter extends AbstractPointListBasedInfoPresenter {

    @Override
    public EntityGeometry createGeometry(ObservableList<Point> points) {
        return new LineGeometry(points);
    }
}
