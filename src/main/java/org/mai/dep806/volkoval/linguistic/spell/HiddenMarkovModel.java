package org.mai.dep806.volkoval.linguistic.spell;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramProbabilityEstimator;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 01.07.13
 * Time: 19:50
 * To change this template use File | Settings | File Templates.
 */
public class HiddenMarkovModel {

    private final static int MAX_LENGTH = 100;

    private final static double MIN = 0.0000000001;

    private NGramModel model;

    private LDDNGramWordLinker linker;

    private List<List<Double>> stateEmission;
    private List<List<Double>> symbolEmission;

    private List<String> original;
    private List<String> corrected;


    public HiddenMarkovModel(List<String> tokens, NGramModel model) throws UnsupposedArgumentException {
        int n = tokens.size();

        if (n == 0 || n >= MAX_LENGTH) {
            throw new UnsupposedArgumentException("numner of original is unacceptable");
        }

        stateEmission  = new ArrayList<>(n);
        symbolEmission = new ArrayList<>(n);
        this.model     = model;
        this.original = tokens;
        linker         = new LDDNGramWordLinker(model.getWordStorage());
    }

    public void initialize() throws UnsupposedTypeException, UnsupposedArgumentException {

        int n = original.size();

        int iter = 0;

        for (int t = 0; t < n; ++t) {
            stateEmission.add(new ArrayList<Double>());
            symbolEmission.add(new ArrayList<Double>());
        }

        // here we are filling the State Emission Probability sequence
        for (int t = 0; t < n; ++t) {
                // compute P(Yj|Yi) = P(Yi,Yj) / P(Yi)
            if (t == 0) {
                for (String name : linker.getAllEquivalences(original.get(t))) {

                    if (name.equals("лютая")) {
                        System.out.println();
                    }
                    double Pxt = model.getProbability(
                            Arrays.asList(new String[]{ name }));

                    stateEmission.get(t).add(Pxt);
                }
            }
            else {
                iter = 0;
                for (String nameFirst : linker.getAllEquivalences(original.get(t - 1))) {
                    for (String nameSecond : linker.getAllEquivalences(original.get(t))) {




                        double Pxtt = model.getProbability(
                                Arrays.asList(new String[]{ nameSecond, nameFirst }));
                        double Pxt = model.getProbability(
                                        Arrays.asList(new String[]{ nameFirst }));


                        if (Pxtt / ((Pxt == 0) ? MIN : Pxt) > 1.0) {
                            stateEmission.get(t).add(MIN);
                        }
                        else {
                            stateEmission.get(t).add(Pxtt / ((Pxt == 0) ? MIN : Pxt));
                        }
                        iter++;
                    }
                }
            }
        }

        // getting bigram storage
        NGramStorage storage = null;

        for (NGramProbabilityEstimator estimator : model.getEstimators()) {
            if (estimator.getTrainStorage().getNGramType() == NGram.NGramType.BI_GRAM) {
                storage = estimator.getTrainStorage();
            }
        }

        double m = 0.0;
//        int iter;

        // here we are filling the Symbol Emission Probability matrix B
        for (int t = 0; t < n; ++t) {
            // compute P(Yj|Yi) = P(Yi,Yj) / P(Yi)
            if (t == 0) {
                for (String name : linker.getAllEquivalences(original.get(t))) {
                    if (name.equals("лютая")) {
                        System.out.println();
                    }

                    double Pot  = linker.getEquivFreq(original.get(t));
                    double Poxt = linker.getEquivFreq(name);
//                    double Poxt = model.getProbability(
//                            Arrays.asList(new String[]{ original.get(t) }));
                    double Pxt  = model.getProbability(
                            Arrays.asList(new String[]{ name }));


//                    System.out.println("eq name: " + name);




                    symbolEmission.get(t).add((Pot * Poxt));
                }
            }
            else {
                for (String nameFirst : linker.getAllEquivalences(original.get(t - 1))) {
//                    iter = 0;
                    for (String nameSecond : linker.getAllEquivalences(original.get(t))) {

//                        if (nameSecond.equals("неприязнь")) {
//                             System.out.println();
//                        }

                        double Pot = linker.getEquivFreq(original.get(t));
                        double Poxt = linker.getEquivFreq(nameFirst) * linker.getEquivFreq(nameSecond);
//                        double Poxt = model.getProbability(
//                                Arrays.asList(new String[]{ original.get(t), nameSecond }));
//                        double Pxt = model.getProbability(
//                                Arrays.asList(new String[]{ nameFirst, nameSecond }));

//                        System.out.println("eq names: " + nameSecond);
                          if (m < (Pot * Poxt)) {
                            m = (Pot * Poxt);
//                            System.out.println("eq name: " + nameFirst + " " + nameSecond);
                          }
                        symbolEmission.get(t).add((Pot * Poxt));
                        iter++;
                    }
                }
            }
        }
    }

    public void computeChain() throws UnsupposedTypeException {
        // viterbi realization

        int T = original.size();
        int[] deltaMax = new int[T];

        // step 1. initialization
//        for (int i = 0; i < T; ++ i) {
//            deltaPrev[i] = model.getProbability(original.subList(i, i + 1));
//        }

        // step 2. induction
        for (int t = 0; t < T; ++t) {
                deltaMax[t] = getMax(t);
        }

        corrected = new ArrayList<>();

        for (int t = 0, i; t < T; ++t) {
            i = 0;
            for (String name : linker.getAllEquivalences(original.get(t))) {
                if (deltaMax[t] == i) {
                    corrected.add(name);
                }
                i++;
            }
        }
    }

    private int getMax(int t) {
        int n        = stateEmission.get(t).size();
        double[] res = new double[n];
        double  max  = 0.0;
        int argMax   = 0;

        for (int i = 0; i < n; ++i) {
            res[i] = stateEmission.get(t).get(i) * symbolEmission.get(t).get(i);

            if (max < res[i]) {
                max    = res[i];
                argMax = i;
            }
        }

        if (t > 0) {
            argMax %= linker.getAllEquivalences(original.get(t)).size();
        }
        return argMax;
    }

    public List<String> correctedChain() {
        return corrected;
    }
}
