package org.mai.dep806.volkoval;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.data.CounterDataHandler;
import org.mai.dep806.volkoval.data.DataHandler;
import org.mai.dep806.volkoval.data.DataRetriever;
import org.mai.dep806.volkoval.data.SIngleThreadDataRetriever;
import org.mai.dep806.volkoval.exception.UnsupposedArgumentException;
import org.mai.dep806.volkoval.exception.UnsupposedTypeException;
import org.mai.dep806.volkoval.lab.*;
import org.mai.dep806.volkoval.linguistic.CommonStatistic;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.mai.dep806.volkoval.linguistic.model.HeldOutNGramModel;
import org.mai.dep806.volkoval.linguistic.model.NGramModel;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;

import static java.lang.System.out;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 11.06.13
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class Labs {

    private static Logger logger = LogManager.getLogger("MainLogger");

    public static void main(String[] args) {

        logger.info("start labs");

        if (args.length < 2) {
            usage();
        }

        Set<Integer> labs                 = new LinkedHashSet<>();
        List<String> fileList             = new ArrayList<>();
        Map<String, List<String>> subArgs = new HashMap<>();

        for (int i = 0; i < args.length; ++i) {

            if (args[i].equals("-lab")) {
                if (args.length == i + 1) {
                    usage();
                }

                String lab = args[i+1];

                switch (lab) {
                    case "1":
                        labs.add(1);
                        break;
                    case "2":
                        labs.add(2);
                        break;
                    case "3":
                        labs.add(3);
                        break;
                    case "4":
                        labs.add(4);
                        break;
                    default:
                        usage();
                        break;
                }

                i++;
            }
            else if (args[i].substring(0, 1).equals("-")) {
                String name          = args[i].substring(1, args[i].length());
                List<String> argList = new ArrayList<>();

                i++;
                while (i < args.length && !args[i].substring(0, 1).equals("-")) {
                    argList.add(args[i]);
                    i++;
                }
                subArgs.put(name, argList);
                i--;
            }
        }

        executeLabs(labs, subArgs);

        logger.info("successfully end labs");
    }

    private static void executeLabs(Set<Integer> labs, Map<String, List<String>> argsMap) {

        AbstractLab lab = null;

        for (Integer labNumber : labs) {

            switch (labNumber) {
                case 1:
                    lab = new FirstLab();
                    break;
                case 2:
                    lab = new SecondLab();
                    break;
                case 3:
                    lab = new ThirdLab();
                    break;
                case 4:
                    lab = new FourthLab();
                    break;
            }

            try {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                SAXParser saxParser = spf.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();
                DataRetriever retriever = new SIngleThreadDataRetriever();

                xmlReader.setContentHandler((SIngleThreadDataRetriever) retriever);

                int top   = 30;
                int ratio;
                int iter  = 0;

                double precisionRank = 1.0;
                HashMap<String, Integer> wordCounts = new HashMap<>();

                CommonStatistic statistic = new CommonStatistic();

                if (argsMap.containsKey("precisionrank") && labNumber != 3) {
                    LinguaUtil.setPrecisionRank(true);
                    precisionRank = Double.valueOf(argsMap.get("precisionrank").get(0));

                    DataHandler counter = new CounterDataHandler();

                    retriever.addDataHandler(counter);
                    for (String filename : argsMap.get("file")) {
                        ((CounterDataHandler) counter).toZero();
                        xmlReader.parse(convertToFileURL(filename));
                        wordCounts.put(filename,
                                (int) (((CounterDataHandler) counter).getCounts() * precisionRank));
                    }
                }

                lab.setDataRetriever(retriever);

                if (argsMap.containsKey("top")) {
                    top = Integer.parseInt(argsMap.get("top").get(0));
                }

                if (argsMap.containsKey("ratio")) {
                    ratio = (int) Math.round(argsMap.get("file").size()
                            * Double.valueOf(argsMap.get("ratio").get(0)));
                }
                else {
                    ratio = argsMap.get("file").size() / 2;
                }

                if (argsMap.containsKey("normalize")) {
                    LinguaUtil.setNormalize(true);
                }

                if (labNumber == 1) {
                    List<String> detectors = new ArrayList<>();

                    if (argsMap.containsKey("detector")) {
                        detectors = argsMap.get("detector");
                    }
                    ((FirstLab) lab).setDetectors(detectors);
                }
                else if (labNumber == 2) {
                    List<String> estimators = new ArrayList<>();

                    if (argsMap.containsKey("estimator")) {
                        estimators = argsMap.get("estimator");
                    }
                    ((SecondLab) lab).setEstimators(estimators);
                }
                else if (labNumber == 4) {
                    List<String> estimators = new ArrayList<>();

                    if (argsMap.containsKey("estimator")) {
                        estimators = argsMap.get("estimator");
                    }
                    ((FourthLab) lab).setEstimators(estimators);

                    List<String> queries = new ArrayList<>();

                    if (argsMap.containsKey("queryfile")) {
                        queries.add(readFile(argsMap.get("queryfile").get(0)));
                    }
                    if (argsMap.containsKey("query")) {
                        queries.addAll(argsMap.get("query"));
                    }
                    if (argsMap.containsKey("queries")) {
                        queries.addAll(argsMap.get("queries"));
                    }
                    ((FourthLab) lab).setQueries(queries);
                    if (argsMap.containsKey("spell")) {
                        ((FourthLab) lab).setSpellCheckers(argsMap.get("spell"));
                    }
                }

                lab.init();
                for (String filename : argsMap.get("file")) {
                    if ((labNumber == 2 || labNumber == 3 || labNumber == 4) && iter == ratio) {
                        if ((lab instanceof SecondLab)) {
                            for (NGramModel model : ((SecondLab) lab).getModels()) {
                                if (model.getType() == NGramModel.NGramModelType.HELD_OUT) {
                                    ((HeldOutNGramModel) model).setMode(HeldOutNGramModel.ModelMode.VALIDATION);
                                }
                            }
                        }
                        else if ((lab instanceof FourthLab)) {
                            for (NGramModel model : ((FourthLab) lab).getModels()) {
                                if (model.getType() == NGramModel.NGramModelType.HELD_OUT) {
                                    ((HeldOutNGramModel) model).setMode(HeldOutNGramModel.ModelMode.VALIDATION);
                                }
                            }
                        }
                        else if (lab instanceof ThirdLab) {
                            ((ThirdLab) lab).setMode(ThirdLab.InputMode.VALIDATION);
                        }
                    }
                    if ((labNumber != 2 && labNumber != 3) || iter < ratio) {
                        logger.info("filename to train: " + filename);
                    }
                    else {
                        logger.info("filename to validation: " + filename);
                    }
                    statistic = new CommonStatistic();
                    statistic.setParameter("wordtreshold", wordCounts.get(filename));
                    lab.setStatistic(statistic);
                    xmlReader.parse(convertToFileURL(filename));
                    iter++;
                }

                if (argsMap.containsKey("precisionrank") && labNumber == 3) {
                    LinguaUtil.setPrecisionRank(true);
                    precisionRank = Double.valueOf(argsMap.get("precisionrank").get(0));

                    DataHandler counter = new CounterDataHandler();

                    retriever.addDataHandler(counter);
                    for (String filename : argsMap.get("pnamefile")) {
                        xmlReader.parse(convertToFileURL(filename));
                        wordCounts.put(filename,
                                (int) (((CounterDataHandler) counter).getCounts() * precisionRank));
                    }
                }

                if (labNumber == 3) {
                    ((ThirdLab) lab).setMode(ThirdLab.InputMode.PROPER_NAME);
                    if (labNumber == 3) {
                        if (argsMap.containsKey("delta")) {
                            ((ThirdLab) lab).getRetriever().setDelta(Double.valueOf(argsMap.get("delta").get(0)));
                        }
                        if (argsMap.containsKey("tau")) {
                            ((ThirdLab) lab).getRetriever().setTau(Double.valueOf(argsMap.get("tau").get(0)));
                        }
                    }
                    for (String filename : argsMap.get("pnamefile")) {
                        logger.info("filename to proper name extraction: " + filename);
                        statistic = new CommonStatistic();
                        statistic.setParameter("wordtreshold", wordCounts.get(filename));
                        lab.setStatistic(statistic);
                        xmlReader.parse(convertToFileURL(filename));
                    }
                }

                lab.flush();
                lab.produce(top);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            } catch (SAXException e) {
                e.printStackTrace();
                logger.error(e);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                logger.error(e);
            } catch (UnsupposedArgumentException e) {
                e.printStackTrace();
                logger.error(e);
            } catch (UnsupposedTypeException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    public static String readFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line + " ");
            }

            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }

        return "";
    }

    private static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }

    private static void usage() {
        out.println("usage: java -jar ir-labs.jar [-lab <{n | 1 <= n <= 4 }> | -file <filenames>" +
                " | -detector <detectors> | -top <top number> ]");
        logger.info("turn out because error input arguments were obtained");
        System.exit(1);
    }


}
