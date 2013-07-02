package org.mai.dep806.volkoval.lab;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.data.DataRetriever;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.model.HeadOutNGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramProbabilityEstimator;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 01.07.13
 * Time: 19:40
 * To change this template use File | Settings | File Templates.
 */
public class FourthLab extends AbstractLab {
    private static Logger logger = LogManager.getLogger(SecondLab.class);

    private List<NGramModel> models = new ArrayList<>();

    @Override
    public void setDataRetriever(DataRetriever dataRetriever) {
        super.setDataRetriever(dataRetriever);
        dataRetriever.addDataHandler(new SecondLabDataHandler());
    }
    @Override
    public String getLabName() {
        return "Second Lab";
    }

    @Override
    public void produce(int freq) throws UnsupposedArgumentException {
        if (freq < 1 || freq > 1000) {
            throw new UnsupposedArgumentException("frquency number must bi in range from 1 to 1000");
        }

        try {
            for (NGramModel model : models) {
                // header
                System.out.println("-------------------------------------------------------------------------------------" +
                        "------------------------------------------------------------------------------------------------");
                System.out.println("model: " + model.getName());
                for (int r = 1; r < freq; ++r) {

                    // value
                    System.out.printf("r = %2d: f = %-7s ", r, String.format("%5.2f", model.f(r)));
                    model.getProbability(Arrays.asList(new String[]{"которой", "каждый"}), "воин");
//                System.out.printf("| %5s ", model.getNr(r));
//                System.out.printf("| %5s ", model.getTr(r));
                    System.out.println();
                }
                NGramProbabilityEstimator estimator = model.getEstimators().get(1);
                for (NGram nGram : estimator.getValidationStorage().getAllNGrams()) {
                    List<Word> words = nGram.getWords();
                    List<String> prev = new ArrayList<>();
                    String next = words.get(2).getName();

                    prev.add(words.get(0).getName());
                    prev.add(words.get(1).getName());
                    System.out.println("-------------------------------------------------------------------------------------" +
                            "------------------------------------------------------------------------------------------------");
                    System.out.printf(" %15s %15s | %15s ", prev.get(0), prev.get(1), next);
                    System.out.printf("| %5s \n", model.getProbability(prev, next));
                    for (NGram synNGram : nGram.getSynonyms()) {
                        System.out.printf(" %15s %15s | %15s ",
                                synNGram.getWords().get(0).getName(),
                                synNGram.getWords().get(1).getName(),
                                synNGram.getWords().get(2).getName());
                        System.out.printf("| %5s \n", (double) synNGram.getCount() / nGram.getCount());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    @Override
    public void init() {
        new SecondLabDataHandler().initHandler();
    }

    @Override
    public void flush() {
        new SecondLabDataHandler().flushHandler();
    }

    public void setEstimators(List<String> models) {
        try {
            for (String model : models) {
                switch (model) {
                    case "heldout":
                        this.models.add(new HeadOutNGramModel(NGram.NGramType.BI_GRAM));
                        break;
                }
            }
        } catch (UnsupposedArgumentException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (UnsupposedTypeException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public List<NGramModel> getModels() {
        return models;
    }

    protected class SecondLabDataHandler extends AbstractLab.LabDataHandler {

        @Override
        public void initHandler() {
        }

        @Override
        public synchronized void handle(List<String> tokens) {
            try {
                for (NGramModel model : models) {
                    model.addNGrams(tokens);
                }
            } catch (UnsupposedTypeException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }

        @Override
        public void flushHandler() {
            for (NGramModel model : models) {
                model.refreshStatistics();
            }
        }
    }
}
