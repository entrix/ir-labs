package org.mai.dep806.volkoval.linguistic.model;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.CommonStatistic;
import org.mai.dep806.volkoval.linguistic.ngram.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 12:41
 * To change this template use File | Settings | File Templates.
 */
public class HeldOutNGramModel implements NGramModel {

    // NGram probability estimators
    private List<HeadOutNGramProbabilityEstimator> nGramProbabilityEstimators;

    private NGram.NGramType nGramBaseType;
    private int level;

    private ModelMode mode = ModelMode.TRAIN;
    // if we added any data thea we must recompute statistic
    private boolean modified = true;

    public static NGram lastNfGram;

    public static NGram lastsNGram;

    static int itr = 0;

    public HeldOutNGramModel(NGram.NGramType nGramType)
            throws UnsupposedArgumentException, UnsupposedTypeException {
        this.nGramBaseType = nGramType;
        this.level         = 1;
        initialze();
    }

    public HeldOutNGramModel(NGram.NGramType nGramType, int level)
            throws UnsupposedArgumentException, UnsupposedTypeException {
        this.nGramBaseType = nGramType;
        this.level         = level;
        initialze();
    }

    public HeldOutNGramModel(List<NGramProbabilityEstimator> estimators) {
        this.nGramProbabilityEstimators = new ArrayList<>();
        for (NGramProbabilityEstimator estimator : estimators) {
            nGramProbabilityEstimators.add((HeadOutNGramProbabilityEstimator) estimator);
        }
        this.nGramBaseType = estimators.get(0).getTrainStorage().getNGramType();
    }

    @Override
    public String getName() {
        return "Held Out Model";
    }

    public void addNGrams(List<String> tokens) throws UnsupposedTypeException {
        for (HeadOutNGramProbabilityEstimator estimator : nGramProbabilityEstimators) {
            switch (mode) {
                case TRAIN:
                    estimator.addToTrain(tokens);
                    break;
                case VALIDATION:
                    estimator.addToValidation(tokens);
                    break;
            }
        }
    }

    @Override
    public double getProbability(List<String> prev, String next)
            throws UnsupposedTypeException, UnsupposedArgumentException {
        if (modified) {
            refreshStatistics();
            modified = false;
        }

        HeadOutNGramProbabilityEstimator estimator;
        double numerProbability = 1.0;
        double denomProbability = 0.0;
        List<String> full = new ArrayList<>(prev);

        full.add(next);
        for (int i = 0; i < nGramProbabilityEstimators.size(); ++i) {
            estimator = nGramProbabilityEstimators.get(i);

            if (estimator.getTrainStorage().getNGramType() == NGramUtil.getTypeByLength(prev.size())) {
                denomProbability  = estimator.getProbability(prev);
                estimator = nGramProbabilityEstimators.get(i + 1);
                numerProbability *= estimator.getProbability(full);
            }
        }

        return numerProbability / denomProbability;
    }

    @Override
    public double getProbability(List<String> tokens) throws UnsupposedTypeException, UnsupposedArgumentException {
        NGram.NGramType type = NGramUtil.getTypeByLength(tokens.size());

        if (type == NGram.NGramType.UNI_GRAM) {
            WordStorage storage = nGramProbabilityEstimators.get(0).getWordStorage();

            return (double) (storage.getWordCount(tokens.get(0)) == 0 ?
                    1 : storage.getWordCount(tokens.get(0))) / storage.getWordCount();
        }

        for (NGramProbabilityEstimator estimator : nGramProbabilityEstimators) {
            if (type == estimator.getTrainStorage().getNGramType()) {
                return estimator.getProbability(tokens);
            }
        }

        throw new UnsupposedTypeException("type of ngramm isn't supported by this model");
    }

    @Override
    public WordStorage getWordStorage() {
        return nGramProbabilityEstimators.get(0).getWordStorage();
    }

    @Override
    public List<NGramStorage> getNGramStorages() {
        List<NGramStorage> storages = new ArrayList<>();

        for (NGramProbabilityEstimator estimator : nGramProbabilityEstimators) {
            storages.add(estimator.getTrainStorage());
        }

        return storages;
    }

    @Override
    public void refreshStatistics() throws UnsupposedArgumentException, UnsupposedTypeException {
        for (HeadOutNGramProbabilityEstimator estimator : nGramProbabilityEstimators) {
            estimator.computeStatistics();
        }
    }

    @Override
    public NGram.NGramType getNGramType() {
        return nGramBaseType;
    }

    public List<HeadOutNGramProbabilityEstimator> getEstimators() {
        return nGramProbabilityEstimators;
    }

    public void setnGramProbabilityEstimators(List<HeadOutNGramProbabilityEstimator> nGramProbabilityEstimators) {
        this.nGramProbabilityEstimators = nGramProbabilityEstimators;
    }

    @Override
    public double f(int r) {
        HeadOutNGramProbabilityEstimator estimator = nGramProbabilityEstimators.get(0);

        int nr = estimator.getNr(r);
        int tr = estimator.getTr(r);

        return (double) nr / tr;
    }

    public void clear() throws UnsupposedArgumentException, UnsupposedTypeException {
        initialze();
    }

    @Override
    public NGramModelType getType() {
        return NGramModelType.HELD_OUT;
    }

    public ModelMode getMode() {
        return mode;
    }

    public void setMode(ModelMode mode) {
        if (this.mode == mode) {
            return;
        }

        this.mode = mode;
    }

    private void initialze() throws UnsupposedArgumentException, UnsupposedTypeException {
        WordMapStorage storage = new WordMapStorage();
        NGram.NGramType currentType = nGramBaseType;
        HeadOutNGramProbabilityEstimator currentEtimator;

        nGramProbabilityEstimators = new ArrayList<>();
        currentEtimator = new HeadOutNGramProbabilityEstimator(currentType);
        currentEtimator.initialize(storage);
        nGramProbabilityEstimators.add(currentEtimator);
        for (int i = 0; i < level; ++i) {
            currentType     = NGramUtil.getNextType(currentType);
            currentEtimator = new HeadOutNGramProbabilityEstimator(currentType);
            currentEtimator.initialize(storage);
            nGramProbabilityEstimators.add(currentEtimator);
        }
    }

    public static enum ModelMode {
        TRAIN, VALIDATION;
    }

    public static class HeadOutNGramProbabilityEstimator implements NGramProbabilityEstimator {

        private NGram.NGramType type;
        // NGram training proecssor
        private NGramProcessor trainingProcessor;
        // NGram training proecssor
        private NGramProcessor validationProcessor;

        private Map<Integer, Integer> nrMap;
        private Map<Integer, Integer> trMap;
        private Map<Integer, List<NGram>> nrNGramMap;


        public HeadOutNGramProbabilityEstimator(NGram.NGramType type) {
            this.type = type;
        }

        @Override
        public void initialize(WordStorage storage) throws UnsupposedArgumentException, UnsupposedTypeException {
            trainingProcessor = new NGramProcessor(
                    new CommonStatistic(),
                    new NGramFactory(new NGramMapStorage(type, storage)),
                    NGramUtil.getLengthByType(type));
            validationProcessor = new NGramProcessor(
                    new CommonStatistic(),
                    new NGramFactory(new NGramMapStorage(type, storage)),
                    NGramUtil.getLengthByType(type));
        }

        @Override
        public void computeStatistics() throws UnsupposedArgumentException, UnsupposedTypeException {
            // computing Nr coefficients
            nrNGramMap = new HashMap<>();
            for (NGram nGram : trainingProcessor.getNGramFactory().getStorage().getAllNGrams()) {
                if (!nrNGramMap.containsKey(nGram.getCount())) {
                    nrNGramMap.put(nGram.getCount(), new ArrayList<NGram>());
                }
                nrNGramMap.get(nGram.getCount()).add(nGram);
            }
            // computing Nr and Tr coefficients
            nrMap = new HashMap<>(nrNGramMap.size());
            trMap = new HashMap<>(nrNGramMap.size());
            for (Map.Entry<Integer, List<NGram>> nrEntry : nrNGramMap.entrySet()) {
                int sum = 0;
                // add values to nrMap
                nrMap.put(nrEntry.getKey(), nrEntry.getValue().size());
                // add values to trMap
                for (NGram nGram : nrEntry.getValue()) {
                    NGram vNGram = validationProcessor.getNGramFactory().getStorage().getNGram(nGram);
                    // summarize frequencies of thw one-frquency ngrams from training in validation set
                    sum += vNGram.getCount();
                }
                trMap.put(nrEntry.getKey(), sum);
            }
            if (!nrMap.containsKey(0)) {
                nrMap.put(0, 100000);
            }
            if (!trMap.containsKey(0)) {
                trMap.put(0, 1);
            }
            nrNGramMap.clear();
        }

        @Override
        public int getNr(List<String> tokens) throws UnsupposedTypeException, UnsupposedArgumentException {
            NGram nGram = trainingProcessor.getNGramFactory().createNGram(tokens);
            int r       = nGram.getCount();

            if (!nrMap.containsKey(r)) {
                do {
                    if (r == 0) {
                        return 1;
                    }
                    r--;
                } while (!nrMap.containsKey(r));
            }

            return nrMap.get(r);
        }

        @Override
        public int getTr(List<String> tokens) throws UnsupposedTypeException, UnsupposedArgumentException {
            NGram nGram = trainingProcessor.getNGramFactory().createNGram(tokens);
            int r       = nGram.getCount();

            if ((itr % 2) == 0) {
                lastNfGram = nGram;
                itr = 1;
            }
            else {
                lastsNGram = nGram;
                itr = 0;
            }

            if (!trMap.containsKey(r)) {
                do {
                    if (r == 0) {
                        return 1;
                    }
                    r--;
                } while (!trMap.containsKey(r));
            }

            return trMap.get(r);
        }

        @Override
        public int getNr(int r) {
            return (nrMap.get(r) == null) ?
                    0 : nrMap.get(r);
        }

        @Override
        public int getTr(int r) {
            return (trMap.get(r) == null) ?
                    1 : trMap.get(r);
        }

        @Override
        public int getN() {
            return trainingProcessor.getNGramFactory().getStorage().getNGramCount();
        }

        @Override
        public void addToTrain(List<String> tokens) throws UnsupposedTypeException {
            trainingProcessor.addNGrams(tokens);
        }

        @Override
        public void addToValidation(List<String> tokens) throws UnsupposedTypeException {
            validationProcessor.addNGrams(tokens);
        }

        @Override
        public double getProbability(List<String> tokens) throws UnsupposedTypeException, UnsupposedArgumentException {
            double nr = getNr(tokens);
            double tr = getTr(tokens);
            double n  = getN();

            return tr / (n * nr);
        }

        @Override
        public NGramStorage getTrainStorage() {
            return trainingProcessor.getNGramFactory().getStorage();
        }

        @Override
        public NGramStorage getValidationStorage() {
            return validationProcessor.getNGramFactory().getStorage();
        }

        @Override
        public WordStorage getWordStorage() {
            return trainingProcessor.getNGramFactory().getStorage().getWordStorage();
        }
    }
}
