package org.mai.dep806.volkoval.lab;

import org.mai.dep806.volkoval.data.DataHandler;
import org.mai.dep806.volkoval.data.DataRetriever;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 11.06.13
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLab {

    DataRetriever dataRetriever;

    public void setDataRetriever(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }

    public abstract String getLabName();

    public abstract void produce(int top) throws UnsupposedArgumentException;

    public abstract void init();

    public abstract void flush() throws UnsupposedArgumentException, UnsupposedTypeException;

    protected abstract class LabDataHandler implements DataHandler {
    }
}
