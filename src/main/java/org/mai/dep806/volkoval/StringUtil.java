package org.mai.dep806.volkoval;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 04.07.13
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

    public static List<String> asList(String ... args) {
        List<String> result = new ArrayList<>();

        for (String arg : args) {
            result.add(arg);
        }

        return result;
    }
}
