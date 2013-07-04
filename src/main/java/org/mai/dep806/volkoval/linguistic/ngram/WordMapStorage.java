package org.mai.dep806.volkoval.linguistic.ngram;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 20:57
 * To change this template use File | Settings | File Templates.
 */
public class WordMapStorage implements WordStorage {

    private Map<String, Word> wordMap;
    private Map<Word, List<Word>> wordStorage;
    private int allWordCount;

    public WordMapStorage() {
        wordMap     = new HashMap<>();
        wordStorage = new HashMap<>();
    }

    public WordMapStorage(int capacity) {
        wordMap     = new HashMap<>(capacity);
        wordStorage = new HashMap<>(capacity);
    }

    public WordMapStorage(Map<String, Word> wordMap, Map<Word, List<Word>> wordStorage) {
        this.wordMap     = wordMap;
        this.wordStorage = wordStorage;
    }

    @Override
    public Word addWord(String name) {

        if (name == null) {
            return null;
        }

        Word word;

        if (!wordMap.containsKey(name)) {
            word = new Word(name, 1);

            wordMap.put(name, word);
            wordStorage.put(word, new ArrayList<Word>());
            word.incrementCount();
            this.incrementCount();
        }
        else {
            word = wordMap.get(name);
            word.incrementCount();
            this.incrementCount();
        }

        return word;
    }

    @Override
    public Word getWord(String name) {
        if (name == null || !wordMap.containsKey(name)) {
            return new Word(name, 0);
        }

        return wordMap.get(name);
    }

    @Override
    public int getWordCount(String name) {
        return getWord(name).getCount();
    }

    @Override
    public int getWordCount(Word word) {
        return wordMap.get(word.getName()).getCount();
    }

    @Override
    public int getWordCount() {
        return wordMap.size();
    }

    @Override
    public Collection<Word> getAllWords() {
        return wordMap.values();
    }

    @Override
    public double getWordFrequency() {
        return allWordCount;
    }

    public void incrementCount() {
        allWordCount++;
    }
}
