package org.mai.dep806.volkoval.linguistic.spell;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramProbabilityEstimator;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 01.07.13
 * Time: 19:50
 * To change this template use File | Settings | File Templates.
 */
public class HiddenMarkovModel {

    private final static int MAX_LENGTH = 100;

    private NGramModel model;

    private LDDNGramWordLinker linker;

    private List<Double[]> stateEmission;
    private List<Double[]> symbolEmission;

    private List<String> tokens;


    public HiddenMarkovModel(List<String> tokens, NGramModel model) throws UnsupposedArgumentException {
        int n = tokens.size();

        if (n == 0 || n >= MAX_LENGTH) {
            throw new UnsupposedArgumentException("numner of tokens is unacceptable");
        }

        stateEmission  = new ArrayList<>(n);
        symbolEmission = new ArrayList<>(n);
        this.model     = model;
        this.tokens    = tokens;
        linker         = new LDDNGramWordLinker(model.getWordStorage());

        for (int t = 0; t < n + 1; ++t) {
            symbolEmission.add(new Double[(n + 1) * (n + 1)]);
        }
    }

    public void initialize() throws UnsupposedTypeException, UnsupposedArgumentException {

        int n = stateEmission.size();

        // here we are filling the State Emission Probability matrix A
        for (int i = 1; i < n; ++i) {
            for (int j = 1; j < n; ++j) {
                // compute P(Yj|Yi) = P(Yi,Yj) / P(Yi)
                double Pij = model.getProbability(Arrays.asList(
                        new String[] {
                                hmm.(tokens.get(i)),
                                tokens.get(j) }));
                double Pj  = model.getProbability(Arrays.asList(new String[] { tokens.get(i) }));

                stateEmission[i*n + j] = Pij / Pj;
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
            Double[] symEmission = symbolEmission.get(t);

            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    // compute P(Oi|Yi,Yj) = P(Oi,Yi,Yj) / P(Yi,Yj)
                    double Pt  = linker.getEquivFreq(tokens.get(t));
                    double Ptj = storage.getNGram(Arrays.asList(
                            new String[] { tokens.get(t), tokens.get(j) })).getCount() /
                            storage.getNGramCount();
                    double Pij = model.getProbability(Arrays.asList(new String[] { tokens.get(i) }));

                    symEmission[i*n + j] = (Pt * Ptj) / Pij;
                }
            }
        }
    }

    public void computeChain() throws UnsupposedTypeException {
        // viterbi realization

        int n = tokens.size();
        int T = n;
        double[] deltaPrev = new double[n];
        double[] deltaNext = new double[n];

        // step 1. initialization
        for (int i = 0; i < n - 1; ++ i) {
            deltaPrev[i] = model.getProbability(tokens.subList(i, i + 1));
        }

        // step 2. induction
        for (int t = 0; t < T; ++t) {
            for (int j = 0; j < n; ++j) {
                deltaNext[j] = getMax(deltaPrev, t - 1, j);
            }
            deltaPrev = deltaNext;
        }

    }

    private double getMax(double[] deltaPrev, int t, int j) {
        int n        = tokens.size();
        double[] res = new double[n];
        double  max = 0.0;

        for (int i = 0; i < n; ++i) {
            res[i] = deltaPrev[i] * stateEmission[i*n + j] * symbolEmission.get(t)[i*n + j];

            if (max > res[i]) {
                max = res[i];
            }
        }

        return max;
    }
}
