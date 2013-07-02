package org.mai.dep806.volkoval.linguistic.ngram;

import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
public class NGramMapStorage implements NGramStorage {

    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(NGramProcessor.class);


    private WordStorage wordStorage;

    private Map<Integer, NGram> nGramSet;

    private NGram.NGramType nGramType;


    public NGramMapStorage(NGram.NGramType NGramType) {
        this.wordStorage = new WordMapStorage();
        this.nGramType = NGramType;
        this.nGramSet = new HashMap<>();
    }

    public NGramMapStorage(NGram.NGramType NGramType, WordStorage wordStorage) {
        this.wordStorage = wordStorage;
        this.nGramType = NGramType;
        this.nGramSet = new HashMap<>();
    }

    public NGramMapStorage(NGram.NGramType NGramType, WordStorage wordStorage, Map<Integer, NGram> nGramSet) {
        this.wordStorage = wordStorage;
        this.nGramSet = nGramSet;
        this.nGramType = NGramType;
    }

    @Override
    public WordStorage getWordStorage() {
        return wordStorage;
    }

    @Override
    public void setWordStorage(WordStorage wordStorage) {
        this.wordStorage = wordStorage;
    }

    @Override
    public NGram addNGram(List<String> names) throws UnsupposedArgumentException {

        if (names == null || names.size() != NGram.getNGramTypeLength(nGramType)) {
            return null;
        }

        List<Word> words = new ArrayList<>(names.size());

        for (String name : names) {
            words.add(wordStorage.addWord(name));
        }

        NGram nGram = new NGram(words);

        if (!nGramSet.containsKey(nGram.hashCode())) {
            nGramSet.put(nGram.hashCode(), nGram);
        }
        else {
            nGram = nGramSet.get(nGram.hashCode());
            nGram.incrementCount();
        }

        return nGram;
    }

    @Override
    public NGram addNGram(NGram nGram) {

        if (nGram == null) {
            return null;
        }

        if (!nGramSet.containsKey(nGram.hashCode())) {
            nGramSet.put(nGram.hashCode(), nGram);
        }
        else {
            nGram = nGramSet.get(nGram.hashCode());
            nGram.incrementCount();
        }

        return nGram;
    }

    @Override
    public NGram getNGram(List<String> names) throws UnsupposedArgumentException {
        if (names == null || names.size() != NGram.getNGramTypeLength(nGramType)) {
            return null;
        }

        List<Word> words = new ArrayList<>(names.size());

        for (String name : names) {
            words.add(wordStorage.getWord(name));
        }

        NGram nGram = new NGram(words);

        if (nGramSet.containsKey(nGram.hashCode())) {
            return nGramSet.get(nGram.hashCode());
        }

        return nGram;
    }

    @Override
    public NGram getNGram(NGram nGram) throws UnsupposedArgumentException {
        if (!nGramSet.containsKey(nGram.hashCode())) {
            List<Word> words = nGram.getWords();

            return new NGram(words);
        }
        return nGramSet.get(nGram.hashCode());
    }

    @Override
    public NGram.NGramType getNGramType() {
        return nGramType;
    }

    @Override
    public int getNGramCount() {
        return nGramSet.size();
    }

    @Override
    public int getNGramCount(List<String> names) throws UnsupposedArgumentException {
        List<Word> words = new ArrayList<>(names.size());

        for (String name : names) {
            words.add(wordStorage.getWord(name));
        }

        return nGramSet.get(new NGram(words).hashCode()).getCount();
    }

    @Override
    public int getNGramCount(NGram nGram) {
        return nGramSet.get(nGram.hashCode()).getCount();
    }

    @Override
    public int getWordCount() {
        return wordStorage.getWordCount();
    }

    @Override
    public Collection<NGram> getAllNGrams() {
        return nGramSet.values();
    }

    @Override
    public Collection<Word> getAllWords() {
        return wordStorage.getAllWords();
    }

    public int getWordCount(String name) throws UnsupposedTypeException {
        return wordStorage.getWordCount(name);
    }

    public int getWordCount(Word word) throws UnsupposedTypeException {
        return wordStorage.getWordCount(word);
    }

    //    @Override
//    public int getBiGramCount(String name1, String name2) {
//        return 0;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public double getBiGramFrequency() {
//        return 0;  //To change body of implemented methods use File | Settings | File Templates.
//    }
}
