package com.api.parser;

import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.field.Field86;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

            List<String> codeWords = Arrays.asList("RTRN", "CREF", "EREF", "PREF", "IREF", "MARF", "CSID", "CNTP", "REMI", "PURP", "ULTC", "ULTD", "EXCH", "CHGS");
            JSONArray tag86 = new JSONArray();
            for (Field86 field : mt940.getField86()) {
                JSONObject obj = new JSONObject();
//                obj.put("narrative", field.getNarrativeLine1());
//                obj.put("narrative2", field.getNarrativeLine2());
//                obj.put("narrative3", field.getNarrativeLine3());
//                obj.put("narrative4", field.getNarrativeLine4());
//                obj.put("narrative5", field.getNarrativeLine5());
//                obj.put("narrative6", field.getNarrativeLine6());
                obj.put("narrative", field.getComponentLabels());
//                String[] tag86parts = field.getNarrative().split("//");
                tag86.put(obj);
            }


            JSONObject tags = new JSONObject();
            tags.put("20", tag20);
            tags.put("25", tag25);
            tags.put("28C", tag28C);
            tags.put("60F", tag60F);
            tags.put("61", tag61);
            tags.put("86", tag86);


            formattedJSON.put("tags", tags);




            return formattedJSON.toString();
        } catch (IOException e) {
            System.out.println("There was a problem with getting the file");
            return null;
        }
    }

    public String getTag86Parts() {
        String line = "/EREF/EV123REP123412T1234//MARF/MND-EV01//CSID/NL32ZZZ999999991234//CNTP/NL32INGB0000012345/INGBNL2A/ING Bank N.V. inzake WeB///REMI/USTD//EV123REP123412T1234/";
        String tag86parts = "";
        Pattern pattern = Pattern.compile("/REMI/[A-Za-z0-9]+//");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            tag86parts = matcher.group();
        }
        return tag86parts;
    }
}
