package com.api.parser;

import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;

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
                tag61.add(obj);
            }


            JSONObject tags = new JSONObject();
            tags.put("20", tag20);
            tags.put("25", tag25);
            tags.put("28C", tag28C);
            tags.put("60F", tag60F);
            tags.put("61", tag61);


            formattedJSON.put("tags", tags);




            return formattedJSON.toString();
        } catch (IOException e) {
            System.out.println("There was a problem with getting the file");
            return null;
        }
    }
}
