package com.api.parser;

import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

import java.io.IOException;

public class JSONParser extends Parser {
    @Override
    public String parseMT940() {
        try {
            MT940 mt940 = getMT940File();
            return mt940.toJson();
        } catch (IOException e) {
            System.out.println("There was a problem with getting the file");
            return null;
        }
    }
}
