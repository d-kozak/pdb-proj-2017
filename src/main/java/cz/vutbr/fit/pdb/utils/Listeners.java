package cz.vutbr.fit.pdb.utils;

import cz.vutbr.fit.pdb.component.map.MapRenderer;
import cz.vutbr.fit.pdb.entity.geometry.Point;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;

public class Listeners {
    public static <T> void addRedrawListener(MapRenderer mapRenderer, Property<T>... properties) {
        for (Property<T> property : properties)
            property.addListener((observable, oldValue, newValue) -> {
                mapRenderer.redraw();
            });
    }

    public static void addRedrawListener(MapRenderer mapRenderer, ObservableList<Point> points) {
        points.forEach(point -> {
            point.xProperty()
                 .addListener((observable, oldValue, newValue) -> {
                     mapRenderer.redraw();
                 });
            point.yProperty()
                 .addListener((observable, oldValue, newValue) -> {
                     mapRenderer.redraw();
                 });
        });
    }
}
