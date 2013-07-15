package org.mai.dep806.volkoval.linguistic.experimental;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: AVVolkov
 * Date: 15.07.13
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public class SimpleNGram {

    private int                        count;
    private NGramType                  type;
    private Map<PositionFlag, Integer> flags    = new HashMap<>();
    private List<String>               words;
    private List<SimpleNGram>          next     = new ArrayList<>();
    private List<SimpleNGram>          synonyms = new ArrayList<>();


    protected SimpleNGram() {
        words = new ArrayList<>();
        count = 0;
    }


    protected SimpleNGram(List<String> words) throws UnsupposedArgumentException, UnsupposedTypeException {
        this(words, 1);
    }

    public SimpleNGram(List<String> words, int count) throws UnsupposedArgumentException, UnsupposedTypeException {
        this(words, count, PositionFlag.USUAL);
    }

    public SimpleNGram(List<String> words, int count, PositionFlag flag) throws UnsupposedArgumentException, UnsupposedTypeException {
        this.words = new ArrayList<>(words);
        this.count = count;
        this.flags.put(PositionFlag.START, 0);
        this.flags.put(PositionFlag.USUAL, 0);
        this.flags.put(PositionFlag.END, 0);
//        addFlag(flag);
        type = NGramUtil.getTypeByLength(words.size());
    }

    protected SimpleNGram(String ... wordArray) throws UnsupposedTypeException, UnsupposedArgumentException {
        this(Arrays.asList(wordArray), 1, PositionFlag.USUAL);
    }

    public void addSynonym(SimpleNGram nGram) throws UnsupposedTypeException {
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

    private void incrementCount() {
        count++;
    }

    public List<SimpleNGram> getSynonyms() {
        return synonyms;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<SimpleNGram> getNext() {
        return next;
    }

    public void setNext(List<SimpleNGram> next) {
        this.next = next;
    }

    public NGramType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int    code = 0;
        double prec = 1.1;
        double step = 0.1;

        for (String token : words) {
            code += (token.hashCode() % 2000000000) * prec;
            prec += step;
        }

        return code;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof SimpleNGram)) {
            return false;
        }

        SimpleNGram nGram = (SimpleNGram) obj;
        List<String> words = nGram.getWords();
        int count = words.size();

        // check equality types of the NGram's
        if (count != getWords().size()) {
            return false;
        }

        // check that all words equals
        for (int i = 0; i < words.size(); ++i) {
            if (!getWords().get(i).equals(words.get(i))) {
                return false;
            }
        }

        return true;
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

    public List<PositionFlag> getFlags() {
        List<PositionFlag> flagList = new ArrayList<>();

        for (PositionFlag flag : flags.keySet()) {
            if (flags.get(flag) > 0) {
                flagList.add(flag);
            }
        }

        return flagList;
    }

    public void addFlag(PositionFlag flag) {
            flags.put(flag, flags.get(flag) + 1);
    }

    public static enum NGramType {
        ZERO, UNI_GRAM, BI_GRAM, TRI_GRAM;
    }

    public static enum PositionFlag {
        START, USUAL, END
    }

    public Map<PositionFlag, Integer> getFlagsWithFreq() {
        return flags;
    }
}
