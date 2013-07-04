package org.mai.dep806.volkoval.linguistic.spell;

import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramUtil;
import org.mai.dep806.volkoval.linguistic.ngram.Word;
import org.mai.dep806.volkoval.linguistic.ngram.WordStorage;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 01.07.13
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class LDDNGramWordLinker {

    private Map<String, EquivalenceClass<String>> classes = new HashMap<>();

    private WordStorage storage;

    public LDDNGramWordLinker(WordStorage storage) {
        this.storage = storage;
    }


    public Set<String> getAllEquivalences(String name) {
        if (!classes.containsKey(name)) {
            classes.put(name, new LDDWordEquivalenceClass(name));
        }

        return classes.get(name).getAllElements();
    }

    /**
     * Equivalence frequency of the word
     *
     * @param name
     * @return
     */
    public double getEquivFreq(String name) {
        if (!classes.containsKey(name)) {
            classes.put(name, new LDDWordEquivalenceClass(name));
        }

        return ((LDDWordEquivalenceClass) classes.get(name)).getFrequency(name);
    }

    /**
     * get all NGrams with word 'name' at the 'pop' position
     *
     * @param name
     * @param pos
     * @return
     */
    public List<NGram> getNGramsAt(String name, int pos) throws UnsupposedTypeException {

        List<NGram> result = new ArrayList<>();

        for (Map.Entry<NGram, Integer> entry : storage.getWord(name).getOccurences().entrySet()) {
            NGram nGram = entry.getKey();

            if (NGramUtil.getLengthByType(nGram.getType()) < pos) {
                if (nGram.getWords().get(pos).equals(name)) {
                    result.add(nGram);
                }
            }
        }

        return result;
    }

    /**
     * get all NGrams with word 'name' at the last position
     * @param name
     * @return
     */
    public List<NGram> getNGramsAtLast(String name) {
        List<NGram> result = new ArrayList<>();

        for (Map.Entry<NGram, Integer> entry : storage.getWord(name).getOccurences().entrySet()) {
            NGram nGram = entry.getKey();

            if (nGram.getWords().get(nGram.getWords().size() - 1).equals(name)) {
                result.add(nGram);
            }
        }

        return result;
    }

    public class LDDWordEquivalenceClass extends EquivalenceClass<String> {

        private String seed;

        private int summarizedFrequency = 10;


        public LDDWordEquivalenceClass(String seed) {
            this.seed = seed;
            initialize();
        }

        @Override
        public boolean addElement(String elem) {
            // not provided
            return false;
        }

        public double getFrequency(String name) {
            if (!elements.contains(name)) {
                return 0.0;
            }

            Word elem = storage.getWord(name);

            return (double) (elem.getCount() == 0 ?
                    1 : elem.getCount()) / summarizedFrequency;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }

        @Override
        protected void initialize() {
            super.initialize();

            for (String permut : LinguaUtil.getLevensteinDamerauDisplacement(seed)) {
                if (storage.getWord(permut).getCount() > 0) {
                    elements.add(permut);
                    summarizedFrequency += storage.getWordCount(permut);
                }
            }
            summarizedFrequency += storage.getWordCount(seed);
        }
    }

}
