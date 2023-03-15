package com.api.parser;

import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Parser {
    public abstract String parseMT940();

    public MT940 getMT940File() throws IOException {
        // Haalt data nu nog uit txt bestand om te testen
        Path filePath = Path.of("src/main/java/com/api/parser/test.txt");
        String mt940Content = Files.readString(filePath);
        return MT940.parse(mt940Content);
    }
}
