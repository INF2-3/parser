package com.api.parser.parser;

import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class JSONParser extends Parser {
    @SuppressWarnings("unchecked")
    @Override
    public String parseToFormat(MultipartFile file) {
        MT940 mt940 = parseMT940(file);
        if (mt940 == null) {
            return null;
        }

        JSONObject formattedJSON = new JSONObject();
        JSONObject tags = new JSONObject();
        formattedJSON.put("header", new JSONObject(getHeaderInfoAsMap(mt940)));

        tags.put("transactionReferenceNumber", new JSONObject(getTag20AsMap(mt940)));
        tags.put("accountIdentification", new JSONObject(getTag25AsMap(mt940)));
        tags.put("statementNumber", new JSONObject(getTag28CAsMap(mt940)));
        tags.put("openingBalance", new JSONObject(getTag60FAsMap(mt940)));
        tags.put("closingBalance", new JSONObject(getTag62FAsMap(mt940)));
        tags.put("closingAvailableBalance", new JSONObject(getTag64AsMap(mt940)));
        tags.put("forwardAvailableBalance", new JSONArray(getTag65AsArrayList(mt940)));
        tags.put("transactions", getTransactions(mt940));
        tags.put("generalInformationToAccountOwner", new JSONObject(getGeneral86TagAsMap(mt940)));

        formattedJSON.put("tags", tags);
        return formattedJSON.toString(1).replace("\\r", "");
    }

    public JSONArray getTransactions(MT940 mt940) {
        LinkedHashMap<Field61, LinkedHashMap<String, String>> transactionsMap = getTransactionsAsMap(mt940);
        JSONArray transactions = new JSONArray();

        for (Map.Entry<Field61, LinkedHashMap<String, String>> entry : transactionsMap.entrySet()) {
            JSONObject obj61 = new JSONObject(entry.getKey().toJson());
            String amount = obj61.get("amount").toString().replace(",", ".");
            obj61.put("amount", amount);
            JSONObject obj86 = new JSONObject(entry.getValue());
            obj61.put("informationToAccountOwner", obj86);
            transactions.put(obj61);
        }
        return transactions;
    }
}
