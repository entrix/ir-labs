package org.mai.dep806.volkoval.linguistic.experimental;

import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;


import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 19.06.13
 * Time: 2:41
 * To change this template use File | Settings | File Templates.
 */
public class NGramUtil {

    public static List<List<String>> getBigrams(List<String> tokens) throws UnsupposedTypeException {
        return groupWords(tokens, SimpleNGram.NGramType.BI_GRAM);
    }

    public static List<List<String>> geTrigrams(List<String> tokens) throws UnsupposedTypeException {
        return groupWords(tokens, SimpleNGram.NGramType.TRI_GRAM);
    }

    public static List<List<String>> groupWords(List<String> tokens, SimpleNGram.NGramType type) throws UnsupposedTypeException {
        List<List<String>> nGrams = new ArrayList<>();
        int len = getLengthByType(type);

        for (int i = 0; i <= tokens.size() - len; ++i) {

            for (int k = i; k <= tokens.size() - len; ++k) {
                List<String> nGram = new ArrayList<>();

                // add i-th word
                nGram.add(tokens.get(i));
                // and then get all nGram combinations with i-th word
                for (int j = k + 1; j < k + len; ++j) {
                    nGram.add(tokens.get(j));
                }
                nGrams.add(nGram);
            }
        }

        return nGrams;
    }

    public static SimpleNGram.NGramType getTypeByLength(int len) throws UnsupposedTypeException {
        switch (len) {
            case 1:
                return SimpleNGram.NGramType.UNI_GRAM;
            case 2:
                return SimpleNGram.NGramType.BI_GRAM;
            case 3:
                return SimpleNGram.NGramType.TRI_GRAM;
            default:
                throw new UnsupposedTypeException("unknown type of nGrams lenght " + len);
        }
    }

    public static int getLengthByType(SimpleNGram.NGramType type) throws UnsupposedTypeException {

        switch (type) {
            case UNI_GRAM:
                return 1;
            case BI_GRAM:
                return 2;
            case TRI_GRAM:
                return 3;
            default:
                throw new UnsupposedTypeException("unknown type of nGrams  " + type);
        }
    }

    public static boolean isSatisfied(List<String> words, SimpleNGram.NGramType type) {
        try {
            return getTypeByLength(words.size()) == type;
        } catch (UnsupposedTypeException e) {
            return false;
        }
    }

    public static void printSimpleNGram(SimpleNGram nGram) {

        if (nGram == null) {
            return;
        }

        System.out.print("count: " + nGram.getCount() + " ");
        for (String word : nGram.getWords()) {
            System.out.print(word + " ");
        }
        System.out.println();
    }

    public static SimpleNGram.NGramType getNextType(SimpleNGram.NGramType nGramType) throws UnsupposedArgumentException {
        switch (nGramType) {
            case ZERO:
                return SimpleNGram.NGramType.UNI_GRAM;
            case UNI_GRAM:
                return SimpleNGram.NGramType.BI_GRAM;
            case BI_GRAM:
                return SimpleNGram.NGramType.TRI_GRAM;
            default:
                throw new UnsupposedArgumentException("Not allowed 4th gramm there");
        }
    }
}
