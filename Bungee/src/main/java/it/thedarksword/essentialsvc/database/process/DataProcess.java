package it.thedarksword.essentialsvc.database.process;

import it.thedarksword.essentialsvc.data.domain.GenericData;
import it.thedarksword.essentialsvc.database.controller.DataController;
import it.thedarksword.essentialsvc.database.process.task.DataProcessTask;

import java.util.concurrent.CompletableFuture;

public class DataProcess {

    public DataProcessTask task = new DataProcessTask(this);

    private final DataController controller;

    public DataProcess(DataController controller) {
        this.controller = controller;
    }

    public CompletableFuture<GenericData> save(GenericData data) {
        return CompletableFuture.supplyAsync(() -> {
            controller.saveData(data);
            return data;
        });
    }

    public CompletableFuture<GenericData> delete(GenericData data) {
        return CompletableFuture.supplyAsync(() -> {
            controller.deleteData(data);
            return data;
        });
    }

}
