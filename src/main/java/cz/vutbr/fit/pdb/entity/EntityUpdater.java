package cz.vutbr.fit.pdb.entity;

import cz.vutbr.fit.pdb.entity.concurent.AddEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.RemoveEntityTask;
import cz.vutbr.fit.pdb.entity.concurent.UpdateEntityTask;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

import java.util.List;

import static cz.vutbr.fit.pdb.configuration.Configuration.THREAD_POOL;

@Log
public class EntityUpdater {
    private AddEntityTask addEntityTask = new AddEntityTask();
    private RemoveEntityTask removeEntityTask = new RemoveEntityTask();
    private UpdateEntityTask updateEntityTask = new UpdateEntityTask();
    private EntityService entityService;

    public EntityUpdater(EntityService entityService) {
        this.entityService = entityService;
    }


    public void addListeners(ObservableList<Entity> entities) {
        // add/remove entity listeners
        entities.addListener((ListChangeListener<Entity>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    List<? extends Entity> added = c.getAddedSubList();
                    added.forEach(EntityUpdater.this::addEntity);

                }
                if (c.wasRemoved()) {
                    List<? extends Entity> removed = c.getRemoved();
                    removed.forEach(EntityUpdater.this::removeEntity);
                }
            }
        });

        for (Entity entity : entities) {
            entity.nameProperty()
                  .addListener((observable, oldValue, newValue) -> {
                      updateEntity(entity, "name", oldValue, newValue);
                  });
            // and so on ....
        }
    }

    private void updateEntity(Entity entity, String changedField, Object oldValue, Object newValue) {
        log.info("Updating entity " + entity + ", field" + changedField);
        updateEntityTask.setEntity(entity);
        updateEntityTask.setField(changedField);

        updateEntityTask.setOnSucceeded(event -> {
            log.info("success");
        });
        updateEntityTask.setOnFailed(event -> {
            log.severe("failed, reverting the update");
            //ReflectionUtils.setField(entity, changedField, oldValue);
        });

        THREAD_POOL.submit(updateEntityTask);
    }

    private void addEntity(Entity entity) {
        log.info("Adding entity " + entity);
        addEntityTask.setEntity(entity);

        addEntityTask.setOnSucceeded(event -> {
            log.info("success");
        });
        addEntityTask.setOnFailed(event -> {
            log.severe("failed, removing new entity");
//            entityService.getEntities()
//                         .remove(entity);
        });
        THREAD_POOL.submit(addEntityTask);
    }

    private void removeEntity(Entity entity) {
        log.info("Removing entity" + entity);
        removeEntityTask.setEntity(entity);

        removeEntityTask.setOnSucceeded(event -> {
            log.info("success");
        });
        removeEntityTask.setOnFailed(event -> {
            log.severe("failed, adding entity back");
//            entityService.getEntities()
//                         .add(entity);
        });
        THREAD_POOL.submit(removeEntityTask);
    }
}
