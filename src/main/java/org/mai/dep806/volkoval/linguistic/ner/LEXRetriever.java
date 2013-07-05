package org.mai.dep806.volkoval.linguistic.ner;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.CommonStatistic;
import org.mai.dep806.volkoval.linguistic.collocation.CollocationDetector;
import org.mai.dep806.volkoval.linguistic.collocation.LikeHoodCollocationDetector;
import org.mai.dep806.volkoval.linguistic.model.HeldOutNGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramProbabilityEstimator;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramMapStorage;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;
import org.mai.dep806.volkoval.linguistic.ngram.WordStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.mai.dep806.volkoval.linguistic.collocation.CollocationDetector.NGramSortUnit;
import org.mai.dep806.volkoval.linguistic.spell.LDDNGramWordLinker;

import static org.mai.dep806.volkoval.linguistic.collocation.CollocationDetector.NGramSortUnit;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class LEXRetriever {

//    NGramProbabilityEstimator gramProbabilityEstimator;
    LikeHoodCollocationDetector gramDetector;
//    NGramProbabilityEstimator biGramProbabilityEstimator;
    LikeHoodCollocationDetector biGramDetector;
//    NGramProbabilityEstimator triGramProbabilityEstimator;
    LikeHoodCollocationDetector triGramDetector;
//    NGramStorage gramStorage;
//    NGramStorage biGramStorage;
//    NGramStorage triGramStorage;
//    WordStorage wordStorage;

    NGramModel model;

    LDDNGramWordLinker linker;


    double tau = 0.3;

    double delta = 0.5;

    double average = 0.0;
    int number = 0;


    public LEXRetriever(HeldOutNGramModel model) {
        this.model = model;

//        gramDetector = new LikeHoodCollocationDetector();
//        gramDetector.setStorage(model.getNGramStorages().get(0));
//        gramDetector.setStatistic(new CommonStatistic());
//        gramDetector.detect();

        biGramDetector = new LikeHoodCollocationDetector();
        biGramDetector.setStorage(model.getNGramStorages().get(1));
        biGramDetector.setStatistic(new CommonStatistic());
        biGramDetector.detect();

//        triGramDetector = new LikeHoodCollocationDetector();
//        triGramDetector.setStorage(model.getNGramStorages().get(2));
//        triGramDetector.setStatistic(new CommonStatistic());
//        triGramDetector.detect();

        linker = new LDDNGramWordLinker(model.getWordStorage());
    }

//    public LEXRetriever(NGramProbabilityEstimator gramProbabilityEstimator,
//                        NGramProbabilityEstimator biGramProbabilityEstimator,
//                        NGramProbabilityEstimator triGramProbabilityEstimator) {
//
//        wordStorage = gramProbabilityEstimator.getWordStorage();
//
//        this.gramProbabilityEstimator = gramProbabilityEstimator;
//        LinkedList<NGram> nGrams = new LinkedList<>();
////        gramStorage = new NGramMapStorage(NGram.NGramType.UNI_GRAM,
////                gramProbabilityEstimator.getTrainStorage().getWordStorage());
////        nGrams.addAll(gramProbabilityEstimator.getTrainStorage().getAllNGrams());
////        nGrams.addAll(gramProbabilityEstimator.getValidationStorage().getAllNGrams());
////        for (NGram nGram : nGrams) {
////            gramStorage.addNGram(nGram);
////        }
////        gramDetector = new LikeHoodCollocationDetector();
////        gramDetector.setStorage(gramStorage);
////        gramDetector.setStatistic(new CommonStatistic());
////        gramDetector.detect();
//
//        this.biGramProbabilityEstimator = triGramProbabilityEstimator;
//        nGrams = new LinkedList<>();
//        biGramStorage = new NGramMapStorage(NGram.NGramType.BI_GRAM,
//                wordStorage);
//        nGrams.addAll(biGramProbabilityEstimator.getTrainStorage().getAllNGrams());
//        nGrams.addAll(biGramProbabilityEstimator.getValidationStorage().getAllNGrams());
//        for (NGram nGram : nGrams) {
//            biGramStorage.addNGram(nGram);
//        }
//        biGramDetector = new LikeHoodCollocationDetector();
//        biGramDetector.setStorage(biGramStorage);
//        biGramDetector.setStatistic(new CommonStatistic());
//        biGramDetector.detect();
//
//        this.triGramProbabilityEstimator = triGramProbabilityEstimator;
////        nGrams = new LinkedList<>();
////        triGramStorage = new NGramMapStorage(NGram.NGramType.TRI_GRAM,
////                triGramProbabilityEstimator.getTrainStorage().getWordStorage());
////        nGrams.addAll(triGramProbabilityEstimator.getTrainStorage().getAllNGrams());
////        nGrams.addAll(triGramProbabilityEstimator.getValidationStorage().getAllNGrams());
////        triGramDetector = new LikeHoodCollocationDetector();
////        triGramDetector.setStorage(triGramStorage);
////        triGramDetector.setStatistic(new CommonStatistic());
////        triGramDetector.detect();
//    }

    public double getTau() {
        return tau;
    }

    public void setTau(double tau) {
        this.tau = tau;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    private List<List<String>> getUpperSeqs(List<String> tokens) {
        List<List<String>> resultSeqs = new ArrayList<>();
        List<String> tempResult = new ArrayList<>();
        boolean switcher = false;

        // for states for all cases new first character and previous first character
        for (String token : tokens) {
            if (Character.isUpperCase(token.charAt(0))) {
                if (switcher) {
                    tempResult.add(token);
                }
                else {
                    switcher = true;
                    tempResult.add(token);
                }
            }
            else {
                if (switcher) {
                    int index = tokens.indexOf(token);

                    if (index + 1 < tokens.size() &&
                            Character.isUpperCase(tokens.get(index + 1).charAt(0))) {
                        tempResult.add(token);
                    }
                    else {
                        switcher = false;
                        if (!tempResult.isEmpty()) {
                            resultSeqs.add(tempResult);
                            tempResult = new ArrayList<>();
                        }
                    }
                }
                else {
                    switcher = false;
                    if (!tempResult.isEmpty()) {
                        resultSeqs.add(tempResult);
                        tempResult = new ArrayList<>();
                    }
                }
            }
        }

        return resultSeqs;
    }

    private void printChunks(List<String> tokens, List<List<String>> upperSeqs) {
        System.out.println("line: ");
        for (String token : tokens) {
            System.out.print(token + " ");
        }
        System.out.println();
        System.out.println("chunks: ");
        for (List<String> seq : upperSeqs) {
            for (String name : seq) {
                System.out.print(name + " ");
            }
            System.out.println();
        }
    }

    public List<MWU> retrieveProperNames(List<String> tokens) throws UnsupposedArgumentException, UnsupposedTypeException {
        List<MWU> mwuList = new ArrayList<>();
        List<List<String>> upperSeqs = getUpperSeqs(tokens);
        CollocationDetector[] detectors = new CollocationDetector[] {
                null,
                gramDetector,
                biGramDetector,
                triGramDetector
        };

//        printChunks(tokens, upperSeqs);

        for (List<String> seq : upperSeqs) {
            if (seq.size() <= 4) {
                boolean truthSeqOne = true;
                boolean truthSeqTwo = true;

                for (int step = 1; step <= 2; ++step) {

//                    if (seq.size() == 1) {
//                        continue;
//                    }
                    for (int i = 0; i < seq.size(); i += step) {

                        if (i + step <= seq.size()) {
                            List<String> subSeq = seq.subList(i, i + step);
                            NGram        nGram  = model.getNGramStorages().get(step-1).getNGram(subSeq);

                            if (subSeq.size() == 1) {
                                truthSeqOne &= model.getProbability(subSeq) *
                                        linker.getEquivFreq(subSeq.get(0)) > 0.0001;
                            }
                            else if (step == 2) {
                                truthSeqOne &= detectors[step].getLatestResult().contains(
                                        new NGramSortUnit(0.0, nGram));

//                                if(detectors[step].getLatestResult().contains(
//                                        new NGramSortUnit(0.0, nGram))) {
////                                    System.out.println("Ist's here!!!!!!!!!!!!!!!!!!!!!!!!!");
//                                }
                            }

                            if (subSeq.size() > 2) {
                                for (int j = 0; j < subSeq.size() - 1; ++j) {
                                    if (Character.isLowerCase(subSeq.get(j).charAt(0))) {
                                        truthSeqTwo &=
                                                f(subSeq.get(j - 1), subSeq.get(j), subSeq.get(j + 1), 3) > tau;
                                    }
                                }
                            }
//                            model.getProbability(seq)
                        }
                    }



                    if (truthSeqOne && truthSeqTwo) {
                        mwuList.add(new MWU(seq));
                    }
                }
            }
        }

        return mwuList;
    }

//    public List<MWU> retrieveProperNames(List<String> tokens) throws UnsupposedTypeException, UnsupposedArgumentException {
//        List<MWU> mwuList = new ArrayList<>();
//        List<String> clearTokens;
//        int first = 0;
//        int last  = 0;
//        boolean isFirst = false;
//
//        // find first word with first character in upper case
//        for (int i = 0; i < tokens.size(); ++i) {
//            if (Character.isUpperCase(tokens.get(i).charAt(0))) {
//                first = i;
//                break;
//            }
//        }
//        // find last word with first character in upper case
//        for (int i = tokens.size() - 1; i >= 0; --i) {
//            if (Character.isUpperCase(tokens.get(i).charAt(0))) {
//                last  = i + 1;
//                break;
//            }
//        }
//        // if it's a one word, then skip it
//        if (first == last) {
////            mwuList.add(new MWU(
////                    Arrays.asList(new String[]{tokens.get(first)})));
//            return mwuList;
//        }
//        clearTokens = tokens.subList(first, last);
//        while (!clearTokens.isEmpty()) {
//            int iter = 0;
//            String prev;
//            List<String> subNames = new ArrayList<>();
//
//            while (true) {
//                do {
//                    prev = clearTokens.get(iter);
//                    subNames.add(prev);
//                    iter++;
//                } while (iter < clearTokens.size() && Character.isUpperCase(clearTokens.get(iter).charAt(0)));
//                // if next two words ion lower case,
//                // just skip it until meeting first word starting upper case
//                if (iter + 1 < clearTokens.size() && !Character.isUpperCase(clearTokens.get(iter + 1).charAt(0))) {
//                    if (!isFirst && first == 0 && subNames.size() == 1) {
//                        subNames.clear();
//                        isFirst = true;
//                    }
//                    if (!subNames.isEmpty()) {
//                        addToMWU(mwuList, subNames);
//                    }
//                    do {
//                        iter++;
//                    } while (iter < clearTokens.size() && !Character.isUpperCase(clearTokens.get(iter).charAt(0)));
//                    // and start to find next proper name
//                    clearTokens = clearTokens.subList(iter, clearTokens.size());
//                    break;
//                }
//
//                if (iter + 1 < clearTokens.size() && f(prev, clearTokens.get(iter), clearTokens.get(iter + 1), 3) > tau) {
//                    subNames.add(clearTokens.get(iter));
//                    subNames.add(clearTokens.get(iter + 1));
//                    iter += 2;
//                    clearTokens = clearTokens.subList(iter, clearTokens.size());
//                    addToMWU(mwuList, subNames);
//                    break;
//                }
//                else {
//                    if (iter != clearTokens.size()) {
//                        iter++;
//                    }
//                    clearTokens = clearTokens.subList(iter, clearTokens.size());
//                    if (!isFirst && first == 0 && subNames.size() == 1) {
//                        subNames.clear();
//                        isFirst = true;
//                    }
//                    if (!subNames.isEmpty()) {
//                        addToMWU(mwuList, subNames);
//                    }
//                    break;
//                }
//            }
//        }
//
//        return mwuList;
//    }

    private void addToMWU(List<MWU> mwuList, List<String> subNames) {
        if (!subNames.isEmpty()) {
            if (subNames.size() == 1) {
//                if (wordStorage.getWord(subNames.get(0)).getCount() <= 10) {
//                    mwuList.add(new MWU(subNames));
//                }
            }
//            else if (subNames.size() == 2) {
//                if (biGramDetector.getLatestResult().contains(biGramStorage.getNGram(subNames))) {
//                    mwuList.add(new MWU(subNames));
//                }
//            }
//            else if (subNames.size() == 3) {
//                if (triGramDetector.getLatestResult().contains(gramStorage.getNGram(subNames))) {
//                    mwuList.add(new MWU(subNames));
//                }
//            }
            else {
                mwuList.add(new MWU(subNames));
            }
        }
    }

//    private double f(String s0, String s1, String s2, int order) throws UnsupposedTypeException, UnsupposedArgumentException {
//        double numerator   = triGramProbabilityEstimator.getProbability(
//                Arrays.asList(new String[] { s0, s1, s2}));
//        double denominator = gramProbabilityEstimator.getProbability(Arrays.asList(new String[]{ s0 })) *
//                gramProbabilityEstimator.getProbability(Arrays.asList(new String[]{s1})) *
//                gramProbabilityEstimator.getProbability(Arrays.asList(new String[]{s2}));
//        double scpMeasure  = Math.exp(Math.log(numerator) * order) / denominator;
//
//        average += scpMeasure;
//        number++;
//
//        return scpMeasure;
//    }

    private double f(String s0, String s1, String s2, int order) throws UnsupposedTypeException, UnsupposedArgumentException {
        double numerator   = model.getProbability(
                Arrays.asList(new String[] { s0, s1, s2}));
        double denominator = model.getProbability(Arrays.asList(new String[]{ s0 })) *
                model.getProbability(Arrays.asList(new String[]{s1})) *
                model.getProbability(Arrays.asList(new String[]{s2}));
        double scpMeasure  = Math.exp(Math.log(numerator) * order) / denominator;

        average += scpMeasure;
        number++;

        return scpMeasure;
    }

    public double getAverage() {
        if (number == 0) {
            return 0;
        }

        return average / number;
    }
}
