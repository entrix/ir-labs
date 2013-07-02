package org.mai.dep806.volkoval.linguistic.ngram;

import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */
public class NGramAnalyzer {

    private NGramStorage storage;

    public NGramAnalyzer(NGramStorage storage) {
        this.storage = storage;
    }

    public double getNGramProbability(List<String> words) throws UnsupposedTypeException {
        return storage.getNGramCount(words) /
               storage.getNGramCount();
    }

    public double getNGramFrequency(NGram nGram) {
        return storage.getNGramCount(nGram) /
               storage.getNGramCount();
    }

    public double getWordFrequency(String word) {
        return storage.getWordStorage().getWordCount(word) /
               storage.getWordStorage().getWordCount();
    }
}
