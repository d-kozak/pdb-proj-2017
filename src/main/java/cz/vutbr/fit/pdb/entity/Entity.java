package cz.vutbr.fit.pdb.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.sql.Date;
import java.util.List;

public class Entity {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Image> flag = new SimpleObjectProperty<>();
    private ObservableList<Image> images = FXCollections.observableArrayList();
    private ObjectProperty<Date> from = new SimpleObjectProperty<>();
    private ObjectProperty<Date> to = new SimpleObjectProperty<>();

    public Entity() {
    }

    public Entity(String name, String description, Image flag, List<Image> images) {
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
    }

    public Entity(String name, String description, Image flag, List<Image> images, Date from, Date to) {
        this.name.setValue(name);
        this.description.setValue(description);
        this.flag.setValue(flag);
        this.images = FXCollections.observableArrayList(images);
        this.from.setValue(from);
        this.to.setValue(to);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Image getFlag() {
        return flag.get();
    }

    public void setFlag(Image flag) {
        this.flag.set(flag);
    }

    public ObjectProperty<Image> flagProperty() {
        return flag;
    }

    public ObservableList<Image> getImages() {
        return images;
    }

    public void setImages(ObservableList<Image> images) {
        this.images = images;
    }

    public Date getFrom() {
        return from.get();
    }

    public void setFrom(Date from) {
        this.from.set(from);
    }

    public ObjectProperty<Date> fromProperty() {
        return from;
    }

    public Date getTo() {
        return to.get();
    }

    public void setTo(Date to) {
        this.to.set(to);
    }

    public ObjectProperty<Date> toProperty() {
        return to;
    }
}
