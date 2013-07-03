package org.mai.dep806.volkoval.linguistic.ngram;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 02.07.13
 * Time: 0:16
 * To change this template use File | Settings | File Templates.
 */
public class Word {
    private String word;
    private int count;
    private Map<NGram, Integer> links = new HashMap<>();

    public Word(String word) {
        this.word = new String(word);
        count = 0;
    }

    public Word(String word, int freq) {
        this.word  = new String(word);
        this.count = freq;
    }

    public String getName() {
        return word;
    }

    public int getCount() {
        return count;
    }

//        @Override
//        public int hashCode() {
//            return word.hashCode();
//        }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Word)) {
            return false;
        }

        return ((Word) obj).getName().equals(this.getName());
    }

    public void incrementCount() {
        count++;
    }
    public void addOccurence(NGram nGram) throws UnsupposedArgumentException {
        int i = 0;

        for (Word word : nGram.getWords()) {
            if (this.equals(word)) {
                links.put(nGram, i);

                return;
            }
            i++;
        }

        throw new UnsupposedArgumentException("Ngram hasn't occurence of this word");
    }

    public Map<NGram, Integer> getOccurences() {
        return links;
    }
}
