package org.mai.dep806.volkoval.linguistic.ngram;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 20:53
 * To change this template use File | Settings | File Templates.
 */
public interface WordStorage {

    public Word addWord(String name);

    public Word getWord(String name);

    public int getWordCount(String name);

    public int getWordCount(Word word);

    public int getWordCount();

    public Collection<Word> getAllWords();

    public double getWordFrequency();
}
