package com.api.parser.parser;

import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Parser {
    public abstract String parseMT940(MultipartFile file);
}
