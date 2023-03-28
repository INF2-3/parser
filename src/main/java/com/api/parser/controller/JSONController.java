package com.api.parser.controller;

import com.api.parser.parser.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class JSONController {
    private final JSONParser jsonParser;

    @Autowired
    public JSONController() {
        this.jsonParser = new JSONParser();
    }

    @PostMapping("/MT940toJSON")
    public String MT940toJSON(@RequestParam("file") MultipartFile file) {
        return jsonParser.parseToFormat(file).toString(1);
    }
}
