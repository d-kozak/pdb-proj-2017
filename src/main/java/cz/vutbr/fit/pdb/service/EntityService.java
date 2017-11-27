package cz.vutbr.fit.pdb.service;

import cz.vutbr.fit.pdb.entity.Entity;

import java.util.List;

public class EntityService {

    public static void addEntity(Entity entity) {
        DummyData.addEntity(entity);
    }

    public List<Entity> getEntities() {
        return DummyData.getEntities();
    }
}
