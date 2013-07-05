package org.mai.dep806.volkoval.lab;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.data.DataRetriever;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.mai.dep806.volkoval.linguistic.model.HeldOutNGramModel;
import org.mai.dep806.volkoval.linguistic.model.HeldOutNGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramProbabilityEstimator;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.Word;
import org.mai.dep806.volkoval.linguistic.spell.SimpleSpellChecker;
import org.mai.dep806.volkoval.linguistic.spell.SpellChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

    private List<String> queries = new ArrayList<>();

    private List<SpellChecker> spellCheckers = new ArrayList<>();

    @Override
    public void setDataRetriever(DataRetriever dataRetriever) {
        super.setDataRetriever(dataRetriever);
        dataRetriever.addDataHandler(new FourthLabDataHandler());
    }
    @Override
    public String getLabName() {
        return "Second Lab";
    }

    @Override
    public void produce(int freq) throws UnsupposedArgumentException {
        if (freq < 1 || freq > 1000) {
            throw new UnsupposedArgumentException("frequency number must bi in range from 1 to 1000");
        }

        try {
            for (SpellChecker spellChecker : spellCheckers) {
                // header
                System.out.println("-------------------------------------------------------------------------------------" +
                        "------------------------------------------------------------------------------------------------");
                System.out.println("spellChecker: " + spellChecker.getName());
                System.out.println("model: " + spellChecker.getModel());
                ((HeldOutNGramModel) spellChecker.getModel()).setMode(HeldOutNGramModel.ModelMode.TRAIN);

                for (String query : queries) {
                    System.out.println("-------------------------------------------------------------------------------------" +
                            "------------------------------------------------------------------------------------------------");
                    System.out.println("query: " + query);
                    System.out.print("correction: ");

                    // check whether string don't ends with sentence terminal symbol
                    if (Pattern.compile(".*\\w[^\\.\\?\\!]$").matcher(query).find()) {
                        query += ".";
                    }

                    for (List<String> sentence : LinguaUtil.toSentences((query).toCharArray())) {
                        int i = 0;
                        List<String> prepSentence = new ArrayList<>();

                        if (sentence.isEmpty()) {
                            continue;
                        }

                        for (String word : sentence) {
                            prepSentence.add(word.toLowerCase());
                        }
                        for (String elem : spellChecker.getCorrection(prepSentence)) {
                            if (i == 0) {
                                elem = (String.valueOf(elem.toCharArray()[0]).toUpperCase()) +
                                        elem.substring(1);
                                i++;
                            }
                            System.out.print(elem + " ");
                        }
                        System.out.print(". ");
                    }
                    System.out.println(".");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    @Override
    public void init() {
        new FourthLabDataHandler().initHandler();
    }

    @Override
    public void flush() throws UnsupposedArgumentException, UnsupposedTypeException {
        new FourthLabDataHandler().flushHandler();
    }

    public void setEstimators(List<String> models) {
        try {
            for (String model : models) {
                switch (model) {
                    case "heldout":
                        this.models.add(new HeldOutNGramModel(NGram.NGramType.UNI_GRAM, 2));
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

    public void setSpellCheckers(List<String> spellCheckers) {
        for (String spellChecker : spellCheckers) {
            switch (spellChecker) {
                case "simple":
                    for (NGramModel model : models) {
                        this.spellCheckers.add(new SimpleSpellChecker(model));
                    }
                    break;
            }
        }
    }

    public List<NGramModel> getModels() {
        return models;
    }

    public void setQueries(List<String> queries) {
        this.queries.addAll(queries);
    }

    protected class FourthLabDataHandler extends AbstractLab.LabDataHandler {

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
        public void flushHandler() throws UnsupposedArgumentException, UnsupposedTypeException {
            for (NGramModel model : models) {
                model.refreshStatistics();
            }
        }
    }
}
