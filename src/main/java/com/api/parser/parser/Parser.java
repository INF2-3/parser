package com.api.parser.parser;

import com.prowidesoftware.swift.model.field.Field;
import com.prowidesoftware.swift.model.field.Field61;
import com.prowidesoftware.swift.model.field.Field65;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parser {
    public abstract <T> T parseToFormat(MultipartFile file);

    public MT940 parseMT940(MultipartFile file) {
        try {
            return MT940.parse(file.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * This method gets every sub value from the 86 tag by using regex and the pattern and matcher classes
     *
     * @param tag86ContentInline The full content of the 86 tag in one line
     * @return Hashmap with every sub value as the value and the name as the key
     */
    public LinkedHashMap<String, String> splitTag86IntoParts(String tag86ContentInline) {
        LinkedHashMap<String, String> codeWords = new LinkedHashMap<>() {{
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
        LinkedHashMap<String, String> tag86split = new LinkedHashMap<>();
        tag86split.put("accountOwnerInformationInOneLine", tag86ContentInline);

        for (Map.Entry<String, String> entry : codeWords.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey() + "/(?<=/).+?(?=//)");

            if (entry.getKey().equals("REMI")) {
                pattern = Pattern.compile("REMI/.*/");
            }
            Matcher matcher = pattern.matcher(tag86ContentInline);

            if (matcher.find()) {
                String[] match = matcher.group().split("/");
                String[] cleanedResult = Arrays.copyOfRange(match, 1, match.length);
                String result = String.join("/", cleanedResult);
                tag86split.put(entry.getValue(), result);
            } else {
                tag86split.put(entry.getValue(), "");
            }
        }
        return tag86split;
    }

    /**
     * This method gets every Field61 and the corresponding 86 Field and returns them in a hashmap
     *
     * @param mt940 MT940 file
     * @return Hashmap with a Field61 obj as key and a hashmap that contains every tag 86 sub value that corresponds
     * to the specific Field61
     */
    public LinkedHashMap<Field61, LinkedHashMap<String, String>> getTransactionsAsMap(MT940 mt940) {
        int transactionCount = 0;
        LinkedHashMap<Field61, LinkedHashMap<String, String>> transactions = new LinkedHashMap<>();
        for (Field field : mt940.getFields()) {
            if (field.getName().equals("61")) {
                Field61 field61 = (Field61) field;
                LinkedHashMap<String, String> correspondingTag86 = new LinkedHashMap<>();
                if (mt940.getField86().size() - 1 >= transactionCount) {
                    correspondingTag86 = splitTag86IntoParts(mt940.getField86().get(transactionCount).getNarrative());
                }
                transactions.put(field61, correspondingTag86);
                transactionCount++;
            }
        }
        return transactions;
    }

    public LinkedHashMap<String, String> getTag20AsMap(MT940 mt940) {
        return new LinkedHashMap<>() {{
            put("name", mt940.getField20().getName());
            put("referenceNumber", mt940.getField20().getReference());
            put("description", "This tag specifies the reference assigned by the Sender to unambiguously identify\n" +
                    "the message.");
        }};
    }

    public LinkedHashMap<String, String> getTag25AsMap(MT940 mt940) {
        return new LinkedHashMap<>() {{
            put("name", mt940.getField25().getName());
            put("accountNumber", mt940.getField25().getAccount());
            put("description", "This tag identifies the account for which the statement is sent");
        }};
    }

    public LinkedHashMap<String, String> getTag28CAsMap(MT940 mt940) {
        return new LinkedHashMap<>() {{
            put("name", mt940.getField28C().getName());
            put("statementNumber", mt940.getField28C().getStatementNumber());
            put("description", "This tag contains the sequential number of the statement");
        }};
    }

    public LinkedHashMap<String, String> getTag60FAsMap(MT940 mt940) {
        return new LinkedHashMap<>() {{
            put("name", mt940.getField60F().getName());
            put("dCMark", mt940.getField60F().getDCMark());
            put("date", mt940.getField60F().getDate());
            put("currency", mt940.getField60F().getCurrency());
            put("amount", mt940.getField60F().getAmount());
            put("description", "This tag specifies, for the opening balance, whether it is a debit or credit balance,\n" +
                    "the date, the currency and the amount of the balance.");
        }};
    }

    public LinkedHashMap<String, String> getTag62FAsMap(MT940 mt940) {
        return new LinkedHashMap<>() {{
            put("name", mt940.getField62F().getName());
            put("dCMark", mt940.getField62F().getDCMark());
            put("date", mt940.getField62F().getDate());
            put("currency", mt940.getField62F().getCurrency());
            put("amount", mt940.getField62F().getAmount());
            put("description", "This tag specifies for the closing balance, whether it is a debit or credit balance, the\n" +
                    "date, the currency and the amount of the balance.");
        }};
    }

    public LinkedHashMap<String, String> getTag64AsMap(MT940 mt940) {
        return new LinkedHashMap<>() {{
            put("name", mt940.getField64().getName());
            put("dCMark", mt940.getField64().getDCMark());
            put("date", mt940.getField64().getDate());
            put("currency", mt940.getField64().getCurrency());
            put("amount", mt940.getField64().getAmount());
            put("description", "This tag specifies for the closing available balance, whether it is a debit or credit\n" +
                    "balance, the date, the currency and the amount of the balance");
        }};
    }

    public ArrayList<LinkedHashMap<String, String>> getTag65AsArrayList(MT940 mt940) {
        ArrayList<LinkedHashMap<String, String>> tag65 = new ArrayList<>();
        for (Field65 field : mt940.getField65()) {
            LinkedHashMap<String, String> map = new LinkedHashMap<>() {{
                put("dCMark", field.getDCMark());
                put("date", field.getDate());
                put("currency", field.getCurrency());
                put("amount", field.getAmount());
            }};
            tag65.add(map);
        }
        return tag65;
    }

    public LinkedHashMap<String, String> getHeaderInfoAsMap(MT940 mt940) {
        return new LinkedHashMap<>() {{
            put("applicationId", mt940.getApplicationId());
            put("serviceId", mt940.getServiceId());
            put("logicalTerminal", mt940.getLogicalTerminal());
            put("sessionNumber", mt940.getSessionNumber());
            put("sequenceNumber", mt940.getSequenceNumber());
            put("receiverAddress", mt940.getReceiver());
            put("messageType", mt940.getMessageType());
            put("mtId", mt940.getMtId().toString());
        }};
    }
}
