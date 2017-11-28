package cz.vutbr.fit.pdb.service;

import cz.vutbr.fit.pdb.entity.Entity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import lombok.val;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class DummyData {

    private static List<Entity> entities = FXCollections.observableArrayList();

    static {
        entities.add(loadDummyEntity());
    }

    public static void addEntity(Entity entity) {
        entities.add(entity);
    }

    public static List<Entity> getEntities() {
        return entities;
    }

    private static Entity loadDummyEntity() {
        Entity entity = new Entity();
        entity.setName("Brno");
        entity.setDescription("The best town in the Czech Republic");
        try {
            val image = new Image(new FileInputStream("src/resources/brno.jpg"));
            entity.setFlag(image);

            ObservableList<Image> images = FXCollections.observableArrayList();
            for (int i = 0; i < 10; i++) {
                images.add(image);
            }
            entity.setImages(images);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return entity;
    }
}
