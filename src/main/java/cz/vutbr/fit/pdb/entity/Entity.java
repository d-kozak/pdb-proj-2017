package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;

public class Entity {
    public static final Entity NULL = new Entity(0, "", "", null, FXCollections.observableArrayList(), null);
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private ObjectProperty<EntityImage> flag = new SimpleObjectProperty<>();
    private ObservableList<EntityImage> images = FXCollections.observableArrayList();
    private ObjectProperty<LocalDate> from = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> to = new SimpleObjectProperty<>();

    private EntityGeometry geometry;
    private ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public Entity() {
    }

    public Entity(Integer id, String name, String description, EntityImage flag, List<EntityImage> images, EntityGeometry geometry) {
        this.id.setValue(id);
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
        this.geometry = geometry;
    }

    public Entity(Integer id, String name, String description, EntityImage flag, List<EntityImage> images, EntityGeometry geometry, LocalDate from, LocalDate to) {
        this.id.setValue(id);
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
        this.geometry = geometry;
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
        return geometry;
    }

    public void setGeometry(EntityGeometry geometry) {
        this.geometry = geometry;
    }

    public DrawingMode getGeometryType() {
        return geometry.getType();
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
}
