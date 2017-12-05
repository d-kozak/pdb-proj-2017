package cz.vutbr.fit.pdb.utils;

import cz.vutbr.fit.pdb.configuration.Configuration;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public class MathUtils {

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static void addNewPointFromFields(Point p, TextField xField, TextField yField, ObservableList<Point> points, Configuration configuration) {
        xField.setText("");
        yField.setText("");
        points.add(p);
        configuration.getMapRenderer()
                     .redraw();
        Listeners.addRedrawListener(configuration.getMapRenderer(), p.xProperty(), p.yProperty());
    }
}
