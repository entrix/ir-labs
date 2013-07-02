package org.mai.dep806.volkoval.data;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 11.06.13
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public interface DataHandler {
    public void initHandler() throws UnsupposedArgumentException, UnsupposedTypeException;
    public void handle(List<String> tokens);
    public void flushHandler() throws UnsupposedArgumentException;
}
