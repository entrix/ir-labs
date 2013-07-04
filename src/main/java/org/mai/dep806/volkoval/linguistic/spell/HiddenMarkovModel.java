package org.mai.dep806.volkoval.linguistic.spell;

import org.mai.dep806.volkoval.StringUtil;
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

        double m = 0.0;

        for (int t = 0; t < n; ++t) {
            stateEmission.add(new ArrayList<Double>());
            symbolEmission.add(new ArrayList<Double>());
        }

        model.getProbability(StringUtil.asList("просто", "быть"));
        model.getProbability(StringUtil.asList("Жиль"));

        // here we are filling the State Emission Probability sequence
        for (int t = 0; t < n; ++t) {
                // compute P(Yj|Yi) = P(Yi,Yj) / P(Yi)
            if (t == 0) {
                for (String name : linker.getAllEquivalences(original.get(t))) {

                    double Pxt = model.getProbability(StringUtil.asList(name));


                    stateEmission.get(t).add(Pxt);
                }
            }
            else {
                iter = 0;
                for (String nameFirst : linker.getAllEquivalences(original.get(t - 1))) {
                    for (String nameSecond : linker.getAllEquivalences(original.get(t))) {

                        double Pxtt = model.getProbability(StringUtil.asList(nameFirst, nameSecond));
                        double Pxt  = model.getProbability(StringUtil.asList(nameFirst));


                        if (Pxtt / ((Pxt == 0) ? MIN : Pxt) > 1.0) {
                            System.out.printf("error: name: %15s %15s pxtt: %10f pxt: %10f\n", nameFirst, nameSecond,
                                    Pxtt, Pxt);
                            stateEmission.get(t).add(MIN);
                            Pxtt = model.getProbability(StringUtil.asList(nameFirst, nameSecond));
                        }
                        else {
                            stateEmission.get(t).add(Pxtt / ((Pxt == 0) ? MIN : Pxt));
                            if (m < Pxtt / ((Pxt == 0) ? MIN : Pxt)) {
                                m = Pxtt / ((Pxt == 0) ? MIN : Pxt);
//                                System.out.println("eq name: " + nameFirst + " " + nameSecond + " m: " + m);
                            }
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
                    double Pxt  = model.getProbability(StringUtil.asList(name));


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
//                        double Pxtt = model.getProbability(StringUtil.asList(nameFirst, nameSecond));

//                        double Poxt = model.getProbability(
//                                Arrays.asList(new String[]{ original.get(t), nameSecond }));
//                        double Pxt = model.getProbability(
//                                Arrays.asList(new String[]{ nameFirst, nameSecond }));

//                        System.out.println("eq names: " + nameSecond);
//                          if (m < (Pot * Poxt)) {
//                            m = (Pot * Poxt);
////                            System.out.println("eq name: " + nameFirst + " " + nameSecond);
//                          }
                        symbolEmission.get(t).add((Pot * Poxt));
                        iter++;
                    }
                }
            }
        }
    }

    public void computeChain() throws UnsupposedTypeException, UnsupposedArgumentException {
        // viterbi realization

        int T               = original.size();
        int lastMax         = 0;
        int[] deltaMax      = new int[T + 1];
        List<Double> deltas = new ArrayList<Double>();
        List<List<Integer>> phi = new ArrayList<>();

        // step 1. initialization
        for (int i = 0; i < T; ++ i) {
            phi.add(new ArrayList<Integer>());
        }
        for (String elem : linker.getAllEquivalences(original.get(0))) {
            deltas.add(model.getProbability(StringUtil.asList(elem)));
        }


        // step 2. induction
        for (int t = 1; t < T; ++t) {
            lastMax = getMax(phi, deltas, t - 1);
        }

        corrected = new ArrayList<>();

        // step 3
        deltaMax[T-1] = lastMax;
        for (int i = T - 1; i >= 1; --i) {
            deltaMax[i-1] = phi.get(i).get(deltaMax[i]);
        }

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

    private int getMax(List<List<Integer>> phi, List<Double> deltaPrev, int t) {
        int prevLayerSize = linker.getAllEquivalences(original.get(t)).size();
        int nextLayerSize = linker.getAllEquivalences(original.get(t + 1)).size();
        List<Double> deltaRes   = new ArrayList<>();
        List<Integer> deltaArgs = new ArrayList<>();
        double   max            = 0.0;
        double   current;
        int      argMax         = 0;
        int iter                = 0;

        for (int j = 0; j < nextLayerSize; ++j) {
            iter =  j;

            for (int i = 0; i < prevLayerSize; ++i) {
                current = deltaPrev.get(i) *
                        stateEmission.get(t + 1).get(iter) *
                        symbolEmission.get(t + 1).get(iter);
                iter += nextLayerSize;

                if (max < current) {
                    max    = current;
                    argMax = i % prevLayerSize;
                }
            }


            deltaRes.add(max);
            deltaArgs.add(argMax);
            max = 0.0;
        }

        // reinitialise deltaPrev
        deltaPrev.clear();
        argMax = 0;
        iter   = 0;
        for (double delta : deltaRes) {
            deltaPrev.add(delta);
            if (max < delta) {
                max    = delta;
                argMax = iter;
            }
            iter++;
        }

        // add phi store
        for (int arg : deltaArgs) {
            phi.get(t + 1).add(arg);
        }

        return argMax;
    }

    public List<String> correctedChain() {
        return corrected;
    }
}
