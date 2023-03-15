package com.api.parser.parser;

import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.field.Field65;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser extends Parser {
    @Override
    public String parseMT940(MultipartFile file) {
        MT940 mt940 = null;
        try {
            mt940 = MT940.parse(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject formattedJSON = new JSONObject();
        formattedJSON.put("type", mt940.getMessageType());

        JSONObject tags = new JSONObject();
        tags.put("header", getHeaderInfo(mt940));
        tags.put("transactionReferenceNumber", getTag20(mt940));
        tags.put("accountIdentification", getTag25(mt940));
        tags.put("statementNumber", getTag28C(mt940));
        tags.put("openingBalance", getTag60F(mt940));
        tags.put("closingBalance", getTag62F(mt940));
        tags.put("closingAvailableBalance", getTag64(mt940));
        tags.put("forwardAvailableBalance", getTag65(mt940));
        tags.put("transactions", getTransactions(mt940));
        formattedJSON.put("tags", tags);

        return formattedJSON.toString(1);
    }

    public JSONArray getTransactions(MT940 mt940) {
        int transactionCount = 0;
        JSONArray transactions = new JSONArray();
        for (Field field : mt940.getFields()) {
            if (field.getName().equals("61")) {
                JSONObject obj = new JSONObject(field.toJson());
                JSONObject correspondingTag86 = getTag86(mt940.getField86().get(transactionCount).getNarrative());
                obj.put("informationToAccountOwner", correspondingTag86);
                transactions.put(obj);
                transactionCount++;
            }
        }
        return transactions;
    }

    public JSONObject getTag20(MT940 mt940) {
        JSONObject tag20 = new JSONObject();
        tag20.put("tag", mt940.getField20().getName());
        tag20.put("referenceNumber", mt940.getField20().getReference());
        tag20.put("description", "This tag specifies the reference assigned by the Sender to unambiguously identify\n" +
                "the message.");
        return tag20;
    }

    public JSONObject getTag25(MT940 mt940) {
        JSONObject tag25 = new JSONObject();
        tag25.put("tag", mt940.getField25().getName());
        tag25.put("accountNumber", mt940.getField25().getAccount());
        tag25.put("description", "This tag identifies the account for which the statement is sent");
        return tag25;
    }

    public JSONObject getTag28C(MT940 mt940) {
        JSONObject tag28C = new JSONObject();
        tag28C.put("tag", mt940.getField28C().getName());
        tag28C.put("statementNumber", mt940.getField28C().getStatementNumber());
        tag28C.put("description", "This tag contains the sequential number of the statement");
        return tag28C;
    }

    public JSONObject getTag60F(MT940 mt940) {
        JSONObject tag60F = new JSONObject();
        tag60F.put("tag", mt940.getField60F().getName());
        tag60F.put("dCMark", mt940.getField60F().getDCMark());
        tag60F.put("date", mt940.getField60F().getDate());
        tag60F.put("currency", mt940.getField60F().getCurrency());
        tag60F.put("amount", mt940.getField60F().getAmount());
        tag60F.put("description", "This tag specifies, for the opening balance, whether it is a debit or credit balance,\n" +
                "the date, the currency and the amount of the balance.");
        return tag60F;
    }

    public JSONObject getTag62F(MT940 mt940) {
        JSONObject tag62F = new JSONObject();
        tag62F.put("tag", mt940.getField62F().getName());
        tag62F.put("dCMark", mt940.getField62F().getDCMark());
        tag62F.put("date", mt940.getField62F().getDate());
        tag62F.put("currency", mt940.getField62F().getCurrency());
        tag62F.put("amount", mt940.getField62F().getAmount());
        tag62F.put("description", "This tag specifies for the closing balance, whether it is a debit or credit balance, the\n" +
                "date, the currency and the amount of the balance.");
        return tag62F;
    }

    public JSONObject getTag64(MT940 mt940) {
        JSONObject tag64 = new JSONObject();
        tag64.put("tag", mt940.getField64().getName());
        tag64.put("dCMark", mt940.getField64().getDCMark());
        tag64.put("date", mt940.getField64().getDate());
        tag64.put("currency", mt940.getField64().getCurrency());
        tag64.put("amount", mt940.getField64().getAmount());
        tag64.put("description", "This tag specifies for the closing available balance, whether it is a debit or credit\n" +
                "balance, the date, the currency and the amount of the balance");
        return tag64;
    }

    public JSONArray getTag65(MT940 mt940) {
        JSONArray tag65 = new JSONArray();
        for (Field65 field : mt940.getField65()) {
            JSONObject obj = new JSONObject();
            obj.put("dCMark", field.getDCMark());
            obj.put("date", field.getDate());
            obj.put("currency", field.getCurrency());
            obj.put("amount", field.getAmount());
            tag65.put(obj);
        }
        return tag65;
    }

    public JSONObject getHeaderInfo(MT940 mt940) {
        JSONObject header = new JSONObject();
        header.put("applicationId", mt940.getApplicationId());
        header.put("serviceId", mt940.getServiceId());
        header.put("logicalTerminal", mt940.getLogicalTerminal());
        header.put("sessionNumber", mt940.getSessionNumber());
        header.put("sequenceNumber", mt940.getSequenceNumber());
        header.put("receiverAddress", mt940.getReceiver());
        header.put("messageType", mt940.getMessageType());
        header.put("mtId", mt940.getMtId());
        return header;
    }

    public JSONObject getTag86(String inp) {
        HashMap<String, String> data = splitTag86IntoParts(inp);
        return new JSONObject(data);
    }
}
