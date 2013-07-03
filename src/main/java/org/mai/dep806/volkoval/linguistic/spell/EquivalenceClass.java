package org.mai.dep806.volkoval.linguistic.spell;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 02.07.13
 * Time: 0:03
 * To change this template use File | Settings | File Templates.
 */
public abstract class EquivalenceClass<T> {

    protected Set<T> elements;

    public boolean addElement(T elem) {
        boolean res = false;

        if (isEquivalent(elem)) {
            res = elements.add(elem);
        }

        return res;
    }

    public Set<T> getAllElements() {
        return new TreeSet<T>(elements);
    }

    public boolean isEquivalent(T elem) {
        return elements.contains(elem);
    }

    protected void initialize() {
        if (elements == null) {
            elements = new HashSet<>();
        }
        else {
            elements.clear();
        }
    }
}
