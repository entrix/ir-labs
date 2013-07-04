package org.mai.dep806.volkoval.linguistic.spell;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;
import org.mai.dep806.volkoval.linguistic.ngram.NGramUtil;
import org.mai.dep806.volkoval.linguistic.ngram.Word;
import org.mai.dep806.volkoval.linguistic.ngram.WordStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 01.07.13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class SimpleSpellChecker implements SpellChecker  {

    private NGramModel model;

    private HiddenMarkovModel hmm;

    private final static int MAX_LENGTH = 100;

    public SimpleSpellChecker(NGramModel model) {
        this.model = model;
    }

    @Override
    public String getName() {
        return "Simple Spell Checker";
    }

    @Override
    public NGramModel getModel() {
        return model;
    }

    @Override
    public List<String> getCorrection(List<String> tokens) throws UnsupposedArgumentException, UnsupposedTypeException {
        if (tokens.size() == 0 || tokens.size() >= MAX_LENGTH) {
            throw new UnsupposedArgumentException("numner of tokens is unacceptable");
        }

        // processing
        if (LinguaUtil.isNormalize()) {
            List<String> normalizedTokens = new ArrayList<>();

            for (String token : tokens) {
                normalizedTokens.add(LinguaUtil.getRussianNormalForm(token));
            }
            tokens = normalizedTokens;
        }
        model.addNGrams(tokens);

        hmm = new HiddenMarkovModel(tokens, model);
        hmm.initialize();
        hmm.computeChain();

        return hmm.correctedChain();
    }

}
