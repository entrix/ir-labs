package org.mai.dep806.volkoval.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 11.06.13
 * Time: 23:28
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataRetriever {

    private static Logger logger = LogManager.getLogger(DataRetriever.class);

    protected List<DataHandler> handlers = new CopyOnWriteArrayList<>();

    protected ExecutorService executorService = null;

    protected boolean isInitExecutor = false;


    public void addDataHandler(DataHandler handler) {
        if (!isInitExecutor) {
            this.handlers.add(handler);
        }
    }

    public void initExecutor() {
        if (!isInitExecutor) {
            // initialize only one time per thread
            executorService = Executors.newFixedThreadPool(handlers.size());
            // and block following handler addition
            isInitExecutor = true;
        }
    }

    public void proceedTasks(List<RunnableTask> tasks) {
        List<Future<RunnableTask>> futures = new ArrayList<>(tasks.size());

        for (RunnableTask task : tasks) {
            futures.add(executorService.submit(task));
        }

        try {
//            while (!executorService.isTerminated()) {
//            executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
//
            for (Future<RunnableTask> task : futures) {
                task.cancel(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    protected abstract class RunnableTask implements Callable {

        DataHandler handler;

        public DataHandler getHandler() {
            return handler;
        }

        protected RunnableTask(int i) {
            handler = handlers.get(i);
        }
    }
}
