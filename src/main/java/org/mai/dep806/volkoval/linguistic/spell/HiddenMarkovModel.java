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

    private List<Double>[] stateEmission;
    private List<Double>[] symbolEmission;

    private List<String> original;
    private List<String> corrected;


    public HiddenMarkovModel(List<String> tokens, NGramModel model) throws UnsupposedArgumentException {
        int n = tokens.size();

        if (n == 0 || n >= MAX_LENGTH) {
            throw new UnsupposedArgumentException("numner of original is unacceptable");
        }

        stateEmission  = new List[n];
        symbolEmission = new List[n];
        this.model     = model;
        this.original = tokens;
        linker         = new LDDNGramWordLinker(model.getWordStorage());
    }

    public void initialize() throws UnsupposedTypeException, UnsupposedArgumentException {

        int n = stateEmission.length;

        for (int t = 0; t < n; ++t) {
            stateEmission[t]  = new ArrayList<Double>();
            symbolEmission[t] = new ArrayList<Double>();
        }

        // here we are filling the State Emission Probability sequence
        for (int t = 0; t < n; ++t) {
                // compute P(Yj|Yi) = P(Yi,Yj) / P(Yi)
            if (t == 0) {
                for (String name : linker.getAllEquivalences(original.get(t))) {

                    double Pxt = model.getProbability(
                            Arrays.asList(new String[]{ name }));

                    stateEmission[t].add(Pxt);
                }
            }
            else {
                for (String nameFirst : linker.getAllEquivalences(original.get(t - 1))) {
                    for (String nameSecond : linker.getAllEquivalences(original.get(t))) {

                        double Pxtt = model.getProbability(
                                Arrays.asList(new String[]{ nameSecond, nameFirst }));
                        double Pxt = model.getProbability(
                                        Arrays.asList(new String[]{ nameFirst }));

                        stateEmission[t].add(Pxtt / ((Pxt == 0) ? MIN : Pxt));
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

        // here we are filling the Symbol Emission Probability matrix B
        for (int t = 0; t < n; ++t) {
            // compute P(Yj|Yi) = P(Yi,Yj) / P(Yi)
            if (t == 0) {
                for (String name : linker.getAllEquivalences(original.get(t))) {

                    double Pot = linker.getEquivFreq(original.get(t));
                    double Poxt = model.getProbability(
                            Arrays.asList(new String[]{ original.get(t) }));
                    double Pxt = model.getProbability(
                            Arrays.asList(new String[]{ name }));

                    symbolEmission[t].add((Pot * Poxt) / ((Pxt == 0) ? MIN : Pxt));
                }
            }
            else {
                for (String nameFirst : linker.getAllEquivalences(original.get(t - 1))) {
                    for (String nameSecond : linker.getAllEquivalences(original.get(t))) {

                        double Pot = linker.getEquivFreq(original.get(t));
                        double Poxt = model.getProbability(
                                Arrays.asList(new String[]{ original.get(t), nameSecond }));
                        double Pxt = model.getProbability(
                                Arrays.asList(new String[]{ nameFirst, nameSecond }));

                        symbolEmission[t].add((Pot * Poxt) / ((Pxt == 0) ? MIN : Pxt));
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
        for (int t = 1; t < T; ++t) {
                deltaMax[t] = getMax(t - 1);
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
        int n        = stateEmission[t].size();
        double[] res = new double[n];
        double  max  = 0.0;
        int argMax   = 0;

        for (int i = 0; i < n; ++i) {
            res[i] = stateEmission[t].get(i) * symbolEmission[t].get(i);

            if (max > res[i]) {
                max    = res[i];
                argMax = i;
            }
        }

        return argMax;
    }

    public List<String> correctedChain() {
        return corrected;
    }
}
