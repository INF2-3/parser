package com.api.parser.parser;

import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMLParser extends Parser {

    private Document document;

    public void createNewDocument() throws ParserConfigurationException {
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String parseToFormat(MultipartFile file) {
        MT940 mt940 = parseMT940(file);
        if (mt940 == null) {
            return null;
        }

        try {
            return returnXMLContent(mt940);
        } catch (ParserConfigurationException | TransformerException e) {
            return null;
        }
    }

    /**
     * This method creates a new document, adds the xml to it and transforms it to a DOMSource
     *
     * @param mt940 The MT940 file
     * @return A string containing the XML
     * @throws ParserConfigurationException if the document could not be created
     * @throws TransformerException         if transformer could not be created
     */
    public String returnXMLContent(MT940 mt940) throws ParserConfigurationException, TransformerException {
        // Create a new empty document
        createNewDocument();
        buildXMLStructure(mt940);

        // Need transformer to transform the doc source into a result
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);

        // Transform the document to DOMSource and put content in outputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        transformer.transform(source, result);

        return outputStream.toString(StandardCharsets.UTF_8).replace("&#13;", "");
    }

    /**
     * Creates the root element and adds all the other elements to it (header and tags)
     *
     * @param mt940 The MT940 file
     */
    private void buildXMLStructure(MT940 mt940) {
        Element root = this.document.createElement("MT940");
        this.document.appendChild(root);
        root.appendChild(getHeaderInfoAsXML(mt940));
        root.appendChild(getTagsAsXML(mt940));
    }

    public Element getHeaderInfoAsXML(MT940 mt940) {
        return createElementWithTextFromMap("header", getHeaderInfoAsMap(mt940));
    }

    public Element getTagsAsXML(MT940 mt940) {
        Element tags = this.document.createElement("tags");

        tags.appendChild(createElementWithTextFromMap("transactionReferenceNumber", getTag20AsMap(mt940)));
        tags.appendChild(createElementWithTextFromMap("accountIdentification", getTag25AsMap(mt940)));
        tags.appendChild(createElementWithTextFromMap("statementNumber", getTag28CAsMap(mt940)));
        tags.appendChild(createElementWithTextFromMap("openingBalance", getTag60FAsMap(mt940)));
        tags.appendChild(createElementWithTextFromMap("closingBalance", getTag62FAsMap(mt940)));
        tags.appendChild(createElementWithTextFromMap("closingAvailableBalance", getTag64AsMap(mt940)));
        tags.appendChild(getTag65AsXML(mt940));
        tags.appendChild(getTransactionsAsXML(mt940));
        tags.appendChild(createElementWithTextFromMap("generalInformationToAccountOwner", getGeneral86TagAsMap(mt940)));

        return tags;
    }

    public Element getTag65AsXML(MT940 mt940) {
        Element tag65 = this.document.createElement("forwardAvailableBalances");
        for (LinkedHashMap<String, String> map : getTag65AsArrayList(mt940)) {
            tag65.appendChild(createElementWithTextFromMap("forwardAvailableBalance", map));
        }
        return tag65;
    }

    public Element createElementWithText(String elementName, String elementValue) {
        Element element = this.document.createElement(elementName);
        element.appendChild(this.document.createTextNode(elementValue));
        return element;
    }

    /**
     * Creates an xml element when given the tag name and a map with key value pairs for that tag
     * @param tagName Name of the xml tag
     * @param map Map containing all entries of a specific tag
     * @return Element with all content stored as xml
     */
    public Element createElementWithTextFromMap(String tagName, Map<String, String> map) {
        Element tag = this.document.createElement(tagName);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            tag.appendChild(createElementWithText(entry.getKey(), entry.getValue()));
        }
        return tag;
    }

    public Element getTransactionsAsXML(MT940 mt940) {
        Element transactions = this.document.createElement("transactions");
        for (Map.Entry<Field61, LinkedHashMap<String, String>> entry : getTransactionsAsMap(mt940).entrySet()) {
            Element transaction = this.document.createElement("transaction");

            transaction.appendChild(createElementWithText("name", entry.getKey().getName()));
            transaction.appendChild(createElementWithText("transactionType", entry.getKey().getTransactionType()));
            transaction.appendChild(createElementWithText("identificationCode", entry.getKey().getIdentificationCode()));
            transaction.appendChild(createElementWithText("amount", entry.getKey().getAmount().replace(",", ".")));
            transaction.appendChild(createElementWithText("entryDate", entry.getKey().getEntryDate()));
            transaction.appendChild(createElementWithText("supplementaryDetails", entry.getKey().getSupplementaryDetails()));
            transaction.appendChild(createElementWithText("debitCreditMark", entry.getKey().getDebitCreditMark()));
            transaction.appendChild(createElementWithText("valueDate", entry.getKey().getValueDate()));
            transaction.appendChild(createElementWithText("referenceForTheAccountOwner", entry.getKey().getReferenceForTheAccountOwner()));
            transaction.appendChild(createElementWithText("referenceOfTheAccountServicingInstitution", entry.getKey().getReferenceOfTheAccountServicingInstitution()));

            transaction.appendChild(createElementWithTextFromMap("informationToAccountOwner", entry.getValue()));
            transactions.appendChild(transaction);
        }
        return transactions;
    }
}
