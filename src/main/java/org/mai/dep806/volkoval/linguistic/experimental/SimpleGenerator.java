package org.mai.dep806.volkoval.linguistic.experimental;

import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: AVVolkov
 * Date: 15.07.13
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class SimpleGenerator {

    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(SimpleGenerator.class);

    private static Random random = new Random(System.currentTimeMillis());

    private HashMap<Integer, List<SimpleNGram>> nGramMap = new HashMap<>();
    private List<SimpleNGram> startNGramList = new ArrayList<>();

    private SimpleNGram.NGramType nGramType;

    private int windowSize = 2;

    private int fullCount = 0;


    public SimpleGenerator() {
        nGramMap = new HashMap<>();
    }

    public SimpleGenerator(SimpleNGram.NGramType mode) throws UnsupposedTypeException {
     this(mode, NGramUtil.getLengthByType(mode));
    }

    public SimpleGenerator(SimpleNGram.NGramType mode, int windowSize) {
        this.nGramType = mode;
        this.windowSize = windowSize;
    }

    public SimpleGenerator(int windowSize) {
        this.windowSize = windowSize;
    }

    public SimpleGenerator(SimpleGenerator model) {
        this.nGramMap = model.nGramMap;
        this.nGramType = model.nGramType;
        this.windowSize = model.windowSize;
    }

    public List<String> generateSentence(String startToken) throws UnsupposedArgumentException, UnsupposedTypeException {
        List<String> result = new ArrayList<>();
        SimpleNGram iterator;

        if (!startToken.isEmpty() && startNGramList.contains(new SimpleNGram(startToken))) {
            iterator = startNGramList.get(startNGramList.indexOf(new SimpleNGram(startToken)));
        }
        else {
            int start = Math.abs(random.nextInt()) % startNGramList.size();

            iterator = startNGramList.get(start);
        }


        while (iterator.getFlag() != SimpleNGram.PositionFlag.END) {
            iterator = getNextByDistribution(iterator, false);
            result.addAll(iterator.getWords());
        }

        return result;
    }

    public List<String> generateSentence() throws UnsupposedArgumentException, UnsupposedTypeException {
        return generateSentence("");
    }

    public List<String> generatePhrase(String startToken) throws UnsupposedArgumentException, UnsupposedTypeException {
        List<String> result = new ArrayList<>();
        SimpleNGram iterator;
        int counter = 0;

        if (!startToken.isEmpty() && startNGramList.contains(new SimpleNGram(startToken))) {
            iterator = startNGramList.get(startNGramList.indexOf(new SimpleNGram(startToken)));
        }
        else {
            int start = Math.abs(random.nextInt()) % startNGramList.size();

            iterator = startNGramList.get(start);
        }

        while (iterator.getFlag() != SimpleNGram.PositionFlag.END) {
            iterator = getNextByDistribution(iterator, true);
            result.addAll(iterator.getWords());
            counter++;

            if (counter > 3 && iterator.getWords().get(iterator.getWords().size() - 1).length() >= 3) {
                int diff = 10 - (counter - 3) * 2;

                diff = (diff < 0) ?
                        0 : diff;
                if (diff == 0 || random.nextInt() % diff == 0) {
                    break;
                }
            }
        }

        return result;
    }

    public List<String> generatePhrase() throws UnsupposedArgumentException, UnsupposedTypeException {
        return generatePhrase("");
    }

    private SimpleNGram getNextByDistribution(SimpleNGram nGram, boolean isPhrase) {

        if (nGram.getNext().size() == 1) {
            return getWithSynonyms(nGram.getNext().get(0));
        }

        List<SimpleNGram> nextNGrams = nGram.getNext();
        List<Integer> frequencies = new ArrayList<>(nextNGrams.size());
        List<Integer> distribution = new ArrayList<>(nextNGrams.size());
        int freqSum = 0;

        for (SimpleNGram next : nextNGrams) {
            System.out.println("next = " + next);

            if (isPhrase && next.getFlag() == SimpleNGram.PositionFlag.END) {
                freqSum += next.getCount() * 2;
            }
            else {
                freqSum += next.getCount();
            }
            distribution.add(freqSum);
        }

        System.out.println("freq = " + freqSum);

        int nextItem = Math.abs(random.nextInt()) % freqSum;
        int index = 0;
        SimpleNGram result = null;

        for (; index < distribution.size(); index++) {
            if (nextItem < distribution.get(index)) {
                result = nextNGrams.get(index);
            }
        }

        return result;
    }

    private SimpleNGram getWithSynonyms(SimpleNGram simpleNGram) {
        SimpleNGram nGram = simpleNGram;

        if (!simpleNGram.getSynonyms().isEmpty()) {
            int index = Math.abs(random.nextInt()) %
                    (nGram.getSynonyms().size() + 1);

            nGram =  (index == 0) ?
                      nGram : nGram.getSynonyms().get(index - 1);
        }

        return nGram;
    }

    public void addFragment(List<String> tokens) throws UnsupposedTypeException {
        int cursor = 0;
        SimpleNGram prevNGram = null;

        if (tokens.size() < NGramUtil.getLengthByType(nGramType)) {
            return;
        }
        while (cursor < tokens.size())  {
            List<String> subTokens = tokens.subList(cursor,
                    cursor + windowSize >= tokens.size() ?
                            tokens.size() : cursor + windowSize);

            if (windowSize > 1) {
                cursor += 1;
            }
            else {
                cursor++;
            }

            try {
                for (List<String> names : NGramUtil.groupWords(subTokens, nGramType)) {
                    SimpleNGram nGram = new SimpleNGram(names);

                    if (!nGramMap.containsKey(nGram)) {
                        nGramMap.put(nGram.hashCode(), new ArrayList<SimpleNGram>());
                    }
                    nGramMap.get(nGram.hashCode()).add(nGram);
                    fullCount++;

                    if (cursor == 1) {
                        nGram.setFlag(SimpleNGram.PositionFlag.START);
                        startNGramList.add(nGram);
                    }
                    else {
                        if (cursor == tokens.size()) {
                            if (nGram.getWords().get(nGram.getWords().size() - 1).length() < 3) {
                                prevNGram.setFlag(SimpleNGram.PositionFlag.END);
                                prevNGram.setNext(new ArrayList<SimpleNGram>());
                                nGram = prevNGram;
                            }
                            else {
                                nGram.setFlag(SimpleNGram.PositionFlag.END);
                            }
                        }
                        prevNGram.getNext().add(nGram);
                    }
                    prevNGram = nGram;
                }
            } catch (UnsupposedTypeException e) {
                logger.error("NGrammProcessor has error underlying nGramType", e);
                throw new UnsupposedTypeException("NGrammProcessor doesn't work");
            } catch (UnsupposedArgumentException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
        prevNGram.setFlag(SimpleNGram.PositionFlag.END);
        if (startNGramList.contains(prevNGram)) {
            startNGramList.remove(prevNGram);
        }
    }

    public void setnGramType(SimpleNGram.NGramType nGramType) {
        this.nGramType = nGramType;
    }

    public SimpleNGram.NGramType getnGramType() {
        return nGramType;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
}
