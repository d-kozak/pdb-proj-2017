package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.configuration.DrawingMode;
import cz.vutbr.fit.pdb.entity.geometry.EntityGeometry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.time.LocalDate;
import java.util.List;

public class Entity {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Image> flag = new SimpleObjectProperty<>();
    private ObservableList<Image> images = FXCollections.observableArrayList();
    private ObjectProperty<LocalDate> from = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> to = new SimpleObjectProperty<>();

    private EntityGeometry geometry;

    public Entity() {
    }

    public Entity(String name, String description, Image flag, List<Image> images, EntityGeometry geometry) {
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
        this.geometry = geometry;
    }

    public Entity(String name, String description, Image flag, List<Image> images, EntityGeometry geometry, LocalDate from, LocalDate to) {
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
        this.geometry = geometry;
        this.from.setValue(from);
        this.to.setValue(to);
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

    public Image getFlag() {
        return flag.get();
    }

    public ObjectProperty<Image> flagProperty() {
        return flag;
    }

    public void setFlag(Image flag) {
        this.flag.set(flag);
    }

    public ObservableList<Image> getImages() {
        return images;
    }

    public void setImages(ObservableList<Image> images) {
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
}
