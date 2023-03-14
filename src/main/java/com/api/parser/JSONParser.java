package com.api.parser;

import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.field.Field65;
import com.prowidesoftware.swift.model.field.Field86;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser extends Parser {
    @Override
    public String parseMT940() {
        try {
            MT940 mt940 = getMT940File();

            JSONObject formattedJSON = new JSONObject();
            String tag20 = mt940.getField20().getReference();
            String tag25 = mt940.getField25().getAccount();
            String tag28C = mt940.getField28C().getStatementNumber();

            JSONObject tag60F = new JSONObject();
            tag60F.put("dCMark", mt940.getField60F().getDCMark());
            tag60F.put("date", mt940.getField60F().getDate());
            tag60F.put("currency", mt940.getField60F().getCurrency());
            tag60F.put("amount", mt940.getField60F().getAmount());

            JSONArray tag61 = new JSONArray();
            for(Field61 field : mt940.getField61()) {
                JSONObject obj = new JSONObject();
                obj.put("valueDate", field.getValueDate());
                obj.put("entryDate", field.getEntryDate());
                obj.put("debitCreditMark", field.getDebitCreditMark());
                obj.put("amount", field.getAmount());
                obj.put("transactionType", field.getTransactionType());
                obj.put("identificationCode", field.getIdentificationCode());
                obj.put("referenceForTheAccountOwner", field.getReferenceForTheAccountOwner());
                obj.put("referenceOfTheAccountServicingInstitution", field.getReferenceOfTheAccountServicingInstitution());
                obj.put("supplementaryDetails", field.getSupplementaryDetails());
                tag61.put(obj);
            }

            JSONArray tag86 = new JSONArray();
            for (Field86 field : mt940.getField86()) {
                JSONObject obj = new JSONObject();
                obj.put("accountOwnerInformationInline", field.getNarrative());
                obj.put("accountOwnerInformationSplit", splitTag86IntoParts(field.getNarrative()));
                tag86.put(obj);
            }

            JSONObject tag62F = new JSONObject();
            tag62F.put("dCMark", mt940.getField62F().getDCMark());
            tag62F.put("date", mt940.getField62F().getDate());
            tag62F.put("currency", mt940.getField62F().getCurrency());
            tag62F.put("amount", mt940.getField62F().getAmount());

            JSONObject tag64 = new JSONObject();
            tag64.put("dCMark", mt940.getField64().getDCMark());
            tag64.put("date", mt940.getField64().getDate());
            tag64.put("currency", mt940.getField64().getCurrency());
            tag64.put("amount", mt940.getField64().getAmount());

            JSONArray tag65 = new JSONArray();
            for (Field65 field : mt940.getField65()) {
                JSONObject obj = new JSONObject();
                obj.put("dCMark", field.getDCMark());
                obj.put("date", field.getDate());
                obj.put("currency", field.getCurrency());
                obj.put("amount", field.getAmount());
                tag65.put(obj);
            }

            JSONObject tags = new JSONObject();
            tags.put("20", tag20);
            tags.put("25", tag25);
            tags.put("28C", tag28C);
            tags.put("60F", tag60F);
            tags.put("61", tag61);
            tags.put("86", tag86);
            tags.put("62", tag62F);
            tags.put("64", tag64);
            tags.put("65", tag65);

            formattedJSON.put("tags", tags);

            return formattedJSON.toString();
        } catch (IOException e) {
            System.out.println("There was a problem with getting the file");
            return null;
        }
    }

    public JSONObject splitTag86IntoParts(String tag86ContentInline) {
        HashMap<String, String> codeWords = new HashMap<>() {{
            put("RTRN", "returnReason");
            put("CREF", "clientReference");
            put("EREF", "endToEndReference");
            put("PREF", "paymentInformationId");
            put("IREF", "instructionId");
            put("MARF", "mandateReference");
            put("CSID", "creditorId");
            put("CNTP", "counterPartyId");
            put("REMI", "remittanceInformation");
            put("PURP", "purposeCode");
            put("ULTC", "ultimateCreditor");
            put("ULTD", "ultimateDebitor");
            put("EXCH", "exchangeRate");
            put("CHGS", "charges");
        }};

        JSONObject obj = new JSONObject();

        for (Map.Entry<String, String> entry : codeWords.entrySet()) {
            Pattern pattern = Pattern.compile( entry.getKey() + "/(?<=/).+?(?=//)");

            if (entry.getKey().equals("REMI")) {
                pattern = Pattern.compile("REMI/.*/");
            }
            Matcher matcher = pattern.matcher(tag86ContentInline);

            if (matcher.find()) {
                String[] match = matcher.group().split("/");
                String[] cleanedResult = Arrays.copyOfRange(match, 1, match.length);
                String result = String.join("/", cleanedResult);
                obj.put(entry.getValue(), result);
            } else {
                obj.put(entry.getValue(), "");
            }
        }
        return obj;
    }
}
