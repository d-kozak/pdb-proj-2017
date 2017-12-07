package cz.vutbr.fit.pdb.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.time.LocalDate;

public class EntityImage {
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> time = new SimpleObjectProperty<>();

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Image getImage() {
        return image.get();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public LocalDate getTime() {
        return time.get();
    }

    public void setTime(LocalDate time) {
        this.time.set(time);
    }

    public ObjectProperty<LocalDate> timeProperty() {
        return time;
    }

    @Override
    public String toString() {
        return "EntityImage{" +
                "description=" + description +
                ", image=" + image +
                ", time=" + time +
                '}';
    }
}
