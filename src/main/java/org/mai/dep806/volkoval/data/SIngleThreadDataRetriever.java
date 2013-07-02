package org.mai.dep806.volkoval.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mai.dep806.volkoval.linguistic.LinguaUtil;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: avvolkov
 * Date: 20.06.13
 * Time: 20:58
 * To change this template use File | Settings | File Templates.
 */
public class SIngleThreadDataRetriever extends DataRetriever implements ContentHandler {
    private static Logger logger = LogManager.getLogger(SAXDataRetriever.class);

    private Pattern punctuation = Pattern.compile("[\\\"\\'\\`\\,\\:\\;\\(\\)\\[\\]\\{\\}\\@\\#\\$\\%\\^\\&\\*\\=\\+]");

    private boolean read;

//    private String lastString;

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {
//        for (DataHandler handler : handlers) {
//            handler.initHandler();
//        }
    }

    @Override
    public void endDocument() throws SAXException {
//        for (DataHandler handler : handlers) {
//            handler.flushHandler();
//        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
//        logger.info("start prefix map data");
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
//        logger.info("end prefix map data");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (!qName.equals("p")) {
            read = false;
        }
        else {
            read = true;
        }
//        logger.info("start retrieve data");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        logger.info("end retrieve data");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (!read) {
            return;
        }

        List<List<String>> sentences = LinguaUtil.toSentences(Arrays.copyOfRange(ch, start, start + length));

        for (List<String> sentence : sentences) {
            if (sentence.size() > 1) {
                for (DataHandler handler : handlers) {
                    handler.handle(sentence);
                }
            }
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
//        logger.info("retrieve data");
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
//        logger.info("retrieve data");
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
//        logger.info("retrieve data");
    }
}
