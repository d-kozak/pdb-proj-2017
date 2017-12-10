package cz.vutbr.fit.pdb.entity;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.time.LocalDate;

public class EntityImage {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> time = new SimpleObjectProperty<>();
    private StringProperty url = new SimpleStringProperty();

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

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    @Override
    public String toString() {
        return "EntityImage{" +
                "id=" + id +
                ", description=" + description +
                ", image=" + image +
                ", time=" + time +
                ", url=" + url +
                '}';
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public StringProperty urlProperty() {
        return url;
    }
}
