package com.api.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class ParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParserApplication.class, args);

		//Test
		JSONParser JSONparser = new JSONParser();
//		Parser XMLparser = new XMLParser();
//		String[] json = JSONparser.getTag86Parts();
//		String xml = XMLparser.parseMT940();
		System.out.println(JSONparser.getTag86Parts());
//		System.out.println(xml);
	}

}
