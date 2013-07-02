package org.mai.dep806.volkoval.linguistic.model;

import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.ngram.NGramAnalyzer;
import org.mai.dep806.volkoval.linguistic.ngram.NGramStorage;
import org.mai.dep806.volkoval.linguistic.ngram.NGramUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 19.06.13
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class ModelNGramAnalyzer extends NGramAnalyzer {

    private NGramModel model;

    public ModelNGramAnalyzer(NGramStorage storage) {
        super(storage);
    }

    public void setModel(NGramModel model) {
        this.model = model;
    }

    @Override
    public double getNGramProbability(List<String> words) throws UnsupposedTypeException {
        if (NGramUtil.isSatisfied(words, model.getNGramType())) {
            return super.getNGramProbability(words);
        }
        else {
            if (model == null) {
                return 0;
            }

            return model.getProbability(
                    words.subList(0, words.size() - 1),
                    words.get(words.size() - 1));
        }
    }
}
