package com.api.parser;

import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {
    public String parseMT940(String format) {
        try {
            MT940 mt940 = getMT940File();
            String parsedContent;

            if (format.equals("json")) {
                parsedContent = mt940.toJson();
            } else if (format.equals("xml")) {
                parsedContent = mt940.xml();
            } else {
                parsedContent = null;
            }

            return parsedContent;
        } catch (IOException e) {
            System.out.println("There was a problem with getting the file");
            return null;
        }
    }

    public MT940 getMT940File() throws IOException {
        // Haalt data nu nog uit txt bestand om te testen
        Path filePath = Path.of("src/main/java/com/api/parser/test.txt");
        String mt940Content = Files.readString(filePath);
        return MT940.parse(mt940Content);
    }
}
