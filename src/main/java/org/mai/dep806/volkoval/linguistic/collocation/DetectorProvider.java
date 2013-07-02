package org.mai.dep806.volkoval.linguistic.collocation;

import org.mai.dep806.volkoval.linguistic.CommonStatistic;
import org.mai.dep806.volkoval.linguistic.ngram.NGram;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class DetectorProvider {

    private NGramStorage storage;

    private CommonStatistic statistic;

    private List<CollocationDetector> detectors = new ArrayList<>();

    public DetectorProvider(NGramStorage storage, CommonStatistic statistic) {
        this.storage = storage;
        this.statistic = statistic;
    }

    public void addDetector(CollocationDetector detector) {
        detector.setStorage(storage);
        detector.setStatistic(statistic);
        detectors.add(detector);
    }

    public void removeDetector(CollocationDetector detector) {
        detectors.remove(detector);
    }

    public List<CollocationDetector> getDetectors() {
        return detectors;
    }


    public void detectCollocations() {
        for (CollocationDetector detector : detectors) {
            detector.detect();
        }
    }
}
