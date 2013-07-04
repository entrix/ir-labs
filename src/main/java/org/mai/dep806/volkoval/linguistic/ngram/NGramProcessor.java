package org.mai.dep806.volkoval.linguistic.ngram;

import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.CommonStatistic;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.mai.dep806.volkoval.linguistic.collocation.CollocationDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class NGramProcessor {

    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(NGramProcessor.class);


    private int windowSize;

    private NGram.NGramType nGramType;

    private CommonStatistic statistic;

    private NGramFactory nGramFactory;

    private boolean normalize = LinguaUtil.isNormalize();


    public NGramProcessor() {
        nGramFactory = new NGramFactory(new NGramMapStorage(NGram.NGramType.BI_GRAM));
        this.statistic = new CommonStatistic();
        windowSize = 9;
    }

    public NGramProcessor(CommonStatistic statistic, NGram.NGramType nGramType) {
        this.nGramType  = nGramType;
        this.windowSize = nGramType.ordinal();
        this.statistic  = statistic;
        nGramFactory    = new NGramFactory(new NGramMapStorage(nGramType));
    }

    public NGramProcessor(CommonStatistic statistic, NGram.NGramType nGramType, int windowSize) {
        this.nGramType = nGramType;
        this.statistic = statistic;
        this.windowSize = windowSize >= 2 ?
            windowSize : 2;
        nGramFactory = new NGramFactory(new NGramMapStorage(nGramType));
    }

    public NGramProcessor(CommonStatistic statistic, NGramFactory nGramFactory) {
        this.nGramFactory = nGramFactory;
        this.statistic  = statistic;
        this.nGramType  = nGramFactory.getStorage().getNGramType();
        this.windowSize = nGramType.ordinal();
    }

    public NGramProcessor(CommonStatistic statistic, NGramFactory nGramFactory, int windowSize) {
        this.nGramFactory = nGramFactory;
        this.statistic    = statistic;
        this.windowSize   = windowSize >= 2 ?
                2 : windowSize;
        this.nGramType    = nGramFactory.getStorage().getNGramType();
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public CommonStatistic getStatistic() {
        return statistic;
    }

    public NGramFactory getNGramFactory() {
        return nGramFactory;
    }


    public int getNr(int r) {
        return 0;
    }

    public int getTr(int r) {
        return 0;
    }

    public void addNGrams(List<String> tokens) throws UnsupposedTypeException {
            int cursor = 0;

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
                        if (normalize) {
                            NGram nGram;
                            List<String> normalizedNames = new ArrayList<>();

                            for (String name : names) {
                                normalizedNames.add(LinguaUtil.getRussianNormalForm(name));
                            }
                            nGram = nGramFactory.addNGram(normalizedNames);
                            nGram.addSynonym(nGramFactory.createNGram(names));
                        }
                        else {
//                            if (names.get(0).equals("странный")) {
                                nGramFactory.addNGram(names);
//                            }
                        }
                    }
            } catch (UnsupposedTypeException e) {
                logger.error("NGrammProcessor has error underlying nGramType", e);
                throw new UnsupposedTypeException("NGrammProcessor doesn't work");
            } catch (UnsupposedArgumentException e) {
                    e.printStackTrace();
                    logger.error(e);
                }
            }
    }
}
