package org.mai.dep806.volkoval.lab;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.data.DataRetriever;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.mai.dep806.volkoval.linguistic.model.HeldOutNGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramProbabilityEstimator;
import org.mai.dep806.volkoval.linguistic.ner.LEXRetriever;
import org.mai.dep806.volkoval.linguistic.ner.MWU;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.WordMapStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 28.06.13
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class ThirdLab extends AbstractLab {

    private static Logger logger = LogManager.getLogger(ThirdLab.class);


    private HeldOutNGramModel.HeadOutNGramProbabilityEstimator gramProbabilityEstimator;
    private HeldOutNGramModel.HeadOutNGramProbabilityEstimator biGramProbabilityEstimator;
    private HeldOutNGramModel.HeadOutNGramProbabilityEstimator triGramProbabilityEstimator;

    private LEXRetriever retriever = null;

    private InputMode mode = InputMode.TRAIN;

    private Set<MWU> properNames     = new HashSet<>();

    private Set<MWU> testProperNames = new HashSet<>();


    @Override
    public void setDataRetriever(DataRetriever dataRetriever) {
        super.setDataRetriever(dataRetriever);
        dataRetriever.addDataHandler(new ThirdLabDataHandler());
    }

    @Override
    public String getLabName() {
        return "Third Lab";
    }

    public void setMode(InputMode mode) throws UnsupposedArgumentException, UnsupposedTypeException {
        this.mode = mode;

        if (mode == InputMode.PROPER_NAME) {
            if (retriever == null) {
                List<NGramProbabilityEstimator> estimators = new ArrayList<>();

                gramProbabilityEstimator.computeStatistics();
                biGramProbabilityEstimator.computeStatistics();
                triGramProbabilityEstimator.computeStatistics();

                estimators.add(gramProbabilityEstimator);
                estimators.add(biGramProbabilityEstimator);
                estimators.add(triGramProbabilityEstimator);

                retriever = new LEXRetriever(new HeldOutNGramModel(estimators));
            }
        }
    }

    @Override
    public void produce(int top) throws UnsupposedArgumentException {

        int iter = 0;

        System.out.println("Result: ");

        Set<MWU> tParamNames = new HashSet(properNames);

        tParamNames.retainAll(testProperNames);
        System.out.println("--------------------------------------------" +
                "--------------------------------------------------");
        System.out.println("completeness: " + (double) tParamNames.size() / testProperNames.size());
        System.out.println("--------------------------------------------" +
                "--------------------------------------------------");

        for (MWU mwu : properNames) {
            if (iter == top) {
                break;
            }
            for (String chunk : mwu.getAll()) {
                System.out.print(chunk + " ");
            }
            System.out.println();
            iter++;
        }
    }

    @Override
    public void init() {
        try {
            new ThirdLabDataHandler().initHandler();
        } catch (UnsupposedArgumentException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (UnsupposedTypeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void flush() {
        new ThirdLabDataHandler().flushHandler();
    }

    protected class ThirdLabDataHandler extends AbstractLab.LabDataHandler {

        @Override
        public void initHandler() throws UnsupposedArgumentException, UnsupposedTypeException {
            WordMapStorage storage = new WordMapStorage();

            gramProbabilityEstimator    = new HeldOutNGramModel.HeadOutNGramProbabilityEstimator(NGram.NGramType.UNI_GRAM);
            gramProbabilityEstimator.initialize(storage);
            biGramProbabilityEstimator  = new HeldOutNGramModel.HeadOutNGramProbabilityEstimator(NGram.NGramType.BI_GRAM);
            biGramProbabilityEstimator.initialize(storage);
            triGramProbabilityEstimator = new HeldOutNGramModel.HeadOutNGramProbabilityEstimator(NGram.NGramType.TRI_GRAM);
            triGramProbabilityEstimator.initialize(storage);
        }

        @Override
        public synchronized void handle(List<String> tokens) {
            try {
                if (mode == ThirdLab.InputMode.PROPER_NAME) {
                    statistic.setParameter("wordcount",
                            (int) statistic.getParameter("wordcount") + tokens.size());

                    if (LinguaUtil.isPrecisionRank() && mode != InputMode.TEST) {
                        if ((int) statistic.getParameter("wordcount") > (int) statistic.getParameter("wordtreshold")) {
                            mode = InputMode.TEST;
                        }
                    }
                }

                switch (mode) {
                    case TRAIN:
                        gramProbabilityEstimator.addToTrain(tokens);
                        biGramProbabilityEstimator.addToTrain(tokens);
                        triGramProbabilityEstimator.addToTrain(tokens);
                        break;
                    case VALIDATION:
                        gramProbabilityEstimator.addToValidation(tokens);
                        biGramProbabilityEstimator.addToValidation(tokens);
                        triGramProbabilityEstimator.addToValidation(tokens);
                        break;
                    case PROPER_NAME:
                        properNames.addAll(retriever.retrieveProperNames(tokens));
                        break;
                    case TEST:
                        testProperNames.addAll(retriever.retrieveProperNames(tokens));
                        break;
                }
            } catch (UnsupposedTypeException e) {
                e.printStackTrace();
                logger.error(e);
            } catch (UnsupposedArgumentException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }

        @Override
        public void flushHandler() {
        }
    }

    public LEXRetriever getRetriever() {
        return retriever;
    }

    public static enum InputMode {
        TRAIN, VALIDATION, PROPER_NAME, TEST;
    }
}
