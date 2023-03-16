package com.api.parser.parser;

import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.json.JSONObject;
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
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
        return "Parsed into XML";
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
        tags.appendChild(getTag25AsXML(mt940, document));
        tags.appendChild(getTag28CAsXML(mt940, document));
        tags.appendChild(getTag60FAsXML(mt940, document));
        tags.appendChild(getTag62FAsXML(mt940, document));
        tags.appendChild(getTag64AsXML(mt940, document));
        tags.appendChild(getTag65AsXML(mt940, document));
        tags.appendChild(getTransactionsAsXML(mt940, document));
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

    public Element getTag25AsXML(MT940 mt940, Document document) {
        Element tag25 = document.createElement("accountIdentification");
        for (Map.Entry<String, String> entry : getTag25AsMap(mt940).entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createTextNode(entry.getValue()));
            tag25.appendChild(element);
        }
        return tag25;
    }

    public Element getTag28CAsXML(MT940 mt940, Document document) {
        Element tag28C = document.createElement("statementNumber");
        for (Map.Entry<String, String> entry : getTag28CAsMap(mt940).entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createTextNode(entry.getValue()));
            tag28C.appendChild(element);
        }
        return tag28C;
    }

    public Element getTag60FAsXML(MT940 mt940, Document document) {
        Element tag60F = document.createElement("openingBalance");
        for (Map.Entry<String, String> entry : getTag60FAsMap(mt940).entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createTextNode(entry.getValue()));
            tag60F.appendChild(element);
        }
        return tag60F;
    }

    public Element getTag62FAsXML(MT940 mt940, Document document) {
        Element tag62F = document.createElement("closingBalance");
        for (Map.Entry<String, String> entry : getTag62FAsMap(mt940).entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createTextNode(entry.getValue()));
            tag62F.appendChild(element);
        }
        return tag62F;
    }

    public Element getTag64AsXML(MT940 mt940, Document document) {
        Element tag64 = document.createElement("closingAvailableBalance");
        for (Map.Entry<String, String> entry : getTag64AsMap(mt940).entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createTextNode(entry.getValue()));
            tag64.appendChild(element);
        }
        return tag64;
    }

    public Element getTag65AsXML(MT940 mt940, Document document) {
        Element tag65 = document.createElement("forwardAvailableBalances");
        for (HashMap<String, String> map : getTag65AsArrayList(mt940)) {
            Element forwardAvailableBalance = document.createElement("forwardAvailableBalance");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Element element = document.createElement(entry.getKey());
                element.appendChild(document.createTextNode(entry.getValue()));
                forwardAvailableBalance.appendChild(element);
            }
            tag65.appendChild(forwardAvailableBalance);
        }
        return tag65;
    }

    public Element getTransactionsAsXML(MT940 mt940, Document document) {
        Element transactions = document.createElement("transactions");
        for (Map.Entry<Field61, HashMap<String, String>> entry : getTransactionsAsMap(mt940).entrySet()) {
            Element transaction = document.createElement("transaction");

            transaction.appendChild(document.createElement("name")).appendChild(document.createTextNode(entry.getKey().getName()));
            transaction.appendChild(document.createElement("transactionType")).appendChild(document.createTextNode(entry.getKey().getTransactionType()));
            transaction.appendChild(document.createElement("identificationCode")).appendChild(document.createTextNode(entry.getKey().getIdentificationCode()));
            transaction.appendChild(document.createElement("amount")).appendChild(document.createTextNode(entry.getKey().getAmount()));
            transaction.appendChild(document.createElement("entryDate")).appendChild(document.createTextNode(entry.getKey().getEntryDate()));
            transaction.appendChild(document.createElement("supplementaryDetails")).appendChild(document.createTextNode(entry.getKey().getSupplementaryDetails()));
            transaction.appendChild(document.createElement("debitCreditMark")).appendChild(document.createTextNode(entry.getKey().getDebitCreditMark()));
            transaction.appendChild(document.createElement("valueDate")).appendChild(document.createTextNode(entry.getKey().getValueDate()));
            transaction.appendChild(document.createElement("referenceForTheAccountOwner")).appendChild(document.createTextNode(entry.getKey().getReferenceForTheAccountOwner()));
            transaction.appendChild(document.createElement("referenceOfTheAccountServicingInstitution")).appendChild(document.createTextNode(entry.getKey().getReferenceOfTheAccountServicingInstitution()));

            Element tag86 = document.createElement("informationToAccountOwner");
            for(Map.Entry<String, String> tag86entry : entry.getValue().entrySet()) {
                tag86.appendChild(document.createElement(tag86entry.getKey())).appendChild(document.createTextNode(tag86entry.getValue()));
            }
            transaction.appendChild(tag86);
            transactions.appendChild(transaction);
        }
        return transactions;
    }
}
