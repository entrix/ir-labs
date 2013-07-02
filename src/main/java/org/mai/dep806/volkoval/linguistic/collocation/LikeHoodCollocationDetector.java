package org.mai.dep806.volkoval.linguistic.collocation;

import org.mai.dep806.volkoval.linguistic.CommonStatistic;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramUtil;
import org.mai.dep806.volkoval.linguistic.ngram.Word;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 19.06.13
 * Time: 3:54
 * To change this template use File | Settings | File Templates.
 */
public class LikeHoodCollocationDetector extends CollocationDetector {

    @Override
    public void detect() {

        int N = storage.getWordStorage().getWordCount();
        // likehood ratio
        Double lambda;
        double maxLambda = 0.0;
        NGram maxNgram = null;

        latestResult = new LinkedList<>();

        for (NGram nGram : storage.getAllNGrams()) {
            lambda  = lamdaEval(nGram, N);

            if (lambda.isInfinite() || lambda.isNaN()) {
                continue;
            }

            if (lambda > 7.88) {
                latestResult.add(new NGramSortUnit(lambda, nGram));
            }

            if (lambda > maxLambda) {
                maxLambda = lambda;
                maxNgram = nGram;
            }
        }

        Collections.sort(latestResult, new Comparator<NGramSortUnit>() {
            @Override
            public int compare(NGramSortUnit o1, NGramSortUnit o2) {
                return o2.coeff.compareTo(o1.coeff);
            }
        });

//        System.out.println("MAX LAMBDA: " + maxLambda);
//        NGramUtil.printNGram(maxNgram);
    }

    @Override
    public String getName() {
        return "Likehood Ratio";
    }

    private double lamdaEval(NGram nGram, int N) {
        Word w1 = nGram.getWords().get(0);
        Word w2 = nGram.getWords().get(1);

        int c1  = w1.getCount();
        int c2  = w2.getCount();
        int c12 = nGram.getCount();

        double p  = (double) c2 / N;
        double p1 = (double) c12 / c1;
        double p2 = (double) (c2 - c12) / (double) (N - c1);

        double lambda =
                evalBinomLog(c12, c1, p) +
                evalBinomLog(c2 - c12, N - c1, p) -
                evalBinomLog(c12, c1, p1) -
                evalBinomLog(c2 - c12, N - c1, p2);

        return -2.0 * lambda;
    }

    private double evalBinomLog(int k, int n, double p) {
        return Math.log(Math.exp(k * Math.log(p)) * Math.exp((n - k) * Math.log(1.0 - p)));
    }
}
