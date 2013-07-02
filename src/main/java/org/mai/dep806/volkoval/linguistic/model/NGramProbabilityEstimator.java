package org.mai.dep806.volkoval.linguistic.model;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;
import org.mai.dep806.volkoval.linguistic.ngram.WordStorage;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 28.06.13
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public interface NGramProbabilityEstimator {
    void initialize(WordStorage storage) throws UnsupposedArgumentException, UnsupposedTypeException;

    void computeStatistics() throws UnsupposedArgumentException;

    int getNr(List<String> tokens) throws UnsupposedTypeException;

    int getTr(List<String> tokens) throws UnsupposedTypeException;

    int getNr(int r);

    int getTr(int r);

    int getN();

    void addToTrain(List<String> tokens) throws UnsupposedTypeException;

    void addToValidation(List<String> tokens) throws UnsupposedTypeException;

    double getProbability(List<String> tokens) throws UnsupposedTypeException;

    NGramStorage getTrainStorage();

    NGramStorage getValidationStorage();

    WordStorage getWordStorage();
}
