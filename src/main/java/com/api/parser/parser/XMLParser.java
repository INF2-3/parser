package com.api.parser.parser;

import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLParser extends Parser{

    private Document document;
    public XMLParser() throws ParserConfigurationException {
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }
    @Override
    public String parseMT940(MultipartFile file) {
        MT940 mt940 = null;
        try {
            mt940 = MT940.parse(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            setupXML(mt940);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return "S";
    }

    public void setupXML(MT940 mt940) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();

        Element root = document.createElement("MT940");
        document.appendChild(root);
        root.appendChild(getHeaderInfoAsXML(mt940, document));
        root.appendChild(getTagsAsXML(mt940, document));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File("src/main/java/com/api/parser/data.xml"));
        transformer.transform(source, result);

        // Output to console for testing
        StreamResult consoleResult = new StreamResult(System.out);
        transformer.transform(source, consoleResult);
    }

    public Element getHeaderInfoAsXML(MT940 mt940, Document document) {
        Element header = document.createElement("header");
        HashMap<String, String> headerInfoAsMap = getHeaderInfoAsMap(mt940);
        for (Map.Entry<String, String> entry : headerInfoAsMap.entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createTextNode(entry.getValue()));
            header.appendChild(element);
        }
        return header;
    }

    public Element getTagsAsXML(MT940 mt940, Document document) {
        Element tags = document.createElement("tags");
        tags.appendChild(getTag20(mt940, document));
        return tags;
    }

    public Element getTag20(MT940 mt940, Document document) {
        Element tag20 = document.createElement("transactionReferenceNumber");
        for (Map.Entry<String, String> entry : getTag20AsMap(mt940).entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createTextNode(entry.getValue()));
            tag20.appendChild(element);
        }
        return tag20;
    }
}
