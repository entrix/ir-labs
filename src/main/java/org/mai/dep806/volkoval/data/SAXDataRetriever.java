package org.mai.dep806.volkoval.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: entrix
 * Date: 11.06.13
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class SAXDataRetriever extends DataRetriever implements ContentHandler {

    private static Logger logger = LogManager.getLogger(SAXDataRetriever.class);


    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {

        List<RunnableTask> tasks = new ArrayList<>();

        // executorService initialization's here
        initExecutor();

        for (int i = 0; i < handlers.size(); ++i) {
            tasks.add(new RunnableTask(i) {
                @Override
                public Object call() throws Exception {

                    getHandler().initHandler();

                    return true;
                }
            });
        }

        proceedTasks(tasks);
    }

    @Override
    public void endDocument() throws SAXException {
        List<RunnableTask> tasks = new ArrayList<>();

        for (int i = 0; i < handlers.size(); ++i) {
            tasks.add(new RunnableTask(i) {
                @Override
                public Object call() throws Exception {
                    getHandler().flushHandler();

                    return true;
                }
            });
        }
//
        proceedTasks(tasks);
        isInitExecutor = false;
        executorService.shutdown();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        logger.info("start prefix map data");
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        logger.info("end prefix map data");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        logger.info("start retrieve data");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        logger.info("end retrieve data");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        StringBuilder builder = new StringBuilder();
        final List<String> strings = new ArrayList<>();
        List<RunnableTask> tasks = new ArrayList<>();

        for (int i = start, end = i + length; i < end; ++i) {
//            System.out.print(ch[i]);
            if (ch[i] != '.' && ch[i] != '?' && ch[i] != '!') {
                builder.append(ch[i]);
            }
            else {
                strings.add(builder.toString());
                builder = new StringBuilder();
            }
        }

        for (int i = 0; i < handlers.size(); ++i) {
            tasks.add(new RunnableTask(i) {
                @Override
                public Object call() throws Exception {
                    getHandler().handle(strings);

                    return true;
                }
            });
        }

        proceedTasks(tasks);

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        logger.info("retrieve data");
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        logger.info("retrieve data");
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        logger.info("retrieve data");
    }


}
