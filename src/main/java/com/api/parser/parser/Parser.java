package com.api.parser.parser;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parser {
    public abstract String parseMT940(MultipartFile file);

    public HashMap<String, String> splitTag86IntoParts(String tag86ContentInline) {
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
        HashMap<String, String> tag86split = new HashMap<>();
        tag86split.put("accountOwnerInformationInOneLine", tag86ContentInline);

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
                tag86split.put(entry.getValue(), result);
            } else {
                tag86split.put(entry.getValue(), "");
            }
        }
        return tag86split;
    }
}
