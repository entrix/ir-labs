package org.mai.dep806.volkoval.linguistic.ngram;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public interface NGramStorage {

    public NGram addNGram(List<String> names) throws UnsupposedArgumentException, UnsupposedTypeException;

    public NGram getNGram(List<String> names) throws UnsupposedArgumentException, UnsupposedTypeException;

    public NGram getNGram(NGram nGram) throws UnsupposedArgumentException, UnsupposedTypeException;

    public NGram.NGramType getNGramType();

    public int getNGramCount();

    public int getNGramCount(List<String> names) throws UnsupposedArgumentException;

    public int getNGramCount(NGram nGram);

    public int getWordCount();

    public Collection<NGram> getAllNGrams();

    public Collection<Word> getAllWords();

    WordStorage getWordStorage();

    void setWordStorage(WordStorage wordStorage);

    NGram addNGram(NGram nGram);

    double intersect(NGramStorage externStorage) throws UnsupposedArgumentException, UnsupposedTypeException;
}
