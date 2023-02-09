package it.thedarksword.essentialsvc.database.process.task;

import it.thedarksword.essentialsvc.data.domain.GenericData;
import it.thedarksword.essentialsvc.database.process.DataProcess;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataProcessTask implements Runnable {

    public BlockingQueue<Map.Entry<GenericData, Boolean>> queue = new LinkedBlockingQueue<>();
    public Boolean running = false;

    private final DataProcess dataProcess;

    public DataProcessTask(DataProcess dataProcess) {
        this.dataProcess = dataProcess;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Map.Entry<GenericData, Boolean> current = queue.take();
                if (current.getValue()) dataProcess.save(current.getKey()).join();
                else dataProcess.delete(current.getKey()).join();

                if (queue.isEmpty()) running = false;
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void startTask(DataProcessTask task) {
        if (task.running) return;

        Thread thread = new Thread(task);
        task.running = true;

        thread.start();
    }

    public static void stopTask(DataProcessTask task) {
        task.running = false;
    }

}