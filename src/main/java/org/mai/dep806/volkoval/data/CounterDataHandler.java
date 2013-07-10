package org.mai.dep806.volkoval.data;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 10.07.13
 * Time: 21:22
 * To change this template use File | Settings | File Templates.
 */
public class CounterDataHandler implements DataHandler {

    int counts = 0;


    public int getCounts() {
        return counts;
    }

    @Override
    public void initHandler() throws UnsupposedArgumentException, UnsupposedTypeException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void handle(List<String> tokens) {
        counts += tokens.size();
    }

    @Override
    public void flushHandler() throws UnsupposedArgumentException, UnsupposedTypeException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
