package org.mai.dep806.volkoval.linguistic.collocation;

import org.mai.dep806.volkoval.linguistic.CommonStatistic;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;
import org.mai.dep806.volkoval.linguistic.ngram.NGramUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 15.06.13
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class CollocationDetector {

    protected NGramStorage storage;
    protected LinkedList<NGramSortUnit> latestResult;
    protected CommonStatistic statistic;


    public void setStorage(NGramStorage storage) {
        this.storage = storage;
    }

    public void setStatistic(CommonStatistic statistic) {
        this.statistic = statistic;
    }

    public abstract void detect();

    public abstract String getName();

    public List<NGramSortUnit> getTop(int n) {

        if (latestResult != null && !latestResult.isEmpty()) {
            int limit = latestResult.size() < n ?
                    latestResult.size() : n;
            List<NGram> resultList = new ArrayList<>(limit);
            int i = 0;

            return latestResult.subList(0, limit);
        }

        return new ArrayList<>();
    }

    public class NGramSortUnit {
        Double coeff;
        NGram nGram;

        public NGramSortUnit(double coeff, NGram nGram) {
            this.coeff = coeff;
            this.nGram = nGram;
        }

        public Double getCoeff() {
            return coeff;
        }

        public NGram getnGram() {
            return nGram;
        }
    }

    public LinkedList<NGramSortUnit> getLatestResult() {
        return latestResult;
    }
}
