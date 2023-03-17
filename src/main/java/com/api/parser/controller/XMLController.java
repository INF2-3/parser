package com.api.parser.controller;

import com.api.parser.parser.XMLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

@RestController
public class XMLController {
    private final XMLParser xmlParser;

    @Autowired
    public XMLController() {
        this.xmlParser = new XMLParser();
    }

    @PostMapping("/MT940toXML")
    public String MT940toXML(@RequestParam("file") MultipartFile file) {
        return xmlParser.parseToFormat(file);
    }
}
