package org.mai.dep806.volkoval.linguistic.experimental;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: AVVolkov
 * Date: 15.07.13
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class SimpleGeneratorTest {

    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(SimpleGenerator.class);

    private static Random random = new Random(System.currentTimeMillis());


    SimpleGenerator generator;
    String testFile = "auto.txt";

    @Before
    public void setUp() {
        FileReader reader = null;
        char[]     buf    = new char[10000];
        int counter = 0;

        try {
            generator = new SimpleGenerator(SimpleNGram.NGramType.UNI_GRAM);
            reader    = new FileReader(testFile);

            while (reader.ready() && counter < 10000) {
                buf[counter++] = (char) reader.read();

                if (counter == 10000) {
                    counter = flushToGenerator(buf, counter);
                }
            }
            flushToGenerator(buf, counter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (UnsupposedTypeException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    @Test
    public void fuzzyTest() {
        List<String> result;
        try {
            for (int i = 0; i < 1000; ++i) {
//                if (random.nextBoolean()) {
//                    System.out.print("generate sentence:");
//                    result = generator.generateSentence();
//                }
//                else {
                    System.out.print("generate phrase: ");
                    result = generator.generatePhrase();
//                }

                for (String word : result) {
                    System.out.print(word + ' ');
                }
                System.out.println();
            }


        } catch (UnsupposedArgumentException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (UnsupposedTypeException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    private int flushToGenerator(char[] buf, int counter) throws UnsupposedTypeException {
            int lastSeparator = counter - 1;

            while (buf[lastSeparator] == '.' || buf[lastSeparator] == '!' || buf[lastSeparator] == '?') {
                lastSeparator--;
            }

            List<List<String>> sentences = LinguaUtil.toSentences(Arrays.copyOfRange(buf, 0, counter));

            for (List<String> sentence : sentences) {
                generator.addFragment(sentence);
            }

            char[] newBuf = Arrays.copyOfRange(buf, lastSeparator, counter);

            for (int i = 0; i < newBuf.length; ++i) {
                buf[i] = newBuf[i];
            }

            return newBuf.length;
    }
}
