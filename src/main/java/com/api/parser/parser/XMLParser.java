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

        return outputStream.toString(StandardCharsets.UTF_8);
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
        Element header = this.document.createElement("header");
        LinkedHashMap<String, String> headerInfoAsMap = getHeaderInfoAsMap(mt940);
        for (Map.Entry<String, String> entry : headerInfoAsMap.entrySet()) {
            header.appendChild(createElementWithText(entry.getKey(), entry.getValue()));
        }
        return header;
    }

    public Element getTagsAsXML(MT940 mt940) {
        Element tags = this.document.createElement("tags");

        tags.appendChild(getTag20(mt940));
        tags.appendChild(getTag25AsXML(mt940));
        tags.appendChild(getTag28CAsXML(mt940));
        tags.appendChild(getTag60FAsXML(mt940));
        tags.appendChild(getTag62FAsXML(mt940));
        tags.appendChild(getTag64AsXML(mt940));
        tags.appendChild(getTag65AsXML(mt940));
        tags.appendChild(getTransactionsAsXML(mt940));

        return tags;
    }

    public Element getTag20(MT940 mt940) {
        Element tag20 = this.document.createElement("transactionReferenceNumber");
        for (Map.Entry<String, String> entry : getTag20AsMap(mt940).entrySet()) {
            Element element = this.document.createElement(entry.getKey());
            element.appendChild(this.document.createTextNode(entry.getValue()));
            tag20.appendChild(element);
        }
        return tag20;
    }

    public Element getTag25AsXML(MT940 mt940) {
        Element tag25 = this.document.createElement("accountIdentification");
        for (Map.Entry<String, String> entry : getTag25AsMap(mt940).entrySet()) {
            Element element = this.document.createElement(entry.getKey());
            element.appendChild(this.document.createTextNode(entry.getValue()));
            tag25.appendChild(element);
        }
        return tag25;
    }

    public Element getTag28CAsXML(MT940 mt940) {
        Element tag28C = this.document.createElement("statementNumber");
        for (Map.Entry<String, String> entry : getTag28CAsMap(mt940).entrySet()) {
            Element element = this.document.createElement(entry.getKey());
            element.appendChild(this.document.createTextNode(entry.getValue()));
            tag28C.appendChild(element);
        }
        return tag28C;
    }

    public Element getTag60FAsXML(MT940 mt940) {
        Element tag60F = this.document.createElement("openingBalance");
        for (Map.Entry<String, String> entry : getTag60FAsMap(mt940).entrySet()) {
            Element element = this.document.createElement(entry.getKey());
            element.appendChild(this.document.createTextNode(entry.getValue()));
            tag60F.appendChild(element);
        }
        return tag60F;
    }

    public Element getTag62FAsXML(MT940 mt940) {
        Element tag62F = this.document.createElement("closingBalance");
        for (Map.Entry<String, String> entry : getTag62FAsMap(mt940).entrySet()) {
            Element element = this.document.createElement(entry.getKey());
            element.appendChild(this.document.createTextNode(entry.getValue()));
            tag62F.appendChild(element);
        }
        return tag62F;
    }

    public Element getTag64AsXML(MT940 mt940) {
        Element tag64 = this.document.createElement("closingAvailableBalance");
        for (Map.Entry<String, String> entry : getTag64AsMap(mt940).entrySet()) {
            Element element = this.document.createElement(entry.getKey());
            element.appendChild(this.document.createTextNode(entry.getValue()));
            tag64.appendChild(element);
        }
        return tag64;
    }

    public Element getTag65AsXML(MT940 mt940) {
        Element tag65 = this.document.createElement("forwardAvailableBalances");
        for (LinkedHashMap<String, String> map : getTag65AsArrayList(mt940)) {
            Element forwardAvailableBalance = this.document.createElement("forwardAvailableBalance");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                forwardAvailableBalance.appendChild(createElementWithText(entry.getKey(), entry.getValue()));
            }
            tag65.appendChild(forwardAvailableBalance);
        }
        return tag65;
    }

    public Element createElementWithText(String elementName, String elementValue) {
        Element element = this.document.createElement(elementName);
        element.appendChild(this.document.createTextNode(elementValue));
        return element;
    }

    public Element getTransactionsAsXML(MT940 mt940) {
        Element transactions = this.document.createElement("transactions");
        for (Map.Entry<Field61, LinkedHashMap<String, String>> entry : getTransactionsAsMap(mt940).entrySet()) {
            Element transaction = this.document.createElement("transaction");

            transaction.appendChild(createElementWithText("name", entry.getKey().getName()));
            transaction.appendChild(createElementWithText("transactionType", entry.getKey().getTransactionType()));
            transaction.appendChild(createElementWithText("identificationCode", entry.getKey().getIdentificationCode()));
            transaction.appendChild(createElementWithText("amount", entry.getKey().getAmount()));
            transaction.appendChild(createElementWithText("entryDate", entry.getKey().getEntryDate()));
            transaction.appendChild(createElementWithText("supplementaryDetails", entry.getKey().getSupplementaryDetails()));
            transaction.appendChild(createElementWithText("debitCreditMark", entry.getKey().getDebitCreditMark()));
            transaction.appendChild(createElementWithText("valueDate", entry.getKey().getValueDate()));
            transaction.appendChild(createElementWithText("referenceForTheAccountOwner", entry.getKey().getReferenceForTheAccountOwner()));
            transaction.appendChild(createElementWithText("referenceOfTheAccountServicingInstitution", entry.getKey().getReferenceOfTheAccountServicingInstitution()));

            Element tag86 = this.document.createElement("informationToAccountOwner");
            for (Map.Entry<String, String> tag86entry : entry.getValue().entrySet()) {
                tag86.appendChild(createElementWithText(tag86entry.getKey(), tag86entry.getValue()));
            }
            transaction.appendChild(tag86);
            transactions.appendChild(transaction);
        }
        return transactions;
    }
}
