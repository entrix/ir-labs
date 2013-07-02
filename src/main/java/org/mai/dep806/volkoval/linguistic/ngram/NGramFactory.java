package org.mai.dep806.volkoval.linguistic.ngram;

import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.CommonStatistic;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public class NGramFactory {

    private NGramStorage storage;


    public NGramFactory() {
        storage = new NGramMapStorage(NGram.NGramType.BI_GRAM);
    }

    public NGramFactory(NGramStorage storage) {
        this.storage = storage;
    }

    public NGramStorage getStorage() {
        return storage;
    }

    public void setStorage(NGramStorage storage) {
        this.storage = storage;
    }

    public NGram createNGram(List<String> names) throws UnsupposedTypeException {

        if (storage.getNGramType() != NGramUtil.getTypeByLength(names.size())) {
            throw new UnsupposedTypeException("factory storage type doesn't correspond this ngram type");
        }

        return storage.getNGram(names);
    }

    public NGram addNGram(List<String> names) throws UnsupposedTypeException {

        if (storage.getNGramType() != NGramUtil.getTypeByLength(names.size())) {
            throw new UnsupposedTypeException("factory storage type doesn't correspond this ngram type");
        }

        return storage.addNGram(names);
    }
}
