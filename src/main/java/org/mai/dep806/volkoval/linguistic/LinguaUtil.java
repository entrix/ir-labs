package org.mai.dep806.volkoval.linguistic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 21.06.13
 * Time: 12:47
 * To change this template use File | Settings | File Templates.
 */
public class LinguaUtil {

    private static Logger logger = LogManager.getLogger(LinguaUtil.class);

    private static Pattern punctuationPattern =
            Pattern.compile("[\\\"\\'\\`\\,\\:\\;\\(\\)\\[\\]\\{\\}\\@\\#\\$\\%\\^\\&\\*\\=\\+]");
    private static String normalFormPattern = "\\|";
    private static String whiteSpacePattern = "\\ +";
    private static String parserDelimiterPattern = ":";

    private static LuceneMorphology luceneMorph;
    private static QueryParser russianParser;

    private static CommonStatistic statistic;

    private static List<Character> letters;


    private static boolean normalize = false;

    public static boolean isNormalize() {
        return normalize;
    }

    public static void setNormalize(boolean normalize) {
        LinguaUtil.normalize = normalize;
    }

    static {
        letters = new ArrayList<>();
        letters.add('б');
        letters.add('в');
        letters.add('г');
        letters.add('д');
        letters.add('е');
        letters.add('ж');
        letters.add('з');
        letters.add('к');
        letters.add('л');
        letters.add('м');
        letters.add('й');
        letters.add('н');
        letters.add('п');
        letters.add('р');
        letters.add('с');
        letters.add('т');
        letters.add('у');
        letters.add('ф');
        letters.add('х');
        letters.add('ц');
        letters.add('ч');
        letters.add('ш');
        letters.add('щ');
        letters.add('а');
        letters.add('е');
        letters.add('и');
        letters.add('о');
        letters.add('у');
        letters.add('ы');
        letters.add('э');
        letters.add('ю');
        letters.add('я');

        luceneMorph = null;
        try {
            RussianAnalyzer ru_an = new RussianAnalyzer();

            luceneMorph = new RussianLuceneMorphology();
            russianParser = new QueryParser(Version.LUCENE_30, "any_string", ru_an);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public static List<List<String>> toSentences(char[] ch) {
        List<List<String>> sentences = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        List<String> items = new ArrayList<>();


        for (int i = 0; i < ch.length; ++i) {

            if (!isCyrillic(ch[i])) {
                while (i < ch.length && ch[i] != '.' && ch[i] != '?' && ch[i] != '!' && ch[i] != ' ') {
                    i++;
                }

                if (i == ch.length) {
                    return sentences;
                }
            }
            if (ch[i] != '.' && ch[i] != '?' && ch[i] != '!') {
                builder.append(ch[i]);
            }
            else {
                if (statistic != null) {
                    statistic.setParameter("sentence.number", (int)statistic.getParameter("sentence.number") + 1);
                }

                String sentence = punctuationPattern.matcher(builder.toString()).replaceAll("");

                sentence = sentence.replaceAll(whiteSpacePattern, " ").trim();

                for (String str : sentence.split(" ")) {
                    if (!str.isEmpty()) {
                        items.add(str);
                    }
                }

                sentences.add(new ArrayList<String>(items));
                items.clear();
                builder = new StringBuilder();
            }
        }

        return sentences;
    }

    public static String getRussianNormalForm(String word) {

        String normalForm = luceneMorph.getMorphInfo(word.toLowerCase()).get(0).split(normalFormPattern)[0];

//        try {
//            normalForm = russianParser.parse(normalForm).toString().split(parserDelimiterPattern)[1];
//        } catch (ParseException e) {
//            e.printStackTrace();
//            logger.error(e);
//        }

        return normalForm;
    }

    public static boolean isCyrillic(char c) {
        return Character.UnicodeBlock.CYRILLIC.equals(Character.UnicodeBlock.of(c));
    }

    public static CommonStatistic getStatistic() {
        return statistic;
    }

    public static void setStatistic(CommonStatistic statistic) {
        LinguaUtil.statistic = statistic;
    }

    public static Set<String> getLevensteinDamerauDisplacement(String seed) {
        Set<String> disSet = new TreeSet<>();

        disSet.add(seed);

        for (int i = 0; i < seed.length(); ++i) {
            String prev = "";

            if (i >= 1) {
                prev = seed.substring(0, i);
            }

            if (letters.contains(seed.charAt(i))) {
                for (Character ch : letters) {
                    if (seed.charAt(i) != ch) {
                        disSet.add(prev + String.valueOf(ch) + seed.substring(i + 1, seed.length()));
                    }
                }
            }
//            else if (letters.contains(seed.charAt(i))) {
//                for (Character ch : letters) {
//                    if (seed.charAt(i) != ch) {
//                        disSet.add(prev + String.valueOf(ch) + seed.substring(i + 1, seed.length()));
//                    }
//                }
//            }
            // deletion
            disSet.add(prev + seed.substring(i + 1, seed.length()));

            // insertion
            for (Character ch : letters) {
                disSet.add(prev + String.valueOf(ch) + seed.substring(i, seed.length()));
            }
//            for (Character ch : letters) {
//                disSet.add(prev + String.valueOf(ch) + seed.substring(i, seed.length()));
//            }

            if (i > 0) {
                prev = "";
                if (i > 1) {
                    prev = seed.substring(0, i - 1);
                }
                disSet.add(prev + String.valueOf(seed.charAt(i)) +
                        String.valueOf(seed.charAt(i - 1)) + seed.substring(i + 1, seed.length()));
            }

        }

        return disSet;
    }
}
