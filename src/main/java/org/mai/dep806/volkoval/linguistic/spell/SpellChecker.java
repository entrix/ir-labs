package org.mai.dep806.volkoval.linguistic.spell;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 01.07.13
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public interface SpellChecker {

    public String getName();

    public NGramModel getModel();

    public List<String> getCorrection(List<String> tokens) throws UnsupposedArgumentException, UnsupposedTypeException;
}
