package org.mai.dep806.volkoval.linguistic.model;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;
import org.mai.dep806.volkoval.linguistic.ngram.WordStorage;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
public interface NGramModel {

    public String getName();

    public void addNGrams(List<String> tokens) throws UnsupposedTypeException;

    public double getProbability(List<String> prev, String next) throws UnsupposedTypeException, UnsupposedArgumentException;

    public void refreshStatistics() throws UnsupposedArgumentException, UnsupposedTypeException;

    public NGram.NGramType getNGramType();

    public double f(int r);

    public void clear() throws UnsupposedArgumentException, UnsupposedTypeException;

    public NGramModelType getType();

    public List<HeldOutNGramModel.HeadOutNGramProbabilityEstimator> getEstimators();

    public double getProbability(List<String> tokens) throws UnsupposedTypeException, UnsupposedArgumentException;

    public WordStorage getWordStorage();

    public List<NGramStorage> getNGramStorages();

    public static enum NGramModelType {
        HELD_OUT;
    }
}
