package org.mai.dep806.volkoval.linguistic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.mai.dep806.volkoval.StringUtil;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 21.06.13
 * Time: 12:47
 * To change this template use File | Settings | File Templates.
 */
public class    LinguaUtil {

    private static Logger logger = LogManager.getLogger(LinguaUtil.class);

    private static Random random = new Random(System.currentTimeMillis());


    private static Pattern punctuationPattern =
            Pattern.compile("[\\r\\n\\t\\\"\\'\\`\\,\\:\\;\\(\\)\\[\\]\\{\\}\\@\\#\\$\\%\\^\\&\\*\\=\\+]");
    private static String normalFormPattern = "\\|";
    private static String whiteSpacePattern = "\\ +";
    private static String parserDelimiterPattern = ":";

    private static LuceneMorphology luceneMorph;
    private static QueryParser russianParser;

    private static CommonStatistic statistic;

    private static List<Character> letters;


    private static boolean normalize = false;

    private static boolean precisionRank = false;


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

    public static boolean isNormalize() {
        return normalize;
    }

    public static void setNormalize(boolean normalize) {
        LinguaUtil.normalize = normalize;
    }

    public static boolean isPrecisionRank() {
        return precisionRank;
    }

    public static void setPrecisionRank(boolean precisionRank) {
        LinguaUtil.precisionRank = precisionRank;
    }

    public static List<List<String>> toSentences(char[] ch) {
        List<List<String>> sentences = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        List<String> items = new ArrayList<>();

        for (int i = 0; i < ch.length; ++i) {

//            if (!isCyrillic(ch[i]) ) {
//                while (i < ch.length && ch[i] != '.' && ch[i] != '?' && ch[i] != '!' && ch[i] != ' ') {
//                    i++;
//                }

                if (i == ch.length) {
                    return sentences;
                }
//            }

            if (Character.isUpperCase(ch[i])) {
                ch[i] = Character.toLowerCase(ch[i]);
            }
            if (ch[i] != '.' && ch[i] != '?' && ch[i] != '!') {
                builder.append(ch[i]);
            }
            else if (Character.isUpperCase(ch[i + 1]) || Character.isUpperCase(ch[i + 2]) ||
                    Character.isUpperCase(ch[i + 3]) && random.nextBoolean()) {
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
        String normalForm = word;

        try {
            normalForm = luceneMorph.getMorphInfo(word.toLowerCase()).get(0).split(normalFormPattern)[0];
        }
        catch (Exception e) {
            // do nothing
        }
//        try {
//            normalForm = russianParser.parse(normalForm).toString().split(parserDelimiterPattern)[1];
//        } catch (ParseException e) {
//            e.printStackTrace();
//            logger.error(e);
//        }

        return normalForm;
    }

    public static List<String> getRussianForm(String word) {
        List<String> normalForm = StringUtil.asList(word);

        try {
            normalForm = luceneMorph.getMorphInfo(word.toLowerCase());
        }
        catch (Exception e) {
            // do nothing
        }
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

    public static final List<String> stopWords = StringUtil.asList(new String[]{
            "-", "еще", "него", "сказать",
            "а", "ж", "нее", "со",
            "без", "же", "ней", "совсем",
            "более", "жизнь", "нельзя", "так",
            "больше", "за", "нет", "такой",
            "будет", "зачем", "ни", "там",
            "будто", "здесь", "нибудь", "тебя",
            "бы", "и", "никогда", "тем",
            "был", "из", "ним", "теперь",
            "была", "из-за", "них", "то",
            "были", "или", "ничего", "тогда",
            "было", "им", "но", "того",
            "быть", "иногда", "ну", "тоже",
            "в", "их", "о", "только",
            "вам", "к", "об", "том",
            "вас", "кажется", "один", "тот",
            "вдруг", "как", "он", "три",
            "ведь", "какая", "она", "тут",
            "во", "какой", "они", "ты",
            "вот", "когда", "опять", "у",
            "впрочем", "конечно", "от", "уж",
            "все", "которого", "перед", "уже",
            "всегда", "которые", "по", "хорошо",
            "всего", "кто", "под", "хоть",
            "всех", "куда", "после", "чего",
            "всю", "ли", "потом", "человек",
            "вы", "лучше", "потому", "чем",
            "г", "между", "почти", "через",
            "где", "меня", "при", "что",
            "говорил", "мне", "про", "чтоб",
            "да", "много", "раз", "чтобы",
            "даже", "может", "разве", "чуть",
            "два", "можно", "с", "эти",
            "для", "мой", "сам", "этого",
            "до", "моя", "свое", "этой",
            "другой", "мы", "свою", "этом",
            "его", "на", "себе", "этот",
            "ее", "над", "себя", "эту",
            "ей", "надо", "сегодня", "я",
            "ему", "наконец", "сейчас",
            "если", "нас", "сказал",
            "есть", "не", "сказала",
            "а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "к", "л", "м", "н",
            "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "э", "ь", "ы", "ю", "я",
            ".", ",", "-", "_", "=", "+", "/", "!", "\"", ";", ":", "%", "?", "*", "(", ")",
            "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "ноль",
    });
}
