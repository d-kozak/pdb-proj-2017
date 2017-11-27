package cz.vutbr.fit.pdb.service;

import cz.vutbr.fit.pdb.entity.Entity;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;

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
            entity.setFlag(new Image(new FileInputStream("src/resources/brno.jpg")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return entity;
    }
}
