package com.api.parser.parser;

import org.springframework.web.multipart.MultipartFile;

public abstract class Parser {
    public abstract String parseMT940(MultipartFile file);
}
