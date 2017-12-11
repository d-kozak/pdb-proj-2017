package cz.vutbr.fit.pdb.entity;

import com.google.gson.Gson;
import cz.vutbr.fit.pdb.configuration.DrawingMode;
import cz.vutbr.fit.pdb.entity.geometry.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.extern.java.Log;
import netscape.javascript.JSObject;

import java.time.LocalDate;
import java.util.List;

import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showError;
import static cz.vutbr.fit.pdb.utils.JavaFXUtils.showInfo;

@Log
public class Entity {
    public static final Entity NULL = new Entity(0, "", "", null, FXCollections.observableArrayList(), null);
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<EntityImage> flag = new SimpleObjectProperty<>();
    private ObservableList<EntityImage> images = FXCollections.observableArrayList();
    private ObjectProperty<LocalDate> from = new SimpleObjectProperty<>(LocalDate.now());
    private ObjectProperty<LocalDate> to = new SimpleObjectProperty<>(LocalDate.now());

    private ObjectProperty<EntityGeometry> geometry = new SimpleObjectProperty<>();
    private ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public Entity() {
    }

    public Entity(Integer id, String name, String description, EntityImage flag, List<EntityImage> images, EntityGeometry geometry) {
        this.id.setValue(id);
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
        this.geometry = new SimpleObjectProperty<>(geometry);
    }

    public Entity(Integer id, String name, String description, EntityImage flag, List<EntityImage> images, EntityGeometry geometry, LocalDate from, LocalDate to) {
        this.id.setValue(id);
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
        this.geometry = new SimpleObjectProperty<>(geometry);
        this.from.setValue(from);
        this.to.setValue(to);
    }

    public Integer getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public EntityImage getFlag() {
        return flag.get();
    }

    public void setFlag(EntityImage flag) {
        this.flag.set(flag);
    }

    public ObjectProperty<EntityImage> flagProperty() {
        return flag;
    }

    public ObservableList<EntityImage> getImages() {
        return images;
    }

    public void setImages(ObservableList<EntityImage> images) {
        this.images = images;
    }

    public LocalDate getFrom() {
        return from.get();
    }

    public void setFrom(LocalDate from) {
        this.from.set(from);
    }

    public ObjectProperty<LocalDate> fromProperty() {
        return from;
    }

    public LocalDate getTo() {
        return to.get();
    }

    public void setTo(LocalDate to) {
        this.to.set(to);
    }

    public ObjectProperty<LocalDate> toProperty() {
        return to;
    }

    public EntityGeometry getGeometry() {
        return geometry.get();
    }

    public void setGeometry(EntityGeometry geometry) {
        this.geometry.set(geometry);
    }

    public ObjectProperty<EntityGeometry> geometryProperty() {
        return geometry;
    }

    public DrawingMode getGeometryType() {
        return geometry.get()
                       .getType();
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", flag=" + flag +
                ", images=" + images +
                ", from=" + from +
                ", to=" + to +
                ", geometry=" + geometry +
                ", color=" + color +
                '}';
    }

    public boolean existsInYear(int selectedYear) {
        LocalDate from = this.from.getValue();
        LocalDate to = this.to.getValue();
        if (from == null || to == null)
            return true;
        return from.getYear() <= selectedYear && selectedYear <= to.getYear();
    }

    public Entity copyOf() {
        Entity copy = new Entity();
        copy.setId(getId());
        copy.setName(getName());
        copy.setDescription(getDescription());
        copy.setColor(getColor());
        copy.setFlag(getFlag());
        copy.setFrom(getFrom());
        copy.setTo(getTo());
        copy.setGeometry(getGeometry().copyOf());
        copy.setImages(FXCollections.observableArrayList(getImages()));
        return copy;
    }

    public EntityService entityService;

    public void select() {
        entityService.selectedEntityService.setEntityProperty(this);
        log.info("Selection of entity from map.");
    }

    private void updateGeometry() {
        Entity copy = this.copyOf();
        log.info("Updating geometry...");
        entityService.updateEntity(copy, "geometry", (newEntity) -> {
            entityService.getEntities()
                         .remove(copy);
            entityService.getEntities()
                         .add(newEntity);
            entityService.getConfiguration()
                         .getMapRenderer()
                         .redraw();
            showInfo("Entity updated", "Entity updated successfully");
        }, () -> {
            showError("Database error", "Could not update entity");
        });
    }

    public void updatePointGeometry(double x, double y) {
        setGeometry(new PointGeometry(new Point(x, y)));
        updateGeometry();
    }

    public void updateCircleGeometry(double x, double y, double r) {
        setGeometry(new CircleGeometry(x, y, r));
        updateGeometry();
    }

    public void updateStringGeometry(String _coords) {
        Gson gson = new Gson();
        ObservableList<Point> points = FXCollections.observableArrayList();
        double coords[] = gson.fromJson(_coords, double[].class);

        for (int i = 0; i < coords.length; i += 2) {
            points.add(new Point(coords[i], coords[i + 1]));
        }
        if (geometry.get() instanceof RectangleGeometry)
            setGeometry(new RectangleGeometry(points));
        else if (geometry.get() instanceof PolygonGeometry)
            setGeometry(new PolygonGeometry(points));
        else//(geometry instanceof PolygonGeometry)
            setGeometry(new LineGeometry(points));
        updateGeometry();
    }

    private JSObject layer; // LeafLet layer

    public void setLayer(JSObject layer) {
        this.layer = layer;
    }

    public void highlight() {
        layer.call("highlight");
    }
}
