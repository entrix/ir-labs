package org.mai.dep806.volkoval.linguistic.collocation;

import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramUtil;
import org.mai.dep806.volkoval.linguistic.ngram.Word;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class PoissonDetector extends CollocationDetector {

    protected LinkedList<NGramSortUnit>  latestRelativeResult;

    @Override
    public void detect() {
        int n   = (int) statistic.getParameter("sentence.number");
        int N   = storage.getWordStorage().getWordCount();
        int freq;
        // significant Poisson measure
        Double sig;
        // relative Collocation measure
        Double relativeSig;
        // comparator for sorting
        Comparator comparator = new Comparator<NGramSortUnit>() {
            @Override
            public int compare(NGramSortUnit o1, NGramSortUnit o2) {
                return o2.coeff.compareTo(o1.coeff);
            }
        };

        latestResult         = new LinkedList<>();
        latestRelativeResult = new LinkedList<>();
        // number of the sentences present as a number of the experiments

        for (NGram nGram : storage.getAllNGrams()) {
            sig         = sig(nGram, 44, n, N);
            freq        = nGram.getWords().get(0).getCount();
            relativeSig = relativeSig(sig, freq);

            if (sig.isInfinite() || sig.isNaN() || relativeSig.isInfinite() || relativeSig.isNaN()) {
                continue;
            }

            latestResult.add(new NGramSortUnit(sig, nGram));
            latestRelativeResult.add(new NGramSortUnit(relativeSig, nGram));
        }


        Collections.sort(latestResult, comparator);
        Collections.sort(latestRelativeResult, comparator);
    }

    @Override
    public String getName() {
        return "Poisson";
    }

    private double sig(NGram nGram, int k, int n, int N) {
        Word w1 = nGram.getWords().get(0);
        Word w2 = nGram.getWords().get(1);

        double p1 = (double) w1.getCount() / N;
        double p2 = (double) w2.getCount() / N;
        double lambda = n * p1 * p2;

        return -k * (Math.log(k) - Math.log(lambda) - 1) / Math.log(n);
    }

    private double relativeSig(double sig, int freq) {
        return sig / freq;
    }

    public List<NGram> getRelativeTop(int n) {

        if (latestRelativeResult != null && !latestRelativeResult.isEmpty()) {
            int limit = latestRelativeResult.size() < n ?
                    latestRelativeResult.size() : n;

                List<NGram> resultList = new ArrayList<>(limit);
                int i = 0;

                for (NGramSortUnit nGramSortUnit : latestRelativeResult) {
                    NGramUtil.printNGram(nGramSortUnit.nGram);

                    System.out.print(nGramSortUnit.coeff + " -> ");
                    resultList.add(nGramSortUnit.nGram);
                    if (i == limit) {
                        break;
                    }
                    i++;
                }

                return resultList;
        }

        return new ArrayList<>();
    }
}
