package org.mai.dep806.volkoval.lab;

import org.mai.dep806.volkoval.data.DataHandler;
import org.mai.dep806.volkoval.data.DataRetriever;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.CommonStatistic;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 11.06.13
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLab {

    protected DataRetriever dataRetriever;

    protected CommonStatistic statistic;

    public void setDataRetriever(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
        this.statistic     = new CommonStatistic();
    }

    public abstract String getLabName();

    public abstract void produce(int top) throws UnsupposedArgumentException, UnsupposedTypeException;

    public abstract void init();

    public abstract void flush() throws UnsupposedArgumentException, UnsupposedTypeException;

    public void setStatistic(CommonStatistic statistic) {
        this.statistic = statistic;
    }

    protected abstract class LabDataHandler implements DataHandler {
    }
}
