package com.api.parser.controller;

import com.api.parser.parser.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class JSONController {
    private final JSONParser jsonParser;

    @Autowired
    public JSONController() {
        this.jsonParser = new JSONParser();
    }

    @PostMapping("/MT940toJSON")
    public String MT940toJSON(@RequestParam("file") File file) throws IOException {
        System.out.println(jsonParser.parseToFormat(convertFileToMultiPartFile(file)).toString(1));
        return jsonParser.parseToFormat(convertFileToMultiPartFile(file)).toString(1);
    }

    private MultipartFile convertFileToMultiPartFile(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        InputStream stream = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), MediaType.TEXT_HTML_VALUE, stream);
    }

}
