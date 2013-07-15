package org.mai.dep806.volkoval.lab;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.data.DataRetriever;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.mai.dep806.volkoval.linguistic.collocation.*;
import org.mai.dep806.volkoval.linguistic.ngram.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 11.06.13
 * Time: 23:42
 * To change this template use File | Settings | File Templates.
 */
public class FirstLab extends AbstractLab {

    private static Logger logger = LogManager.getLogger(FirstLab.class);

    private NGramProcessor processor;
    private NGramProcessor testProcessor;
    private List<CollocationDetector> detectors     = new ArrayList<>();
    private List<CollocationDetector> testDetectors = new ArrayList<>();

    @Override
    public void setDataRetriever(DataRetriever dataRetriever) {
        super.setDataRetriever(dataRetriever);
        dataRetriever.addDataHandler(new FirstLabDataHandler());
    }

    @Override
    public String getLabName() {
        return "First Lab";
    }

    @Override
    public void produce(int top) throws UnsupposedArgumentException, UnsupposedTypeException {
        if (top < 1 || top > 1000) {
            throw new UnsupposedArgumentException("top number must bi in range from 1 to 1000");
        }

        System.out.println("Result: ");

        NGram.NGramType nGramType = processor.getNGramFactory().getStorage().getNGramType();

        int index = 0;

        for (CollocationDetector detector : detectors) {
            // header
            System.out.println("--------------------------------------------" +
                    "--------------------------------------------------");
            List<CollocationDetector.NGramSortUnit> completeness =
                    new ArrayList<>(testDetectors.get(index).getLatestResult());

            int count = 0;

            for (CollocationDetector.NGramSortUnit unit : completeness) {
                List<String> names = new ArrayList<>();

                for (Word word : unit.getnGram().getWords()) {
                    names.add(word.getName());
                }
                if (processor.getNGramFactory().getStorage().getNGram(names).getCount() > 0) {
                    count++;
                }
            }

            System.out.println("completeness: " +
                    (double) count / completeness.size());
            System.out.println("--------------------------------------------" +
                    "--------------------------------------------------");
            System.out.println("detector: " + detector.getName());
            printHeader(nGramType);

            for (CollocationDetector.NGramSortUnit unit: detector.getLatestResult()) {
                List<Word> words = unit.getnGram().getWords();

                // value
                System.out.printf("%-7s ", String.format("%5.2f", unit.getCoeff()));
                // C(Wi)
                for (Word word : words) {
                    System.out.printf("| %10s ", word.getCount());
                }
                // C(W1, .., Wn)
                System.out.printf("| %10s ", unit.getnGram().getCount());
                // Wi
                for (Word word : words) {
                    System.out.printf("| %20s ", word.getName());
                }
                // next line
                System.out.println("|");
            }

            index++;
        }
    }

    private void printHeader(NGram.NGramType nGramType) {
        String res = "";

        // first column
        System.out.print("value   ");
        // second column
        for (int i = 1; i <= nGramType.ordinal(); ++i) {
            System.out.printf("| %10s ", "C(W" + i + ")");
        }
        // third column
        res = "C(";
        for (int i =1; i <= nGramType.ordinal(); ++i) {
            res += "W" + i + ",";
        }
        res = res.substring(0, res.length() - 1) + ")";
        System.out.printf("| %10s ", res);
        // fourth column
        for (int i = 1; i <= nGramType.ordinal(); ++i) {
            System.out.printf("| %20s ", "W" + i);
        }
        // next line
        System.out.println("|");
        // footer
        System.out.println("--------------------------------------------" +
                "--------------------------------------------------");
    }

    @Override
    public void init() {
        new FirstLabDataHandler().initHandler();
    }

    @Override
    public void flush() {
        new FirstLabDataHandler().flushHandler();
    }

    public void setDetectors(List<String> detectors) {
        for (String detector : detectors) {
            switch (detector) {
                case "lratio":
                    this.detectors.add(new LikeHoodCollocationDetector());
                    this.testDetectors.add(new LikeHoodCollocationDetector());
                    logger.info("set likehood ratio detector");
                    break;
                case "poisson":
                    this.detectors.add(new PoissonDetector());
                    this.testDetectors.add(new PoissonDetector());
                    logger.info("set poisson detector");
                    break;
                case "ttest":
                    this.detectors.add(new TTestCollocationDetector());
                    this.testDetectors.add(new TTestCollocationDetector());
                    logger.info("set likehood t test detector");
                    break;
            }
        }
    }

    protected class FirstLabDataHandler extends LabDataHandler {

        @Override
        public void initHandler() {
            NGramStorage        storage     = new NGramMapStorage(NGram.NGramType.BI_GRAM);
            NGramStorage        testStorage = new NGramMapStorage(NGram.NGramType.BI_GRAM);
            DetectorProvider    provider    = new DetectorProvider(storage, statistic);

            if (detectors.size() == 0) {
                detectors.add(new LikeHoodCollocationDetector());
                testDetectors.add(new LikeHoodCollocationDetector());
                detectors.add(new PoissonDetector());
                testDetectors.add(new PoissonDetector());
                detectors.add(new TTestCollocationDetector());
                testDetectors.add(new TTestCollocationDetector());
            }

            int index = 0;

            for (CollocationDetector detector : detectors) {
                detector.setStatistic(statistic);
                detector.setStorage(storage);
                testDetectors.get(index).setStatistic(statistic);
                testDetectors.get(index).setStorage(testStorage);
                index++;
            }
            processor     = new NGramProcessor(statistic, new NGramFactory(storage),     2);
            testProcessor = new NGramProcessor(statistic, new NGramFactory(testStorage), 2);
            LinguaUtil.setStatistic(statistic);
        }

        @Override
        public synchronized void handle(List<String> tokens) {
            try {
                statistic.setParameter("wordcount",
                        (int) statistic.getParameter("wordcount") + tokens.size());

                if (LinguaUtil.isPrecisionRank()) {
                    if ((int) statistic.getParameter("wordcount") > (int) statistic.getParameter("wordtreshold")) {
                        testProcessor.addNGrams(tokens);
                    }
                    else {
                        processor.addNGrams(tokens);
                    }
                } else {
                    processor.addNGrams(tokens);
                }
            } catch (UnsupposedTypeException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }

        @Override
        public void flushHandler() {
            for (CollocationDetector detector : detectors) {
                detector.detect();
            }
            for (CollocationDetector detector : testDetectors) {
                detector.detect();
            }
        }
    }
}
