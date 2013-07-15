package org.mai.dep806.volkoval.linguistic.ngram;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class NGram {

    private List<Word> words;

    private List<NGram> synonyms = new ArrayList<>();

    private int count;

    private NGramType type;

    private List<NGram> next = new ArrayList<>();


    protected NGram() {
        words = new ArrayList<>();
        count = 0;
    }

//    @Override
//    public int hashCode() {
//        Integer hashSum = 0;
//
//        for (Word word : words) {
//            hashSum += word.hashCode();
//        }
//
//        return hashSum;
//    }


    protected NGram(List<Word> words) throws UnsupposedArgumentException, UnsupposedTypeException {
        this.words = words;
        count = 0;
        type = NGramUtil.getTypeByLength(words.size());
    }


    public NGram(List<Word> words, int count) throws UnsupposedArgumentException, UnsupposedTypeException {
        this.words = words;
        this.count = count;
        type = NGramUtil.getTypeByLength(words.size());
    }

    protected NGram(Word ... wordArray) throws UnsupposedTypeException, UnsupposedArgumentException {
        words = new ArrayList<>();

        for (Word word : wordArray) {
            words.add(word);
        }

        type = NGramUtil.getTypeByLength(words.size());
        count = 1;
    }

    public void addSynonym(NGram nGram) throws UnsupposedTypeException {
        if (nGram.type != type) {
            throw new UnsupposedTypeException("Synonym must be the same type");
        }
        if (!synonyms.contains(nGram)) {
            synonyms.add(nGram);
        }
        else {
            synonyms.get(synonyms.indexOf(nGram)).incrementCount();
        }
    }

    public List<NGram> getSynonyms() {
        return synonyms;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
    }

    public List<Word> getWords() {
        return words;
    }

    public NGramType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof NGram)) {
            return false;
        }

        NGram nGram = (NGram) obj;
        List<Word> words = nGram.getWords();
        int count = words.size();

        // check equality types of the NGram's
        if (count != getWords().size()) {
            return false;
        }

        // check that all words equals
        for (Word word : words) {
            if (!getWords().contains(word)) {
                return false;
            }
        }

        return true;
    }

    public List<NGram> getNext() {
        return next;
    }

    public void setNext(List<NGram> next) {
        this.next = next;
    }

    public static int getNGramTypeLength(NGramType type) {
        switch (type) {
            case UNI_GRAM:
                return 1;
            case BI_GRAM:
                return 2;
            case TRI_GRAM:
                return 3;
            default:
                return 0;
        }
    }

    public static enum NGramType {
        ZERO, UNI_GRAM, BI_GRAM, TRI_GRAM;
    }
}
