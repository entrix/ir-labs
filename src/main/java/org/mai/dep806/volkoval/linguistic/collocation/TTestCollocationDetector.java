package org.mai.dep806.volkoval.linguistic.collocation;

import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramUtil;
import org.mai.dep806.volkoval.linguistic.ngram.Word;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 22.06.13
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
public class TTestCollocationDetector extends CollocationDetector {
    @Override
    public void detect() {

        int N = storage.getWordStorage().getWordCount();
        // likehood ratio
        Double t;
        double maxT = 0.0;
        NGram maxNgram = null;

        latestResult = new LinkedList<>();

        for (NGram nGram : storage.getAllNGrams()) {
            t  = tEval(nGram, N);

            if (t.isInfinite() || t.isNaN()) {
                continue;
            }

            if (t > 2.576) {
                latestResult.add(new NGramSortUnit(t, nGram));
            }

            if (t > maxT) {
                maxT = t;
                maxNgram = nGram;
            }
        }

        Collections.sort(latestResult, new Comparator<NGramSortUnit>() {
            @Override
            public int compare(NGramSortUnit o1, NGramSortUnit o2) {
                return o2.coeff.compareTo(o1.coeff);
            }
        });

//        System.out.println("MAX T: " + maxT);
//        NGramUtil.printNGram(maxNgram);
    }

    @Override
    public String getName() {
        return "t Test";
    }

    private double tEval(NGram nGram, int N) {
        Word w1 = nGram.getWords().get(0);
        Word w2 = nGram.getWords().get(1);

        int c1  = w1.getCount();
        int c2  = w2.getCount();
        int c12 = nGram.getCount();

        double x  = (double) c12 / N;
        double mu = (double) (c1 * c2) / (N * N);
        double t =  (x - mu) / Math.sqrt(x / N);

        return t;
    }

    private double evalBinomLog(int k, int n, double p) {
        return Math.log(Math.exp(k * Math.log(p)) * Math.exp((n - k) * Math.log(1.0 - p)));
    }
}
