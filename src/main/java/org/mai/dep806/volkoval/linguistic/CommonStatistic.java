package org.mai.dep806.volkoval.linguistic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 19.06.13
 * Time: 4:06
 * To change this template use File | Settings | File Templates.
 */
public class CommonStatistic {

    private Map<String, Number> parameters = new HashMap<>();

    public void setParameter(String name, Number value) {
        parameters.put(name, value);
    }

    public Number getParameter(String name) {
        return parameters.get(name) == null ?
            0 : parameters.get(name);
    }
}
